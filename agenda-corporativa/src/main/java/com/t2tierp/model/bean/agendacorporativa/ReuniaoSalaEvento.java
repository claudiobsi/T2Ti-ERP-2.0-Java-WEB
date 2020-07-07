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
package com.t2tierp.model.bean.agendacorporativa;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;


@Entity
@Table(name = "REUNIAO_SALA_EVENTO")
public class ReuniaoSalaEvento implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @Temporal(TemporalType.DATE)
    @Column(name = "DATA_RESERVA")
    private Date dataReserva;
    @JoinColumn(name = "ID_REUNIAO_SALA", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private ReuniaoSala reuniaoSala;
    @JoinColumn(name = "ID_AGENDA_COMPROMISSO", referencedColumnName = "ID")
    @OneToOne(optional = false)
    private AgendaCompromisso agendaCompromisso;

    public ReuniaoSalaEvento() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getDataReserva() {
        return dataReserva;
    }

    public void setDataReserva(Date dataReserva) {
        this.dataReserva = dataReserva;
    }

    public ReuniaoSala getReuniaoSala() {
        return reuniaoSala;
    }

    public void setReuniaoSala(ReuniaoSala reuniaoSala) {
        this.reuniaoSala = reuniaoSala;
    }

    public AgendaCompromisso getAgendaCompromisso() {
        return agendaCompromisso;
    }

    public void setAgendaCompromisso(AgendaCompromisso agendaCompromisso) {
        this.agendaCompromisso = agendaCompromisso;
    }

    @Override
    public String toString() {
        return "com.t2tierp.model.bean.agendacorporativa.ReuniaoSalaEvento[id=" + id + "]";
    }

}
