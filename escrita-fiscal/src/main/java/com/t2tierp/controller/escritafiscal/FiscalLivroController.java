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
package com.t2tierp.controller.escritafiscal;

import java.io.Serializable;
import java.util.HashSet;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.t2tierp.controller.AbstractController;
import com.t2tierp.model.bean.escritafiscal.FiscalLivro;
import com.t2tierp.model.bean.escritafiscal.FiscalTermo;
import com.t2tierp.util.FacesContextUtil;

@ManagedBean
@ViewScoped
public class FiscalLivroController extends AbstractController<FiscalLivro> implements Serializable {

    private static final long serialVersionUID = 1L;

	private FiscalTermo fiscalTermo;
	private FiscalTermo fiscalTermoSelecionado;

    @Override
    public Class<FiscalLivro> getClazz() {
        return FiscalLivro.class;
    }

    @Override
    public String getFuncaoBase() {
        return "FISCAL_LIVRO";
    }

    @Override
    public void incluir() {
        super.incluir();
        getObjeto().setEmpresa(FacesContextUtil.getEmpresaUsuario());
        getObjeto().setListaFiscalTermo(new HashSet<FiscalTermo>());
	}

	public void incluirFiscalTermo() {
        fiscalTermo = new FiscalTermo();
        fiscalTermo.setFiscalLivro(getObjeto());
	}

	public void alterarFiscalTermo() {
        fiscalTermo = fiscalTermoSelecionado;
	}

	public void salvarFiscalTermo() {
        if (fiscalTermo.getId() == null) {
            getObjeto().getListaFiscalTermo().add(fiscalTermo);
        }
        salvar("Registro salvo com sucesso!");
	}

	public void excluirFiscalTermo() {
        if (fiscalTermoSelecionado == null || fiscalTermoSelecionado.getId() == null) {
            FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_INFO, "Nenhum registro selecionado!", null);
        } else {
            getObjeto().getListaFiscalTermo().remove(fiscalTermoSelecionado);
            salvar("Registro excluído com sucesso!");
        }
	}

    public FiscalTermo getFiscalTermo() {
        return fiscalTermo;
    }

    public void setFiscalTermo(FiscalTermo fiscalTermo) {
        this.fiscalTermo = fiscalTermo;
    }

	public FiscalTermo getFiscalTermoSelecionado() {
		return fiscalTermoSelecionado;
	}

	public void setFiscalTermoSelecionado(FiscalTermo fiscalTermoSelecionado) {
		this.fiscalTermoSelecionado = fiscalTermoSelecionado;
	}


}
