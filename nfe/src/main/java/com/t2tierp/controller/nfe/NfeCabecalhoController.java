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
package com.t2tierp.controller.nfe;

import java.awt.Image;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.security.KeyStore;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.swing.ImageIcon;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Unmarshaller;

import net.sf.jasperreports.engine.JREmptyDataSource;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRXmlDataSource;

import org.apache.axis2.classloader.IoUtil;
import org.apache.commons.mail.EmailAttachment;
import org.apache.commons.mail.MultiPartEmail;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.UploadedFile;

import br.inf.portalfiscal.nfe.procnfe.TNfeProc;

import com.t2tierp.controller.AbstractController;
import com.t2tierp.controller.T2TiLazyDataModel;
import com.t2tierp.model.bean.cadastros.Certificado;
import com.t2tierp.model.bean.cadastros.Cliente;
import com.t2tierp.model.bean.cadastros.ContaCaixa;
import com.t2tierp.model.bean.cadastros.Empresa;
import com.t2tierp.model.bean.cadastros.PessoaCliente;
import com.t2tierp.model.bean.cadastros.Produto;
import com.t2tierp.model.bean.cadastros.Transportadora;
import com.t2tierp.model.bean.financeiro.FinDocumentoOrigem;
import com.t2tierp.model.bean.financeiro.FinLancamentoReceber;
import com.t2tierp.model.bean.financeiro.FinParcelaReceber;
import com.t2tierp.model.bean.financeiro.FinStatusParcela;
import com.t2tierp.model.bean.nfe.NfeCabecalho;
import com.t2tierp.model.bean.nfe.NfeCalculo;
import com.t2tierp.model.bean.nfe.NfeConfiguracao;
import com.t2tierp.model.bean.nfe.NfeCteReferenciado;
import com.t2tierp.model.bean.nfe.NfeCupomFiscalReferenciado;
import com.t2tierp.model.bean.nfe.NfeDeclaracaoImportacao;
import com.t2tierp.model.bean.nfe.NfeDestinatario;
import com.t2tierp.model.bean.nfe.NfeDetEspecificoArmamento;
import com.t2tierp.model.bean.nfe.NfeDetEspecificoMedicamento;
import com.t2tierp.model.bean.nfe.NfeDetalhe;
import com.t2tierp.model.bean.nfe.NfeDetalheImpostoCofins;
import com.t2tierp.model.bean.nfe.NfeDetalheImpostoIcms;
import com.t2tierp.model.bean.nfe.NfeDetalheImpostoIi;
import com.t2tierp.model.bean.nfe.NfeDetalheImpostoIpi;
import com.t2tierp.model.bean.nfe.NfeDetalheImpostoIssqn;
import com.t2tierp.model.bean.nfe.NfeDetalheImpostoPis;
import com.t2tierp.model.bean.nfe.NfeDuplicata;
import com.t2tierp.model.bean.nfe.NfeFatura;
import com.t2tierp.model.bean.nfe.NfeImportacaoDetalhe;
import com.t2tierp.model.bean.nfe.NfeLocalEntrega;
import com.t2tierp.model.bean.nfe.NfeLocalRetirada;
import com.t2tierp.model.bean.nfe.NfeNfReferenciada;
import com.t2tierp.model.bean.nfe.NfeNumero;
import com.t2tierp.model.bean.nfe.NfeProdRuralReferenciada;
import com.t2tierp.model.bean.nfe.NfeReferenciada;
import com.t2tierp.model.bean.nfe.NfeTransporte;
import com.t2tierp.model.bean.nfe.NfeTransporteReboque;
import com.t2tierp.model.bean.nfe.NfeTransporteVolume;
import com.t2tierp.model.bean.nfe.RespostaSefaz;
import com.t2tierp.model.bean.tributacao.TributIss;
import com.t2tierp.model.bean.tributacao.TributOperacaoFiscal;
import com.t2tierp.model.bean.tributacao.ViewTributacaoCofins;
import com.t2tierp.model.bean.tributacao.ViewTributacaoIcms;
import com.t2tierp.model.bean.tributacao.ViewTributacaoIcmsCustom;
import com.t2tierp.model.bean.tributacao.ViewTributacaoIpi;
import com.t2tierp.model.bean.tributacao.ViewTributacaoPis;
import com.t2tierp.model.dao.Filtro;
import com.t2tierp.model.dao.InterfaceDAO;
import com.t2tierp.model.dao.controleestoque.ControleEstoqueDAO;
import com.t2tierp.util.Biblioteca;
import com.t2tierp.util.FacesContextUtil;

@ManagedBean
@ViewScoped
public class NfeCabecalhoController extends AbstractController<NfeCabecalho> implements Serializable {

	private static final long serialVersionUID = 1L;
	private NfeCabecalhoDataModel dataModel;
	private NfeDetalhe nfeDetalhe;
	private NfeDetalhe nfeDetalheSelecionado;
	private NfeDetalheImpostoIcms nfeDetalheImpostoIcms = new NfeDetalheImpostoIcms();
	private NfeTransporteReboque nfeTransporteReboque;
	private NfeTransporteReboque nfeTransporteReboqueSelecionado;
	private Integer numeroItem = 0;
	private boolean podeIncluirProduto = false;
	@EJB
	private InterfaceDAO<TributOperacaoFiscal> operacaoFiscalDao;
	@EJB
	private InterfaceDAO<Produto> produtoDao;
	PessoaCliente pessoaCliente;
	@EJB
	private InterfaceDAO<PessoaCliente> pessoaClienteDao;
	@EJB
	private InterfaceDAO<ViewTributacaoIcms> icmsDao;
	@EJB
	private InterfaceDAO<ViewTributacaoIpi> ipiDao;
	@EJB
	private InterfaceDAO<ViewTributacaoPis> pisDao;
	@EJB
	private InterfaceDAO<ViewTributacaoCofins> cofinsDao;
	@EJB
	private InterfaceDAO<ViewTributacaoIcmsCustom> icmsCustomDao;
	@EJB
	private InterfaceDAO<NfeNumero> nfeNumeroDao;
	@EJB
	private InterfaceDAO<NfeConfiguracao> nfeConfiguracaoDao;
	@EJB
	private ControleEstoqueDAO controleEstoqueDao;
	@EJB
	private InterfaceDAO<NfeDetalhe> nfeDetalheDao;
	@EJB
	private InterfaceDAO<Transportadora> transportadoraDao;
	@EJB
	private InterfaceDAO<FinLancamentoReceber> lancamentoReceberDao;

	private boolean dadosSalvos = false;
	private String senhaCertificado;
	private Certificado certificado;
	private String justificativaCancelamento;
	private KeyStore keyStore;
	private final String diretorioXml = "modulos/nfe/xml";
	private String cartaCorrecao;
	private boolean requerJustificativaDanfe;
	private String justificativaDanfe;

	@Override
	public Class<NfeCabecalho> getClazz() {
		return NfeCabecalho.class;
	}

	@Override
	public String getFuncaoBase() {
		return "NFE_CABECALHO";
	}

	@Override
	public T2TiLazyDataModel<NfeCabecalho> getDataModel() {
		if (dataModel == null) {
			dataModel = new NfeCabecalhoDataModel();
			dataModel.setClazz(getClazz());
			dataModel.setDao(dao);
		}
		return dataModel;
	}

	@Override
	public void incluir() {
		super.incluir();
		getObjeto().setDestinatario(new NfeDestinatario());
		getObjeto().getDestinatario().setNfeCabecalho(getObjeto());
		getObjeto().setLocalEntrega(new NfeLocalEntrega());
		getObjeto().getLocalEntrega().setNfeCabecalho(getObjeto());
		getObjeto().setLocalRetirada(new NfeLocalRetirada());
		getObjeto().getLocalRetirada().setNfeCabecalho(getObjeto());
		getObjeto().setTransporte(new NfeTransporte());
		getObjeto().getTransporte().setNfeCabecalho(getObjeto());
		getObjeto().setFatura(new NfeFatura());
		getObjeto().getFatura().setNfeCabecalho(getObjeto());

		getObjeto().setListaNfeReferenciada(new HashSet<NfeReferenciada>());
		getObjeto().setListaNfReferenciada(new HashSet<NfeNfReferenciada>());
		getObjeto().setListaCteReferenciado(new HashSet<NfeCteReferenciado>());
		getObjeto().setListaProdRuralReferenciada(new HashSet<NfeProdRuralReferenciada>());
		getObjeto().setListaCupomFiscalReferenciado(new HashSet<NfeCupomFiscalReferenciado>());
		getObjeto().getTransporte().setListaTransporteReboque(new HashSet<NfeTransporteReboque>());
		getObjeto().getTransporte().setListaTransporteVolume(new HashSet<NfeTransporteVolume>());
		getObjeto().setListaDuplicata(new HashSet<NfeDuplicata>());
		getObjeto().setListaNfeDetalhe(new ArrayList<NfeDetalhe>());

		valoresPadrao();
		dadosSalvos = false;
	}

	@Override
	public void alterar() {
		super.alterar();
		dadosSalvos = true;
	}

	@Override
	public void salvar() {
		if (verificaAutorizado()) {
			FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_INFO, "Esta NF-e já foi autorizada. Os dados não foram salvos.", "");
		} else if (verificaCancelado()) {
			FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_INFO, "Esta NF-e já foi cancelada. Os dados não foram salvos.", "");
		} else {
			try {
				if (getObjeto().getId() == null) {
					numeroNfe();
					configuraNfe();
				} else {
					HashMap<String, Object> filtro = new HashMap<>();
					filtro.put("nfeCabecalho", getObjeto());
					String atributos[] = new String[] { "produto", "quantidadeComercial" };
					List<NfeDetalhe> listaNfeDetOld = nfeDetalheDao.getBeans(NfeDetalhe.class, 0, 0, null, null, filtro, atributos);
					for (NfeDetalhe detalhe : listaNfeDetOld) {
						controleEstoqueDao.atualizaEstoque(detalhe.getProduto().getId(), detalhe.getQuantidadeComercial());
					}
				}
				defineNumeroItens();
				controleEstoqueDao.atualizaEstoque(getObjeto().getListaNfeDetalhe());
				super.salvar();
				dadosSalvos = true;
			} catch (Exception e) {
				e.printStackTrace();
				FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro ao salvar o registro", e.getMessage());
			}
		}
	}

	private void configuraNfe() throws Exception {
		NfeConfiguracao configuracao = nfeConfiguracaoDao.getBean(NfeConfiguracao.class, new ArrayList<Filtro>());

		getObjeto().setAmbiente(configuracao.getWebserviceAmbiente());
		getObjeto().setProcessoEmissao(configuracao.getProcessoEmissao());
		getObjeto().setVersaoProcessoEmissao(configuracao.getVersaoProcessoEmissao());
	}

	private void defineNumeroItens() throws Exception {
		int i = 0;
		for (NfeDetalhe nfeDetalhe : getObjeto().getListaNfeDetalhe()) {
			nfeDetalhe.setNumeroItem(++i);

			for (NfeDetEspecificoArmamento armamento : nfeDetalhe.getListaArmamento()) {
				armamento.setNfeDetalhe(nfeDetalhe);
			}

			for (NfeDeclaracaoImportacao declaracaoImportacao : nfeDetalhe.getListaDeclaracaoImportacao()) {
				declaracaoImportacao.setNfeDetalhe(nfeDetalhe);
				for (NfeImportacaoDetalhe importacaoDetalhe : declaracaoImportacao.getListaImportacaoDetalhe()) {
					importacaoDetalhe.setNfeDeclaracaoImportacao(declaracaoImportacao);
				}
			}
			for (NfeDetEspecificoMedicamento medicamento : nfeDetalhe.getListaMedicamento()) {
				medicamento.setNfeDetalhe(nfeDetalhe);
			}
		}
	}
	
	@SuppressWarnings("rawtypes")
	public void enviaNfe() {
		if (dadosSalvos) {
			if (verificaAutorizado()) {
				FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_INFO, "Esta NF-e já foi autorizada. Envio não realizado", null);
			} else if (verificaCancelado()) {
				FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_INFO, "Esta NF-e já foi cancelada. Envio não realizado", null);
			} else if (certificado == null || certificado.getArquivo() == null || certificado.getSenha() == null) {
				FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_INFO, "É Necessário informar os dados do certificado antes do envio!", null);
			} else {
				try {
					Empresa empresa = FacesContextUtil.getEmpresaUsuario();

					getKeyStore();

					GeraXMLEnvio geraXmlNfe = new GeraXMLEnvio();
					String xml = geraXmlNfe.gerarXmlEnvio(empresa, getObjeto(), certificado.getAlias(), getKeyStore(), certificado.getSenha());

					try {
						ValidaXmlNfe validaXmlNfe = new ValidaXmlNfe();
						validaXmlNfe.validaXmlEnvio(xml, (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext());
					} catch (Exception e) {
						throw new Exception("Erro na validação do XML\n" + e.getMessage());
					}

					EnviaNfe envia = new EnviaNfe();
					Map mapResposta = envia.enviaNfe(xml, certificado.getAlias(), getKeyStore(), certificado.getSenha(), getObjeto().getUfEmitente().toString(), String.valueOf(getObjeto().getAmbiente()));

					Boolean autorizado = (Boolean) mapResposta.get("autorizado");
					RespostaSefaz respostaSefaz = new RespostaSefaz();
					respostaSefaz.setAutorizado(autorizado);
					respostaSefaz.setResposta((String) mapResposta.get("resposta"));
					respostaSefaz.setNumeroRecibo((String) mapResposta.get("numeroRecibo"));
					respostaSefaz.setXmlEnviNfe((String) mapResposta.get("xmlEnviNfe"));

					if (autorizado) {
						String xmlProc = (String) mapResposta.get("xmlProc");
						salvaArquivos(xmlProc);
					}

					FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_INFO, "NF-e enviada com sucesso!", null);
				} catch (Exception e) {
					e.printStackTrace();
					FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro ao enviar a NF-e!", e.getMessage());
				}
			}
		} else {
			FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_INFO, "Antes de enviar a NF-e é necessário salvar as informações no banco!", null);
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private void salvaArquivos(String xml) throws Exception {
		getObjeto().setStatusNota(5);

		setObjeto(dao.merge(getObjeto()));

		// salva o xml
		ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
		String caminhoArquivo = context.getRealPath(diretorioXml) + System.getProperty("file.separator") + getObjeto().getChaveAcesso() + getObjeto().getDigitoChaveAcesso();
		OutputStream outXml = new FileOutputStream(new File(caminhoArquivo + "-nfeproc.xml"));
		outXml.write(xml.getBytes());
		outXml.close();

		// gera e salva o danfe
		Map map = new HashMap();
		Image image = new ImageIcon(context.getRealPath("imagens/logo_t2ti.png")).getImage();
		map.put("Logo", image);

		JRXmlDataSource jrXmlDataSource = new JRXmlDataSource(caminhoArquivo + "-nfeproc.xml", "/nfeProc/NFe/infNFe/det");
		JasperPrint jp = JasperFillManager.fillReport(context.getRealPath("modulos/nfe/danfe/danfeR.jasper"), map, jrXmlDataSource);
		OutputStream outPdf = new FileOutputStream(new File(caminhoArquivo + ".pdf"));
		outPdf.write(JasperExportManager.exportReportToPdf(jp));
		outPdf.close();
	}

	public void impressaoDanfe() {
		if (getObjeto().getQuantidadeImpressaoDanfe() == null) {
			requerJustificativaDanfe = false;
			getObjeto().setQuantidadeImpressaoDanfe(0);
			danfe();
		} else {
			requerJustificativaDanfe = true;
		}
	}
	
	public void danfe() {
		try {
			if (getObjeto().getStatusNota().intValue() != 5) {
				throw new Exception("NF-e não autorizada. Impressão do Danfe não permitida!");
			}

			ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
			String caminhoArquivo = context.getRealPath(diretorioXml) + System.getProperty("file.separator") + getObjeto().getChaveAcesso() + getObjeto().getDigitoChaveAcesso() + ".pdf";
			File arquivoPdf = new File(caminhoArquivo);
			if (!arquivoPdf.exists()) {
				throw new Exception("Arquivo do danfe não localizado!");
			}
			
			getObjeto().setQuantidadeImpressaoDanfe(getObjeto().getQuantidadeImpressaoDanfe() + 1);			
			dao.merge(getObjeto());

			FacesContextUtil.downloadArquivo(arquivoPdf, arquivoPdf.getName());
		} catch (Exception e) {
			e.printStackTrace();
			FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro ao buscar o danfe!", e.getMessage());
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void etiqueta() {
		try {
			if (getObjeto().getStatusNota().intValue() != 5) {
				throw new Exception("NF-e não autorizada. Geração de etiqueta não permitida!");
			}

			ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
			
			File etiqueta = File.createTempFile("etiqueta", ".pdf");

			Map map = new HashMap();
			Image image = new ImageIcon(context.getRealPath("imagens/logo_t2ti.png")).getImage();
			map.put("LOGO", image);
			map.put("EMPRESA", getObjeto().getEmpresa().getNomeFantasia() );
			map.put("DESCRICAO", "Produtos Diversos" );
			map.put("CONTEUDO", "000457092");
			map.put("QUANTIDADE", getObjeto().getListaNfeDetalhe().size());
			map.put("NUMERO", getObjeto().getChaveAcesso());

            JasperPrint jp = JasperFillManager.fillReport(context.getRealPath("modulos/nfe/etiqueta/EtiquetaNFe.jasper"), map, new JREmptyDataSource());
			OutputStream outPdf = new FileOutputStream(etiqueta);
			outPdf.write(JasperExportManager.exportReportToPdf(jp));
			outPdf.close();

			FacesContextUtil.downloadArquivo(etiqueta, etiqueta.getName());
		} catch (Exception e) {
			e.printStackTrace();
			FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro ao gerar a etiqueta!", e.getMessage());
		}
	}
	
	public void enviaEmail() {
		try {
			if (getObjeto().getStatusNota().intValue() != 5) {
				throw new Exception("NF-e não autorizada. Envio de email não permitido!");
			}

			NfeConfiguracao configuracao = nfeConfiguracaoDao.getBean(NfeConfiguracao.class, new ArrayList<Filtro>());

			if (configuracao == null) {
				throw new Exception("Configuração NFe não definida");
			}
			if (configuracao.getEmailAssunto() == null || configuracao.getEmailSenha() == null || configuracao.getEmailServidorSmtp() == null || configuracao.getEmailTexto() == null || configuracao.getEmailUsuario() == null) {
				throw new Exception("Configuração de envio de email não definida");
			}

			MultiPartEmail email = new MultiPartEmail();
			email.setHostName(configuracao.getEmailServidorSmtp());
			email.setFrom(configuracao.getEmpresa().getEmail());
			email.addTo(getObjeto().getDestinatario().getEmail());
			email.setSubject(configuracao.getEmailAssunto());
			email.setMsg(configuracao.getEmailTexto());

			email.setAuthentication(configuracao.getEmailUsuario(), configuracao.getEmailSenha());
			if (configuracao.getEmailPorta() != null) {
				if (configuracao.getEmailAutenticaSsl() != null && configuracao.getEmailAutenticaSsl().equals("S")) {
					email.setSSLOnConnect(true);
					email.setSslSmtpPort(configuracao.getEmailPorta().toString());
				} else {
					email.setSmtpPort(configuracao.getEmailPorta());
				}
			}

			ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();

			EmailAttachment anexo = new EmailAttachment();
			anexo.setPath(context.getRealPath(diretorioXml) + System.getProperty("file.separator") + getObjeto().getChaveAcesso() + getObjeto().getDigitoChaveAcesso() + "-nfeproc.xml");
			anexo.setDisposition(EmailAttachment.ATTACHMENT);
			anexo.setDescription("Nota Fiscal Eletronica");
			anexo.setName(getObjeto().getChaveAcesso() + getObjeto().getDigitoChaveAcesso() + "-nfeproc.xml");

			EmailAttachment anexo2 = new EmailAttachment();
			anexo2.setPath(context.getRealPath(diretorioXml) + System.getProperty("file.separator") + getObjeto().getChaveAcesso() + getObjeto().getDigitoChaveAcesso() + ".pdf");
			anexo2.setDisposition(EmailAttachment.ATTACHMENT);
			anexo2.setDescription("Nota Fiscal Eletronica");
			anexo2.setName(getObjeto().getChaveAcesso() + getObjeto().getDigitoChaveAcesso() + ".pdf");

			email.attach(anexo);
			email.attach(anexo2);

			email.send();

			FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_INFO, "Email enviado com sucesso", "");
		} catch (Exception ex) {
			ex.printStackTrace();
			FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro ao enviar o email.", ex.getMessage());
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void cancelaNfe() {
		if (certificado == null || certificado.getArquivo() == null || certificado.getSenha() == null) {
			FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_INFO, "É Necessário informar os dados do certificado antes do cancelamento!", null);
		} else {
			try {
				if (justificativaCancelamento == null) {
					throw new Exception("É necessário informar uma justificativa para o cancelamento da NF-e.");
				}
				if (justificativaCancelamento.trim().equals("")) {
					throw new Exception("É necessário informar uma justificativa para o cancelamento da NF-e.");
				}
				if (justificativaCancelamento.trim().length() < 15) {
					throw new Exception("A justificativa deve ter no mínimo 15 caracteres.");
				}
				if (justificativaCancelamento.trim().length() > 255) {
					throw new Exception("A justificativa deve ter no máximo 255 caracteres.");
				}
				if (getObjeto().getStatusNota().intValue() == 6) {
					throw new Exception("NF-e já cancelada. Cancelamento não permitido!");
				}
				if (getObjeto().getStatusNota().intValue() != 5) {
					throw new Exception("NF-e não autorizada. Cancelamento não permitido!");
				}

				ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
				String caminhoArquivo = context.getRealPath(diretorioXml) + System.getProperty("file.separator") + getObjeto().getChaveAcesso() + getObjeto().getDigitoChaveAcesso() + "-nfeproc.xml";
				File arquivoXml = new File(caminhoArquivo);
				if (!arquivoXml.exists()) {
					throw new Exception("Arquivo XML da NF-e não localizado!");
				}

				JAXBContext jc = JAXBContext.newInstance("br.inf.portalfiscal.nfe.procnfe");
				Unmarshaller unmarshaller = jc.createUnmarshaller();

				JAXBElement<TNfeProc> element = (JAXBElement) unmarshaller.unmarshal(arquivoXml);
				String protocolo = element.getValue().getProtNFe().getInfProt().getNProt();

				getKeyStore();

				CancelaNfe cancelaNfe = new CancelaNfe();
				Map dadosCancelamento = cancelaNfe.cancelaNfe(certificado.getAlias(), getKeyStore(), certificado.getSenha(), getObjeto().getUfEmitente().toString(), String.valueOf(getObjeto().getAmbiente()), getObjeto().getChaveAcesso() + getObjeto().getDigitoChaveAcesso(), protocolo, justificativaCancelamento.trim(), getObjeto().getEmpresa().getCnpj());

				RespostaSefaz respostaSefaz = new RespostaSefaz();
				respostaSefaz.setCancelado((Boolean) dadosCancelamento.get("nfeCancelada"));

				String resposta = "";
				if (respostaSefaz.isCancelado()) {
					// salva o xml
					caminhoArquivo = context.getRealPath(diretorioXml) + System.getProperty("file.separator") + getObjeto().getChaveAcesso() + getObjeto().getDigitoChaveAcesso() + "-nfeCanc.xml";
					OutputStream out = new FileOutputStream(new File(caminhoArquivo));
					out.write(((String) dadosCancelamento.get("xmlCancelamento")).getBytes());
					out.close();

					getObjeto().setStatusNota(6);
					setObjeto(dao.merge(getObjeto()));

					// atualiza o estoque
					for (NfeDetalhe nfeDetalhe : getObjeto().getListaNfeDetalhe()) {
						controleEstoqueDao.atualizaEstoque(nfeDetalhe.getProduto().getId(), nfeDetalhe.getQuantidadeComercial());
					}

					resposta += "NF-e Cancelada com sucesso";
				} else {
					resposta += "A NF-e NÃO foi cancelada.";
				}
				resposta += "\n" + (String) dadosCancelamento.get("motivo1");
				resposta += "\n" + (String) dadosCancelamento.get("motivo2");

				respostaSefaz.setResposta(resposta);

				FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_INFO, respostaSefaz.getResposta(), "");
			} catch (Exception e) {
				e.printStackTrace();
				FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro ao cancelar a NF-e!", e.getMessage());
			}
		}
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void cartaCorrecaoNfe() {
		if (certificado == null || certificado.getArquivo() == null || certificado.getSenha() == null) {
			FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_INFO, "É Necessário informar os dados do certificado antes de enviar a carta de correção!", null);
		} else {
			try {
				if (cartaCorrecao == null) {
					throw new Exception("É necessário informar o texto da carta de correção.");
				}
				if (cartaCorrecao.trim().equals("")) {
					throw new Exception("É necessário informar o texto da carta de correção.");
				}
				if (getObjeto().getStatusNota().intValue() == 6) {
					throw new Exception("NF-e já cancelada. Carta de Correção não permitido!");
				}
				if (getObjeto().getStatusNota().intValue() != 5) {
					throw new Exception("NF-e não autorizada. Carta de Correção não permitido!");
				}

				ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
				String caminhoArquivo = context.getRealPath(diretorioXml) + System.getProperty("file.separator") + getObjeto().getChaveAcesso() + getObjeto().getDigitoChaveAcesso() + "-nfeproc.xml";
				File arquivoXml = new File(caminhoArquivo);
				if (!arquivoXml.exists()) {
					throw new Exception("Arquivo XML da NF-e não localizado!");
				}

				JAXBContext jc = JAXBContext.newInstance("br.inf.portalfiscal.nfe.procnfe");
				Unmarshaller unmarshaller = jc.createUnmarshaller();

				JAXBElement<TNfeProc> element = (JAXBElement) unmarshaller.unmarshal(arquivoXml);
				String protocolo = element.getValue().getProtNFe().getInfProt().getNProt();

				getKeyStore();

				CartaCorrecaoNfe cartaCorrecaoNfe = new CartaCorrecaoNfe();
				Map dadosCancelamento = cartaCorrecaoNfe.cartaCorrecao(certificado.getAlias(), getKeyStore(), certificado.getSenha(), getObjeto().getUfEmitente().toString(), String.valueOf(getObjeto().getAmbiente()), getObjeto().getChaveAcesso() + getObjeto().getDigitoChaveAcesso(), protocolo, cartaCorrecao.trim(), getObjeto().getEmpresa().getCnpj());

				RespostaSefaz respostaSefaz = new RespostaSefaz();
				respostaSefaz.setCancelado((Boolean) dadosCancelamento.get("nfeCorrigida"));

				String resposta = "";
				if (respostaSefaz.isCancelado()) {
					// salva o xml
					caminhoArquivo = context.getRealPath(diretorioXml) + System.getProperty("file.separator") + getObjeto().getChaveAcesso() + getObjeto().getDigitoChaveAcesso() + "-nfeCorrecao.xml";
					OutputStream out = new FileOutputStream(new File(caminhoArquivo));
					out.write(((String) dadosCancelamento.get("xmlCorrecao")).getBytes());
					out.close();

					getObjeto().setStatusNota(6);
					setObjeto(dao.merge(getObjeto()));

					// atualiza o estoque
					for (NfeDetalhe nfeDetalhe : getObjeto().getListaNfeDetalhe()) {
						controleEstoqueDao.atualizaEstoque(nfeDetalhe.getProduto().getId(), nfeDetalhe.getQuantidadeComercial());
					}

					resposta += "Carta de Correção enviada com sucesso";
				} else {
					resposta += "Carta de Correção NÃO enviada.";
				}
				resposta += "\n" + (String) dadosCancelamento.get("motivo1");
				resposta += "\n" + (String) dadosCancelamento.get("motivo2");

				respostaSefaz.setResposta(resposta);

				FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_INFO, respostaSefaz.getResposta(), "");
			} catch (Exception e) {
				e.printStackTrace();
				FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro ao enviar a carta de correção!", e.getMessage());
			}
		}
	}
	
    public void geraLancamentoReceber() {
        try {
            //Exercicio: Implemente este método de acordo com a necessidade do seu cliente
            //01-Pergunte de ele quer lançar no contas a receber após a emissão da nota
            //01-Deixe uma opção na tela para mandar os dados para o financeiro
            //03-Deixe a opção de capturar esses dados no financeiro apenas
            //04-Realize o lançamento sem que o usuário da NF-e saiba o que está ocorendo e deixe o restante a cargo do profissional do setor financeiro.
            //
            //Corrija as informações estáticas que estão no código.
            FinDocumentoOrigem documentoOrigem = new FinDocumentoOrigem();
            documentoOrigem.setId(32);
            FinStatusParcela statusParcela = new FinStatusParcela();
            statusParcela.setId(1);
            ContaCaixa contaCaixa = new ContaCaixa();
            contaCaixa.setId(1);
            
            FinLancamentoReceber lancamentoReceber = new FinLancamentoReceber();
            lancamentoReceber.setCliente(getObjeto().getCliente());
            lancamentoReceber.setFinDocumentoOrigem(documentoOrigem);
            lancamentoReceber.setQuantidadeParcela(1);
            lancamentoReceber.setValorTotal(getObjeto().getValorTotal());
            lancamentoReceber.setValorAReceber(getObjeto().getValorTotal());
            lancamentoReceber.setDataLancamento(getObjeto().getDataHoraEmissao());
            lancamentoReceber.setNumeroDocumento("NF-e: " + getObjeto().getNumero());
            lancamentoReceber.setPrimeiroVencimento(getObjeto().getDataHoraEmissao());
            lancamentoReceber.setIntervaloEntreParcelas(30);
            lancamentoReceber.setListaFinParcelaReceber(new HashSet<FinParcelaReceber>());
            
            FinParcelaReceber parcelaReceber = new FinParcelaReceber();
            parcelaReceber.setFinLancamentoReceber(lancamentoReceber);
            parcelaReceber.setFinStatusParcela(statusParcela);
            parcelaReceber.setContaCaixa(contaCaixa);
            parcelaReceber.setDataEmissao(getObjeto().getDataHoraEmissao());
            parcelaReceber.setDataVencimento(getObjeto().getDataHoraEmissao());
            parcelaReceber.setValor(getObjeto().getValorTotal());
            
            lancamentoReceber.getListaFinParcelaReceber().add(parcelaReceber);
            
            lancamentoReceberDao.persist(lancamentoReceber);
            
            FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_INFO, "Lançamneto a receber gerado com sucesso.", "");
        } catch (Exception e) {
			e.printStackTrace();
			FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro ao gerar o lançamento a receber!", e.getMessage());
        }
    }
	
	public void carregaDadosCertificado() {
		try {
			if (certificado != null) {
				if (senhaCertificado != null) {
					certificado.setSenha(senhaCertificado.toCharArray());

					FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_INFO, "Dados do certificado carregado com sucesso!", null);
				} else {
					certificado = null;
					FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_ERROR, "Senha do certficado não informada!", null);
				}
			} else {
				FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_ERROR, "Upload do certificado não foi realizado!", null);
			}
		} catch (Exception e) {
			certificado = null;
			e.printStackTrace();
			FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro ao carregar os dados do certificado!", e.getMessage());
		}
	}

	public void uploadCertificado(FileUploadEvent event) {
		try {
			UploadedFile arquivoUpload = event.getFile();
			keyStore = null;
			certificado = new Certificado();
			certificado.setArquivo(IoUtil.getBytes(arquivoUpload.getInputstream()));
			arquivoUpload.getInputstream().close();
		} catch (Exception e) {
			certificado = null;
			FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro ao realizar o upload do arquivo!", e.getMessage());
			// e.printStackTrace();
		}
	}

	private KeyStore getKeyStore() throws Exception {
		if (keyStore == null) {
			keyStore = KeyStore.getInstance("PKCS12");
			keyStore.load(new ByteArrayInputStream(certificado.getArquivo()), certificado.getSenha());
			certificado.setAlias(keyStore.aliases().nextElement());
		}
		return keyStore;
	}

	public boolean verificaAutorizado() {
		return getObjeto().getStatusNota().intValue() == 5;
	}

	public boolean verificaCancelado() {
		return getObjeto().getStatusNota().intValue() == 6;
	}

	private void valoresPadrao() {
		getObjeto().setStatusNota(0);
		getObjeto().setTipoOperacao(1);
		getObjeto().setStatusNota(0);
		getObjeto().setFormatoImpressaoDanfe(1);
		getObjeto().setConsumidorOperacao(1);
		getObjeto().setConsumidorPresenca(1);
		getObjeto().setTipoEmissao(1);
		getObjeto().setFinalidadeEmissao(1);
		getObjeto().setIndicadorFormaPagamento(0);
		getObjeto().setLocalDestino(1);
		getObjeto().getTransporte().setModalidadeFrete(0);

		Date dataAtual = new Date();
		getObjeto().setEmpresa(FacesContextUtil.getEmpresaUsuario());
		getObjeto().setUfEmitente(getObjeto().getEmpresa().getCodigoIbgeUf());
		getObjeto().setDataHoraEmissao(dataAtual);
		getObjeto().setDataHoraEntradaSaida(dataAtual);

		getObjeto().setBaseCalculoIcms(BigDecimal.ZERO);
		getObjeto().setValorIcms(BigDecimal.ZERO);
		getObjeto().setValorTotalProdutos(BigDecimal.ZERO);
		getObjeto().setBaseCalculoIcmsSt(BigDecimal.ZERO);
		getObjeto().setValorIcmsSt(BigDecimal.ZERO);
		getObjeto().setValorIpi(BigDecimal.ZERO);
		getObjeto().setValorPis(BigDecimal.ZERO);
		getObjeto().setValorCofins(BigDecimal.ZERO);
		getObjeto().setValorFrete(BigDecimal.ZERO);
		getObjeto().setValorSeguro(BigDecimal.ZERO);
		getObjeto().setValorDespesasAcessorias(BigDecimal.ZERO);
		getObjeto().setValorDesconto(BigDecimal.ZERO);
		getObjeto().setValorTotal(BigDecimal.ZERO);
		getObjeto().setValorImpostoImportacao(BigDecimal.ZERO);
		getObjeto().setBaseCalculoIssqn(BigDecimal.ZERO);
		getObjeto().setValorIssqn(BigDecimal.ZERO);
		getObjeto().setValorPisIssqn(BigDecimal.ZERO);
		getObjeto().setValorCofinsIssqn(BigDecimal.ZERO);
		getObjeto().setValorServicos(BigDecimal.ZERO);
		getObjeto().setValorRetidoPis(BigDecimal.ZERO);
		getObjeto().setValorRetidoCofins(BigDecimal.ZERO);
		getObjeto().setValorRetidoCsll(BigDecimal.ZERO);
		getObjeto().setBaseCalculoIrrf(BigDecimal.ZERO);
		getObjeto().setValorRetidoIrrf(BigDecimal.ZERO);
		getObjeto().setBaseCalculoPrevidencia(BigDecimal.ZERO);
		getObjeto().setValorRetidoPrevidencia(BigDecimal.ZERO);
		getObjeto().setValorIcmsDesonerado(BigDecimal.ZERO);
	}
	
	public void detalheImposto() {
		setNfeDetalheImpostoIcms(nfeDetalheSelecionado.getNfeDetalheImpostoIcms());
	}

	public void selecionaOperacaoFiscal(SelectEvent event) {
		TributOperacaoFiscal tributOperacaoFiscal = (TributOperacaoFiscal) event.getObject();
		getObjeto().setNaturezaOperacao(tributOperacaoFiscal.getDescricaoNaNf());
		dadosSalvos = false;
	}

	public void selecionaDestinatario(SelectEvent event) {
		pessoaCliente = (PessoaCliente) event.getObject();
		Cliente cliente = new Cliente();
		cliente.setId(pessoaCliente.getId());
		getObjeto().setCliente(cliente);

		getObjeto().getDestinatario().setCpfCnpj(pessoaCliente.getCpfCnpj());
		getObjeto().getDestinatario().setNome(pessoaCliente.getNome());
		getObjeto().getDestinatario().setLogradouro(pessoaCliente.getLogradouro());
		getObjeto().getDestinatario().setComplemento(pessoaCliente.getComplemento());
		getObjeto().getDestinatario().setNumero(pessoaCliente.getNumero());
		getObjeto().getDestinatario().setBairro(pessoaCliente.getBairro());
		getObjeto().getDestinatario().setNomeMunicipio(pessoaCliente.getCidade());
		getObjeto().getDestinatario().setCodigoMunicipio(pessoaCliente.getMunicipioIbge());
		getObjeto().getDestinatario().setUf(pessoaCliente.getUf());
		getObjeto().getDestinatario().setCep(pessoaCliente.getCep());
		getObjeto().getDestinatario().setTelefone(pessoaCliente.getFone());
		getObjeto().getDestinatario().setInscricaoEstadual(pessoaCliente.getRgIe());
		getObjeto().getDestinatario().setEmail(pessoaCliente.getEmail());
		getObjeto().getDestinatario().setCodigoPais(1058);
		getObjeto().getDestinatario().setNomePais("Brazil");

		getObjeto().setCodigoMunicipio(getObjeto().getDestinatario().getCodigoMunicipio());

		pessoaCliente = null;
		dadosSalvos = false;
	}

	public void incluiProduto() {
		if (getObjeto().getTributOperacaoFiscal() == null) {
			podeIncluirProduto = false;
			FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_INFO, "Antes de incluir produtos selecione a Operação Fiscal.", null);
		} else if (getObjeto().getDestinatario().getNome() == null || getObjeto().getDestinatario().getNome().isEmpty()) {
			podeIncluirProduto = false;
			FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_INFO, "Antes de incluir produtos selecione o destinatário.", null);
		} else {
			podeIncluirProduto = true;
			nfeDetalhe = new NfeDetalhe();
			nfeDetalhe.setNfeCabecalho(getObjeto());
		}
	}

	public void alteraProduto() {
		nfeDetalhe = nfeDetalheSelecionado;
	}

	public void salvaProduto() {
		try {
			realizaCalculosItem();
			if (!getObjeto().getListaNfeDetalhe().contains(nfeDetalhe)) {
				getObjeto().getListaNfeDetalhe().add(nfeDetalhe);
				atualizaTotais();
				FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_INFO, "Registro incluído!", null);
			} else {
				atualizaTotais();
				FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_INFO, "Registro alterado!", null);
			}
			dadosSalvos = false;
		} catch (Exception e) {
			e.printStackTrace();
			FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro ao salvar o registro", e.getMessage());
		}

	}

	public void excluirProduto() {
		if (nfeDetalheSelecionado == null) {
			FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_INFO, "Nenhum registro selecionado!", null);
		} else {
			getObjeto().getListaNfeDetalhe().remove(nfeDetalheSelecionado);
			atualizaTotais();
			dadosSalvos = false;
			FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_INFO, "Registro excluído!", null);
		}
	}

	private NfeDetalhe realizaCalculosItem() throws Exception {
		nfeDetalhe.setNumeroItem(++numeroItem);
		nfeDetalhe.setGtin(nfeDetalhe.getProduto().getGtin());
		nfeDetalhe.setNomeProduto(nfeDetalhe.getProduto().getDescricaoPdv());
		nfeDetalhe.setNcm(nfeDetalhe.getProduto().getNcm());
		if (nfeDetalhe.getProduto().getExTipi() != null) {
			nfeDetalhe.setExTipi(Integer.valueOf(nfeDetalhe.getProduto().getExTipi()));
		}
		nfeDetalhe.setUnidadeComercial(nfeDetalhe.getProduto().getUnidadeProduto().getSigla());
		nfeDetalhe.setValorUnitarioComercial(nfeDetalhe.getProduto().getValorVenda());

		nfeDetalhe.setCodigoProduto(nfeDetalhe.getGtin());
		nfeDetalhe.setGtinUnidadeTributavel(nfeDetalhe.getGtin());
		nfeDetalhe.setUnidadeTributavel(nfeDetalhe.getUnidadeComercial());
		nfeDetalhe.setValorUnitarioTributavel(nfeDetalhe.getValorUnitarioComercial());
		nfeDetalhe.setValorBrutoProduto(Biblioteca.multiplica(nfeDetalhe.getValorUnitarioComercial(), nfeDetalhe.getQuantidadeComercial()));
		nfeDetalhe.setQuantidadeTributavel(nfeDetalhe.getQuantidadeComercial());
		nfeDetalhe.setValorSubtotal(nfeDetalhe.getValorBrutoProduto());
		nfeDetalhe.setEntraTotal(1);
		if (nfeDetalhe.getValorFrete() == null) {
			nfeDetalhe.setValorFrete(BigDecimal.ZERO);
		}
		if (nfeDetalhe.getValorOutrasDespesas() == null) {
			nfeDetalhe.setValorOutrasDespesas(BigDecimal.ZERO);
		}
		if (nfeDetalhe.getValorSeguro() == null) {
			nfeDetalhe.setValorSeguro(BigDecimal.ZERO);
		}
		if (nfeDetalhe.getValorDesconto() == null) {
			nfeDetalhe.setValorDesconto(BigDecimal.ZERO);
		}
		nfeDetalhe.setValorTotal(nfeDetalhe.getValorBrutoProduto().subtract(nfeDetalhe.getValorDesconto()));

		defineTributacao();
		return nfeDetalhe;
	}

	private void defineTributacao() throws Exception {
		TributOperacaoFiscal operacaoFiscal = getObjeto().getTributOperacaoFiscal();
		Empresa empresa = FacesContextUtil.getEmpresaUsuario();
		NfeDestinatario destinatario = getObjeto().getDestinatario();
		nfeDetalhe.setNfeDetalheImpostoIssqn(new NfeDetalheImpostoIssqn());
		nfeDetalhe.getNfeDetalheImpostoIssqn().setNfeDetalhe(nfeDetalhe);
		nfeDetalhe.setNfeDetalheImpostoPis(new NfeDetalheImpostoPis());
		nfeDetalhe.getNfeDetalheImpostoPis().setNfeDetalhe(nfeDetalhe);
		nfeDetalhe.setNfeDetalheImpostoCofins(new NfeDetalheImpostoCofins());
		nfeDetalhe.getNfeDetalheImpostoCofins().setNfeDetalhe(nfeDetalhe);
		nfeDetalhe.setNfeDetalheImpostoIcms(new NfeDetalheImpostoIcms());
		nfeDetalhe.getNfeDetalheImpostoIcms().setNfeDetalhe(nfeDetalhe);
		nfeDetalhe.setNfeDetalheImpostoIpi(new NfeDetalheImpostoIpi());
		nfeDetalhe.getNfeDetalheImpostoIpi().setNfeDetalhe(nfeDetalhe);
		nfeDetalhe.setNfeDetalheImpostoIi(new NfeDetalheImpostoIi());
		nfeDetalhe.getNfeDetalheImpostoIi().setNfeDetalhe(nfeDetalhe);
		nfeDetalhe.setListaArmamento(new HashSet<NfeDetEspecificoArmamento>());
		nfeDetalhe.setListaMedicamento(new HashSet<NfeDetEspecificoMedicamento>());
		nfeDetalhe.setListaDeclaracaoImportacao(new HashSet<NfeDeclaracaoImportacao>());

		// Se houver CFOP cadastrado na Operação Fiscal, a nota é de serviços
		if (operacaoFiscal.getCfop() != null) {
			nfeDetalhe.setCfop(operacaoFiscal.getCfop());

			// ISSQN
			TributIss iss = operacaoFiscal.getListaIss().get(0);
			nfeDetalhe.getNfeDetalheImpostoIssqn().setBaseCalculoIssqn(nfeDetalhe.getValorBrutoProduto());
			nfeDetalhe.getNfeDetalheImpostoIssqn().setAliquotaIssqn(iss.getAliquotaPorcento());
			nfeDetalhe.getNfeDetalheImpostoIssqn().setValorIssqn(nfeDetalhe.getNfeDetalheImpostoIssqn().getBaseCalculoIssqn().multiply(iss.getAliquotaPorcento().divide(BigDecimal.valueOf(100), RoundingMode.HALF_DOWN)));
			nfeDetalhe.getNfeDetalheImpostoIssqn().setMunicipioIssqn(empresa.getCodigoIbgeCidade());
			nfeDetalhe.getNfeDetalheImpostoIssqn().setItemListaServicos(iss.getItemListaServico());
			// nfeDetalhe.getNfeDetalheImpostoIssqn().setTributacaoIssqn(iss.getCodigoTributacao());

			// PIS ISSQN
			nfeDetalhe.getNfeDetalheImpostoPis().setAliquotaPisPercentual(BigDecimal.ZERO);
			nfeDetalhe.getNfeDetalheImpostoPis().setAliquotaPisReais(BigDecimal.ZERO);
			nfeDetalhe.getNfeDetalheImpostoPis().setValorBaseCalculoPis(BigDecimal.ZERO);
			nfeDetalhe.getNfeDetalheImpostoPis().setValorPis(BigDecimal.ZERO);

			// COFINS ISSQN
			nfeDetalhe.getNfeDetalheImpostoCofins().setAliquotaCofinsPercentual(BigDecimal.ZERO);
			nfeDetalhe.getNfeDetalheImpostoCofins().setAliquotaCofinsReais(BigDecimal.ZERO);
			nfeDetalhe.getNfeDetalheImpostoCofins().setBaseCalculoCofins(BigDecimal.ZERO);
			nfeDetalhe.getNfeDetalheImpostoCofins().setValorCofins(BigDecimal.ZERO);
		} else {
			// ICMS
			// Se o Produto estiver vinculado a uma configuração de Operação
			// Fiscal + Grupo Tributário, carrega esses dados
			if (nfeDetalhe.getProduto().getTributGrupoTributario() != null) {
				List<Filtro> listaFiltro = new ArrayList<>();

				listaFiltro.add(new Filtro("AND", "idTributOperacaoFiscal", "=", operacaoFiscal.getId()));
				listaFiltro.add(new Filtro("AND", "idTributGrupoTributario", "=", nfeDetalhe.getProduto().getTributGrupoTributario().getId()));
				listaFiltro.add(new Filtro("AND", "ufDestino", "=", destinatario.getUf()));

				ViewTributacaoIcms icms = icmsDao.getBean(ViewTributacaoIcms.class, listaFiltro);
				if (icms != null) {
					nfeDetalhe.setCfop(icms.getCfop());
					nfeDetalhe.getNfeDetalheImpostoIcms().setOrigemMercadoria(Integer.valueOf(icms.getOrigemMercadoria()));
					nfeDetalhe.getNfeDetalheImpostoIcms().setCstIcms(icms.getCstB());
					nfeDetalhe.getNfeDetalheImpostoIcms().setCsosn(icms.getCsosnB());
					nfeDetalhe.getNfeDetalheImpostoIcms().setModalidadeBcIcms(Integer.valueOf(icms.getModalidadeBc()));
					nfeDetalhe.getNfeDetalheImpostoIcms().setTaxaReducaoBcIcms(icms.getPorcentoBc());
					nfeDetalhe.getNfeDetalheImpostoIcms().setAliquotaIcms(icms.getAliquota());
					nfeDetalhe.getNfeDetalheImpostoIcms().setModalidadeBcIcmsSt(Integer.valueOf(icms.getModalidadeBcSt()));
					nfeDetalhe.getNfeDetalheImpostoIcms().setPercentualMvaIcmsSt(icms.getMva());
					nfeDetalhe.getNfeDetalheImpostoIcms().setPercentualReducaoBcIcmsSt(icms.getPorcentoBcSt());
					nfeDetalhe.getNfeDetalheImpostoIcms().setAliquotaIcmsSt(icms.getAliquotaIcmsSt());
					nfeDetalhe.getNfeDetalheImpostoIcms().setAliquotaCreditoIcmsSn(BigDecimal.ZERO);
				} else {
					throw new Exception("Não existe tributação de ICMS definida para os parâmetros informados. Operação não realizada.");
				}

				// IPI
				listaFiltro.clear();
				listaFiltro.add(new Filtro("AND", "idTributOperacaoFiscal", "=", operacaoFiscal.getId()));
				listaFiltro.add(new Filtro("AND", "idTributGrupoTributario", "=", nfeDetalhe.getProduto().getTributGrupoTributario().getId()));

				ViewTributacaoIpi ipi = ipiDao.getBean(ViewTributacaoIpi.class, listaFiltro);
				if (ipi != null) {
					nfeDetalhe.getNfeDetalheImpostoIpi().setCstIpi(ipi.getCstIpi());
					nfeDetalhe.getNfeDetalheImpostoIpi().setAliquotaIpi(ipi.getAliquotaPorcento());
				}

				// PIS ICMS
				listaFiltro.clear();
				listaFiltro.add(new Filtro("AND", "idTributOperacaoFiscal", "=", operacaoFiscal.getId()));
				listaFiltro.add(new Filtro("AND", "idTributGrupoTributario", "=", nfeDetalhe.getProduto().getTributGrupoTributario().getId()));

				ViewTributacaoPis pis = pisDao.getBean(ViewTributacaoPis.class, listaFiltro);
				if (pis != null) {
					nfeDetalhe.getNfeDetalheImpostoPis().setCstPis(pis.getCstPis());
					nfeDetalhe.getNfeDetalheImpostoPis().setAliquotaPisPercentual(pis.getAliquotaPorcento());
					nfeDetalhe.getNfeDetalheImpostoPis().setAliquotaPisReais(pis.getAliquotaUnidade());
				}

				// COFINS ICMS
				listaFiltro.clear();
				listaFiltro.add(new Filtro("AND", "idTributOperacaoFiscal", "=", operacaoFiscal.getId()));
				listaFiltro.add(new Filtro("AND", "idTributGrupoTributario", "=", nfeDetalhe.getProduto().getTributGrupoTributario().getId()));

				ViewTributacaoCofins cofins = cofinsDao.getBean(ViewTributacaoCofins.class, listaFiltro);
				if (cofins != null) {
					nfeDetalhe.getNfeDetalheImpostoCofins().setNfeDetalhe(nfeDetalhe);
					nfeDetalhe.getNfeDetalheImpostoCofins().setCstCofins(cofins.getCstCofins());
					nfeDetalhe.getNfeDetalheImpostoCofins().setAliquotaCofinsPercentual(cofins.getAliquotaPorcento());
					nfeDetalhe.getNfeDetalheImpostoCofins().setAliquotaCofinsReais(cofins.getAliquotaUnidade());
				}
			} else if (nfeDetalhe.getProduto().getTributIcmsCustomCab() != null) {
				// Senão pega do ICMS Customizado
				List<Filtro> listaFiltro = new ArrayList<>();

				listaFiltro.add(new Filtro("AND", "id", "=", nfeDetalhe.getProduto().getTributIcmsCustomCab().getId()));
				listaFiltro.add(new Filtro("AND", "ufDestino", "=", destinatario.getUf()));

				ViewTributacaoIcmsCustom icms = icmsCustomDao.getBean(ViewTributacaoIcmsCustom.class, listaFiltro);
				if (icms != null) {
					nfeDetalhe.getNfeDetalheImpostoIcms().setNfeDetalhe(nfeDetalhe);
					nfeDetalhe.setCfop(icms.getCfop());
					nfeDetalhe.getNfeDetalheImpostoIcms().setOrigemMercadoria(Integer.valueOf(icms.getOrigemMercadoria()));
					nfeDetalhe.getNfeDetalheImpostoIcms().setCstIcms(icms.getCstB());
					nfeDetalhe.getNfeDetalheImpostoIcms().setCsosn(icms.getCsosnB());
					nfeDetalhe.getNfeDetalheImpostoIcms().setModalidadeBcIcms(Integer.valueOf(icms.getModalidadeBc()));
					nfeDetalhe.getNfeDetalheImpostoIcms().setTaxaReducaoBcIcms(icms.getPorcentoBc());
					nfeDetalhe.getNfeDetalheImpostoIcms().setAliquotaIcms(icms.getAliquota());
					nfeDetalhe.getNfeDetalheImpostoIcms().setModalidadeBcIcmsSt(Integer.valueOf(icms.getModalidadeBcSt()));
					nfeDetalhe.getNfeDetalheImpostoIcms().setPercentualMvaIcmsSt(icms.getMva());
					nfeDetalhe.getNfeDetalheImpostoIcms().setPercentualReducaoBcIcmsSt(icms.getPorcentoBcSt());
					nfeDetalhe.getNfeDetalheImpostoIcms().setAliquotaIcmsSt(icms.getAliquotaIcmsSt());
					nfeDetalhe.getNfeDetalheImpostoIcms().setAliquotaCreditoIcmsSn(BigDecimal.ZERO);
				} else {
					throw new Exception("Não existe tributação de ICMS definida para os parâmetros informados. Operação não realizada.");
				}
			}

			NfeCalculo calculo = NfeCalculoController.calculo(nfeDetalhe, empresa, destinatario);

			// Valores ICMS
			nfeDetalhe.getNfeDetalheImpostoIcms().setBaseCalculoIcms(calculo.getBaseCalculoIcms());
			nfeDetalhe.getNfeDetalheImpostoIcms().setPercentualReducaoBcIcmsSt(calculo.getReducaoBcIcmsSt());
			nfeDetalhe.getNfeDetalheImpostoIcms().setValorIcms(calculo.getValorIcms());
			// valores de icms st
			nfeDetalhe.getNfeDetalheImpostoIcms().setValorBaseCalculoIcmsSt(calculo.getBaseCalculoIcmsSt());
			nfeDetalhe.getNfeDetalheImpostoIcms().setValorIcmsSt(calculo.getValorIcmsSt());
			// credito de icmssn
			nfeDetalhe.getNfeDetalheImpostoIcms().setValorCreditoIcmsSn(calculo.getValorCreditoIcmsSn());

			// Valores IPI
			nfeDetalhe.getNfeDetalheImpostoIpi().setValorBaseCalculoIpi(calculo.getBaseCalculoIpi());
			nfeDetalhe.getNfeDetalheImpostoIpi().setValorIpi(calculo.getValorIpi());

			// Valores PIS
			nfeDetalhe.getNfeDetalheImpostoPis().setValorBaseCalculoPis(calculo.getBaseCalculoPis());
			nfeDetalhe.getNfeDetalheImpostoPis().setValorPis(calculo.getValorPis());

			// Valores COFINS
			nfeDetalhe.getNfeDetalheImpostoCofins().setBaseCalculoCofins(calculo.getBaseCalculoCofins());
			nfeDetalhe.getNfeDetalheImpostoCofins().setValorCofins(calculo.getValorCofins());
		}
	}

	private void atualizaTotais() {
		BigDecimal totalProdutos = BigDecimal.ZERO;
		BigDecimal valorFrete = BigDecimal.ZERO;
		BigDecimal valorSeguro = BigDecimal.ZERO;
		BigDecimal valorOutrasDespesas = BigDecimal.ZERO;
		BigDecimal desconto = BigDecimal.ZERO;
		BigDecimal baseCalculoIcms = BigDecimal.ZERO;
		BigDecimal valorIcms = BigDecimal.ZERO;
		BigDecimal baseCalculoIcmsSt = BigDecimal.ZERO;
		BigDecimal valorIcmsSt = BigDecimal.ZERO;
		BigDecimal valorIpi = BigDecimal.ZERO;
		BigDecimal valorPis = BigDecimal.ZERO;
		BigDecimal valorCofins = BigDecimal.ZERO;
		BigDecimal valorNotaFiscal = BigDecimal.ZERO;

		BigDecimal totalServicos = BigDecimal.ZERO;
		BigDecimal baseCalculoIssqn = BigDecimal.ZERO;
		BigDecimal valorIssqn = BigDecimal.ZERO;
		BigDecimal valorPisIssqn = BigDecimal.ZERO;
		BigDecimal valorCofinsIssqn = BigDecimal.ZERO;

		// Se houver CFOP cadastrado na Operação Fiscal, a nota é de serviços
		if (getObjeto().getTributOperacaoFiscal().getCfop() != null) {
			for (NfeDetalhe itensNfe : getObjeto().getListaNfeDetalhe()) {
				totalServicos = totalServicos.add(itensNfe.getValorTotal());
				valorFrete = valorFrete.add(itensNfe.getValorFrete());
				valorSeguro = valorSeguro.add(itensNfe.getValorSeguro());
				valorOutrasDespesas = valorOutrasDespesas.add(itensNfe.getValorOutrasDespesas());
				desconto = desconto.add(itensNfe.getValorDesconto());
				baseCalculoIssqn = baseCalculoIssqn.add(itensNfe.getNfeDetalheImpostoIssqn().getBaseCalculoIssqn());
				valorIssqn = valorIssqn.add(itensNfe.getNfeDetalheImpostoIssqn().getValorIssqn());
				valorPisIssqn = valorPisIssqn.add(itensNfe.getNfeDetalheImpostoPis().getValorPis());
				valorCofinsIssqn = valorCofinsIssqn.add(itensNfe.getNfeDetalheImpostoCofins().getValorCofins());
			}
			// valorNotaFiscal =
			// totalServicos.add(valorPis).add(valorCofins).add(valorOutrasDespesas).subtract(desconto);
			valorNotaFiscal = totalServicos.add(valorOutrasDespesas).subtract(desconto);
		} else {
			for (NfeDetalhe itensNfe : getObjeto().getListaNfeDetalhe()) {
				totalProdutos = totalProdutos.add(itensNfe.getValorTotal());
				valorFrete = valorFrete.add(itensNfe.getValorFrete());
				valorSeguro = valorSeguro.add(itensNfe.getValorSeguro());
				valorOutrasDespesas = valorOutrasDespesas.add(itensNfe.getValorOutrasDespesas());
				desconto = desconto.add(itensNfe.getValorDesconto());
				if (itensNfe.getNfeDetalheImpostoIcms().getBaseCalculoIcms() != null) {
					baseCalculoIcms = baseCalculoIcms.add(itensNfe.getNfeDetalheImpostoIcms().getBaseCalculoIcms());
				}
				if (itensNfe.getNfeDetalheImpostoIcms().getValorIcms() != null) {
					valorIcms = valorIcms.add(itensNfe.getNfeDetalheImpostoIcms().getValorIcms());
				}
				if (itensNfe.getNfeDetalheImpostoIcms().getValorBaseCalculoIcmsSt() != null) {
					baseCalculoIcmsSt = baseCalculoIcmsSt.add(itensNfe.getNfeDetalheImpostoIcms().getValorBaseCalculoIcmsSt());
				}
				if (itensNfe.getNfeDetalheImpostoIcms().getValorIcmsSt() != null) {
					valorIcmsSt = valorIcmsSt.add(itensNfe.getNfeDetalheImpostoIcms().getValorIcmsSt());
				}
				if (itensNfe.getNfeDetalheImpostoIpi().getValorIpi() != null) {
					valorIpi = valorIpi.add(itensNfe.getNfeDetalheImpostoIpi().getValorIpi());
				}
				if (itensNfe.getNfeDetalheImpostoPis().getValorPis() != null) {
					valorPis = valorPis.add(itensNfe.getNfeDetalheImpostoPis().getValorPis());
				}
				if (itensNfe.getNfeDetalheImpostoCofins().getValorCofins() != null) {
					valorCofins = valorCofins.add(itensNfe.getNfeDetalheImpostoCofins().getValorCofins());
				}
			}
			// valorNotaFiscal =
			// totalProdutos.add(valorIcmsSt).add(valorPis).add(valorCofins).add(valorIpi).add(valorOutrasDespesas).subtract(desconto);
			valorNotaFiscal = totalProdutos.add(valorIpi).add(valorOutrasDespesas).subtract(desconto);
		}

		getObjeto().setValorFrete(valorFrete);
		getObjeto().setValorDespesasAcessorias(valorOutrasDespesas);
		getObjeto().setValorSeguro(valorSeguro);
		getObjeto().setValorDesconto(desconto);

		getObjeto().setValorServicos(totalServicos);
		getObjeto().setBaseCalculoIssqn(baseCalculoIssqn);
		getObjeto().setValorIssqn(valorIssqn);
		getObjeto().setValorPisIssqn(valorPisIssqn);
		getObjeto().setValorCofinsIssqn(valorCofinsIssqn);

		getObjeto().setValorTotalProdutos(totalProdutos);
		getObjeto().setBaseCalculoIcms(baseCalculoIcms);
		getObjeto().setValorIcms(valorIcms);
		getObjeto().setBaseCalculoIcmsSt(baseCalculoIcmsSt);
		getObjeto().setValorIcmsSt(valorIcmsSt);
		getObjeto().setValorIpi(valorIpi);
		getObjeto().setValorPis(valorPis);
		getObjeto().setValorCofins(valorCofins);

		getObjeto().setValorTotal(valorNotaFiscal);
	}

	private void numeroNfe() throws Exception {
		DecimalFormat formatoNumero = new DecimalFormat("000000000");
		DecimalFormat formatoCodigoNumerico = new DecimalFormat("00000000");
		SimpleDateFormat formatoAno = new SimpleDateFormat("yy");
		SimpleDateFormat formatoMes = new SimpleDateFormat("MM");

		NfeNumero numero = nfeNumeroDao.getBean(NfeNumero.class, new ArrayList<Filtro>());
		if (numero == null) {
			numero = new NfeNumero();
			numero.setEmpresa(getObjeto().getEmpresa());
			numero.setSerie("001");
			numero.setNumero(1);
		} else {
			numero.setNumero(numero.getNumero() + 1);
		}
		nfeNumeroDao.merge(numero);

		getObjeto().setNumero(formatoNumero.format(numero.getNumero()));
		getObjeto().setCodigoNumerico(formatoCodigoNumerico.format(numero.getNumero()));
		getObjeto().setSerie(numero.getSerie());

		getObjeto().setChaveAcesso("" + getObjeto().getEmpresa().getCodigoIbgeUf() + formatoAno.format(getObjeto().getDataHoraEmissao()) + formatoMes.format(getObjeto().getDataHoraEmissao()) + getObjeto().getEmpresa().getCnpj() + getObjeto().getCodigoModelo() + getObjeto().getSerie() + getObjeto().getNumero() + "1" + getObjeto().getCodigoNumerico());
		getObjeto().setDigitoChaveAcesso(Biblioteca.modulo11(getObjeto().getChaveAcesso()).toString());
	}

	public List<TributOperacaoFiscal> getListaTributOperacaoFiscal(String descricao) {
		List<TributOperacaoFiscal> listaTributOperacaoFiscal = new ArrayList<>();
		try {
			listaTributOperacaoFiscal = operacaoFiscalDao.getBeansLike(TributOperacaoFiscal.class, "descricao", descricao);
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return listaTributOperacaoFiscal;
	}

	public List<Produto> getListaProduto(String descricao) {
		List<Produto> listaProduto = new ArrayList<>();
		try {
			listaProduto = produtoDao.getBeansLike(Produto.class, "descricaoPdv", descricao);
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return listaProduto;
	}

	public List<PessoaCliente> getListaPessoaCliente(String nome) {
		List<PessoaCliente> listaPessoaCliente = new ArrayList<>();
		try {
			listaPessoaCliente = pessoaClienteDao.getBeansLike(PessoaCliente.class, "nome", nome);
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return listaPessoaCliente;
	}

	public List<Transportadora> getListaTransportadora(String nome) {
		List<Transportadora> listaTransportadora = new ArrayList<>();
		try {
			listaTransportadora = transportadoraDao.getBeansLike(Transportadora.class, "pessoa.nome", nome);
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return listaTransportadora;
	}

	public PessoaCliente getPessoaCliente() {
		return pessoaCliente;
	}

	public void setPessoaCliente(PessoaCliente pessoaCliente) {
		this.pessoaCliente = pessoaCliente;
	}

	public NfeDetalhe getNfeDetalhe() {
		return nfeDetalhe;
	}

	public void setNfeDetalhe(NfeDetalhe nfeDetalhe) {
		this.nfeDetalhe = nfeDetalhe;
	}

	public NfeDetalhe getNfeDetalheSelecionado() {
		return nfeDetalheSelecionado;
	}

	public void setNfeDetalheSelecionado(NfeDetalhe nfeDetalheSelecionado) {
		this.nfeDetalheSelecionado = nfeDetalheSelecionado;
	}

	public boolean isPodeIncluirProduto() {
		return podeIncluirProduto;
	}

	public void setPodeIncluirProduto(boolean podeIncluirProduto) {
		this.podeIncluirProduto = podeIncluirProduto;
	}

	public String getSenhaCertificado() {
		return senhaCertificado;
	}

	public void setSenhaCertificado(String senhaCertificado) {
		this.senhaCertificado = senhaCertificado;
	}

	public String getJustificativaCancelamento() {
		return justificativaCancelamento;
	}

	public void setJustificativaCancelamento(String justificativaCancelamento) {
		this.justificativaCancelamento = justificativaCancelamento;
	}

	public NfeTransporteReboque getNfeTransporteReboque() {
		return nfeTransporteReboque;
	}

	public void setNfeTransporteReboque(NfeTransporteReboque nfeTransporteReboque) {
		this.nfeTransporteReboque = nfeTransporteReboque;
	}

	public NfeTransporteReboque getNfeTransporteReboqueSelecionado() {
		return nfeTransporteReboqueSelecionado;
	}

	public void setNfeTransporteReboqueSelecionado(NfeTransporteReboque nfeTransporteReboqueSelecionado) {
		this.nfeTransporteReboqueSelecionado = nfeTransporteReboqueSelecionado;
	}

	public NfeDetalheImpostoIcms getNfeDetalheImpostoIcms() {
		return nfeDetalheImpostoIcms;
	}

	public void setNfeDetalheImpostoIcms(NfeDetalheImpostoIcms nfeDetalheImpostoIcms) {
		this.nfeDetalheImpostoIcms = nfeDetalheImpostoIcms;
	}

	public String getCartaCorrecao() {
		return cartaCorrecao;
	}

	public void setCartaCorrecao(String cartaCorrecao) {
		this.cartaCorrecao = cartaCorrecao;
	}

	public boolean isRequerJustificativaDanfe() {
		return requerJustificativaDanfe;
	}

	public void setRequerJustificativaDanfe(boolean requerJustificativaDanfe) {
		this.requerJustificativaDanfe = requerJustificativaDanfe;
	}

	public String getJustificativaDanfe() {
		return justificativaDanfe;
	}

	public void setJustificativaDanfe(String justificativaDanfe) {
		this.justificativaDanfe = justificativaDanfe;
	}

}
