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
package com.t2tierp.controller.sped;

import java.io.File;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;

import com.t2tierp.model.bean.cadastros.Contador;
import com.t2tierp.model.dao.InterfaceDAO;
import com.t2tierp.util.FacesContextUtil;

@ManagedBean
@ViewScoped
public class SpedContribuicoesController implements Serializable {

	private static final long serialVersionUID = 1L;
	private Date dataInicial;
	private Date dataFinal;
	private String versao;
	private String tipoEscrituracao;
	private Integer idContador;
	private HashMap<String, Integer> contadores;
	@Inject
	private ArquivoSpedContribuicoes arquivoSpedContribuicoes;
	@EJB
	private InterfaceDAO<Contador> contadorDao;

	@PostConstruct
	public void init() {
		try {
			contadores = new HashMap<>();
			List<Contador> listaContador = contadorDao.getBeans(Contador.class);
			for (Contador c : listaContador) {
				contadores.put(c.getNome(), c.getId());
			}
		} catch (Exception e) {
			//e.printStackTrace();
			contadores.put("Ocorreu um erro ao buscar os dados de contadores", 0);
		}
	}

	public SpedContribuicoesController() {
	}

	public void geraSpedContribuicoes() {
		try {
			Calendar d1 = Calendar.getInstance();
			Calendar d2 = Calendar.getInstance();
			d1.setTime(dataInicial);
			d2.setTime(dataFinal);
			if (d2.before(d1)) {
				throw new Exception("Data inicial posterior a data final!");
			}
			File arquivo = arquivoSpedContribuicoes.geraArquivo(versao, tipoEscrituracao, dataInicial, dataFinal, idContador);
			FacesContextUtil.downloadArquivo(arquivo, "spedContribuicoes.txt");
		} catch (Exception ex) {
			ex.printStackTrace();
			FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro ao gerar o arquivo.", ex.getMessage());
		}
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

	public String getVersao() {
		return versao;
	}

	public void setVersao(String versao) {
		this.versao = versao;
	}

	public String gettipoEscrituracao() {
		return tipoEscrituracao;
	}

	public void settipoEscrituracao(String tipoEscrituracao) {
		this.tipoEscrituracao = tipoEscrituracao;
	}

	public Integer getIdContador() {
		return idContador;
	}

	public void setIdContador(Integer idContador) {
		this.idContador = idContador;
	}

	public HashMap<String, Integer> getContadores() {
		return contadores;
	}

}
