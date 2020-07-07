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
package com.t2tierp.controller.vendas;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.t2tierp.controller.AbstractController;
import com.t2tierp.model.bean.cadastros.Cliente;
import com.t2tierp.model.bean.cadastros.Produto;
import com.t2tierp.model.bean.cadastros.Transportadora;
import com.t2tierp.model.bean.cadastros.Vendedor;
import com.t2tierp.model.bean.vendas.CondicoesPagamento;
import com.t2tierp.model.bean.vendas.VendaOrcamentoCabecalho;
import com.t2tierp.model.bean.vendas.VendaOrcamentoDetalhe;
import com.t2tierp.model.dao.InterfaceDAO;
import com.t2tierp.util.Biblioteca;
import com.t2tierp.util.FacesContextUtil;

@ManagedBean
@ViewScoped
public class VendaOrcamentoCabecalhoController extends AbstractController<VendaOrcamentoCabecalho> implements Serializable {

    private static final long serialVersionUID = 1L;
    @EJB
    private InterfaceDAO<CondicoesPagamento> vendaCondicoesPagamentoDao;
    @EJB
    private InterfaceDAO<Vendedor> vendedorDao;
    @EJB
    private InterfaceDAO<Transportadora> transportadoraDao;
    @EJB
    private InterfaceDAO<Cliente> clienteDao;
    @EJB
    private InterfaceDAO<Produto> produtoDao;

	private VendaOrcamentoDetalhe vendaOrcamentoDetalhe;
	private VendaOrcamentoDetalhe vendaOrcamentoDetalheSelecionado;

    @Override
    public Class<VendaOrcamentoCabecalho> getClazz() {
        return VendaOrcamentoCabecalho.class;
    }

    @Override
    public String getFuncaoBase() {
        return "VENDA_ORCAMENTO_CABECALHO";
    }

    @Override
    public void incluir() {
        super.incluir();
        getObjeto().setListaVendaOrcamentoDetalhe(new HashSet<VendaOrcamentoDetalhe>());
        getObjeto().setSituacao("D");
	}

    @Override
    public void salvar() {
        try {
        	String situacao = getObjeto().getSituacao();
            if (!situacao.equals("D")){
                String mensagem = "Este registro não pode ser alterado.\n";
                if (situacao.equals("P")){
                    mensagem += "Situação: Em Produção";
                }
                if (situacao.equals("X")){
                    mensagem += "Situação: Em Expedição";
                }
                if (situacao.equals("F")){
                    mensagem += "Situação: Faturado";
                }
                if (situacao.equals("E")){
                    mensagem += "Situação: Entregue";
                }
                throw new Exception(mensagem);
            }
        	super.salvar();
        } catch(Exception e) {
        	FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro!", e.getMessage());
        }
    }
    
    private void calculaTotais() throws Exception {
        VendaOrcamentoCabecalho orcamentoCabecalho = getObjeto();
        BigDecimal subTotal = BigDecimal.ZERO;
        BigDecimal totalDesconto = BigDecimal.ZERO;
        for (VendaOrcamentoDetalhe d : getObjeto().getListaVendaOrcamentoDetalhe()) {
            d.setValorSubtotal(Biblioteca.multiplica(d.getQuantidade(), d.getValorUnitario()));
            subTotal = Biblioteca.soma(subTotal, d.getValorSubtotal());
            if (d.getTaxaDesconto() != null) {
                d.setValorDesconto(Biblioteca.multiplica(d.getValorSubtotal(), Biblioteca.divide(d.getTaxaDesconto(), BigDecimal.valueOf(100))));
            }
            if (d.getValorDesconto() != null) {
                totalDesconto = Biblioteca.soma(totalDesconto, d.getValorDesconto());
                d.setValorTotal(Biblioteca.subtrai(d.getValorSubtotal(), d.getValorDesconto()));
            } else {
                d.setValorTotal(d.getValorSubtotal());
            }
        }
        orcamentoCabecalho.setValorSubtotal(subTotal);
        if (totalDesconto.compareTo(BigDecimal.ZERO) != 0) {
            orcamentoCabecalho.setValorDesconto(totalDesconto);
            orcamentoCabecalho.setTaxaDesconto(Biblioteca.multiplica(Biblioteca.divide(totalDesconto, subTotal), BigDecimal.valueOf(100)));
        }

        orcamentoCabecalho.setValorTotal(subTotal);
        if (orcamentoCabecalho.getValorFrete() != null){
            orcamentoCabecalho.setValorTotal(Biblioteca.soma(orcamentoCabecalho.getValorTotal(), orcamentoCabecalho.getValorFrete()));
        }
        if (orcamentoCabecalho.getValorDesconto() != null){
            orcamentoCabecalho.setValorTotal(Biblioteca.subtrai(orcamentoCabecalho.getValorTotal(), orcamentoCabecalho.getValorDesconto()));
        }

        if (orcamentoCabecalho.getTaxaComissao() != null) {
            orcamentoCabecalho.setValorComissao(Biblioteca.multiplica(Biblioteca.subtrai(subTotal, totalDesconto), Biblioteca.divide(orcamentoCabecalho.getTaxaComissao(), BigDecimal.valueOf(100))));
        }
        atualizaTotais();
    }
    
    public void atualizaTotais() throws Exception {
        if (getObjeto().getValorSubtotal() != null) {
            if (getObjeto().getTaxaDesconto() != null) {
            	getObjeto().setValorDesconto(Biblioteca.multiplica(getObjeto().getValorSubtotal(), Biblioteca.divide(getObjeto().getTaxaDesconto(), BigDecimal.valueOf(100))));
            	getObjeto().setValorTotal(Biblioteca.subtrai(getObjeto().getValorSubtotal(), getObjeto().getValorDesconto()));
            }
            if (getObjeto().getValorFrete() != null) {
                if (getObjeto().getValorTotal() == null) {
                	getObjeto().setValorTotal(getObjeto().getValorSubtotal());
                }
                getObjeto().setValorTotal(Biblioteca.soma(getObjeto().getValorTotal(), getObjeto().getValorFrete()));
            }
        }
    }
    
	public void incluirVendaOrcamentoDetalhe() {
        vendaOrcamentoDetalhe = new VendaOrcamentoDetalhe();
        vendaOrcamentoDetalhe.setVendaOrcamentoCabecalho(getObjeto());
	}

	public void alterarVendaOrcamentoDetalhe() {
        vendaOrcamentoDetalhe = vendaOrcamentoDetalheSelecionado;
	}

	public void salvarVendaOrcamentoDetalhe() {
        vendaOrcamentoDetalhe.setValorUnitario(vendaOrcamentoDetalhe.getProduto().getValorVenda());
		if (vendaOrcamentoDetalhe.getId() == null) {
            getObjeto().getListaVendaOrcamentoDetalhe().add(vendaOrcamentoDetalhe);
        }
        try {
			calculaTotais();
			salvar("Registro salvo com sucesso!");
		} catch (Exception e) {
			e.printStackTrace();
			FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro!", e.getMessage());
		}
	}
	
	public void excluirVendaOrcamentoDetalhe() {
        if (vendaOrcamentoDetalheSelecionado == null || vendaOrcamentoDetalheSelecionado.getId() == null) {
            FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_INFO, "Nenhum registro selecionado!", null);
        } else {
            try {
            	getObjeto().getListaVendaOrcamentoDetalhe().remove(vendaOrcamentoDetalheSelecionado);
                calculaTotais();
                salvar("Registro excluído com sucesso!");
    		} catch (Exception e) {
    			e.printStackTrace();
    			FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro!", e.getMessage());
    		}
        }
	}

    public List<CondicoesPagamento> getListaVendaCondicoesPagamento(String nome) {
        List<CondicoesPagamento> listaVendaCondicoesPagamento = new ArrayList<>();
        try {
            listaVendaCondicoesPagamento = vendaCondicoesPagamentoDao.getBeansLike(CondicoesPagamento.class, "nome", nome);
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return listaVendaCondicoesPagamento;
    }

    public List<Vendedor> getListaVendedor(String nome) {
        List<Vendedor> listaVendedor = new ArrayList<>();
        try {
            listaVendedor = vendedorDao.getBeansLike(Vendedor.class, "colaborador.pessoa.nome", nome);
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return listaVendedor;
    }

    public List<Transportadora> getListaTransportadora(String nome) {
        List<Transportadora> listaTransportadora = new ArrayList<>();
        try {
            listaTransportadora = transportadoraDao.getBeansLike(Transportadora.class, "pessoa.nome", nome);
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return listaTransportadora;
    }

    public List<Cliente> getListaCliente(String nome) {
        List<Cliente> listaCliente = new ArrayList<>();
        try {
            listaCliente = clienteDao.getBeansLike(Cliente.class, "pessoa.nome", nome);
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return listaCliente;
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
    
    public VendaOrcamentoDetalhe getVendaOrcamentoDetalhe() {
        return vendaOrcamentoDetalhe;
    }

    public void setVendaOrcamentoDetalhe(VendaOrcamentoDetalhe vendaOrcamentoDetalhe) {
        this.vendaOrcamentoDetalhe = vendaOrcamentoDetalhe;
    }

	public VendaOrcamentoDetalhe getVendaOrcamentoDetalheSelecionado() {
		return vendaOrcamentoDetalheSelecionado;
	}

	public void setVendaOrcamentoDetalheSelecionado(VendaOrcamentoDetalhe vendaOrcamentoDetalheSelecionado) {
		this.vendaOrcamentoDetalheSelecionado = vendaOrcamentoDetalheSelecionado;
	}


}
