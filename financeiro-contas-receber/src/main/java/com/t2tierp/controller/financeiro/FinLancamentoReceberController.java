/*
* The MIT License
* 
* Copyright: Copyright (C) 2014 T2Ti.COM
* 
* Permission is hereby granted, free of charge, to any person obtaining a copy
* of this software and associated documentation files (the "Software"), to deal
* in the Software without restriction, including without limitation the rights
* to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
* copies of the Software, and to permit persons to whom the Software is
* furnished to do so, subject to the following conditions:
* 
* The above copyright notice and this permission notice shall be included in
* all copies or substantial portions of the Software.
* 
* THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
* IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
* FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
* AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
* LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
* OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN
* THE SOFTWARE.
* 
* The author may be contacted at: t2ti.com@gmail.com
*
* @author Claudio de Barros (T2Ti.com)
* @version 2.0
*/
package com.t2tierp.controller.financeiro;

import java.io.File;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.jrimum.bopepo.BancosSuportados;
import org.jrimum.bopepo.Boleto;
import org.jrimum.bopepo.view.BoletoViewer;
import org.jrimum.domkee.comum.pessoa.endereco.CEP;
import org.jrimum.domkee.comum.pessoa.endereco.Endereco;
import org.jrimum.domkee.comum.pessoa.endereco.UnidadeFederativa;
import org.jrimum.domkee.financeiro.banco.febraban.Agencia;
import org.jrimum.domkee.financeiro.banco.febraban.Carteira;
import org.jrimum.domkee.financeiro.banco.febraban.Cedente;
import org.jrimum.domkee.financeiro.banco.febraban.ContaBancaria;
import org.jrimum.domkee.financeiro.banco.febraban.NumeroDaConta;
import org.jrimum.domkee.financeiro.banco.febraban.Sacado;
import org.jrimum.domkee.financeiro.banco.febraban.TipoDeTitulo;
import org.jrimum.domkee.financeiro.banco.febraban.Titulo;
import org.jrimum.domkee.financeiro.banco.febraban.Titulo.Aceite;

import com.t2tierp.controller.AbstractController;
import com.t2tierp.edi.cnab240.bb.GerarArquivoRemessaBB;
import com.t2tierp.model.bean.administrativo.AdmParametro;
import com.t2tierp.model.bean.cadastros.Cliente;
import com.t2tierp.model.bean.cadastros.ContaCaixa;
import com.t2tierp.model.bean.cadastros.Empresa;
import com.t2tierp.model.bean.cadastros.Pessoa;
import com.t2tierp.model.bean.cadastros.PessoaEndereco;
import com.t2tierp.model.bean.financeiro.FinConfiguracaoBoleto;
import com.t2tierp.model.bean.financeiro.FinDocumentoOrigem;
import com.t2tierp.model.bean.financeiro.FinLancamentoReceber;
import com.t2tierp.model.bean.financeiro.FinLctoReceberNtFinanceira;
import com.t2tierp.model.bean.financeiro.FinParcelaReceber;
import com.t2tierp.model.bean.financeiro.FinStatusParcela;
import com.t2tierp.model.bean.financeiro.NaturezaFinanceira;
import com.t2tierp.model.dao.Filtro;
import com.t2tierp.model.dao.InterfaceDAO;
import com.t2tierp.util.FacesContextUtil;

@ManagedBean
@ViewScoped
public class FinLancamentoReceberController extends AbstractController<FinLancamentoReceber> implements Serializable {

	private static final long serialVersionUID = 1L;
	@EJB
	private InterfaceDAO<FinDocumentoOrigem> finDocumentoOrigemDao;
	@EJB
	private InterfaceDAO<Cliente> clienteDao;
	@EJB
	private InterfaceDAO<Pessoa> pessoaDao;
	@EJB
	private InterfaceDAO<ContaCaixa> contaCaixaDao;
	@EJB
	private InterfaceDAO<NaturezaFinanceira> naturezaFinanceiraDao;
	@EJB
	private InterfaceDAO<AdmParametro> admParametroDao;
	@EJB
	private InterfaceDAO<FinStatusParcela> finStatusParcelaDao;
	@EJB
	private InterfaceDAO<FinParcelaReceber> finParcelaReceberDao;
	@EJB
	private InterfaceDAO<FinConfiguracaoBoleto> configuracaoBoletoDao;

	private List<FinLancamentoReceber> lancamentosSelecionados;

	private FinParcelaReceber finParcelaReceber;
	private FinParcelaReceber finParcelaReceberSelecionado;
	private FinLctoReceberNtFinanceira finLctoReceberNtFinanceira;
	private FinLctoReceberNtFinanceira finLctoReceberNtFinanceiraSelecionado;

	//atributos utilizados para geração das parcelas
	private ContaCaixa contaCaixa;
	private NaturezaFinanceira naturezaFinanceira;

	@Override
	public Class<FinLancamentoReceber> getClazz() {
		return FinLancamentoReceber.class;
	}

	@Override
	public String getFuncaoBase() {
		return "FIN_LANCAMENTO_RECEBER";
	}

	@Override
	public void incluir() {
		super.incluir();
		getObjeto().setListaFinParcelaReceber(new HashSet<>());
		getObjeto().setListaFinLctoReceberNtFinanceira(new HashSet<>());
	}

	@Override
	public void alterar() {
		if (lancamentosSelecionados.size() > 1) {
			FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_WARN, "Selecione somente um registro!", "");
		} else if (lancamentosSelecionados.size() == 1) {
			setObjetoSelecionado(lancamentosSelecionados.get(0));
			super.alterar();
		} else if (lancamentosSelecionados.isEmpty()) {
			FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_WARN, "Nenhum registro selecionado!", "");
		}
	}

	@Override
	public void salvar(String mensagem) {
		try {
			if (getObjeto().getId() == null) {
				gerarParcelas();
				geraNaturezaFinanceira();
			}
			super.salvar(mensagem);
		} catch (Exception e) {
			e.printStackTrace();
			FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro ao salvar o registro!", e.getMessage());
		}
	}

	public void gerarParcelas() throws Exception {

		List<Filtro> filtros = new ArrayList<>();
		filtros.add(new Filtro(Filtro.AND, "empresa", Filtro.IGUAL, FacesContextUtil.getEmpresaUsuario()));
		AdmParametro admParametro = admParametroDao.getBean(AdmParametro.class, filtros);

		FinStatusParcela statusParcela = null;
		if (admParametro != null) {
			statusParcela = finStatusParcelaDao.getBean(admParametro.getFinParcelaAberto(), FinStatusParcela.class);
		}
		if (statusParcela == null) {
			throw new Exception("O status de parcela em aberto não está cadastrado.\nEntre em contato com a Software House.");
		}

		if (contaCaixa == null || contaCaixa.getId() == null) {
			throw new Exception("É necessário informar a conta caixa para previsão das parcelas.");
		}

		Date dataEmissão = new Date();
		Calendar primeiroVencimento = Calendar.getInstance();
		primeiroVencimento.setTime(getObjeto().getPrimeiroVencimento());
		BigDecimal valorParcela = getObjeto().getValorAReceber().divide(BigDecimal.valueOf(getObjeto().getQuantidadeParcela()), RoundingMode.HALF_DOWN);
		BigDecimal somaParcelas = BigDecimal.ZERO;
		BigDecimal residuo = BigDecimal.ZERO;
		String nossoNumero;
		DecimalFormat formatoNossoNumero4 = new DecimalFormat("0000");
		DecimalFormat formatoNossoNumero5 = new DecimalFormat("00000");
		SimpleDateFormat formatoNossoNumero6 = new SimpleDateFormat("DDD");
		Date dataAtual = new Date();
		for (int i = 0; i < getObjeto().getQuantidadeParcela(); i++) {
			FinParcelaReceber parcelaReceber = new FinParcelaReceber();
			parcelaReceber.setFinLancamentoReceber(getObjeto());
			parcelaReceber.setFinStatusParcela(statusParcela);
			parcelaReceber.setContaCaixa(contaCaixa);
			parcelaReceber.setNumeroParcela(i + 1);
			parcelaReceber.setDataEmissao(dataEmissão);
			if (i > 0) {
				primeiroVencimento.add(Calendar.DAY_OF_MONTH, getObjeto().getIntervaloEntreParcelas());
			}
			parcelaReceber.setDataVencimento(primeiroVencimento.getTime());
			parcelaReceber.setEmitiuBoleto("S");

			nossoNumero = formatoNossoNumero5.format(getObjeto().getCliente().getId());
			nossoNumero += formatoNossoNumero5.format(parcelaReceber.getContaCaixa().getId());
			nossoNumero += formatoNossoNumero4.format(parcelaReceber.getNumeroParcela());
			nossoNumero += formatoNossoNumero6.format(dataAtual);
			parcelaReceber.setBoletoNossoNumero(nossoNumero);

			parcelaReceber.setValor(valorParcela);

			somaParcelas = somaParcelas.add(valorParcela);
			if (i == (getObjeto().getQuantidadeParcela() - 1)) {
				residuo = getObjeto().getValorAReceber().subtract(somaParcelas);
				valorParcela = valorParcela.add(residuo);
				parcelaReceber.setValor(valorParcela);
			}
			getObjeto().getListaFinParcelaReceber().add(parcelaReceber);
		}
	}

	private void geraNaturezaFinanceira() {
		FinLctoReceberNtFinanceira finLctoReceberNaturezaFinancaeira = new FinLctoReceberNtFinanceira();
		finLctoReceberNaturezaFinancaeira.setFinLancamentoReceber(getObjeto());
		finLctoReceberNaturezaFinancaeira.setNaturezaFinanceira(naturezaFinanceira);
		finLctoReceberNaturezaFinancaeira.setDataInclusao(new Date());
		finLctoReceberNaturezaFinancaeira.setValor(getObjeto().getValorAReceber());

		getObjeto().getListaFinLctoReceberNtFinanceira().add(finLctoReceberNaturezaFinancaeira);
	}

	public void mesclarLancamentos() {
		try {
			if (lancamentosSelecionados.size() <= 1) {
				FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_WARN, "Necessário selecionar 2 ou mais lançamentos!", "");
			} else if (lancamentosSelecionados.size() > 1) {
				BigDecimal valorTotal = BigDecimal.ZERO;
				int quantidadeParcelas = 0;
				for (FinLancamentoReceber l : lancamentosSelecionados) {
					if (l.getMescladoPara() != null) {
						throw new Exception("Lançamento selecionado já mesclado: " + l.getId());
					}
					if (l.getValorTotal() != null) {
						valorTotal = valorTotal.add(l.getValorTotal());
					}
					quantidadeParcelas += l.getQuantidadeParcela();
				}

				List<Filtro> filtros = new ArrayList<>();
				filtros.add(new Filtro(Filtro.AND, "finLancamentoReceber", " IN ", lancamentosSelecionados));

				List<FinParcelaReceber> parcelas = finParcelaReceberDao.getBeans(FinParcelaReceber.class, filtros);

				FinLancamentoReceber lancamentoMesclado = new FinLancamentoReceber();
				lancamentoMesclado.setCliente(lancamentosSelecionados.get(0).getCliente());
				lancamentoMesclado.setFinDocumentoOrigem(lancamentosSelecionados.get(0).getFinDocumentoOrigem());
				lancamentoMesclado.setDataLancamento(lancamentosSelecionados.get(0).getDataLancamento());
				lancamentoMesclado.setIntervaloEntreParcelas(lancamentosSelecionados.get(0).getIntervaloEntreParcelas());
				lancamentoMesclado.setNumeroDocumento(lancamentosSelecionados.get(0).getNumeroDocumento());
				lancamentoMesclado.setPrimeiroVencimento(lancamentosSelecionados.get(0).getPrimeiroVencimento());
				lancamentoMesclado.setQuantidadeParcela(quantidadeParcelas);
				lancamentoMesclado.setValorAReceber(valorTotal);
				lancamentoMesclado.setValorTotal(valorTotal);

				finParcelaReceberDao.clear();
				for (FinParcelaReceber p : parcelas) {
					p.setId(null);
					p.setFinLancamentoReceber(lancamentoMesclado);
					p.setListaFinParcelaRecebimento(null);
				}
				lancamentoMesclado.setListaFinParcelaReceber(new HashSet<>(parcelas));

				dao.persist(lancamentoMesclado);

				for (FinLancamentoReceber l : lancamentosSelecionados) {
					l.setMescladoPara(lancamentoMesclado.getId());
					l = dao.merge(l);
				}
				FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_INFO, "Lançamentos Mesclados!", "");
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro ao mesclar os lançamentos!", e.getMessage());
		}
	}

	public void gerarBoleto() {
		try {
			List<FinParcelaReceber> listaParcelasReceber = new ArrayList<>(getObjeto().getListaFinParcelaReceber());
			if (listaParcelasReceber.isEmpty()) {
				throw new Exception("Nenhuma parcela para gerar boleto.");
			}
			ContaCaixa contaCaixa = listaParcelasReceber.get(0).getContaCaixa();
			if (contaCaixa.getAgenciaBanco() == null) {
				throw new Exception("A conta/caixa não está vinculada a um banco. Geração de boletos não permitida.");
			}
			List<FinParcelaReceber> listaParcelasBoleto = new ArrayList<>();
			for (int i = 0; i < listaParcelasReceber.size(); i++) {
				if (listaParcelasReceber.get(i).getEmitiuBoleto().equals("S")) {
					listaParcelasBoleto.add(listaParcelasReceber.get(i));
				}
			}
			if (listaParcelasBoleto.isEmpty()) {
				throw new Exception("Nenhuma parcela selecionada para gerar boleto.");
			}

			FinConfiguracaoBoleto configuracaoBoleto = configuracaoBoleto(listaParcelasReceber.get(0).getContaCaixa());
			Cliente cliente = getObjeto().getCliente();
			Empresa empresa = FacesContextUtil.getEmpresaUsuario();
			SimpleDateFormat formatoData = new SimpleDateFormat("dd/MM/yyyy");

			Cedente cedente = new Cedente(empresa.getRazaoSocial(), empresa.getCnpj());

			String cpfCnpjSacado;
			if (cliente.getPessoa().getTipo().equals("F")) {
				cpfCnpjSacado = cliente.getPessoa().getPessoaFisica().getCpf();
			} else {
				cpfCnpjSacado = cliente.getPessoa().getPessoaJuridica().getCnpj();
			}
			Sacado sacado = new Sacado(cliente.getPessoa().getNome(), cpfCnpjSacado);

			PessoaEndereco pessoaEndereco = null;
			Pessoa pessoa = pessoaDao.getBeanJoinFetch(cliente.getPessoa().getId(), Pessoa.class);
			for (PessoaEndereco e : pessoa.getListaPessoaEndereco()) {
				if (e.getPrincipal() != null && e.getPrincipal().equals("S")) {
					pessoaEndereco = e;
				}
			}
			if (pessoaEndereco == null) {
				throw new Exception("Cliente sem endereço principal cadastrado.");
			}

			Endereco enderecoSacado = new Endereco();
			enderecoSacado.setUF(UnidadeFederativa.valueOfSigla(pessoaEndereco.getUf()));
			enderecoSacado.setLocalidade(pessoaEndereco.getCidade());
			enderecoSacado.setCep(new CEP(pessoaEndereco.getCep()));
			enderecoSacado.setBairro(pessoaEndereco.getBairro());
			enderecoSacado.setLogradouro(pessoaEndereco.getLogradouro());
			enderecoSacado.setNumero(pessoaEndereco.getNumero());
			sacado.addEndereco(enderecoSacado);

			ContaBancaria contaBancaria = new ContaBancaria(BancosSuportados.suportados.get(contaCaixa.getAgenciaBanco().getBanco().getCodigo()).create());
			contaBancaria.setNumeroDaConta(new NumeroDaConta(Integer.valueOf(contaCaixa.getCodigo()), contaCaixa.getDigito()));
			contaBancaria.setCarteira(new Carteira(Integer.valueOf(configuracaoBoleto.getCarteira())));
			contaBancaria.setAgencia(new Agencia(Integer.valueOf(contaCaixa.getAgenciaBanco().getCodigo()), contaCaixa.getAgenciaBanco().getDigito()));

			Titulo titulo;
			FinParcelaReceber parcela;
			Boleto boleto;
			List<Boleto> listaBoleto = new ArrayList<Boleto>();
			for (int i = 0; i < listaParcelasBoleto.size(); i++) {
				parcela = listaParcelasBoleto.get(i);

				titulo = new Titulo(contaBancaria, sacado, cedente);
				titulo.setNumeroDoDocumento(parcela.getBoletoNossoNumero().substring(0, 15));
				titulo.setNossoNumero(parcela.getBoletoNossoNumero());
				titulo.setDigitoDoNossoNumero("");
				titulo.setValor(parcela.getValor());
				titulo.setDataDoDocumento(parcela.getDataEmissao());
				titulo.setDataDoVencimento(parcela.getDataVencimento());
				if (configuracaoBoleto.getEspecie().equals("DM")) {
					titulo.setTipoDeDocumento(TipoDeTitulo.DM_DUPLICATA_MERCANTIL);
				} else if (configuracaoBoleto.getEspecie().equals("DS")) {
					titulo.setTipoDeDocumento(TipoDeTitulo.DS_DUPLICATA_DE_SERVICO);
				} else if (configuracaoBoleto.getEspecie().equals("RC")) {
					titulo.setTipoDeDocumento(TipoDeTitulo.RC_RECIBO);
				} else if (configuracaoBoleto.getEspecie().equals("NP")) {
					titulo.setTipoDeDocumento(TipoDeTitulo.NP_NOTA_PROMISSORIA);
				}
				if (configuracaoBoleto.getAceite().equals("S")) {
					titulo.setAceite(Aceite.A);
				} else {
					titulo.setAceite(Aceite.N);
				}
				titulo.setDesconto(parcela.getValorDesconto());
				//titulo.setDeducao(BigDecimal.ZERO);
				//titulo.setMora(BigDecimal.ZERO);
				//titulo.setAcrecimo(BigDecimal.ZERO);
				//titulo.setValorCobrado(BigDecimal.ZERO);

				boleto = new Boleto(titulo);
				boleto.setLocalPagamento(configuracaoBoleto.getLocalPagamento());
				boleto.setInstrucaoAoSacado(configuracaoBoleto.getMensagem());
				boleto.setInstrucao1(configuracaoBoleto.getInstrucao01());
				boleto.setInstrucao2(configuracaoBoleto.getInstrucao02());
				if (parcela.getDescontoAte() != null && parcela.getTaxaDesconto() != null) {
					boleto.setInstrucao3("Para pagamento até o dia " + formatoData.format(parcela.getDescontoAte()) + " conceder desconto de " + parcela.getTaxaDesconto() + "%.");
				} else {
					boleto.setInstrucao3("");
				}

				listaBoleto.add(boleto);
			}

			File arquivo = File.createTempFile("boleto_" + cliente.getId(), ".pdf");
			BoletoViewer.groupInOnePDF(listaBoleto, arquivo);
			FacesContextUtil.downloadArquivo(arquivo, "boleto.pdf");
		} catch (Exception e) {
			e.printStackTrace();
			FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro ao gerar o boleto!", e.getMessage());
		}
	}

	public void gerarRemessa() {
		try {
			File file = File.createTempFile("cnab240", ".txt");

			List<FinParcelaReceber> listaParcelasReceber = new ArrayList<>(getObjeto().getListaFinParcelaReceber());
			if (listaParcelasReceber.isEmpty()) {
				throw new Exception("É necessário gerar as parcelas antes de gerar a remessa.");
			}
			FinConfiguracaoBoleto configuracaoBoleto = configuracaoBoleto(listaParcelasReceber.get(0).getContaCaixa());
			Empresa empresa = FacesContextUtil.getEmpresaUsuario();
			
			PessoaEndereco pessoaEndereco = null;
			Pessoa pessoa = pessoaDao.getBeanJoinFetch(getObjeto().getCliente().getPessoa().getId(), Pessoa.class);
			for (PessoaEndereco e : pessoa.getListaPessoaEndereco()) {
				if (e.getPrincipal() != null && e.getPrincipal().equals("S")) {
					pessoaEndereco = e;
				}
			}
			if (pessoaEndereco == null) {
				throw new Exception("Cliente sem endereço principal cadastrado.");
			}
			
			GerarArquivoRemessaBB gerarArquivoRemessaBB = new GerarArquivoRemessaBB();
			gerarArquivoRemessaBB.gerarArquivoRemessa(listaParcelasReceber, empresa, pessoaEndereco, configuracaoBoleto, file);

			FacesContextUtil.downloadArquivo(file, "cnab240.txt");
		} catch (Exception e) {
			e.printStackTrace();
			FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro ao gerar o arquivo remessa!", e.getMessage());
		}
	}

	private FinConfiguracaoBoleto configuracaoBoleto(ContaCaixa contaCaixa) throws Exception {
		List<Filtro> filtros = new ArrayList<>();
		filtros.add(new Filtro(Filtro.AND, "contaCaixa", Filtro.IGUAL, contaCaixa));
		List<FinConfiguracaoBoleto> listaConfiguracaoBoleto = configuracaoBoletoDao.getBeans(FinConfiguracaoBoleto.class, filtros);
		if (listaConfiguracaoBoleto.isEmpty()) {
			throw new Exception("Não existem configurações de boleto para a conta/caixa.");
		}
		return listaConfiguracaoBoleto.get(0);
	}

	public void alterarFinParcelaReceber() {
		finParcelaReceber = finParcelaReceberSelecionado;
	}

	public void salvarFinParcelaReceber() {
		salvar("Registro salvo com sucesso!");
	}

	public void incluirFinLctoReceberNtFinanceira() {
		finLctoReceberNtFinanceira = new FinLctoReceberNtFinanceira();
		finLctoReceberNtFinanceira.setFinLancamentoReceber(getObjeto());
	}

	public void alterarFinLctoReceberNtFinanceira() {
		finLctoReceberNtFinanceira = finLctoReceberNtFinanceiraSelecionado;
	}

	public void salvarFinLctoReceberNtFinanceira() {
		if (finLctoReceberNtFinanceira.getId() == null) {
			getObjeto().getListaFinLctoReceberNtFinanceira().add(finLctoReceberNtFinanceira);
		}
		salvar("Registro salvo com sucesso!");
	}

	public void excluirFinLctoReceberNtFinanceira() {
		if (finLctoReceberNtFinanceiraSelecionado == null || finLctoReceberNtFinanceiraSelecionado.getId() == null) {
			FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_INFO, "Nenhum registro selecionado!", null);
		} else {
			getObjeto().getListaFinLctoReceberNtFinanceira().remove(finLctoReceberNtFinanceiraSelecionado);
			salvar("Registro excluído com sucesso!");
		}
	}

	public List<FinDocumentoOrigem> getListaFinDocumentoOrigem(String nome) {
		List<FinDocumentoOrigem> listaFinDocumentoOrigem = new ArrayList<>();
		try {
			listaFinDocumentoOrigem = finDocumentoOrigemDao.getBeansLike(FinDocumentoOrigem.class, "descricao", nome);
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return listaFinDocumentoOrigem;
	}

	public List<Cliente> getListaCliente(String nome) {
		List<Cliente> listaCliente = new ArrayList<>();
		try {
			listaCliente = clienteDao.getBeansLike(Cliente.class, "pessoa.nome", nome);
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return listaCliente;
	}

	public List<ContaCaixa> getListaContaCaixa(String nome) {
		List<ContaCaixa> listaContaCaixa = new ArrayList<>();
		try {
			listaContaCaixa = contaCaixaDao.getBeansLike(ContaCaixa.class, "descricao", nome);
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return listaContaCaixa;
	}

	public List<NaturezaFinanceira> getListaNaturezaFinanceira(String nome) {
		List<NaturezaFinanceira> listaNaturezaFinanceira = new ArrayList<>();
		try {
			listaNaturezaFinanceira = naturezaFinanceiraDao.getBeansLike(NaturezaFinanceira.class, "descricao", nome);
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return listaNaturezaFinanceira;
	}

	public ContaCaixa getContaCaixa() {
		return contaCaixa;
	}

	public void setContaCaixa(ContaCaixa contaCaixa) {
		this.contaCaixa = contaCaixa;
	}

	public NaturezaFinanceira getNaturezaFinanceira() {
		return naturezaFinanceira;
	}

	public void setNaturezaFinanceira(NaturezaFinanceira naturezaFinanceira) {
		this.naturezaFinanceira = naturezaFinanceira;
	}

	public FinParcelaReceber getFinParcelaReceber() {
		return finParcelaReceber;
	}

	public void setFinParcelaReceber(FinParcelaReceber finParcelaReceber) {
		this.finParcelaReceber = finParcelaReceber;
	}

	public FinParcelaReceber getFinParcelaReceberSelecionado() {
		return finParcelaReceberSelecionado;
	}

	public void setFinParcelaReceberSelecionado(FinParcelaReceber finParcelaReceberSelecionado) {
		this.finParcelaReceberSelecionado = finParcelaReceberSelecionado;
	}

	public FinLctoReceberNtFinanceira getFinLctoReceberNtFinanceira() {
		return finLctoReceberNtFinanceira;
	}

	public void setFinLctoReceberNtFinanceira(FinLctoReceberNtFinanceira finLctoReceberNtFinanceira) {
		this.finLctoReceberNtFinanceira = finLctoReceberNtFinanceira;
	}

	public FinLctoReceberNtFinanceira getFinLctoReceberNtFinanceiraSelecionado() {
		return finLctoReceberNtFinanceiraSelecionado;
	}

	public void setFinLctoReceberNtFinanceiraSelecionado(FinLctoReceberNtFinanceira finLctoReceberNtFinanceiraSelecionado) {
		this.finLctoReceberNtFinanceiraSelecionado = finLctoReceberNtFinanceiraSelecionado;
	}

	public List<FinLancamentoReceber> getLancamentosSelecionados() {
		return lancamentosSelecionados;
	}

	public void setLancamentosSelecionados(List<FinLancamentoReceber> lancamentosSelecionados) {
		this.lancamentosSelecionados = lancamentosSelecionados;
	}

}
