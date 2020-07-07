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
package com.t2tierp.model.bean.iventario;

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

import com.t2tierp.model.bean.cadastros.Empresa;


@Entity
@Table(name = "INVENTARIO_CONTAGEM_CAB")
public class InventarioContagemCab implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @Temporal(TemporalType.DATE)
    @Column(name = "DATA_CONTAGEM")
    private Date dataContagem;
    @Column(name = "ESTOQUE_ATUALIZADO")
    private String estoqueAtualizado;
    @Column(name = "TIPO")
    private String tipo;
    @JoinColumn(name = "ID_EMPRESA", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Empresa empresa;
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "inventarioContagemCab", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<InventarioContagemDet> listaInventarioContagemDet;

    public InventarioContagemCab() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getDataContagem() {
        return dataContagem;
    }

    public void setDataContagem(Date dataContagem) {
        this.dataContagem = dataContagem;
    }

    public String getEstoqueAtualizado() {
        return estoqueAtualizado;
    }

    public void setEstoqueAtualizado(String estoqueAtualizado) {
        this.estoqueAtualizado = estoqueAtualizado;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

    @Override
    public String toString() {
        return "com.t2tierp.model.bean.iventario.InventarioContagemCab[id=" + id + "]";
    }

	public Set<InventarioContagemDet> getListaInventarioContagemDet() {
		return listaInventarioContagemDet;
	}

	public void setListaInventarioContagemDet(Set<InventarioContagemDet> listaInventarioContagemDet) {
		this.listaInventarioContagemDet = listaInventarioContagemDet;
	}

}
