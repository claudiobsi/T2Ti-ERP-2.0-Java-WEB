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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.t2tierp.controller.AbstractController;
import com.t2tierp.model.bean.cadastros.Produto;
import com.t2tierp.model.bean.wms.WmsOrdemSeparacaoCab;
import com.t2tierp.model.bean.wms.WmsOrdemSeparacaoDet;
import com.t2tierp.model.dao.InterfaceDAO;
import com.t2tierp.util.FacesContextUtil;

@ManagedBean
@ViewScoped
public class WmsOrdemSeparacaoCabController extends AbstractController<WmsOrdemSeparacaoCab> implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private InterfaceDAO<Produto> produtoDao;

	private WmsOrdemSeparacaoDet wmsOrdemSeparacaoDet;
	private WmsOrdemSeparacaoDet wmsOrdemSeparacaoDetSelecionado;

    @Override
    public Class<WmsOrdemSeparacaoCab> getClazz() {
        return WmsOrdemSeparacaoCab.class;
    }

    @Override
    public String getFuncaoBase() {
        return "WMS_ORDEM_SEPARACAO_CAB";
    }

    @Override
    public void incluir() {
        super.incluir();

        getObjeto().setEmpresa(FacesContextUtil.getEmpresaUsuario());
        getObjeto().setListaWmsOrdemSeparacaoDet(new HashSet<>());
	}

	public void incluirWmsOrdemSeparacaoDet() {
        wmsOrdemSeparacaoDet = new WmsOrdemSeparacaoDet();
        wmsOrdemSeparacaoDet.setWmsOrdemSeparacaoCab(getObjeto());
	}

	public void alterarWmsOrdemSeparacaoDet() {
        wmsOrdemSeparacaoDet = wmsOrdemSeparacaoDetSelecionado;
	}

	public void salvarWmsOrdemSeparacaoDet() {
        if (wmsOrdemSeparacaoDet.getId() == null) {
            getObjeto().getListaWmsOrdemSeparacaoDet().add(wmsOrdemSeparacaoDet);
        }
        salvar("Registro salvo com sucesso!");
	}
	
	public void excluirWmsOrdemSeparacaoDet() {
        if (wmsOrdemSeparacaoDetSelecionado == null || wmsOrdemSeparacaoDetSelecionado.getId() == null) {
            FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_INFO, "Nenhum registro selecionado!", null);
        } else {
            getObjeto().getListaWmsOrdemSeparacaoDet().remove(wmsOrdemSeparacaoDetSelecionado);
            salvar("Registro excluído com sucesso!");
        }
	}

    public List<Produto> getListaProduto(String nome) {
        List<Produto> listaProduto = new ArrayList<>();
        try {
            listaProduto = produtoDao.getBeansLike(Produto.class, "nome", nome);
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return listaProduto;
    }
	
    public WmsOrdemSeparacaoDet getWmsOrdemSeparacaoDet() {
        return wmsOrdemSeparacaoDet;
    }

    public void setWmsOrdemSeparacaoDet(WmsOrdemSeparacaoDet wmsOrdemSeparacaoDet) {
        this.wmsOrdemSeparacaoDet = wmsOrdemSeparacaoDet;
    }

	public WmsOrdemSeparacaoDet getWmsOrdemSeparacaoDetSelecionado() {
		return wmsOrdemSeparacaoDetSelecionado;
	}

	public void setWmsOrdemSeparacaoDetSelecionado(WmsOrdemSeparacaoDet wmsOrdemSeparacaoDetSelecionado) {
		this.wmsOrdemSeparacaoDetSelecionado = wmsOrdemSeparacaoDetSelecionado;
	}


}
