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
package com.t2tierp.controller.ponto;

import java.io.Serializable;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.t2tierp.controller.AbstractController;
import com.t2tierp.model.bean.ponto.PontoParametro;
import com.t2tierp.util.Biblioteca;
import com.t2tierp.util.FacesContextUtil;

@ManagedBean
@ViewScoped
public class PontoParametroController extends AbstractController<PontoParametro> implements Serializable {

	private static final long serialVersionUID = 1L;
	private String gerarParametrosAno;

	@Override
	public Class<PontoParametro> getClazz() {
		return PontoParametro.class;
	}

	@Override
	public String getFuncaoBase() {
		return "PONTO_PARAMETRO";
	}

	@Override
	public void incluir() {
		super.incluir();
		getObjeto().setEmpresa(FacesContextUtil.getEmpresaUsuario());
	}

	@Override
	public void salvar() {
		try {
			String mesAno = getObjeto().getMesAno();
			if (!Biblioteca.validaData(01, Integer.valueOf(mesAno.substring(0, 2)), Integer.valueOf(mesAno.substring(3, 7)))) {
				throw new Exception("Mês/Ano inválido!");
			}

            if (gerarParametrosAno.equals("S") && getObjeto().getId() == null) {
                String ano = getObjeto().getMesAno().substring(3, 7);
                for (int i = 0; i < 12; i++) {
                    if (i < 9) {
                    	getObjeto().setMesAno("0" + (i + 1) + "/" + ano);
                    } else {
                    	getObjeto().setMesAno((i + 1) + "/" + ano);
                    }
                    dao.persist(getObjeto());
                    dao.clear();
                    getObjeto().setId(null);
                }
            } else {
            	dao.merge(getObjeto());
            }
            FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_INFO, "Registro salvo com sucesso!", "");
		} catch (Exception e) {
			FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro ao salvar o registro!", e.getMessage());
		}
	}

	public String getGerarParametrosAno() {
		return gerarParametrosAno;
	}

	public void setGerarParametrosAno(String gerarParametrosAno) {
		this.gerarParametrosAno = gerarParametrosAno;
	}

}
