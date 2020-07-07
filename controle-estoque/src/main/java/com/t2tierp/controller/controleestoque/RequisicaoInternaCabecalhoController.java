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
package com.t2tierp.controller.controleestoque;

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
import com.t2tierp.model.bean.cadastros.Produto;
import com.t2tierp.model.bean.controleestoque.RequisicaoInternaCabecalho;
import com.t2tierp.model.bean.controleestoque.RequisicaoInternaDetalhe;
import com.t2tierp.model.dao.InterfaceDAO;
import com.t2tierp.util.FacesContextUtil;

@ManagedBean
@ViewScoped
public class RequisicaoInternaCabecalhoController extends AbstractController<RequisicaoInternaCabecalho> implements Serializable {

    private static final long serialVersionUID = 1L;
    @EJB
    private InterfaceDAO<Colaborador> colaboradorDao;
    @EJB
    private InterfaceDAO<Produto> produtoDao;

	private RequisicaoInternaDetalhe requisicaoInternaDetalhe;
	private RequisicaoInternaDetalhe requisicaoInternaDetalheSelecionado;

    @Override
    public Class<RequisicaoInternaCabecalho> getClazz() {
        return RequisicaoInternaCabecalho.class;
    }

    @Override
    public String getFuncaoBase() {
        return "REQUISICAO_INTERNA_CABECALHO";
    }

    @Override
    public void incluir() {
        super.incluir();
        getObjeto().setListaRequisicaoInternaDetalhe(new HashSet<RequisicaoInternaDetalhe>());
	}

    public void deferirRequisicao() {
    	alteraRequisição("D");
    }

    public void indeferirRequisicao() {
    	alteraRequisição("I");
    }
    
    private void alteraRequisição(String situacao) {
        try {
        	if (getObjeto().getListaRequisicaoInternaDetalhe().isEmpty()) {
        		FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_WARN, "Não há itens na requisição.", "");
            } else {
                if (getObjeto().getSituacao() != null && getObjeto().getSituacao().equals("D")) {
                	FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_WARN, "Esta requisição já foi deferida.\nNenhuma alteração realizada.", "");
                } else if (getObjeto().getSituacao() != null && getObjeto().getSituacao().equals("I")) {
                	FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_WARN, "Esta requisição já foi indeferida.\nNenhuma alteração realizada.", "");
                } else {
                    getObjeto().setSituacao(situacao);
                    String msg = situacao.equals("D") ? "Requisição deferida!" : "Requisição indeferida!";
                    salvar(msg);
                }
            }
        } catch (Exception e) {
        	FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro!", e.getMessage());
        }
    }
    
	public void incluirRequisicaoInternaDetalhe() {
        requisicaoInternaDetalhe = new RequisicaoInternaDetalhe();
        requisicaoInternaDetalhe.setRequisicaoInternaCabecalho(getObjeto());
	}

	public void alterarRequisicaoInternaDetalhe() {
        requisicaoInternaDetalhe = requisicaoInternaDetalheSelecionado;
	}

	public void salvarRequisicaoInternaDetalhe() {
        if (requisicaoInternaDetalhe.getId() == null) {
            getObjeto().getListaRequisicaoInternaDetalhe().add(requisicaoInternaDetalhe);
        }
        salvar("Registro salvo com sucesso!");
	}
	
	public void excluirRequisicaoInternaDetalhe() {
        if (requisicaoInternaDetalheSelecionado == null || requisicaoInternaDetalheSelecionado.getId() == null) {
            FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_INFO, "Nenhum registro selecionado!", null);
        } else {
            getObjeto().getListaRequisicaoInternaDetalhe().remove(requisicaoInternaDetalheSelecionado);
            salvar("Registro excluÃ­do com sucesso!");
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

    public List<Produto> getListaProduto(String nome) {
        List<Produto> listaProduto = new ArrayList<>();
        try {
            listaProduto = produtoDao.getBeansLike(Produto.class, "nome", nome);
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return listaProduto;
    }
    
    public RequisicaoInternaDetalhe getRequisicaoInternaDetalhe() {
        return requisicaoInternaDetalhe;
    }

    public void setRequisicaoInternaDetalhe(RequisicaoInternaDetalhe requisicaoInternaDetalhe) {
        this.requisicaoInternaDetalhe = requisicaoInternaDetalhe;
    }

	public RequisicaoInternaDetalhe getRequisicaoInternaDetalheSelecionado() {
		return requisicaoInternaDetalheSelecionado;
	}

	public void setRequisicaoInternaDetalheSelecionado(RequisicaoInternaDetalhe requisicaoInternaDetalheSelecionado) {
		this.requisicaoInternaDetalheSelecionado = requisicaoInternaDetalheSelecionado;
	}


}
