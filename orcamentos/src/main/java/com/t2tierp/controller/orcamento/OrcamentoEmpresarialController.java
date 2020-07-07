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
package com.t2tierp.controller.orcamento;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.component.datatable.DataTable;
import org.primefaces.event.CellEditEvent;

import com.t2tierp.controller.AbstractController;
import com.t2tierp.model.bean.financeiro.NaturezaFinanceira;
import com.t2tierp.model.bean.financeiro.PlanoNaturezaFinanceira;
import com.t2tierp.model.bean.orcamento.OrcamentoDetalhe;
import com.t2tierp.model.bean.orcamento.OrcamentoEmpresarial;
import com.t2tierp.model.bean.orcamento.OrcamentoPeriodo;
import com.t2tierp.model.dao.Filtro;
import com.t2tierp.model.dao.InterfaceDAO;
import com.t2tierp.model.dao.orcamento.OrcamentoDAO;
import com.t2tierp.util.Biblioteca;
import com.t2tierp.util.FacesContextUtil;

@ManagedBean
@ViewScoped
public class OrcamentoEmpresarialController extends AbstractController<OrcamentoEmpresarial> implements Serializable {

	private static final long serialVersionUID = 1L;

	@EJB
	private InterfaceDAO<PlanoNaturezaFinanceira> planoNaturezaFinanceiraDao;
	@EJB
	private InterfaceDAO<NaturezaFinanceira> naturezaFinanceiraDao;
	@EJB
	private InterfaceDAO<OrcamentoPeriodo> orcamentoPeriodoDao;
	@EJB
	private InterfaceDAO<OrcamentoDetalhe> orcamentoDetalheDao;
	@EJB
	private OrcamentoDAO orcamentoDao;

	@Override
	public Class<OrcamentoEmpresarial> getClazz() {
		return OrcamentoEmpresarial.class;
	}

	@Override
	public String getFuncaoBase() {
		return "ORCAMENTO_EMPRESARIAL";
	}

	@Override
	public void salvar() {
		try {
			geraDetalhesOrcamento();
			super.salvar();
		} catch (Exception e) {
			e.printStackTrace();
			FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro ao salvar o registro!", e.getMessage());
		}
	}

	private void geraDetalhesOrcamento() throws Exception {
		int periodo = Integer.valueOf(getObjeto().getOrcamentoPeriodo().getPeriodo());

		Calendar dataBaseFim = Calendar.getInstance();
		dataBaseFim.setLenient(false);

		Calendar dataPeriodo = Calendar.getInstance();
		dataPeriodo.setLenient(false);

		int numeroPeriodos = getObjeto().getNumeroPeriodos();

		SimpleDateFormat formatoDiario = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat formatoMensal = new SimpleDateFormat("MM/yyyy");
		SimpleDateFormat formatoAnual = new SimpleDateFormat("yyyy");

		List<Filtro> filtros = new ArrayList<Filtro>();
		filtros.add(new Filtro(Filtro.AND, "planoNaturezaFinanceira", Filtro.IGUAL, getObjeto().getPlanoNaturezaFinanceira()));
		List<NaturezaFinanceira> listaNaturezaFinanceira = naturezaFinanceiraDao.getBeans(NaturezaFinanceira.class, filtros);

		getObjeto().setListaOrcamentoDetalhe(new ArrayList<>());

		for (NaturezaFinanceira n : listaNaturezaFinanceira) {
			dataPeriodo.setTime(getObjeto().getDataInicial());

			for (int j = 0; j < numeroPeriodos; j++) {
				OrcamentoDetalhe orcamentoDetalhe = new OrcamentoDetalhe();
				orcamentoDetalhe.setNaturezaFinanceira(n);
				orcamentoDetalhe.setOrcamentoEmpresarial(getObjeto());

				switch (periodo) {
				case 1: {
					//Di�rio
					if (j != 0) {
						dataPeriodo.add(Calendar.DAY_OF_MONTH, 1);
					}
					orcamentoDetalhe.setPeriodo(formatoDiario.format(dataPeriodo.getTime()));
					break;
				}
				case 2: {
					//Semanal
					if (j != 0) {
						dataPeriodo.add(Calendar.DAY_OF_MONTH, 7);
					}
					orcamentoDetalhe.setPeriodo(formatoDiario.format(dataPeriodo.getTime()));
					break;
				}
				case 3: {
					//Mensal
					if (j != 0) {
						dataPeriodo.add(Calendar.MONTH, 1);
					}
					orcamentoDetalhe.setPeriodo(formatoMensal.format(dataPeriodo.getTime()));
					break;
				}
				case 4: {
					//Bimestral
					if (j != 0) {
						dataPeriodo.add(Calendar.MONTH, 2);
					}
					orcamentoDetalhe.setPeriodo(formatoMensal.format(dataPeriodo.getTime()));
					break;
				}
				case 5: {
					//Trimestral
					if (j != 0) {
						dataPeriodo.add(Calendar.MONTH, 3);
					}
					orcamentoDetalhe.setPeriodo(formatoMensal.format(dataPeriodo.getTime()));
					break;
				}
				case 6: {
					//Semestral
					if (j != 0) {
						dataPeriodo.add(Calendar.MONTH, 6);
					}
					orcamentoDetalhe.setPeriodo(formatoMensal.format(dataPeriodo.getTime()));
					break;
				}
				case 7: {
					//Anual
					if (j != 0) {
						dataPeriodo.add(Calendar.YEAR, 1);
					}
					orcamentoDetalhe.setPeriodo(formatoAnual.format(dataPeriodo.getTime()));
					break;
				}
				}
				getObjeto().getListaOrcamentoDetalhe().add(orcamentoDetalhe);
			}
		}
	}

	public void alteraDetalhe(CellEditEvent event) {
		try {
			DataTable dataTable = (DataTable) event.getSource();
			OrcamentoDetalhe detalhe = (OrcamentoDetalhe) dataTable.getRowData();
			BigDecimal valorOrcado = (BigDecimal) event.getNewValue();
			detalhe.setValorOrcado(valorOrcado);
			orcamentoDetalheDao.merge(detalhe);
		} catch (Exception e) {
			e.printStackTrace();
			FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro ao salvar o registro!", e.getMessage());
		}
	}	
	
	public void buscaValorRealizado() {
		try {
			Calendar dataInicio = Calendar.getInstance();
			Calendar dataFim = Calendar.getInstance();
			int periodo = Integer.valueOf(getObjeto().getOrcamentoPeriodo().getPeriodo());

			BigDecimal realizadoReceber;
			BigDecimal realizadoPagar;
			for (OrcamentoDetalhe d : getObjeto().getListaOrcamentoDetalhe()) {
				dataInicio = Biblioteca.dataStrToCalendar(d.getPeriodo());

				switch (periodo) {
				case 1: {
					dataFim.setTime(dataInicio.getTime());
					break;
				}
				case 2: {
					dataFim.add(Calendar.DAY_OF_MONTH, 7);
					break;
				}
				case 3: {
					dataFim.add(Calendar.MONTH, 1);
					break;
				}
				case 4: {
					dataFim.add(Calendar.MONTH, 2);
					break;
				}
				case 5: {
					dataFim.add(Calendar.MONTH, 3);
					break;
				}
				case 6: {
					dataFim.add(Calendar.MONTH, 6);
					break;
				}
				case 7: {
					dataFim.add(Calendar.YEAR, 1);
					break;
				}
				}

				//busca receita do per�odo
				realizadoReceber = orcamentoDao.getValorRecebimento(dataInicio.getTime(), dataFim.getTime(), null);

				//busca despesa do per�odo
				realizadoPagar = orcamentoDao.getValorPagamento(dataInicio.getTime(), dataFim.getTime(), null);

				d.setValorRealizado(realizadoReceber.add(realizadoPagar));

				orcamentoDetalheDao.merge(d);
			}

			FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_INFO, "Valores atualizados com sucesso.", "");
		} catch (Exception ex) {
            ex.printStackTrace();
            FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro ao buscar o valor realizado!", ex.getMessage());
		}
	}

	public void calcularVariacao() {
		try {
			BigDecimal realizado;
			BigDecimal orcado;
			BigDecimal variacao;
			for (OrcamentoDetalhe d : getObjeto().getListaOrcamentoDetalhe()) {
				realizado = d.getValorRealizado();
				orcado = d.getValorOrcado();
				if (realizado != null && orcado != null) {
					if ((realizado.compareTo(BigDecimal.ZERO) != 0) || (orcado.compareTo(BigDecimal.ZERO) != 0)) {
						variacao = realizado.subtract(orcado);
						d.setValorVariacao(variacao);

						variacao = variacao.divide(orcado, RoundingMode.HALF_DOWN).multiply(BigDecimal.valueOf(100));
						variacao = variacao.setScale(2, RoundingMode.HALF_DOWN);
						d.setTaxaVariacao(variacao);
					}
				}

				orcamentoDetalheDao.merge(d);
			}
			FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_INFO, "Valores atualizados com sucesso.", "");
		} catch (Exception ex) {
            ex.printStackTrace();
            FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro ao calcular a varia��o!", ex.getMessage());
		}
	}

	public List<PlanoNaturezaFinanceira> getListaPlanoNaturezaFinanceira(String nome) {
		List<PlanoNaturezaFinanceira> listaPlanoNaturezaFinanceira = new ArrayList<>();
		try {
			listaPlanoNaturezaFinanceira = planoNaturezaFinanceiraDao.getBeansLike(PlanoNaturezaFinanceira.class, "nome", nome);
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return listaPlanoNaturezaFinanceira;
	}

	public List<OrcamentoPeriodo> getListaOrcamentoPeriodo(String nome) {
		List<OrcamentoPeriodo> listaOrcamentoPeriodo = new ArrayList<>();
		try {
			listaOrcamentoPeriodo = orcamentoPeriodoDao.getBeansLike(OrcamentoPeriodo.class, "nome", nome);
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return listaOrcamentoPeriodo;
	}
}
