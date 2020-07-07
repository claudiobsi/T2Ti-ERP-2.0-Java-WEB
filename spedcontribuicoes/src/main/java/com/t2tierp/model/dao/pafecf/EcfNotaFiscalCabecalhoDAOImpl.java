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
package com.t2tierp.model.dao.pafecf;

import java.util.Date;
import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.t2tierp.model.bean.pafecf.EcfNotaFiscalCabecalho;

@Stateless(name="ejb/EcfNotaFiscalCabecalhoDAO", mappedName="ejb/EcfNotaFiscalCabecalhoDAO")
public class EcfNotaFiscalCabecalhoDAOImpl  implements EcfNotaFiscalCabecalhoDAO {

	@PersistenceContext
	private EntityManager em;

	@SuppressWarnings("unchecked")
	@Override
	public List<EcfNotaFiscalCabecalho> getBeans(Date dataInicio, Date dataFim, String serie, String subserie) {
		Query q = em.createQuery("SELECT o FROM EcfNotaFiscalCabecalho o WHERE o.dataEmissao BETWEEN :dataInicial AND :dataFinal AND o.serie = :serie AND o.subserie = :subserie ORDER BY o.numero");
		q.setParameter("dataInicial", dataInicio);
		q.setParameter("dataFinal", dataFim);
		q.setParameter("serie", serie);
		q.setParameter("subserie", subserie);
		return q.getResultList();
	}
	
}
