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
package com.t2tierp.controller.compras;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.primefaces.model.SortOrder;

import com.t2tierp.controller.T2TiLazyDataModel;
import com.t2tierp.model.bean.compras.CompraCotacao;

public class CompraConfirmaCotacaoDataModel extends T2TiLazyDataModel<CompraCotacao> {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	@Override
	public List<CompraCotacao> load(int first, int pageSize, String sortField, SortOrder sortOrder, Map<String, Object> filters) {
		try {
			filters.put("situacao", "A");
			List<CompraCotacao> beans = dao.getBeans(getClazz(), first, pageSize, sortField, sortOrder, filters);
			Long totalRegistros = dao.getTotalRegistros(getClazz(), filters);
			this.setRowCount(totalRegistros.intValue());
			return beans;
		} catch (Exception e) {
			e.printStackTrace();
		}
		return new ArrayList<CompraCotacao>();
	}

}
