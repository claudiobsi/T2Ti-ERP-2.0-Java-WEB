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
import com.t2tierp.model.bean.cadastros.Colaborador;
import com.t2tierp.model.bean.folhapagamento.FolhaPpp;
import com.t2tierp.model.bean.folhapagamento.FolhaPppAtividade;
import com.t2tierp.model.bean.folhapagamento.FolhaPppCat;
import com.t2tierp.model.bean.folhapagamento.FolhaPppExameMedico;
import com.t2tierp.model.bean.folhapagamento.FolhaPppFatorRisco;
import com.t2tierp.model.dao.InterfaceDAO;
import com.t2tierp.util.FacesContextUtil;

@ManagedBean
@ViewScoped
public class FolhaPppController extends AbstractController<FolhaPpp> implements Serializable {

    private static final long serialVersionUID = 1L;
    @EJB
    private InterfaceDAO<Colaborador> colaboradorDao;

	private FolhaPppCat folhaPppCat;
	private FolhaPppCat folhaPppCatSelecionado;
	private FolhaPppAtividade folhaPppAtividade;
	private FolhaPppAtividade folhaPppAtividadeSelecionado;
	private FolhaPppFatorRisco folhaPppFatorRisco;
	private FolhaPppFatorRisco folhaPppFatorRiscoSelecionado;
	private FolhaPppExameMedico folhaPppExameMedico;
	private FolhaPppExameMedico folhaPppExameMedicoSelecionado;
    
    @Override
    public Class<FolhaPpp> getClazz() {
        return FolhaPpp.class;
    }

    @Override
    public String getFuncaoBase() {
        return "FOLHA_PPP";
    }

    @Override
    public void incluir() {
        super.incluir();
        getObjeto().setListaFolhaPppCat(new HashSet<FolhaPppCat>());
        getObjeto().setListaFolhaPppAtividade(new HashSet<FolhaPppAtividade>());
        getObjeto().setListaFolhaPppFatorRisco(new HashSet<FolhaPppFatorRisco>());
        getObjeto().setListaFolhaPppExameMedico(new HashSet<FolhaPppExameMedico>());
	}
    
	public void incluirFolhaPppCat() {
        folhaPppCat = new FolhaPppCat();
        folhaPppCat.setFolhaPpp(getObjeto());
	}

	public void alterarFolhaPppCat() {
        folhaPppCat = folhaPppCatSelecionado;
	}

	public void salvarFolhaPppCat() {
        if (folhaPppCat.getId() == null) {
            getObjeto().getListaFolhaPppCat().add(folhaPppCat);
        }
        salvar("Registro salvo com sucesso!");
	}
	
	public void excluirFolhaPppCat() {
        if (folhaPppCatSelecionado == null || folhaPppCatSelecionado.getId() == null) {
            FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_INFO, "Nenhum registro selecionado!", null);
        } else {
            getObjeto().getListaFolhaPppCat().remove(folhaPppCatSelecionado);
            salvar("Registro excluído com sucesso!");
        }
	}

	public void incluirFolhaPppAtividade() {
        folhaPppAtividade = new FolhaPppAtividade();
        folhaPppAtividade.setFolhaPpp(getObjeto());
	}

	public void alterarFolhaPppAtividade() {
        folhaPppAtividade = folhaPppAtividadeSelecionado;
	}

	public void salvarFolhaPppAtividade() {
        if (folhaPppAtividade.getId() == null) {
            getObjeto().getListaFolhaPppAtividade().add(folhaPppAtividade);
        }
        salvar("Registro salvo com sucesso!");
	}
	
	public void excluirFolhaPppAtividade() {
        if (folhaPppAtividadeSelecionado == null || folhaPppAtividadeSelecionado.getId() == null) {
            FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_INFO, "Nenhum registro selecionado!", null);
        } else {
            getObjeto().getListaFolhaPppAtividade().remove(folhaPppAtividadeSelecionado);
            salvar("Registro excluído com sucesso!");
        }
	}

	public void incluirFolhaPppFatorRisco() {
        folhaPppFatorRisco = new FolhaPppFatorRisco();
        folhaPppFatorRisco.setFolhaPpp(getObjeto());
	}

	public void alterarFolhaPppFatorRisco() {
        folhaPppFatorRisco = folhaPppFatorRiscoSelecionado;
	}

	public void salvarFolhaPppFatorRisco() {
        if (folhaPppFatorRisco.getId() == null) {
            getObjeto().getListaFolhaPppFatorRisco().add(folhaPppFatorRisco);
        }
        salvar("Registro salvo com sucesso!");
	}
	
	public void excluirFolhaPppFatorRisco() {
        if (folhaPppFatorRiscoSelecionado == null || folhaPppFatorRiscoSelecionado.getId() == null) {
            FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_INFO, "Nenhum registro selecionado!", null);
        } else {
            getObjeto().getListaFolhaPppFatorRisco().remove(folhaPppFatorRiscoSelecionado);
            salvar("Registro excluído com sucesso!");
        }
	}

	public void incluirFolhaPppExameMedico() {
        folhaPppExameMedico = new FolhaPppExameMedico();
        folhaPppExameMedico.setFolhaPpp(getObjeto());
	}

	public void alterarFolhaPppExameMedico() {
        folhaPppExameMedico = folhaPppExameMedicoSelecionado;
	}

	public void salvarFolhaPppExameMedico() {
        if (folhaPppExameMedico.getId() == null) {
            getObjeto().getListaFolhaPppExameMedico().add(folhaPppExameMedico);
        }
        salvar("Registro salvo com sucesso!");
	}
	
	public void excluirFolhaPppExameMedico() {
        if (folhaPppExameMedicoSelecionado == null || folhaPppExameMedicoSelecionado.getId() == null) {
            FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_INFO, "Nenhum registro selecionado!", null);
        } else {
            getObjeto().getListaFolhaPppExameMedico().remove(folhaPppExameMedicoSelecionado);
            salvar("Registro excluído com sucesso!");
        }
	}
    
    public List<Colaborador> getListaColaborador(String nome) {
        List<Colaborador> listaColaborador = new ArrayList<>();
        try {
            listaColaborador = colaboradorDao.getBeansLike(Colaborador.class, "pessoa.nome", nome);
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return listaColaborador;
    }

	public FolhaPppCat getFolhaPppCat() {
		return folhaPppCat;
	}

	public void setFolhaPppCat(FolhaPppCat folhaPppCat) {
		this.folhaPppCat = folhaPppCat;
	}

	public FolhaPppCat getFolhaPppCatSelecionado() {
		return folhaPppCatSelecionado;
	}

	public void setFolhaPppCatSelecionado(FolhaPppCat folhaPppCatSelecionado) {
		this.folhaPppCatSelecionado = folhaPppCatSelecionado;
	}

	public FolhaPppAtividade getFolhaPppAtividade() {
		return folhaPppAtividade;
	}

	public void setFolhaPppAtividade(FolhaPppAtividade folhaPppAtividade) {
		this.folhaPppAtividade = folhaPppAtividade;
	}

	public FolhaPppAtividade getFolhaPppAtividadeSelecionado() {
		return folhaPppAtividadeSelecionado;
	}

	public void setFolhaPppAtividadeSelecionado(FolhaPppAtividade folhaPppAtividadeSelecionado) {
		this.folhaPppAtividadeSelecionado = folhaPppAtividadeSelecionado;
	}

	public FolhaPppFatorRisco getFolhaPppFatorRisco() {
		return folhaPppFatorRisco;
	}

	public void setFolhaPppFatorRisco(FolhaPppFatorRisco folhaPppFatorRisco) {
		this.folhaPppFatorRisco = folhaPppFatorRisco;
	}

	public FolhaPppFatorRisco getFolhaPppFatorRiscoSelecionado() {
		return folhaPppFatorRiscoSelecionado;
	}

	public void setFolhaPppFatorRiscoSelecionado(FolhaPppFatorRisco folhaPppFatorRiscoSelecionado) {
		this.folhaPppFatorRiscoSelecionado = folhaPppFatorRiscoSelecionado;
	}

	public FolhaPppExameMedico getFolhaPppExameMedico() {
		return folhaPppExameMedico;
	}

	public void setFolhaPppExameMedico(FolhaPppExameMedico folhaPppExameMedico) {
		this.folhaPppExameMedico = folhaPppExameMedico;
	}

	public FolhaPppExameMedico getFolhaPppExameMedicoSelecionado() {
		return folhaPppExameMedicoSelecionado;
	}

	public void setFolhaPppExameMedicoSelecionado(FolhaPppExameMedico folhaPppExameMedicoSelecionado) {
		this.folhaPppExameMedicoSelecionado = folhaPppExameMedicoSelecionado;
	}

}
