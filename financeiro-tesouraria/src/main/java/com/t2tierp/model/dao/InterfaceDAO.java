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

import java.util.Date;
import java.util.List;
import java.util.Map;

import javax.ejb.Local;

import org.primefaces.model.SortOrder;

@Local
public interface InterfaceDAO<T> {
	void clear() throws Exception;
	
	void persist(T bean) throws Exception;

	T merge(T bean) throws Exception;

	void excluir(T bean) throws Exception;

	void excluir(T bean, Integer id) throws Exception;

	void excluir(Class<T> clazz, Integer id) throws Exception;

	T getBean(Integer id, Class<T> clazz) throws Exception;

	T getBeanJoinFetch(Integer id, Class<T> clazz) throws Exception;

	T getBean(Class<T> clazz, String atributo, Object valor) throws Exception;

	T getBean(Class<T> clazz, String atributo1, Object valor1, String atributo2, Object valor2) throws Exception;

	T getBean(Class<T> clazz, List<Filtro> filtros) throws Exception;

	Long getTotalRegistros(Class<T> clazz, Map<String, Object> filters) throws Exception;
	
	Long getTotalRegistros(Class<T> clazz, Map<String, Object> filters, String atributoCount) throws Exception;

	List<T> getBeans(Class<T> clazz) throws Exception;

	List<T> getBeansJoinFetch(Class<T> clazz) throws Exception;

	List<T> getBeans(Class<T> clazz, String atributo, Object valor) throws Exception;

	List<T> getBeansLike(Class<T> clazz, String atributo, String valor) throws Exception;

	List<T> getBeans(Class<T> clazz, String atributo1, Object valor1, String atributo2, Object valor2) throws Exception;

	List<T> getBeans(Class<T> clazz, String nomeAtributoData, Date dataInicio, Date dataFim);

	List<T> getBeans(Class<T> clazz, String atributo, Object valor, String nomeAtributoData, Date dataInicio, Date dataFim);

	List<T> getBeans(Class<T> clazz, List<Filtro> filtros) throws Exception;

	List<T> getBeans(Class<T> clazz, int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) throws Exception;

	List<T> getBeans(Class<T> clazz, int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters, String[] atributos) throws Exception;
}
