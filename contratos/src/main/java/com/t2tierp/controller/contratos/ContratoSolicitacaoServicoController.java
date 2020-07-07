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
package com.t2tierp.controller.contratos;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.t2tierp.controller.AbstractController;
import com.t2tierp.model.bean.cadastros.Cliente;
import com.t2tierp.model.bean.cadastros.Colaborador;
import com.t2tierp.model.bean.cadastros.Fornecedor;
import com.t2tierp.model.bean.cadastros.Setor;
import com.t2tierp.model.bean.contratos.ContratoSolicitacaoServico;
import com.t2tierp.model.bean.contratos.ContratoTipoServico;
import com.t2tierp.model.dao.InterfaceDAO;

@ManagedBean
@ViewScoped
public class ContratoSolicitacaoServicoController extends AbstractController<ContratoSolicitacaoServico> implements Serializable {

    private static final long serialVersionUID = 1L;
    @EJB
    private InterfaceDAO<Fornecedor> fornecedorDao;
    @EJB
    private InterfaceDAO<Cliente> clienteDao;
    @EJB
    private InterfaceDAO<Setor> setorDao;
    @EJB
    private InterfaceDAO<Colaborador> colaboradorDao;
    @EJB
    private InterfaceDAO<ContratoTipoServico> contratoTipoServicoDao;
    
    private String servicoPrestadoContratado;

    @Override
    public Class<ContratoSolicitacaoServico> getClazz() {
        return ContratoSolicitacaoServico.class;
    }

    @Override
    public String getFuncaoBase() {
        return "CONTRATO_SOLICITACAO_SERVICO";
    }
    
    @Override
    public void incluir() {
    	super.incluir();
    	getObjeto().setStatusSolicitacao("A");
    	servicoPrestadoContratado = "prestado";
    }
    
    @Override
    public void alterar() {
    	super.alterar();
    	if(getObjeto().getCliente() != null) {
    		servicoPrestadoContratado = "prestado";
    	} else {
    		servicoPrestadoContratado = "contratado";
    	}
    }

    public boolean getServicoContratado() {
    	return servicoPrestadoContratado.equals("contratado");
    }

    public boolean getServicoPrestado() {
    	return servicoPrestadoContratado.equals("prestado");
    }
    
    public List<Fornecedor> getListaFornecedor(String nome) {
        List<Fornecedor> listaFornecedor = new ArrayList<>();
        try {
            listaFornecedor = fornecedorDao.getBeansLike(Fornecedor.class, "pessoa.nome", nome);
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return listaFornecedor;
    }

    public List<Cliente> getListaCliente(String nome) {
        List<Cliente> listaCliente = new ArrayList<>();
        try {
            listaCliente = clienteDao.getBeansLike(Cliente.class, "pessoa.nome", nome);
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return listaCliente;
    }

    public List<Setor> getListaSetor(String nome) {
        List<Setor> listaSetor = new ArrayList<>();
        try {
            listaSetor = setorDao.getBeansLike(Setor.class, "nome", nome);
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return listaSetor;
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

    public List<ContratoTipoServico> getListaContratoTipoServico(String nome) {
        List<ContratoTipoServico> listaContratoTipoServico = new ArrayList<>();
        try {
            listaContratoTipoServico = contratoTipoServicoDao.getBeansLike(ContratoTipoServico.class, "nome", nome);
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return listaContratoTipoServico;
    }

	public String getServicoPrestadoContratado() {
		return servicoPrestadoContratado;
	}

	public void setServicoPrestadoContratado(String servicoPrestadoContratado) {
		this.servicoPrestadoContratado = servicoPrestadoContratado;
	}

}
