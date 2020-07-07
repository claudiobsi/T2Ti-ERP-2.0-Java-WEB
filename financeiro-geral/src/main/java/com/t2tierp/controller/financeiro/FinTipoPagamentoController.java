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
package com.t2tierp.controller.financeiro;

import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.t2tierp.controller.AbstractController;
import com.t2tierp.model.bean.financeiro.FinTipoPagamento;
import com.t2tierp.util.FacesContextUtil;

@ManagedBean
@ViewScoped
public class FinTipoPagamentoController extends AbstractController<FinTipoPagamento> implements Serializable {

	private static final long serialVersionUID = 1L;

	@Override
	public Class<FinTipoPagamento> getClazz() {
		return FinTipoPagamento.class;
	}

	@Override
	public String getFuncaoBase() {
		return "FIN_TIPO_PAGAMENTO";
	}

    @Override
    public void incluir() {
    	super.incluir();
    	getObjeto().setEmpresa(FacesContextUtil.getEmpresaUsuario());
    }
	
	@Override
	public void salvar() {
		if (!podeAlterarRegistro(getObjeto().getTipo())) {
			FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_WARN, "O código informado para o tipo não pode ser cadastrado.", "");
		} else {
			super.salvar();
		}
	}

	@Override
	public void excluir() {
		if (!podeAlterarRegistro(getObjetoSelecionado().getTipo())) {
			FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_WARN, "Esse registro não pode ser excluído.", "");
		} else {
			super.excluir();
		}
	}

	public boolean podeAlterarRegistro(String tipo) {
		if (tipo != null && (tipo.equals("01") || tipo.equals("02") || tipo.equals("03"))) {
			return false;
		}
		return true;
	}

}
