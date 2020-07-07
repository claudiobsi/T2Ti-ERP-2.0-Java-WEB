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

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.t2tierp.controller.AbstractController;
import com.t2tierp.model.bean.administrativo.AdmParametro;
import com.t2tierp.model.bean.cadastros.ContaCaixa;
import com.t2tierp.model.bean.cadastros.Fornecedor;
import com.t2tierp.model.bean.financeiro.FinDocumentoOrigem;
import com.t2tierp.model.bean.financeiro.FinLancamentoPagar;
import com.t2tierp.model.bean.financeiro.FinLctoPagarNtFinanceira;
import com.t2tierp.model.bean.financeiro.FinPagamentoFixo;
import com.t2tierp.model.bean.financeiro.FinParcelaPagar;
import com.t2tierp.model.bean.financeiro.FinStatusParcela;
import com.t2tierp.model.bean.financeiro.NaturezaFinanceira;
import com.t2tierp.model.dao.Filtro;
import com.t2tierp.model.dao.InterfaceDAO;
import com.t2tierp.util.FacesContextUtil;

@ManagedBean
@ViewScoped
public class FinLancamentoPagarController extends AbstractController<FinLancamentoPagar> implements Serializable {

	private static final long serialVersionUID = 1L;

	@EJB
	private InterfaceDAO<Fornecedor> fornecedorDao;
	@EJB
	private InterfaceDAO<FinDocumentoOrigem> finDocumentoOrigemDao;
	@EJB
	private InterfaceDAO<ContaCaixa> contaCaixaDao;
	@EJB
	private InterfaceDAO<NaturezaFinanceira> naturezaFinanceiraDao;
	@EJB
	private InterfaceDAO<AdmParametro> admParametroDao;
	@EJB
	private InterfaceDAO<FinStatusParcela> finStatusParcelaDao;
	@EJB
	private InterfaceDAO<FinPagamentoFixo> finPagamentoFixoDao;
	@EJB
	private InterfaceDAO<FinParcelaPagar> finParcelaPagarDao;

	private List<FinLancamentoPagar> lancamentosSelecionados;
	
	private FinParcelaPagar finParcelaPagar;
	private FinParcelaPagar finParcelaPagarSelecionado;

	private FinLctoPagarNtFinanceira finLctoPagarNtFinanceira;
	private FinLctoPagarNtFinanceira finLctoPagarNtFinanceiraSelecionado;

	//atributos utilizados para geração das parcelas
	private ContaCaixa contaCaixa;
	private NaturezaFinanceira naturezaFinanceira;

	@Override
	public Class<FinLancamentoPagar> getClazz() {
		return FinLancamentoPagar.class;
	}

	@Override
	public String getFuncaoBase() {
		return "FIN_LANCAMENTO_PAGAR";
	}

	@Override
	public void incluir() {
		super.incluir();
		getObjeto().setListaFinParcelaPagar(new HashSet<FinParcelaPagar>());
		getObjeto().setListaFinLctoPagarNtFinanceira(new HashSet<FinLctoPagarNtFinanceira>());
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
		BigDecimal valorParcela = getObjeto().getValorAPagar().divide(BigDecimal.valueOf(getObjeto().getQuantidadeParcela()), RoundingMode.HALF_DOWN);
		BigDecimal somaParcelas = BigDecimal.ZERO;
		BigDecimal residuo = BigDecimal.ZERO;
		for (int i = 0; i < getObjeto().getQuantidadeParcela(); i++) {
			FinParcelaPagar parcelaPagar = new FinParcelaPagar();
			parcelaPagar.setFinLancamentoPagar(getObjeto());
			parcelaPagar.setFinStatusParcela(statusParcela);
			parcelaPagar.setContaCaixa(contaCaixa);
			parcelaPagar.setNumeroParcela(i + 1);
			parcelaPagar.setDataEmissao(dataEmissão);
			if (i > 0) {
				primeiroVencimento.add(Calendar.DAY_OF_MONTH, getObjeto().getIntervaloEntreParcelas());
			}
			parcelaPagar.setDataVencimento(primeiroVencimento.getTime());
			parcelaPagar.setSofreRetencao(getObjeto().getFornecedor().getSofreRetencao());
			parcelaPagar.setValor(valorParcela);

			somaParcelas = somaParcelas.add(valorParcela);
			if (i == (getObjeto().getQuantidadeParcela() - 1)) {
				residuo = getObjeto().getValorAPagar().subtract(somaParcelas);
				valorParcela = valorParcela.add(residuo);
				parcelaPagar.setValor(valorParcela);
			}
			getObjeto().getListaFinParcelaPagar().add(parcelaPagar);
		}
	}
	
	private void geraNaturezaFinanceira() {
		FinLctoPagarNtFinanceira finLctoPagarNaturezaFinancaeira = new FinLctoPagarNtFinanceira();
		finLctoPagarNaturezaFinancaeira.setFinLancamentoPagar(getObjeto());
		finLctoPagarNaturezaFinancaeira.setNaturezaFinanceira(naturezaFinanceira);
		finLctoPagarNaturezaFinancaeira.setDataInclusao(new Date());
		finLctoPagarNaturezaFinancaeira.setValor(getObjeto().getValorAPagar());
		
		getObjeto().getListaFinLctoPagarNtFinanceira().add(finLctoPagarNaturezaFinancaeira);
	}

    public void geraPagamentoFixo() {
        try {
            FinPagamentoFixo pagamentoFixo = new FinPagamentoFixo();
            pagamentoFixo.setFornecedor(getObjeto().getFornecedor());
            pagamentoFixo.setFinDocumentoOrigem(getObjeto().getFinDocumentoOrigem());
            pagamentoFixo.setPagamentoCompartilhado(getObjeto().getPagamentoCompartilhado());
            pagamentoFixo.setQuantidadeParcela(getObjeto().getQuantidadeParcela());
            pagamentoFixo.setValorTotal(getObjeto().getValorTotal());
            pagamentoFixo.setValorAPagar(getObjeto().getValorAPagar());
            pagamentoFixo.setDataLancamento(getObjeto().getDataLancamento());
            pagamentoFixo.setNumeroDocumento(getObjeto().getNumeroDocumento());
            pagamentoFixo.setPrimeiroVencimento(getObjeto().getPrimeiroVencimento());
            pagamentoFixo.setIntervaloEntreParcelas(getObjeto().getIntervaloEntreParcelas());
            pagamentoFixo.setImagemDocumento(getObjeto().getImagemDocumento());
            
            finPagamentoFixoDao.persist(pagamentoFixo);
            
            FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_INFO, "Pagamento fixo/recorrente gerado com sucesso.", "");
        } catch (Exception ex) {
            ex.printStackTrace();
            FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro ao salvar o registro!", ex.getMessage());
        }
    }
    
	public void mesclarLancamentos() {
		try {
			if (lancamentosSelecionados.size() <= 1) {
				FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_WARN, "Necessário selecionar 2 ou mais lançamentos!", "");
			} else if (lancamentosSelecionados.size() > 1) {
	            BigDecimal valorTotal = BigDecimal.ZERO;
	            int quantidadeParcelas = 0;
	            for (FinLancamentoPagar l : lancamentosSelecionados) {
	                if (l.getMescladoPara() != null) {
	                    throw new Exception("Lançamento selecionado já mesclado: " + l.getId());
	                }
	                if (l.getValorTotal() != null) {
	                    valorTotal = valorTotal.add(l.getValorTotal());
	                }
	                quantidadeParcelas += l.getQuantidadeParcela();
	            }

	            List<Filtro> filtros = new ArrayList<>();
	            filtros.add(new Filtro(Filtro.AND, "finLancamentoPagar", " IN ", lancamentosSelecionados));
	            
	            List<FinParcelaPagar> parcelas = finParcelaPagarDao.getBeans(FinParcelaPagar.class, filtros);

	            FinLancamentoPagar lancamentoMesclado = new FinLancamentoPagar();
	            lancamentoMesclado.setFornecedor(lancamentosSelecionados.get(0).getFornecedor());
	            lancamentoMesclado.setFinDocumentoOrigem(lancamentosSelecionados.get(0).getFinDocumentoOrigem());
	            lancamentoMesclado.setDataLancamento(lancamentosSelecionados.get(0).getDataLancamento());
	            lancamentoMesclado.setIntervaloEntreParcelas(lancamentosSelecionados.get(0).getIntervaloEntreParcelas());
	            lancamentoMesclado.setNumeroDocumento(lancamentosSelecionados.get(0).getNumeroDocumento());
	            lancamentoMesclado.setPagamentoCompartilhado(lancamentosSelecionados.get(0).getPagamentoCompartilhado());
	            lancamentoMesclado.setPrimeiroVencimento(lancamentosSelecionados.get(0).getPrimeiroVencimento());
	            lancamentoMesclado.setQuantidadeParcela(quantidadeParcelas);
	            lancamentoMesclado.setValorAPagar(valorTotal);
	            lancamentoMesclado.setValorTotal(valorTotal);
	            
	            finParcelaPagarDao.clear();
	            for (FinParcelaPagar p : parcelas) {
	            	p.setId(null);
	            	p.setFinLancamentoPagar(lancamentoMesclado);
	            	p.setListaFinParcelaPagamento(null);
	            }
	            lancamentoMesclado.setListaFinParcelaPagar(new HashSet<>(parcelas));
	            
	            dao.persist(lancamentoMesclado);
	            
	            for (FinLancamentoPagar l : lancamentosSelecionados) {
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
	
	public void alterarFinParcelaPagar() {
		finParcelaPagar = finParcelaPagarSelecionado;
	}

	public void salvarFinParcelaPagar() {
		salvar("Registro salvo com sucesso!");
	}

	public void incluirFinLctoPagarNtFinanceira() {
        finLctoPagarNtFinanceira = new FinLctoPagarNtFinanceira();
        finLctoPagarNtFinanceira.setFinLancamentoPagar(getObjeto());
	}

	public void alterarFinLctoPagarNtFinanceira() {
        finLctoPagarNtFinanceira = finLctoPagarNtFinanceiraSelecionado;
	}

	public void salvarFinLctoPagarNtFinanceira() {
        if (finLctoPagarNtFinanceira.getId() == null) {
            getObjeto().getListaFinLctoPagarNtFinanceira().add(finLctoPagarNtFinanceira);
        }
        salvar("Registro salvo com sucesso!");
	}

	public void excluirFinLctoPagarNtFinanceira() {
        if (finLctoPagarNtFinanceiraSelecionado == null || finLctoPagarNtFinanceiraSelecionado.getId() == null) {
            FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_INFO, "Nenhum registro selecionado!", null);
        } else {
            getObjeto().getListaFinLctoPagarNtFinanceira().remove(finLctoPagarNtFinanceiraSelecionado);
            salvar("Registro excluído com sucesso!");
        }
	}
	
	public List<Fornecedor> getListaFornecedor(String nome) {
		List<Fornecedor> listaFornecedor = new ArrayList<>();
		try {
			listaFornecedor = fornecedorDao.getBeansLike(Fornecedor.class, "pessoa.nome", nome);
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return listaFornecedor;
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

	public FinParcelaPagar getFinParcelaPagar() {
		return finParcelaPagar;
	}

	public void setFinParcelaPagar(FinParcelaPagar finParcelaPagar) {
		this.finParcelaPagar = finParcelaPagar;
	}

	public FinParcelaPagar getFinParcelaPagarSelecionado() {
		return finParcelaPagarSelecionado;
	}

	public void setFinParcelaPagarSelecionado(FinParcelaPagar finParcelaPagarSelecionado) {
		this.finParcelaPagarSelecionado = finParcelaPagarSelecionado;
	}

	public FinLctoPagarNtFinanceira getFinLctoPagarNtFinanceira() {
		return finLctoPagarNtFinanceira;
	}

	public void setFinLctoPagarNtFinanceira(FinLctoPagarNtFinanceira finLctoPagarNtFinanceira) {
		this.finLctoPagarNtFinanceira = finLctoPagarNtFinanceira;
	}

	public FinLctoPagarNtFinanceira getFinLctoPagarNtFinanceiraSelecionado() {
		return finLctoPagarNtFinanceiraSelecionado;
	}

	public void setFinLctoPagarNtFinanceiraSelecionado(FinLctoPagarNtFinanceira finLctoPagarNtFinanceiraSelecionado) {
		this.finLctoPagarNtFinanceiraSelecionado = finLctoPagarNtFinanceiraSelecionado;
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

	public List<FinLancamentoPagar> getLancamentosSelecionados() {
		return lancamentosSelecionados;
	}

	public void setLancamentosSelecionados(List<FinLancamentoPagar> lancamentosSelecionados) {
		this.lancamentosSelecionados = lancamentosSelecionados;
	}

}
