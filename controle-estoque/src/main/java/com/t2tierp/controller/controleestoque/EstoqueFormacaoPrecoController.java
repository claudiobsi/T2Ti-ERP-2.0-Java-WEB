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
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.t2tierp.controller.AbstractController;
import com.t2tierp.model.bean.cadastros.Produto;
import com.t2tierp.model.bean.cadastros.ProdutoSubgrupo;
import com.t2tierp.model.dao.InterfaceDAO;
import com.t2tierp.util.Biblioteca;
import com.t2tierp.util.FacesContextUtil;

@ManagedBean
@ViewScoped
public class EstoqueFormacaoPrecoController extends AbstractController<ProdutoSubgrupo> implements Serializable {

	private static final long serialVersionUID = 1L;
	@EJB
	private InterfaceDAO<Produto> produtoDao;

	private BigDecimal encargos;
	private BigDecimal markup;
	private List<Produto> listaProduto;
	
	@Override
	public Class<ProdutoSubgrupo> getClazz() {
		return ProdutoSubgrupo.class;
	}

	@Override
	public String getFuncaoBase() {
		return "ESTOQUE_FORMACAO_PRECO";
	}
	
	@Override
	public void alterar() {
		super.alterar();
		buscaGrupoProdutos();
	}
	
	public void buscaGrupoProdutos() {
		try {
			listaProduto = produtoDao.getBeans(Produto.class, "produtoSubgrupo", getObjeto());
		} catch (Exception e) {
			FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro.", e.getMessage());
		}
	}

	public void efetuarCalculos() {
		try {
	        /*
	        C = Valor Compra
	        E = % de encargos sobre vendas
	        M = % markup  sobre o custo
	        P = preço de venda
	        
	        P = C(1 + M) ÷ (1 - E)
	        */
	        if (encargos == null) {
	        	FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_INFO, "Informe os encargos da venda.", "");
	            return;
	        }
	        if (markup == null) {
	        	FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_INFO, "Informe o markup.", "");
	            return;
	        }

	        if (listaProduto.isEmpty()) {
	        	FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_INFO, "Nenhum item para formar preço.", "");
	            return;
	        }
	        BigDecimal valorVenda;
	        BigDecimal encargo = Biblioteca.divide(this.markup, BigDecimal.valueOf(100));
	        BigDecimal markup = Biblioteca.divide(this.encargos, BigDecimal.valueOf(100));
	        for (Produto p : listaProduto) {
	            if (p.getValorCompra() != null) {
	                valorVenda = p.getValorCompra().multiply(BigDecimal.ONE.add(markup)).divide(BigDecimal.ONE.subtract(encargo), 2, RoundingMode.DOWN);

	                p.setValorVenda(valorVenda);
	                p.setMarkup(markup);
	                p.setEncargosVenda(encargo);
	            }
	        }
	        FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_INFO, "Cálculos Efetuados.", null);
		} catch (Exception e) {
			e.printStackTrace();
			FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro.", e.getMessage());
		}
	}

	public void salvarCalculos() {
		try {
	        if (listaProduto.isEmpty()) {
	        	FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_INFO, "Nenhum produto na lista", "");
	        } else {
	        	for (Produto p : listaProduto) {
	        		produtoDao.merge(p);
	        	}
	        }
	        FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_INFO, "Preços formados com sucesso!", "");
		} catch (Exception e) {
			e.printStackTrace();
			FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro.", e.getMessage());
		}
	}
	
	public BigDecimal getEncargos() {
		return encargos;
	}

	public void setEncargos(BigDecimal encargos) {
		this.encargos = encargos;
	}

	public BigDecimal getMarkup() {
		return markup;
	}

	public void setMarkup(BigDecimal markup) {
		this.markup = markup;
	}

	public List<Produto> getListaProduto() {
		return listaProduto;
	}

	public void setListaProduto(List<Produto> listaProduto) {
		this.listaProduto = listaProduto;
	}

}
