package com.t2tierp.controller;

import java.io.Serializable;

import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;

import org.primefaces.event.NodeSelectEvent;
import org.primefaces.model.DefaultTreeNode;
import org.primefaces.model.TreeNode;

@ManagedBean
@SessionScoped
public class FuncoesController implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private TreeNode rootCadastros;
	private TreeNode funcaoSelecionada;
	private String pagina;
	private String tituloJanela;
	private boolean janelaVisivel = false;
	
	public FuncoesController() {  
        rootCadastros = new DefaultTreeNode("root", null);  
  
        /*TreeNode produto = new DefaultTreeNode(new Funcao("Produto", null), rootCadastros);  
        new DefaultTreeNode("document", new Funcao("Marca", "/cadastros/marca.xhtml"), produto);*/
        
        TreeNode financeiro = new DefaultTreeNode(new Funcao("Financeiro", null), rootCadastros);
        new DefaultTreeNode("document", new Funcao("Banco", "/modulos/cadastros/banco.jsf"), financeiro);
        new DefaultTreeNode("document", new Funcao("Agência Banco", "/modulos/cadastros/agenciaBanco.jsf"), financeiro);
    }  
  
    public TreeNode getRootCadastros() {  
        return rootCadastros;  
    }
    
	public void onNodeSelect(NodeSelectEvent event) {
    	pagina = ((Funcao) event.getTreeNode().getData()).getPagina();
    	tituloJanela = ((Funcao) event.getTreeNode().getData()).getNome();
    	janelaVisivel = true;
    }      

    public TreeNode getFuncaoSelecionada() {
		return funcaoSelecionada;
	}

	public void setFuncaoSelecionada(TreeNode funcaoSelecionada) {
		this.funcaoSelecionada = funcaoSelecionada;
	}

	public String getPagina() {
		return pagina;
	}

	public String getTituloJanela() {
		return tituloJanela;
	}

	public boolean isJanelaVisivel() {
		return janelaVisivel;
	}
	
}
