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
package com.t2tierp.controller.conciliacaocontabil;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.t2tierp.controller.AbstractController;
import com.t2tierp.model.bean.contabilidade.ContabilConta;
import com.t2tierp.model.bean.financeiro.FinExtratoContaBanco;
import com.t2tierp.model.dao.Filtro;
import com.t2tierp.model.dao.InterfaceDAO;
import com.t2tierp.util.FacesContextUtil;

@ManagedBean
@ViewScoped
public class ComposicaoSaldoController extends AbstractController<ContabilConta> implements Serializable {

    private static final long serialVersionUID = 1L;
    @EJB
    private InterfaceDAO<FinExtratoContaBanco> extratoContaBancoDao;
    
    private Date dataInicial;
    private Date dataFinal;
    private List<FinExtratoContaBanco> extratoContaBanco;
    private String labelTotal;

    @Override
    public Class<ContabilConta> getClazz() {
        return ContabilConta.class;
    }

    @Override
    public String getFuncaoBase() {
        return "COMPOSICAO_SALDO";
    }

    public void carregaDados() {
    	try {
    		List<Filtro> filtros = new ArrayList<>();
    		filtros.add(new Filtro(Filtro.AND, "dataMovimento", Filtro.MAIOR_OU_IGUAL, dataInicial));
    		filtros.add(new Filtro(Filtro.AND, "dataMovimento", Filtro.MENOR_OU_IGUAL, dataFinal));
    		extratoContaBanco = extratoContaBancoDao.getBeans(FinExtratoContaBanco.class, filtros);

            BigDecimal totalConta = BigDecimal.ZERO;
            for (int i = 0; i < extratoContaBanco.size(); i++){
                totalConta = totalConta.add(extratoContaBanco.get(i).getValor());
            }
            labelTotal = "Saldo da conta: " + NumberFormat.getCurrencyInstance().format(totalConta);
    	} catch (Exception e) {
    		FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro ao carregar os dados!", e.getMessage());
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

	public List<FinExtratoContaBanco> getExtratoContaBanco() {
		return extratoContaBanco;
	}

	public void setExtratoContaBanco(List<FinExtratoContaBanco> extratoContaBanco) {
		this.extratoContaBanco = extratoContaBanco;
	}

	public String getLabelTotal() {
		return labelTotal;
	}

	public void setLabelTotal(String labelTotal) {
		this.labelTotal = labelTotal;
	}

}
