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
import com.t2tierp.model.bean.contabilidade.ContabilDreCabecalho;
import com.t2tierp.model.bean.contabilidade.ContabilDreDetalhe;
import com.t2tierp.util.FacesContextUtil;

@ManagedBean
@ViewScoped
public class ContabilDreCabecalhoController extends AbstractController<ContabilDreCabecalho> implements Serializable {

    private static final long serialVersionUID = 1L;

	private ContabilDreDetalhe contabilDreDetalhe;
	private ContabilDreDetalhe contabilDreDetalheSelecionado;

    @Override
    public Class<ContabilDreCabecalho> getClazz() {
        return ContabilDreCabecalho.class;
    }

    @Override
    public String getFuncaoBase() {
        return "CONTABIL_DRE_CABECALHO";
    }

    @Override
    public void incluir() {
    	super.incluir();
    	getObjeto().setEmpresa(FacesContextUtil.getEmpresaUsuario());
    	getObjeto().setListaContabilDreDetalhe(new HashSet<ContabilDreDetalhe>());
    }

	public void incluirContabilDreDetalhe() {
        contabilDreDetalhe = new ContabilDreDetalhe();
        contabilDreDetalhe.setContabilDreCabecalho(getObjeto());
	}

	public void alterarContabilDreDetalhe() {
        contabilDreDetalhe = contabilDreDetalheSelecionado;
	}

	public void salvarContabilDreDetalhe() {
        if (contabilDreDetalhe.getId() == null) {
            getObjeto().getListaContabilDreDetalhe().add(contabilDreDetalhe);
        }
        salvar("Registro salvo com sucesso!");
	}
	
	public void excluirContabilDreDetalhe() {
        if (contabilDreDetalheSelecionado == null || contabilDreDetalheSelecionado.getId() == null) {
            FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_INFO, "Nenhum registro selecionado!", null);
        } else {
            getObjeto().getListaContabilDreDetalhe().remove(contabilDreDetalheSelecionado);
            salvar("Registro excluído com sucesso!");
        }
	}
    
	public ContabilDreDetalhe getContabilDreDetalhe() {
		return contabilDreDetalhe;
	}

	public void setContabilDreDetalhe(ContabilDreDetalhe contabilDreDetalhe) {
		this.contabilDreDetalhe = contabilDreDetalhe;
	}

	public ContabilDreDetalhe getContabilDreDetalheSelecionado() {
		return contabilDreDetalheSelecionado;
	}

	public void setContabilDreDetalheSelecionado(ContabilDreDetalhe contabilDreDetalheSelecionado) {
		this.contabilDreDetalheSelecionado = contabilDreDetalheSelecionado;
	}    
}