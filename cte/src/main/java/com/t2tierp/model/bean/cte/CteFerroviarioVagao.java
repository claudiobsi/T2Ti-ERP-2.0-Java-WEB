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
package com.t2tierp.model.bean.cte;

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
@Table(name = "CTE_FERROVIARIO_VAGAO")
public class CteFerroviarioVagao implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @Column(name = "NUMERO_VAGAO")
    private Integer numeroVagao;
    @Column(name = "CAPACIDADE")
    private BigDecimal capacidade;
    @Column(name = "TIPO_VAGAO")
    private String tipoVagao;
    @Column(name = "PESO_REAL")
    private BigDecimal pesoReal;
    @Column(name = "PESO_BC")
    private BigDecimal pesoBc;
    @JoinColumn(name = "ID_CTE_FERROVIARIO", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private CteFerroviario cteFerroviario;

    public CteFerroviarioVagao() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getNumeroVagao() {
        return numeroVagao;
    }

    public void setNumeroVagao(Integer numeroVagao) {
        this.numeroVagao = numeroVagao;
    }

    public BigDecimal getCapacidade() {
        return capacidade;
    }

    public void setCapacidade(BigDecimal capacidade) {
        this.capacidade = capacidade;
    }

    public String getTipoVagao() {
        return tipoVagao;
    }

    public void setTipoVagao(String tipoVagao) {
        this.tipoVagao = tipoVagao;
    }

    public BigDecimal getPesoReal() {
        return pesoReal;
    }

    public void setPesoReal(BigDecimal pesoReal) {
        this.pesoReal = pesoReal;
    }

    public BigDecimal getPesoBc() {
        return pesoBc;
    }

    public void setPesoBc(BigDecimal pesoBc) {
        this.pesoBc = pesoBc;
    }

    public CteFerroviario getCteFerroviario() {
        return cteFerroviario;
    }

    public void setCteFerroviario(CteFerroviario cteFerroviario) {
        this.cteFerroviario = cteFerroviario;
    }

    @Override
    public String toString() {
        return "com.t2tierp.model.bean.cte.CteFerroviarioVagao[id=" + id + "]";
    }

}
