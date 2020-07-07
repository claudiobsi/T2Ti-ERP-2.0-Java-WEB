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
package com.t2tierp.controller.financeiro;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.t2tierp.controller.AbstractController;
import com.t2tierp.model.bean.administrativo.AdmParametro;
import com.t2tierp.model.bean.cadastros.Cheque;
import com.t2tierp.model.bean.cadastros.ContaCaixa;
import com.t2tierp.model.bean.financeiro.FinChequeEmitido;
import com.t2tierp.model.bean.financeiro.FinParcelaPagamento;
import com.t2tierp.model.bean.financeiro.FinParcelaPagar;
import com.t2tierp.model.bean.financeiro.FinStatusParcela;
import com.t2tierp.model.bean.financeiro.FinTipoPagamento;
import com.t2tierp.model.dao.Filtro;
import com.t2tierp.model.dao.InterfaceDAO;
import com.t2tierp.util.FacesContextUtil;

@ManagedBean
@ViewScoped
public class FinParcelaPagamentoController extends AbstractController<FinParcelaPagar> implements Serializable {

	private static final long serialVersionUID = 1L;

	@EJB
	private InterfaceDAO<FinTipoPagamento> finTipoPagamentoDao;
	@EJB
	private InterfaceDAO<ContaCaixa> contaCaixaDao;
	@EJB
	private InterfaceDAO<AdmParametro> admParametroDao;
	@EJB
	private InterfaceDAO<FinStatusParcela> finStatusParcelaDao;
	@EJB
	private InterfaceDAO<Cheque> chequeDao;
	@EJB
	private InterfaceDAO<FinChequeEmitido> finChequeEmitidoDao;

	private FinParcelaPagamento finParcelaPagamento;
	private FinParcelaPagamento finParcelaPagamentoSelecionado;

	private FinChequeEmitido finChequeEmitido;

	private String strTipoBaixa;
	private List<FinParcelaPagar> parcelasSelecionadas;
	private boolean pagamentoCheque;

	@Override
	public Class<FinParcelaPagar> getClazz() {
		return FinParcelaPagar.class;
	}

	@Override
	public String getFuncaoBase() {
		return "FIN_PARCELA_PAGAMENTO";
	}

	@Override
	public void alterar() {
		if (parcelasSelecionadas != null) {
			if (parcelasSelecionadas.isEmpty()) {
				FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_WARN, "Nenhuma parcela foi selecionada!", "");
			} else if (parcelasSelecionadas.size() == 1) {
				setObjetoSelecionado(parcelasSelecionadas.get(0));
				super.alterar();
				novoPagamento();
			} else if (parcelasSelecionadas.size() > 1) {
				iniciaPagamentoCheque();
			}
		}
	}

	private void novoPagamento() {
		finParcelaPagamento = new FinParcelaPagamento();
		finParcelaPagamento.setFinParcelaPagar(getObjeto());
		finParcelaPagamento.setDataPagamento(new Date());

		strTipoBaixa = "T";
	}

	public void iniciaPagamentoCheque() {
		Date dataAtual = new Date();
		BigDecimal totalParcelas = BigDecimal.ZERO;
		finChequeEmitido = new FinChequeEmitido();
		if (parcelasSelecionadas.size() > 1) {
			for (FinParcelaPagar p : parcelasSelecionadas) {
				if (p.getFinStatusParcela().getSituacao().equals("02")) {
					FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_WARN, "Foi selecionado parcela já quitada.", "Pagamento não realizado.");
					return;
				}
				if (p.getDataVencimento().before(dataAtual)) {
					FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_WARN, "Foi selecionado parcela já vencida.", "Pagamento não realizado.");
					return;
				}
				if (p.getSofreRetencao() != null && p.getSofreRetencao().equals("S")) {
					FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_WARN, "Procedimento não permitido. Fornecedor sofre retenção.", "Pagamento não realizado.");
					return;
				}
				if (p.getValor() != null) {
					totalParcelas = totalParcelas.add(p.getValor());
				}
			}
			finChequeEmitido.setValor(totalParcelas);
		}
		pagamentoCheque = true;
	}

	public void cancelaPagamentoCheque() {
		pagamentoCheque = false;
	}

	public void finalizaPagamentoCheque() {
		pagamentoCheque = false;
		incluirPagamento();
	}

	public void iniciaPagamento() {
		if (finParcelaPagamento.getFinTipoPagamento().getTipo().equals("02")) {
			iniciaPagamentoCheque();
		} else {
			incluirPagamento();
		}
	}

	private void incluirPagamento() {
		try {
			List<Filtro> filtros = new ArrayList<>();
			filtros.add(new Filtro(Filtro.AND, "empresa", Filtro.IGUAL, FacesContextUtil.getEmpresaUsuario()));
			AdmParametro admParametro = admParametroDao.getBean(AdmParametro.class, filtros);

			FinStatusParcela statusParcela = null;
			if (admParametro == null) {
				throw new Exception("Parâmetros administrativos não encontrados. Entre em contato com a Software House.");
			}
			statusParcela = finStatusParcelaDao.getBean(admParametro.getFinParcelaQuitado(), FinStatusParcela.class);
			if (statusParcela == null) {
				throw new Exception("O status de parcela 'Quitado' não está cadastrado.\nEntre em contato com a Software House.");
			}

			if (parcelasSelecionadas.size() == 1 && !finParcelaPagamento.getFinTipoPagamento().getTipo().equals("02")) {
				calculaTotalPago();

				FinParcelaPagamento pagamento = new FinParcelaPagamento();
				pagamento.setFinParcelaPagar(finParcelaPagamento.getFinParcelaPagar());
				pagamento.setContaCaixa(finParcelaPagamento.getContaCaixa());
				pagamento.setDataPagamento(finParcelaPagamento.getDataPagamento());
				pagamento.setFinTipoPagamento(finParcelaPagamento.getFinTipoPagamento());
				pagamento.setHistorico(finParcelaPagamento.getHistorico());
				pagamento.setTaxaDesconto(finParcelaPagamento.getTaxaDesconto());
				pagamento.setTaxaJuro(finParcelaPagamento.getTaxaJuro());
				pagamento.setTaxaMulta(finParcelaPagamento.getTaxaMulta());
				pagamento.setValorDesconto(finParcelaPagamento.getValorDesconto());
				pagamento.setValorJuro(finParcelaPagamento.getValorJuro());
				pagamento.setValorMulta(finParcelaPagamento.getValorMulta());
				pagamento.setValorPago(finParcelaPagamento.getValorPago());

				if (strTipoBaixa.equals("P")) {
					statusParcela = finStatusParcelaDao.getBean(admParametro.getFinParcelaQuitadoParcial(), FinStatusParcela.class);
					if (statusParcela == null) {
						throw new Exception("O status de parcela 'Quitado Parcial' não está cadastrado.\nEntre em contato com a Software House.");
					}
				}

				getObjeto().setFinStatusParcela(statusParcela);
				getObjeto().getListaFinParcelaPagamento().add(pagamento);
				salvar("Pagamento incluído com sucesso!");
				novoPagamento();
			} else {
				filtros = new ArrayList<>();
				filtros.add(new Filtro(Filtro.AND, "tipo", Filtro.IGUAL, "02"));				
				FinTipoPagamento tipoPagamento = finTipoPagamentoDao.getBean(FinTipoPagamento.class, filtros);
				if (tipoPagamento == null) {
					throw new Exception("Tipo de pagamento 'CHEQUE' não está cadastrado.\nEntre em contato com a Software House.");
				}
				
				finChequeEmitidoDao.persist(finChequeEmitido);
				finChequeEmitido.getCheque().setStatusCheque("U");
				chequeDao.merge(finChequeEmitido.getCheque());
				
				for (FinParcelaPagar p : parcelasSelecionadas) {
					FinParcelaPagamento pagamento = new FinParcelaPagamento();
					pagamento.setFinTipoPagamento(tipoPagamento);
					pagamento.setFinParcelaPagar(p);
					pagamento.setFinChequeEmitido(finChequeEmitido);
					pagamento.setContaCaixa(finChequeEmitido.getCheque().getTalonarioCheque().getContaCaixa());
					pagamento.setDataPagamento(finChequeEmitido.getBomPara());
					pagamento.setHistorico(finChequeEmitido.getHistorico());
					pagamento.setValorPago(p.getValor());

					p.setFinStatusParcela(statusParcela);
					p.getListaFinParcelaPagamento().add(pagamento);

					p = dao.merge(p);
				}

				if (parcelasSelecionadas.size() == 1) {
					setObjeto(dao.getBeanJoinFetch(getObjeto().getId(), getClazz()));
					novoPagamento();
				}

				FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_INFO, "Pagamento efetuado com sucesso!", "");
			}
		} catch (Exception e) {
			FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_ERROR, "Erro ao incluir o pagamento!", e.getMessage());
		}
	}

	public void calculaTotalPago() throws Exception {
		BigDecimal valorJuro = BigDecimal.ZERO;
		BigDecimal valorMulta = BigDecimal.ZERO;
		BigDecimal valorDesconto = BigDecimal.ZERO;
		if (finParcelaPagamento.getTaxaJuro() != null && finParcelaPagamento.getDataPagamento() != null) {
			Calendar dataPagamento = Calendar.getInstance();
			dataPagamento.setTime(finParcelaPagamento.getDataPagamento());
			Calendar dataVencimento = Calendar.getInstance();
			dataVencimento.setTime(finParcelaPagamento.getFinParcelaPagar().getDataVencimento());
			if (dataVencimento.before(dataPagamento)) {
				long diasAtraso = (dataPagamento.getTimeInMillis() - dataVencimento.getTimeInMillis()) / 86400000l;
				//valorJuro = valor * ((taxaJuro / 30) / 100) * diasAtraso
				finParcelaPagamento.setValorJuro(finParcelaPagamento.getFinParcelaPagar().getValor().multiply(finParcelaPagamento.getTaxaJuro().divide(BigDecimal.valueOf(30), RoundingMode.HALF_DOWN)).divide(BigDecimal.valueOf(100), RoundingMode.HALF_DOWN).multiply(BigDecimal.valueOf(diasAtraso)));
				valorJuro = finParcelaPagamento.getValorJuro();
			}
		}
		finParcelaPagamento.setValorJuro(valorJuro);

		if (finParcelaPagamento.getTaxaMulta() != null) {
			finParcelaPagamento.setValorMulta(finParcelaPagamento.getFinParcelaPagar().getValor().multiply(finParcelaPagamento.getTaxaMulta()).divide(BigDecimal.valueOf(100), RoundingMode.HALF_DOWN));
			valorMulta = finParcelaPagamento.getValorMulta();
		} else {
			finParcelaPagamento.setValorMulta(valorMulta);
		}

		if (finParcelaPagamento.getTaxaDesconto() != null) {
			finParcelaPagamento.setValorDesconto(finParcelaPagamento.getFinParcelaPagar().getValor().multiply(finParcelaPagamento.getTaxaDesconto()).divide(BigDecimal.valueOf(100), RoundingMode.HALF_DOWN));
			valorDesconto = finParcelaPagamento.getValorDesconto();
		} else {
			finParcelaPagamento.setValorDesconto(valorDesconto);
		}

		finParcelaPagamento.setValorPago(finParcelaPagamento.getFinParcelaPagar().getValor().add(valorJuro).add(valorMulta).subtract(valorDesconto));
	}

	public void excluirFinParcelaPagamento() {
		if (finParcelaPagamentoSelecionado == null || finParcelaPagamentoSelecionado.getId() == null) {
			FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_INFO, "Nenhum registro selecionado!", null);
		} else {
			getObjeto().getListaFinParcelaPagamento().remove(finParcelaPagamentoSelecionado);
			salvar("Registro excluído com sucesso!");
		}
	}

	public List<FinTipoPagamento> getListaFinTipoPagamento(String nome) {
		List<FinTipoPagamento> listaFinTipoPagamento = new ArrayList<>();
		try {
			listaFinTipoPagamento = finTipoPagamentoDao.getBeansLike(FinTipoPagamento.class, "descricao", nome);
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return listaFinTipoPagamento;
	}

	public List<ContaCaixa> getListaContaCaixa(String nome) {
		List<ContaCaixa> listaContaCaixa = new ArrayList<>();
		try {
			listaContaCaixa = contaCaixaDao.getBeansLike(ContaCaixa.class, "nome", nome);
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return listaContaCaixa;
	}

	public List<Cheque> getListaCheque(String nome) {
		List<Cheque> listaCheque = new ArrayList<>();
		try {
			listaCheque = chequeDao.getBeans(Cheque.class, "statusCheque", "E");
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return listaCheque;
	}

	public FinParcelaPagamento getFinParcelaPagamento() {
		return finParcelaPagamento;
	}

	public void setFinParcelaPagamento(FinParcelaPagamento finParcelaPagamento) {
		this.finParcelaPagamento = finParcelaPagamento;
	}

	public FinParcelaPagamento getFinParcelaPagamentoSelecionado() {
		return finParcelaPagamentoSelecionado;
	}

	public void setFinParcelaPagamentoSelecionado(FinParcelaPagamento finParcelaPagamentoSelecionado) {
		this.finParcelaPagamentoSelecionado = finParcelaPagamentoSelecionado;
	}

	public String getStrTipoBaixa() {
		return strTipoBaixa;
	}

	public void setStrTipoBaixa(String strTipoBaixa) {
		this.strTipoBaixa = strTipoBaixa;
	}

	public List<FinParcelaPagar> getParcelasSelecionadas() {
		return parcelasSelecionadas;
	}

	public void setParcelasSelecionadas(List<FinParcelaPagar> parcelasSelecionadas) {
		this.parcelasSelecionadas = parcelasSelecionadas;
	}

	public boolean isPagamentoCheque() {
		return pagamentoCheque;
	}

	public void setPagamentoCheque(boolean pagamentoCheque) {
		this.pagamentoCheque = pagamentoCheque;
	}

	public FinChequeEmitido getFinChequeEmitido() {
		return finChequeEmitido;
	}

	public void setFinChequeEmitido(FinChequeEmitido finChequeEmitido) {
		this.finChequeEmitido = finChequeEmitido;
	}

}
