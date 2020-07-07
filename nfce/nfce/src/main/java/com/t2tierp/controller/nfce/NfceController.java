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
package com.t2tierp.controller.nfce;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.security.KeyStore;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRXmlDataSource;
import net.sf.jasperreports.engine.util.JRXmlUtils;

import org.apache.commons.codec.digest.DigestUtils;
import org.primefaces.context.RequestContext;

import br.inf.portalfiscal.nfe.procnfe.TNfeProc;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.qrcode.QRCodeWriter;
import com.t2ti.nfce.infra.NfceConstantes;
import com.t2ti.pafecf.infra.Tipos;
import com.t2tierp.controller.T2TiLazyDataModel;
import com.t2tierp.controller.nfe.EnviaNfe;
import com.t2tierp.controller.nfe.NfeCabecalhoDataModel;
import com.t2tierp.controller.nfe.ValidaXmlNfe;
import com.t2tierp.model.bean.cadastros.Cliente;
import com.t2tierp.model.bean.cadastros.ContaCaixa;
import com.t2tierp.model.bean.cadastros.Produto;
import com.t2tierp.model.bean.cadastros.Vendedor;
import com.t2tierp.model.bean.financeiro.FinDocumentoOrigem;
import com.t2tierp.model.bean.financeiro.FinLancamentoReceber;
import com.t2tierp.model.bean.financeiro.FinParcelaReceber;
import com.t2tierp.model.bean.financeiro.FinStatusParcela;
import com.t2tierp.model.bean.nfce.NfceConfiguracao;
import com.t2tierp.model.bean.nfce.NfceOperador;
import com.t2tierp.model.bean.nfce.NfceTipoPagamento;
import com.t2tierp.model.bean.nfce.ViewNfceCliente;
import com.t2tierp.model.bean.nfe.NfeCabecalho;
import com.t2tierp.model.bean.nfe.NfeConfiguracao;
import com.t2tierp.model.bean.nfe.NfeCteReferenciado;
import com.t2tierp.model.bean.nfe.NfeCupomFiscalReferenciado;
import com.t2tierp.model.bean.nfe.NfeDeclaracaoImportacao;
import com.t2tierp.model.bean.nfe.NfeDestinatario;
import com.t2tierp.model.bean.nfe.NfeDetEspecificoArmamento;
import com.t2tierp.model.bean.nfe.NfeDetEspecificoMedicamento;
import com.t2tierp.model.bean.nfe.NfeDetalhe;
import com.t2tierp.model.bean.nfe.NfeDetalheImpostoIcms;
import com.t2tierp.model.bean.nfe.NfeDuplicata;
import com.t2tierp.model.bean.nfe.NfeFormaPagamento;
import com.t2tierp.model.bean.nfe.NfeNfReferenciada;
import com.t2tierp.model.bean.nfe.NfeNumero;
import com.t2tierp.model.bean.nfe.NfeProdRuralReferenciada;
import com.t2tierp.model.bean.nfe.NfeReferenciada;
import com.t2tierp.model.dao.Filtro;
import com.t2tierp.model.dao.InterfaceDAO;
import com.t2tierp.util.Biblioteca;
import com.t2tierp.util.FacesContextUtil;
import com.t2tierp.util.NfceException;

@ManagedBean
@ViewScoped
public class NfceController implements Serializable, Tipos {

	private static final long serialVersionUID = 1L;

	private String codigoProduto;
	private BigDecimal quantidade;

	private String txtValorUnitario;
	private String txtValorTotalItem;
	private String txtValorSubTotal;
	private String txtValorTotalGeral;
	private String txtValorDesconto;
	private String txtValorAcrescimo;
	private String txtValorTroco;
	private String txtValorTotalRecebido;
	private String txtValorTotalReceber;
	private String txtValorSaldoRestante;

	private String descricaoProduto;
	private NfeCabecalho vendaAtual;
	private NfeDetalhe vendaDetalhe;
	private Integer statusCaixa;
	private String mensagemOperador;
	private NfceConfiguracao nfceConfiguracao;
	private NfeConfiguracao nfeConfiguracao;

	private int itemCupom;
	private BigDecimal subTotal;
	private BigDecimal totalGeral;
	private BigDecimal valorIcms;
	private BigDecimal desconto;
	private BigDecimal acrescimo;
	private BigDecimal descontoAcrescimo;
	private BigDecimal valorInformadoDescontoAcrescimo;
	private HashMap<String, Integer> operacaoDescontoAcrescimo;
	private Integer operacaoDescontoAcrescimoSelecionada;

	private BigDecimal totalReceber;
	private BigDecimal troco;
	private BigDecimal totalRecebido;
	private BigDecimal saldoRestante;

	private NfceTipoPagamento tipoPagamento;
	private NfeFormaPagamento formaPagamento;
	private BigDecimal valorPagamento;

	private List<NfceTipoPagamento> listaTipoPagamento;

	private boolean pagamentoParcelado;
	private Integer quantidadeParcelas;
	private BigDecimal valorParcelar;

	private KeyStore certificado;
	private String alias;
	private char[] senhaCertificado;
	private String idToken;
	private String valorToken;

	private final String diretorioXml = "modulos/nfce/xml";
	private String danfe;

	private ViewNfceCliente cliente;

	private NfceOperador gerenteSupervisor;

	private Integer numeroItemCancelar;

	private NfeCabecalhoDataModel dataModel;
	private NfeCabecalho vendaSelecionada;

	private Integer operacaoNfce;
	private String justificativa;

	private T2TiLazyDataModel<Vendedor> vendedorDataModel;
	private Vendedor vendedorSelecionado;

	private T2TiLazyDataModel<Produto> produtoDataModel;
	private Produto produtoSelecionado;

	@EJB
	private InterfaceDAO<Produto> produtoDAO;
	@EJB
	private InterfaceDAO<NfceConfiguracao> nfceConfiguracaoDAO;
	@EJB
	private InterfaceDAO<NfeConfiguracao> nfeConfiguracaoDAO;
	@EJB
	private InterfaceDAO<NfeCabecalho> vendaCabecalhoDAO;
	@EJB
	private InterfaceDAO<NfeDetalhe> vendaDetalheDAO;
	@EJB
	private InterfaceDAO<NfeNumero> nfeNumeroDAO;
	@EJB
	private InterfaceDAO<NfceTipoPagamento> nfceTipoPagamentoDAO;
	@EJB
	private InterfaceDAO<ViewNfceCliente> viewNfceClienteDAO;
	@EJB
	private InterfaceDAO<NfceOperador> nfceOperadorDAO;
	@EJB
	private InterfaceDAO<Vendedor> vendedorDAO;
	@EJB
	private InterfaceDAO<FinLancamentoReceber> lancamentoReceberDAO;

	@PostConstruct
	public void init() {
		try {
			nfceConfiguracao = nfceConfiguracaoDAO.getBean(1, NfceConfiguracao.class);
			nfeConfiguracao = nfeConfiguracaoDAO.getBean(1, NfeConfiguracao.class);
			vendaAtual = new NfeCabecalho();
			vendaDetalhe = new NfeDetalhe();
			vendaAtual.setListaNfeDetalhe(new ArrayList<>());
			vendaAtual.setListaNfeFormaPagamento(new HashSet<>());

			listaTipoPagamento = nfceTipoPagamentoDAO.getBeans(NfceTipoPagamento.class);

			quantidade = BigDecimal.ONE;
			txtValorUnitario = "0,00";
			txtValorTotalItem = "0,00";
			txtValorSubTotal = "0,00";
			txtValorTotalGeral = "0,00";

			txtValorDesconto = "0,00";
			txtValorAcrescimo = "0,00";
			txtValorTroco = "0,00";
			txtValorTotalRecebido = "0,00";
			txtValorTotalReceber = "0,00";
			txtValorSaldoRestante = "0,00";

			itemCupom = 0;
			subTotal = BigDecimal.ZERO;
			totalGeral = BigDecimal.ZERO;
			valorIcms = BigDecimal.ZERO;
			desconto = BigDecimal.ZERO;
			acrescimo = BigDecimal.ZERO;
			descontoAcrescimo = BigDecimal.ZERO;
			totalReceber = BigDecimal.ZERO;
			troco = BigDecimal.ZERO;
			totalRecebido = BigDecimal.ZERO;
			saldoRestante = BigDecimal.ZERO;

			statusCaixa = SC_ABERTO;

			telaPadrao();

			carregaDadosCertificado();

			carregaDadosToken();

			cliente = new ViewNfceCliente();

			gerenteSupervisor = new NfceOperador();

			dataModel = new NfeCabecalhoDataModel();
			dataModel.setClazz(NfeCabecalho.class);
			dataModel.setDao(vendaCabecalhoDAO);

			vendedorDataModel = new T2TiLazyDataModel<Vendedor>();
			vendedorDataModel.setClazz(Vendedor.class);
			vendedorDataModel.setDao(vendedorDAO);

			produtoDataModel = new T2TiLazyDataModel<Produto>();
			produtoDataModel.setClazz(Produto.class);
			produtoDataModel.setDao(produtoDAO);

			operacaoDescontoAcrescimo = new HashMap<>();
			operacaoDescontoAcrescimo.put("Desconto em Dinheiro", DESCONTO_VALOR);
			operacaoDescontoAcrescimo.put("Desconto Percentual", DESCONTO_PERCENTUAL);
			operacaoDescontoAcrescimo.put("Acréscimo em Dinheiro", ACRESCIMO_VALOR);
			operacaoDescontoAcrescimo.put("Acréscimo Percentual", ACRESCIMO_PERCENTUAL);
			operacaoDescontoAcrescimo.put("Cancelar Desconto ou Acréscimo", CANCELA_DESCONTO_ACRESCIMO);

		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	private void carregaDadosCertificado() {
		try {
			certificado = KeyStore.getInstance("PKCS12");
			senhaCertificado = nfeConfiguracao.getCertificadoDigitalSenha().toCharArray();
			certificado.load(new FileInputStream(nfeConfiguracao.getCertificadoDigitalCaminho()), senhaCertificado);
			alias = certificado.aliases().nextElement();
		} catch (Exception e) {
			certificado = null;
			e.printStackTrace();
			FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro ao carregar os dados do certificado!", e.getMessage());
		}
	}

	private void carregaDadosToken() {
		try {
			//Exercício: onde buscar os dados do token?
			idToken = "000001";
			valorToken = "1234ABCD5678EFGH9012IJKL3456MNOP";
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void iniciaVendaDeItens() {
		try {
			if (certificado == null) {
				throw new NfceException("Dados do certificado não carregados. Não é possível iniciar uma venda.");
			}

			if (statusCaixa == SC_ABERTO) {
				iniciaVenda();
			}

			vendaDetalhe = new NfeDetalhe();
			NfeDetalheImpostoIcms impostoIcms = new NfeDetalheImpostoIcms();
			impostoIcms.setNfeDetalhe(vendaDetalhe);
			vendaDetalhe.setNfeDetalheImpostoIcms(impostoIcms);
			vendaDetalhe.setListaArmamento(new HashSet<NfeDetEspecificoArmamento>());
			vendaDetalhe.setListaMedicamento(new HashSet<NfeDetEspecificoMedicamento>());
			vendaDetalhe.setListaDeclaracaoImportacao(new HashSet<NfeDeclaracaoImportacao>());

			consultaProduto();

			if (vendaDetalhe.getProduto() != null) {
				if (vendaDetalhe.getProduto().getValorVenda().doubleValue() <= 0) {
					valorPadrao();
					throw new NfceException("Produto não pode ser vendido com valor zerado ou negativo.");
				} else if (vendaDetalhe.getProduto().getUnidadeProduto().getPodeFracionar().equals("N") && (Double.valueOf(quantidade.doubleValue() % 1) != 0)) {
					valorPadrao();
					throw new NfceException("Este produto não pode ser vendido em quantidade fracionada.");
				} else {
					txtValorUnitario = Biblioteca.formatoDecimal("V", vendaDetalhe.getProduto().getValorVenda().doubleValue());
					descricaoProduto = vendaDetalhe.getProduto().getDescricaoPdv();

					BigDecimal total = Biblioteca.multiplica(vendaDetalhe.getProduto().getValorVenda(), quantidade);
					txtValorTotalItem = Biblioteca.formatoDecimal("V", total.doubleValue());

					vendeItem();

					subTotal = Biblioteca.soma(subTotal, vendaDetalhe.getValorTotal());
					totalGeral = Biblioteca.soma(totalGeral, vendaDetalhe.getValorTotal());
					valorIcms = Biblioteca.soma(valorIcms, vendaDetalhe.getNfeDetalheImpostoIcms().getValorIcms());
					atualizaTotais();
					codigoProduto = null;
					quantidade = BigDecimal.ONE;
				}
			} else {
				throw new NfceException("Código não encontrado");
			}
		} catch (NfceException e) {
			valorPadrao();
			e.printStackTrace();
			FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_WARN, e.getMessage(), "");
		} catch (Exception e) {
			statusCaixa = SC_ABERTO;
			telaPadrao();
			e.printStackTrace();
			FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_ERROR, "Erro ao vender o item.", e.getMessage());
		}
	}

	private void iniciaVenda() throws NfceException, Exception {
		instanciaVendaAtual();

		parametrosIniciaisVenda();

		statusCaixa = SC_VENDA_EM_ANDAMENTO;
		mensagemOperador = "Venda em andamento...";

		vendaCabecalhoDAO.persist(vendaAtual);

		danfe = null;
	}

	private void instanciaVendaAtual() throws Exception {
		if (vendaAtual.getId() == null) {
			Date dataAtual = new Date();
			vendaAtual = new NfeCabecalho();
			vendaAtual.setListaNfeDetalhe(new ArrayList<>());
			vendaAtual.setListaNfeFormaPagamento(new HashSet<>());
			vendaAtual.setListaNfeReferenciada(new HashSet<NfeReferenciada>());
			vendaAtual.setListaNfReferenciada(new HashSet<NfeNfReferenciada>());
			vendaAtual.setListaCteReferenciado(new HashSet<NfeCteReferenciado>());
			vendaAtual.setListaProdRuralReferenciada(new HashSet<NfeProdRuralReferenciada>());
			vendaAtual.setListaCupomFiscalReferenciado(new HashSet<NfeCupomFiscalReferenciado>());
			vendaAtual.setListaDuplicata(new HashSet<NfeDuplicata>());
			vendaAtual.setUfEmitente(nfceConfiguracao.getEmpresa().getCodigoIbgeUf());
			vendaAtual.setNaturezaOperacao("VENDA");
			vendaAtual.setCodigoModelo("65");
			vendaAtual.setDataHoraEmissao(dataAtual);
			vendaAtual.setDataHoraEntradaSaida(dataAtual);
			vendaAtual.setTipoOperacao(1);
			vendaAtual.setCodigoMunicipio(nfceConfiguracao.getEmpresa().getCodigoIbgeCidade());
			vendaAtual.setFormatoImpressaoDanfe(4);//0=Sem geração de DANFE;1=DANFE normal, Retrato;2=DANFE normal, Paisagem;3=DANFE Simplificado;4=DANFE NFC-e;5=DANFE NFC-e em mensagem eletrônica.
			vendaAtual.setTipoEmissao(1);//1=Emissão normal (não em contingência);2=Contingência FS-IA, com impressão do DANFE em formulário de segurança;3=Contingência SCAN (Sistema de Contingência do Ambiente Nacional);4=Contingência DPEC (Declaração Prévia da Emissão em Contingência);5=Contingência FS-DA, com impressão do DANFE em formulário de segurança; 6=Contingência SVC-AN (SEFAZ Virtual de Contingência do AN); 7=Contingência SVC-RS (SEFAZ Virtual de Contingência do RS); 9=Contingência off-line da NFC-e;
			vendaAtual.setEmpresa(nfceConfiguracao.getEmpresa());
			vendaAtual.setAmbiente(nfeConfiguracao.getWebserviceAmbiente());
			vendaAtual.setFinalidadeEmissao(1);
			vendaAtual.setProcessoEmissao(nfeConfiguracao.getProcessoEmissao());
			vendaAtual.setVersaoProcessoEmissao(nfeConfiguracao.getVersaoProcessoEmissao());
			vendaAtual.setConsumidorPresenca(1);// 1=Operação presencial;4=NFC-e em operação com entrega em domicílio;
			vendaAtual.setConsumidorOperacao(1);// 0=Não;1=Consumidor final;
			vendaAtual.setLocalDestino(1);// 1=Operação interna;2=Operação interestadual;3=Operação com exterior.
			vendaAtual.setStatusNota(NfceConstantes.SN_EM_EDICAO);
			// vendaAtual.setNfceMovimento(SessaoUsuario.movimento);
			vendaAtual.setBaseCalculoIcms(BigDecimal.ZERO);
			vendaAtual.setValorIcms(BigDecimal.ZERO);
			vendaAtual.setValorTotalProdutos(BigDecimal.ZERO);
			vendaAtual.setBaseCalculoIcmsSt(BigDecimal.ZERO);
			vendaAtual.setValorIcmsSt(BigDecimal.ZERO);
			vendaAtual.setValorIpi(BigDecimal.ZERO);
			vendaAtual.setValorPis(BigDecimal.ZERO);
			vendaAtual.setValorCofins(BigDecimal.ZERO);
			vendaAtual.setValorFrete(BigDecimal.ZERO);
			vendaAtual.setValorSeguro(BigDecimal.ZERO);
			vendaAtual.setValorDespesasAcessorias(BigDecimal.ZERO);
			vendaAtual.setValorDesconto(BigDecimal.ZERO);
			vendaAtual.setValorTotal(BigDecimal.ZERO);
			vendaAtual.setValorImpostoImportacao(BigDecimal.ZERO);
			vendaAtual.setBaseCalculoIssqn(BigDecimal.ZERO);
			vendaAtual.setValorIssqn(BigDecimal.ZERO);
			vendaAtual.setValorPisIssqn(BigDecimal.ZERO);
			vendaAtual.setValorCofinsIssqn(BigDecimal.ZERO);
			vendaAtual.setValorServicos(BigDecimal.ZERO);
			vendaAtual.setValorRetidoPis(BigDecimal.ZERO);
			vendaAtual.setValorRetidoCofins(BigDecimal.ZERO);
			vendaAtual.setValorRetidoCsll(BigDecimal.ZERO);
			vendaAtual.setBaseCalculoIrrf(BigDecimal.ZERO);
			vendaAtual.setValorRetidoIrrf(BigDecimal.ZERO);
			vendaAtual.setBaseCalculoPrevidencia(BigDecimal.ZERO);
			vendaAtual.setValorRetidoPrevidencia(BigDecimal.ZERO);
			vendaAtual.setValorIcmsDesonerado(BigDecimal.ZERO);
			vendaAtual.setTroco(BigDecimal.ZERO);
			numeroNfe();

			desconto = BigDecimal.ZERO;
			acrescimo = BigDecimal.ZERO;
		}
	}

	private void parametrosIniciaisVenda() {
		itemCupom = 0;
		subTotal = BigDecimal.ZERO;
		totalGeral = BigDecimal.ZERO;
		valorIcms = BigDecimal.ZERO;
	}

	public void iniciaEncerramentoVenda() {
		try {
			if (statusCaixa == SC_VENDA_EM_ANDAMENTO) {
				if (!vendaAtual.getListaNfeDetalhe().isEmpty()) {
					totalReceber = Biblioteca.soma(totalGeral, acrescimo);
					totalReceber = Biblioteca.subtrai(totalReceber, desconto);
					saldoRestante = totalReceber;
					valorPagamento = saldoRestante;

					atualizaLabelsValores();

					RequestContext.getCurrentInstance().addCallbackParam("podeAbrirEfetuaPagamento", true);
				} else {
					throw new NfceException("A venda não contém itens.");
				}
			} else {
				throw new NfceException("Não existe venda em andamento.");
			}
		} catch (NfceException e) {
			FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_WARN, "Não existe venda em andamento.", "");
		} catch (Exception e) {
			FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_ERROR, "Erro ao concluir a venda.", e.getMessage());
		}
	}

	public void incluiPagamento() {
		if (valorPagamento.compareTo(BigDecimal.ZERO) == 1) {
			verificaSaldoRestante();
			try {
				if (tipoPagamento.getGeraParcelas().equals("S")) {
					vendaAtual.setIndicadorFormaPagamento(1); // ['0', '1', '2'][ipVista, ipPrazo, ipOutras]
					pagamentoParcelado = true;
					valorParcelar = saldoRestante;
					incluiPagamento(tipoPagamento, saldoRestante);
				} else {
					vendaAtual.setIndicadorFormaPagamento(0); // ['0', '1', '2'][ipVista, ipPrazo, ipOutras]
					incluiPagamento(tipoPagamento, valorPagamento);
				}
			} catch (Exception e) {
				e.printStackTrace();
				FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_ERROR, "Erro ao incluir o pagamento.", e.getMessage());
			}
		} else {
			FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_WARN, "Informe um valor válido.", "");
		}
	}

	private void incluiPagamento(NfceTipoPagamento tipoPagamento, BigDecimal valor) throws Exception {
		NfeFormaPagamento formaPagamento = new NfeFormaPagamento();
		formaPagamento.setNfeCabecalho(vendaAtual);
		formaPagamento.setNfceTipoPagamento(tipoPagamento);
		formaPagamento.setValor(valor);
		formaPagamento.setForma(tipoPagamento.getCodigo());
		formaPagamento.setEstorno("N");

		vendaAtual.getListaNfeFormaPagamento().add(formaPagamento);

		totalRecebido = Biblioteca.soma(totalRecebido, valor);
		troco = Biblioteca.subtrai(totalRecebido, totalReceber);
		if (troco.compareTo(BigDecimal.ZERO) == -1) {
			troco = BigDecimal.ZERO;
		}
		verificaSaldoRestante();
		if (saldoRestante.compareTo(BigDecimal.ZERO) <= 0) {
			valorPagamento = BigDecimal.ZERO;
		} else {
			valorPagamento = saldoRestante;
		}
		tipoPagamento = null;
	}

	public void cancelaPagamento() {
		vendaAtual.setListaNfeFormaPagamento(new HashSet<>());
		totalRecebido = BigDecimal.ZERO;
		troco = BigDecimal.ZERO;
		tipoPagamento = null;
		pagamentoParcelado = false;
		quantidadeParcelas = null;
		verificaSaldoRestante();
	}

	public void finalizaVenda() {
		try {
			if (saldoRestante.compareTo(BigDecimal.ZERO) <= 0) {
				String mensagem = "";
				if (pagamentoParcelado) {
					pagamentoParcelado = false;
					geraParcelas();
					mensagem = "Valor Parcelado: " + Biblioteca.formatoDecimal("V", valorParcelar.doubleValue());
				}
				vendaAtual.setValorTotal(totalReceber);
				vendaAtual.setTroco(troco);

				concluiEncerramentoVenda();
				FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_INFO, "Venda finalizada com sucesso.", mensagem);
			} else {
				FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_WARN, "Valores informados não são suficientes para finalizar a venda.", null);
			}

		} catch (Exception e) {
			e.printStackTrace();
			FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro ao finalizar a venda.", e.getMessage());
		}
	}

	@SuppressWarnings("rawtypes")
	private void concluiEncerramentoVenda() throws Exception {

		String xmlEnvio = new GeraXmlEnvio().gerarXmlEnvio(nfceConfiguracao.getEmpresa(), vendaAtual, alias, certificado, senhaCertificado);
		try {
			new ValidaXmlNfe().validaXmlEnvio(xmlEnvio, (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext());
		} catch (Exception e) {
			throw new Exception("Erro na validação do XML\n" + e.getMessage());
		}

		Map mapResposta = new EnviaNfe().enviaNfe(xmlEnvio, alias, certificado, senhaCertificado, vendaAtual.getUfEmitente().toString(), String.valueOf(vendaAtual.getAmbiente()));

		Boolean autorizado = (Boolean) mapResposta.get("autorizado");

		if (autorizado) {
			String xmlProc = (String) mapResposta.get("xmlProc");
			salvaArquivos(xmlProc);

			try {
				imprimeDanfe(mapResposta.get("digVal").toString());
				RequestContext.getCurrentInstance().addCallbackParam("vendaFinalizada", true);
			} catch (Exception e) {
				e.printStackTrace();
				throw e;
			} finally {
				statusCaixa = SC_ABERTO;
				telaPadrao();
			}
		} else {
			throw new Exception(mapResposta.get("resposta").toString());
		}
	}

	private void salvaArquivos(String xml) throws Exception {
		vendaAtual.setStatusNota(NfceConstantes.SN_AUTORIZADA);

		vendaCabecalhoDAO.merge(vendaAtual);

		// salva o xml
		ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
		String caminhoArquivo = context.getRealPath(diretorioXml) + System.getProperty("file.separator") + vendaAtual.getChaveAcesso() + vendaAtual.getDigitoChaveAcesso();
		OutputStream outXml = new FileOutputStream(new File(caminhoArquivo + "-nfeproc.xml"));
		outXml.write(xml.getBytes());
		outXml.close();
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private void imprimeDanfe(String digestValue) throws Exception {
		ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
		String caminhoArquivo = context.getRealPath(diretorioXml) + System.getProperty("file.separator") + vendaAtual.getChaveAcesso() + vendaAtual.getDigitoChaveAcesso();

		Map map = new HashMap();
		map.put("QR_CODE", geraQrCode(digestValue));
		//http://www.nfe.se.gov.br/portal/portalNoticias.jsp?jsp=barra-menu/documentos/tabelaEnderecoConsulta.htm
		map.put("ENDERECO_SEFAZ", "https://www.sefaz.rs.gov.br/NFCE/NFCE-COM.aspx");
		map.put("SUBREPORT_DIR", context.getRealPath("modulos/nfce/danfe/").toString() + System.getProperty("file.separator"));
		map.put("XML_DATA_DOCUMENT", JRXmlUtils.parse(caminhoArquivo + "-nfeproc.xml"));

		File arquivoPdf = new File(caminhoArquivo + ".pdf");
		JRXmlDataSource jrXmlDataSource = new JRXmlDataSource(caminhoArquivo + "-nfeproc.xml", "/nfeProc/NFe/infNFe/det");
		JasperPrint jp = JasperFillManager.fillReport(context.getRealPath("modulos/nfce/danfe/danfeNfce.jasper"), map, jrXmlDataSource);

		OutputStream outPdf = new FileOutputStream(arquivoPdf);
		outPdf.write(JasperExportManager.exportReportToPdf(jp));
		outPdf.close();

		danfe = "/" + diretorioXml + "/" + vendaAtual.getChaveAcesso() + vendaAtual.getDigitoChaveAcesso() + ".pdf";
	}

	private BufferedImage geraQrCode(String digestValue) {
		try {
			DecimalFormatSymbols simboloDecimal = DecimalFormatSymbols.getInstance();
			simboloDecimal.setDecimalSeparator('.');
			DecimalFormat formatoValor = new DecimalFormat("0.00", simboloDecimal);

			String endereco = "https://www.sefaz.rs.gov.br/NFCE/NFCE-COM.aspx?";
			//parametros
			String chNFe = "chNFe=" + vendaAtual.getChaveAcesso() + vendaAtual.getDigitoChaveAcesso();
			String nVersao = "nVersao=100";
			String tpAmb = "tpAmb=" + vendaAtual.getAmbiente();
			String cDest = vendaAtual.getDestinatario() != null ? "cDest=" + vendaAtual.getDestinatario().getCpfCnpj() : null;
			String dhEmi = "dhEmi=" + DigestUtils.sha1Hex(new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").format(vendaAtual.getDataHoraEmissao()));
			String vNF = "vNF=" + formatoValor.format(vendaAtual.getValorTotalProdutos());
			String vICMS = "vICMS=" + formatoValor.format(vendaAtual.getValorIcms());
			String digVal = "digVal=" + DigestUtils.sha1Hex(digestValue);
			String cIdToken = "cIdToken=" + idToken;

			String parametros = chNFe + "&" + nVersao + "&" + tpAmb + (cDest == null ? "" : "&" + cDest) + "&" + dhEmi + "&" + vNF + "&" + vICMS + "&" + digVal + "&" + cIdToken + valorToken;

			String cHashQRCode = "cHashQRCode=" + DigestUtils.sha1Hex(parametros);

			parametros = chNFe + "&" + nVersao + "&" + tpAmb + (cDest == null ? "" : "&" + cDest) + "&" + dhEmi + "&" + vNF + "&" + vICMS + "&" + digVal + "&" + cIdToken + "&" + cHashQRCode;

			BufferedImage image = MatrixToImageWriter.toBufferedImage(new QRCodeWriter().encode(endereco + parametros, BarcodeFormat.QR_CODE, 200, 200));

			return image;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	private void geraParcelas() {
		try {
			BigDecimal valorCadaParcela = Biblioteca.divide(valorParcelar, BigDecimal.valueOf(quantidadeParcelas));
			BigDecimal resto = Biblioteca.multiplica(valorCadaParcela, BigDecimal.valueOf(quantidadeParcelas));
			resto = Biblioteca.subtrai(valorParcelar, resto);

			FinParcelaReceber parcelaReceber;
			Calendar primeiroVencimento = Calendar.getInstance();
			primeiroVencimento.add(Calendar.DAY_OF_MONTH, 30);
			Calendar proximoVencimento = Calendar.getInstance();
			proximoVencimento.setTime(primeiroVencimento.getTime());
			Set<FinParcelaReceber> listaParcela = new HashSet<>();
			for (int i = 0; i < quantidadeParcelas; i++) {
				parcelaReceber = new FinParcelaReceber();
				parcelaReceber.setNumeroParcela(i + 1);
				if (i == 0) {
					parcelaReceber.setValor(Biblioteca.soma(valorCadaParcela, resto));
				} else {
					parcelaReceber.setValor(valorCadaParcela);
					proximoVencimento.add(Calendar.DAY_OF_MONTH, 30);
				}
				parcelaReceber.setDataVencimento(proximoVencimento.getTime());

				listaParcela.add(parcelaReceber);
			}

			FinDocumentoOrigem documentoOrigem = new FinDocumentoOrigem();
			documentoOrigem.setId(32);

			ContaCaixa contaCaixa = new ContaCaixa();
			contaCaixa.setId(2);

			FinStatusParcela statusParcela = new FinStatusParcela();
			statusParcela.setId(1);

			Cliente cliente = new Cliente();
			cliente.setId(1);

			String identificador = "E" + nfceConfiguracao.getEmpresa().getId() + "X" + nfceConfiguracao.getNfceCaixa().getId() + "V" + vendaAtual.getId() + "C" + vendaAtual.getNumero() + "Q" + quantidadeParcelas;

			FinLancamentoReceber lancamentoReceber = new FinLancamentoReceber();
			lancamentoReceber.setFinDocumentoOrigem(documentoOrigem);
			lancamentoReceber.setCliente(cliente);
			lancamentoReceber.setQuantidadeParcela(quantidadeParcelas);
			lancamentoReceber.setValorTotal(valorParcelar);
			lancamentoReceber.setValorAReceber(valorParcelar);
			lancamentoReceber.setDataLancamento(new Date());
			lancamentoReceber.setNumeroDocumento(identificador);
			lancamentoReceber.setPrimeiroVencimento(primeiroVencimento.getTime());
			lancamentoReceber.setCodigoModuloLcto("NFC");
			lancamentoReceber.setListaFinParcelaReceber(listaParcela);

			for (FinParcelaReceber p : lancamentoReceber.getListaFinParcelaReceber()) {
				p.setFinLancamentoReceber(lancamentoReceber);
				p.setFinStatusParcela(statusParcela);
				p.setContaCaixa(contaCaixa);
			}

			lancamentoReceberDAO.persist(lancamentoReceber);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	private void verificaSaldoRestante() {
		BigDecimal recebidoAteAgora = BigDecimal.ZERO;
		for (NfeFormaPagamento p : vendaAtual.getListaNfeFormaPagamento()) {
			recebidoAteAgora = Biblioteca.soma(recebidoAteAgora, p.getValor());
		}

		saldoRestante = Biblioteca.subtrai(totalReceber, recebidoAteAgora);

		atualizaLabelsValores();
	}

	private void atualizaTotais() {
		vendaAtual.setValorTotalProdutos(subTotal);
		vendaAtual.setValorDesconto(desconto);
		vendaAtual.setValorDespesasAcessorias(acrescimo);

		descontoAcrescimo = Biblioteca.subtrai(acrescimo, desconto);

		vendaAtual.setValorTotal(Biblioteca.soma(totalGeral, descontoAcrescimo));
		vendaAtual.setBaseCalculoIcms(vendaAtual.getValorTotal());
		vendaAtual.setValorIcms(valorIcms);

		if (descontoAcrescimo.compareTo(BigDecimal.ZERO) == -1) {
			vendaAtual.setValorDesconto(descontoAcrescimo.negate());
			vendaAtual.setValorDespesasAcessorias(BigDecimal.ZERO);
		} else if (descontoAcrescimo.compareTo(BigDecimal.ZERO) == 1) {
			vendaAtual.setValorDesconto(BigDecimal.ZERO);
			vendaAtual.setValorDespesasAcessorias(descontoAcrescimo);
		} else {
			vendaAtual.setValorDesconto(BigDecimal.ZERO);
			vendaAtual.setValorDespesasAcessorias(BigDecimal.ZERO);
			acrescimo = BigDecimal.ZERO;
			desconto = BigDecimal.ZERO;
		}

		txtValorSubTotal = Biblioteca.formatoDecimal("V", subTotal.doubleValue());
		txtValorTotalGeral = Biblioteca.formatoDecimal("V", vendaAtual.getValorTotal().doubleValue());
	}

	private void atualizaLabelsValores() {
		// formata valores para exibição
		txtValorTotalGeral = Biblioteca.formatoDecimal("V", totalGeral.doubleValue());
		txtValorAcrescimo = Biblioteca.formatoDecimal("V", acrescimo.doubleValue());
		txtValorDesconto = Biblioteca.formatoDecimal("V", desconto.doubleValue());
		txtValorTotalReceber = Biblioteca.formatoDecimal("V", totalReceber.doubleValue());
		txtValorTotalRecebido = Biblioteca.formatoDecimal("V", totalRecebido.doubleValue());
		if (saldoRestante.compareTo(BigDecimal.ZERO) > 0) {
			txtValorSaldoRestante = Biblioteca.formatoDecimal("V", saldoRestante.doubleValue());
		} else {
			txtValorSaldoRestante = Biblioteca.formatoDecimal("V", 0);
		}
		txtValorTroco = Biblioteca.formatoDecimal("V", troco.doubleValue());
	}

	private void numeroNfe() throws Exception {
		DecimalFormat formatoNumero = new DecimalFormat("000000000");
		DecimalFormat formatoCodigoNumerico = new DecimalFormat("00000000");
		SimpleDateFormat formatoAno = new SimpleDateFormat("yy");
		SimpleDateFormat formatoMes = new SimpleDateFormat("MM");

		NfeNumero numero = nfeNumeroDAO.getBean(NfeNumero.class, new ArrayList<Filtro>());
		if (numero == null) {
			numero = new NfeNumero();
			numero.setEmpresa(nfceConfiguracao.getEmpresa());
			numero.setSerie("001");
			numero.setNumero(1);
		} else {
			numero.setNumero(numero.getNumero() + 1);
		}
		nfeNumeroDAO.merge(numero);

		vendaAtual.setNumero(formatoNumero.format(numero.getNumero()));
		vendaAtual.setCodigoNumerico(formatoCodigoNumerico.format(numero.getNumero()));
		vendaAtual.setSerie(numero.getSerie());

		vendaAtual.setChaveAcesso("" + vendaAtual.getEmpresa().getCodigoIbgeUf() + formatoAno.format(vendaAtual.getDataHoraEmissao()) + formatoMes.format(vendaAtual.getDataHoraEmissao()) + vendaAtual.getEmpresa().getCnpj() + vendaAtual.getCodigoModelo() + vendaAtual.getSerie() + vendaAtual.getNumero() + "1" + vendaAtual.getCodigoNumerico());
		vendaAtual.setDigitoChaveAcesso(Biblioteca.modulo11(vendaAtual.getChaveAcesso()).toString());
	}

	private void vendeItem() throws NfceException, Exception {
		compoeItemParaVenda();

		if (vendaDetalhe.getGtin() == null || vendaDetalhe.getGtin().isEmpty()) {
			txtValorUnitario = "0,00";
			txtValorTotalItem = "0,00";
			itemCupom--;
			vendaDetalhe = null;
			throw new NfceException("Produto com Código ou GTIN não definido!");
		}

		if (vendaDetalhe.getProduto().getDescricaoPdv() == null || vendaDetalhe.getProduto().getDescricaoPdv().isEmpty()) {
			txtValorUnitario = "0,00";
			txtValorTotalItem = "0,00";
			itemCupom--;
			vendaDetalhe = null;
			throw new NfceException("Produto com Descrição não definida!");
		}

		if (vendaDetalhe.getProduto().getUnidadeProduto().getSigla() == null || vendaDetalhe.getProduto().getUnidadeProduto().getSigla().isEmpty()) {
			txtValorUnitario = "0,00";
			txtValorTotalItem = "0,00";
			itemCupom--;
			vendaDetalhe = null;
			throw new NfceException("Produto com Unidade não definida!");
		}

		// Imposto
		vendaDetalhe.getNfeDetalheImpostoIcms().setOrigemMercadoria(0); // nacional
		vendaDetalhe.getNfeDetalheImpostoIcms().setCstIcms("00"); // nacional
		vendaDetalhe.getNfeDetalheImpostoIcms().setModalidadeBcIcms(3); // valor
																		// da
																		// operação
		vendaDetalhe.getNfeDetalheImpostoIcms().setAliquotaIcms(vendaDetalhe.getProduto().getAliquotaIcmsPaf());
		vendaDetalhe.getNfeDetalheImpostoIcms().setBaseCalculoIcms(vendaDetalhe.getValorTotal());
		BigDecimal icms = Biblioteca.multiplica(vendaDetalhe.getValorTotal(), vendaDetalhe.getProduto().getAliquotaIcmsPaf());
		icms = Biblioteca.divide(icms, BigDecimal.valueOf(100));
		vendaDetalhe.getNfeDetalheImpostoIcms().setValorIcms(icms);

		vendaDetalheDAO.persist(vendaDetalhe);

		vendaAtual.getListaNfeDetalhe().add(vendaDetalhe);
	}

	private void compoeItemParaVenda() {
		itemCupom++;

		vendaDetalhe.setNumeroItem(itemCupom);
		vendaDetalhe.setCfop(nfceConfiguracao.getCfop());
		vendaDetalhe.setNfeCabecalho(vendaAtual);
		vendaDetalhe.setCodigoProduto(vendaDetalhe.getProduto().getGtin());
		vendaDetalhe.setGtin(vendaDetalhe.getProduto().getGtin());
		vendaDetalhe.setNomeProduto(vendaDetalhe.getProduto().getNome());
		vendaDetalhe.setNcm(vendaDetalhe.getProduto().getNcm());
		vendaDetalhe.setUnidadeComercial(vendaDetalhe.getProduto().getUnidadeProduto().getSigla());
		vendaDetalhe.setUnidadeTributavel(vendaDetalhe.getProduto().getUnidadeProduto().getSigla());
		vendaDetalhe.setGtinUnidadeTributavel(vendaDetalhe.getProduto().getGtin());

		if (statusCaixa == SC_VENDA_EM_ANDAMENTO) {
			vendaDetalhe.setQuantidadeComercial(quantidade);
			vendaDetalhe.setQuantidadeTributavel(quantidade);
			vendaDetalhe.setValorUnitarioComercial(vendaDetalhe.getProduto().getValorVenda());
			vendaDetalhe.setValorUnitarioTributavel(vendaDetalhe.getProduto().getValorVenda());
			vendaDetalhe.setValorBrutoProduto(Biblioteca.multiplica(vendaDetalhe.getValorUnitarioComercial(), vendaDetalhe.getQuantidadeComercial()));
			vendaDetalhe.setValorSubtotal(BigDecimal.valueOf(Double.valueOf(txtValorTotalItem.replace(",", "."))));
			vendaDetalhe.setValorTotal(vendaDetalhe.getValorSubtotal());
			// Exercício: implemente o desconto sobre o valor do item de acordo
			// com a sua necessidade
			vendaDetalhe.setValorDesconto(BigDecimal.ZERO);
			vendaDetalhe.setValorTotal(Biblioteca.subtrai(vendaDetalhe.getValorTotal(), vendaDetalhe.getValorDesconto()));
			vendaDetalhe.setValorFrete(BigDecimal.ZERO);
			vendaDetalhe.setValorSeguro(BigDecimal.ZERO);
			vendaDetalhe.setValorOutrasDespesas(BigDecimal.ZERO);
			vendaDetalhe.setEntraTotal(1);
		}
	}

	private void telaPadrao() {
		vendaAtual = new NfeCabecalho();

		if (statusCaixa == SC_ABERTO) {
			mensagemOperador = "Caixa Aberto";
		}
		quantidade = BigDecimal.ONE;
		codigoProduto = null;
		descricaoProduto = "";
		txtValorUnitario = "0,00";
		txtValorTotalItem = "0,00";
		txtValorSubTotal = "0,00";
		txtValorTotalGeral = "0,00";
		totalRecebido = BigDecimal.ZERO;
		troco = BigDecimal.ZERO;
		desconto = BigDecimal.ZERO;
		acrescimo = BigDecimal.ZERO;
	}

	private void valorPadrao() {
		quantidade = BigDecimal.ONE;
		codigoProduto = null;
	}

	private void consultaProduto() {
		if (codigoProduto.length() == 13 || codigoProduto.length() == 14) {
			consultaProduto(codigoProduto);
		} else {
			consultaProduto(codigoProduto, PESQUISA_CODIGO_INTERNO);
		}
	}

	private void consultaProduto(String pCodigo) {
		consultaProduto(pCodigo, PESQUISA_GTIN);
	}

	private void consultaProduto(String codigo, int tipo) {
		try {
			List<Filtro> listaFiltro = new ArrayList<>();

			switch (tipo) {
			case PESQUISA_CODIGO_BALANCA: {
				listaFiltro.add(new Filtro("AND", "codigoBalanca", "=", codigo));
				break;
			}
			case PESQUISA_GTIN: {
				listaFiltro.add(new Filtro("AND", "gtin", "=", codigo));
				break;
			}
			case PESQUISA_CODIGO_INTERNO: {
				listaFiltro.add(new Filtro("AND", "codigoInterno", "=", codigo));
				break;
			}
			case PESQUISA_ID: {
				listaFiltro.add(new Filtro("AND", "id", "=", codigo));
				break;
			}
			}

			vendaDetalhe.setProduto(produtoDAO.getBean(Produto.class, listaFiltro));
		} catch (Exception ex) {
			vendaDetalhe.setProduto(null);
		}
	}

	public void identificaCliente() {
		if (statusCaixa == SC_ABERTO) {
			FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_WARN, "Não há venda em andamento.", "Inicie uma venda antes de identificar o cliente.");
		} else {
			NfeDestinatario destinatario = new NfeDestinatario();
			destinatario.setNfeCabecalho(vendaAtual);
			destinatario.setNome(cliente.getNome() == null ? "CONSUMIDOR FINAL" : cliente.getNome());
			destinatario.setCpfCnpj(Biblioteca.soNumero(cliente.getCpf()));

			vendaAtual.setDestinatario(destinatario);

			String mensagem = cliente.getCpf() + " - " + (cliente.getNome() == null ? "CONSUMIDOR FINAL" : cliente.getNome());

			cliente = new ViewNfceCliente();

			FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_INFO, "Cliente idenficado", mensagem);
		}
	}

	public void cancelaItem() {
		if (statusCaixa == SC_ABERTO) {
			FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_WARN, "Não há venda em andamento.", "");
		} else {
			if (numeroItemCancelar != null) {
				if (loginGerenteSupervisor()) {
					if (numeroItemCancelar > (vendaAtual.getListaNfeDetalhe().size())) {
						FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_WARN, "Nr. de item inválido.", "");
					} else {
						try {
							cancelaItem(numeroItemCancelar);
							FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_INFO, "Item " + numeroItemCancelar + " cancelado.", "");
						} catch (Exception ex) {
							ex.printStackTrace();
							FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_ERROR, "Erro ao cancelar o item.", ex.getMessage());
						} finally {
							numeroItemCancelar = null;
						}
					}
				}
			}
		}
	}

	private void cancelaItem(int numeroItem) throws Exception {
		vendaDetalhe = vendaAtual.getListaNfeDetalhe().get(numeroItem - 1);
		BigDecimal valorTotalItem = vendaDetalhe.getValorTotal();
		BigDecimal valorIcmsItem = vendaDetalhe.getNfeDetalheImpostoIcms().getValorIcms();

		vendaDetalhe.setNfeDetalheImpostoIcms(null);
		vendaDetalheDAO.merge(vendaDetalhe);
		vendaDetalheDAO.excluir(NfeDetalhe.class, vendaDetalhe.getId());

		vendaAtual.getListaNfeDetalhe().remove(numeroItem - 1);

		subTotal = Biblioteca.subtrai(subTotal, valorTotalItem);
		totalGeral = Biblioteca.subtrai(totalGeral, valorTotalItem);
		valorIcms = Biblioteca.subtrai(valorIcms, valorIcmsItem);

		desconto = BigDecimal.ZERO;
		acrescimo = BigDecimal.ZERO;

		atualizaTotais();
	}

	public void cancelaVenda() {
		if (statusCaixa == SC_VENDA_EM_ANDAMENTO || statusCaixa == SC_VENDA_RECUPERADA_DAV_PREVENDA) {
			statusCaixa = SC_ABERTO;
			valorPadrao();
			telaPadrao();
			FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_INFO, "Venda cancelada.", "");
		} else {
			FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_WARN, "Não existe venda em andamento.", "");
		}
	}

	public void filtroRecuperaVenda() {
		operacaoNfce = NfceConstantes.OP_RECUPERA_VENDA;
		defineFiltro(NfceConstantes.SN_EM_EDICAO);
	}

	public void filtroInutilizaNumero() {
		operacaoNfce = NfceConstantes.OP_INUTILIZA_NUMERO;
		defineFiltro(NfceConstantes.SN_EM_EDICAO);
	}

	public void filtroCancelaNfce() {
		operacaoNfce = NfceConstantes.OP_CANCELA_NFCE;
		defineFiltro(NfceConstantes.SN_AUTORIZADA);
	}

	private void defineFiltro(int statusNota) {
		Map<String, Object> filtro = new HashMap<String, Object>();
		filtro.put("statusNota", statusNota);

		String[] atributos = new String[7];
		atributos[0] = "serie";
		atributos[1] = "numero";
		atributos[2] = "dataHoraEmissao";
		atributos[3] = "chaveAcesso";
		atributos[4] = "digitoChaveAcesso";
		atributos[5] = "valorTotal";
		atributos[6] = "statusNota";

		dataModel.setFiltroPadrao(filtro);
		dataModel.setAtributosPadrao(atributos);
	}

	public void executaOperacaoNfce() {
		switch (operacaoNfce) {
		case NfceConstantes.OP_RECUPERA_VENDA: {
			recuperaVenda();
			break;
		}
		case NfceConstantes.OP_CANCELA_NFCE: {
			cancelaInutiliza();
			break;
		}
		case NfceConstantes.OP_INUTILIZA_NUMERO: {
			cancelaInutiliza();
			break;
		}
		}
	}

	private void recuperaVenda() {
		RequestContext context = RequestContext.getCurrentInstance();
		if (vendaSelecionada != null) {
			try {
				vendaAtual = vendaCabecalhoDAO.getBeanJoinFetch(vendaSelecionada.getId(), NfeCabecalho.class);
				if (vendaAtual != null) {
					parametrosIniciaisVenda();
					statusCaixa = SC_VENDA_RECUPERADA_DAV_PREVENDA;
					mensagemOperador = "Venda recuperada...";

					for (NfeDetalhe v : vendaAtual.getListaNfeDetalhe()) {
						vendaDetalhe = v;
						compoeItemParaVenda();

						subTotal = Biblioteca.soma(subTotal, vendaDetalhe.getValorTotal());
						totalGeral = Biblioteca.soma(totalGeral, vendaDetalhe.getValorTotal());
						atualizaTotais();
					}
					statusCaixa = SC_VENDA_EM_ANDAMENTO;
					FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_INFO, "Venda recuperada.", "");
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_INFO, "Erro ao recuperar a venda.", ex.getMessage());
			} finally {
				vendaSelecionada = null;
				context.addCallbackParam("vendaSelecionada", true);
			}
		} else {
			FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_WARN, "É necessário selecionar uma venda.", "");
			context.addCallbackParam("vendaSelecionada", false);
		}
	}

	public void cancelaInutiliza() {
		RequestContext context = RequestContext.getCurrentInstance();
		if (vendaSelecionada != null) {
			try {
				if (loginGerenteSupervisor()) {
					if (justificativa != null) {
						if (justificativa.trim().equals("")) {
							FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_WARN, "É necessário informar uma justificativa.", "");
						} else if (justificativa.trim().length() < 15) {
							FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_WARN, "A justificativa deve ter no mínimo 15 caracteres.", "");
						} else if (justificativa.trim().length() > 255) {
							FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_WARN, "A justificativa deve ter no máximo 255 caracteres.", "");
						} else {
							vendaAtual = vendaCabecalhoDAO.getBean(vendaSelecionada.getId(), NfeCabecalho.class);
							String resposta;
							if (operacaoNfce == NfceConstantes.OP_CANCELA_NFCE) {
								resposta = cancelaNfce(justificativa);
								FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_INFO, resposta, null);
							} else {
								resposta = inutilizaNumero(justificativa);
								FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_INFO, resposta, null);
							}
						}
					} else {
						FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_WARN, "É necessário informar uma justificativa.", "");
					}
				}
			} catch (Exception ex) {
				ex.printStackTrace();
				FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_INFO, "Erro ao executar o procedimento.", ex.getMessage());
			} finally {
				vendaSelecionada = null;
				justificativa = null;
				telaPadrao();
				context.addCallbackParam("vendaSelecionada", true);
			}
		} else {
			FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_WARN, "É necessário selecionar uma venda.", "");
			context.addCallbackParam("vendaSelecionada", false);
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	private String cancelaNfce(String justificativa) throws Exception {
		ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
		String caminhoArquivo = context.getRealPath(diretorioXml) + System.getProperty("file.separator") + vendaAtual.getChaveAcesso() + vendaAtual.getDigitoChaveAcesso();

		File arquivoXml = new File(caminhoArquivo + "-nfeproc.xml");
		if (!arquivoXml.exists()) {
			throw new Exception("Arquivo XML da NFC-e não localizado!");
		} else {
			JAXBContext jc = JAXBContext.newInstance("br.inf.portalfiscal.nfe.procnfe");
			Unmarshaller unmarshaller = jc.createUnmarshaller();

			JAXBElement<TNfeProc> element = (JAXBElement) unmarshaller.unmarshal(arquivoXml);
			String protocolo = element.getValue().getProtNFe().getInfProt().getNProt();

			CancelaNfe cancelaNfe = new CancelaNfe();
			Map dadosCancelamento = cancelaNfe.cancelaNfe(alias, certificado, senhaCertificado, vendaAtual.getUfEmitente().toString(), String.valueOf(vendaAtual.getAmbiente()), vendaAtual.getChaveAcesso() + vendaAtual.getDigitoChaveAcesso(), protocolo, justificativa.trim(), vendaAtual.getEmpresa().getCnpj());

			boolean cancelado = (Boolean) dadosCancelamento.get("nfeCancelada");

			String resposta = "";
			if (cancelado) {
				OutputStream out = new FileOutputStream(new File(caminhoArquivo + "-nfeCanc.xml"));
				out.write(((String) dadosCancelamento.get("xmlCancelamento")).getBytes());
				out.close();

				vendaAtual.setStatusNota(NfceConstantes.SN_CANCELADA);
				vendaCabecalhoDAO.merge(vendaAtual);
				resposta += "NFC-e Cancelada com sucesso";
			} else {
				resposta += "A NFC-e NÃO foi cancelada.";
			}
			resposta += "\n" + (String) dadosCancelamento.get("motivo1");
			resposta += "\n" + (String) dadosCancelamento.get("motivo2");

			return resposta;
		}
	}

	@SuppressWarnings("rawtypes")
	private String inutilizaNumero(String justificativa) throws Exception {
		ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
		String caminhoArquivo = context.getRealPath(diretorioXml) + System.getProperty("file.separator") + vendaAtual.getChaveAcesso() + vendaAtual.getDigitoChaveAcesso();

		SimpleDateFormat formatoAno = new SimpleDateFormat("yy");
		InutilizaNumero inutiliza = new InutilizaNumero();
		Map dadosInutilizacao = inutiliza.inutiliza(alias, certificado, senhaCertificado, vendaAtual.getUfEmitente().toString(), String.valueOf(vendaAtual.getAmbiente()), formatoAno.format(vendaAtual.getDataHoraEmissao()), vendaAtual.getSerie(), justificativa.trim(), vendaAtual.getEmpresa().getCnpj(), vendaAtual.getNumero(), vendaAtual.getNumero());

		boolean inutilizado = (Boolean) dadosInutilizacao.get("nfeInutilizada");

		String resposta = "";
		if (inutilizado) {
			OutputStream out = new FileOutputStream(new File(caminhoArquivo + "-nfeinut.xml"));
			out.write(((String) dadosInutilizacao.get("xmlInutilizacao")).getBytes());
			out.close();

			vendaAtual.setStatusNota(NfceConstantes.SN_INUTILIZADA);
			vendaCabecalhoDAO.merge(vendaAtual);
			resposta += "NFC-e Inutlizada com sucesso";
		} else {
			resposta += "A NFC-e NÃO foi inutilizada.";
		}
		resposta += "\n" + (String) dadosInutilizacao.get("motivo");

		return resposta;
	}

	public void identificaVendedor() {
		RequestContext context = RequestContext.getCurrentInstance();
		if (statusCaixa == SC_VENDA_EM_ANDAMENTO) {
			if (vendedorSelecionado == null) {
				FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_WARN, "É necessário selecionar um vendedor.", null);
				context.addCallbackParam("vendedorSelecionado", false);
			} else {
				vendaAtual.setVendedor(vendedorSelecionado);
				vendedorSelecionado = null;
				FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_WARN, "Vendedor: ", vendaAtual.getVendedor().getColaborador().getPessoa().getNome());
				context.addCallbackParam("vendedorSelecionado", true);
			}
		} else {
			FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_WARN, "Não existe venda em andamento..", null);
		}
	}

	public void descontoAcrescimo() {
		if (statusCaixa == SC_VENDA_EM_ANDAMENTO) {
			RequestContext context = RequestContext.getCurrentInstance();
			if (loginGerenteSupervisor()) {
				if (valorInformadoDescontoAcrescimo != null) {
					if (valorInformadoDescontoAcrescimo.compareTo(BigDecimal.ZERO) <= 0) {
						FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_WARN, "Valor zerado ou negativo.", "Operação não realizada.");
						context.addCallbackParam("valorInformado", false);
					} else if (valorInformadoDescontoAcrescimo.compareTo(vendaAtual.getValorTotalProdutos()) >= 0) {
						FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_WARN, "Valor não pode ser superior ou igual ao valor da venda.", "Operação não realizada.");
						context.addCallbackParam("valorInformado", false);
					} else {
						String mensagem = "";
						switch (operacaoDescontoAcrescimoSelecionada) {
						case DESCONTO_VALOR: {
							desconto = valorInformadoDescontoAcrescimo;
							mensagem = "Desconto: ";
							break;
						}
						case DESCONTO_PERCENTUAL: {
							desconto = Biblioteca.divide(valorInformadoDescontoAcrescimo, BigDecimal.valueOf(100));
							desconto = Biblioteca.multiplica(subTotal, desconto);
							mensagem = "Desconto: ";
							break;
						}
						case ACRESCIMO_VALOR: {
							acrescimo = valorInformadoDescontoAcrescimo;
							mensagem = "Acréscimo: ";
							break;
						}
						case ACRESCIMO_PERCENTUAL: {
							acrescimo = Biblioteca.divide(valorInformadoDescontoAcrescimo, BigDecimal.valueOf(100));
							acrescimo = Biblioteca.multiplica(subTotal, acrescimo);
							mensagem = "Acréscimo: ";
							break;
						}
						case CANCELA_DESCONTO_ACRESCIMO: {
							acrescimo = BigDecimal.ZERO;
							desconto = BigDecimal.ZERO;
							mensagem = "Desconto e/ou Acréscimo cancelado.";
							break;
						}
						}
						atualizaTotais();
						FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_INFO, mensagem, Biblioteca.formatoDecimal("V", valorInformadoDescontoAcrescimo.doubleValue()));
						context.addCallbackParam("valorInformado", true);
					}
				} else {
					FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_WARN, "É necessário informar um valor.", null);
					context.addCallbackParam("valorInformado", false);
				}
			}
		} else {
			FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_WARN, "Não existe venda em andamento..", null);
		}
	}

	private boolean loginGerenteSupervisor() {
		try {
			String login = gerenteSupervisor.getSenha();
			String senha = gerenteSupervisor.getSenha();
			gerenteSupervisor = new NfceOperador();
			List<Filtro> listaFiltro = new ArrayList<>();
			listaFiltro.add(new Filtro("AND", "login", "=", login));
			listaFiltro.add(new Filtro("AND", "senha", "=", senha));
			gerenteSupervisor = nfceOperadorDAO.getBean(NfceOperador.class, listaFiltro);
			if (gerenteSupervisor != null && gerenteSupervisor.getNivelAutorizacao() != null && (gerenteSupervisor.getNivelAutorizacao().equals("S") || gerenteSupervisor.getNivelAutorizacao().equals("G"))) {
				return true;
			} else {
				FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_WARN, "Gerente ou Supervisor: dados incorretos ou nível de acesso não permitido.", "");
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_ERROR, "Erro no login Gerente Supervisor.", e.getMessage());
		} finally {
			gerenteSupervisor = new NfceOperador();
		}
		return false;
	}

	public void pesquisaProduto() {
		RequestContext context = RequestContext.getCurrentInstance();
		if (produtoSelecionado == null) {
			FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_WARN, "É necessário selecionar um produto.", null);
			context.addCallbackParam("produtoSelecionado", false);
		} else {
			codigoProduto = produtoSelecionado.getGtin();
			produtoSelecionado = null;
			context.addCallbackParam("produtoSelecionado", true);
		}
	}

	public boolean podeInserir() {
		return FacesContextUtil.isUserInRole("NFCE_INSERE") || FacesContextUtil.isUserInRole("ADMIN");
	}

	public String getCodigoProduto() {
		return codigoProduto;
	}

	public void setCodigoProduto(String codigoProduto) {
		this.codigoProduto = codigoProduto;
	}

	public BigDecimal getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(BigDecimal quantidade) {
		this.quantidade = quantidade;
	}

	public NfeCabecalho getNfeCabecalho() {
		return vendaAtual;
	}

	public void setNfeCabecalho(NfeCabecalho nfeCabecalho) {
		this.vendaAtual = nfeCabecalho;
	}

	public NfeDetalhe getNfeDetalhe() {
		return vendaDetalhe;
	}

	public void setNfeDetalhe(NfeDetalhe nfeDetalhe) {
		this.vendaDetalhe = nfeDetalhe;
	}

	public String getMensagemOperador() {
		return mensagemOperador;
	}

	public void setMensagemOperador(String mensagemOperador) {
		this.mensagemOperador = mensagemOperador;
	}

	public String getDescricaoProduto() {
		return descricaoProduto;
	}

	public void setDescricaoProduto(String descricaoProduto) {
		this.descricaoProduto = descricaoProduto;
	}

	public List<NfceTipoPagamento> getListaTipoPagamento() {
		return listaTipoPagamento;
	}

	public void setListaTipoPagamento(List<NfceTipoPagamento> listaTipoPagamento) {
		this.listaTipoPagamento = listaTipoPagamento;
	}

	public NfceTipoPagamento getTipoPagamento() {
		return tipoPagamento;
	}

	public void setTipoPagamento(NfceTipoPagamento tipoPagamento) {
		this.tipoPagamento = tipoPagamento;
	}

	public NfeFormaPagamento getFormaPagamento() {
		return formaPagamento;
	}

	public void setFormaPagamento(NfeFormaPagamento formaPagamento) {
		this.formaPagamento = formaPagamento;
	}

	public String getTxtValorUnitario() {
		return txtValorUnitario;
	}

	public void setTxtValorUnitario(String txtValorUnitario) {
		this.txtValorUnitario = txtValorUnitario;
	}

	public String getTxtValorTotalItem() {
		return txtValorTotalItem;
	}

	public void setTxtValorTotalItem(String txtValorTotalItem) {
		this.txtValorTotalItem = txtValorTotalItem;
	}

	public String getTxtValorSubTotal() {
		return txtValorSubTotal;
	}

	public void setTxtValorSubTotal(String txtValorSubTotal) {
		this.txtValorSubTotal = txtValorSubTotal;
	}

	public String getTxtValorTotalGeral() {
		return txtValorTotalGeral;
	}

	public void setTxtValorTotalGeral(String txtValorTotalGeral) {
		this.txtValorTotalGeral = txtValorTotalGeral;
	}

	public String getTxtValorDesconto() {
		return txtValorDesconto;
	}

	public void setTxtValorDesconto(String txtValorDesconto) {
		this.txtValorDesconto = txtValorDesconto;
	}

	public String getTxtValorAcrescimo() {
		return txtValorAcrescimo;
	}

	public void setTxtValorAcrescimo(String txtValorAcrescimo) {
		this.txtValorAcrescimo = txtValorAcrescimo;
	}

	public String getTxtValorTroco() {
		return txtValorTroco;
	}

	public void setTxtValorTroco(String txtValorTroco) {
		this.txtValorTroco = txtValorTroco;
	}

	public String getTxtValorTotalRecebido() {
		return txtValorTotalRecebido;
	}

	public void setTxtValorTotalRecebido(String txtValorTotalRecebido) {
		this.txtValorTotalRecebido = txtValorTotalRecebido;
	}

	public String getTxtValorTotalReceber() {
		return txtValorTotalReceber;
	}

	public void setTxtValorTotalReceber(String txtValorTotalReceber) {
		this.txtValorTotalReceber = txtValorTotalReceber;
	}

	public String getTxtValorSaldoRestante() {
		return txtValorSaldoRestante;
	}

	public void setTxtValorSaldoRestante(String txtValorSaldoRestante) {
		this.txtValorSaldoRestante = txtValorSaldoRestante;
	}

	public BigDecimal getTotalReceber() {
		return totalReceber;
	}

	public void setTotalReceber(BigDecimal totalReceber) {
		this.totalReceber = totalReceber;
	}

	public BigDecimal getTroco() {
		return troco;
	}

	public void setTroco(BigDecimal troco) {
		this.troco = troco;
	}

	public BigDecimal getTotalRecebido() {
		return totalRecebido;
	}

	public void setTotalRecebido(BigDecimal totalRecebido) {
		this.totalRecebido = totalRecebido;
	}

	public BigDecimal getSaldoRestante() {
		return saldoRestante;
	}

	public void setSaldoRestante(BigDecimal saldoRestante) {
		this.saldoRestante = saldoRestante;
	}

	public BigDecimal getValorPagamento() {
		return valorPagamento;
	}

	public void setValorPagamento(BigDecimal valorPagamento) {
		this.valorPagamento = valorPagamento;
	}

	public String getDanfe() {
		return danfe;
	}

	public void setDanfe(String ultimoDanfe) {
		this.danfe = ultimoDanfe;
	}

	public ViewNfceCliente getCliente() {
		return cliente;
	}

	public void setCliente(ViewNfceCliente cliente) {
		this.cliente = cliente;
	}

	public Integer getNumeroItemCancelar() {
		return numeroItemCancelar;
	}

	public void setNumeroItemCancelar(Integer numeroItemCancelar) {
		this.numeroItemCancelar = numeroItemCancelar;
	}

	public NfceOperador getGerenteSupervisor() {
		return gerenteSupervisor;
	}

	public void setGerenteSupervisor(NfceOperador gerenteSupervisor) {
		this.gerenteSupervisor = gerenteSupervisor;
	}

	public NfeCabecalho getVendaSelecionada() {
		return vendaSelecionada;
	}

	public void setVendaSelecionada(NfeCabecalho vendaSelecionada) {
		this.vendaSelecionada = vendaSelecionada;
	}

	public Integer getOperacaoNfce() {
		return operacaoNfce;
	}

	public void setOperacaoNfce(Integer operacaoNfce) {
		this.operacaoNfce = operacaoNfce;
	}

	public String getJustificativa() {
		return justificativa;
	}

	public void setJustificativa(String justificativa) {
		this.justificativa = justificativa;
	}

	public T2TiLazyDataModel<NfeCabecalho> getDataModel() {
		return dataModel;
	}

	public T2TiLazyDataModel<Vendedor> getVendedorDataModel() {
		return vendedorDataModel;
	}

	public Vendedor getVendedorSelecionado() {
		return vendedorSelecionado;
	}

	public void setVendedorSelecionado(Vendedor vendedorSelecionado) {
		this.vendedorSelecionado = vendedorSelecionado;
	}

	public BigDecimal getValorInformadoDescontoAcrescimo() {
		return valorInformadoDescontoAcrescimo;
	}

	public void setValorInformadoDescontoAcrescimo(BigDecimal valorInformadoDescontoAcrescimo) {
		this.valorInformadoDescontoAcrescimo = valorInformadoDescontoAcrescimo;
	}

	public HashMap<String, Integer> getOperacaoDescontoAcrescimo() {
		return operacaoDescontoAcrescimo;
	}

	public Integer getOperacaoDescontoAcrescimoSelecionada() {
		return operacaoDescontoAcrescimoSelecionada;
	}

	public void setOperacaoDescontoAcrescimoSelecionada(Integer operacaoDescontoAcrescimoSelecionada) {
		this.operacaoDescontoAcrescimoSelecionada = operacaoDescontoAcrescimoSelecionada;
	}

	public T2TiLazyDataModel<Produto> getProdutoDataModel() {
		return produtoDataModel;
	}

	public Produto getProdutoSelecionado() {
		return produtoSelecionado;
	}

	public void setProdutoSelecionado(Produto produtoSelecionado) {
		this.produtoSelecionado = produtoSelecionado;
	}

	public boolean isPagamentoParcelado() {
		return pagamentoParcelado;
	}

	public void setPagamentoParcelado(boolean pagamentoParcelado) {
		this.pagamentoParcelado = pagamentoParcelado;
	}

	public Integer getQuantidadeParcelas() {
		return quantidadeParcelas;
	}

	public void setQuantidadeParcelas(Integer quantidadeParcelas) {
		this.quantidadeParcelas = quantidadeParcelas;
	}

}
