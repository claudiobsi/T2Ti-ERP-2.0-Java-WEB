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
import java.util.List;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.t2tierp.controller.AbstractController;
import com.t2tierp.model.bean.wms.WmsArmazenamento;
import com.t2tierp.model.bean.wms.WmsCaixa;
import com.t2tierp.model.bean.wms.WmsRecebimentoCabecalho;
import com.t2tierp.model.bean.wms.WmsRecebimentoDetalhe;
import com.t2tierp.model.dao.InterfaceDAO;
import com.t2tierp.util.FacesContextUtil;

@ManagedBean
@ViewScoped
public class WmsArmazenamentoController extends AbstractController<WmsRecebimentoCabecalho> implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@EJB
	private InterfaceDAO<WmsArmazenamento> armazenamentoDAO;
    @EJB
    private InterfaceDAO<WmsCaixa> wmsCaixaDao;
	
	private List<WmsRecebimentoDetalhe> listaRecebimentoDetalhe;
	private WmsCaixa wmsCaixa;
	private Integer quantidade;

	@Override
	public Class<WmsRecebimentoCabecalho> getClazz() {
		return WmsRecebimentoCabecalho.class;
	}

	@Override
	public String getFuncaoBase() {
		return "WMS_ARMAZENAMENTO";
	}

	public void armazenarItens() {
		try {
			if (wmsCaixa == null) {
				FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_WARN, "Necessário selecionar uma caixa", "");
			} else if (listaRecebimentoDetalhe == null || listaRecebimentoDetalhe.isEmpty()){
				FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_WARN, "Nenhum item selecionado", "");
			} else {
	            for (WmsRecebimentoDetalhe item : listaRecebimentoDetalhe) {
	                WmsArmazenamento armazenamento = new WmsArmazenamento();
	                armazenamento.setWmsCaixa(wmsCaixa);
	                armazenamento.setWmsRecebimentoDetalhe(item);
	                armazenamento.setQuantidade(item.getQuantidadeRecebida());
	                
	                armazenamentoDAO.persist(armazenamento);
	            }
				
				FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_INFO, "Armazenamento realizado com sucesso", "");
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro!", e.getMessage());
		}
	}

    public List<WmsCaixa> getListaWmsCaixa(String codigo) {
        List<WmsCaixa> listaWmsCaixa = new ArrayList<>();
        try {
            listaWmsCaixa = wmsCaixaDao.getBeansLike(WmsCaixa.class, "codigo", codigo);
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return listaWmsCaixa;
    }
	
	public List<WmsRecebimentoDetalhe> getListaRecebimentoDetalhe() {
		return listaRecebimentoDetalhe;
	}

	public void setListaRecebimentoDetalhe(List<WmsRecebimentoDetalhe> listaRecebimentoDetalhe) {
		this.listaRecebimentoDetalhe = listaRecebimentoDetalhe;
	}

	public WmsCaixa getWmsCaixa() {
		return wmsCaixa;
	}

	public void setWmsCaixa(WmsCaixa wmsCaixa) {
		this.wmsCaixa = wmsCaixa;
	}

	public Integer getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}

}
