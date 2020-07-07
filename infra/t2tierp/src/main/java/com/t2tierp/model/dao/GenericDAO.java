package com.t2tierp.model.dao;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

@Stateless
public class GenericDAO<T> implements InterfaceDAO<T> {

	@PersistenceContext
	private EntityManager em;

	public GenericDAO() {
	}

	@Override
	public void salvar(T bean) throws Exception {
		em.persist(bean);
	}

	@Override
	public void atualizar(T bean) throws Exception {
		em.merge(bean);
	}

	@Override
	public void excluir(T bean) throws Exception {
		em.remove(bean);
	}

	@Override
	public void excluir(T bean, Integer id) throws Exception {
		em.remove(em.getReference(bean.getClass(), id));
	}

	@Override
	public T getBean(Integer id, Class<T> clazz) throws Exception {
		return em.find(clazz, id);
	}

	@Override
	public List<T> getBeans(Class<T> clazz) throws Exception {
		CriteriaQuery<T> criteria = em.getCriteriaBuilder().createQuery(clazz);
		Root<T> bean = criteria.from(clazz);
		criteria.select(bean);
		return em.createQuery(criteria).getResultList();
	}

}
