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
package com.t2tierp.controller.sintegra;

import java.io.File;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;

import com.t2tierp.util.FacesContextUtil;

@ManagedBean
@ViewScoped
public class SintegraController implements Serializable {

	private static final long serialVersionUID = 1L;
	private String codigoConvenio;
	private String finalidadeArquivo;
	private String naturezaOperacao;
	private Date dataInicio;
	private Date dataFim;
	@Inject
	private ArquivoSintegra arquivoSintegra;

	public void geraSintegra() {
		try {
            Calendar dataInicial = Calendar.getInstance();
            Calendar dataFinal = Calendar.getInstance();
            dataInicial.setTime(dataInicio);
            dataFinal.setTime(dataFim);
            if (dataFinal.before(dataInicial)) {
                throw new Exception("Data inicial posterior a data final!");
            }			
			File arquivo = arquivoSintegra.geraArquivo(finalidadeArquivo, naturezaOperacao, codigoConvenio, dataInicio, dataFim);
			FacesContextUtil.downloadArquivo(arquivo, "sintegra.txt");
		} catch (Exception ex) {
			ex.printStackTrace();
			FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro ao gerar o arquivo.", ex.getMessage());
		}
	}

	public String getCodigoConvenio() {
		return codigoConvenio;
	}

	public void setCodigoConvenio(String codigoConvenio) {
		this.codigoConvenio = codigoConvenio;
	}

	public String getFinalidadeArquivo() {
		return finalidadeArquivo;
	}

	public void setFinalidadeArquivo(String finalidadeArquivo) {
		this.finalidadeArquivo = finalidadeArquivo;
	}

	public String getNaturezaOperacao() {
		return naturezaOperacao;
	}

	public void setNaturezaOperacao(String naturezaOperacao) {
		this.naturezaOperacao = naturezaOperacao;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Date getDataFim() {
		return dataFim;
	}

	public void setDataFim(Date dataFim) {
		this.dataFim = dataFim;
	}
}
