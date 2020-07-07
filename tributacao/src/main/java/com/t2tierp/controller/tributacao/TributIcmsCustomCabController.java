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
package com.t2tierp.controller.tributacao;

import java.io.Serializable;
import java.util.HashSet;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.t2tierp.controller.AbstractController;
import com.t2tierp.model.bean.tributacao.TributIcmsCustomCab;
import com.t2tierp.model.bean.tributacao.TributIcmsCustomDet;
import com.t2tierp.util.FacesContextUtil;

@ManagedBean
@ViewScoped
public class TributIcmsCustomCabController extends AbstractController<TributIcmsCustomCab> implements Serializable {

	private static final long serialVersionUID = 1L;

	private TributIcmsCustomDet tributIcmsCustomDet;
	private TributIcmsCustomDet tributIcmsCustomDetSelecionado;

	@Override
	public Class<TributIcmsCustomCab> getClazz() {
		return TributIcmsCustomCab.class;
	}

	@Override
	public String getFuncaoBase() {
		return "TRIBUT_ICMS_CUSTOM_CAB";
	}

	@Override
	public void incluir() {
		super.incluir();
		getObjeto().setEmpresa(FacesContextUtil.getEmpresaUsuario());
		getObjeto().setListaTributIcmsCustomDet(new HashSet<TributIcmsCustomDet>());
	}

	public void incluirTributIcmsCustomDet() {
		tributIcmsCustomDet = new TributIcmsCustomDet();
		tributIcmsCustomDet.setTributIcmsCustomCab(getObjeto());
	}

	public void alterarTributIcmsCustomDet() {
		tributIcmsCustomDet = tributIcmsCustomDetSelecionado;
	}

	public void salvarTributIcmsCustomDet() {
		if (tributIcmsCustomDet.getId() == null) {
			getObjeto().getListaTributIcmsCustomDet().add(tributIcmsCustomDet);
		}
		salvar("Registro salvo com sucesso!");
	}

	public void excluirTributIcmsCustomDet() {
		if (tributIcmsCustomDetSelecionado == null || tributIcmsCustomDetSelecionado.getId() == null) {
			FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_INFO, "Nenhum registro selecionado!", null);
		} else {
			getObjeto().getListaTributIcmsCustomDet().remove(tributIcmsCustomDetSelecionado);
			salvar("Registro excluído com sucesso!");
		}
	}

	public TributIcmsCustomDet getTributIcmsCustomDet() {
		return tributIcmsCustomDet;
	}

	public void setTributIcmsCustomDet(TributIcmsCustomDet tributIcmsCustomDet) {
		this.tributIcmsCustomDet = tributIcmsCustomDet;
	}

	public TributIcmsCustomDet getTributIcmsCustomDetSelecionado() {
		return tributIcmsCustomDetSelecionado;
	}

	public void setTributIcmsCustomDetSelecionado(TributIcmsCustomDet tributIcmsCustomDetSelecionado) {
		this.tributIcmsCustomDetSelecionado = tributIcmsCustomDetSelecionado;
	}

}
