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
package com.t2tierp.controller.ponto;

import java.io.File;
import java.io.Serializable;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.t2tierp.controller.AbstractController;
import com.t2tierp.model.bean.ponto.PontoFechamentoJornada;
import com.t2tierp.model.bean.ponto.PontoHorario;
import com.t2tierp.model.bean.ponto.PontoMarcacao;
import com.t2tierp.model.dao.InterfaceDAO;
import com.t2tierp.ponto.acjef.GeraArquivoACJEF;
import com.t2tierp.ponto.afdt.GeraArquivoAFDT;
import com.t2tierp.util.FacesContextUtil;

@ManagedBean
@ViewScoped
public class PontoGeracaoArquivoController extends AbstractController<PontoFechamentoJornada> implements Serializable {

	private static final long serialVersionUID = 1L;
	@EJB
	private InterfaceDAO<PontoMarcacao> pontoMarcacaoDao;
	@EJB
	private InterfaceDAO<PontoHorario> pontoHorarioDao;

	private List<PontoMarcacao> listaPontoMarcacao;
	private List<PontoFechamentoJornada> listaFechamentoJornada;
	private Date dataInicial;
	private Date dataFinal;

	@Override
	public Class<PontoFechamentoJornada> getClazz() {
		return PontoFechamentoJornada.class;
	}

	@Override
	public String getFuncaoBase() {
		return "PONTO_GERACAO_ARQUIVO";
	}

	public void carregarDados() {
		try {
			//Exercício: Ordenar a lista abaixo de forma ascendente pelos seguintes campos: colaborador, dataMarcacao e horaMarcacao
			listaPontoMarcacao = pontoMarcacaoDao.getBeans(PontoMarcacao.class, "dataMarcacao", dataInicial, dataFinal);

			//Exercício: Ordenar a lista abaixo de forma ascendente pelos seguintes campos: colaborador e dataFechamento
			listaFechamentoJornada = dao.getBeans(getClazz(), "dataFechamento", dataInicial, dataFinal);
		} catch (Exception e) {
			e.printStackTrace();
			FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_ERROR, "Erro ao buscar os dados!", e.getMessage());
		}
	}

	public void geraArquivoAFDT() {
		try {
			if (listaPontoMarcacao == null || listaPontoMarcacao.isEmpty()) {
				FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_WARN, "Nenhum registro para ser gerado!", "");
			} else {
				File file = File.createTempFile("afdt", ".txt");
				file.deleteOnExit();

				GeraArquivoAFDT geraArquivo = new GeraArquivoAFDT();
				geraArquivo.geraArquivoAFDT(file, dataInicial, dataFinal, listaPontoMarcacao);
				FacesContextUtil.downloadArquivo(file, "afdt.txt");
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro ao gerar o arquivo!", e.getMessage());
		}
	}

	public void geraArquivoACJEF() {
		try {
			List<PontoHorario> horarios = pontoHorarioDao.getBeans(PontoHorario.class);

			if (horarios.isEmpty()) {
				FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_WARN, "Nenhum horário cadastrado!", "");
			} else if (listaFechamentoJornada == null || listaFechamentoJornada.isEmpty()) {
				FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_WARN, "Nenhum registro de fechamento da jornada foi encontrado!", "");
			} else {
				File file = File.createTempFile("acfef", ".txt");
				file.deleteOnExit();

				GeraArquivoACJEF geraArquivo = new GeraArquivoACJEF();
				geraArquivo.geraArquivoACJEF(file, dataInicial, dataFinal, horarios, listaFechamentoJornada);
				FacesContextUtil.downloadArquivo(file, "acjef.txt");
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro ao gerar o arquivo!", e.getMessage());
		}
	}

	public List<PontoFechamentoJornada> getListaFechamentoJornada() {
		return listaFechamentoJornada;
	}

	public void setListaFechamentoJornada(List<PontoFechamentoJornada> listaFechamentoJornada) {
		this.listaFechamentoJornada = listaFechamentoJornada;
	}

	public List<PontoMarcacao> getListaPontoMarcacao() {
		return listaPontoMarcacao;
	}

	public void setListaPontoMarcacao(List<PontoMarcacao> listaPontoMarcacao) {
		this.listaPontoMarcacao = listaPontoMarcacao;
	}

	public Date getDataInicial() {
		return dataInicial;
	}

	public void setDataInicial(Date dataInicial) {
		this.dataInicial = dataInicial;
	}

	public Date getDataFinal() {
		return dataFinal;
	}

	public void setDataFinal(Date dataFinal) {
		this.dataFinal = dataFinal;
	}

}
