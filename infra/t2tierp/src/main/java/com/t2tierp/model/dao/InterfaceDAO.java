package com.t2tierp.model.dao;

import java.util.List;

import javax.ejb.Local;

@Local
public interface InterfaceDAO<T> {
	void salvar(T bean) throws Exception;
	void atualizar(T bean) throws Exception;
	void excluir(T bean) throws Exception;
	void excluir(T bean, Integer id) throws Exception;
	T getBean(Integer id, Class<T> clazz) throws Exception;
	List<T> getBeans(Class<T> clazz) throws Exception;

}
