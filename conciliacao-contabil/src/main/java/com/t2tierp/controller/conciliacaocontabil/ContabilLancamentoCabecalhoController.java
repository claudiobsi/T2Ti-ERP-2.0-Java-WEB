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

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.t2tierp.controller.AbstractController;
import com.t2tierp.model.bean.contabilidade.ContabilConta;
import com.t2tierp.model.bean.contabilidade.ContabilHistorico;
import com.t2tierp.model.bean.contabilidade.ContabilLancamentoCabecalho;
import com.t2tierp.model.bean.contabilidade.ContabilLancamentoDetalhe;
import com.t2tierp.model.bean.contabilidade.ContabilLote;
import com.t2tierp.model.dao.InterfaceDAO;

@ManagedBean
@ViewScoped
public class ContabilLancamentoCabecalhoController extends AbstractController<ContabilLancamentoCabecalho> implements Serializable {

    private static final long serialVersionUID = 1L;
    @EJB
    private InterfaceDAO<ContabilLote> contabilLoteDao;
    @EJB
    private InterfaceDAO<ContabilConta> contabilContaDao;
    @EJB
    private InterfaceDAO<ContabilHistorico> contabilHistoricoDao;
	
    private ContabilLancamentoDetalhe contabilLancamentoDetalhe;
	private ContabilLancamentoDetalhe contabilLancamentoDetalheSelecionado;

    @Override
    public Class<ContabilLancamentoCabecalho> getClazz() {
        return ContabilLancamentoCabecalho.class;
    }

    @Override
    public String getFuncaoBase() {
        return "CONCILIACAO_CONTABIL";
    }

    public void conciliaLancamentos(){
        BigDecimal totalCreditos = BigDecimal.ZERO;
        BigDecimal totalDebitos = BigDecimal.ZERO;
        String conciliado = "N";

        for (ContabilLancamentoDetalhe d : getObjeto().getListaContabilLancamentoDetalhe()){
            if (d.getTipo().equals("C")){
                totalCreditos = totalCreditos.add(d.getValor());
            } else if (d.getTipo().equals("D")) {
                totalDebitos = totalDebitos.add(d.getValor());
            }
        }

        if (totalCreditos.compareTo(totalDebitos) == 0){
            conciliado = "S";
        }

        for (ContabilLancamentoDetalhe d : getObjeto().getListaContabilLancamentoDetalhe()){
            d.setConciliado(conciliado);
        }
    }
    
    public void estorna(){
        //Implementado a critério do Participante do T2Ti ERP
    	contabilLancamentoDetalheSelecionado.setConciliado("E");
    }

    public void complementa(){
        //Implementado a critério do Participante do T2Ti ERP
    	contabilLancamentoDetalheSelecionado.setConciliado("C");
    }

    public void transfere(){
        //Implementado a critério do Participante do T2Ti ERP
    	contabilLancamentoDetalheSelecionado.setConciliado("T");
    }
    
	public ContabilLancamentoDetalhe getContabilLancamentoDetalhe() {
		return contabilLancamentoDetalhe;
	}

	public void setContabilLancamentoDetalhe(ContabilLancamentoDetalhe contabilLancamentoDetalhe) {
		this.contabilLancamentoDetalhe = contabilLancamentoDetalhe;
	}

	public ContabilLancamentoDetalhe getContabilLancamentoDetalheSelecionado() {
		return contabilLancamentoDetalheSelecionado;
	}

	public void setContabilLancamentoDetalheSelecionado(ContabilLancamentoDetalhe contabilLancamentoDetalheSelecionado) {
		this.contabilLancamentoDetalheSelecionado = contabilLancamentoDetalheSelecionado;
	}

}
