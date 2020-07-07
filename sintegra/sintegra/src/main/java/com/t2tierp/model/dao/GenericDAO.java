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
