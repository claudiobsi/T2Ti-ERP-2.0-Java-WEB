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

import org.primefaces.event.SelectEvent;

import com.t2tierp.controller.AbstractController;
import com.t2tierp.model.bean.cadastros.Cliente;
import com.t2tierp.model.bean.cadastros.Produto;
import com.t2tierp.model.bean.cadastros.Transportadora;
import com.t2tierp.model.bean.cadastros.Vendedor;
import com.t2tierp.model.bean.vendas.CondicoesPagamento;
import com.t2tierp.model.bean.vendas.NotaFiscalTipo;
import com.t2tierp.model.bean.vendas.VendaCabecalho;
import com.t2tierp.model.bean.vendas.VendaDetalhe;
import com.t2tierp.model.bean.vendas.VendaOrcamentoCabecalho;
import com.t2tierp.model.bean.vendas.VendaOrcamentoDetalhe;
import com.t2tierp.model.dao.InterfaceDAO;
import com.t2tierp.util.Biblioteca;
import com.t2tierp.util.FacesContextUtil;

@ManagedBean
@ViewScoped
public class VendaCabecalhoController extends AbstractController<VendaCabecalho> implements Serializable {

    private static final long serialVersionUID = 1L;
    @EJB
    private InterfaceDAO<VendaOrcamentoCabecalho> vendaOrcamentoCabecalhoDao;
    @EJB
    private InterfaceDAO<CondicoesPagamento> vendaCondicoesPagamentoDao;
    @EJB
    private InterfaceDAO<Transportadora> transportadoraDao;
    @EJB
    private InterfaceDAO<NotaFiscalTipo> tipoNotaFiscalDao;
    @EJB
    private InterfaceDAO<Cliente> clienteDao;
    @EJB
    private InterfaceDAO<Vendedor> vendedorDao;
    @EJB
    private InterfaceDAO<Produto> produtoDao;

	private VendaDetalhe vendaDetalhe;
	private VendaDetalhe vendaDetalheSelecionado;

    @Override
    public Class<VendaCabecalho> getClazz() {
        return VendaCabecalho.class;
    }

    @Override
    public String getFuncaoBase() {
        return "VENDA_CABECALHO";
    }

    @Override
    public void incluir() {
        super.incluir();
        getObjeto().setListaVendaDetalhe(new HashSet<VendaDetalhe>());
	}

	public void incluirVendaDetalhe() {
        vendaDetalhe = new VendaDetalhe();
        vendaDetalhe.setVendaCabecalho(getObjeto());
	}

	public void alterarVendaDetalhe() {
        vendaDetalhe = vendaDetalheSelecionado;
	}

	public void salvarVendaDetalhe() {
		vendaDetalhe.setValorUnitario(vendaDetalhe.getProduto().getValorVenda());
		if (vendaDetalhe.getId() == null) {
            getObjeto().getListaVendaDetalhe().add(vendaDetalhe);
        }
        try {
			calculaTotais();
			salvar("Registro salvo com sucesso!");
		} catch (Exception e) {
			e.printStackTrace();
			FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro!", e.getMessage());
		}
	}
	
	public void excluirVendaDetalhe() {
        if (vendaDetalheSelecionado == null || vendaDetalheSelecionado.getId() == null) {
            FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_INFO, "Nenhum registro selecionado!", null);
        } else {
            try {
                getObjeto().getListaVendaDetalhe().remove(vendaDetalheSelecionado);
                calculaTotais();
                salvar("Registro excluído com sucesso!");
    		} catch (Exception e) {
    			e.printStackTrace();
    			FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro!", e.getMessage());
    		}
        }
	}

    private void calculaTotais() throws Exception {
    	VendaCabecalho vendaCabecalho = getObjeto();
        BigDecimal subTotal = BigDecimal.ZERO;
        BigDecimal totalDesconto = BigDecimal.ZERO;
        for (VendaDetalhe d : getObjeto().getListaVendaDetalhe()) {
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
        vendaCabecalho.setValorSubtotal(subTotal);
        if (totalDesconto.compareTo(BigDecimal.ZERO) != 0) {
        	vendaCabecalho.setValorDesconto(totalDesconto);
        	vendaCabecalho.setTaxaDesconto(Biblioteca.multiplica(Biblioteca.divide(totalDesconto, subTotal), BigDecimal.valueOf(100)));
        }

        vendaCabecalho.setValorTotal(subTotal);
        if (vendaCabecalho.getValorFrete() != null){
        	vendaCabecalho.setValorTotal(Biblioteca.soma(vendaCabecalho.getValorTotal(), vendaCabecalho.getValorFrete()));
        }
        if (vendaCabecalho.getValorDesconto() != null){
        	vendaCabecalho.setValorTotal(Biblioteca.subtrai(vendaCabecalho.getValorTotal(), vendaCabecalho.getValorDesconto()));
        }

        if (vendaCabecalho.getTaxaComissao() != null) {
        	vendaCabecalho.setValorComissao(Biblioteca.multiplica(Biblioteca.subtrai(subTotal, totalDesconto), Biblioteca.divide(vendaCabecalho.getTaxaComissao(), BigDecimal.valueOf(100))));
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
    
    public void carregaItensOrcamento(SelectEvent event) {
        try {
        	VendaOrcamentoCabecalho orcamento = (VendaOrcamentoCabecalho) event.getObject();
            VendaDetalhe itemVenda;
            getObjeto().setListaVendaDetalhe(new HashSet<>());
            getObjeto().setVendaOrcamentoCabecalho(vendaOrcamentoCabecalhoDao.getBeanJoinFetch(orcamento.getId(), VendaOrcamentoCabecalho.class));
            for (VendaOrcamentoDetalhe d : getObjeto().getVendaOrcamentoCabecalho().getListaVendaOrcamentoDetalhe()) {
                itemVenda = new VendaDetalhe();
                itemVenda.setProduto(d.getProduto());
                itemVenda.setQuantidade(d.getQuantidade());
                itemVenda.setTaxaDesconto(d.getTaxaDesconto());
                itemVenda.setValorDesconto(d.getValorDesconto());
                itemVenda.setValorSubtotal(d.getValorSubtotal());
                itemVenda.setValorTotal(d.getValorTotal());
                itemVenda.setValorUnitario(d.getValorUnitario());
                
                getObjeto().getListaVendaDetalhe().add(itemVenda);
            }
            
            getObjeto().setCliente(orcamento.getCliente());
            getObjeto().setCondicoesPagamento(orcamento.getCondicoesPagamento());
            getObjeto().setTransportadora(orcamento.getTransportadora());
            getObjeto().setVendedor(orcamento.getVendedor());
            getObjeto().setTipoFrete(orcamento.getTipoFrete());
            getObjeto().setValorSubtotal(orcamento.getValorSubtotal());
            getObjeto().setValorFrete(orcamento.getValorFrete());
            getObjeto().setTaxaComissao(orcamento.getTaxaComissao());
            getObjeto().setValorComissao(orcamento.getValorComissao());
            getObjeto().setTaxaDesconto(orcamento.getValorDesconto());
            getObjeto().setValorTotal(orcamento.getValorTotal());
            getObjeto().setObservacao(orcamento.getObservacao());
            
            calculaTotais();
        } catch (Exception e) {
        	e.printStackTrace();
        	FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro!", e.getMessage());
        }
    }
    
    public List<VendaOrcamentoCabecalho> getListaVendaOrcamentoCabecalho(String nome) {
        List<VendaOrcamentoCabecalho> listaVendaOrcamentoCabecalho = new ArrayList<>();
        try {
            listaVendaOrcamentoCabecalho = vendaOrcamentoCabecalhoDao.getBeansLike(VendaOrcamentoCabecalho.class, "codigo", nome);
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return listaVendaOrcamentoCabecalho;
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

    public List<Transportadora> getListaTransportadora(String nome) {
        List<Transportadora> listaTransportadora = new ArrayList<>();
        try {
            listaTransportadora = transportadoraDao.getBeansLike(Transportadora.class, "pessoa.nome", nome);
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return listaTransportadora;
    }

    public List<NotaFiscalTipo> getListaTipoNotaFiscal(String nome) {
        List<NotaFiscalTipo> listaTipoNotaFiscal = new ArrayList<>();
        try {
            listaTipoNotaFiscal = tipoNotaFiscalDao.getBeansLike(NotaFiscalTipo.class, "nome", nome);
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return listaTipoNotaFiscal;
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

    public List<Vendedor> getListaVendedor(String nome) {
        List<Vendedor> listaVendedor = new ArrayList<>();
        try {
            listaVendedor = vendedorDao.getBeansLike(Vendedor.class, "colaborador.pessoa.nome", nome);
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return listaVendedor;
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
	
    public VendaDetalhe getVendaDetalhe() {
        return vendaDetalhe;
    }

    public void setVendaDetalhe(VendaDetalhe vendaDetalhe) {
        this.vendaDetalhe = vendaDetalhe;
    }

	public VendaDetalhe getVendaDetalheSelecionado() {
		return vendaDetalheSelecionado;
	}

	public void setVendaDetalheSelecionado(VendaDetalhe vendaDetalheSelecionado) {
		this.vendaDetalheSelecionado = vendaDetalheSelecionado;
	}


}
