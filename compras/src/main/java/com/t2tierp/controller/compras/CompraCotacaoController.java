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
package com.t2tierp.controller.compras;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.t2tierp.controller.AbstractController;
import com.t2tierp.model.bean.cadastros.Fornecedor;
import com.t2tierp.model.bean.compras.CompraCotacao;
import com.t2tierp.model.bean.compras.CompraCotacaoDetalhe;
import com.t2tierp.model.bean.compras.CompraFornecedorCotacao;
import com.t2tierp.model.bean.compras.CompraReqCotacaoDetalhe;
import com.t2tierp.model.bean.compras.CompraRequisicaoDetalhe;
import com.t2tierp.model.dao.Filtro;
import com.t2tierp.model.dao.InterfaceDAO;
import com.t2tierp.util.FacesContextUtil;

@ManagedBean
@ViewScoped
public class CompraCotacaoController extends AbstractController<CompraCotacao> implements Serializable {

	private static final long serialVersionUID = 1L;

	@EJB
	private InterfaceDAO<Fornecedor> fornecedorDao;
	private CompraFornecedorCotacao compraFornecedorCotacao;

	@EJB
	private InterfaceDAO<CompraCotacaoDetalhe> compraCotacaoDetalheDao;
	private CompraCotacaoDetalhe compraCotacaoDetalhe;
	private Set<CompraCotacaoDetalhe> listaCompraCotacaoDetalhe;

	@EJB
	private InterfaceDAO<CompraRequisicaoDetalhe> compraRequisicaoDetalheDao;
	private CompraRequisicaoDetalhe compraRequisicaoDetalhe;

	@Override
	public Class<CompraCotacao> getClazz() {
		return CompraCotacao.class;
	}

	@Override
	public String getFuncaoBase() {
		return "COMPRA_COTACAO";
	}

	@Override
	public void incluir() {
		super.incluir();
		getObjeto().setListaCompraFornecedorCotacao(new HashSet<CompraFornecedorCotacao>());
		getObjeto().setListaCompraReqCotacaoDetalhe(new HashSet<CompraReqCotacaoDetalhe>());
		getObjeto().setSituacao("A");
		listaCompraCotacaoDetalhe = new HashSet<>();
	}
	
	@Override
	public void alterar() {
		super.alterar();

		try {
			CompraFornecedorCotacao fornecedorCotacao = null;
			for (CompraFornecedorCotacao f : getObjeto().getListaCompraFornecedorCotacao()) {
				fornecedorCotacao = f;
			}
			
			List<Filtro> filtros = new ArrayList<>();
			filtros.add(new Filtro(Filtro.AND, "compraFornecedorCotacao", Filtro.IGUAL, fornecedorCotacao));
			listaCompraCotacaoDetalhe = new HashSet<CompraCotacaoDetalhe>(compraCotacaoDetalheDao.getBeans(CompraCotacaoDetalhe.class, filtros));
		} catch (Exception e) {
			e.printStackTrace();
			FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_ERROR, "Erro ao buscar os itens da cotação" ,e.getMessage());
		}
	}
	
	@Override
	public void salvar() {
		try {
			if (getObjeto().getId() != null) {
				throw new Exception("Não é possível alterar uma cotação!");
			}
			
			if (getObjeto().getListaCompraFornecedorCotacao().isEmpty()) {
				throw new Exception("É necessário incluir um fornecedor para cotação!");
			}

			if (listaCompraCotacaoDetalhe.isEmpty()) {
				throw new Exception("É necessário incluir itens para cotação!");
			}

			for (CompraCotacaoDetalhe d : listaCompraCotacaoDetalhe) {
				CompraReqCotacaoDetalhe compraReqCotacaoDetalhe = new CompraReqCotacaoDetalhe();
                compraReqCotacaoDetalhe.setCompraCotacao(getObjeto());
                compraReqCotacaoDetalhe.setCompraRequisicaoDetalhe(d.getCompraRequisicaoDetalhe());
                compraReqCotacaoDetalhe.setQuantidadeCotada(d.getQuantidade());
                
                getObjeto().getListaCompraReqCotacaoDetalhe().add(compraReqCotacaoDetalhe);
			}

			for (CompraFornecedorCotacao f : getObjeto().getListaCompraFornecedorCotacao()) {
				HashSet<CompraCotacaoDetalhe> listaDetalhe = new HashSet<>(); 
				
				for (CompraCotacaoDetalhe d : listaCompraCotacaoDetalhe) {
					CompraCotacaoDetalhe ccd = new CompraCotacaoDetalhe();
					ccd.setCompraRequisicaoDetalhe(d.getCompraRequisicaoDetalhe());
					ccd.setCompraFornecedorCotacao(f);
					ccd.setProduto(d.getProduto());
					ccd.setQuantidade(d.getQuantidade());
					
					listaDetalhe.add(ccd);
				}
				
				f.setListaCompraCotacaoDetalhe(listaDetalhe);
			}
			
			super.salvar();
		} catch(Exception e) {
			FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_ERROR, e.getMessage(), null);
		}
	}

	public void incluirFornecedor() {
		compraFornecedorCotacao = new CompraFornecedorCotacao();
		compraFornecedorCotacao.setCompraCotacao(getObjeto());
	}

	public void salvarFornecedor() {
		getObjeto().getListaCompraFornecedorCotacao().add(compraFornecedorCotacao);
		FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_INFO, "Fornecedor incluído com sucesso!", null);
	}

	public void incluirItem() {
		compraCotacaoDetalhe = new CompraCotacaoDetalhe();
		compraRequisicaoDetalhe = new CompraRequisicaoDetalhe();
	}

	public void salvarItem() {
		try {
			if ((compraRequisicaoDetalhe.getQuantidade().subtract(compraRequisicaoDetalhe.getQuantidadeCotada())).compareTo(compraCotacaoDetalhe.getQuantidade()) == -1) {
				FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_WARN, "A quantidade informada excede a quantidade disponível para cotação!", null);
			} else {
				compraCotacaoDetalhe.setProduto(compraRequisicaoDetalhe.getProduto());
				listaCompraCotacaoDetalhe.add(compraCotacaoDetalhe);

				compraRequisicaoDetalhe.setQuantidadeCotada(compraRequisicaoDetalhe.getQuantidadeCotada().add(compraCotacaoDetalhe.getQuantidade()));
				if (compraRequisicaoDetalhe.getQuantidade().compareTo(compraRequisicaoDetalhe.getQuantidadeCotada()) == 0) {
					compraRequisicaoDetalhe.setItemCotado("S");
				}

				compraRequisicaoDetalheDao.merge(compraRequisicaoDetalhe);
				
				compraCotacaoDetalhe.setCompraRequisicaoDetalhe(compraRequisicaoDetalhe);
				
				FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_INFO, "Item incluído com sucesso!", null);
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro na inclusão do item", e.getMessage());
		}
	}

	public List<Fornecedor> getListaFornecedor(String nome) {
		List<Fornecedor> listaFornecedor = new ArrayList<>();
		try {
			listaFornecedor = fornecedorDao.getBeansLike(Fornecedor.class, "pessoa.nome", nome);
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return listaFornecedor;
	}

	public List<CompraRequisicaoDetalhe> getListaItensRequisicao(String nome) {
		List<CompraRequisicaoDetalhe> listaCompraRequisicaoDetalhe = new ArrayList<>();
		try {
			listaCompraRequisicaoDetalhe = compraRequisicaoDetalheDao.getBeans(CompraRequisicaoDetalhe.class, "itemCotado", "N");
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return listaCompraRequisicaoDetalhe;
	}

	public CompraFornecedorCotacao getCompraFornecedorCotacao() {
		return compraFornecedorCotacao;
	}

	public void setCompraFornecedorCotacao(CompraFornecedorCotacao compraFornecedorCotacao) {
		this.compraFornecedorCotacao = compraFornecedorCotacao;
	}

	public Set<CompraCotacaoDetalhe> getListaCompraCotacaoDetalhe() {
		return listaCompraCotacaoDetalhe;
	}

	public void setListaCompraCotacaoDetalhe(Set<CompraCotacaoDetalhe> listaCompraCotacaoDetalhe) {
		this.listaCompraCotacaoDetalhe = listaCompraCotacaoDetalhe;
	}

	public CompraCotacaoDetalhe getCompraCotacaoDetalhe() {
		return compraCotacaoDetalhe;
	}

	public void setCompraCotacaoDetalhe(CompraCotacaoDetalhe compraCotacaoDetalhe) {
		this.compraCotacaoDetalhe = compraCotacaoDetalhe;
	}

	public CompraRequisicaoDetalhe getCompraRequisicaoDetalhe() {
		return compraRequisicaoDetalhe;
	}

	public void setCompraRequisicaoDetalhe(CompraRequisicaoDetalhe compraRequisicaoDetalhe) {
		this.compraRequisicaoDetalhe = compraRequisicaoDetalhe;
	}

}
