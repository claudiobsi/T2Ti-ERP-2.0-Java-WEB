package com.t2tierp.controller;

import java.io.Serializable;

public class Funcao implements Serializable{

	private static final long serialVersionUID = 1L;
	
	private String nome;
	private String pagina;
	
	public Funcao(String nome, String pagina) {
		this.nome = nome;
		this.pagina = pagina;
	}
	
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public String getPagina() {
		return pagina;
	}
	public void setPagina(String pagina) {
		this.pagina = pagina;
	}

}
