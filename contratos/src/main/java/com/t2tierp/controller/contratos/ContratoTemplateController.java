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
package com.t2tierp.controller.contratos;

import java.io.File;
import java.io.Serializable;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import com.t2tierp.controller.AbstractController;
import com.t2tierp.model.bean.contratos.ContratoTemplate;
import com.t2tierp.util.FacesContextUtil;

@ManagedBean
@ViewScoped
public class ContratoTemplateController extends AbstractController<ContratoTemplate> implements Serializable {

    private static final long serialVersionUID = 1L;
  
    private String template;
    private final String diretorioTemplate = "modulos/contratos/template/";
    
    @Override
    public Class<ContratoTemplate> getClazz() {
        return ContratoTemplate.class;
    }

    @Override
    public String getFuncaoBase() {
        return "CONTRATO_TEMPLATE";
    }

    @Override
    public void incluir() {
    	super.incluir();
    	getObjeto().setEmpresa(FacesContextUtil.getEmpresaUsuario());
    	template = null;
    }

    @Override
    public void alterar() {
    	super.alterar();
    	try {
    		template = new String(Files.readAllBytes(Paths.get(getURIArquivo())), "UTF-8");
    	} catch (Exception e) {
    		e.printStackTrace();
    		FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro.", e.getMessage());
    	}
    }
    
    @Override
    public void salvar() {
    	if (template == null || template.trim().isEmpty()) {
    		FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_WARN, "Necessário definir um template.", "");
    	} else {
        	try {
        		setObjeto(dao.merge(getObjeto()));
        		
        		Files.write(Paths.get(getURIArquivo()), template.getBytes("UTF-8"));
        		
        		FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_INFO, "Template salvo com sucesso.", "");
        	} catch (Exception e) {
        		e.printStackTrace();
        		FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro.", e.getMessage());
        	}
    	}
    }
    
    private URI getURIArquivo() {
		ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
		File file = new File(context.getRealPath(diretorioTemplate) + System.getProperty("file.separator") + getObjeto().getId() + ".rtf");
		return file.toURI();
    }
    
	public String getTemplate() {
		return template;
	}

	public void setTemplate(String template) {
		this.template = template;
	}
	    
}
