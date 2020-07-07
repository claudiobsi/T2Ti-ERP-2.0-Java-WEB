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
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.RowEditEvent;

import com.t2tierp.controller.AbstractController;
import com.t2tierp.model.bean.administrativo.AdmParametro;
import com.t2tierp.model.bean.cadastros.Cliente;
import com.t2tierp.model.bean.financeiro.FinCobranca;
import com.t2tierp.model.bean.financeiro.FinCobrancaParcelaReceber;
import com.t2tierp.model.bean.financeiro.FinParcelaReceber;
import com.t2tierp.model.dao.Filtro;
import com.t2tierp.model.dao.InterfaceDAO;
import com.t2tierp.util.Biblioteca;
import com.t2tierp.util.FacesContextUtil;

@ManagedBean
@ViewScoped
public class FinCobrancaController extends AbstractController<FinCobranca> implements Serializable {

	private static final long serialVersionUID = 1L;
	@EJB
	private InterfaceDAO<Cliente> clienteDao;
	@EJB
	private InterfaceDAO<FinParcelaReceber> parcelaReceberDao;
	@EJB
	private InterfaceDAO<AdmParametro> admParametroDao;

	private FinParcelaReceber finParcelaReceber;
	private FinParcelaReceber finParcelaReceberSelecionado;

	private FinCobrancaParcelaReceber finCobrancaParcelaReceber;
	private FinCobrancaParcelaReceber finCobrancaParcelaReceberSelecionado;

	private List<FinParcelaReceber> parcelasVencidas;

	@Override
	public Class<FinCobranca> getClazz() {
		return FinCobranca.class;
	}

	@Override
	public String getFuncaoBase() {
		return "FIN_COBRANCA";
	}

	@Override
	public void incluir() {
		super.incluir();

		Date dataContato = new Date();
		SimpleDateFormat format = new SimpleDateFormat("HH:mm:ss");
		getObjeto().setDataContato(dataContato);
		getObjeto().setHoraContato(format.format(dataContato));
		getObjeto().setListaFinCobrancaParcelaReceber(new HashSet<>());

		parcelasVencidas = new ArrayList<>();
	}

	@Override
	public void voltar() {
		parcelasVencidas = new ArrayList<>();
		super.voltar();
	}

	public void buscaParcelaVencida() {
		try {
			List<Filtro> filtros = new ArrayList<>();

			filtros.add(new Filtro(Filtro.AND, "empresa", Filtro.IGUAL, FacesContextUtil.getEmpresaUsuario()));
			AdmParametro admParametro = admParametroDao.getBean(AdmParametro.class, filtros);
			if (admParametro == null) {
				throw new Exception("Parâmetros administrativos não cadastrados.\nEntre em contato com a Software House.");
			}

			filtros = new ArrayList<>();
			filtros.add(new Filtro(Filtro.AND, "finStatusParcela.id", Filtro.IGUAL, admParametro.getFinParcelaAberto()));

			Cliente cliente = getObjeto().getCliente();
			filtros.add(new Filtro(Filtro.AND, "finLancamentoReceber.cliente", Filtro.IGUAL, cliente));

			Date dataAtual = new Date();
			filtros.add(new Filtro(Filtro.AND, "dataVencimento", Filtro.MENOR, dataAtual));

			parcelasVencidas = parcelaReceberDao.getBeans(FinParcelaReceber.class, filtros);

			if (parcelasVencidas.isEmpty()) {
				FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_INFO, "Nenhuma parcela vencida para o cliente informado.!", "");
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro ao salvar o registro!", e.getMessage());
		}
	}

	public void alteraParcelaVencida(RowEditEvent event) {
		try {
			FinParcelaReceber parcela = (FinParcelaReceber) event.getObject();
			parcelaReceberDao.merge(parcela);
			FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_INFO, "Dados salvos com sucesso!", null);
		} catch (Exception e) {
			e.printStackTrace();
			FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro ao salvar o registro!", e.getMessage());
		}
	}

	public void alteraParcelaCobranca(RowEditEvent event) {
		try {
			FinCobrancaParcelaReceber parcela = (FinCobrancaParcelaReceber) event.getObject();
			getObjeto().getListaFinCobrancaParcelaReceber().remove(parcela);
			getObjeto().getListaFinCobrancaParcelaReceber().add(parcela);
			
	        for (FinCobrancaParcelaReceber p : getObjeto().getListaFinCobrancaParcelaReceber()) {
	            p.setValorReceberSimulado(p.getValorParcela().add(p.getValorJuroSimulado()).add(p.getValorMultaSimulado()));
	        }
			
		} catch (Exception e) {
			e.printStackTrace();
			FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro ao salvar o registro!", e.getMessage());
		}
	}

	public void calcularJurosMulta() {
		try {
			if (parcelasVencidas ==  null || parcelasVencidas.isEmpty()) {
				FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_INFO, "Nenhuma parcela vencida!", "");
			} else {
				BigDecimal juros = BigDecimal.ZERO;
				BigDecimal multa = BigDecimal.ZERO;
				BigDecimal total = BigDecimal.ZERO;
				BigDecimal totalAtraso = BigDecimal.ZERO;

				BigDecimal valorJurosParcela;
				BigDecimal valorMultaParcela;

				getObjeto().setListaFinCobrancaParcelaReceber(new HashSet<>());

				for (FinParcelaReceber p : parcelasVencidas) {
					p = (FinParcelaReceber) Biblioteca.nullToEmpty(p, false);

					FinCobrancaParcelaReceber parcelaCobranca = new FinCobrancaParcelaReceber();
					parcelaCobranca.setFinCobranca(getObjeto());
					parcelaCobranca.setIdFinLancamentoReceber(p.getFinLancamentoReceber().getId());
					parcelaCobranca.setIdFinParcelaReceber(p.getId());
					parcelaCobranca.setDataVencimento(p.getDataVencimento());
					parcelaCobranca.setValorParcela(p.getValor());

					valorJurosParcela = p.getValor().multiply(p.getTaxaJuro().divide(BigDecimal.valueOf(100)));
					valorMultaParcela = p.getValor().multiply(p.getTaxaMulta().divide(BigDecimal.valueOf(100)));

					parcelaCobranca.setValorJuroSimulado(valorJurosParcela);
					parcelaCobranca.setValorJuroAcordo(valorJurosParcela);
					parcelaCobranca.setValorMultaSimulado(valorMultaParcela);
					parcelaCobranca.setValorMultaAcordo(valorMultaParcela);
					parcelaCobranca.setValorReceberSimulado(p.getValor().add(valorJurosParcela).add(valorMultaParcela));
					parcelaCobranca.setValorReceberAcordo(parcelaCobranca.getValorReceberSimulado());

					totalAtraso = totalAtraso.add(p.getValor());
					total = total.add(parcelaCobranca.getValorReceberSimulado());
					juros = juros.add(parcelaCobranca.getValorJuroSimulado());
					multa = multa.add(parcelaCobranca.getValorMultaSimulado());

					getObjeto().getListaFinCobrancaParcelaReceber().add(parcelaCobranca);
				}

				getObjeto().setTotalReceberNaData(total);
				getObjeto().setTotalJuros(juros);
				getObjeto().setTotalMulta(multa);
				getObjeto().setTotalAtrasado(totalAtraso);
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro ao realizar os cálculos!", e.getMessage());
		}
	}

	public void simulaValores() {
		try {
			BigDecimal juros = BigDecimal.ZERO;
			BigDecimal multa = BigDecimal.ZERO;
			BigDecimal total = BigDecimal.ZERO;

			for (FinCobrancaParcelaReceber p : getObjeto().getListaFinCobrancaParcelaReceber()) {
				p = (FinCobrancaParcelaReceber) Biblioteca.nullToEmpty(p, false);

				total = total.add(p.getValorReceberSimulado());
				juros = juros.add(p.getValorJuroSimulado());
				multa = multa.add(p.getValorMultaSimulado());
			}

			getObjeto().setTotalReceberNaData(total);
			getObjeto().setTotalJuros(juros);
			getObjeto().setTotalMulta(multa);
		} catch (Exception e) {
			e.printStackTrace();
			FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro ao simular os valores!", e.getMessage());
		}
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

	public FinParcelaReceber getFinParcelaReceber() {
		return finParcelaReceber;
	}

	public void setFinParcelaReceber(FinParcelaReceber finParcelaReceber) {
		this.finParcelaReceber = finParcelaReceber;
	}

	public FinParcelaReceber getFinParcelaReceberSelecionado() {
		return finParcelaReceberSelecionado;
	}

	public void setFinParcelaReceberSelecionado(FinParcelaReceber finParcelaReceberSelecionado) {
		this.finParcelaReceberSelecionado = finParcelaReceberSelecionado;
	}

	public FinCobrancaParcelaReceber getFinCobrancaParcelaReceber() {
		return finCobrancaParcelaReceber;
	}

	public void setFinCobrancaParcelaReceber(FinCobrancaParcelaReceber finCobrancaParcelaReceber) {
		this.finCobrancaParcelaReceber = finCobrancaParcelaReceber;
	}

	public FinCobrancaParcelaReceber getFinCobrancaParcelaReceberSelecionado() {
		return finCobrancaParcelaReceberSelecionado;
	}

	public void setFinCobrancaParcelaReceberSelecionado(FinCobrancaParcelaReceber finCobrancaParcelaReceberSelecionado) {
		this.finCobrancaParcelaReceberSelecionado = finCobrancaParcelaReceberSelecionado;
	}

	public List<FinParcelaReceber> getParcelasVencidas() {
		return parcelasVencidas;
	}

	public void setParcelasVencidas(List<FinParcelaReceber> parcelasVencidas) {
		this.parcelasVencidas = parcelasVencidas;
	}

}
