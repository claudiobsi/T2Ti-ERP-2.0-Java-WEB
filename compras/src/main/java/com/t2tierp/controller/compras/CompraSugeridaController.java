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
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.t2tierp.controller.AbstractController;
import com.t2tierp.model.bean.cadastros.Produto;
import com.t2tierp.model.bean.compras.CompraPedido;
import com.t2tierp.model.bean.compras.CompraPedidoDetalhe;
import com.t2tierp.model.bean.compras.CompraRequisicaoDetalhe;
import com.t2tierp.model.dao.compras.CompraSugeridaDAO;
import com.t2tierp.util.FacesContextUtil;

@ManagedBean
@ViewScoped
public class CompraSugeridaController extends AbstractController<CompraPedido> implements Serializable {

	private static final long serialVersionUID = 1L;
	@EJB
	private CompraSugeridaDAO compraSugeridaDAO;

	private String tipoCompraSugerida;

	@Override
	public Class<CompraPedido> getClazz() {
		return CompraPedido.class;
	}

	@Override
	public String getFuncaoBase() {
		return "COMPRA_SUGERIDA";
	}

	public String confirma() {
		try {
			List<Produto> listaProduto = compraSugeridaDAO.getItensCompraSugerida();
			if (listaProduto.isEmpty()) {
				throw new Exception("Nenhum produto com estoque menor que o mínimo!");
			}
			if (tipoCompraSugerida.equals("Requisicao")) {
				return "compraRequisicao.jsf?faces-redirect=true&compraSugerida=true";
			} else if (tipoCompraSugerida.equals("Pedido")) {
				return "compraPedido.jsf?faces-redirect=true&compraSugerida=true";
			}

			return null;
		} catch (Exception e) {
			e.printStackTrace();
			FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro ao buscar os itens!", e.getMessage());
		}
		return null;
	}
	
    public Set<CompraRequisicaoDetalhe> geraRequisicao() throws Exception {
    	List<Produto> listaProduto = compraSugeridaDAO.getItensCompraSugerida();
    	Set<CompraRequisicaoDetalhe> listaRequisicaoDetalhe = new HashSet<>();
        for (Produto p : listaProduto) {
        	CompraRequisicaoDetalhe requisicaoDetalhe = new CompraRequisicaoDetalhe();
            requisicaoDetalhe.setProduto(p);
            requisicaoDetalhe.setQuantidade(p.getEstoqueIdeal().subtract(p.getQuantidadeEstoque()));
            requisicaoDetalhe.setQuantidadeCotada(BigDecimal.ZERO);
            requisicaoDetalhe.setItemCotado("N");
            
            listaRequisicaoDetalhe.add(requisicaoDetalhe);
        }
        
        return listaRequisicaoDetalhe;
    }

    public Set<CompraPedidoDetalhe> geraPedido() throws Exception {
    	List<Produto> listaProduto = compraSugeridaDAO.getItensCompraSugerida();
    	Set<CompraPedidoDetalhe> listaPedidoDetalhe = new HashSet<>();
    	for (Produto p : listaProduto) {
        	CompraPedidoDetalhe pedidoDetalhe = new CompraPedidoDetalhe();
            pedidoDetalhe.setProduto(p);
            pedidoDetalhe.setQuantidade(p.getEstoqueIdeal().subtract(p.getQuantidadeEstoque()));
            pedidoDetalhe.setValorUnitario(p.getValorCompra());

            listaPedidoDetalhe.add(pedidoDetalhe);
        }
    	return listaPedidoDetalhe;
    }    
    
	public String getTipoCompraSugerida() {
		return tipoCompraSugerida;
	}

	public void setTipoCompraSugerida(String tipoCompraSugerida) {
		this.tipoCompraSugerida = tipoCompraSugerida;
	}

}
