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
import com.t2tierp.model.bean.cadastros.AtividadeForCli;
import com.t2tierp.model.bean.cadastros.Cliente;
import com.t2tierp.model.bean.cadastros.Pessoa;
import com.t2tierp.model.bean.cadastros.SituacaoForCli;
import com.t2tierp.model.bean.contabilidade.ContabilConta;
import com.t2tierp.model.bean.tributacao.TributOperacaoFiscal;
import com.t2tierp.model.dao.Filtro;
import com.t2tierp.model.dao.InterfaceDAO;
import com.t2tierp.util.FacesContextUtil;

@ManagedBean
@ViewScoped
public class ClienteController extends AbstractController<Cliente> implements Serializable {

	private static final long serialVersionUID = 1L;
	@EJB
	private InterfaceDAO<Pessoa> pessoaDao;
	@EJB
	private InterfaceDAO<SituacaoForCli> situacaoDao;
	@EJB
	private InterfaceDAO<AtividadeForCli> atividadeDao;
	@EJB
	private InterfaceDAO<ContabilConta> contabilContaDao;
	@EJB
	private InterfaceDAO<TributOperacaoFiscal> operacaoFiscalDao;

	@Override
	public Class<Cliente> getClazz() {
		return Cliente.class;
	}

	@Override
	public String getFuncaoBase() {
		return "CLIENTE";
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
			Cliente c = dao.getBean(Cliente.class, filtros);
			if (c != null) {
				FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_WARN, "Já existe um cliente vinculado a pessoa selecionada.", "O registro não foi salvo.");
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

	public List<SituacaoForCli> getListaSituacaoForCli(String nome) {
		List<SituacaoForCli> listaSituacaoForCli = new ArrayList<>();
		try {
			listaSituacaoForCli = situacaoDao.getBeansLike(SituacaoForCli.class, "nome", nome);
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return listaSituacaoForCli;
	}

	public List<AtividadeForCli> getListaAtividadeForCli(String nome) {
		List<AtividadeForCli> listaAtividadeForCli = new ArrayList<>();
		try {
			listaAtividadeForCli = atividadeDao.getBeansLike(AtividadeForCli.class, "nome", nome);
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return listaAtividadeForCli;
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

	public List<TributOperacaoFiscal> getListaTributOperacaoFiscal(String descricao) {
		List<TributOperacaoFiscal> listaTributOperacaoFiscal = new ArrayList<>();
		try {
			listaTributOperacaoFiscal = operacaoFiscalDao.getBeansLike(TributOperacaoFiscal.class, "descricao", descricao);
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return listaTributOperacaoFiscal;
	}

}
