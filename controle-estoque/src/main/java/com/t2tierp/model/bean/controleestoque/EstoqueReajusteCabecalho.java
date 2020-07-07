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
package com.t2tierp.model.bean.controleestoque;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.t2tierp.model.bean.cadastros.Colaborador;


@Entity
@Table(name = "ESTOQUE_REAJUSTE_CABECALHO")
public class EstoqueReajusteCabecalho implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @Temporal(TemporalType.DATE)
    @Column(name = "DATA_REAJUSTE")
    private Date dataReajuste;
    @Column(name = "PORCENTAGEM")
    private BigDecimal porcentagem;
    @Column(name = "TIPO_REAJUSTE")
    private String tipoReajuste;
    @JoinColumn(name = "ID_COLABORADOR", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Colaborador colaborador;
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "estoqueReajusteCabecalho", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<EstoqueReajusteDetalhe> listaEstoqueReajusteDetalhe;

    public EstoqueReajusteCabecalho() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getDataReajuste() {
        return dataReajuste;
    }

    public void setDataReajuste(Date dataReajuste) {
        this.dataReajuste = dataReajuste;
    }

    public BigDecimal getPorcentagem() {
        return porcentagem;
    }

    public void setPorcentagem(BigDecimal porcentagem) {
        this.porcentagem = porcentagem;
    }

    public String getTipoReajuste() {
        return tipoReajuste;
    }

    public void setTipoReajuste(String tipoReajuste) {
        this.tipoReajuste = tipoReajuste;
    }

    public Colaborador getColaborador() {
        return colaborador;
    }

    public void setColaborador(Colaborador colaborador) {
        this.colaborador = colaborador;
    }

    @Override
    public String toString() {
        return "com.t2tierp.model.bean.controleestoque.EstoqueReajusteCabecalho[id=" + id + "]";
    }

	public Set<EstoqueReajusteDetalhe> getListaEstoqueReajusteDetalhe() {
		return listaEstoqueReajusteDetalhe;
	}

	public void setListaEstoqueReajusteDetalhe(Set<EstoqueReajusteDetalhe> listaEstoqueReajusteDetalhe) {
		this.listaEstoqueReajusteDetalhe = listaEstoqueReajusteDetalhe;
	}

}
