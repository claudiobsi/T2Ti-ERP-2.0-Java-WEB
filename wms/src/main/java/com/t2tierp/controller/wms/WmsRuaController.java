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
package com.t2tierp.controller.wms;

import java.io.Serializable;
import java.util.HashSet;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.t2tierp.controller.AbstractController;
import com.t2tierp.model.bean.wms.WmsCaixa;
import com.t2tierp.model.bean.wms.WmsEstante;
import com.t2tierp.model.bean.wms.WmsRua;
import com.t2tierp.model.dao.InterfaceDAO;
import com.t2tierp.util.FacesContextUtil;

@ManagedBean
@ViewScoped
public class WmsRuaController extends AbstractController<WmsRua> implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private InterfaceDAO<WmsEstante> estanteDao;
    
	private WmsEstante wmsEstante;
	private WmsEstante wmsEstanteSelecionado;

	private WmsCaixa wmsCaixa;
	private WmsCaixa wmsCaixaSelecionado;

    @Override
    public Class<WmsRua> getClazz() {
        return WmsRua.class;
    }

    @Override
    public String getFuncaoBase() {
        return "WMS_RUA";
    }

    @Override
    public void incluir() {
        super.incluir();

        getObjeto().setListaWmsEstante(new HashSet<>());
	}

	public void incluirWmsEstante() {
        wmsEstante = new WmsEstante();
        wmsEstante.setWmsRua(getObjeto());
        wmsEstante.setListaWmsCaixa(new HashSet<>());
	}

	public void alterarWmsEstante() {
        wmsEstante = wmsEstanteSelecionado;
	}

	public void salvarWmsEstante() {
        if (wmsEstante.getId() == null) {
            getObjeto().getListaWmsEstante().add(wmsEstante);
            getObjeto().setQuantidadeEstante(getObjeto().getListaWmsEstante().size());
        }
        salvar("Registro salvo com sucesso!");
	}
	
	public void excluirWmsEstante() {
        if (wmsEstanteSelecionado == null || wmsEstanteSelecionado.getId() == null) {
            FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_INFO, "Nenhum registro selecionado!", null);
        } else {
            getObjeto().getListaWmsEstante().remove(wmsEstanteSelecionado);
            getObjeto().setQuantidadeEstante(getObjeto().getListaWmsEstante().size());
            salvar("Registro excluído com sucesso!");
        }
	}

	public void incluirWmsCaixa() {
        wmsCaixa = new WmsCaixa();
        wmsCaixa.setWmsEstante(wmsEstanteSelecionado);
	}

	public void alterarWmsCaixa() {
        wmsCaixa = wmsCaixaSelecionado;
	}

	public void salvarWmsCaixa() {
        if (wmsEstanteSelecionado == null || wmsEstanteSelecionado.getId() == null) {
            FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_ERROR, "Nenhum Estante selecionada!", "");
        } else {
            if (wmsCaixa.getId() == null) {
                wmsEstanteSelecionado.getListaWmsCaixa().add(wmsCaixa);
            }
            try {
            	wmsEstanteSelecionado.setQuantidadeCaixa(wmsEstanteSelecionado.getListaWmsCaixa().size());
            	wmsEstanteSelecionado = estanteDao.merge(wmsEstanteSelecionado);
            	
            	FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_INFO, "Registro incluído com sucesso!", "");
            } catch(Exception e) {
            	e.printStackTrace();
            	FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro!", e.getMessage());
            }
        }
	}
	
	public void excluirWmsCaixa() {
        if (wmsCaixaSelecionado == null || wmsCaixaSelecionado.getId() == null) {
            FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_INFO, "Nenhum registro selecionado!", null);
        } else {
            try {
            	wmsEstanteSelecionado.getListaWmsCaixa().remove(wmsCaixaSelecionado);
            	wmsEstanteSelecionado.setQuantidadeCaixa(wmsEstanteSelecionado.getListaWmsCaixa().size());
            	wmsEstanteSelecionado = estanteDao.merge(wmsEstanteSelecionado);
            	
            	FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_INFO, "Registro excluído com sucesso!", "");
            } catch(Exception e) {
            	e.printStackTrace();
            	FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro!", e.getMessage());
            }
        }
	}

	public WmsEstante getWmsEstante() {
		return wmsEstante;
	}

	public void setWmsEstante(WmsEstante wmsEstante) {
		this.wmsEstante = wmsEstante;
	}

	public WmsEstante getWmsEstanteSelecionado() {
		return wmsEstanteSelecionado;
	}

	public void setWmsEstanteSelecionado(WmsEstante wmsEstanteSelecionado) {
		this.wmsEstanteSelecionado = wmsEstanteSelecionado;
	}

	public WmsCaixa getWmsCaixa() {
		return wmsCaixa;
	}

	public void setWmsCaixa(WmsCaixa wmsCaixa) {
		this.wmsCaixa = wmsCaixa;
	}

	public WmsCaixa getWmsCaixaSelecionado() {
		return wmsCaixaSelecionado;
	}

	public void setWmsCaixaSelecionado(WmsCaixa wmsCaixaSelecionado) {
		this.wmsCaixaSelecionado = wmsCaixaSelecionado;
	}




}
