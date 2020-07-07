package com.t2tierp.controller.cadastros;

import java.io.Serializable;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.SessionScoped;
import javax.inject.Named;

import com.t2tierp.model.bean.cadastros.AgenciaBanco;
import com.t2tierp.model.dao.InterfaceDAO;

@Named
@SessionScoped
public class AgenciaBancoController implements Serializable {

	private static final long serialVersionUID = 1L;

	private List<AgenciaBanco> agencias;
	
	@EJB
	private InterfaceDAO<AgenciaBanco> dao;

	public List<AgenciaBanco> getAgencias() {
		try {
			agencias = dao.getBeans(AgenciaBanco.class);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return agencias;
	}

}
