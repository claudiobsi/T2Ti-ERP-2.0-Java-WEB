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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;

import com.t2tierp.controller.AbstractController;
import com.t2tierp.model.bean.cadastros.Colaborador;
import com.t2tierp.model.bean.cadastros.Produto;
import com.t2tierp.model.bean.cadastros.ProdutoSubgrupo;
import com.t2tierp.model.bean.controleestoque.EstoqueReajusteCabecalho;
import com.t2tierp.model.bean.controleestoque.EstoqueReajusteDetalhe;
import com.t2tierp.model.dao.InterfaceDAO;
import com.t2tierp.util.FacesContextUtil;

@ManagedBean
@ViewScoped
public class EstoqueReajusteCabecalhoController extends AbstractController<EstoqueReajusteCabecalho> implements Serializable {

	private static final long serialVersionUID = 1L;
	@EJB
	private InterfaceDAO<Colaborador> colaboradorDao;
	@EJB
	private InterfaceDAO<ProdutoSubgrupo> produtoSubGrupoDao;
	@EJB
	private InterfaceDAO<Produto> produtoDao;

	private ProdutoSubgrupo produtoSubgrupo;

	@Override
	public Class<EstoqueReajusteCabecalho> getClazz() {
		return EstoqueReajusteCabecalho.class;
	}

	@Override
	public String getFuncaoBase() {
		return "ESTOQUE_REAJUSTE_CABECALHO";
	}

	@Override
	public void incluir() {
		super.incluir();
		getObjeto().setListaEstoqueReajusteDetalhe(new HashSet<EstoqueReajusteDetalhe>());

		produtoSubgrupo = new ProdutoSubgrupo();
	}

	@Override
	public void salvar() {
		efetuarCalculos();
		super.salvar();
	}
	
	public void buscaGrupoProdutos(SelectEvent event) {
		try {
			ProdutoSubgrupo subGrupo = (ProdutoSubgrupo) event.getObject();
			List<Produto> produtos = produtoDao.getBeans(Produto.class, "produtoSubgrupo", subGrupo);
			EstoqueReajusteDetalhe itensReajuste;
			for (int i = 0; i < produtos.size(); i++) {
				itensReajuste = new EstoqueReajusteDetalhe();
				itensReajuste.setEstoqueReajusteCabecalho(getObjeto());
				itensReajuste.setProduto(produtos.get(i));
				itensReajuste.setValorOriginal(produtos.get(i).getValorVenda());

				getObjeto().getListaEstoqueReajusteDetalhe().add(itensReajuste);
			}
			if (produtos.isEmpty()) {
				FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_INFO, "Nenhum produto encontrado para o grupo selecionado.", null);
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro.", e.getMessage());
		}
	}

	public void efetuarCalculos() {
		try {
			if (getObjeto().getListaEstoqueReajusteDetalhe().isEmpty()) {
				FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_INFO, "Nenhum item para calcular.", null);
			} else {
				for (EstoqueReajusteDetalhe d : getObjeto().getListaEstoqueReajusteDetalhe()) {
					if (d.getValorOriginal() != null) {
						if (getObjeto().getTipoReajuste().equals("A")) {
							d.setValorReajuste(d.getValorOriginal().multiply(BigDecimal.ONE.add(getObjeto().getPorcentagem().divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_DOWN))));
						} else {
							d.setValorReajuste(d.getValorOriginal().multiply(BigDecimal.ONE.subtract(getObjeto().getPorcentagem().divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_DOWN))));
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro.", e.getMessage());
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

	public List<ProdutoSubgrupo> getListaSubGrupo(String nome) {
		List<ProdutoSubgrupo> listaProdutoSubGrupo = new ArrayList<>();
		try {
			listaProdutoSubGrupo = produtoSubGrupoDao.getBeansLike(ProdutoSubgrupo.class, "nome", nome);
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return listaProdutoSubGrupo;
	}

	public ProdutoSubgrupo getProdutoSubgrupo() {
		return produtoSubgrupo;
	}

	public void setProdutoSubgrupo(ProdutoSubgrupo produtoSubgrupo) {
		this.produtoSubgrupo = produtoSubgrupo;
	}

}
