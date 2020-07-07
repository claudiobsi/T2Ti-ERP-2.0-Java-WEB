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
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

import com.t2tierp.model.bean.cadastros.Colaborador;


@Entity
@Table(name = "AGENDA_COMPROMISSO")
public class AgendaCompromisso implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @Temporal(TemporalType.DATE)
    @Column(name = "DATA_COMPROMISSO")
    private Date dataCompromisso;
    @Column(name = "HORA")
    private String hora;
    @Column(name = "DURACAO")
    private Integer duracao;
    @Column(name = "ONDE")
    private String onde;
    @Column(name = "DESCRICAO")
    private String descricao;
    @Column(name = "TIPO")
    private Integer tipo;
    @JoinColumn(name = "ID_AGENDA_CATEGORIA_COMPROMISSO", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private AgendaCategoriaCompromisso agendaCategoriaCompromisso;
    @JoinColumn(name = "ID_COLABORADOR", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Colaborador colaborador;
	@OneToOne(fetch = FetchType.LAZY, mappedBy = "agendaCompromisso", cascade = CascadeType.ALL, orphanRemoval = true)
	private ReuniaoSalaEvento reuniaoSalaEvento;
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "agendaCompromisso", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<AgendaCompromissoConvidado> listaAgendaCompromissoConvidado;

    public AgendaCompromisso() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Date getDataCompromisso() {
        return dataCompromisso;
    }

    public void setDataCompromisso(Date dataCompromisso) {
        this.dataCompromisso = dataCompromisso;
    }

    public String getHora() {
        return hora;
    }

    public void setHora(String hora) {
        this.hora = hora;
    }

    public Integer getDuracao() {
        return duracao;
    }

    public void setDuracao(Integer duracao) {
        this.duracao = duracao;
    }

    public String getOnde() {
        return onde;
    }

    public void setOnde(String onde) {
        this.onde = onde;
    }

    public String getDescricao() {
        return descricao;
    }

    public void setDescricao(String descricao) {
        this.descricao = descricao;
    }

    public Integer getTipo() {
        return tipo;
    }

    public void setTipo(Integer tipo) {
        this.tipo = tipo;
    }

    public AgendaCategoriaCompromisso getAgendaCategoriaCompromisso() {
        return agendaCategoriaCompromisso;
    }

    public void setAgendaCategoriaCompromisso(AgendaCategoriaCompromisso agendaCategoriaCompromisso) {
        this.agendaCategoriaCompromisso = agendaCategoriaCompromisso;
    }

    public Colaborador getColaborador() {
        return colaborador;
    }

    public void setColaborador(Colaborador colaborador) {
        this.colaborador = colaborador;
    }

    @Override
    public String toString() {
        return "com.t2tierp.model.bean.agendacorporativa.AgendaCompromisso[id=" + id + "]";
    }

	public Set<AgendaCompromissoConvidado> getListaAgendaCompromissoConvidado() {
		return listaAgendaCompromissoConvidado;
	}

	public void setListaAgendaCompromissoConvidado(Set<AgendaCompromissoConvidado> listaAgendaCompromissoConvidado) {
		this.listaAgendaCompromissoConvidado = listaAgendaCompromissoConvidado;
	}

	public ReuniaoSalaEvento getReuniaoSalaEvento() {
		return reuniaoSalaEvento;
	}

	public void setReuniaoSalaEvento(ReuniaoSalaEvento reuniaoSalaEvento) {
		this.reuniaoSalaEvento = reuniaoSalaEvento;
	}

}
