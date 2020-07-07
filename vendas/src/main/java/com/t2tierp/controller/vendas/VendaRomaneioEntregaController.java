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
package com.t2tierp.controller.vendas;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.t2tierp.controller.AbstractController;
import com.t2tierp.model.bean.cadastros.Colaborador;
import com.t2tierp.model.bean.vendas.VendaCabecalho;
import com.t2tierp.model.bean.vendas.VendaRomaneioEntrega;
import com.t2tierp.model.dao.InterfaceDAO;
import com.t2tierp.util.FacesContextUtil;

@ManagedBean
@ViewScoped
public class VendaRomaneioEntregaController extends AbstractController<VendaRomaneioEntrega> implements Serializable {

	private static final long serialVersionUID = 1L;
	@EJB
	private InterfaceDAO<Colaborador> colaboradorDao;
	@EJB
	private InterfaceDAO<VendaCabecalho> vendaCabecalhoDao;

	private Integer numeroFatura;

	@Override
	public Class<VendaRomaneioEntrega> getClazz() {
		return VendaRomaneioEntrega.class;
	}

	@Override
	public String getFuncaoBase() {
		return "VENDA_ROMANEIO_ENTREGA";
	}

	@Override
	public void incluir() {
		super.incluir();
		getObjeto().setSituacao("A");
		getObjeto().setListaVendaCabecalho(new HashSet<>());
	}

	public void incluiVenda() {
		try {
			if (numeroFatura != null) {
				VendaCabecalho venda = vendaCabecalhoDao.getBean(VendaCabecalho.class, "numeroFatura", numeroFatura);
				if (venda == null) {
					FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_WARN, "Venda não localizada!", "");
				} else {
					getObjeto().getListaVendaCabecalho().add(venda);
				}
			}
		} catch(Exception e) {
			FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro!", e.getMessage());
		}
	}

	public List<Colaborador> getListaColaborador(String nome) {
		List<Colaborador> listaColaborador = new ArrayList<>();
		try {
			listaColaborador = colaboradorDao.getBeansLike(Colaborador.class, "pessoa.nome", nome);
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return listaColaborador;
	}

	public Integer getNumeroFatura() {
		return numeroFatura;
	}

	public void setNumeroFatura(Integer numeroFatura) {
		this.numeroFatura = numeroFatura;
	}

}
