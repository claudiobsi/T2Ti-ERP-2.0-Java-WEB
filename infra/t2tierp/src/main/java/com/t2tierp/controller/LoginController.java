package com.t2tierp.controller;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.naming.InitialContext;

import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import com.t2tierp.model.bean.cadastros.PapelFuncao;
import com.t2tierp.model.bean.cadastros.Usuario;
import com.t2tierp.model.dao.cadastros.UsuarioDAO;

@EJB(name = "usuarioDAO", beanInterface = UsuarioDAO.class)
public class LoginController implements AuthenticationProvider {

	private UsuarioDAO dao;

	@Override
	public Authentication authenticate(Authentication authentication) throws AuthenticationException {
		String nomeUsuario = authentication.getName();
		String senha = authentication.getCredentials().toString();
		Usuario usuario = null;

		try {
			InitialContext initialContext = new InitialContext();
			dao = (UsuarioDAO) initialContext.lookup("java:comp/ejb/usuarioDAO");
			Md5PasswordEncoder enc = new Md5PasswordEncoder();
			senha = enc.encodePassword(nomeUsuario + senha, null);
			usuario = dao.getUsuario(nomeUsuario, senha);
			if (usuario != null) {
				List<PapelFuncao> funcoes = dao.getPapelFuncao(usuario);
				List<GrantedAuthority> grantedAuths = new ArrayList<>();
				for (PapelFuncao p : funcoes) {
					grantedAuths.add(new SimpleGrantedAuthority(p.getFuncao().getNome()));
				}
				Authentication auth = new UsernamePasswordAuthenticationToken(nomeUsuario, senha, grantedAuths);
				return auth;
			}
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}

}
