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
package com.t2tierp.controller.ponto;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.SortOrder;
import org.springframework.security.authentication.encoding.Md5PasswordEncoder;

import com.t2tierp.controller.AbstractController;
import com.t2tierp.model.bean.cadastros.Usuario;
import com.t2tierp.model.bean.ponto.PontoMarcacao;
import com.t2tierp.model.dao.cadastros.UsuarioDAO;
import com.t2tierp.util.FacesContextUtil;

@ManagedBean
@ViewScoped
public class PontoMarcacaoController extends AbstractController<PontoMarcacao> implements Serializable {

	private static final long serialVersionUID = 1L;
	@EJB
	private UsuarioDAO usuarioDao;

	private String nomeUsuario;
	private String senha;
	
	@Override
	public Class<PontoMarcacao> getClazz() {
		return PontoMarcacao.class;
	}

	@Override
	public String getFuncaoBase() {
		return "PONTO_MARCACAO";
	}

	public void registraMarcacao() {
		try {
			senha = new Md5PasswordEncoder().encodePassword(nomeUsuario + senha, null);
			
			Usuario usuario = null; 
			try {
				usuario = usuarioDao.getUsuario(nomeUsuario, senha);
			} catch (Exception e) {
				throw new Exception("Usuário Inválido!");
			}
			
			if (usuario == null) {
				throw new Exception("Usuário Inválido!");
			}

			Calendar dataAtual = Calendar.getInstance();
			SimpleDateFormat formatoHora = new SimpleDateFormat("HH:mm:ss");

			PontoMarcacao marcacao = new PontoMarcacao();
			marcacao.setColaborador(usuario.getColaborador());
			marcacao.setDataMarcacao(dataAtual.getTime());
			marcacao.setHoraMarcacao(formatoHora.format(dataAtual.getTime()));
			marcacao.setTipoRegistro("O");

			Map<String, Object> filters = new HashMap<>();
			filters.put("colaborador", usuario.getColaborador());
			filters.put("dataMarcacao", dataAtual.getTime());
			List<PontoMarcacao> listaMarcacaoes = dao.getBeans(getClazz(), 0, 0, "horaMarcacao", SortOrder.ASCENDING, filters);
			
			//define o tipo de marcacao
			String tipoMarcacao = "E";
			int par = 1;
			for (int i = 0; i < listaMarcacaoes.size(); i++) {
				if (listaMarcacaoes.get(i).getTipoMarcacao().equals("E")) {
					tipoMarcacao = "S";
				} else if (listaMarcacaoes.get(i).getTipoMarcacao().equals("S")) {
					tipoMarcacao = "E";
				}
				par = Integer.valueOf(listaMarcacaoes.get(i).getParEntradaSaida().substring(1));
			}
			if (!listaMarcacaoes.isEmpty()) {
				if (listaMarcacaoes.size() % 2 == 0) {
					par += 1;
				}
			}
			marcacao.setTipoMarcacao(tipoMarcacao);
			marcacao.setParEntradaSaida(tipoMarcacao + par);
			
			dao.persist(marcacao);

			FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_INFO, "Registro incluído com sucesso!", "");
		} catch (Exception e) {
			//e.printStackTrace();
			FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro ao registrar a marcação!", e.getMessage());
		} finally {
			nomeUsuario = null;
			senha = null;
		}
	}

	public String getNomeUsuario() {
		return nomeUsuario;
	}

	public void setNomeUsuario(String nomeUsuario) {
		this.nomeUsuario = nomeUsuario;
	}

	public String getSenha() {
		return senha;
	}

	public void setSenha(String senha) {
		this.senha = senha;
	}

}
