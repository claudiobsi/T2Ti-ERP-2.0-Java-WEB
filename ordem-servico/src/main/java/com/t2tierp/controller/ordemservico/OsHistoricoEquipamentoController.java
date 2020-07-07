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
package com.t2tierp.controller.ordemservico;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.t2tierp.controller.AbstractController;
import com.t2tierp.model.bean.ordemservico.OsAbertura;
import com.t2tierp.model.bean.ordemservico.OsAberturaEquipamento;
import com.t2tierp.model.bean.ordemservico.OsEquipamento;
import com.t2tierp.model.bean.ordemservico.OsProdutoServico;
import com.t2tierp.model.dao.InterfaceDAO;
import com.t2tierp.util.FacesContextUtil;

@ManagedBean
@ViewScoped
public class OsHistoricoEquipamentoController extends AbstractController<OsAberturaEquipamento> implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private InterfaceDAO<OsEquipamento> osEquipamentoDao;
    @EJB
    private InterfaceDAO<OsAbertura> osAberturaDao;
    @EJB
    private InterfaceDAO<OsProdutoServico> osProdutoServicoDao;
    
    private List<OsProdutoServico> listaOsProdutoServico;

    @Override
    public Class<OsAberturaEquipamento> getClazz() {
        return OsAberturaEquipamento.class;
    }

    @Override
    public String getFuncaoBase() {
        return "OS_HISTORICO_EQUIPAMENTO";
    }

    @Override
    public void alterar() {
    	super.alterar();
    	try {
    		List<OsAberturaEquipamento> listaAberturaEquipamento = dao.getBeans(OsAberturaEquipamento.class, "numeroSerie", getObjeto().getNumeroSerie());
    		
    		listaOsProdutoServico = new ArrayList<>();
            for (OsAberturaEquipamento a : listaAberturaEquipamento) {
                listaOsProdutoServico.addAll(osProdutoServicoDao.getBeans(OsProdutoServico.class, "osAbertura", a.getOsAbertura()));
            }    		
		} catch (Exception e) {
			e.printStackTrace();
			FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro!", e.getMessage());
		}
    }
    
	public List<OsProdutoServico> getListaOsProdutoServico() {
		return listaOsProdutoServico;
	}

	public void setListaOsProdutoServico(List<OsProdutoServico> listaOsProdutoServico) {
		this.listaOsProdutoServico = listaOsProdutoServico;
	}

}
