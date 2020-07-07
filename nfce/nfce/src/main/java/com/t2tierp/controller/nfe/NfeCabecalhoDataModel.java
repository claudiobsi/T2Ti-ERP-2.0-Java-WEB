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
package com.t2tierp.controller.nfe;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.primefaces.model.SortOrder;

import com.t2tierp.controller.T2TiLazyDataModel;
import com.t2tierp.model.bean.nfe.NfeCabecalho;

public class NfeCabecalhoDataModel extends T2TiLazyDataModel<NfeCabecalho> {

	private static final long serialVersionUID = 1L;
	private Map<String, Object> filtroPadrao;
	private String[] atributosPadrao;

	@Override
	public List<NfeCabecalho> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
		try {
			if (filtroPadrao != null) {
				filters.putAll(filtroPadrao);
			}

			List<NfeCabecalho> beans;
			if (atributosPadrao == null) {
				String[] atributos = new String[8];
				atributos[0] = "cliente";
				atributos[1] = "serie";
				atributos[2] = "numero";
				atributos[3] = "dataHoraEmissao";
				atributos[4] = "chaveAcesso";
				atributos[5] = "digitoChaveAcesso";
				atributos[6] = "valorTotal";
				atributos[7] = "statusNota";
				
				beans = dao.getBeans(getClazz(), first, pageSize, sortField, sortOrder, filters, atributos);
			} else {
				beans = dao.getBeans(getClazz(), first, pageSize, sortField, sortOrder, filters, atributosPadrao);
			}
			
			Long totalRegistros = dao.getTotalRegistros(getClazz(), filters);
			this.setRowCount(totalRegistros.intValue());
			return beans;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<NfeCabecalho>();
	}

	public Map<String, Object> getFiltroPadrao() {
		return filtroPadrao;
	}

	public void setFiltroPadrao(Map<String, Object> filtroPadrao) {
		this.filtroPadrao = filtroPadrao;
	}

	public String[] getAtributosPadrao() {
		return atributosPadrao;
	}

	public void setAtributosPadrao(String[] atributosPadrao) {
		this.atributosPadrao = atributosPadrao;
	}

}
