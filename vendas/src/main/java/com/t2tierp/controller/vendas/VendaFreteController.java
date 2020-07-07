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
import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.t2tierp.controller.AbstractController;
import com.t2tierp.model.bean.cadastros.Transportadora;
import com.t2tierp.model.bean.vendas.VendaCabecalho;
import com.t2tierp.model.bean.vendas.VendaFrete;
import com.t2tierp.model.dao.InterfaceDAO;

@ManagedBean
@ViewScoped
public class VendaFreteController extends AbstractController<VendaFrete> implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private InterfaceDAO<Transportadora> transportadoraDao;
    @EJB
    private InterfaceDAO<VendaCabecalho> vendaCabecalhoDao;

    @Override
    public Class<VendaFrete> getClazz() {
        return VendaFrete.class;
    }

    @Override
    public String getFuncaoBase() {
        return "VENDA_FRETE";
    }

    public List<Transportadora> getListaTransportadora(String nome) {
        List<Transportadora> listaTransportadora = new ArrayList<>();
        try {
            listaTransportadora = transportadoraDao.getBeansLike(Transportadora.class, "pessoa.nome", nome);
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return listaTransportadora;
    }

    public List<VendaCabecalho> getListaVendaCabecalho(String nome) {
        List<VendaCabecalho> listaVendaCabecalho = new ArrayList<>();
        try {
            listaVendaCabecalho = vendaCabecalhoDao.getBeansLike(VendaCabecalho.class, "numeroFatura", nome);
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return listaVendaCabecalho;
    }

}
