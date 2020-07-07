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
package com.t2tierp.controller.cte;

import java.io.File;
import java.io.FileInputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.security.KeyStore;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import com.t2tierp.controller.AbstractController;
import com.t2tierp.model.bean.cadastros.Empresa;
import com.t2tierp.model.bean.cadastros.EmpresaEndereco;
import com.t2tierp.model.bean.cte.CteCabecalho;
import com.t2tierp.model.bean.cte.CteCarga;
import com.t2tierp.model.bean.cte.CteDestinatario;
import com.t2tierp.model.bean.cte.CteEmitente;
import com.t2tierp.model.bean.cte.CteInformacaoNfOutros;
import com.t2tierp.model.bean.cte.CteRemetente;
import com.t2tierp.model.bean.cte.CteRodoviario;
import com.t2tierp.model.bean.cte.CteRodoviarioOcc;
import com.t2tierp.model.bean.nfe.NfeCabecalho;
import com.t2tierp.model.bean.nfe.NfeConfiguracao;
import com.t2tierp.model.bean.nfe.NfeDetalhe;
import com.t2tierp.model.bean.nfe.NfeNumero;
import com.t2tierp.model.bean.nfe.RespostaSefaz;
import com.t2tierp.model.dao.InterfaceDAO;
import com.t2tierp.util.Biblioteca;
import com.t2tierp.util.FacesContextUtil;

@ManagedBean
@ViewScoped
public class CteCabecalhoController extends AbstractController<CteCabecalho> implements Serializable {

	private static final long serialVersionUID = 1L;

	@EJB
	private InterfaceDAO<NfeCabecalho> nfeCabecalhoDao;
	@EJB
	private InterfaceDAO<CteRodoviario> cteRodoviarioDao;
	@EJB
	private InterfaceDAO<NfeNumero> nfeNumeroDao;
	@EJB
	private InterfaceDAO<NfeConfiguracao> nfeConfiguracaoDao;
	

	private CteCarga cteCarga;
	private CteCarga cteCargaSelecionado;

	private CteInformacaoNfOutros cteInformacaoNfOutros;
	private CteInformacaoNfOutros cteInformacaoNfOutrosSelecionado;

	private CteRodoviarioOcc cteRodoviarioOcc;
	private CteRodoviarioOcc cteRodoviarioOccSelecionado;

	@Override
	public Class<CteCabecalho> getClazz() {
		return CteCabecalho.class;
	}

	@Override
	public String getFuncaoBase() {
		return "CTE_CABECALHO";
	}

	@Override
	public void incluir() {
		super.incluir();

		getObjeto().setCteRemetente(new CteRemetente());
		getObjeto().setCteDestinatario(new CteDestinatario());
		getObjeto().setCteRodoviario(new CteRodoviario());
		getObjeto().getCteRodoviario().setListaCteRodoviarioOcc(new HashSet<CteRodoviarioOcc>());
		getObjeto().setListaCteCarga(new HashSet<CteCarga>());
		getObjeto().setListaCteInformacaoNfOutros(new HashSet<CteInformacaoNfOutros>());
		
		try {
			valoresPadrao();
		} catch (Exception e) {
			e.printStackTrace();
			FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro!", e.getMessage());
		}
	}
	
	private void valoresPadrao() throws Exception {
		EmpresaEndereco enderecoPrincipalEmpresa = null;
		for(EmpresaEndereco e : FacesContextUtil.getEmpresaUsuario().getListaEndereco()){
			if (e.getPrincipal().equals("S")){
				enderecoPrincipalEmpresa = e;
			}
		}
		
		if (enderecoPrincipalEmpresa == null) {
			throw new Exception("Endereço principal da empresa não cadastrado.");
		}

		getObjeto().setNaturezaOperacao("PRESTAÇÃO DE SERVIÇO");
		getObjeto().setModelo("57");
		getObjeto().setCfop(5353);
        
        getObjeto().setFuncionarioEmissor(FacesContextUtil.getUsuarioSessao().getColaborador().getPessoa().getNome());
        
        Empresa empresa = FacesContextUtil.getEmpresaUsuario();
        getObjeto().setEmpresa(empresa);

        CteEmitente cteEmitente = new CteEmitente();
        cteEmitente.setCteCabecalho(getObjeto());
        cteEmitente.setBairro(enderecoPrincipalEmpresa.getBairro());
        cteEmitente.setCep(enderecoPrincipalEmpresa.getCep());
        cteEmitente.setCodigoMunicipio(enderecoPrincipalEmpresa.getMunicipioIbge());
        cteEmitente.setComplemento(enderecoPrincipalEmpresa.getComplemento());
        cteEmitente.setLogradouro(enderecoPrincipalEmpresa.getLogradouro());
        cteEmitente.setNomeMunicipio(enderecoPrincipalEmpresa.getCidade());
        cteEmitente.setNumero(enderecoPrincipalEmpresa.getNumero());
        cteEmitente.setTelefone(enderecoPrincipalEmpresa.getFone());
        cteEmitente.setUf(enderecoPrincipalEmpresa.getUf());
        cteEmitente.setCnpj(empresa.getCnpj());
        cteEmitente.setIe(empresa.getInscricaoEstadual());
        cteEmitente.setFantasia(empresa.getNomeFantasia());
        cteEmitente.setNome(empresa.getRazaoSocial());
        
        getObjeto().setCteEmitente(cteEmitente);
        getObjeto().setCst("00");
        getObjeto().setSimplesNacionalIndicador(0);
        getObjeto().setValorBcIcmsOutraUf(BigDecimal.ZERO);
        getObjeto().setValorBcIcmsStRetido(BigDecimal.ZERO);
        getObjeto().setValorCreditoPresumidoIcms(BigDecimal.ZERO);
        getObjeto().setValorIcms(BigDecimal.ZERO);
        getObjeto().setValorIcmsOutraUf(BigDecimal.ZERO);
        getObjeto().setValorIcmsStRetido(BigDecimal.ZERO);
        getObjeto().setValorReceber(BigDecimal.ZERO);
        getObjeto().setValorTotalCarga(BigDecimal.ZERO);
        getObjeto().setValorTotalServico(BigDecimal.ZERO);
        getObjeto().setAliquotaIcms(BigDecimal.ZERO);
        getObjeto().setAliquotaIcmsOutraUf(BigDecimal.ZERO);
        getObjeto().setAliquotaIcmsStRetido(BigDecimal.ZERO);
        getObjeto().setBaseCalculoIcms(BigDecimal.ZERO);
        getObjeto().setPercentualBcIcmsOutraUf(BigDecimal.ZERO);
        getObjeto().setPercentualReducaoBcIcms(BigDecimal.ZERO);
		
	}
	
	@Override
	public void alterar() {
		super.alterar();
		
		try {
			getObjeto().setCteRodoviario(cteRodoviarioDao.getBeanJoinFetch(getObjeto().getCteRodoviario().getId(), CteRodoviario.class));
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public void incluirCteCarga() {
		cteCarga = new CteCarga();
		cteCarga.setCteCabecalho(getObjeto());
	}

	public void alterarCteCarga() {
		cteCarga = cteCargaSelecionado;
	}

	public void salvarCteCarga() {
		if (cteCarga.getId() == null) {
			getObjeto().getListaCteCarga().add(cteCarga);
		}
		salvar("Registro salvo com sucesso!");
	}

	public void excluirCteCarga() {
		if (cteCargaSelecionado == null || cteCargaSelecionado.getId() == null) {
			FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_INFO, "Nenhum registro selecionado!", null);
		} else {
			getObjeto().getListaCteCarga().remove(cteCargaSelecionado);
			salvar("Registro excluído com sucesso!");
		}
	}

	public void incluirCteInformacaoNfOutros() {
		cteInformacaoNfOutros = new CteInformacaoNfOutros();
		cteInformacaoNfOutros.setCteCabecalho(getObjeto());
	}

	public void alterarCteInformacaoNfOutros() {
		cteInformacaoNfOutros = cteInformacaoNfOutrosSelecionado;
	}

	public void salvarCteInformacaoNfOutros() {
		try {
			if (cteInformacaoNfOutros.getId() == null) {
				cteInformacaoNfOutros.setCteCabecalho(getObjeto());
				cteInformacaoNfOutros.setChaveAcessoNfe(cteInformacaoNfOutros.getNfeCabecalho().getChaveAcesso());
				cteInformacaoNfOutros.setChaveAcessoNfe(cteInformacaoNfOutros.getChaveAcessoNfe() + cteInformacaoNfOutros.getNfeCabecalho().getDigitoChaveAcesso());
				cteInformacaoNfOutros.setCodigoModelo(cteInformacaoNfOutros.getNfeCabecalho().getCodigoModelo());
				cteInformacaoNfOutros.setSerie(cteInformacaoNfOutros.getNfeCabecalho().getSerie());
				cteInformacaoNfOutros.setBaseCalculoIcms(cteInformacaoNfOutros.getNfeCabecalho().getBaseCalculoIcms());
				cteInformacaoNfOutros.setDataEmissao(cteInformacaoNfOutros.getNfeCabecalho().getDataHoraEmissao());
				cteInformacaoNfOutros.setValorIcms(cteInformacaoNfOutros.getNfeCabecalho().getValorIcms());
				cteInformacaoNfOutros.setValorTotal(cteInformacaoNfOutros.getNfeCabecalho().getValorTotal());
				cteInformacaoNfOutros.setValorTotalProdutos(cteInformacaoNfOutros.getNfeCabecalho().getValorTotalProdutos());

				getObjeto().getListaCteInformacaoNfOutros().add(cteInformacaoNfOutros);

				defineDadosCte(cteInformacaoNfOutros.getNfeCabecalho());
			}
			salvar("Registro salvo com sucesso!");
		} catch (Exception e) {
			e.printStackTrace();
			FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro!", e.getMessage());
		}
	}

	private void defineDadosCte(NfeCabecalho nfeCabecalho) throws Exception {
		CteRemetente cteRemetente = new CteRemetente();

		EmpresaEndereco enderecoPrincipalEmpresa = null;
		for(EmpresaEndereco e : FacesContextUtil.getEmpresaUsuario().getListaEndereco()){
			if (e.getPrincipal().equals("S")){
				enderecoPrincipalEmpresa = e;
			}
		}
		
		if (enderecoPrincipalEmpresa == null) {
			throw new Exception("Endereço principal da empresa não cadastrado.");
		}
		
		cteRemetente.setCteCabecalho(getObjeto());
		cteRemetente.setBairro(enderecoPrincipalEmpresa.getBairro());
		cteRemetente.setCep(enderecoPrincipalEmpresa.getCep());
		cteRemetente.setCodigoMunicipio(enderecoPrincipalEmpresa.getMunicipioIbge());
		cteRemetente.setCodigoPais(55);
		cteRemetente.setComplemento(enderecoPrincipalEmpresa.getComplemento());
		cteRemetente.setLogradouro(enderecoPrincipalEmpresa.getLogradouro());
		cteRemetente.setNomeMunicipio(enderecoPrincipalEmpresa.getCidade());
		cteRemetente.setNomePais("BRASIL");
		cteRemetente.setNumero(enderecoPrincipalEmpresa.getNumero());
		cteRemetente.setTelefone(enderecoPrincipalEmpresa.getFone());
		cteRemetente.setUf(enderecoPrincipalEmpresa.getUf());
		cteRemetente.setCnpj(nfeCabecalho.getEmpresa().getCnpj());
		cteRemetente.setFantasia(nfeCabecalho.getEmpresa().getNomeFantasia());
		cteRemetente.setEmail(nfeCabecalho.getEmpresa().getEmail());
		cteRemetente.setNome(nfeCabecalho.getEmpresa().getRazaoSocial());

		getObjeto().setCteRemetente(cteRemetente);

		CteDestinatario cteDestinatario = new CteDestinatario();
		cteDestinatario.setCteCabecalho(getObjeto());
		cteDestinatario.setBairro(nfeCabecalho.getDestinatario().getBairro());
		cteDestinatario.setCep(nfeCabecalho.getDestinatario().getCep());
		if (nfeCabecalho.getDestinatario().getCpfCnpj().length() == 11) {
			cteDestinatario.setCpf(nfeCabecalho.getDestinatario().getCpfCnpj());
		} else {
			cteDestinatario.setCnpj(nfeCabecalho.getDestinatario().getCpfCnpj());
		}
		cteDestinatario.setCodigoMunicipio(nfeCabecalho.getDestinatario().getCodigoMunicipio());
		cteDestinatario.setCodigoPais(nfeCabecalho.getDestinatario().getCodigoPais());
		cteDestinatario.setComplemento(nfeCabecalho.getDestinatario().getComplemento());
		cteDestinatario.setEmail(nfeCabecalho.getDestinatario().getEmail());
		cteDestinatario.setFantasia(nfeCabecalho.getDestinatario().getNome());
		cteDestinatario.setIe(nfeCabecalho.getDestinatario().getInscricaoEstadual());
		cteDestinatario.setLogradouro(nfeCabecalho.getDestinatario().getLogradouro());
		cteDestinatario.setNome(nfeCabecalho.getDestinatario().getNome());
		cteDestinatario.setNomeMunicipio(nfeCabecalho.getDestinatario().getNomeMunicipio());
		cteDestinatario.setNomePais(nfeCabecalho.getDestinatario().getNomePais());
		cteDestinatario.setNumero(nfeCabecalho.getDestinatario().getNumero());
		cteDestinatario.setTelefone(nfeCabecalho.getDestinatario().getTelefone());
		cteDestinatario.setUf(nfeCabecalho.getDestinatario().getUf());

		getObjeto().setCteDestinatario(cteDestinatario);

		nfeCabecalho = nfeCabecalhoDao.getBeanJoinFetch(nfeCabecalho.getId(), NfeCabecalho.class);
		
		BigDecimal valorTotalCarga = BigDecimal.ZERO;
		for (NfeDetalhe d : nfeCabecalho.getListaNfeDetalhe()) {
			CteCarga cteCarga = new CteCarga();
			cteCarga.setCteCabecalho(getObjeto());
			cteCarga.setCodigoUnidadeMedida("00");
			cteCarga.setQuantidade(d.getQuantidadeComercial());
			cteCarga.setTipoMedida("PESO DECLARADO");

			getObjeto().getListaCteCarga().add(cteCarga);

	        getObjeto().setProdutoPredominante(d.getNomeProduto());
	        getObjeto().setCargaOutrasCaracteristicas("NENHUMA");

			valorTotalCarga = valorTotalCarga.add(d.getValorTotal());
		}
		getObjeto().setValorTotalCarga(valorTotalCarga);
		getObjeto().setValorTotalServico(valorTotalCarga);
		
		if (getObjeto().getId() == null) {
			try {
	            Date dataAtual = new Date();

	            getObjeto().setUfEmitente(getObjeto().getEmpresa().getCodigoIbgeUf());
	            getObjeto().setDataHoraEmissao(dataAtual);
	            getObjeto().setCodigoMunicipioEnvio(getObjeto().getCteDestinatario().getCodigoMunicipio());

	            numeroCte();
	            configuraCte();

	            getObjeto().getCteDestinatario().setCteCabecalho(getObjeto());
	            getObjeto().getCteEmitente().setCteCabecalho(getObjeto());
	            getObjeto().getCteRemetente().setCteCabecalho(getObjeto());
	            getObjeto().getCteRodoviario().setCteCabecalho(getObjeto());
	            
	            getObjeto().setUfEnvio(getObjeto().getCteRemetente().getUf());
	            getObjeto().setNomeMunicipioEnvio(getObjeto().getCteRemetente().getNomeMunicipio());
	            getObjeto().setNomeMunicipioIniPrestacao(getObjeto().getCteRemetente().getNomeMunicipio());
	            getObjeto().setUfIniPrestacao(getObjeto().getCteRemetente().getUf());
	            getObjeto().setCodigoMunicipioIniPrestacao(getObjeto().getCteRemetente().getCodigoMunicipio());
	            getObjeto().setCodigoMunicipioFimPrestacao(getObjeto().getCteDestinatario().getCodigoMunicipio());
	            getObjeto().setUfFimPrestacao(getObjeto().getCteDestinatario().getUf());
	            getObjeto().setNomeMunicipioFimPrestacao(getObjeto().getCteDestinatario().getNomeMunicipio());
			} catch(Exception e) {
				e.printStackTrace();
				FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro!", e.getMessage());
			}
		}
	}

    private void numeroCte() throws Exception {
        DecimalFormat formatoNumero = new DecimalFormat("000000000");
        DecimalFormat formatoCodigoNumerico = new DecimalFormat("00000000");
        SimpleDateFormat formatoAno = new SimpleDateFormat("yy");
        SimpleDateFormat formatoMes = new SimpleDateFormat("MM");

        NfeNumero numero = nfeNumeroDao.getBean(1, NfeNumero.class);
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
        getObjeto().setSerie("000");

        getObjeto().setChaveAcesso("" + getObjeto().getEmpresa().getCodigoIbgeUf()
                + formatoAno.format(getObjeto().getDataHoraEmissao())
                + formatoMes.format(getObjeto().getDataHoraEmissao())
                + getObjeto().getEmpresa().getCnpj()
                + getObjeto().getModelo()
                + getObjeto().getSerie()
                + getObjeto().getNumero()
                + getObjeto().getTipoEmissao()
                + getObjeto().getCodigoNumerico());
        getObjeto().setDigitoChaveAcesso(Biblioteca.modulo11(getObjeto().getChaveAcesso()).toString());
    }

    private void configuraCte() throws Exception {
        NfeConfiguracao configuracao = nfeConfiguracaoDao.getBean(1, NfeConfiguracao.class);

        getObjeto().setAmbiente(configuracao.getWebserviceAmbiente());
        getObjeto().setProcessoEmissao(configuracao.getProcessoEmissao());
        getObjeto().setVersaoProcessoEmissao(configuracao.getVersaoProcessoEmissao());
    }
	
	public void excluirCteInformacaoNfOutros() {
		if (cteInformacaoNfOutrosSelecionado == null || cteInformacaoNfOutrosSelecionado.getId() == null) {
			FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_INFO, "Nenhum registro selecionado!", null);
		} else {
			getObjeto().getListaCteInformacaoNfOutros().remove(cteInformacaoNfOutrosSelecionado);
			salvar("Registro excluído com sucesso!");
		}
	}

	public void incluirCteRodoviarioOcc() {
		cteRodoviarioOcc = new CteRodoviarioOcc();
		cteRodoviarioOcc.setCteRodoviario(getObjeto().getCteRodoviario());
	}

	public void alterarCteRodoviarioOcc() {
		cteRodoviarioOcc = cteRodoviarioOccSelecionado;
	}

	public void salvarCteRodoviarioOcc() {
		if (cteRodoviarioOcc.getId() == null) {
			getObjeto().getCteRodoviario().getListaCteRodoviarioOcc().add(cteRodoviarioOcc);
		}
		salvar("Registro salvo com sucesso!");
	}

	public void excluirCteRodoviarioOcc() {
		if (cteRodoviarioOccSelecionado == null || cteRodoviarioOccSelecionado.getId() == null) {
			FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_INFO, "Nenhum registro selecionado!", null);
		} else {
			getObjeto().getCteRodoviario().getListaCteRodoviarioOcc().remove(cteRodoviarioOccSelecionado);
			salvar("Registro excluído com sucesso!");
		}
	}

    @SuppressWarnings("rawtypes")
	public void enviaCte() {
        try {
        	NfeConfiguracao nfeConfiguracao = nfeConfiguracaoDao.getBean(1, NfeConfiguracao.class);
        	
            if (nfeConfiguracao == null) {
                throw new Exception("Configuração NF-e não definida.");
            }

            KeyStore ks = KeyStore.getInstance("PKCS12");
            char[] senha = nfeConfiguracao.getCertificadoDigitalSenha().toCharArray();
            ks.load(new FileInputStream(new File(nfeConfiguracao.getCertificadoDigitalCaminho())), senha);
            String alias = ks.aliases().nextElement();

            GerarXmlCte geraXmlCte = new GerarXmlCte();
            String xml = geraXmlCte.geraXmlEnvio(getObjeto(), alias, ks, senha);

            ServletContext context = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
            try {
                ValidaXmlCte validaXmlNfe = new ValidaXmlCte();
                validaXmlNfe.validaXmlEnvio(xml, context);
            } catch (Exception e) {
                throw new Exception("Erro na validação do XML\n" + e.getMessage());
            }

            EnviaCte envia = new EnviaCte();
            Map mapResposta = envia.enviaCte(xml, alias, ks, senha, getObjeto().getUfEmitente().toString(), String.valueOf(getObjeto().getAmbiente()));

            Boolean autorizado = (Boolean) mapResposta.get("autorizado");
            RespostaSefaz respostaSefaz = new RespostaSefaz();
            respostaSefaz.setAutorizado(autorizado);
            respostaSefaz.setResposta((String) mapResposta.get("resposta"));
            respostaSefaz.setNumeroRecibo((String) mapResposta.get("numeroRecibo"));
            respostaSefaz.setXmlEnviNfe((String) mapResposta.get("xmlEnviCte"));

            if (autorizado) {
                String xmlProc = (String) mapResposta.get("xmlProc");
                salvaArquivos(xmlProc, context);
            }

            dao.merge(getObjeto());
            
            FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_INFO, "CT-e enviado!", respostaSefaz.getResposta());
        } catch (Exception ex) {
            ex.printStackTrace();
            FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro!", ex.getMessage());
        }
    }

    //@SuppressWarnings({ "rawtypes", "unchecked" })
	private void salvaArquivos(String xml, ServletContext context) throws Exception {
        //salva o xml
        /*String caminhoArquivo = context.getRealPath("modulos/cte/xml") + System.getProperty("file.separator") + getObjeto().getChaveAcesso() + getObjeto().getDigitoChaveAcesso();
        OutputStream outXml = new FileOutputStream(new File(caminhoArquivo + "-cteproc.xml"));
        outXml.write(xml.getBytes());
        outXml.close();

        //gera e salva o dacte
        Map map = new HashMap();
        Image image = new ImageIcon(context.getRealPath("imagens/logo_t2ti.png")).getImage();
        map.put("Logo", image);

        JRXmlDataSource jrXmlDataSource = new JRXmlDataSource(caminhoArquivo + "-cteproc.xml", "/cteProc/CTe/infCTe/det");
        JasperPrint jp = JasperFillManager.fillReport(context.getRealPath("modulos/cte/dacte/dacte.jasper"), map, jrXmlDataSource);
        OutputStream outPdf = new FileOutputStream(new File(caminhoArquivo + ".pdf"));
        outPdf.write(JasperExportManager.exportReportToPdf(jp));
        outPdf.close();*/
    }
	
	public List<NfeCabecalho> getListaNfeCabecalho(String chaveAcesso) {
		List<NfeCabecalho> listaNfeCabecalho = new ArrayList<>();
		try {
			listaNfeCabecalho = nfeCabecalhoDao.getBeansLike(NfeCabecalho.class, "chaveAcesso", chaveAcesso);
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return listaNfeCabecalho;
	}

	public CteCarga getCteCarga() {
		return cteCarga;
	}

	public void setCteCarga(CteCarga cteCarga) {
		this.cteCarga = cteCarga;
	}

	public CteCarga getCteCargaSelecionado() {
		return cteCargaSelecionado;
	}

	public void setCteCargaSelecionado(CteCarga cteCargaSelecionado) {
		this.cteCargaSelecionado = cteCargaSelecionado;
	}

	public CteInformacaoNfOutros getCteInformacaoNfOutros() {
		return cteInformacaoNfOutros;
	}

	public void setCteInformacaoNfOutros(CteInformacaoNfOutros cteInformacaoNfOutros) {
		this.cteInformacaoNfOutros = cteInformacaoNfOutros;
	}

	public CteInformacaoNfOutros getCteInformacaoNfOutrosSelecionado() {
		return cteInformacaoNfOutrosSelecionado;
	}

	public void setCteInformacaoNfOutrosSelecionado(CteInformacaoNfOutros cteInformacaoNfOutrosSelecionado) {
		this.cteInformacaoNfOutrosSelecionado = cteInformacaoNfOutrosSelecionado;
	}

	public CteRodoviarioOcc getCteRodoviarioOcc() {
		return cteRodoviarioOcc;
	}

	public void setCteRodoviarioOcc(CteRodoviarioOcc cteRodoviarioOcc) {
		this.cteRodoviarioOcc = cteRodoviarioOcc;
	}

	public CteRodoviarioOcc getCteRodoviarioOccSelecionado() {
		return cteRodoviarioOccSelecionado;
	}

	public void setCteRodoviarioOccSelecionado(CteRodoviarioOcc cteRodoviarioOccSelecionado) {
		this.cteRodoviarioOccSelecionado = cteRodoviarioOccSelecionado;
	}

}
