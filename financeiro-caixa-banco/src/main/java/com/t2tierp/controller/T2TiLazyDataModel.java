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

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.primefaces.model.LazyDataModel;
import org.primefaces.model.SortOrder;

import com.t2tierp.model.dao.InterfaceDAO;

public class T2TiLazyDataModel<T> extends LazyDataModel<T> {

	private static final long serialVersionUID = 1L;
	protected InterfaceDAO<T> dao;
	private Class<T> clazz;
	private String atributoCount;

	@Override
	public T getRowData(String rowKey) {
		try {
			return dao.getBeanJoinFetch(Integer.valueOf(rowKey), getClazz());
		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public Object getRowKey(T object) {
		try {
			Method metodo = object.getClass().getDeclaredMethod("getId");
			return metodo.invoke(object);
		} catch (NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public List<T> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
		try {
			List<T> beans = dao.getBeans(getClazz(), first, pageSize, sortField, sortOrder, filters);
			Long totalRegistros = dao.getTotalRegistros(getClazz(), filters, atributoCount);
			this.setRowCount(totalRegistros.intValue());
			return beans;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<T>();
	}

	public Class<T> getClazz() {
		return clazz;
	}

	public void setClazz(Class<T> clazz) {
		this.clazz = clazz;
	}

	public InterfaceDAO<T> getDao() {
		return dao;
	}

	public void setDao(InterfaceDAO<T> dao) {
		this.dao = dao;
	}

	public void setAtributoCount(String atributoCount) {
		this.atributoCount = atributoCount;
	}
	
}
