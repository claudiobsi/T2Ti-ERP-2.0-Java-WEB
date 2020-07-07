package com.t2tierp.model.dao.cadastros;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.t2tierp.model.bean.cadastros.PapelFuncao;
import com.t2tierp.model.bean.cadastros.Usuario;

@Stateless(name="ejb/usuarioDAO", mappedName="ejb/usuarioDAO")
public class UsuarioDAOImpl  implements UsuarioDAO {

	@PersistenceContext
	private EntityManager em;

	@Override
	public Usuario getUsuario(String nomeUsuario, String senhaUsuario) throws Exception {
		Query q = em.createQuery("SELECT u FROM Usuario u join fetch u.papel WHERE u.login = :login AND u.senha = :senha");
		q.setParameter("login", nomeUsuario);
		q.setParameter("senha", senhaUsuario);
		return (Usuario) q.getSingleResult();
	}

	@Override
	public Usuario getUsuario(String nomeUsuario) throws Exception {
		Query q = em.createQuery("SELECT u FROM Usuario u join fetch u.papel WHERE u.login = :login");
		q.setParameter("login", nomeUsuario);
		return (Usuario) q.getSingleResult();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<PapelFuncao> getPapelFuncao(Usuario usuario) throws Exception {
		Query q = em.createQuery("SELECT p FROM PapelFuncao p join fetch p.papel join fetch p.funcao WHERE p.papel = :papel");
		q.setParameter("papel", usuario.getPapel());
		return (List<PapelFuncao>) q.getResultList();
	}

}
