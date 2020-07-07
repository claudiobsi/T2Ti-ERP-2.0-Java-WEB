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
package com.t2tierp.model.bean.conciliacaocontabil;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

import com.t2tierp.model.bean.cadastros.ContaCaixa;
import com.t2tierp.model.bean.contabilidade.ContabilConta;
import com.t2tierp.model.bean.contabilidade.ContabilHistorico;

public class ConciliacaoBancaria implements Serializable {

    private static final long serialVersionUID = 1L;
    private String extratoMes;
    private String extratoAno;
    private Date extratoDataMovimento;
    private Date extratoDataBalancete;
    private String extratoHistorico;
    private BigDecimal extratoValor;
    private ContaCaixa extratoContaCaixa;

    private String lancamentoHistorico;
    private BigDecimal lancamentoValor;
    private String lancamentoTipo;
    private ContabilHistorico lancamentoContabilHistorico;
    private ContabilConta lancamentoContabilConta;
    
    public ConciliacaoBancaria() {
	}
    
	public String getExtratoMes() {
		return extratoMes;
	}
	public void setExtratoMes(String extratoMes) {
		this.extratoMes = extratoMes;
	}
	public String getExtratoAno() {
		return extratoAno;
	}
	public void setExtratoAno(String extratoAno) {
		this.extratoAno = extratoAno;
	}
	public Date getExtratoDataMovimento() {
		return extratoDataMovimento;
	}
	public void setExtratoDataMovimento(Date extratoDataMovimento) {
		this.extratoDataMovimento = extratoDataMovimento;
	}
	public Date getExtratoDataBalancete() {
		return extratoDataBalancete;
	}
	public void setExtratoDataBalancete(Date extratoDataBalancete) {
		this.extratoDataBalancete = extratoDataBalancete;
	}
	public String getExtratoHistorico() {
		return extratoHistorico;
	}
	public void setExtratoHistorico(String extratoHistorico) {
		this.extratoHistorico = extratoHistorico;
	}
	public BigDecimal getExtratoValor() {
		return extratoValor;
	}
	public void setExtratoValor(BigDecimal extratoValor) {
		this.extratoValor = extratoValor;
	}
	public ContaCaixa getExtratoContaCaixa() {
		return extratoContaCaixa;
	}
	public void setExtratoContaCaixa(ContaCaixa extratoContaCaixa) {
		this.extratoContaCaixa = extratoContaCaixa;
	}
	public String getLancamentoHistorico() {
		return lancamentoHistorico;
	}
	public void setLancamentoHistorico(String lancamentoHistorico) {
		this.lancamentoHistorico = lancamentoHistorico;
	}
	public BigDecimal getLancamentoValor() {
		return lancamentoValor;
	}
	public void setLancamentoValor(BigDecimal lancamentoValor) {
		this.lancamentoValor = lancamentoValor;
	}
	public String getLancamentoTipo() {
		return lancamentoTipo;
	}
	public void setLancamentoTipo(String lancamentoTipo) {
		this.lancamentoTipo = lancamentoTipo;
	}
	public ContabilHistorico getLancamentoContabilHistorico() {
		return lancamentoContabilHistorico;
	}
	public void setLancamentoContabilHistorico(ContabilHistorico lancamentoContabilHistorico) {
		this.lancamentoContabilHistorico = lancamentoContabilHistorico;
	}
	public ContabilConta getLancamentoContabilConta() {
		return lancamentoContabilConta;
	}
	public void setLancamentoContabilConta(ContabilConta lancamentoContabilConta) {
		this.lancamentoContabilConta = lancamentoContabilConta;
	}


}
