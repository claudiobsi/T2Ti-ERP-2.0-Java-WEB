package com.t2tierp.model.dao.cadastros;

import java.util.List;

import javax.ejb.Local;

import com.t2tierp.model.bean.cadastros.PapelFuncao;
import com.t2tierp.model.bean.cadastros.Usuario;

@Local
public interface UsuarioDAO {
	Usuario getUsuario(String login, String senha) throws Exception;
	
	Usuario getUsuario(String login) throws Exception;

	List<PapelFuncao> getPapelFuncao(Usuario usuario) throws Exception;
}
