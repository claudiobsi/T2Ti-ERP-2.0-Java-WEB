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
package com.t2tierp.controller.contabilidade;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.t2tierp.controller.AbstractController;
import com.t2tierp.model.bean.contabilidade.ContabilConta;
import com.t2tierp.model.bean.contabilidade.ContabilEncerramentoExeCab;
import com.t2tierp.model.bean.contabilidade.ContabilEncerramentoExeDet;
import com.t2tierp.model.dao.InterfaceDAO;
import com.t2tierp.util.FacesContextUtil;

@ManagedBean
@ViewScoped
public class ContabilEncerramentoExeCabController extends AbstractController<ContabilEncerramentoExeCab> implements Serializable {

    private static final long serialVersionUID = 1L;
    @EJB
    private InterfaceDAO<ContabilConta> contabilContaDao;
	private ContabilEncerramentoExeDet contabilEncerramentoExeDet;
	private ContabilEncerramentoExeDet contabilEncerramentoExeDetSelecionado;


    @Override
    public Class<ContabilEncerramentoExeCab> getClazz() {
        return ContabilEncerramentoExeCab.class;
    }

    @Override
    public String getFuncaoBase() {
        return "CONTABIL_ENCERRAMENTO_EXE_CAB";
    }

    @Override
    public void incluir() {
    	super.incluir();
    	getObjeto().setEmpresa(FacesContextUtil.getEmpresaUsuario());
    	getObjeto().setListaContabilEncerramentoExeDet(new HashSet<ContabilEncerramentoExeDet>());
    }

	public void incluirContabilEncerramentoExeDet() {
        contabilEncerramentoExeDet = new ContabilEncerramentoExeDet();
        contabilEncerramentoExeDet.setContabilEncerramentoExeCab(getObjeto());
	}

	public void alterarContabilEncerramentoExeDet() {
        contabilEncerramentoExeDet = contabilEncerramentoExeDetSelecionado;
	}

	public void salvarContabilEncerramentoExeDet() {
        if (contabilEncerramentoExeDet.getId() == null) {
            getObjeto().getListaContabilEncerramentoExeDet().add(contabilEncerramentoExeDet);
        }
        salvar("Registro salvo com sucesso!");
	}

	public void excluirContabilEncerramentoExeDet() {
        if (contabilEncerramentoExeDetSelecionado == null || contabilEncerramentoExeDetSelecionado.getId() == null) {
            FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_INFO, "Nenhum registro selecionado!", null);
        } else {
            getObjeto().getListaContabilEncerramentoExeDet().remove(contabilEncerramentoExeDetSelecionado);
            salvar("Registro excluído com sucesso!");
        }
	}
    
    public List<ContabilConta> getListaContabilConta(String nome) {
        List<ContabilConta> listaContabilConta = new ArrayList<>();
        try {
            listaContabilConta = contabilContaDao.getBeansLike(ContabilConta.class, "classificacao", nome);
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return listaContabilConta;
    }
    
	public ContabilEncerramentoExeDet getContabilEncerramentoExeDet() {
		return contabilEncerramentoExeDet;
	}

	public void setContabilEncerramentoExeDet(ContabilEncerramentoExeDet contabilEncerramentoExeDet) {
		this.contabilEncerramentoExeDet = contabilEncerramentoExeDet;
	}

	public ContabilEncerramentoExeDet getContabilEncerramentoExeDetSelecionado() {
		return contabilEncerramentoExeDetSelecionado;
	}

	public void setContabilEncerramentoExeDetSelecionado(ContabilEncerramentoExeDet contabilEncerramentoExeDetSelecionado) {
		this.contabilEncerramentoExeDetSelecionado = contabilEncerramentoExeDetSelecionado;
	}    
}
