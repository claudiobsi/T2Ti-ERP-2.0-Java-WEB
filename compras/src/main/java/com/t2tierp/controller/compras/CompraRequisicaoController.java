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
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;

import com.t2tierp.controller.AbstractController;
import com.t2tierp.model.bean.cadastros.Colaborador;
import com.t2tierp.model.bean.cadastros.Produto;
import com.t2tierp.model.bean.compras.CompraRequisicao;
import com.t2tierp.model.bean.compras.CompraRequisicaoDetalhe;
import com.t2tierp.model.bean.compras.CompraTipoRequisicao;
import com.t2tierp.model.dao.InterfaceDAO;
import com.t2tierp.util.FacesContextUtil;

@ManagedBean
@ViewScoped
public class CompraRequisicaoController extends AbstractController<CompraRequisicao> implements Serializable {

    private static final long serialVersionUID = 1L;
	@EJB
	private InterfaceDAO<CompraTipoRequisicao> compraTipoRequisicaoDao;
	@EJB
	private InterfaceDAO<Colaborador> colaboradorDao;
	@EJB
	private InterfaceDAO<Produto> produtoDao;
	
	private CompraRequisicaoDetalhe detalheSelecionado;
	private CompraRequisicaoDetalhe compraRequisicaoDetalhe;
	
	private Boolean parametroCompraSugerida;
	@Inject
	private CompraSugeridaController compraSugeridaController;
	private Set<CompraRequisicaoDetalhe> listaCompraSugerida;

    @Override
    public Class<CompraRequisicao> getClazz() {
        return CompraRequisicao.class;
    }

    @Override
    public String getFuncaoBase() {
        return "COMPRA_REQUISICAO";
    }

    @Override
    public void incluir() {
    	super.incluir();
    	getObjeto().setDataRequisicao(new Date());
    	getObjeto().setListaCompraRequisicaoDetalhe(new HashSet<CompraRequisicaoDetalhe>());
    }
    
    @Override
    public void salvar() {
    	if (parametroCompraSugerida != null) {
    		if (getObjeto().getId() == null) {
    			for (CompraRequisicaoDetalhe d : listaCompraSugerida) {
    				d.setCompraRequisicao(getObjeto());
    			}
    			getObjeto().setListaCompraRequisicaoDetalhe(listaCompraSugerida);
    			parametroCompraSugerida = null;
    		}
    	}
    	super.salvar();
    }
    
    public void incluirItemRequisicao() {
    	compraRequisicaoDetalhe = new CompraRequisicaoDetalhe();
    	compraRequisicaoDetalhe.setCompraRequisicao(getObjeto());
    	compraRequisicaoDetalhe.setItemCotado("N");
    	compraRequisicaoDetalhe.setQuantidadeCotada(BigDecimal.ZERO);
    }
    
	public void alterarItemRequisicao() {
		compraRequisicaoDetalhe = detalheSelecionado;
	}
	
	public void salvarItemRequisicao() {
		if (compraRequisicaoDetalhe.getId() == null) {
			getObjeto().getListaCompraRequisicaoDetalhe().add(compraRequisicaoDetalhe);
		}
		salvar("Item salvo com sucesso!");
	}
	
	public void excluirItemRequisicao() {
		if (detalheSelecionado == null || detalheSelecionado.getId() == null) {
			FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_INFO, "Nenhum registro selecionado!", null);
		} else {
			getObjeto().getListaCompraRequisicaoDetalhe().remove(detalheSelecionado);
			salvar("Item excluído com sucesso!");
		}
	}
    
	public void carregaCompraSugerida() {
		try {
			if (parametroCompraSugerida != null) {
				listaCompraSugerida = compraSugeridaController.geraRequisicao();
				incluir();
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_ERROR, "Erro ao carregar a compra sugerida!", e.getMessage());
		}
	}
	
	public List<CompraTipoRequisicao> getListaCompraTipoRequisicao(String nome) {
		List<CompraTipoRequisicao> listaCompraTipoRequisicao = new ArrayList<>();
		try {
			listaCompraTipoRequisicao = compraTipoRequisicaoDao.getBeansLike(CompraTipoRequisicao.class, "nome", nome);
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return listaCompraTipoRequisicao;
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
	
	public CompraRequisicaoDetalhe getDetalheSelecionado() {
		return detalheSelecionado;
	}

	public void setDetalheSelecionado(CompraRequisicaoDetalhe detalheSelecionado) {
		this.detalheSelecionado = detalheSelecionado;
	}

	public CompraRequisicaoDetalhe getCompraRequisicaoDetalhe() {
		return compraRequisicaoDetalhe;
	}

	public void setCompraRequisicaoDetalhe(CompraRequisicaoDetalhe compraRequisicaoDetalhe) {
		this.compraRequisicaoDetalhe = compraRequisicaoDetalhe;
	}

	public Boolean getParametroCompraSugerida() {
		return parametroCompraSugerida;
	}

	public void setParametroCompraSugerida(Boolean parametroCompraSugerida) {
		this.parametroCompraSugerida = parametroCompraSugerida;
	}    
	
}
