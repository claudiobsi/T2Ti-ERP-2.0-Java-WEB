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
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.t2tierp.controller.AbstractController;
import com.t2tierp.model.bean.wms.WmsArmazenamento;
import com.t2tierp.model.bean.wms.WmsExpedicao;
import com.t2tierp.model.bean.wms.WmsOrdemSeparacaoCab;
import com.t2tierp.model.bean.wms.WmsOrdemSeparacaoDet;
import com.t2tierp.model.dao.InterfaceDAO;
import com.t2tierp.util.FacesContextUtil;

@ManagedBean
@ViewScoped
public class WmsExpedicaoController extends AbstractController<WmsOrdemSeparacaoCab> implements Serializable {

	private static final long serialVersionUID = 1L;
	
	@EJB
	private InterfaceDAO<WmsExpedicao> expedicaoDAO;
    @EJB
    private InterfaceDAO<WmsArmazenamento> armazenamentoDAO;
	
	private List<WmsOrdemSeparacaoDet> listaOrdemSeparacaoDetalhe;
	private WmsArmazenamento wmsArmazenamento;
	private Integer quantidade;
	private Date dataSaida;

	@Override
	public Class<WmsOrdemSeparacaoCab> getClazz() {
		return WmsOrdemSeparacaoCab.class;
	}

	@Override
	public String getFuncaoBase() {
		return "WMS_EXPEDICAO";
	}

	public void expedicaoItens() {
		try {
			if (wmsArmazenamento == null) {
				FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_WARN, "Necessário selecionar o armazenamento", "");
			} else if (listaOrdemSeparacaoDetalhe == null || listaOrdemSeparacaoDetalhe.isEmpty()){
				FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_WARN, "Nenhum item selecionado", "");
			} else {
	            for (WmsOrdemSeparacaoDet item : listaOrdemSeparacaoDetalhe) {
	                WmsExpedicao expedicao = new WmsExpedicao();
	                expedicao.setWmsArmazenamento(wmsArmazenamento);
	                expedicao.setWmsOrdemSeparacaoDet(item);
	                expedicao.setQuantidade(quantidade);
	                expedicao.setDataSaida(dataSaida);
	                
	                expedicaoDAO.persist(expedicao);
	            }
				
				FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_INFO, "Expedição realizada com sucesso", "");
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro!", e.getMessage());
		}
	}

    public List<WmsArmazenamento> getListaWmsArmazenamento(String codigo) {
        List<WmsArmazenamento> listaWmsArmazenamento = new ArrayList<>();
        try {
            listaWmsArmazenamento = armazenamentoDAO.getBeansLike(WmsArmazenamento.class, "wmsCaixa.codigo", codigo);
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return listaWmsArmazenamento;
    }
    
	public Integer getQuantidade() {
		return quantidade;
	}

	public void setQuantidade(Integer quantidade) {
		this.quantidade = quantidade;
	}

	public WmsArmazenamento getWmsArmazenamento() {
		return wmsArmazenamento;
	}

	public void setWmsArmazenamento(WmsArmazenamento wmsArmazenamento) {
		this.wmsArmazenamento = wmsArmazenamento;
	}

	public Date getDataSaida() {
		return dataSaida;
	}

	public void setDataSaida(Date dataSaida) {
		this.dataSaida = dataSaida;
	}

	public List<WmsOrdemSeparacaoDet> getListaOrdemSeparacaoDetalhe() {
		return listaOrdemSeparacaoDetalhe;
	}

	public void setListaOrdemSeparacaoDetalhe(List<WmsOrdemSeparacaoDet> listaOrdemSeparacaoDetalhe) {
		this.listaOrdemSeparacaoDetalhe = listaOrdemSeparacaoDetalhe;
	}

}
