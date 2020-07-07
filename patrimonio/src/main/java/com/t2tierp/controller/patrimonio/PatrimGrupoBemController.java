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
package com.t2tierp.controller.patrimonio;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;

import com.t2tierp.controller.AbstractController;
import com.t2tierp.model.bean.contabilidade.ContabilConta;
import com.t2tierp.model.bean.contabilidade.ContabilHistorico;
import com.t2tierp.model.bean.patrimonio.PatrimGrupoBem;
import com.t2tierp.model.dao.InterfaceDAO;
import com.t2tierp.util.FacesContextUtil;

@ManagedBean
@ViewScoped
public class PatrimGrupoBemController extends AbstractController<PatrimGrupoBem> implements Serializable {

    private static final long serialVersionUID = 1L;
	@EJB
	private InterfaceDAO<ContabilConta> contabilContaDao;
	@EJB
	private InterfaceDAO<ContabilHistorico> contabilHistoricoDao;
	
	private ContabilConta contaDespesaDepreciacao;
	private ContabilConta contaDepreciacaoAcumulada;
	private ContabilConta contaAtivoImobilizado;
	private ContabilHistorico historico;

    @Override
    public Class<PatrimGrupoBem> getClazz() {
        return PatrimGrupoBem.class;
    }

    @Override
    public String getFuncaoBase() {
        return "PATRIM_GRUPO_BEM";
    }

    @Override
    public void incluir() {
    	super.incluir();
    	getObjeto().setEmpresa(FacesContextUtil.getEmpresaUsuario());
    }
    
    public void selecionaContaAtivoImobilizado(SelectEvent event) {
    	ContabilConta conta = (ContabilConta) event.getObject();
    	getObjeto().setContaAtivoImobilizado(conta.getClassificacao());
    }

    public void selecionaContaDepreciacaoAcumulada(SelectEvent event) {
    	ContabilConta conta = (ContabilConta) event.getObject();
    	getObjeto().setContaDepreciacaoAcumulada(conta.getClassificacao());
    }

    public void selecionaContaDespesaDepreciacao(SelectEvent event) {
    	ContabilConta conta = (ContabilConta) event.getObject();
    	getObjeto().setContaDespesaDepreciacao(conta.getClassificacao());
    }

    public void selecionaContabilHistorico(SelectEvent event) {
    	ContabilHistorico historico = (ContabilHistorico) event.getObject();
    	getObjeto().setCodigoHistorico(historico.getId());
    }
    
	public List<ContabilConta> getListaContabilConta(String nome) {
		List<ContabilConta> listaContabilConta = new ArrayList<>();
		try {
			listaContabilConta = contabilContaDao.getBeansLike(ContabilConta.class, "descricao", nome);
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return listaContabilConta;
	}

	public List<ContabilHistorico> getListaContabilHistorico(String nome) {
		List<ContabilHistorico> listaContabilHistorico = new ArrayList<>();
		try {
			listaContabilHistorico = contabilHistoricoDao.getBeansLike(ContabilHistorico.class, "descricao", nome);
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return listaContabilHistorico;
	}
	
	public InterfaceDAO<ContabilConta> getContabilContaDao() {
		return contabilContaDao;
	}

	public void setContabilContaDao(InterfaceDAO<ContabilConta> contabilContaDao) {
		this.contabilContaDao = contabilContaDao;
	}

	public ContabilConta getContaDespesaDepreciacao() {
		return contaDespesaDepreciacao;
	}

	public void setContaDespesaDepreciacao(ContabilConta contaDespesaDepreciacao) {
		this.contaDespesaDepreciacao = contaDespesaDepreciacao;
	}

	public ContabilConta getContaDepreciacaoAcumulada() {
		return contaDepreciacaoAcumulada;
	}

	public void setContaDepreciacaoAcumulada(ContabilConta contaDepreciacaoAcumulada) {
		this.contaDepreciacaoAcumulada = contaDepreciacaoAcumulada;
	}

	public ContabilConta getContaAtivoImobilizado() {
		return contaAtivoImobilizado;
	}

	public void setContaAtivoImobilizado(ContabilConta contaAtivoImobilizado) {
		this.contaAtivoImobilizado = contaAtivoImobilizado;
	}

	public ContabilHistorico getHistorico() {
		return historico;
	}

	public void setHistorico(ContabilHistorico historico) {
		this.historico = historico;
	}
    
}
