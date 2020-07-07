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
package com.t2tierp.controller.inventario;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;

import com.t2tierp.controller.AbstractController;
import com.t2tierp.model.bean.cadastros.Produto;
import com.t2tierp.model.bean.cadastros.ProdutoSubgrupo;
import com.t2tierp.model.bean.iventario.InventarioContagemCab;
import com.t2tierp.model.bean.iventario.InventarioContagemDet;
import com.t2tierp.model.dao.InterfaceDAO;
import com.t2tierp.util.Biblioteca;
import com.t2tierp.util.FacesContextUtil;

@ManagedBean
@ViewScoped
public class InventarioContagemCabController extends AbstractController<InventarioContagemCab> implements Serializable {

	private static final long serialVersionUID = 1L;
	@EJB
	private InterfaceDAO<Produto> produtoDao;
	@EJB
	private InterfaceDAO<ProdutoSubgrupo> produtoSubGrupoDao;

	private InventarioContagemDet inventarioContagemDet;
	private InventarioContagemDet inventarioContagemDetSelecionado;

	private ProdutoSubgrupo produtoSubgrupo;

	@Override
	public Class<InventarioContagemCab> getClazz() {
		return InventarioContagemCab.class;
	}

	@Override
	public String getFuncaoBase() {
		return "INVENTARIO_CONTAGEM_CAB";
	}

	@Override
	public void incluir() {
		super.incluir();
		getObjeto().setEmpresa(FacesContextUtil.getEmpresaUsuario());
		getObjeto().setListaInventarioContagemDet(new HashSet<InventarioContagemDet>());
		produtoSubgrupo = new ProdutoSubgrupo();
	}

	@Override
	public void salvar() {
		try {
			super.salvar();
			if (getObjeto().getEstoqueAtualizado().equals("S")) {
				for (InventarioContagemDet det : getObjeto().getListaInventarioContagemDet()) {
					// EXERCICIO
					// Verifique qual dos três campos foi selecionado para o fechamento da contagem (FECHADO_CONTAGEM)
					// e atualize o estoque de acordo.            
					det.getProduto().setQuantidadeEstoque(det.getContagem01());
					produtoDao.merge(det.getProduto());
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro.", e.getMessage());
		}
	}

	public void alterarInventarioContagemDet() {
		inventarioContagemDet = inventarioContagemDetSelecionado;
	}

	public void salvarInventarioContagemDet() {
		if (inventarioContagemDet.getId() == null) {
			getObjeto().getListaInventarioContagemDet().add(inventarioContagemDet);
		}
		efetuarCalculos();
	}

	public void buscaGrupoProdutos(SelectEvent event) {
		try {
			ProdutoSubgrupo subGrupo = (ProdutoSubgrupo) event.getObject();
			List<Produto> produtos = produtoDao.getBeans(Produto.class, "produtoSubgrupo", subGrupo);
			for (int i = 0; i < produtos.size(); i++) {
				InventarioContagemDet item = new InventarioContagemDet();
				item.setInventarioContagemCab(getObjeto());
				item.setProduto(produtos.get(i));
				item.setQuantidadeSistema(produtos.get(i).getQuantidadeEstoque());

				getObjeto().getListaInventarioContagemDet().add(item);
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
			if (getObjeto().getListaInventarioContagemDet().isEmpty()) {
				FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_WARN, "Nenhum item para calcular.", "");
			} else {
				for (InventarioContagemDet item : getObjeto().getListaInventarioContagemDet()) {
					// EXERCICIO
					// Verifique qual dos três campos foi selecionado para o fechamento da contagem (FECHADO_CONTAGEM)
					// e realize o calculo com base nesse campo            
					if (item.getContagem01() != null && item.getQuantidadeSistema() != null) {
						//acuracidade dos registros = (registros sistema / registros contados) X 100 }
						if (item.getContagem01().compareTo(BigDecimal.ZERO) != 0) {
							item.setAcuracidade(Biblioteca.multiplica(Biblioteca.divide(item.getQuantidadeSistema(), item.getContagem01()), BigDecimal.valueOf(100)));
						} else {
							item.setAcuracidade(BigDecimal.ZERO);
						}
						//divergencia dos registros = ((registros contados - registros sistema) / registros sistema) X 100 }
						item.setDivergencia(Biblioteca.multiplica(Biblioteca.divide(Biblioteca.subtrai(item.getContagem01(), item.getQuantidadeSistema()), item.getQuantidadeSistema()), BigDecimal.valueOf(100)));
					}

					if (item.getQuantidadeSistema() != null) {
						if (item.getContagem01() != null && (item.getContagem01().compareTo(item.getQuantidadeSistema()) == 0)) {
							item.setFechadoContagem("01");
						} else if (item.getContagem02() != null && (item.getContagem02().compareTo(item.getQuantidadeSistema()) == 0)) {
							item.setFechadoContagem("02");
						} else if (item.getContagem03() != null && (item.getContagem03().compareTo(item.getQuantidadeSistema()) == 0)) {
							item.setFechadoContagem("03");
						} else {
							item.setFechadoContagem("XX");
						}
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro.", e.getMessage());
		}
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

	public InventarioContagemDet getInventarioContagemDet() {
		return inventarioContagemDet;
	}

	public void setInventarioContagemDet(InventarioContagemDet inventarioContagemDet) {
		this.inventarioContagemDet = inventarioContagemDet;
	}

	public InventarioContagemDet getInventarioContagemDetSelecionado() {
		return inventarioContagemDetSelecionado;
	}

	public void setInventarioContagemDetSelecionado(InventarioContagemDet inventarioContagemDetSelecionado) {
		this.inventarioContagemDetSelecionado = inventarioContagemDetSelecionado;
	}

	public ProdutoSubgrupo getProdutoSubgrupo() {
		return produtoSubgrupo;
	}

	public void setProdutoSubgrupo(ProdutoSubgrupo produtoSubgrupo) {
		this.produtoSubgrupo = produtoSubgrupo;
	}

}
