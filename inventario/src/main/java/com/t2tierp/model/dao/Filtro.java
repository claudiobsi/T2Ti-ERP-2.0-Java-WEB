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

import java.io.Serializable;

public class Filtro implements Serializable {

	private static final long serialVersionUID = 1L;
	public static final String AND = "AND";
	public static final String OR = "OR";
	public static final String NOT = "NOT";
	public static final String IGUAL = "=";
	public static final String DIFERENTE = "<>";
	public static final String MENOR = "<";
	public static final String MENOR_OU_IGUAL = "<=";
	public static final String MAIOR = ">";
	public static final String MAIOR_OU_IGUAL = ">=";
	public static final String NAO_NULO = "NOT NULL";
	private String operadorLogico;
	private String atributo;
	private String operadorRelacional;
	private Object valor;
	
	public Filtro() {
	}
	
	public Filtro(String operadorLogico, String atributo, String operadorRelacional, Object valor) {
		this.operadorLogico = operadorLogico;
		this.atributo = atributo;
		this.operadorRelacional = operadorRelacional;
		this.valor = valor;
	}
	
	public String getOperadorLogico() {
		return operadorLogico;
	}
	public void setOperadorLogico(String operadorLogico) {
		this.operadorLogico = operadorLogico;
	}
	public String getAtributo() {
		return atributo;
	}
	public void setAtributo(String atributo) {
		this.atributo = atributo;
	}
	public String getOperadorRelacional() {
		return operadorRelacional;
	}
	public void setOperadorRelacional(String operadorRelacional) {
		this.operadorRelacional = operadorRelacional;
	}
	public Object getValor() {
		return valor;
	}
	public void setValor(Object valor) {
		this.valor = valor;
	}
	
}
