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
package com.t2tierp.model.bean.cadastros;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;
import java.util.Set;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;


@Entity
@Table(name = "EMPRESA")
public class Empresa implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @Column(name = "RAZAO_SOCIAL")
    private String razaoSocial;
    @Column(name = "NOME_FANTASIA")
    private String nomeFantasia;
    @Column(name = "CNPJ")
    private String cnpj;
    @Column(name = "INSCRICAO_ESTADUAL")
    private String inscricaoEstadual;
    @Column(name = "INSCRICAO_ESTADUAL_ST")
    private String inscricaoEstadualSt;
    @Column(name = "INSCRICAO_MUNICIPAL")
    private String inscricaoMunicipal;
    @Column(name = "INSCRICAO_JUNTA_COMERCIAL")
    private String inscricaoJuntaComercial;
    @Temporal(TemporalType.DATE)
    @Column(name = "DATA_INSC_JUNTA_COMERCIAL")
    private Date dataInscJuntaComercial;
    @Column(name = "TIPO")
    private String tipo;
    @Temporal(TemporalType.DATE)
    @Column(name = "DATA_CADASTRO")
    private Date dataCadastro;
    @Temporal(TemporalType.DATE)
    @Column(name = "DATA_INICIO_ATIVIDADES")
    private Date dataInicioAtividades;
    @Column(name = "SUFRAMA")
    private String suframa;
    @Column(name = "EMAIL")
    private String email;
    @Column(name = "IMAGEM_LOGOTIPO")
    private String imagemLogotipo;
    @Column(name = "CRT")
    private String crt;
    @Column(name = "TIPO_REGIME")
    private String tipoRegime;
    @Column(name = "ALIQUOTA_PIS")
    private BigDecimal aliquotaPis;
    @Column(name = "CONTATO")
    private String contato;
    @Column(name = "ALIQUOTA_COFINS")
    private BigDecimal aliquotaCofins;
    @Column(name = "CODIGO_IBGE_CIDADE")
    private Integer codigoIbgeCidade;
    @Column(name = "CODIGO_IBGE_UF")
    private Integer codigoIbgeUf;
    @Column(name = "CODIGO_TERCEIROS")
    private Integer codigoTerceiros;
    @Column(name = "CODIGO_GPS")
    private Integer codigoGps;
    @Column(name = "ALIQUOTA_SAT")
    private BigDecimal aliquotaSat;
    @Column(name = "CEI")
    private String cei;
    @Column(name = "CODIGO_CNAE_PRINCIPAL")
    private String codigoCnaePrincipal;
    @Column(name = "TIPO_CONTROLE_ESTOQUE")
    private String tipoControleEstoque;
    @JoinColumn(name = "ID_CONTADOR", referencedColumnName = "ID")
    @ManyToOne
    private Contador contador;
    @JoinColumn(name = "ID_FPAS", referencedColumnName = "ID")
    @ManyToOne
    private Fpas fpas;
    @JoinColumn(name = "ID_SINDICATO_PATRONAL", referencedColumnName = "ID")
    @ManyToOne
    private Sindicato sindicato;
    @JoinColumn(name = "ID_EMPRESA", referencedColumnName = "ID")
    @ManyToOne
    private Empresa empresa;
    @OneToMany(fetch=FetchType.EAGER, mappedBy="empresa")
    private Set<EmpresaEndereco> listaEndereco;
    @ManyToMany(fetch=FetchType.LAZY, mappedBy = "listaEmpresa")
    private Set<Pessoa> listaPessoa;
    @Transient
    private byte[] imagem;

    public Empresa() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRazaoSocial() {
        return razaoSocial;
    }

    public void setRazaoSocial(String razaoSocial) {
        this.razaoSocial = razaoSocial;
    }

    public String getNomeFantasia() {
        return nomeFantasia;
    }

    public void setNomeFantasia(String nomeFantasia) {
        this.nomeFantasia = nomeFantasia;
    }

    public String getCnpj() {
        return cnpj;
    }

    public void setCnpj(String cnpj) {
        this.cnpj = cnpj;
    }

    public String getInscricaoEstadual() {
        return inscricaoEstadual;
    }

    public void setInscricaoEstadual(String inscricaoEstadual) {
        this.inscricaoEstadual = inscricaoEstadual;
    }

    public String getInscricaoEstadualSt() {
        return inscricaoEstadualSt;
    }

    public void setInscricaoEstadualSt(String inscricaoEstadualSt) {
        this.inscricaoEstadualSt = inscricaoEstadualSt;
    }

    public String getInscricaoMunicipal() {
        return inscricaoMunicipal;
    }

    public void setInscricaoMunicipal(String inscricaoMunicipal) {
        this.inscricaoMunicipal = inscricaoMunicipal;
    }

    public String getInscricaoJuntaComercial() {
        return inscricaoJuntaComercial;
    }

    public void setInscricaoJuntaComercial(String inscricaoJuntaComercial) {
        this.inscricaoJuntaComercial = inscricaoJuntaComercial;
    }

    public Date getDataInscJuntaComercial() {
        return dataInscJuntaComercial;
    }

    public void setDataInscJuntaComercial(Date dataInscJuntaComercial) {
        this.dataInscJuntaComercial = dataInscJuntaComercial;
    }

    public String getTipo() {
        return tipo;
    }

    public void setTipo(String tipo) {
        this.tipo = tipo;
    }

    public Date getDataCadastro() {
        return dataCadastro;
    }

    public void setDataCadastro(Date dataCadastro) {
        this.dataCadastro = dataCadastro;
    }

    public Date getDataInicioAtividades() {
        return dataInicioAtividades;
    }

    public void setDataInicioAtividades(Date dataInicioAtividades) {
        this.dataInicioAtividades = dataInicioAtividades;
    }

    public String getSuframa() {
        return suframa;
    }

    public void setSuframa(String suframa) {
        this.suframa = suframa;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImagemLogotipo() {
        return imagemLogotipo;
    }

    public void setImagemLogotipo(String imagemLogotipo) {
        this.imagemLogotipo = imagemLogotipo;
    }

    public String getCrt() {
        return crt;
    }

    public void setCrt(String crt) {
        this.crt = crt;
    }

    public String getTipoRegime() {
        return tipoRegime;
    }

    public void setTipoRegime(String tipoRegime) {
        this.tipoRegime = tipoRegime;
    }

    public BigDecimal getAliquotaPis() {
        return aliquotaPis;
    }

    public void setAliquotaPis(BigDecimal aliquotaPis) {
        this.aliquotaPis = aliquotaPis;
    }

    public String getContato() {
        return contato;
    }

    public void setContato(String contato) {
        this.contato = contato;
    }

    public BigDecimal getAliquotaCofins() {
        return aliquotaCofins;
    }

    public void setAliquotaCofins(BigDecimal aliquotaCofins) {
        this.aliquotaCofins = aliquotaCofins;
    }

    public Integer getCodigoIbgeCidade() {
        return codigoIbgeCidade;
    }

    public void setCodigoIbgeCidade(Integer codigoIbgeCidade) {
        this.codigoIbgeCidade = codigoIbgeCidade;
    }

    public Integer getCodigoIbgeUf() {
        return codigoIbgeUf;
    }

    public void setCodigoIbgeUf(Integer codigoIbgeUf) {
        this.codigoIbgeUf = codigoIbgeUf;
    }

    public Integer getCodigoTerceiros() {
        return codigoTerceiros;
    }

    public void setCodigoTerceiros(Integer codigoTerceiros) {
        this.codigoTerceiros = codigoTerceiros;
    }

    public Integer getCodigoGps() {
        return codigoGps;
    }

    public void setCodigoGps(Integer codigoGps) {
        this.codigoGps = codigoGps;
    }

    public BigDecimal getAliquotaSat() {
        return aliquotaSat;
    }

    public void setAliquotaSat(BigDecimal aliquotaSat) {
        this.aliquotaSat = aliquotaSat;
    }

    public String getCei() {
        return cei;
    }

    public void setCei(String cei) {
        this.cei = cei;
    }

    public String getCodigoCnaePrincipal() {
        return codigoCnaePrincipal;
    }

    public void setCodigoCnaePrincipal(String codigoCnaePrincipal) {
        this.codigoCnaePrincipal = codigoCnaePrincipal;
    }

    public String getTipoControleEstoque() {
        return tipoControleEstoque;
    }

    public void setTipoControleEstoque(String tipoControleEstoque) {
        this.tipoControleEstoque = tipoControleEstoque;
    }

    public Contador getContador() {
        return contador;
    }

    public void setContador(Contador contador) {
        this.contador = contador;
    }

    public Fpas getFpas() {
        return fpas;
    }

    public void setFpas(Fpas fpas) {
        this.fpas = fpas;
    }

    public Sindicato getSindicato() {
        return sindicato;
    }

    public void setSindicato(Sindicato sindicato) {
        this.sindicato = sindicato;
    }

    public Empresa getEmpresa() {
        return empresa;
    }

    public void setEmpresa(Empresa empresa) {
        this.empresa = empresa;
    }

	public Set<EmpresaEndereco> getListaEndereco() {
		return listaEndereco;
	}

	public void setListaEndereco(Set<EmpresaEndereco> listaEndereco) {
		this.listaEndereco = listaEndereco;
	}

	public byte[] getImagem() {
		return imagem;
	}

	public void setImagem(byte[] imagem) {
		this.imagem = imagem;
	}

	public Set<Pessoa> getListaPessoa() {
		return listaPessoa;
	}

	public void setListaPessoa(Set<Pessoa> listaPessoa) {
		this.listaPessoa = listaPessoa;
	}
    
    @Override
    public String toString() {
        return "com.t2tierp.model.bean.cadastros.Empresa[id=" + id + "]";
    }

}
