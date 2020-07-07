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
import java.util.HashSet;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.t2tierp.controller.AbstractController;
import com.t2tierp.model.bean.cadastros.Empresa;
import com.t2tierp.model.bean.cadastros.EstadoCivil;
import com.t2tierp.model.bean.cadastros.Pessoa;
import com.t2tierp.model.bean.cadastros.PessoaContato;
import com.t2tierp.model.bean.cadastros.PessoaEndereco;
import com.t2tierp.model.bean.cadastros.PessoaFisica;
import com.t2tierp.model.bean.cadastros.PessoaJuridica;
import com.t2tierp.model.bean.cadastros.PessoaTelefone;
import com.t2tierp.model.dao.InterfaceDAO;
import com.t2tierp.util.FacesContextUtil;

@ManagedBean
@ViewScoped
public class PessoaController extends AbstractController<Pessoa> implements Serializable {

	private static final long serialVersionUID = 1L;

	@EJB
	private InterfaceDAO<EstadoCivil> estadoCivilDao;
	@EJB
	private InterfaceDAO<PessoaContato> pessoaContatoDao;
	@EJB
	private InterfaceDAO<PessoaEndereco> pessoaEnderecoDao;

	private PessoaContato pessoaContatoSelecionado;
	private PessoaContato pessoaContato;
	private PessoaEndereco pessoaEnderecoSelecionado;
	private PessoaEndereco pessoaEndereco;
	private PessoaTelefone pessoaTelefoneSelecionado;
	private PessoaTelefone pessoaTelefone;

	@Override
	public Class<Pessoa> getClazz() {
		return Pessoa.class;
	}

	@Override
	public String getFuncaoBase() {
		return "PESSOA";
	}

	@Override
	public void incluir() {
		super.incluir();
		PessoaFisica pessoaFisica = new PessoaFisica();
		pessoaFisica.setPessoa(getObjeto());
		PessoaJuridica pessoaJuridica = new PessoaJuridica();
		pessoaJuridica.setPessoa(getObjeto());
		
		getObjeto().setTipo("F");
		getObjeto().setPessoaFisica(pessoaFisica);
		getObjeto().setPessoaJuridica(pessoaJuridica);
		getObjeto().setListaPessoaContato(new HashSet<PessoaContato>());
		getObjeto().setListaPessoaEndereco(new HashSet<PessoaEndereco>());
		getObjeto().setListaPessoaTelefone(new HashSet<PessoaTelefone>());
		getObjeto().setListaEmpresa(new HashSet<Empresa>());
		getObjeto().getListaEmpresa().add(FacesContextUtil.getEmpresaUsuario());
	}

	@Override
	public void salvar(String mensagem) {
		removeMascaraCpfCnpj();
		if (getObjeto().getTipo().equals("F")) {
			getObjeto().setPessoaJuridica(null);
		} else {
			getObjeto().setPessoaFisica(null);
		}
		super.salvar(mensagem);
	}

	public void incluirContato() {
		pessoaContato = new PessoaContato();
		pessoaContato.setPessoa(getObjeto());
	}

	public void alterarContato() {
		pessoaContato = pessoaContatoSelecionado;
	}

	public void salvarContato() {
		if (pessoaContato.getId() == null) {
			getObjeto().getListaPessoaContato().add(pessoaContato);
		}
		salvar("Contato salvo com sucesso!");
	}

	public void excluirContato() {
		if (pessoaContatoSelecionado == null || pessoaContatoSelecionado.getId() == null) {
			FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_INFO, "Nenhum registro selecionado!", null);
		} else {
			getObjeto().getListaPessoaContato().remove(pessoaContatoSelecionado);
			salvar("Contato excluído com sucesso!");
		}
	}

	public void incluirEndereco() {
		pessoaEndereco = new PessoaEndereco();
		pessoaEndereco.setPessoa(getObjeto());
	}

	public void alterarEndereco() {
		pessoaEndereco = pessoaEnderecoSelecionado;
	}

	public void salvarEndereco() {
		if (pessoaEndereco.getCep() != null) {
			pessoaEndereco.setCep(pessoaEndereco.getCep().replaceAll("\\D", ""));
		}
		if (pessoaEndereco.getId() == null) {
			getObjeto().getListaPessoaEndereco().add(pessoaEndereco);
		}
		salvar("Endereço salvo com sucesso!");
	}

	public void excluirEndereco() {
		if (pessoaEnderecoSelecionado == null || pessoaEnderecoSelecionado.getId() == null) {
			FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_INFO, "Nenhum registro selecionado!", null);
		} else {
			getObjeto().getListaPessoaEndereco().remove(pessoaEnderecoSelecionado);
			salvar("Endereço excluído com sucesso!");
		}
	}

	public void incluirTelefone() {
		pessoaTelefone = new PessoaTelefone();
		pessoaTelefone.setPessoa(getObjeto());
	}

	public void alterarTelefone() {
		pessoaTelefone = pessoaTelefoneSelecionado;
	}

	public void salvarTelefone() {
		if (pessoaTelefone.getId() == null) {
			getObjeto().getListaPessoaTelefone().add(pessoaTelefone);
		}
		salvar("Telefone salvo com sucesso!");
	}

	public void excluirTelefone() {
		if (pessoaTelefoneSelecionado == null || pessoaTelefoneSelecionado.getId() == null) {
			FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_INFO, "Nenhum registro selecionado!", null);
		} else {
			getObjeto().getListaPessoaTelefone().remove(pessoaTelefoneSelecionado);
			salvar("Telefone excluído com sucesso!");
		}
	}
	
	public List<EstadoCivil> getListaEstadoCivil(String nome) {
		List<EstadoCivil> listaEstadoCivil = new ArrayList<>();
		try {
			listaEstadoCivil = estadoCivilDao.getBeansLike(EstadoCivil.class, "nome", nome);
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return listaEstadoCivil;
	}

	private void removeMascaraCpfCnpj() {
		if (getObjeto().getPessoaFisica() != null) {
			if (getObjeto().getPessoaFisica().getCpf() != null) {
				getObjeto().getPessoaFisica().setCpf(getObjeto().getPessoaFisica().getCpf().replaceAll("\\D", ""));
			}
		}
		if (getObjeto().getPessoaJuridica() != null) {
			if (getObjeto().getPessoaJuridica().getCnpj() != null) {
				getObjeto().getPessoaJuridica().setCnpj(getObjeto().getPessoaJuridica().getCnpj().replaceAll("\\D", ""));
			}
		}
	}

	public PessoaContato getPessoaContatoSelecionado() {
		return pessoaContatoSelecionado;
	}

	public void setPessoaContatoSelecionado(PessoaContato pessoaContatoSelecionado) {
		this.pessoaContatoSelecionado = pessoaContatoSelecionado;
	}

	public PessoaContato getPessoaContato() {
		return pessoaContato;
	}

	public void setPessoaContato(PessoaContato pessoaContato) {
		this.pessoaContato = pessoaContato;
	}

	public PessoaEndereco getPessoaEnderecoSelecionado() {
		return pessoaEnderecoSelecionado;
	}

	public void setPessoaEnderecoSelecionado(PessoaEndereco pessoaEnderecoSelecionado) {
		this.pessoaEnderecoSelecionado = pessoaEnderecoSelecionado;
	}

	public PessoaEndereco getPessoaEndereco() {
		return pessoaEndereco;
	}

	public void setPessoaEndereco(PessoaEndereco pessoaEndereco) {
		this.pessoaEndereco = pessoaEndereco;
	}

	public PessoaTelefone getPessoaTelefoneSelecionado() {
		return pessoaTelefoneSelecionado;
	}

	public void setPessoaTelefoneSelecionado(PessoaTelefone pessoaTelefoneSelecionado) {
		this.pessoaTelefoneSelecionado = pessoaTelefoneSelecionado;
	}

	public PessoaTelefone getPessoaTelefone() {
		return pessoaTelefone;
	}

	public void setPessoaTelefone(PessoaTelefone pessoaTelefone) {
		this.pessoaTelefone = pessoaTelefone;
	}

}
