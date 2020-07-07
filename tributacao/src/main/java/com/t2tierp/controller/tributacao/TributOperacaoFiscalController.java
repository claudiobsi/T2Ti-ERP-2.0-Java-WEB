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
package com.t2tierp.controller.tributacao;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;

import com.t2tierp.controller.AbstractController;
import com.t2tierp.model.bean.cadastros.Cfop;
import com.t2tierp.model.bean.tributacao.TributOperacaoFiscal;
import com.t2tierp.model.dao.InterfaceDAO;
import com.t2tierp.util.FacesContextUtil;

@ManagedBean
@ViewScoped
public class TributOperacaoFiscalController extends AbstractController<TributOperacaoFiscal> implements Serializable {

    private static final long serialVersionUID = 1L;
	@EJB
	private InterfaceDAO<Cfop> cfopDao;
	
	private Cfop cfop;

    @Override
    public Class<TributOperacaoFiscal> getClazz() {
        return TributOperacaoFiscal.class;
    }

    @Override
    public String getFuncaoBase() {
        return "TRIBUT_OPERACAO_FISCAL";
    }

    @Override
    public void incluir() {
    	super.incluir();
    	getObjeto().setEmpresa(FacesContextUtil.getEmpresaUsuario());
    }

    public void selecionaCFOP(SelectEvent event) {
    	Cfop cfop = (Cfop) event.getObject();
    	getObjeto().setCfop(cfop.getCfop());
    }    
    
	public List<Cfop> getListaCfop(String nome) {
		List<Cfop> listaCfop = new ArrayList<>();
		try {
			listaCfop = cfopDao.getBeansLike(Cfop.class, "descricao", nome);
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return listaCfop;
	}

	public Cfop getCfop() {
		return cfop;
	}

	public void setCfop(Cfop cfop) {
		this.cfop = cfop;
	}    
    
}
