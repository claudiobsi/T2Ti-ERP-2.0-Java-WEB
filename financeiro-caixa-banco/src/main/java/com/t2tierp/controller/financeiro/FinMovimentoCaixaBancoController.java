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
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.t2tierp.controller.AbstractController;
import com.t2tierp.model.bean.cadastros.ContaCaixa;
import com.t2tierp.model.bean.financeiro.FinFechamentoCaixaBanco;
import com.t2tierp.model.bean.financeiro.ViewFinChequeNaoCompensado;
import com.t2tierp.model.bean.financeiro.ViewFinChequeNaoCompensadoID;
import com.t2tierp.model.bean.financeiro.ViewFinMovimentoCaixaBanco;
import com.t2tierp.model.bean.financeiro.ViewFinMovimentoCaixaBancoID;
import com.t2tierp.model.dao.Filtro;
import com.t2tierp.model.dao.InterfaceDAO;
import com.t2tierp.util.Biblioteca;
import com.t2tierp.util.FacesContextUtil;

@ManagedBean
@ViewScoped
public class FinMovimentoCaixaBancoController extends AbstractController<ViewFinMovimentoCaixaBancoID> implements Serializable {

	private static final long serialVersionUID = 1L;

	private Date periodo;
	private List<ViewFinMovimentoCaixaBancoID> listaMovimentoCaixaBanco;
	private List<ViewFinMovimentoCaixaBancoID> listaMovimentoCaixaBancoDetalhe;
	@EJB
	private InterfaceDAO<ViewFinMovimentoCaixaBancoID> movimentoCaixaBancoDao;
	@EJB
	private InterfaceDAO<FinFechamentoCaixaBanco> fechamentoCaixaBancoDao;
	@EJB
	private InterfaceDAO<ContaCaixa> contaCaixaDao;
	@EJB
	private InterfaceDAO<ViewFinChequeNaoCompensadoID> chequeNaoCompensadoDao;

	private FinFechamentoCaixaBanco fechamentoCaixaBanco;

	@Override
	public Class<ViewFinMovimentoCaixaBancoID> getClazz() {
		return ViewFinMovimentoCaixaBancoID.class;
	}

	@Override
	public String getFuncaoBase() {
		return "FIN_MOVIMENTO_CAIXA_BANCO";
	}

	@Override
	public void alterar() {
		super.alterar();
		buscaDados();
	}

	public void buscaDados() {
		try {
			if (periodo == null) {
				FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_INFO, "Necessário informar o período!", "");
			} else {
				List<Filtro> filtros = new ArrayList<>();
				filtros.add(new Filtro(Filtro.AND, "viewFinMovimentoCaixaBanco.dataLancamento", Filtro.MAIOR_OU_IGUAL, getDataInicial()));
				filtros.add(new Filtro(Filtro.AND, "viewFinMovimentoCaixaBanco.dataLancamento", Filtro.MENOR_OU_IGUAL, ultimoDiaMes()));

				if (isTelaGrid()) {
					listaMovimentoCaixaBanco = movimentoCaixaBancoDao.getBeans(ViewFinMovimentoCaixaBancoID.class, filtros);
				} else {
					filtros.add(new Filtro(Filtro.AND, "viewFinMovimentoCaixaBanco.idContaCaixa", Filtro.IGUAL, getObjeto().getViewFinMovimentoCaixaBanco().getIdContaCaixa()));
					listaMovimentoCaixaBancoDetalhe = movimentoCaixaBancoDao.getBeans(ViewFinMovimentoCaixaBancoID.class, filtros);
					buscaDadosFechamento();
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_ERROR, "Erro ao buscar os dados!", e.getMessage());
		}
	}

	private Date getDataInicial() {
		try {
			if (periodo == null) {
				return null;
			}
			Calendar dataValida = Calendar.getInstance();
			dataValida.setTime(periodo);
			dataValida.setLenient(false);

			dataValida.set(Calendar.DAY_OF_MONTH, 1);

			dataValida.getTime();

			return dataValida.getTime();
		} catch (Exception e) {
			return null;
		}
	}

	private Date ultimoDiaMes() {
		Calendar dataF = Calendar.getInstance();
		dataF.setTime(periodo);
		dataF.setLenient(false);
		dataF.set(Calendar.DAY_OF_MONTH, dataF.getActualMaximum(Calendar.DAY_OF_MONTH));

		return dataF.getTime();
	}

	private void buscaDadosFechamento() throws Exception {
		DecimalFormat formatoMes = new DecimalFormat("00");
		Calendar dataFechamento = Calendar.getInstance();
		dataFechamento.setTime(getDataInicial());
		int mes = Integer.valueOf(formatoMes.format(dataFechamento.get(Calendar.MONTH) + 1));
		int ano = dataFechamento.get(Calendar.YEAR);

		ContaCaixa contaCaixa = contaCaixaDao.getBean(getObjeto().getViewFinMovimentoCaixaBanco().getIdContaCaixa(), ContaCaixa.class);

		List<Filtro> filtros = new ArrayList<>();
		filtros.add(new Filtro(Filtro.AND, "contaCaixa", Filtro.IGUAL, contaCaixa));
		filtros.add(new Filtro(Filtro.AND, "mes", Filtro.IGUAL, String.valueOf(mes)));
		filtros.add(new Filtro(Filtro.AND, "ano", Filtro.IGUAL, String.valueOf(ano)));

		fechamentoCaixaBanco = fechamentoCaixaBancoDao.getBean(FinFechamentoCaixaBanco.class, filtros);
		if (fechamentoCaixaBanco == null) {
			fechamentoCaixaBanco = new FinFechamentoCaixaBanco();
			fechamentoCaixaBanco.setContaCaixa(contaCaixa);
		}
		
		//busca saldo anterior
		mes -= 1;
		if (mes == 0) {
			mes = 12;
			ano -= 1;
		}
		filtros = new ArrayList<>();
		filtros.add(new Filtro(Filtro.AND, "contaCaixa", Filtro.IGUAL, contaCaixa));
		filtros.add(new Filtro(Filtro.AND, "mes", Filtro.IGUAL, String.valueOf(mes)));
		filtros.add(new Filtro(Filtro.AND, "ano", Filtro.IGUAL, String.valueOf(ano)));

		FinFechamentoCaixaBanco fechamentoAnterior = fechamentoCaixaBancoDao.getBean(FinFechamentoCaixaBanco.class, filtros);

		if (fechamentoAnterior != null && fechamentoAnterior.getSaldoAnterior() != null) {
			fechamentoCaixaBanco.setSaldoAnterior(fechamentoAnterior.getSaldoAnterior());
		} else {
			fechamentoCaixaBanco.setSaldoAnterior(BigDecimal.ZERO);
		}

		//calcula totais
		BigDecimal recebimentos = BigDecimal.ZERO;
		BigDecimal pagamentos = BigDecimal.ZERO;
		BigDecimal chequesNaoCompensados = BigDecimal.ZERO;
		for (ViewFinMovimentoCaixaBancoID v : listaMovimentoCaixaBanco) {
			ViewFinMovimentoCaixaBanco movimento = (ViewFinMovimentoCaixaBanco) Biblioteca.nullToEmpty(v.getViewFinMovimentoCaixaBanco(), false);

			if (movimento.getOperacao().equals("E")) {
				recebimentos = recebimentos.add(movimento.getValor());
			}
			if (movimento.getOperacao().equals("S")) {
				pagamentos = pagamentos.add(movimento.getValor());
			}
		}
		fechamentoCaixaBanco.setPagamentos(pagamentos);
		fechamentoCaixaBanco.setRecebimentos(recebimentos);
		fechamentoCaixaBanco.setSaldoConta(fechamentoCaixaBanco.getSaldoAnterior().subtract(pagamentos).add(recebimentos));

		//busca os cheques não compensados
		filtros = new ArrayList<>();
		filtros.add(new Filtro(Filtro.AND, "viewFinChequeNaoCompensado.idContaCaixa", Filtro.IGUAL, contaCaixa.getId()));

		List<ViewFinChequeNaoCompensadoID> listaChequeNaoCompensado = chequeNaoCompensadoDao.getBeans(ViewFinChequeNaoCompensadoID.class, filtros);
		for (ViewFinChequeNaoCompensadoID c : listaChequeNaoCompensado) {
			ViewFinChequeNaoCompensado cheque = (ViewFinChequeNaoCompensado) Biblioteca.nullToEmpty(c.getViewFinChequeNaoCompensado(), false);

			chequesNaoCompensados = chequesNaoCompensados.add(cheque.getValor());
		}

		fechamentoCaixaBanco.setChequeNaoCompensado(chequesNaoCompensados);
		fechamentoCaixaBanco.setSaldoDisponivel(fechamentoCaixaBanco.getSaldoConta().subtract(fechamentoCaixaBanco.getChequeNaoCompensado()));
	}
	
	public void processaFechamento() {
		try {
			if (fechamentoCaixaBanco.getId() == null) {
				fechamentoCaixaBancoDao.persist(fechamentoCaixaBanco);
			} else {
				fechamentoCaixaBancoDao.merge(fechamentoCaixaBanco);
			}
			FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_INFO, "Fechamento processado com sucesso!", "");
		} catch (Exception e) {
			e.printStackTrace();
			FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_ERROR, "Erro ao processar o fechamento!", e.getMessage());
		}
	}

	public Date getPeriodo() {
		return periodo;
	}

	public void setPeriodo(Date periodo) {
		this.periodo = periodo;
	}

	public List<ViewFinMovimentoCaixaBancoID> getListaMovimentoCaixaBanco() {
		return listaMovimentoCaixaBanco;
	}

	public void setListaMovimentoCaixaBanco(List<ViewFinMovimentoCaixaBancoID> listaMovimentoCaixaBanco) {
		this.listaMovimentoCaixaBanco = listaMovimentoCaixaBanco;
	}

	public List<ViewFinMovimentoCaixaBancoID> getListaMovimentoCaixaBancoDetalhe() {
		return listaMovimentoCaixaBancoDetalhe;
	}

	public void setListaMovimentoCaixaBancoDetalhe(List<ViewFinMovimentoCaixaBancoID> listaMovimentoCaixaBancoDetalhe) {
		this.listaMovimentoCaixaBancoDetalhe = listaMovimentoCaixaBancoDetalhe;
	}

	public FinFechamentoCaixaBanco getFechamentoCaixaBanco() {
		return fechamentoCaixaBanco;
	}

	public void setFechamentoCaixaBanco(FinFechamentoCaixaBanco fechamentoCaixaBanco) {
		this.fechamentoCaixaBanco = fechamentoCaixaBanco;
	}

}
