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
package com.t2tierp.controller.tributacao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.t2tierp.controller.AbstractController;
import com.t2tierp.model.bean.cadastros.TipoReceitaDipi;
import com.t2tierp.model.bean.tributacao.TributCofinsCodApuracao;
import com.t2tierp.model.bean.tributacao.TributConfiguraOfGt;
import com.t2tierp.model.bean.tributacao.TributGrupoTributario;
import com.t2tierp.model.bean.tributacao.TributIcmsUf;
import com.t2tierp.model.bean.tributacao.TributIpiDipi;
import com.t2tierp.model.bean.tributacao.TributOperacaoFiscal;
import com.t2tierp.model.bean.tributacao.TributPisCodApuracao;
import com.t2tierp.model.dao.InterfaceDAO;
import com.t2tierp.util.FacesContextUtil;

@ManagedBean
@ViewScoped
public class TributConfiguraOfGtController extends AbstractController<TributConfiguraOfGt> implements Serializable {

    private static final long serialVersionUID = 1L;
    @EJB
    private InterfaceDAO<TributGrupoTributario> tributGrupoTributarioDao;
    @EJB
    private InterfaceDAO<TributOperacaoFiscal> tributOperacaoFiscalDao;
    @EJB
    private InterfaceDAO<TipoReceitaDipi> tipoReceitaDipiDao;

	private TributIcmsUf tributIcmsUf;
	private TributIcmsUf tributIcmsUfSelecionado;
    
    @Override
    public Class<TributConfiguraOfGt> getClazz() {
        return TributConfiguraOfGt.class;
    }

    @Override
    public String getFuncaoBase() {
        return "TRIBUT_CONFIGURA_OF_GT";
    }

    @Override
    public void incluir() {
        super.incluir();
        
        TributPisCodApuracao pis = new TributPisCodApuracao();
        pis.setTributConfiguraOfGt(getObjeto());
        
        TributCofinsCodApuracao cofins = new TributCofinsCodApuracao();
        cofins.setTributConfiguraOfGt(getObjeto());
        
        TributIpiDipi ipi = new TributIpiDipi();
        ipi.setTributConfiguraOfGt(getObjeto());
        
        getObjeto().setListaTributIcmsUf(new HashSet<TributIcmsUf>());
	}

	public void incluirTributIcmsUf() {
        tributIcmsUf = new TributIcmsUf();
        tributIcmsUf.setTributConfiguraOfGt(getObjeto());
	}

	public void alterarTributIcmsUf() {
        tributIcmsUf = tributIcmsUfSelecionado;
	}

	public void salvarTributIcmsUf() {
        if (tributIcmsUf.getId() == null) {
            getObjeto().getListaTributIcmsUf().add(tributIcmsUf);
        }
        salvar("Registro salvo com sucesso!");
	}
	
	public void excluirTributIcmsUf() {
        if (tributIcmsUfSelecionado == null || tributIcmsUfSelecionado.getId() == null) {
            FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_INFO, "Nenhum registro selecionado!", null);
        } else {
            getObjeto().getListaTributIcmsUf().remove(tributIcmsUfSelecionado);
            salvar("Registro excluído com sucesso!");
        }
	}
    
    public List<TributGrupoTributario> getListaTributGrupoTributario(String nome) {
        List<TributGrupoTributario> listaTributGrupoTributario = new ArrayList<>();
        try {
            listaTributGrupoTributario = tributGrupoTributarioDao.getBeansLike(TributGrupoTributario.class, "descricao", nome);
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return listaTributGrupoTributario;
    }

    public List<TributOperacaoFiscal> getListaTributOperacaoFiscal(String nome) {
        List<TributOperacaoFiscal> listaTributOperacaoFiscal = new ArrayList<>();
        try {
            listaTributOperacaoFiscal = tributOperacaoFiscalDao.getBeansLike(TributOperacaoFiscal.class, "descricao", nome);
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return listaTributOperacaoFiscal;
    }
    
    public List<TipoReceitaDipi> getListaTipoReceitaDipi(String nome) {
        List<TipoReceitaDipi> listaTipoReceitaDipi = new ArrayList<>();
        try {
            listaTipoReceitaDipi = tipoReceitaDipiDao.getBeansLike(TipoReceitaDipi.class, "descricao", nome);
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return listaTipoReceitaDipi;
    }

	public TributIcmsUf getTributIcmsUf() {
		return tributIcmsUf;
	}

	public void setTributIcmsUf(TributIcmsUf tributIcmsUf) {
		this.tributIcmsUf = tributIcmsUf;
	}

	public TributIcmsUf getTributIcmsUfSelecionado() {
		return tributIcmsUfSelecionado;
	}

	public void setTributIcmsUfSelecionado(TributIcmsUf tributIcmsUfSelecionado) {
		this.tributIcmsUfSelecionado = tributIcmsUfSelecionado;
	}

}
