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
	
	private TreeNode rootContabilidade;
	private TreeNode funcaoSelecionada;
	private String pagina;
	private String tituloJanela;
	private boolean janelaVisivel = false;
	
	public FuncoesController() {  
        rootContabilidade = new DefaultTreeNode("root", null);  
        
        TreeNode contabilidade = new DefaultTreeNode(new Funcao("Contabilidade", null), rootContabilidade);
        
        TreeNode cadastros = new DefaultTreeNode(new Funcao("Cadastros", null), contabilidade);
        new DefaultTreeNode("document", new Funcao("Registro Cartório", "/modulos/contabilidade/registroCartorio.jsf"), cadastros);
        new DefaultTreeNode("document", new Funcao("Parâmetros", "/modulos/contabilidade/contabilParametro.jsf"), cadastros);
        new DefaultTreeNode("document", new Funcao("Índices", "/modulos/contabilidade/contabilIndice.jsf"), cadastros);
        new DefaultTreeNode("document", new Funcao("Históricos", "/modulos/contabilidade/contabilHistorico.jsf"), cadastros);
        new DefaultTreeNode("document", new Funcao("AIDF/AIMDF", "/modulos/contabilidade/aidfAimdf.jsf"), cadastros);
        new DefaultTreeNode("document", new Funcao("FAP", "/modulos/contabilidade/fap.jsf"), cadastros);
        
        TreeNode planoContas = new DefaultTreeNode(new Funcao("Plano de Contas", null), contabilidade);
        new DefaultTreeNode("document", new Funcao("Plano Contas", "/modulos/contabilidade/planoConta.jsf"), planoContas);
        new DefaultTreeNode("document", new Funcao("Plano Contas SPED", "/modulos/contabilidade/planoContaRefSped.jsf"), planoContas);
        new DefaultTreeNode("document", new Funcao("Conta Contábil", "/modulos/contabilidade/contabilConta.jsf"), planoContas);

        TreeNode lancamentos = new DefaultTreeNode(new Funcao("Lançamentos", null), contabilidade);
        new DefaultTreeNode("document", new Funcao("Fechamento", "/modulos/contabilidade/contabilFechamento.jsf"), lancamentos);
        new DefaultTreeNode("document", new Funcao("Lançamento Padrão", "/modulos/contabilidade/contabilLancamentoPadrao.jsf"), lancamentos);
        new DefaultTreeNode("document", new Funcao("Lançamento em Lote", "/modulos/contabilidade/contabilLote.jsf"), lancamentos);
        new DefaultTreeNode("document", new Funcao("Lançamento Orçado", "/modulos/contabilidade/contabilLancamentoOrcado.jsf"), lancamentos);
        new DefaultTreeNode("document", new Funcao("Lançamento Contabil", "/modulos/contabilidade/contabilLancamentoCabecalho.jsf"), lancamentos);
        
        TreeNode demonstrativos = new DefaultTreeNode(new Funcao("Demonstrativos", null), contabilidade);
        new DefaultTreeNode("document", new Funcao("DRE", "/modulos/contabilidade/contabilDreCabecalho.jsf"), demonstrativos);
        new DefaultTreeNode("document", new Funcao("DFC", "/modulos/contabilidade/contabilDfc.jsf"), demonstrativos);
        new DefaultTreeNode("document", new Funcao("Balanço Patrimonial", "/modulos/contabilidade/contabilBalancoPatrimonial.jsf"), demonstrativos);
        new DefaultTreeNode("document", new Funcao("Encerramento Exercício", "/modulos/contabilidade/contabilEncerramentoExeCab.jsf"), demonstrativos);
        
        TreeNode livros = new DefaultTreeNode(new Funcao("Livros Contábeis", null), contabilidade);
        new DefaultTreeNode("document", new Funcao("Livros e Termos", "/modulos/contabilidade/contabilLivro.jsf"), livros);
        new DefaultTreeNode("document", new Funcao("Emissão de Livros", "/modulos/contabilidade/contabilEmissaoLivro.jsf"), livros);
    }  
  
    public TreeNode getRootContabilidade() {  
        return rootContabilidade;  
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
