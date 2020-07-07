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
package com.t2tierp.controller.contabilidade;

import java.io.Serializable;
import java.util.HashSet;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.t2tierp.controller.AbstractController;
import com.t2tierp.model.bean.contabilidade.ContabilLivro;
import com.t2tierp.model.bean.contabilidade.ContabilTermo;
import com.t2tierp.util.FacesContextUtil;

@ManagedBean
@ViewScoped
public class ContabilLivroController extends AbstractController<ContabilLivro> implements Serializable {

    private static final long serialVersionUID = 1L;
	private ContabilTermo contabilTermo;
	private ContabilTermo contabilTermoSelecionado;


    @Override
    public Class<ContabilLivro> getClazz() {
        return ContabilLivro.class;
    }

    @Override
    public String getFuncaoBase() {
        return "CONTABIL_LIVRO";
    }
    
    @Override
    public void incluir() {
        super.incluir();
        getObjeto().setEmpresa(FacesContextUtil.getEmpresaUsuario());
        getObjeto().setListaContabilTermo(new HashSet<ContabilTermo>());
	}
    
	public void incluirContabilTermo() {
        contabilTermo = new ContabilTermo();
        contabilTermo.setContabilLivro(getObjeto());
	}

	public void alterarContabilTermo() {
        contabilTermo = contabilTermoSelecionado;
	}

	public void salvarContabilTermo() {
        if (contabilTermo.getId() == null) {
            getObjeto().getListaContabilTermo().add(contabilTermo);
        }
        salvar("Registro salvo com sucesso!");
	}

	public void excluirContabilTermo() {
        if (contabilTermoSelecionado == null || contabilTermoSelecionado.getId() == null) {
            FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_INFO, "Nenhum registro selecionado!", null);
        } else {
            getObjeto().getListaContabilTermo().remove(contabilTermoSelecionado);
            salvar("Registro excluído com sucesso!");
        }
	}

	public ContabilTermo getContabilTermo() {
		return contabilTermo;
	}

	public void setContabilTermo(ContabilTermo contabilTermo) {
		this.contabilTermo = contabilTermo;
	}

	public ContabilTermo getContabilTermoSelecionado() {
		return contabilTermoSelecionado;
	}

	public void setContabilTermoSelecionado(ContabilTermo contabilTermoSelecionado) {
		this.contabilTermoSelecionado = contabilTermoSelecionado;
	}

}
