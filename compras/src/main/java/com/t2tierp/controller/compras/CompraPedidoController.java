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
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;

import com.t2tierp.controller.AbstractController;
import com.t2tierp.model.bean.cadastros.Fornecedor;
import com.t2tierp.model.bean.cadastros.Produto;
import com.t2tierp.model.bean.compras.CompraPedido;
import com.t2tierp.model.bean.compras.CompraPedidoDetalhe;
import com.t2tierp.model.bean.compras.CompraTipoPedido;
import com.t2tierp.model.dao.InterfaceDAO;
import com.t2tierp.util.FacesContextUtil;

@ManagedBean
@ViewScoped
public class CompraPedidoController extends AbstractController<CompraPedido> implements Serializable {

    private static final long serialVersionUID = 1L;
	@EJB
	private InterfaceDAO<CompraTipoPedido> compraTipoPedidoDao;
	@EJB
	private InterfaceDAO<Fornecedor> fornecedorDao;
	@EJB
	private InterfaceDAO<Produto> produtoDao;
    
	private CompraPedidoDetalhe compraPedidoDetalhe;
	private CompraPedidoDetalhe compraPedidoDetalheSelecionado;
    
	private Boolean parametroCompraSugerida;
	@Inject
	private CompraSugeridaController compraSugeridaController;
	private Set<CompraPedidoDetalhe> listaCompraSugerida;
	
    @Override
    public Class<CompraPedido> getClazz() {
        return CompraPedido.class;
    }

    @Override
    public String getFuncaoBase() {
        return "COMPRA_PEDIDO";
    }

    @Override
    public void incluir() {
    	super.incluir();
    	getObjeto().setListaCompraPedidoDetalhe(new HashSet<>());
    }
    
    @Override
    public void salvar() {
    	if (parametroCompraSugerida != null) {
    		if (getObjeto().getId() == null) {
    			for (CompraPedidoDetalhe d : listaCompraSugerida) {
    				d.setCompraPedido(getObjeto());
    			}
    			getObjeto().setListaCompraPedidoDetalhe(listaCompraSugerida);
    			parametroCompraSugerida = null;
    		}
    	}
		atualizaTotaisItens();
		atualizaTotaisPedido();

    	super.salvar();
    }
    
    public void incluirItem() {
    	compraPedidoDetalhe = new CompraPedidoDetalhe();
    	compraPedidoDetalhe.setCompraPedido(getObjeto());
    }
    
	public void alterarItem() {
		compraPedidoDetalhe = compraPedidoDetalheSelecionado;
	}
    
	public void salvarItem() {
		if (compraPedidoDetalhe.getId() == null) {
			getObjeto().getListaCompraPedidoDetalhe().add(compraPedidoDetalhe);
		}
		
		atualizaTotaisItens();
		atualizaTotaisPedido();
		
		salvar("Item salvo com sucesso!");
	}
	
	public void excluirItem() {
		if (compraPedidoDetalheSelecionado == null || compraPedidoDetalheSelecionado.getId() == null) {
			FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_INFO, "Nenhum registro selecionado!", null);
		} else {
			getObjeto().getListaCompraPedidoDetalhe().remove(compraPedidoDetalheSelecionado);
			atualizaTotaisPedido();
			salvar("Item excluído com sucesso!");
		}
	}
	
    public void atualizaTotaisItens() {
        for (CompraPedidoDetalhe d : getObjeto().getListaCompraPedidoDetalhe()) {
            d.setValorSubtotal(d.getQuantidade().multiply(d.getValorUnitario()));
            if (d.getTaxaDesconto() != null) {
                d.setValorDesconto(d.getValorSubtotal().multiply(d.getTaxaDesconto().divide(BigDecimal.valueOf(100), RoundingMode.HALF_DOWN)));
            } else {
                d.setValorDesconto(BigDecimal.ZERO);
                d.setTaxaDesconto(BigDecimal.ZERO);
            }
            d.setValorTotal(d.getValorSubtotal().subtract(d.getValorDesconto()));
            d.setBaseCalculoIcms(d.getValorTotal());
            //icms
            if (d.getAliquotaIcms() != null) {
                d.setValorIcms(d.getBaseCalculoIcms().multiply(d.getAliquotaIcms().divide(BigDecimal.valueOf(100), RoundingMode.HALF_DOWN)));
            } else {
                d.setAliquotaIcms(BigDecimal.ZERO);
                d.setValorIcms(BigDecimal.ZERO);
            }
            //ipi
            if (d.getAliquotaIpi() != null) {
                d.setValorIpi(d.getValorTotal().multiply(d.getAliquotaIpi().divide(BigDecimal.valueOf(100), RoundingMode.HALF_DOWN)));
            } else {
                d.setAliquotaIpi(BigDecimal.ZERO);
                d.setValorIpi(BigDecimal.ZERO);
            }
        }
    }

    public void atualizaTotaisPedido() {
        BigDecimal subTotal = BigDecimal.ZERO;
        BigDecimal totalDesconto = BigDecimal.ZERO;
        BigDecimal totalGeral = BigDecimal.ZERO;
        BigDecimal totalBaseCalculoIcms = BigDecimal.ZERO;
        BigDecimal totalIcms = BigDecimal.ZERO;
        BigDecimal totalIpi = BigDecimal.ZERO;
        for (CompraPedidoDetalhe d : getObjeto().getListaCompraPedidoDetalhe()) {
            subTotal = subTotal.add(d.getValorSubtotal());
            totalDesconto = totalDesconto.add(d.getValorDesconto());
            totalGeral = totalGeral.add(d.getValorTotal());
            totalBaseCalculoIcms = totalBaseCalculoIcms.add(d.getBaseCalculoIcms());
            totalIcms = totalIcms.add(d.getValorIcms());
            totalIpi = totalIpi.add(d.getValorIpi());
        }

        getObjeto().setValorSubtotal(subTotal);
        getObjeto().setValorDesconto(totalDesconto);
        getObjeto().setValorTotalPedido(totalGeral);
        getObjeto().setBaseCalculoIcms(totalBaseCalculoIcms);
        getObjeto().setValorIcms(totalIcms);
        getObjeto().setValorTotalProdutos(totalGeral);
        getObjeto().setValorIpi(totalIpi);
        getObjeto().setValorTotalNf(totalGeral.add(totalIcms));
    }

	public void carregaCompraSugerida() {
		try {
			if (parametroCompraSugerida != null) {
				listaCompraSugerida = compraSugeridaController.geraPedido();
				incluir();
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_ERROR, "Erro ao carregar a compra sugerida!", e.getMessage());
		}
	}	
    
	public List<CompraTipoPedido> getListaCompraTipoPedido(String nome) {
		List<CompraTipoPedido> listaCompraTipoPedido = new ArrayList<>();
		try {
			listaCompraTipoPedido = compraTipoPedidoDao.getBeansLike(CompraTipoPedido.class, "nome", nome);
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return listaCompraTipoPedido;
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

	public List<Produto> getListaProduto(String nome) {
		List<Produto> listaProduto = new ArrayList<>();
		try {
			listaProduto = produtoDao.getBeansLike(Produto.class, "nome", nome);
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return listaProduto;
	}
	
	public CompraPedidoDetalhe getCompraPedidoDetalheSelecionado() {
		return compraPedidoDetalheSelecionado;
	}

	public void setCompraPedidoDetalheSelecionado(CompraPedidoDetalhe compraPedidoDetalheSelecionado) {
		this.compraPedidoDetalheSelecionado = compraPedidoDetalheSelecionado;
	}

	public CompraPedidoDetalhe getCompraPedidoDetalhe() {
		return compraPedidoDetalhe;
	}

	public void setCompraPedidoDetalhe(CompraPedidoDetalhe compraPedidoDetalhe) {
		this.compraPedidoDetalhe = compraPedidoDetalhe;
	}      

	public Boolean getParametroCompraSugerida() {
		return parametroCompraSugerida;
	}

	public void setParametroCompraSugerida(Boolean parametroCompraSugerida) {
		this.parametroCompraSugerida = parametroCompraSugerida;
	}    
	
}
