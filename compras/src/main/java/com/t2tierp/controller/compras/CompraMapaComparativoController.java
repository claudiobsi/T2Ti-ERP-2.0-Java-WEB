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
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.component.datatable.DataTable;
import org.primefaces.event.CellEditEvent;

import com.t2tierp.controller.AbstractController;
import com.t2tierp.controller.T2TiLazyDataModel;
import com.t2tierp.model.bean.compras.CompraCotacao;
import com.t2tierp.model.bean.compras.CompraCotacaoDetalhe;
import com.t2tierp.model.bean.compras.CompraCotacaoPedidoDetalhe;
import com.t2tierp.model.bean.compras.CompraFornecedorCotacao;
import com.t2tierp.model.bean.compras.CompraPedido;
import com.t2tierp.model.bean.compras.CompraPedidoDetalhe;
import com.t2tierp.model.bean.compras.CompraTipoPedido;
import com.t2tierp.model.dao.Filtro;
import com.t2tierp.model.dao.InterfaceDAO;
import com.t2tierp.util.FacesContextUtil;

@ManagedBean
@ViewScoped
public class CompraMapaComparativoController extends AbstractController<CompraCotacao> implements Serializable {

	private static final long serialVersionUID = 1L;
	private CompraMapaComparativoDataModel dataModel;

	@EJB
	private InterfaceDAO<CompraCotacaoDetalhe> compraCotacaoDetalheDao;
	private List<CompraCotacaoDetalhe> listaCompraCotacaoDetalhe;

	@EJB
	private InterfaceDAO<CompraTipoPedido> compraTipoPedidoDao;
	@EJB
	private InterfaceDAO<CompraPedido> compraPedidoDao;
	@EJB
	private InterfaceDAO<CompraCotacaoPedidoDetalhe> compraCotacaoPedidoDetalheDao;
	
	@Override
	public Class<CompraCotacao> getClazz() {
		return CompraCotacao.class;
	}

	@Override
	public String getFuncaoBase() {
		return "COMPRA_MAPA_COMPARATIVO";
	}

	@Override
	public T2TiLazyDataModel<CompraCotacao> getDataModel() {
		if (dataModel == null) {
			dataModel = new CompraMapaComparativoDataModel();
			dataModel.setClazz(getClazz());
			dataModel.setDao(dao);
		}
		return dataModel;
	}

	@Override
	public void alterar() {
		try {
			super.alterar();

			buscaListaCompracotacaoDetalhe();
		} catch (Exception e) {
			e.printStackTrace();
			FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro ao buscar os detalhes da cotação!", e.getMessage());
		}
	}

	public void buscaListaCompracotacaoDetalhe() throws Exception {
		super.alterar();

		List<Filtro> filtros = new ArrayList<>();
		filtros.add(new Filtro(Filtro.AND, "compraFornecedorCotacao.compraCotacao", Filtro.IGUAL, getObjeto()));
		listaCompraCotacaoDetalhe = compraCotacaoDetalheDao.getBeans(CompraCotacaoDetalhe.class, filtros);
	}

	@Override
	public void salvar() {
		try {
			buscaListaCompracotacaoDetalhe();

			boolean produtoPedido = false;
			for (CompraCotacaoDetalhe d : listaCompraCotacaoDetalhe) {
				if (d.getQuantidadePedida() != null) {
					if (d.getQuantidadePedida().compareTo(BigDecimal.ZERO) == 1) {
						produtoPedido = true;
					}
				}
			}
			if (!produtoPedido) {
				throw new Exception("Nenhum produto com quantidade pedida maior que 0(zero)!");
			}

			//Pedido vindo de cotação sempre será marcado como Normal
			CompraTipoPedido tipoPedido = compraTipoPedidoDao.getBean(1, CompraTipoPedido.class);
            if (tipoPedido == null) {
            	throw new Exception("Tipo de Pedido não cadastrado!");
            }

            List<CompraPedido> listaPedido = new ArrayList<>();
            List<CompraCotacaoPedidoDetalhe> listaCotacaoPedidoDetalhe = new ArrayList<>();
            CompraPedido pedido;
            Date dataPedido = new Date();
            boolean incluiPedido;
            BigDecimal subTotal;
            BigDecimal totalDesconto;
            BigDecimal totalGeral;
            BigDecimal totalBaseCalculoIcms;
            BigDecimal totalIcms;
            BigDecimal totalIpi;
            for (CompraFornecedorCotacao f : getObjeto().getListaCompraFornecedorCotacao()) {
                pedido = new CompraPedido();
                pedido.setCompraTipoPedido(tipoPedido);
                pedido.setDataPedido(dataPedido);
                pedido.setFornecedor(f.getFornecedor());
                pedido.setListaCompraPedidoDetalhe(new HashSet<>());
                incluiPedido = false;
                subTotal = BigDecimal.ZERO;
                totalDesconto = BigDecimal.ZERO;
                totalGeral = BigDecimal.ZERO;
                totalBaseCalculoIcms = BigDecimal.ZERO;
                totalIcms = BigDecimal.ZERO;
                totalIpi = BigDecimal.ZERO;
                //inclui os itens no pedido
                for (CompraCotacaoDetalhe d :  listaCompraCotacaoDetalhe) {
                    if (d.getCompraFornecedorCotacao().getId().intValue() == f.getId().intValue()) {
                        if (d.getQuantidadePedida() != null) {
                            if (d.getQuantidadePedida().compareTo(BigDecimal.ZERO) == 1) {
                                if (d.getValorUnitario() == null) {
                                	throw new Exception("Valor unitário do produto '" + d.getProduto().getNome() + " não informado!");
                                }
                                incluiPedido = true;

                                CompraPedidoDetalhe pedidoDetalhe = new CompraPedidoDetalhe();
                                pedidoDetalhe.setCompraPedido(pedido);
                                pedidoDetalhe.setProduto(d.getProduto());
                                pedidoDetalhe.setQuantidade(d.getQuantidadePedida());
                                pedidoDetalhe.setValorUnitario(d.getValorUnitario());
                                pedidoDetalhe.setValorSubtotal(d.getValorSubtotal());
                                pedidoDetalhe.setTaxaDesconto(d.getTaxaDesconto());
                                pedidoDetalhe.setValorDesconto(d.getValorDesconto());
                                pedidoDetalhe.setValorTotal(d.getValorTotal());
                                pedidoDetalhe.setBaseCalculoIcms(pedidoDetalhe.getValorTotal());
                                pedidoDetalhe.setAliquotaIcms(d.getProduto().getAliquotaIcmsPaf());
                                if (pedidoDetalhe.getAliquotaIcms() != null && pedidoDetalhe.getBaseCalculoIcms() != null) {
                                    pedidoDetalhe.setValorIcms(pedidoDetalhe.getBaseCalculoIcms().multiply(pedidoDetalhe.getAliquotaIcms().divide(BigDecimal.valueOf(100), RoundingMode.HALF_DOWN)));
                                } else {
                                    pedidoDetalhe.setAliquotaIcms(BigDecimal.ZERO);
                                    pedidoDetalhe.setValorIcms(BigDecimal.ZERO);
                                }
                                pedidoDetalhe.setAliquotaIpi(BigDecimal.ZERO);
                                if (pedidoDetalhe.getAliquotaIpi() != null) {
                                    pedidoDetalhe.setValorIpi(pedidoDetalhe.getValorTotal().multiply(pedidoDetalhe.getAliquotaIpi().divide(BigDecimal.valueOf(100), RoundingMode.HALF_DOWN)));
                                } else {
                                    pedidoDetalhe.setAliquotaIpi(BigDecimal.ZERO);
                                    pedidoDetalhe.setValorIpi(BigDecimal.ZERO);
                                }
                                pedido.getListaCompraPedidoDetalhe().add(pedidoDetalhe);

                                subTotal = subTotal.add(pedidoDetalhe.getValorSubtotal());
                                totalDesconto = totalDesconto.add(pedidoDetalhe.getValorDesconto());
                                totalGeral = totalGeral.add(pedidoDetalhe.getValorTotal());
                                totalBaseCalculoIcms = totalBaseCalculoIcms.add(pedidoDetalhe.getBaseCalculoIcms());
                                totalIcms = totalIcms.add(pedidoDetalhe.getValorIcms());
                                totalIpi = totalIpi.add(pedidoDetalhe.getValorIpi());

                                CompraCotacaoPedidoDetalhe cotacaoPedidoDetalhe = new CompraCotacaoPedidoDetalhe();
                                cotacaoPedidoDetalhe.setCompraPedido(pedido);
                                cotacaoPedidoDetalhe.setCompraCotacaoDetalhe(d);
                                cotacaoPedidoDetalhe.setQuantidadePedida(d.getQuantidadePedida());
                                listaCotacaoPedidoDetalhe.add(cotacaoPedidoDetalhe);
                            }
                        }
                    }
                }
                pedido.setValorSubtotal(subTotal);
                pedido.setValorDesconto(totalDesconto);
                pedido.setValorTotalPedido(totalGeral);
                pedido.setBaseCalculoIcms(totalBaseCalculoIcms);
                pedido.setValorIcms(totalIcms);
                pedido.setValorTotalProdutos(totalGeral);
                pedido.setValorIpi(totalIpi);
                pedido.setValorTotalNf(totalGeral);

                if (incluiPedido) {
                    listaPedido.add(pedido);
                }
            }

            for (CompraPedido c : listaPedido) {
                compraPedidoDao.persist(c);
            }

            for (CompraCotacaoPedidoDetalhe c : listaCotacaoPedidoDetalhe) {
                compraCotacaoPedidoDetalheDao.persist(c);
            }
			
			getObjeto().setSituacao("F");
			dao.merge(getObjeto());

			voltar();

			FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_INFO, "Pedido realizado com sucesso!", "");
		} catch (Exception e) {
			e.printStackTrace();
			FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro ao salvar o registro!", e.getMessage());
		}
	}

	public void alteraItemFornecedor(CellEditEvent event) {
		try {
			DataTable dataTable = (DataTable) event.getSource();
			CompraCotacaoDetalhe detalhe = (CompraCotacaoDetalhe) dataTable.getRowData();
			BigDecimal quantidadePedida = (BigDecimal) event.getNewValue();
			if (quantidadePedida != null) {
				if (quantidadePedida.compareTo(detalhe.getQuantidade()) == 1) {
					throw new Exception("Quantidade pedida do produto '" + detalhe.getProduto().getNome() + "' é maior que a quantidade cotada!");
				} else if (detalhe.getQuantidadePedida().compareTo(BigDecimal.ZERO) == 1) {
					detalhe.setQuantidadePedida((BigDecimal) event.getNewValue());

					compraCotacaoDetalheDao.merge(detalhe);

					FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_INFO, "Dados salvos com sucesso!", null);
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro ao salvar o registro!", e.getMessage());
		}
	}

	public List<CompraCotacaoDetalhe> getListaCompraCotacaoDetalhe() {
		return listaCompraCotacaoDetalhe;
	}

	public void setListaCompraCotacaoDetalhe(List<CompraCotacaoDetalhe> listaCompraCotacaoDetalhe) {
		this.listaCompraCotacaoDetalhe = listaCompraCotacaoDetalhe;
	}

}
