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
package com.t2tierp.model.bean.orcamento;

import com.t2tierp.model.bean.financeiro.NaturezaFinanceira;
import java.io.Serializable;
import java.math.BigDecimal;
import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.Table;


@Entity
@Table(name = "ORCAMENTO_FLUXO_CAIXA_DETALHE")
public class OrcamentoFluxoCaixaDetalhe implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @Column(name = "PERIODO")
    private String periodo;
    @Column(name = "VALOR_ORCADO")
    private BigDecimal valorOrcado;
    @Column(name = "VALOR_REALIZADO")
    private BigDecimal valorRealizado;
    @Column(name = "TAXA_VARIACAO")
    private BigDecimal taxaVariacao;
    @Column(name = "VALOR_VARIACAO")
    private BigDecimal valorVariacao;
    @JoinColumn(name = "ID_ORCAMENTO_FLUXO_CAIXA", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private OrcamentoFluxoCaixa orcamentoFluxoCaixa;
    @JoinColumn(name = "ID_NATUREZA_FINANCEIRA", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private NaturezaFinanceira naturezaFinanceira;

    public OrcamentoFluxoCaixaDetalhe() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getPeriodo() {
        return periodo;
    }

    public void setPeriodo(String periodo) {
        this.periodo = periodo;
    }

    public BigDecimal getValorOrcado() {
        return valorOrcado;
    }

    public void setValorOrcado(BigDecimal valorOrcado) {
        this.valorOrcado = valorOrcado;
    }

    public BigDecimal getValorRealizado() {
        return valorRealizado;
    }

    public void setValorRealizado(BigDecimal valorRealizado) {
        this.valorRealizado = valorRealizado;
    }

    public BigDecimal getTaxaVariacao() {
        return taxaVariacao;
    }

    public void setTaxaVariacao(BigDecimal taxaVariacao) {
        this.taxaVariacao = taxaVariacao;
    }

    public BigDecimal getValorVariacao() {
        return valorVariacao;
    }

    public void setValorVariacao(BigDecimal valorVariacao) {
        this.valorVariacao = valorVariacao;
    }

    public OrcamentoFluxoCaixa getOrcamentoFluxoCaixa() {
        return orcamentoFluxoCaixa;
    }

    public void setOrcamentoFluxoCaixa(OrcamentoFluxoCaixa orcamentoFluxoCaixa) {
        this.orcamentoFluxoCaixa = orcamentoFluxoCaixa;
    }

    public NaturezaFinanceira getNaturezaFinanceira() {
        return naturezaFinanceira;
    }

    public void setNaturezaFinanceira(NaturezaFinanceira naturezaFinanceira) {
        this.naturezaFinanceira = naturezaFinanceira;
    }

    @Override
    public String toString() {
        return "com.t2tierp.model.bean.orcamento.OrcamentoFluxoCaixaDetalhe[id=" + id + "]";
    }

}
