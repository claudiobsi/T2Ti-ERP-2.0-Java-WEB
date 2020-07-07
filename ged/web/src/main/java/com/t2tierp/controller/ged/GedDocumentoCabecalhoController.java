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
package com.t2tierp.controller.ged;

import java.io.File;
import java.io.Serializable;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.apache.commons.io.FileUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import com.t2tierp.controller.AbstractController;
import com.t2tierp.model.bean.cadastros.Empresa;
import com.t2tierp.model.bean.ged.Arquivo;
import com.t2tierp.model.bean.ged.GedDocumentoCabecalho;
import com.t2tierp.model.bean.ged.GedDocumentoDetalhe;
import com.t2tierp.model.bean.ged.GedTipoDocumento;
import com.t2tierp.model.bean.ged.GedVersaoDocumento;
import com.t2tierp.model.bean.nfe.NfeConfiguracao;
import com.t2tierp.model.dao.InterfaceDAO;
import com.t2tierp.util.Biblioteca;
import com.t2tierp.util.FacesContextUtil;

@ManagedBean
@ViewScoped
public class GedDocumentoCabecalhoController extends AbstractController<GedDocumentoCabecalho> implements Serializable {

	private static final long serialVersionUID = 1L;
	@EJB
	private InterfaceDAO<GedTipoDocumento> gedTipoDocumentoDao;
	@EJB
	private InterfaceDAO<NfeConfiguracao> nfeConfiguracaoDAO;

	private GedDocumentoDetalhe gedDocumentoDetalhe;
	private GedDocumentoDetalhe gedDocumentoDetalheSelecionado;

	private final String diretorioDocumentosGed = "modulos/ged/documentos/";

	private NfeConfiguracao nfeConfiguracao;
	private String codigoRequisicao;
	private SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
	private Random random = new Random();
	
	//parametro deve ser utilizado para integração com outros módulos
	//Deve ser chamado método que retorna uma String e definir o retorno como: 
	//'return "modulos/ged/gedDocumentoCabecalho.jsf?faces-redirect=true&integracao=valorDoParametro";'
	private String parametroIntegracao;

	@Override
	public Class<GedDocumentoCabecalho> getClazz() {
		return GedDocumentoCabecalho.class;
	}

	@Override
	public String getFuncaoBase() {
		return "GED_DOCUMENTO_CABECALHO";
	}

	@Override
	public void salvar() {
		try {
			URI uriArquivo;
			Date dataAtual = new Date();
			Empresa empresa = FacesContextUtil.getEmpresaUsuario();
			for (GedDocumentoDetalhe detalhe : getObjeto().getListaGedDocumentoDetalhe()) {
				detalhe.setGedDocumentoCabecalho(getObjeto());
				detalhe.setEmpresa(empresa);

				//salva arquivos relacionados ao documento
				uriArquivo = getURIArquivo(detalhe.getArquivo());
				Files.write(Paths.get(uriArquivo), detalhe.getArquivo().getFile());

				//se o documento for assinado, salva a assinatura
				if (detalhe.getAssinado().equals("S")) {
					Files.write(Paths.get(getURIAssinatura(detalhe.getArquivo())), detalhe.getArquivo().getAssinatura());
				}

				//salva os dados da versao do documento
				if (detalhe.getListaGedVersaoDocumento() == null) {
					detalhe.setListaGedVersaoDocumento(new HashSet<>());
				}

				if (detalhe.getId() == null) {
					GedVersaoDocumento novaVersao = new GedVersaoDocumento();
					novaVersao.setAcao("I");
					novaVersao.setCaminho(uriArquivo.toString());
					novaVersao.setDataHora(dataAtual);
					novaVersao.setGedDocumentoDetalhe(detalhe);
					novaVersao.setHashArquivo(detalhe.getArquivo().getHash());
					novaVersao.setColaborador(FacesContextUtil.getUsuarioSessao().getColaborador());
					novaVersao.setVersao(detalhe.getListaGedVersaoDocumento().size() + 1);

					detalhe.getListaGedVersaoDocumento().add(novaVersao);
				} else {
					//busca a última versão cadastrada
					GedVersaoDocumento gedVersaoDocumento = null;
					for (GedVersaoDocumento versao : detalhe.getListaGedVersaoDocumento()) {
						if (versao.getVersao() == detalhe.getListaGedVersaoDocumento().size()) {
							gedVersaoDocumento = versao;
						}
					}

					//se for incluso novo detalhe OU se o hash do arquivo mudar OU se for excluido um detalhe
					if (gedVersaoDocumento == null || (!gedVersaoDocumento.getHashArquivo().equals(detalhe.getArquivo().getHash()))//
							|| detalhe.getDataExclusao() != null) {
						GedVersaoDocumento novaVersao = new GedVersaoDocumento();
						if (detalhe.getDataExclusao() != null) {
							novaVersao.setAcao("E");
						} else {
							novaVersao.setAcao("A");
						}
						novaVersao.setCaminho(uriArquivo.toString());
						novaVersao.setDataHora(dataAtual);
						novaVersao.setGedDocumentoDetalhe(detalhe);
						novaVersao.setHashArquivo(detalhe.getArquivo().getHash());
						novaVersao.setColaborador(FacesContextUtil.getUsuarioSessao().getColaborador());
						novaVersao.setVersao(detalhe.getListaGedVersaoDocumento().size() + 1);

						detalhe.getListaGedVersaoDocumento().add(novaVersao);
					}
				}
			}
			super.salvar();
		} catch (Exception e) {
			e.printStackTrace();
			FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro!", e.getMessage());
		}
	}
	
	@Override
	public void alterar() {
		super.alterar();
		
		for (GedDocumentoDetalhe detalhe : getObjeto().getListaGedDocumentoDetalhe()) {
			//busca a última versão cadastrada
			GedVersaoDocumento gedVersaoDocumento = null;
			for (GedVersaoDocumento versao : detalhe.getListaGedVersaoDocumento()) {
				if (versao.getVersao() == detalhe.getListaGedVersaoDocumento().size()) {
					gedVersaoDocumento = versao;
				}
			}
			
            try {
    			//File file = new File(gedVersaoDocumento.getCaminho());
                Arquivo arquivo = new Arquivo();
                arquivo.setFile(Files.readAllBytes(Paths.get(new URI(gedVersaoDocumento.getCaminho()))));
                arquivo.setExtensao(getExtensaoArquivo(gedVersaoDocumento.getCaminho()));
                arquivo.setHash(gedVersaoDocumento.getHashArquivo());

                detalhe.setArquivo(arquivo);
            } catch (Exception e) {
            	e.printStackTrace();
            }
		}
	}

	@Override
	public void incluir() {
		super.incluir();
		getObjeto().setListaGedDocumentoDetalhe(new HashSet<>());
	}

	public void incluirGedDocumentoDetalhe() {
		gedDocumentoDetalhe = new GedDocumentoDetalhe();
		gedDocumentoDetalhe.setGedDocumentoCabecalho(getObjeto());
	}

	public void alterarGedDocumentoDetalhe() {
		gedDocumentoDetalhe = gedDocumentoDetalheSelecionado;
	}

	public void salvarGedDocumentoDetalhe() {
		if (gedDocumentoDetalhe.getPodeAlterar().equals("N")) {
			FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_WARN, "Documento não pode ser alterado!", "");
		} else {
			if (gedDocumentoDetalhe.getAssinado().equals("S")) {
				gedDocumentoDetalhe.getArquivo().setAssinatura(assinaDocumento(gedDocumentoDetalhe.getArquivo()));
			}

			if (!getObjeto().getListaGedDocumentoDetalhe().contains(gedDocumentoDetalhe)) {
				getObjeto().getListaGedDocumentoDetalhe().add(gedDocumentoDetalhe);
			}
		}
	}

	public void excluirGedDocumentoDetalhe() {
		if (gedDocumentoDetalhe.getPodeExcluir().equals("N")) {
			FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_WARN, "Documento não pode ser excluído!", "");
		} else {
			if (gedDocumentoDetalheSelecionado == null) {
				FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_INFO, "Nenhum registro selecionado!", null);
			} else {
				gedDocumentoDetalheSelecionado.setDataExclusao(new Date());
			}
		}
	}

	public void selecionaDocumento(FileUploadEvent event) {
		try {
			UploadedFile arquivoUpload = event.getFile();

			File file = File.createTempFile("nfe", getExtensaoArquivo(arquivoUpload.getFileName()));
			FileUtils.copyInputStreamToFile(arquivoUpload.getInputstream(), file);

			Arquivo arquivo = new Arquivo();
			arquivo.setFile(Biblioteca.getBytesFromFile(file));
			arquivo.setExtensao(getExtensaoArquivo(arquivoUpload.getFileName()));
			arquivo.setHash(getHashDocumento(arquivo));

			gedDocumentoDetalhe.setArquivo(arquivo);
			FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_INFO, "Documento vinculado!", "");
		} catch (Exception e) {
			e.printStackTrace();
			FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro!", e.getMessage());
		}
	}

	public void digitalizaDocumento() {
		try {
			codigoRequisicao = dateFormat.format(new Date()) + random.nextInt();
			File arquivoTemplate = new File(FacesContextUtil.getPath("jws/ged.jnlp"));
			String jnlp = new String(Files.readAllBytes(Paths.get(arquivoTemplate.toURI())), "UTF-8");
			jnlp = jnlp.replace("$$codigoRequisicao", codigoRequisicao);
			
			File arquivoJnlp = File.createTempFile("ged", ".jnlp");
			arquivoJnlp.deleteOnExit();
			Files.write(Paths.get(arquivoJnlp.toURI()), jnlp.getBytes("UTF-8"));
			
			FacesContextUtil.downloadArquivo(arquivoJnlp, "ged.jnlp", "application/x-java-jnlp-file");
		} catch (Exception e) {
			e.printStackTrace();
			FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro!", e.getMessage());
		}		
	}

	public void finalizaDigitalizacao() {
		try {
			Arquivo arquivo = GedDocumentoScanner.getDocumento(codigoRequisicao);
			codigoRequisicao = null;
			
			gedDocumentoDetalhe.setArquivo(arquivo);
			FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_INFO, "Documento vinculado!", "");
		} catch (Exception e) {
			e.printStackTrace();
			FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro!", e.getMessage());
		}		
	}
	
	public void downloadDocumento() {
		try {
			if (gedDocumentoDetalheSelecionado == null) {
				FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_INFO, "Nenhum registro selecionado!", "");
			} else {
				File file = File.createTempFile(gedDocumentoDetalheSelecionado.getNome(), gedDocumentoDetalheSelecionado.getArquivo().getExtensao());
				Files.write(Paths.get(file.toURI()), gedDocumentoDetalheSelecionado.getArquivo().getFile());
				
				FacesContextUtil.downloadArquivo(file, gedDocumentoDetalheSelecionado.getNome() + gedDocumentoDetalheSelecionado.getArquivo().getExtensao());
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro!", e.getMessage());
		}
	}
	
	private String getExtensaoArquivo(String caminhoArquivo) {
		if (!caminhoArquivo.equals("")) {
			int indiceExtensao = caminhoArquivo.lastIndexOf(".");
			if (indiceExtensao > -1) {
				return caminhoArquivo.substring(indiceExtensao, caminhoArquivo.length());
			}
		}
		return "";
	}

	private String getHashDocumento(Arquivo arquivo) throws Exception {
		File file = File.createTempFile("arquivo", arquivo.getExtensao());
		file.deleteOnExit();
		Files.write(Paths.get(file.toURI()), arquivo.getFile());

		return Biblioteca.md5Arquivo(file.getAbsolutePath());
	}

	private URI getURIArquivo(Arquivo arquivo) {
		ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
		File file = new File(context.getRealPath(diretorioDocumentosGed) + System.getProperty("file.separator") + arquivo.getHash() + arquivo.getExtensao());
		return file.toURI();
	}

	private URI getURIAssinatura(Arquivo arquivo) {
		ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
		File file = new File(context.getRealPath(diretorioDocumentosGed) + System.getProperty("file.separator") + arquivo.getHash() + ".assinatura");
		return file.toURI();
	}
	
	private byte[] assinaDocumento(Arquivo arquivo) {
		try {
			if (nfeConfiguracao == null) {
				nfeConfiguracao = nfeConfiguracaoDAO.getBean(1, NfeConfiguracao.class);
			}
			File file = new File(nfeConfiguracao.getCertificadoDigitalCaminho());

			return Biblioteca.geraAssinaturaArquivo(arquivo.getFile(), file, nfeConfiguracao.getCertificadoDigitalSenha().toCharArray());
		} catch (Exception e) {
			e.printStackTrace();
			FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro ao assinar o documento!", e.getMessage());
		}
		return null;
	}

	public List<GedTipoDocumento> getListaGedTipoDocumento(String nome) {
		List<GedTipoDocumento> listaGedTipoDocumento = new ArrayList<>();
		try {
			listaGedTipoDocumento = gedTipoDocumentoDao.getBeansLike(GedTipoDocumento.class, "nome", nome);
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return listaGedTipoDocumento;
	}

	public GedDocumentoDetalhe getGedDocumentoDetalhe() {
		return gedDocumentoDetalhe;
	}

	public void setGedDocumentoDetalhe(GedDocumentoDetalhe gedDocumentoDetalhe) {
		this.gedDocumentoDetalhe = gedDocumentoDetalhe;
	}

	public GedDocumentoDetalhe getGedDocumentoDetalheSelecionado() {
		return gedDocumentoDetalheSelecionado;
	}

	public void setGedDocumentoDetalheSelecionado(GedDocumentoDetalhe gedDocumentoDetalheSelecionado) {
		this.gedDocumentoDetalheSelecionado = gedDocumentoDetalheSelecionado;
	}

	public String getParametroIntegracao() {
		return parametroIntegracao;
	}

	public void setParametroIntegracao(String parametroIntegracao) {
		this.parametroIntegracao = parametroIntegracao;
	}

}
