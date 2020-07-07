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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.t2tierp.controller.AbstractController;
import com.t2tierp.model.bean.cadastros.ContaCaixa;
import com.t2tierp.model.bean.conciliacaocontabil.ConciliacaoBancaria;
import com.t2tierp.model.bean.contabilidade.ContabilLancamentoDetalhe;
import com.t2tierp.model.bean.financeiro.FinExtratoContaBanco;
import com.t2tierp.model.dao.Filtro;
import com.t2tierp.model.dao.InterfaceDAO;
import com.t2tierp.util.FacesContextUtil;

@ManagedBean
@ViewScoped
public class ConciliacaoBancariaController extends AbstractController<ContaCaixa> implements Serializable {

    private static final long serialVersionUID = 1L;
    @EJB
    private InterfaceDAO<FinExtratoContaBanco> extratoDAO;
    @EJB
    private InterfaceDAO<ContabilLancamentoDetalhe> lancamentoDAO;
    
    private List<FinExtratoContaBanco> extratoContaBanco;
    private List<ContabilLancamentoDetalhe> contabilLancamentoDetalhe;
    private List<ConciliacaoBancaria> conciliacaoBancaria;
    private Date dataInicial;
    private Date dataFinal;
    
    @Override
    public Class<ContaCaixa> getClazz() {
        return ContaCaixa.class;
    }

    @Override
    public String getFuncaoBase() {
        return "CONTA_CAIXA";
    }

    public void carregaDados() {
    	try {
    		List<Filtro> filtros = new ArrayList<>();
    		filtros.add(new Filtro(Filtro.AND, "dataMovimento", Filtro.MAIOR_OU_IGUAL, dataInicial));
    		filtros.add(new Filtro(Filtro.AND, "dataMovimento", Filtro.MENOR_OU_IGUAL, dataFinal));
    		extratoContaBanco = extratoDAO.getBeans(FinExtratoContaBanco.class, filtros);
    		
    		filtros.clear();
    		filtros.add(new Filtro(Filtro.AND, "contabilLancamentoCabecalho.dataLancamento", Filtro.MAIOR_OU_IGUAL, dataInicial));
    		filtros.add(new Filtro(Filtro.AND, "contabilLancamentoCabecalho.dataLancamento", Filtro.MENOR_OU_IGUAL, dataFinal));
    		contabilLancamentoDetalhe = lancamentoDAO.getBeans(ContabilLancamentoDetalhe.class, filtros);
    		
    	} catch (Exception e) {
    		FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro ao carregar os dados!", e.getMessage());
    	}
    }

    public void conciliaLancamentos() {
    	try {
        	FinExtratoContaBanco extrato;
            ContabilLancamentoDetalhe lancamento;
            ConciliacaoBancaria conciliado;
            conciliacaoBancaria = new ArrayList<>();
            for (int i = 0; i < extratoContaBanco.size(); i++) {
                extrato = extratoContaBanco.get(i);
                for (int j = 0; j < contabilLancamentoDetalhe.size(); j++) {
                    lancamento = contabilLancamentoDetalhe.get(j);

                    if (extrato.getValor().compareTo(lancamento.getValor()) == 0) {
                        conciliado = new ConciliacaoBancaria();

                        conciliado.setExtratoAno(extrato.getAno());
                        conciliado.setExtratoDataBalancete(extrato.getDataBalancete());
                        conciliado.setExtratoDataMovimento(extrato.getDataMovimento());
                        conciliado.setExtratoHistorico(extrato.getHistorico());
                        conciliado.setExtratoMes(extrato.getMes());
                        conciliado.setExtratoValor(extrato.getValor());
                        conciliado.setExtratoContaCaixa(extrato.getContaCaixa());
                        conciliado.setLancamentoContabilConta(lancamento.getContabilConta());
                        conciliado.setLancamentoContabilHistorico(lancamento.getContabilHistorico());
                        conciliado.setLancamentoHistorico(lancamento.getHistorico());
                        conciliado.setLancamentoTipo(lancamento.getTipo());
                        conciliado.setLancamentoValor(lancamento.getValor());

                        conciliacaoBancaria.add(conciliado);
                    }
                }
            }
    	} catch (Exception e) {
    		FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro ao carregar os dados!", e.getMessage());
    	}
    }
    
	public List<FinExtratoContaBanco> getExtratoContaBanco() {
		return extratoContaBanco;
	}

	public void setExtratoContaBanco(List<FinExtratoContaBanco> extratoContaBanco) {
		this.extratoContaBanco = extratoContaBanco;
	}

	public List<ContabilLancamentoDetalhe> getContabilLancamentoDetalhe() {
		return contabilLancamentoDetalhe;
	}

	public void setContabilLancamentoDetalhe(List<ContabilLancamentoDetalhe> contabilLancamentoDetalhe) {
		this.contabilLancamentoDetalhe = contabilLancamentoDetalhe;
	}

	public List<ConciliacaoBancaria> getConciliacaoBancaria() {
		return conciliacaoBancaria;
	}

	public void setConciliacaoBancaria(List<ConciliacaoBancaria> conciliacaoBancaria) {
		this.conciliacaoBancaria = conciliacaoBancaria;
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
