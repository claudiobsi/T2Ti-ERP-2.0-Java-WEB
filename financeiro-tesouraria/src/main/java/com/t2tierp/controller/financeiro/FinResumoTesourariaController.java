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
package com.t2tierp.controller.financeiro;

import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.t2tierp.controller.AbstractController;
import com.t2tierp.model.bean.financeiro.ViewFinResumoTesourariaID;
import com.t2tierp.model.dao.Filtro;
import com.t2tierp.model.dao.InterfaceDAO;
import com.t2tierp.util.FacesContextUtil;

@ManagedBean
@ViewScoped
public class FinResumoTesourariaController extends AbstractController<ViewFinResumoTesourariaID> implements Serializable {

    private static final long serialVersionUID = 1L;

    private Date periodo;
    private List<ViewFinResumoTesourariaID> listaResumoTesouraria;
    private List<ViewFinResumoTesourariaID> listaResumoTesourariaDetalhe;
    @EJB
    private InterfaceDAO<ViewFinResumoTesourariaID> resumoTesourariaDao;
    
    @Override
    public Class<ViewFinResumoTesourariaID> getClazz() {
        return ViewFinResumoTesourariaID.class;
    }

    @Override
    public String getFuncaoBase() {
        return "FIN_RESUMO_TESOURARIA";
    }
    
    @Override
    public void alterar() {
    	super.alterar();
    	buscaDados();
    }
    
    public void buscaDados() {
    	try{
        	if (periodo == null) {
        		FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_INFO, "Necessário informar o período!", "");
        	} else {
        		List<Filtro> filtros = new ArrayList<>();	
        		filtros.add(new Filtro(Filtro.AND, "viewFinResumoTesouraria.dataLancamento", Filtro.MAIOR_OU_IGUAL, getDataInicial()));
        		filtros.add(new Filtro(Filtro.AND, "viewFinResumoTesouraria.dataLancamento", Filtro.MENOR_OU_IGUAL, ultimoDiaMes()));
        		
        		if (isTelaGrid()) {
        			listaResumoTesouraria = resumoTesourariaDao.getBeans(ViewFinResumoTesourariaID.class, filtros);
        		} else {
        			filtros.add(new Filtro(Filtro.AND, "viewFinResumoTesouraria.idContaCaixa", Filtro.IGUAL, getObjeto().getViewFinResumoTesouraria().getIdContaCaixa()));
        			listaResumoTesourariaDetalhe = resumoTesourariaDao.getBeans(ViewFinResumoTesourariaID.class, filtros);
        		}
        	}
    	} catch(Exception e) {
    		e.printStackTrace();
    		FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_ERROR, "Erro ao buscar os dados!", e.getMessage());
    	}
    }
    
    private Date getDataInicial() {
        try {
            if (periodo == null) {
                return null;
            }
            Calendar dataValida = Calendar.getInstance();
            dataValida.setTime(periodo);
            dataValida.setLenient(false);

            dataValida.set(Calendar.DAY_OF_MONTH, 1);

            dataValida.getTime();

            return dataValida.getTime();
        } catch (Exception e) {
            return null;
        }
    }

    private Date ultimoDiaMes() {
        Calendar dataF = Calendar.getInstance();
        dataF.setTime(periodo);
        dataF.setLenient(false);
        dataF.set(Calendar.DAY_OF_MONTH, dataF.getActualMaximum(Calendar.DAY_OF_MONTH));

        return dataF.getTime();
    }    
    
    public String getTotais() {
        BigDecimal aPagar = BigDecimal.ZERO;
        BigDecimal aReceber = BigDecimal.ZERO;
        BigDecimal saldo = BigDecimal.ZERO;

        for (ViewFinResumoTesourariaID f : listaResumoTesourariaDetalhe) {
            if (f.getViewFinResumoTesouraria().getOperacao().equals("E")) {
                aReceber = aReceber.add(f.getViewFinResumoTesouraria().getValor());
            } else {
                aPagar = aPagar.add(f.getViewFinResumoTesouraria().getValor());
            }
        }
        saldo = aReceber.subtract(aPagar);
        
        DecimalFormat decimalFormat = new DecimalFormat("#,###,##0.00");
        String texto = "|   A Receber: R$ " + decimalFormat.format(aReceber);
        texto += "|   A Pagar: R$ " + decimalFormat.format(aPagar);
        texto += "|   Saldo: R$ " + decimalFormat.format(saldo) + "   |";
    	return texto;
    }
    
	public Date getPeriodo() {
		return periodo;
	}

	public void setPeriodo(Date periodo) {
		this.periodo = periodo;
	}

	public List<ViewFinResumoTesourariaID> getListaResumoTesouraria() {
		return listaResumoTesouraria;
	}

	public void setListaResumoTesouraria(List<ViewFinResumoTesourariaID> listaResumoTesouraria) {
		this.listaResumoTesouraria = listaResumoTesouraria;
	}

	public List<ViewFinResumoTesourariaID> getListaResumoTesourariaDetalhe() {
		return listaResumoTesourariaDetalhe;
	}

	public void setListaResumoTesourariaDetalhe(List<ViewFinResumoTesourariaID> listaResumoTesourariaDetalhe) {
		this.listaResumoTesourariaDetalhe = listaResumoTesourariaDetalhe;
	}


}
