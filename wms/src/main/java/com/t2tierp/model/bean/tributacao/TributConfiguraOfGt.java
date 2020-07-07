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
package com.t2tierp.model.bean.tributacao;

import java.io.Serializable;
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


@Entity
@Table(name = "TRIBUT_CONFIGURA_OF_GT")
public class TributConfiguraOfGt implements Serializable {

    private static final long serialVersionUID = 1L;
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Basic(optional = false)
    @Column(name = "ID")
    private Integer id;
    @JoinColumn(name = "ID_TRIBUT_OPERACAO_FISCAL", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private TributOperacaoFiscal tributOperacaoFiscal;
    @JoinColumn(name = "ID_TRIBUT_GRUPO_TRIBUTARIO", referencedColumnName = "ID")
    @ManyToOne(optional = false)
    private TributGrupoTributario tributGrupoTributario;
	@OneToMany(fetch = FetchType.LAZY, mappedBy = "tributConfiguraOfGt", cascade = CascadeType.ALL, orphanRemoval = true)
	private Set<TributIcmsUf> listaTributIcmsUf;
	@OneToOne(fetch = FetchType.EAGER, mappedBy = "tributConfiguraOfGt", cascade = CascadeType.ALL)
	private TributPisCodApuracao tributPisCodApuracao;
	@OneToOne(fetch = FetchType.EAGER, mappedBy = "tributConfiguraOfGt", cascade = CascadeType.ALL)
	private TributCofinsCodApuracao tributCofinsCodApuracao;
	@OneToOne(fetch = FetchType.EAGER, mappedBy = "tributConfiguraOfGt", cascade = CascadeType.ALL)
	private TributIpiDipi tributIpiDipi;
    
    public TributConfiguraOfGt() {
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public TributOperacaoFiscal getTributOperacaoFiscal() {
        return tributOperacaoFiscal;
    }

    public void setTributOperacaoFiscal(TributOperacaoFiscal tributOperacaoFiscal) {
        this.tributOperacaoFiscal = tributOperacaoFiscal;
    }

    public TributGrupoTributario getTributGrupoTributario() {
        return tributGrupoTributario;
    }

    public void setTributGrupoTributario(TributGrupoTributario tributGrupoTributario) {
        this.tributGrupoTributario = tributGrupoTributario;
    }

    @Override
    public String toString() {
        return "com.t2tierp.model.bean.tributacao.TributConfiguraOfGt[id=" + id + "]";
    }

	public Set<TributIcmsUf> getListaTributIcmsUf() {
		return listaTributIcmsUf;
	}

	public void setListaTributIcmsUf(Set<TributIcmsUf> listaTributIcmsUf) {
		this.listaTributIcmsUf = listaTributIcmsUf;
	}

	public TributPisCodApuracao getTributPisCodApuracao() {
		return tributPisCodApuracao;
	}

	public void setTributPisCodApuracao(TributPisCodApuracao tributPisCodApuracao) {
		this.tributPisCodApuracao = tributPisCodApuracao;
	}

	public TributCofinsCodApuracao getTributCofinsCodApuracao() {
		return tributCofinsCodApuracao;
	}

	public void setTributCofinsCodApuracao(TributCofinsCodApuracao tributCofinsCodApuracao) {
		this.tributCofinsCodApuracao = tributCofinsCodApuracao;
	}

	public TributIpiDipi getTributIpiDipi() {
		return tributIpiDipi;
	}

	public void setTributIpiDipi(TributIpiDipi tributIpiDipi) {
		this.tributIpiDipi = tributIpiDipi;
	}

}
