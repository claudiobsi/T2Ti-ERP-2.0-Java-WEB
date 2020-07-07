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

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.apache.axis2.classloader.IoUtil;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import com.t2tierp.controller.AbstractController;
import com.t2tierp.model.bean.cadastros.Certificado;
import com.t2tierp.model.bean.nfe.NfeConfiguracao;
import com.t2tierp.util.FacesContextUtil;

@ManagedBean
@ViewScoped
public class SelecionaCertificadoController extends AbstractController<NfeConfiguracao> {

	private static final long serialVersionUID = 1L;
	private String senhaCertificado;
	private Certificado certificado;
	private final String diretorioCertificado = "modulos/nfe/certificado";

	@Override
	public Class<NfeConfiguracao> getClazz() {
		return NfeConfiguracao.class;
	}

	@Override
	public String getFuncaoBase() {
		return "SELECIONA_CERTIFICADO";
	}

	public void carregaDadosCertificado() {
		try {
			if (certificado != null) {
				if (senhaCertificado != null) {
					certificado.setSenha(senhaCertificado.toCharArray());
					
					ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
					String caminhoArquivo = context.getRealPath(diretorioCertificado) + System.getProperty("file.separator");
					
					File arquivoCertificado = new File(caminhoArquivo + "certificado.p12");
					OutputStream outXml = new FileOutputStream(arquivoCertificado);
					outXml.write(certificado.getArquivo());
					outXml.close();
					
					setObjeto(dao.getBean(1, NfeConfiguracao.class));
					
					getObjeto().setCertificadoDigitalCaminho(arquivoCertificado.getAbsolutePath());
					getObjeto().setCertificadoDigitalSenha(senhaCertificado);
					
					salvar();
					
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
			UploadedFile arquivo = event.getFile();
			certificado = new Certificado();
			certificado.setArquivo(IoUtil.getBytes(arquivo.getInputstream()));
			arquivo.getInputstream().close();
		} catch (Exception e) {
			certificado = null;
			FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro ao realizar o upload do arquivo!", e.getMessage());
			// e.printStackTrace();
		}
	}
	
	public String getSenhaCertificado() {
		return senhaCertificado;
	}

	public void setSenhaCertificado(String senhaCertificado) {
		this.senhaCertificado = senhaCertificado;
	}
	
}
