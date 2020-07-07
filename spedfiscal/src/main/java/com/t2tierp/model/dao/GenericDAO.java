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
package com.t2tierp.model.dao;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Path;
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
	public T getBean(Class<T> clazz, String atributo, Object valor) throws Exception {
		List<T> beans = getBeans(clazz, atributo, valor);
		if (beans.isEmpty()) {
			return null;
		} else {
			return beans.get(0);
		}
	}
	
	@Override
	public T getBean(Class<T> clazz, String atributo1, Object valor1, String atributo2, Object valor2) throws Exception {
		List<T> beans = getBeans(clazz, atributo1, valor1, atributo2, valor2);
		if (beans.isEmpty()) {
			return null;
		} else {
			return beans.get(0);
		}
	}

	@Override
	public List<T> getBeans(Class<T> clazz) throws Exception {
		CriteriaQuery<T> criteria = em.getCriteriaBuilder().createQuery(clazz);
		Root<T> bean = criteria.from(clazz);
		criteria.select(bean);
		return em.createQuery(criteria).getResultList();
	}

	@Override
	public List<T> getBeans(Class<T> clazz, String atributo, Object valor) throws Exception {
		CriteriaBuilder builder = em.getCriteriaBuilder();
		CriteriaQuery<T> criteria = builder.createQuery(clazz);
		Root<T> root = criteria.from(clazz);
		criteria.select(root);

		if (valor.getClass() == String.class) {
			Path<String> nome = root.get(atributo);
			criteria.where(builder.equal(nome, valor));
		} else if (valor.getClass() == Integer.class) {
			Path<Integer> nome = root.get(atributo);
			criteria.where(builder.equal(nome, valor));
		} else if (valor.getClass() == Date.class) {
			Path<Date> nome = root.get(atributo);
			criteria.where(builder.equal(nome, valor));
		} else if (valor.getClass() == BigDecimal.class) {
			Path<BigDecimal> nome = root.get(atributo);
			criteria.where(builder.equal(nome, valor));
		}

		TypedQuery<T> query = em.createQuery(criteria);
		List<T> beans = query.getResultList();
		return beans;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<T> getBeans(Class<T> clazz, String atributo1, Object valor1, String atributo2, Object valor2) throws Exception {
		Query query = em.createQuery("SELECT o FROM " + clazz.getName()
				+ " o WHERE o." + atributo1 + " = :valor1 "
				+ " AND o." + atributo2 + " = :valor2");
		query.setParameter("valor1", valor1);
		query.setParameter("valor2", valor2);
		List<T> beans = query.getResultList();
		return beans;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<T> getBeans(Class<T> clazz, String nomeAtributoData, Date dataInicio, Date dataFim) {
		Query query = em.createQuery("SELECT o FROM " + clazz.getName()
				+ " o WHERE o. " + nomeAtributoData + " BETWEEN :dataInicio AND :dataFim");
		query.setParameter("dataInicio", dataInicio);
		query.setParameter("dataFim", dataFim);
		List<T> beans = query.getResultList();
		return beans;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<T> getBeans(Class<T> clazz, String atributo, Object valor, String nomeAtributoData, Date dataInicio, Date dataFim){
		Query query = em.createQuery("SELECT o FROM " + clazz.getName()
				+ " o WHERE o." + nomeAtributoData + " BETWEEN :dataInicio AND :dataFim "
				+ " AND o." + atributo + " = :valor");
		query.setParameter("dataInicio", dataInicio);
		query.setParameter("dataFim", dataFim);
		query.setParameter("valor", valor);
		List<T> beans = query.getResultList();
		return beans;
	}

	/*public List<T> getBeans(Class<T> clazz, String atributo, String valor) {
		// SELECT o FROM NfeCabecalho o WHERE o.dataHoraEmissao BETWEEN
		// :dataInicial AND :dataFinal
		String jpql = "SELECT o FROM " + clazz.getName() + " ";
		jpql += "WHERE o.";
		for (String s : atributo) {

		}

		return null;
	}*/

}
