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

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.t2tierp.model.bean.cadastros.Produto;
import com.t2tierp.model.bean.nfe.NfeCabecalho;
import com.t2tierp.model.bean.pafecf.EcfNotaFiscalCabecalho;
import com.t2tierp.model.bean.pafecf.EcfSintegra60a;
import com.t2tierp.model.bean.pafecf.EcfSintegra60m;
import com.t2tierp.model.bean.sintegra.ViewSintegra60dId;
import com.t2tierp.model.bean.sintegra.ViewSintegra60rId;
import com.t2tierp.model.bean.sintegra.ViewSintegra61rId;

@Stateless(name="ejb/sintegraDAO", mappedName="ejb/sintegraDAO")
public class SintegraDAOImpl  implements SintegraDAO {

	@PersistenceContext
	private EntityManager em;

	@SuppressWarnings("unchecked")
	@Override
	public List<NfeCabecalho> getNfeCabecalho(Date dataInicial, Date dataFinal) throws Exception {
		Query q = em.createQuery("SELECT o FROM NfeCabecalho o WHERE o.dataHoraEmissao BETWEEN :dataInicial AND :dataFinal");
		q.setParameter("dataInicial", dataInicial);
		q.setParameter("dataFinal", dataFinal);
		return q.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<EcfSintegra60m> getEcfSintegra60m(Date dataInicial, Date dataFinal) throws Exception {
		Query q = em.createQuery("SELECT o FROM EcfSintegra60m o WHERE o.dataEmissao BETWEEN :dataInicial AND :dataFinal");
		q.setParameter("dataInicial", dataInicial);
		q.setParameter("dataFinal", dataFinal);
		return q.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<EcfSintegra60a> getEcfSintegra60a(Integer idSintegra60m) throws Exception {
		Query q = em.createQuery("SELECT o FROM EcfSintegra60a o WHERE o.idSintegra60m = :idSintegra60m");
		q.setParameter("idSintegra60m", idSintegra60m);
		return q.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ViewSintegra60dId> getViewSintegra60d(Date dataInicial, Date dataFinal) throws Exception {
		Query q = em.createQuery("SELECT o FROM ViewSintegra60dId o WHERE o.viewSintegra60d.dataEmissao BETWEEN :dataInicial AND :dataFinal");
		q.setParameter("dataInicial", dataInicial);
		q.setParameter("dataFinal", dataFinal);
		return q.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ViewSintegra60rId> getViewSintegra60r(Date dataInicial, Date dataFinal) throws Exception {
		Query q = em.createQuery("SELECT o FROM ViewSintegra60rId o WHERE o.viewSintegra60r.dataEmissao BETWEEN :dataInicial AND :dataFinal");
		q.setParameter("dataInicial", dataInicial);
		q.setParameter("dataFinal", dataFinal);
		return q.getResultList();
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ViewSintegra61rId> getViewSintegra61r(Date dataInicial, Date dataFinal) throws Exception {
		Query q = em.createQuery("SELECT o FROM ViewSintegra61rId o WHERE o.viewSintegra61r.dataEmissao BETWEEN :dataInicial AND :dataFinal");
		q.setParameter("dataInicial", dataInicial);
		q.setParameter("dataFinal", dataFinal);
		return q.getResultList();
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<EcfNotaFiscalCabecalho> getEcfNotaFiscalCabecalho(Date dataInicial, Date dataFinal) throws Exception {
		Query q = em.createQuery("SELECT o FROM EcfNotaFiscalCabecalho o WHERE o.dataEmissao BETWEEN :dataInicial AND :dataFinal");
		q.setParameter("dataInicial", dataInicial);
		q.setParameter("dataFinal", dataFinal);
		return q.getResultList();
	}
	
	@Override
	public Produto getProduto(String gtin) throws Exception {
		Query q = em.createQuery("SELECT o FROM Produto o WHERE o.gtin = :gtin");
		q.setParameter("gtin", gtin);
		return (Produto) q.getSingleResult();
	}
	
}
