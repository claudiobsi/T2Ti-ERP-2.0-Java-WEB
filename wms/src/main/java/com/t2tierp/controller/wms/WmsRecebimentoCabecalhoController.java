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
import com.t2tierp.model.bean.wms.WmsAgendamento;
import com.t2tierp.model.bean.wms.WmsRecebimentoCabecalho;
import com.t2tierp.model.bean.wms.WmsRecebimentoDetalhe;
import com.t2tierp.model.dao.InterfaceDAO;
import com.t2tierp.util.FacesContextUtil;

@ManagedBean
@ViewScoped
public class WmsRecebimentoCabecalhoController extends AbstractController<WmsRecebimentoCabecalho> implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private InterfaceDAO<WmsAgendamento> wmsAgendamentoDao;
    @EJB
    private InterfaceDAO<Produto> produtoDao;
    
	private WmsRecebimentoDetalhe wmsRecebimentoDetalhe;
	private WmsRecebimentoDetalhe wmsRecebimentoDetalheSelecionado;

    @Override
    public Class<WmsRecebimentoCabecalho> getClazz() {
        return WmsRecebimentoCabecalho.class;
    }

    @Override
    public String getFuncaoBase() {
        return "WMS_RECEBIMENTO_CABECALHO";
    }

    @Override
    public void incluir() {
        super.incluir();

        getObjeto().setListaWmsRecebimentoDetalhe(new HashSet<>());
	}

	public void incluirWmsRecebimentoDetalhe() {
        wmsRecebimentoDetalhe = new WmsRecebimentoDetalhe();
        wmsRecebimentoDetalhe.setWmsRecebimentoCabecalho(getObjeto());
	}

	public void alterarWmsRecebimentoDetalhe() {
        wmsRecebimentoDetalhe = wmsRecebimentoDetalheSelecionado;
	}

	public void salvarWmsRecebimentoDetalhe() {
        if (wmsRecebimentoDetalhe.getId() == null) {
            getObjeto().getListaWmsRecebimentoDetalhe().add(wmsRecebimentoDetalhe);
        }
        salvar("Registro salvo com sucesso!");
	}
	
	public void excluirWmsRecebimentoDetalhe() {
        if (wmsRecebimentoDetalheSelecionado == null || wmsRecebimentoDetalheSelecionado.getId() == null) {
            FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_INFO, "Nenhum registro selecionado!", null);
        } else {
            getObjeto().getListaWmsRecebimentoDetalhe().remove(wmsRecebimentoDetalheSelecionado);
            salvar("Registro excluído com sucesso!");
        }
	}

    public List<WmsAgendamento> getListaWmsAgendamento(String localOperacao) {
        List<WmsAgendamento> listaWmsAgendamento = new ArrayList<>();
        try {
            listaWmsAgendamento = wmsAgendamentoDao.getBeansLike(WmsAgendamento.class, "localOperacao", localOperacao);
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return listaWmsAgendamento;
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
    
    public WmsRecebimentoDetalhe getWmsRecebimentoDetalhe() {
        return wmsRecebimentoDetalhe;
    }

    public void setWmsRecebimentoDetalhe(WmsRecebimentoDetalhe wmsRecebimentoDetalhe) {
        this.wmsRecebimentoDetalhe = wmsRecebimentoDetalhe;
    }

	public WmsRecebimentoDetalhe getWmsRecebimentoDetalheSelecionado() {
		return wmsRecebimentoDetalheSelecionado;
	}

	public void setWmsRecebimentoDetalheSelecionado(WmsRecebimentoDetalhe wmsRecebimentoDetalheSelecionado) {
		this.wmsRecebimentoDetalheSelecionado = wmsRecebimentoDetalheSelecionado;
	}


}
