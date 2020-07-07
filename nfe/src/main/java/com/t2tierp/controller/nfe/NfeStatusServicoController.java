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

import java.io.ByteArrayInputStream;
import java.io.Serializable;
import java.security.KeyStore;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.axis2.classloader.IoUtil;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import com.t2tierp.model.bean.cadastros.Certificado;
import com.t2tierp.model.bean.nfe.RespostaSefaz;
import com.t2tierp.util.FacesContextUtil;

@ManagedBean
@ViewScoped
public class NfeStatusServicoController implements Serializable {

	private static final long serialVersionUID = 1L;
	private String senhaCertificado;
	private Certificado certificado;
	private String status;

	public void consultaStatus() {
		if (certificado == null || certificado.getArquivo() == null || certificado.getSenha() == null) {
			FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_INFO, "É Necessário informar os dados do certificado antes de consultar o status!", null);
		} else {
			try {
				KeyStore ks = KeyStore.getInstance("PKCS12");
				ks.load(new ByteArrayInputStream(certificado.getArquivo()), certificado.getSenha());
				String alias = ks.aliases().nextElement();

				StatusServico statusNfe = new StatusServico();
				RespostaSefaz resposta = new RespostaSefaz();
				resposta.setResposta(statusNfe.verificaStatusServico(ks, alias, certificado.getSenha()));
				status = resposta.getResposta();
			} catch (Exception e) {
				e.printStackTrace();
			}
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

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

}
