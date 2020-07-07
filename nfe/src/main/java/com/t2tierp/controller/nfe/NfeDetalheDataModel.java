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
import com.t2tierp.model.bean.nfe.NfeDetalhe;

public class NfeDetalheDataModel extends T2TiLazyDataModel<NfeDetalhe> {
	
	private static final long serialVersionUID = 1L;
	
	@Override
	public List<NfeDetalhe> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
		try {
			String[] atributos = new String[10];
			atributos[0] = "numeroItem";
			atributos[1] = "gtin";
			atributos[2] = "nomeProduto";
			atributos[3] = "unidadeComercial";
			atributos[4] = "quantidadeComercial";
			atributos[5] = "valorUnitarioComercial";
			atributos[6] = "valorFrete";
			atributos[7] = "valorSeguro";
			atributos[8] = "valorDesconto";
			atributos[9] = "valorTotal";
			List<NfeDetalhe> beans = dao.getBeans(getClazz(), first, pageSize, sortField, sortOrder, filters, atributos);
			Long totalRegistros = dao.getTotalRegistros(getClazz(), filters);
			this.setRowCount(totalRegistros.intValue());
			return beans;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<NfeDetalhe>();
	}

}
