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
package com.t2tierp.model.bean.ordemservico;

import java.io.Serializable;
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
@Table(name = "OS_ABERTURA_EQUIPAMENTO")
public class OsAberturaEquipamento implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @Column(name = "NUMERO_SERIE")
    private String numeroSerie;
    @Column(name = "TIPO_COBERTURA")
    private Integer tipoCobertura;
    @JoinColumn(name = "ID_OS_ABERTURA", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private OsAbertura osAbertura;
    @JoinColumn(name = "ID_OS_EQUIPAMENTO", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private OsEquipamento osEquipamento;

    public OsAberturaEquipamento() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNumeroSerie() {
        return numeroSerie;
    }

    public void setNumeroSerie(String numeroSerie) {
        this.numeroSerie = numeroSerie;
    }

    public Integer getTipoCobertura() {
        return tipoCobertura;
    }

    public void setTipoCobertura(Integer tipoCobertura) {
        this.tipoCobertura = tipoCobertura;
    }

    public OsAbertura getOsAbertura() {
        return osAbertura;
    }

    public void setOsAbertura(OsAbertura osAbertura) {
        this.osAbertura = osAbertura;
    }

    public OsEquipamento getOsEquipamento() {
        return osEquipamento;
    }

    public void setOsEquipamento(OsEquipamento osEquipamento) {
        this.osEquipamento = osEquipamento;
    }

    @Override
    public String toString() {
        return "com.t2tierp.model.bean.ordemservico.OsAberturaEquipamento[id=" + id + "]";
    }

}
