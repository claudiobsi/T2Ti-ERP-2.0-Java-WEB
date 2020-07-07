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
 * all copies or substantial portions of the Software
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
package com.t2tierp.controller.lojavirtual;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;

import org.apache.commons.mail.MultiPartEmail;
import org.primefaces.model.menu.DefaultMenuItem;
import org.primefaces.model.menu.DefaultMenuModel;
import org.primefaces.model.menu.DefaultSubMenu;
import org.primefaces.model.menu.MenuModel;

import com.t2tierp.model.bean.cadastros.Cliente;
import com.t2tierp.model.bean.cadastros.Produto;
import com.t2tierp.model.bean.cadastros.ProdutoGrupo;
import com.t2tierp.model.bean.cadastros.ProdutoSubgrupo;
import com.t2tierp.model.bean.nfe.NfeConfiguracao;
import com.t2tierp.model.dao.Filtro;
import com.t2tierp.model.dao.InterfaceDAO;
import com.t2tierp.util.FacesContextUtil;

@ManagedBean
@SessionScoped
public class LojaVirtualController implements Serializable {

	private static final long serialVersionUID = 1L;

	@EJB
	private InterfaceDAO<Produto> produtoDao;
	@EJB
	private InterfaceDAO<ProdutoGrupo> grupoDao;
	@EJB
	private InterfaceDAO<ProdutoSubgrupo> subGrupoDao;
	@EJB
	private InterfaceDAO<NfeConfiguracao> nfeConfiguracaoDao;
	
	private List<Produto> listaProduto;
	private List<Produto> listaCarrinho;
	private Produto produtoSelecionado;
	
	private MenuModel menuModel;
	
	private boolean telaListaProduto = true;
	
	private Cliente cliente;
	private String emailCliente;
	private String nomeCliente;
	private String senhaCliente;
	
	private String assunto;
	private String mensagem;
	
	private String termoPesquisa;
	
	private String urlVideo;
	
	@PostConstruct
	public void init() {
		menuModel = new DefaultMenuModel();
		setListaCarrinho(new ArrayList<>());
		cliente = new Cliente();
		
		try {
			List<ProdutoGrupo> listaGrupo = grupoDao.getBeans(ProdutoGrupo.class);
			
			for (ProdutoGrupo grupo: listaGrupo) {
				DefaultSubMenu subMenu = new DefaultSubMenu(grupo.getNome());
				
				List<ProdutoSubgrupo> listaSubGrupo = subGrupoDao.getBeans(ProdutoSubgrupo.class, "grupo", grupo);
				
				for (ProdutoSubgrupo subGrupo : listaSubGrupo) {
					DefaultMenuItem menuItem = new DefaultMenuItem();
					menuItem.setValue(subGrupo.getNome());
					menuItem.setCommand("#{lojaVirtualController.selecionaSubGrupo(" + subGrupo.getId() + ")}");
					menuItem.setAjax(true);
					menuItem.setGlobal(true);
					menuItem.setUpdate(":form1:panelListaProduto,:form1:panelDetalheProduto");
					subMenu.addElement(menuItem);
				}
				
				menuModel.addElement(subMenu);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	public MenuModel getMenuModel() {
		return menuModel;
	}
	
	public void selecionaSubGrupo(Integer id){
		try {
			telaListaProduto = true;

			List<Filtro> filtros = new ArrayList<>();
			filtros.add(new Filtro(Filtro.AND, "produtoSubgrupo.id", Filtro.IGUAL, id));
			listaProduto = produtoDao.getBeans(Produto.class, filtros);
		} catch (Exception e) {
			e.printStackTrace();
			FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro.", e.getMessage());
		}
	}

	public String imagemProduto(Integer idProduto) {
		String pastaImagemProduto = "images/produto/";
		
		ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
		String caminhoArquivo = context.getRealPath("resources/" + pastaImagemProduto + idProduto + ".jpg");

		if (caminhoArquivo != null) {
			return pastaImagemProduto + idProduto + ".jpg";
		}
		return pastaImagemProduto + "0.jpg";
	}
	
	public void selecionaProduto(){
		if (produtoSelecionado != null) {
			telaListaProduto = false;
			
			//em qual tabela buscar essa informação?
			urlVideo = "http://www.youtube.com/embed/d9AB-TAfEnU";
		}
	}
	
	public void pesquisa() {
		try {
			listaProduto = produtoDao.getBeansLike(Produto.class, "nome", termoPesquisa);

			if (listaProduto.isEmpty()) {
				FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_INFO, "Nenhum produto encontrado.", "");
			}
			
			telaListaProduto = true;
		} catch (Exception e) {
			e.printStackTrace();
			FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro.", e.getMessage());
		}
	}
	
	public void adicionaCarrinho() {
		if (produtoSelecionado != null) {
			listaCarrinho.add(produtoSelecionado);
			FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_INFO, "Produto adiconado", produtoSelecionado.getNome());
		}
	}
	
	public void loginCliente() {
		//analise o DER e verifique em qual tabela os dados estão armazenados.
		if (emailCliente != null && senhaCliente != null) {
			if (emailCliente.equals("cliente@email.com") && senhaCliente.equals("12345")) {
				cliente.setId(1);
				nomeCliente = "Cliente Teste";
				FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_INFO, "Login realizado com sucesso.", "");
			} else {
				FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_WARN, "Usuário ou senha inválidos.", "");
			}
		} else {
			FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_WARN, "Usuário ou senha inválidos.", "");
		}
	}
	
	public void contato() {
		try {
			NfeConfiguracao configuracao = nfeConfiguracaoDao.getBean(1, NfeConfiguracao.class);
			
			MultiPartEmail email = new MultiPartEmail();
			email.setHostName(configuracao.getEmailServidorSmtp());
			email.setFrom(configuracao.getEmpresa().getEmail(), nomeCliente);
			email.addTo(configuracao.getEmpresa().getEmail());
			email.addReplyTo(emailCliente);
			email.setSubject(assunto);
			email.setMsg(mensagem);

			email.send();
			
			FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_INFO, "Email enviado com sucesso.", "");
		} catch (Exception e) {
			e.printStackTrace();
			FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro.", e.getMessage());
		}
	}
	
	public void finalizaCompra() {
		try {
			if (listaCarrinho.isEmpty()) {
				FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_WARN, "Nenhum item no carriho.", "");
			} else {
				ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
				context.redirect(IntegracaoPagSeguro.checkout(listaCarrinho));
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro.", e.getMessage());
		}
	}
	
	public boolean isTelaListaProduto() {
		return telaListaProduto;
	}

	public void setTelaListaProduto(boolean telaListaProduto) {
		this.telaListaProduto = telaListaProduto;
	}

	public List<Produto> getListaProduto() {
		return listaProduto;
	}

	public void setListaProduto(List<Produto> listaProduto) {
		this.listaProduto = listaProduto;
	}

	public Produto getProdutoSelecionado() {
		return produtoSelecionado;
	}

	public void setProdutoSelecionado(Produto produtoSelecionado) {
		this.produtoSelecionado = produtoSelecionado;
	}

	public List<Produto> getListaCarrinho() {
		return listaCarrinho;
	}

	public void setListaCarrinho(List<Produto> listaCarrinho) {
		this.listaCarrinho = listaCarrinho;
	}

	public String getEmailCliente() {
		return emailCliente;
	}

	public void setEmailCliente(String emailCliente) {
		this.emailCliente = emailCliente;
	}

	public String getSenhaCliente() {
		return senhaCliente;
	}

	public void setSenhaCliente(String senhaCliente) {
		this.senhaCliente = senhaCliente;
	}

	public Cliente getCliente() {
		return cliente;
	}

	public void setCliente(Cliente cliente) {
		this.cliente = cliente;
	}

	public String getNomeCliente() {
		return nomeCliente;
	}

	public void setNomeCliente(String nomeCliente) {
		this.nomeCliente = nomeCliente;
	}

	public String getAssunto() {
		return assunto;
	}

	public void setAssunto(String assunto) {
		this.assunto = assunto;
	}

	public String getMensagem() {
		return mensagem;
	}

	public void setMensagem(String mensagem) {
		this.mensagem = mensagem;
	}

	public String getTermoPesquisa() {
		return termoPesquisa;
	}

	public void setTermoPesquisa(String termoPesquisa) {
		this.termoPesquisa = termoPesquisa;
	}

	public String getUrlVideo() {
		return urlVideo;
	}

	public void setUrlVideo(String urlVideo) {
		this.urlVideo = urlVideo;
	}
	
}
