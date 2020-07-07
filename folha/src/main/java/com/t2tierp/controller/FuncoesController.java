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
	
	private TreeNode rootFolha;
	private TreeNode funcaoSelecionada;
	private String pagina;
	private String tituloJanela;
	private boolean janelaVisivel = false;
	
	public FuncoesController() {  
        rootFolha = new DefaultTreeNode("root", null);  
        
        TreeNode folha = new DefaultTreeNode(new Funcao("Ponto Eletrônico", null), rootFolha);
        
        TreeNode cadastros = new DefaultTreeNode(new Funcao("Cadastros", null), folha);
        new DefaultTreeNode("document", new Funcao("Parâmetros", "/modulos/folha-pagamento/folhaParametro.jsf"), cadastros);
        new DefaultTreeNode("document", new Funcao("Guias Acumuladas", "/modulos/folha-pagamento/guiasAcumuladas.jsf"), cadastros);
        new DefaultTreeNode("document", new Funcao("Plano de Saúde", "/modulos/folha-pagamento/folhaPlanoSaude.jsf"), cadastros);
        new DefaultTreeNode("document", new Funcao("Eventos", "/modulos/folha-pagamento/folhaEvento.jsf"), cadastros);
        
        TreeNode ausencias = new DefaultTreeNode(new Funcao("Ausências", null), folha);
        new DefaultTreeNode("document", new Funcao("Tipos de Afastamento", "/modulos/folha-pagamento/folhaTipoAfastamento.jsf"), ausencias);
        new DefaultTreeNode("document", new Funcao("Afastamentos", "/modulos/folha-pagamento/folhaAfastamento.jsf"), ausencias);
        new DefaultTreeNode("document", new Funcao("Férias Coletivas", "/modulos/folha-pagamento/folhaFeriasColetivas.jsf"), ausencias);
        new DefaultTreeNode("document", new Funcao("Férias - Período Aquisitivo", "/modulos/folha-pagamento/feriasPeriodoAquisitivo.jsf"), ausencias);
        
        TreeNode movimento = new DefaultTreeNode(new Funcao("Movimento", null), folha);
        new DefaultTreeNode("document", new Funcao("Fechamento", "/modulos/folha-pagamento/folhaFechamento.jsf"), movimento);
        new DefaultTreeNode("document", new Funcao("Lançamentos", "/modulos/folha-pagamento/folhaLancamentoCabecalho.jsf"), movimento);
        new DefaultTreeNode("document", new Funcao("Alteração Salarial", "/modulos/folha-pagamento/folhaHistoricoSalarial.jsf"), movimento);
        new DefaultTreeNode("document", new Funcao("Vale Transporte", "/modulos/folha-pagamento/folhaValeTransporte.jsf"), movimento);
        new DefaultTreeNode("document", new Funcao("PPP", "/modulos/folha-pagamento/folhaPpp.jsf"), movimento);
        new DefaultTreeNode("document", new Funcao("Rescisão", "/modulos/folha-pagamento/folhaRescisao.jsf"), movimento);
        new DefaultTreeNode("document", new Funcao("EDI - Folha", "/modulos/folha-pagamento/folhaEDI.jsf"), movimento);
        
        TreeNode inss = new DefaultTreeNode(new Funcao("INSS", null), folha);
        new DefaultTreeNode("document", new Funcao("Serviços", "/modulos/folha-pagamento/folhaInssServico.jsf"), inss);
        new DefaultTreeNode("document", new Funcao("Retenções", "/modulos/folha-pagamento/folhaInss.jsf"), inss);
        
    }  
  
    public TreeNode getRootFolha() {  
        return rootFolha;  
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
