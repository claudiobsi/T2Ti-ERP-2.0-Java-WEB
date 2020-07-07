package com.t2tierp.controller.cadastros;

import java.io.Serializable;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.event.ActionEvent;

import com.t2tierp.model.bean.cadastros.Banco;
import com.t2tierp.model.dao.InterfaceDAO;
import com.t2tierp.util.FacesContextUtil;

@ManagedBean
@ViewScoped
public class BancoController implements Serializable {

	private static final long serialVersionUID = 1L;
	private List<Banco> bancos;
	private Banco bancoSelecionado;

	@EJB
	private InterfaceDAO<Banco> dao;

	public List<Banco> getBancos() {
		try {
			bancos = dao.getBeans(Banco.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return bancos;
	}

	public void incluir(ActionEvent actionEvent) {
		bancoSelecionado = new Banco();
	}

	public void incluirProcessa(ActionEvent actionEvent) {
		try {
			dao.salvar(bancoSelecionado);
			FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_INFO, "Registro incluído com sucesso!", null);
		} catch (Exception e) {
			e.printStackTrace();
			FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro ao incluir o registro!", e.getMessage());
		}
	}

	public void alterarProcessa(ActionEvent actionEvent) {
		try {
			dao.atualizar(bancoSelecionado);
			FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_INFO, "Registro alterado com sucesso!", null);
		} catch (Exception e) {
			e.printStackTrace();
			FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro ao alterar o registro!", e.getMessage());
		}
	}

	public void excluirProcessa(ActionEvent actionEvent) {
		try {
			if (bancoSelecionado == null || bancoSelecionado.getId() == null) {
				FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_INFO, "Nenhum registro selecionado!", null);
			} else {
				dao.excluir(bancoSelecionado, bancoSelecionado.getId());
				FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_INFO, "Registro excluído com sucesso!", null);
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro ao excluir o registro!", e.getMessage());
		}
	}

	public Banco getBancoSelecionado() {
		return bancoSelecionado;
	}

	public void setBancoSelecionado(Banco bancoSelecionado) {
		this.bancoSelecionado = bancoSelecionado;
	}

}
