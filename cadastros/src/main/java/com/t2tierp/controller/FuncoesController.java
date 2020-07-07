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
	
	private TreeNode rootCadastros;
	private TreeNode funcaoSelecionada;
	private String pagina;
	private String tituloJanela;
	private boolean janelaVisivel = false;
	
	public FuncoesController() {  
        rootCadastros = new DefaultTreeNode("root", null);  
        
        TreeNode pessoal = new DefaultTreeNode(new Funcao("Pessoal", null), rootCadastros);
        
        TreeNode pessoa = new DefaultTreeNode(new Funcao("Pessoa", null), pessoal);
        new DefaultTreeNode("document", new Funcao("Pessoa", "/modulos/cadastros/pessoal/pessoa/pessoa.jsf"), pessoa);
        new DefaultTreeNode("document", new Funcao("Estado Civil", "/modulos/cadastros/pessoal/pessoa/estadoCivil.jsf"), pessoa);
        
        TreeNode clienteFornecedorTransportadora = new DefaultTreeNode(new Funcao("Cliente | Fornecedor | Transportadora", null), pessoal);
        new DefaultTreeNode("document", new Funcao("Atividade", "/modulos/cadastros/pessoal/clienteFornecedorTransportadora/atividadeForCli.jsf"), clienteFornecedorTransportadora);
        new DefaultTreeNode("document", new Funcao("Situação", "/modulos/cadastros/pessoal/clienteFornecedorTransportadora/situacaoForCli.jsf"), clienteFornecedorTransportadora);
        new DefaultTreeNode("document", new Funcao("Cliente", "/modulos/cadastros/pessoal/clienteFornecedorTransportadora/cliente.jsf"), clienteFornecedorTransportadora);
        new DefaultTreeNode("document", new Funcao("Fornecedor", "/modulos/cadastros/pessoal/clienteFornecedorTransportadora/fornecedor.jsf"), clienteFornecedorTransportadora);
        new DefaultTreeNode("document", new Funcao("Transportadora", "/modulos/cadastros/pessoal/clienteFornecedorTransportadora/transportadora.jsf"), clienteFornecedorTransportadora);
        
        TreeNode colaborador = new DefaultTreeNode(new Funcao("Colaborador", null), pessoal);
        new DefaultTreeNode("document", new Funcao("Tipo Admissão", "/modulos/cadastros/pessoal/colaborador/tipoAdmissao.jsf"), colaborador);
        new DefaultTreeNode("document", new Funcao("Tipo Relacionamento", "/modulos/cadastros/pessoal/colaborador/tipoRelacionamento.jsf"), colaborador);
        new DefaultTreeNode("document", new Funcao("Situação", "/modulos/cadastros/pessoal/colaborador/situacaoColaborador.jsf"), colaborador);
        new DefaultTreeNode("document", new Funcao("Tipo Desligamento", "/modulos/cadastros/pessoal/colaborador/tipoDesligamento.jsf"), colaborador);
        new DefaultTreeNode("document", new Funcao("Tipo", "/modulos/cadastros/pessoal/colaborador/tipoColaborador.jsf"), colaborador);
        new DefaultTreeNode("document", new Funcao("Cargo", "/modulos/cadastros/pessoal/colaborador/cargo.jsf"), colaborador);
        new DefaultTreeNode("document", new Funcao("Nível Formação", "/modulos/cadastros/pessoal/colaborador/nivelFormacao.jsf"), colaborador);
        new DefaultTreeNode("document", new Funcao("Colaborador", "/modulos/cadastros/pessoal/colaborador/colaborador.jsf"), colaborador);

        TreeNode pessoalOutros = new DefaultTreeNode(new Funcao("Outros", null), pessoal);
        new DefaultTreeNode("document", new Funcao("Contador", "/modulos/cadastros/pessoal/outros/contador.jsf"), pessoalOutros);
        new DefaultTreeNode("document", new Funcao("Sindicato", "/modulos/cadastros/pessoal/outros/sindicato.jsf"), pessoalOutros);
        new DefaultTreeNode("document", new Funcao("Convênio", "/modulos/cadastros/pessoal/outros/convenio.jsf"), pessoalOutros);
        
        TreeNode diversos = new DefaultTreeNode(new Funcao("Diversos", null), rootCadastros);
        new DefaultTreeNode("document", new Funcao("Setor", "/modulos/cadastros/diversos/setor.jsf"), diversos);
        new DefaultTreeNode("document", new Funcao("Almoxarifado", "/modulos/cadastros/diversos/almoxarifado.jsf"), diversos);
        new DefaultTreeNode("document", new Funcao("Operadora Plano Saúde", "/modulos/cadastros/diversos/operadoraPlanoSaude.jsf"), diversos);
        new DefaultTreeNode("document", new Funcao("Operadora Cartão", "/modulos/cadastros/diversos/operadoraCartao.jsf"), diversos);
        
        TreeNode endereco = new DefaultTreeNode(new Funcao("Endereço", null), rootCadastros);
        new DefaultTreeNode("document", new Funcao("País", "/modulos/cadastros/endereco/pais.jsf"), endereco);
        new DefaultTreeNode("document", new Funcao("Estado", "/modulos/cadastros/endereco/uf.jsf"), endereco);
        new DefaultTreeNode("document", new Funcao("Município", "/modulos/cadastros/endereco/municipio.jsf"), endereco);
        new DefaultTreeNode("document", new Funcao("CEP", "/modulos/cadastros/endereco/cep.jsf"), endereco);
        
        TreeNode produto = new DefaultTreeNode(new Funcao("Produto", null), rootCadastros);
        new DefaultTreeNode("document", new Funcao("Marca", "/modulos/cadastros/produto/produtoMarca.jsf"), produto);
        new DefaultTreeNode("document", new Funcao("NCM", "/modulos/cadastros/produto/ncm.jsf"), produto);
        new DefaultTreeNode("document", new Funcao("Unidade", "/modulos/cadastros/produto/unidadeProduto.jsf"), produto);
        new DefaultTreeNode("document", new Funcao("Grupo", "/modulos/cadastros/produto/produtoGrupo.jsf"), produto);
        new DefaultTreeNode("document", new Funcao("SubGrupo", "/modulos/cadastros/produto/produtoSubGrupo.jsf"), produto);
        new DefaultTreeNode("document", new Funcao("Produto", "/modulos/cadastros/produto/produto.jsf"), produto);
        
        TreeNode financeiro = new DefaultTreeNode(new Funcao("Financeiro", null), rootCadastros);
        new DefaultTreeNode("document", new Funcao("Banco", "/modulos/cadastros/financeiro/banco.jsf"), financeiro);
        new DefaultTreeNode("document", new Funcao("Agência Banco", "/modulos/cadastros/financeiro/agenciaBanco.jsf"), financeiro);
        new DefaultTreeNode("document", new Funcao("Conta Caixa", "/modulos/cadastros/financeiro/contaCaixa.jsf"), financeiro);
        new DefaultTreeNode("document", new Funcao("Talonário Cheque", "/modulos/cadastros/financeiro/talonarioCheque.jsf"), financeiro);
        new DefaultTreeNode("document", new Funcao("Cheque", "/modulos/cadastros/financeiro/cheque.jsf"), financeiro);
        
        TreeNode tabelas = new DefaultTreeNode(new Funcao("Tabelas", null), rootCadastros);
        new DefaultTreeNode("document", new Funcao("CFOP", "/modulos/cadastros/tabelas/cfop.jsf"), tabelas);
        new DefaultTreeNode("document", new Funcao("Feriados", "/modulos/cadastros/tabelas/feriados.jsf"), tabelas);
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
