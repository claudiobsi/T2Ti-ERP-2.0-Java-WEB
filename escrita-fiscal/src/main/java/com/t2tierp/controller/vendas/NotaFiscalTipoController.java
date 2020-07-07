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
import com.t2tierp.model.bean.vendas.NotaFiscalModelo;
import com.t2tierp.model.bean.vendas.NotaFiscalTipo;
import com.t2tierp.model.dao.InterfaceDAO;
import com.t2tierp.util.FacesContextUtil;

@ManagedBean
@ViewScoped
public class NotaFiscalTipoController extends AbstractController<NotaFiscalTipo> implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private InterfaceDAO<NotaFiscalModelo> notaFiscalModeloDao;

    @Override
    public Class<NotaFiscalTipo> getClazz() {
        return NotaFiscalTipo.class;
    }

    @Override
    public String getFuncaoBase() {
        return "NOTA_FISCAL_TIPO";
    }
    
    @Override
    public void incluir() {
    	super.incluir();
    	getObjeto().setEmpresa(FacesContextUtil.getEmpresaUsuario());
    }

    public List<NotaFiscalModelo> getListaNotaFiscalModelo(String nome) {
        List<NotaFiscalModelo> listaNotaFiscalModelo = new ArrayList<>();
        try {
            listaNotaFiscalModelo = notaFiscalModeloDao.getBeansLike(NotaFiscalModelo.class, "descricao", nome);
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return listaNotaFiscalModelo;
    }

}
