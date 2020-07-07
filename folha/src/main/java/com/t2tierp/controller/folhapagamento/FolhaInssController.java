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
package com.t2tierp.controller.folhapagamento;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.t2tierp.controller.AbstractController;
import com.t2tierp.model.bean.folhapagamento.FolhaInss;
import com.t2tierp.model.bean.folhapagamento.FolhaInssRetencao;
import com.t2tierp.model.bean.folhapagamento.FolhaInssServico;
import com.t2tierp.model.dao.InterfaceDAO;
import com.t2tierp.util.FacesContextUtil;

@ManagedBean
@ViewScoped
public class FolhaInssController extends AbstractController<FolhaInss> implements Serializable {

    private static final long serialVersionUID = 1L;
    @EJB
    private InterfaceDAO<FolhaInssServico> folhaInssServicoDao;
    
    private FolhaInssRetencao folhaInssRetencao;
	private FolhaInssRetencao folhaInssRetencaoSelecionado;

    @Override
    public Class<FolhaInss> getClazz() {
        return FolhaInss.class;
    }

    @Override
    public String getFuncaoBase() {
        return "FOLHA_INSS";
    }

    @Override
    public void incluir() {
    	super.incluir();
    	getObjeto().setEmpresa(FacesContextUtil.getEmpresaUsuario());
    	getObjeto().setListaFolhaInssRetencao(new HashSet<FolhaInssRetencao>());
    }

	public void incluirFolhaInssRetencao() {
        folhaInssRetencao = new FolhaInssRetencao();
        folhaInssRetencao.setFolhaInss(getObjeto());
	}

	public void alterarFolhaInssRetencao() {
        folhaInssRetencao = folhaInssRetencaoSelecionado;
	}

	public void salvarFolhaInssRetencao() {
        if (folhaInssRetencao.getId() == null) {
            getObjeto().getListaFolhaInssRetencao().add(folhaInssRetencao);
        }
        salvar("Registro salvo com sucesso!");
	}
	
	public void excluirFolhaInssRetencao() {
        if (folhaInssRetencaoSelecionado == null || folhaInssRetencaoSelecionado.getId() == null) {
            FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_INFO, "Nenhum registro selecionado!", null);
        } else {
            getObjeto().getListaFolhaInssRetencao().remove(folhaInssRetencaoSelecionado);
            salvar("Registro excluído com sucesso!");
        }
	}
    
    public List<FolhaInssServico> getListaFolhaInssServico(String nome) {
        List<FolhaInssServico> listaFolhaInssServico = new ArrayList<>();
        try {
            listaFolhaInssServico = folhaInssServicoDao.getBeansLike(FolhaInssServico.class, "nome", nome);
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return listaFolhaInssServico;
    }	
	
	public FolhaInssRetencao getFolhaInssRetencao() {
		return folhaInssRetencao;
	}

	public void setFolhaInssRetencao(FolhaInssRetencao folhaInssRetencao) {
		this.folhaInssRetencao = folhaInssRetencao;
	}

	public FolhaInssRetencao getFolhaInssRetencaoSelecionado() {
		return folhaInssRetencaoSelecionado;
	}

	public void setFolhaInssRetencaoSelecionado(FolhaInssRetencao folhaInssRetencaoSelecionado) {
		this.folhaInssRetencaoSelecionado = folhaInssRetencaoSelecionado;
	}    
}
