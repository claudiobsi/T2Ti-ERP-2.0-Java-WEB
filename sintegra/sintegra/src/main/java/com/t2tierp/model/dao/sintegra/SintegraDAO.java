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
package com.t2tierp.model.dao.sintegra;

import java.util.Date;
import java.util.List;

import javax.ejb.Local;

import com.t2tierp.model.bean.cadastros.Produto;
import com.t2tierp.model.bean.nfe.NfeCabecalho;
import com.t2tierp.model.bean.pafecf.EcfNotaFiscalCabecalho;
import com.t2tierp.model.bean.pafecf.EcfSintegra60a;
import com.t2tierp.model.bean.pafecf.EcfSintegra60m;
import com.t2tierp.model.bean.sintegra.ViewSintegra60dId;
import com.t2tierp.model.bean.sintegra.ViewSintegra60rId;
import com.t2tierp.model.bean.sintegra.ViewSintegra61rId;

@Local
public interface SintegraDAO {

	List<NfeCabecalho> getNfeCabecalho(Date dataInicial, Date dataFinal) throws Exception;

	List<EcfSintegra60m> getEcfSintegra60m(Date dataInicial, Date dataFinal) throws Exception;

	List<EcfSintegra60a> getEcfSintegra60a(Integer idSintegra60m) throws Exception;

	List<ViewSintegra60dId> getViewSintegra60d(Date dataInicial, Date dataFinal) throws Exception;
	
	List<ViewSintegra60rId> getViewSintegra60r(Date dataInicial, Date dataFinal) throws Exception;
	
	List<ViewSintegra61rId> getViewSintegra61r(Date dataInicial, Date dataFinal) throws Exception;

	List<EcfNotaFiscalCabecalho> getEcfNotaFiscalCabecalho(Date dataInicial, Date dataFinal) throws Exception;
	
	Produto getProduto(String gtin) throws Exception;
}
