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
		try {
			InitialContext initialContext = new InitialContext();
			dao = (UsuarioDAO) initialContext.lookup("java:comp/ejb/usuarioDAO");
			
			Md5PasswordEncoder enc = new Md5PasswordEncoder();
			senha = enc.encodePassword(nomeUsuario + senha, null);
			Usuario usuario = dao.getUsuario(nomeUsuario, senha);
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
			//e.printStackTrace();
		}
		return null;
	}

	@Override
	public boolean supports(Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}

}
