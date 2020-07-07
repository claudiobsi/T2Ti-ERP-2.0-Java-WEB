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


@Entity
@Table(name = "CTE_RODOVIARIO")
public class CteRodoviario implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @Column(name = "RNTRC")
    private String rntrc;
    @Temporal(TemporalType.DATE)
    @Column(name = "DATA_PREVISTA_ENTREGA")
    private Date dataPrevistaEntrega;
    @Column(name = "INDICADOR_LOTACAO")
    private Integer indicadorLotacao;
    @Column(name = "CIOT")
    private Integer ciot;
    @JoinColumn(name = "ID_CTE_CABECALHO", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private CteCabecalho cteCabecalho;
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "cteRodoviario", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<CteRodoviarioOcc> listaCteRodoviarioOcc;

    public CteRodoviario() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRntrc() {
        return rntrc;
    }

    public void setRntrc(String rntrc) {
        this.rntrc = rntrc;
    }

    public Date getDataPrevistaEntrega() {
        return dataPrevistaEntrega;
    }

    public void setDataPrevistaEntrega(Date dataPrevistaEntrega) {
        this.dataPrevistaEntrega = dataPrevistaEntrega;
    }

    public Integer getIndicadorLotacao() {
        return indicadorLotacao;
    }

    public void setIndicadorLotacao(Integer indicadorLotacao) {
        this.indicadorLotacao = indicadorLotacao;
    }

    public Integer getCiot() {
        return ciot;
    }

    public void setCiot(Integer ciot) {
        this.ciot = ciot;
    }

    public CteCabecalho getCteCabecalho() {
        return cteCabecalho;
    }

    public void setCteCabecalho(CteCabecalho cteCabecalho) {
        this.cteCabecalho = cteCabecalho;
    }

    @Override
    public String toString() {
        return "com.t2tierp.model.bean.cte.CteRodoviario[id=" + id + "]";
    }

	public Set<CteRodoviarioOcc> getListaCteRodoviarioOcc() {
		return listaCteRodoviarioOcc;
	}

	public void setListaCteRodoviarioOcc(Set<CteRodoviarioOcc> listaCteRodoviarioOcc) {
		this.listaCteRodoviarioOcc = listaCteRodoviarioOcc;
	}

}
