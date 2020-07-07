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
package com.t2tierp.controller.nfse;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyStore;
import java.text.DecimalFormat;
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
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.swing.ImageIcon;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import com.t2tierp.controller.AbstractController;
import com.t2tierp.model.bean.cadastros.Cliente;
import com.t2tierp.model.bean.cadastros.Empresa;
import com.t2tierp.model.bean.cadastros.Pessoa;
import com.t2tierp.model.bean.nfe.NfeConfiguracao;
import com.t2tierp.model.bean.nfse.NfseCabecalho;
import com.t2tierp.model.bean.nfse.NfseDetalhe;
import com.t2tierp.model.bean.nfse.NfseIntermediario;
import com.t2tierp.model.bean.nfse.NfseListaServico;
import com.t2tierp.model.bean.ordemservico.OsAbertura;
import com.t2tierp.model.bean.ordemservico.OsProdutoServico;
import com.t2tierp.model.bean.tributacao.TributIss;
import com.t2tierp.model.dao.Filtro;
import com.t2tierp.model.dao.InterfaceDAO;
import com.t2tierp.util.Biblioteca;
import com.t2tierp.util.FacesContextUtil;

@ManagedBean
@ViewScoped
public class NfseEmissaoController extends AbstractController<NfseCabecalho> implements Serializable {

	private static final long serialVersionUID = 1L;

	@EJB
	private InterfaceDAO<OsAbertura> osAberturaDao;
	@EJB
	private InterfaceDAO<Cliente> clienteDao;
	@EJB
	private InterfaceDAO<Pessoa> pessoaDao;
	@EJB
	private InterfaceDAO<TributIss> tributIssDao;
	@EJB
	private InterfaceDAO<NfseListaServico> listaServicoDao;
	@EJB
	private InterfaceDAO<NfeConfiguracao> nfeConfiguracaoDao;
	

	private NfseDetalhe nfseDetalhe;
	private NfseDetalhe nfseDetalheSelecionado;

	private NfseIntermediario nfseIntermediario;
	private NfseIntermediario nfseIntermediarioSelecionado;

	@Override
	public Class<NfseCabecalho> getClazz() {
		return NfseCabecalho.class;
	}

	@Override
	public String getFuncaoBase() {
		return "NFSE_EMISSAO";
	}

	@Override
	public void incluir() {
		super.incluir();

		getObjeto().setListaNfseDetalhe(new HashSet<>());
		getObjeto().setListaNfseIntermediario(new HashSet<>());
	}

	@Override
	public void salvar() {
		try {
			if (getObjeto().getId() == null) {
				Date dataAtual = new Date();

				calculaItensNfse();
				
				dao.persist(getObjeto());
				
				getObjeto().setNumeroRps(new DecimalFormat("0000000000").format(getObjeto().getId()));
				getObjeto().setSerieRps("T2Ti");
				getObjeto().setDataEmissaoRps(dataAtual);
				
				dao.merge(getObjeto());
			} else {
				dao.merge(getObjeto());
			}
			FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_INFO, "Registro salvo com sucesso.", "");
		} catch (Exception e) {
			e.printStackTrace();
			FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro!", e.getMessage());
		}
	}

	private void calculaItensNfse() throws Exception {
		Empresa empresa = FacesContextUtil.getEmpresaUsuario();

		getObjeto().setOsAbertura(osAberturaDao.getBeanJoinFetch(getObjeto().getOsAbertura().getId(), OsAbertura.class));
		getObjeto().setCliente(getObjeto().getOsAbertura().getCliente());
		getObjeto().setEmpresa(empresa);
		
		for (OsProdutoServico s : getObjeto().getOsAbertura().getListaOsProdutoServico()) {
			if (getObjeto().getCliente().getTributOperacaoFiscal() == null) {
				throw new Exception("Operação Fiscal não definida para o cliente.");
			}

			List<Filtro> filtros = new ArrayList<>();
			filtros.add(new Filtro(Filtro.AND, "tributOperacaoFiscal", Filtro.IGUAL, getObjeto().getCliente().getTributOperacaoFiscal()));
			TributIss iss = tributIssDao.getBean(TributIss.class, filtros);

			if (iss == null) {
				throw new Exception("Configuração ISS não localizada.");
			}

			NfseListaServico nfseListaServico = listaServicoDao.getBean(iss.getItemListaServico(), NfseListaServico.class);

			NfseDetalhe nfseDetalhe = new NfseDetalhe();
			nfseDetalhe.setNfseCabecalho(getObjeto());
			nfseDetalhe.setDiscriminacao(s.getProduto().getDescricao());
			nfseDetalhe.setNfseListaServico(nfseListaServico);
			//nfseDetalhe.setCodigoCnae();
			nfseDetalhe.setCodigoTributacaoMunicipio("010100188");
			nfseDetalhe.setIssRetido("N");
			nfseDetalhe.setMunicipioPrestacao(empresa.getCodigoIbgeCidade());
			nfseDetalhe.setOutrasRetencoes(BigDecimal.ZERO);
			nfseDetalhe.setValorCofins(BigDecimal.ZERO);
			nfseDetalhe.setValorCredito(BigDecimal.ZERO);
			nfseDetalhe.setValorCsll(BigDecimal.ZERO);
			nfseDetalhe.setValorDeducoes(BigDecimal.ZERO);
			nfseDetalhe.setValorDescontoCondicionado(BigDecimal.ZERO);
			nfseDetalhe.setValorInss(BigDecimal.ZERO);
			nfseDetalhe.setValorIr(BigDecimal.ZERO);
			nfseDetalhe.setValorIssRetido(BigDecimal.ZERO);
			nfseDetalhe.setValorLiquido(BigDecimal.ZERO);
			nfseDetalhe.setValorPis(BigDecimal.ZERO);

			nfseDetalhe.setValorBaseCalculo(s.getValorTotal());
			nfseDetalhe.setValorDescontoIncondicionado(s.getValorDesconto());
			nfseDetalhe.setValorServicos(s.getValorTotal());
			nfseDetalhe.setValorIss(Biblioteca.multiplica(nfseDetalhe.getValorServicos(), iss.getAliquotaPorcento()));

			getObjeto().getListaNfseDetalhe().add(nfseDetalhe);
		}
		//(ValorServicos - ValorPIS - ValorCOFINS - ValorINSS - ValorIR - ValorCSLL - OutrasRetençoes - ValorISSRetido -  DescontoIncondicionado - DescontoCondicionado)
	}

	@SuppressWarnings("rawtypes")
	public void enviaNfse() {
		try {
			NfeConfiguracao nfeConfiguracao = nfeConfiguracaoDao.getBean(1, NfeConfiguracao.class);

            if (nfeConfiguracao == null) {
                throw new Exception("Configuração NF-e não definida.");
            }

            KeyStore ks = KeyStore.getInstance("PKCS12");
            char[] senha = nfeConfiguracao.getCertificadoDigitalSenha().toCharArray();
            ks.load(new FileInputStream(new File(nfeConfiguracao.getCertificadoDigitalCaminho())), senha);
            String alias = ks.aliases().nextElement();

            getObjeto().getCliente().setPessoa(pessoaDao.getBeanJoinFetch(getObjeto().getCliente().getPessoa().getId(), Pessoa.class));
            
            GeraXmlRPS geraXmlRps = new GeraXmlRPS();
            String xml = geraXmlRps.geraXml(getObjeto(), alias, ks, senha);

            ServletContext context = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
            
            try {
                ValidaXmlNfse validaXmlNfse = new ValidaXmlNfse();
                validaXmlNfse.validaXmlEnvio(xml, context);
            } catch (Exception e) {
                throw e;
            }

            EnviaRPS enviaRps = new EnviaRPS();
            Map map = enviaRps.enviaRPS(xml, alias, ks, senha);

            String resposta = (String) map.get("mensagem");

            String protocolo = (String) map.get("protocolo");
            if (!protocolo.isEmpty()) {
            	String pastaXml = context.getRealPath("/modulos/nfse/xml") + System.getProperty("file.separator");
            	
            	Files.write(Paths.get(new File(pastaXml + getObjeto().getNumeroRps() + ".protocolo").toURI()), protocolo.getBytes("UTF-8"));

                Thread.sleep(5000);
                resposta += consultaEnvioNfse(ks, senha, alias, context);
            }
            
			FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_INFO, resposta, "");
		} catch (Exception e) {
			e.printStackTrace();
			FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro!", e.getMessage());
		}
	}

	public void consultaEnvioNfse() {
		try {
			NfeConfiguracao nfeConfiguracao = nfeConfiguracaoDao.getBean(1, NfeConfiguracao.class);

            if (nfeConfiguracao == null) {
                throw new Exception("Configuração NF-e não definida.");
            }
            
            ServletContext context = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();

            KeyStore ks = KeyStore.getInstance("PKCS12");
            char[] senha = nfeConfiguracao.getCertificadoDigitalSenha().toCharArray();
            ks.load(new FileInputStream(new File(nfeConfiguracao.getCertificadoDigitalCaminho())), senha);
            String alias = ks.aliases().nextElement();

            String resposta = consultaEnvioNfse(ks, senha, alias, context);

			FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_INFO, resposta, "");
		} catch (Exception e) {
			e.printStackTrace();
			FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro!", e.getMessage());
		}
	}
	
    @SuppressWarnings("rawtypes")
	private String consultaEnvioNfse(KeyStore ks, char[] senha, String alias, ServletContext context) throws Exception {
        String pastaXml = context.getRealPath("/modulos/nfse/xml") + System.getProperty("file.separator");

        String protocolo = new String(Files.readAllBytes(Paths.get(new File(pastaXml + getObjeto().getNumeroRps() + ".protocolo").toURI())), "UTF-8");

        EnviaRPS enviaRps = new EnviaRPS();
        Map map = enviaRps.consultaEnvioRPS(protocolo, getObjeto().getEmpresa().getCnpj(), getObjeto().getEmpresa().getInscricaoMunicipal(), alias, ks, senha);

        String resposta = (String) map.get("msgRetorno");
        resposta += "\nProtocolo: " + protocolo;

        String nfse = (String) map.get("nfse");
        if (!nfse.isEmpty()) {
            Files.write(Paths.get(new File(pastaXml + getObjeto().getNumeroRps() + ".xml").toURI()), nfse.getBytes("UTF-8"));

            getObjeto().setNumero(((BigInteger) map.get("numero")).toString());
            getObjeto().setCodigoVerificacao((String) map.get("codigoVerificacao"));
            getObjeto().setDataHoraEmissao((Date) map.get("dataEmissao"));

            dao.merge(getObjeto());
        }
        
        return resposta;
    }
	
    @SuppressWarnings({ "rawtypes", "unchecked" })
    public void imprimirNfse() {
        try {
            Map map = new HashMap();
            map.put("NUMERO_NOTA", getObjeto().getNumero());
            map.put("DATA_EMISSAO", getObjeto().getDataHoraEmissao());
            map.put("COMPETENCIA", getObjeto().getCompetencia().isEmpty() ? null : getObjeto().getCompetencia());
            map.put("CODIGO_VERIFICACAO", getObjeto().getCodigoVerificacao());
            map.put("PRESTADOR_RAZAO_SOCIAL", getObjeto().getEmpresa().getRazaoSocial());
            map.put("PRESTADOR_CNPJ", getObjeto().getEmpresa().getCnpj());
            map.put("TOMADOR_RAZAO_SOCIAL", getObjeto().getCliente().getPessoa().getNome());
            if (getObjeto().getCliente().getPessoa().getPessoaFisica() != null) {
                map.put("TOMADOR_CPF_CNPJ", getObjeto().getCliente().getPessoa().getPessoaFisica().getCpf());
            }
            if (getObjeto().getCliente().getPessoa().getPessoaJuridica() != null) {
                map.put("TOMADOR_CPF_CNPJ", getObjeto().getCliente().getPessoa().getPessoaJuridica().getCnpj());
            }
            map.put("LOGO_EMPRESA", new ImageIcon(this.getClass().getResource("/images/logo_t2ti.png")).getImage());
            
            JRBeanCollectionDataSource jrbean = new JRBeanCollectionDataSource(getObjeto().getListaNfseDetalhe());
            JasperPrint jp = JasperFillManager.fillReport(this.getClass().getResourceAsStream("/relatorios/nfse.jasper"), map, jrbean);

            File arquivoPdf = File.createTempFile("nfse", ".pdf");
            
			OutputStream outPdf = new FileOutputStream(arquivoPdf);
			outPdf.write(JasperExportManager.exportReportToPdf(jp));
			outPdf.close();

			FacesContextUtil.downloadArquivo(arquivoPdf, "nfse.pdf");
        } catch (Exception e) {
			e.printStackTrace();
			FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro!", e.getMessage());
        }
    }
    
	public void incluirNfseDetalhe() {
		nfseDetalhe = new NfseDetalhe();
		nfseDetalhe.setNfseCabecalho(getObjeto());
	}

	public void alterarNfseDetalhe() {
		nfseDetalhe = nfseDetalheSelecionado;
	}

	public void salvarNfseDetalhe() {
		if (nfseDetalhe.getId() == null) {
			getObjeto().getListaNfseDetalhe().add(nfseDetalhe);
		}
		salvar("Registro salvo com sucesso!");
	}

	public void excluirNfseDetalhe() {
		if (nfseDetalheSelecionado == null || nfseDetalheSelecionado.getId() == null) {
			FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_INFO, "Nenhum registro selecionado!", null);
		} else {
			getObjeto().getListaNfseDetalhe().remove(nfseDetalheSelecionado);
			salvar("Registro excluído com sucesso!");
		}
	}

	public void incluirNfseIntermediario() {
		nfseIntermediario = new NfseIntermediario();
		nfseIntermediario.setNfseCabecalho(getObjeto());
	}

	public void alterarNfseIntermediario() {
		nfseIntermediario = nfseIntermediarioSelecionado;
	}

	public void salvarNfseIntermediario() {
		if (nfseIntermediario.getId() == null) {
			getObjeto().getListaNfseIntermediario().add(nfseIntermediario);
		}
		salvar("Registro salvo com sucesso!");
	}

	public void excluirNfseIntermediario() {
		if (nfseIntermediarioSelecionado == null || nfseIntermediarioSelecionado.getId() == null) {
			FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_INFO, "Nenhum registro selecionado!", null);
		} else {
			getObjeto().getListaNfseIntermediario().remove(nfseIntermediarioSelecionado);
			salvar("Registro excluído com sucesso!");
		}
	}

	public List<OsAbertura> getListaOsAbertura(String nome) {
		List<OsAbertura> listaOsAbertura = new ArrayList<>();
		try {
			listaOsAbertura = osAberturaDao.getBeansLike(OsAbertura.class, "numero", nome);
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return listaOsAbertura;
	}

	public List<Cliente> getListaCliente(String nome) {
		List<Cliente> listaCliente = new ArrayList<>();
		try {
			listaCliente = clienteDao.getBeansLike(Cliente.class, "nome", nome);
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return listaCliente;
	}

	public NfseDetalhe getNfseDetalhe() {
		return nfseDetalhe;
	}

	public void setNfseDetalhe(NfseDetalhe nfseDetalhe) {
		this.nfseDetalhe = nfseDetalhe;
	}

	public NfseDetalhe getNfseDetalheSelecionado() {
		return nfseDetalheSelecionado;
	}

	public void setNfseDetalheSelecionado(NfseDetalhe nfseDetalheSelecionado) {
		this.nfseDetalheSelecionado = nfseDetalheSelecionado;
	}

	public NfseIntermediario getNfseIntermediario() {
		return nfseIntermediario;
	}

	public void setNfseIntermediario(NfseIntermediario nfseIntermediario) {
		this.nfseIntermediario = nfseIntermediario;
	}

	public NfseIntermediario getNfseIntermediarioSelecionado() {
		return nfseIntermediarioSelecionado;
	}

	public void setNfseIntermediarioSelecionado(NfseIntermediario nfseIntermediarioSelecionado) {
		this.nfseIntermediarioSelecionado = nfseIntermediarioSelecionado;
	}

}
