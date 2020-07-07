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
import java.math.BigDecimal;
import java.util.ArrayList;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.t2tierp.controller.AbstractController;
import com.t2tierp.model.bean.vendas.CondicoesPagamento;
import com.t2tierp.model.bean.vendas.VendaCondicoesParcelas;
import com.t2tierp.util.FacesContextUtil;

@ManagedBean
@ViewScoped
public class CondicoesPagamentoController extends AbstractController<CondicoesPagamento> implements Serializable {

	private static final long serialVersionUID = 1L;

	private VendaCondicoesParcelas vendaCondicoesParcelas;

	@Override
	public Class<CondicoesPagamento> getClazz() {
		return CondicoesPagamento.class;
	}

	@Override
	public String getFuncaoBase() {
		return "CONDICOES_PAGAMENTO";
	}

	@Override
	public void incluir() {
		super.incluir();
		getObjeto().setEmpresa(FacesContextUtil.getEmpresaUsuario());
		getObjeto().setListaVendaCondicoesParcelas(new ArrayList<>());
	}

	@Override
	public void salvar() {
		try {
			verificaParcelas();
			
			super.salvar();	
		} catch(Exception e) {
			e.printStackTrace();
			FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro ao salvar o registro!", e.getMessage());
		}
	}
	
	public void incluirVendaCondicoesParcelas() {
		vendaCondicoesParcelas = new VendaCondicoesParcelas();
		vendaCondicoesParcelas.setCondicoesPagamento(getObjeto());
	}

	public void salvarVendaCondicoesParcelas() {
		getObjeto().getListaVendaCondicoesParcelas().add(vendaCondicoesParcelas);
	}

    private void verificaParcelas() throws Exception {
        double prazoMedio = 0;
        BigDecimal totalPorcento = BigDecimal.ZERO;
        for (int i = 0; i < getObjeto().getListaVendaCondicoesParcelas().size(); i++) {
            totalPorcento = totalPorcento.add(getObjeto().getListaVendaCondicoesParcelas().get(i).getTaxa());
            if (i == 0) {
                prazoMedio = getObjeto().getListaVendaCondicoesParcelas().get(i).getDias();
            } else {
                prazoMedio += (getObjeto().getListaVendaCondicoesParcelas().get(i).getDias() - getObjeto().getListaVendaCondicoesParcelas().get(i - 1).getDias());
            }
        }
        if (totalPorcento.compareTo(BigDecimal.valueOf(100)) != 0) {
            throw new Exception("Os valores informados nas taxas não fecham em 100%.");
        }
        prazoMedio = prazoMedio / getObjeto().getListaVendaCondicoesParcelas().size();
        getObjeto().setPrazoMedio((int) prazoMedio);
    }
	
	
	public VendaCondicoesParcelas getVendaCondicoesParcelas() {
		return vendaCondicoesParcelas;
	}

	public void setVendaCondicoesParcelas(VendaCondicoesParcelas vendaCondicoesParcelas) {
		this.vendaCondicoesParcelas = vendaCondicoesParcelas;
	}

}
