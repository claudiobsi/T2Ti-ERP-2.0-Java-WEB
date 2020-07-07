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
	
	private TreeNode rootPonto;
	private TreeNode funcaoSelecionada;
	private String pagina;
	private String tituloJanela;
	private boolean janelaVisivel = false;
	
	public FuncoesController() {  
        rootPonto = new DefaultTreeNode("root", null);  
        
        TreeNode ponto = new DefaultTreeNode(new Funcao("Ponto Eletrônico", null), rootPonto);
        new DefaultTreeNode("document", new Funcao("Parâmetros", "/modulos/ponto/pontoParametro.jsf"), ponto);
        new DefaultTreeNode("document", new Funcao("Horários", "/modulos/ponto/pontoHorario.jsf"), ponto);
        new DefaultTreeNode("document", new Funcao("Escalas e Turmas", "/modulos/ponto/pontoEscala.jsf"), ponto);
        new DefaultTreeNode("document", new Funcao("Classificação Jornada", "/modulos/ponto/pontoClassificacaoJornada.jsf"), ponto);
        new DefaultTreeNode("document", new Funcao("Abono", "/modulos/ponto/pontoAbono.jsf"), ponto);
        new DefaultTreeNode("document", new Funcao("Relógio", "/modulos/ponto/pontoRelogio.jsf"), ponto);
        new DefaultTreeNode("document", new Funcao("Banco de Horas", "/modulos/ponto/pontoBancoHoras.jsf"), ponto);
        new DefaultTreeNode("document", new Funcao("Importa Arquivo AFD", "/modulos/ponto/pontoImportaMarcacao.jsf"), ponto);
        new DefaultTreeNode("document", new Funcao("Geração de Arquivos", "/modulos/ponto/pontoGeracaoArquivo.jsf"), ponto);
        new DefaultTreeNode("document", new Funcao("Espelho Ponto Eletrônico", "/modulos/ponto/pontoEspelho.jsf"), ponto);
        new DefaultTreeNode("document", new Funcao("Registrar Marcação", "/modulos/ponto/pontoMarcacao.jsf"), ponto);
        
    }  
  
    public TreeNode getRootPonto() {  
        return rootPonto;  
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
