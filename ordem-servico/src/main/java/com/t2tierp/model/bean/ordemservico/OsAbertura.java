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

import com.t2tierp.model.bean.cadastros.Cliente;
import com.t2tierp.model.bean.cadastros.Colaborador;


@Entity
@Table(name = "OS_ABERTURA")
public class OsAbertura implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @Column(name = "NUMERO")
    private String numero;
    @Temporal(TemporalType.DATE)
    @Column(name = "DATA_INICIO")
    private Date dataInicio;
    @Column(name = "HORA_INICIO")
    private String horaInicio;
    @Temporal(TemporalType.DATE)
    @Column(name = "DATA_PREVISAO")
    private Date dataPrevisao;
    @Column(name = "HORA_PREVISAO")
    private String horaPrevisao;
    @Temporal(TemporalType.DATE)
    @Column(name = "DATA_FIM")
    private Date dataFim;
    @Column(name = "HORA_FIM")
    private String horaFim;
    @Column(name = "NOME_CONTATO")
    private String nomeContato;
    @Column(name = "FONE_CONTATO")
    private String foneContato;
    @Column(name = "OBSERVACAO_CLIENTE")
    private String observacaoCliente;
    @Column(name = "OBSERVACAO_ABERTURA")
    private String observacaoAbertura;
    @JoinColumn(name = "ID_CLIENTE", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Cliente cliente;
    @JoinColumn(name = "ID_COLABORADOR", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private Colaborador colaborador;
    @JoinColumn(name = "ID_OS_STATUS", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private OsStatus osStatus;
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "osAbertura", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<OsEvolucao> listaOsEvolucao;
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "osAbertura", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<OsProdutoServico> listaOsProdutoServico;
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "osAbertura", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<OsAberturaEquipamento> listaOsAberturaEquipamento;

    public OsAbertura() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNumero() {
        return numero;
    }

    public void setNumero(String numero) {
        this.numero = numero;
    }

    public Date getDataInicio() {
        return dataInicio;
    }

    public void setDataInicio(Date dataInicio) {
        this.dataInicio = dataInicio;
    }

    public String getHoraInicio() {
        return horaInicio;
    }

    public void setHoraInicio(String horaInicio) {
        this.horaInicio = horaInicio;
    }

    public Date getDataPrevisao() {
        return dataPrevisao;
    }

    public void setDataPrevisao(Date dataPrevisao) {
        this.dataPrevisao = dataPrevisao;
    }

    public String getHoraPrevisao() {
        return horaPrevisao;
    }

    public void setHoraPrevisao(String horaPrevisao) {
        this.horaPrevisao = horaPrevisao;
    }

    public Date getDataFim() {
        return dataFim;
    }

    public void setDataFim(Date dataFim) {
        this.dataFim = dataFim;
    }

    public String getHoraFim() {
        return horaFim;
    }

    public void setHoraFim(String horaFim) {
        this.horaFim = horaFim;
    }

    public String getNomeContato() {
        return nomeContato;
    }

    public void setNomeContato(String nomeContato) {
        this.nomeContato = nomeContato;
    }

    public String getFoneContato() {
        return foneContato;
    }

    public void setFoneContato(String foneContato) {
        this.foneContato = foneContato;
    }

    public String getObservacaoCliente() {
        return observacaoCliente;
    }

    public void setObservacaoCliente(String observacaoCliente) {
        this.observacaoCliente = observacaoCliente;
    }

    public String getObservacaoAbertura() {
        return observacaoAbertura;
    }

    public void setObservacaoAbertura(String observacaoAbertura) {
        this.observacaoAbertura = observacaoAbertura;
    }

    public Cliente getCliente() {
        return cliente;
    }

    public void setCliente(Cliente cliente) {
        this.cliente = cliente;
    }

    public Colaborador getColaborador() {
        return colaborador;
    }

    public void setColaborador(Colaborador colaborador) {
        this.colaborador = colaborador;
    }

    public OsStatus getOsStatus() {
        return osStatus;
    }

    public void setOsStatus(OsStatus osStatus) {
        this.osStatus = osStatus;
    }

    @Override
    public String toString() {
        return "com.t2tierp.model.bean.ordemservico.OsAbertura[id=" + id + "]";
    }

	public Set<OsEvolucao> getListaOsEvolucao() {
		return listaOsEvolucao;
	}

	public void setListaOsEvolucao(Set<OsEvolucao> listaOsEvolucao) {
		this.listaOsEvolucao = listaOsEvolucao;
	}

	public Set<OsProdutoServico> getListaOsProdutoServico() {
		return listaOsProdutoServico;
	}

	public void setListaOsProdutoServico(Set<OsProdutoServico> listaOsProdutoServico) {
		this.listaOsProdutoServico = listaOsProdutoServico;
	}

	public Set<OsAberturaEquipamento> getListaOsAberturaEquipamento() {
		return listaOsAberturaEquipamento;
	}

	public void setListaOsAberturaEquipamento(Set<OsAberturaEquipamento> listaOsAberturaEquipamento) {
		this.listaOsAberturaEquipamento = listaOsAberturaEquipamento;
	}

}
