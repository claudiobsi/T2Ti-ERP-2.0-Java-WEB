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
package com.t2tierp.controller.cadastros;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.t2tierp.controller.AbstractController;
import com.t2tierp.model.bean.cadastros.Cargo;
import com.t2tierp.model.bean.cadastros.Colaborador;
import com.t2tierp.model.bean.cadastros.NivelFormacao;
import com.t2tierp.model.bean.cadastros.Pessoa;
import com.t2tierp.model.bean.cadastros.Setor;
import com.t2tierp.model.bean.cadastros.Sindicato;
import com.t2tierp.model.bean.cadastros.SituacaoColaborador;
import com.t2tierp.model.bean.cadastros.TipoAdmissao;
import com.t2tierp.model.bean.cadastros.TipoColaborador;
import com.t2tierp.model.bean.contabilidade.ContabilConta;
import com.t2tierp.model.dao.Filtro;
import com.t2tierp.model.dao.InterfaceDAO;
import com.t2tierp.util.FacesContextUtil;

@ManagedBean
@ViewScoped
public class ColaboradorController extends AbstractController<Colaborador> implements Serializable {

    private static final long serialVersionUID = 1L;
	@EJB
	private InterfaceDAO<Pessoa> pessoaDao;
	@EJB
	private InterfaceDAO<ContabilConta> contabilContaDao;
	@EJB
	private InterfaceDAO<Sindicato> sindicatoDao;
	@EJB
	private InterfaceDAO<TipoAdmissao> tipoAdmissaoDao;
	@EJB
	private InterfaceDAO<TipoColaborador> tipoColaboradorDao;
	@EJB
	private InterfaceDAO<SituacaoColaborador> situacaoColaboradorDao;
	@EJB
	private InterfaceDAO<NivelFormacao> nivelFormacaoDao;
	@EJB
	private InterfaceDAO<Cargo> cargoDao;
	@EJB
	private InterfaceDAO<Setor> setorDao;

    @Override
    public Class<Colaborador> getClazz() {
        return Colaborador.class;
    }

    @Override
    public String getFuncaoBase() {
        return "COLABORADOR";
    }
    
	@Override
	public void incluir() {
		super.incluir();
		getObjeto().setDataCadastro(new Date());
	}
    
	@Override
	public void salvar(String mensagem) {
		try {
			List<Filtro> filtros = new ArrayList<>();
			filtros.add(new Filtro(Filtro.AND, "pessoa", Filtro.IGUAL, getObjeto().getPessoa()));
			if (getObjeto().getId() != null) {
				filtros.add(new Filtro(Filtro.AND, "id", Filtro.DIFERENTE, getObjeto().getId()));
			}
			Colaborador c = dao.getBean(Colaborador.class, filtros);
			if (c != null) {
				FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_WARN, "Já existe um colaborador vinculado a pessoa selecionada.", "O registro não foi salvo.");
			} else {
				super.salvar(mensagem);
			}
		} catch (Exception e) {
			FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro ao salvar o registro!", e.getMessage());
		}
	}
    
	public List<Pessoa> getListaPessoa(String nome) {
		List<Pessoa> listaPessoa = new ArrayList<>();
		try {
			listaPessoa = pessoaDao.getBeansLike(Pessoa.class, "nome", nome);
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return listaPessoa;
	}

	public List<ContabilConta> getListaContabilConta(String descricao) {
		List<ContabilConta> listaContabilConta = new ArrayList<>();
		try {
			listaContabilConta = contabilContaDao.getBeansLike(ContabilConta.class, "descricao", descricao);
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return listaContabilConta;
	}
	
	public List<Sindicato> getListaSindicato(String nome) {
		List<Sindicato> listaSindicato = new ArrayList<>();
		try {
			listaSindicato = sindicatoDao.getBeansLike(Sindicato.class, "nome", nome);
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return listaSindicato;
	}
	
	public List<TipoAdmissao> getListaTipoAdmissao(String nome) {
		List<TipoAdmissao> listaTipoAdmissao = new ArrayList<>();
		try {
			listaTipoAdmissao = tipoAdmissaoDao.getBeansLike(TipoAdmissao.class, "nome", nome);
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return listaTipoAdmissao;
	}
	
	public List<SituacaoColaborador> getListaSituacaoColaborador(String nome) {
		List<SituacaoColaborador> listaSituacaoColaborador = new ArrayList<>();
		try {
			listaSituacaoColaborador = situacaoColaboradorDao.getBeansLike(SituacaoColaborador.class, "nome", nome);
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return listaSituacaoColaborador;
	}

	public List<TipoColaborador> getListaTipoColaborador(String nome) {
		List<TipoColaborador> listaTipoColaborador = new ArrayList<>();
		try {
			listaTipoColaborador = tipoColaboradorDao.getBeansLike(TipoColaborador.class, "nome", nome);
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return listaTipoColaborador;
	}

	public List<NivelFormacao> getListaNivelFormacao(String nome) {
		List<NivelFormacao> listaNivelFormacao = new ArrayList<>();
		try {
			listaNivelFormacao = nivelFormacaoDao.getBeansLike(NivelFormacao.class, "nome", nome);
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return listaNivelFormacao;
	}
	
	public List<Cargo> getListaCargo(String nome) {
		List<Cargo> listaCargo = new ArrayList<>();
		try {
			listaCargo = cargoDao.getBeansLike(Cargo.class, "nome", nome);
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return listaCargo;
	}

	public List<Setor> getListaSetor(String nome) {
		List<Setor> listaSetor = new ArrayList<>();
		try {
			listaSetor = setorDao.getBeansLike(Setor.class, "nome", nome);
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return listaSetor;
	}
	
}
