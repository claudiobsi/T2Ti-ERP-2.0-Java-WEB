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
package com.t2tierp.util;

import java.io.File;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.http.HttpSession;

import com.t2tierp.model.bean.cadastros.Empresa;
import com.t2tierp.model.bean.cadastros.Usuario;

public class FacesContextUtil {

	public static void adiconaMensagem(Severity severity, String mensagem, String msg2) {
		FacesMessage message = new FacesMessage(severity, mensagem, msg2);
		FacesContext.getCurrentInstance().addMessage(null, message);
	}

	public static void downloadArquivo(File file, String nomeArquivo) throws Exception {
		FacesContext facesContext = FacesContext.getCurrentInstance();
		ExternalContext externalContext = facesContext.getExternalContext();
		externalContext.setResponseHeader("Content-Type", "text/plain");
		externalContext.setResponseHeader("Content-Length", String.valueOf(file.length()));
		externalContext.setResponseHeader("Content-Disposition", "attachment;filename=\"" + nomeArquivo + "\"");
		externalContext.getResponseOutputStream().write(Biblioteca.getBytesFromFile(file));
		facesContext.responseComplete();
	}

	public static boolean isUserInRole(String role) {
		return FacesContext.getCurrentInstance().getExternalContext().isUserInRole(role);
	}

	public static Usuario getUsuarioSessao() {
		try {
			HttpSession session = (HttpSession) FacesContext.getCurrentInstance().getExternalContext().getSession(true);
			return (Usuario) session.getAttribute("usuarioT2TiERP");
		} catch (Exception e) {
			adiconaMensagem(FacesMessage.SEVERITY_ERROR, "Erro ao buscar os dados do usuário logado.", e.getMessage());
		}
		return null;
	}

	public static Empresa getEmpresaUsuario() {
		Empresa empresa = null;
		for (Empresa e : getUsuarioSessao().getColaborador().getPessoa().getListaEmpresa()) {
			empresa = e;
		}
		return empresa;
	}
}
