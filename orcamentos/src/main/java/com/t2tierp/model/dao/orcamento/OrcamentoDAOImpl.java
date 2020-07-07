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
package com.t2tierp.model.dao.orcamento;

import java.math.BigDecimal;
import java.util.Date;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.t2tierp.model.bean.cadastros.ContaCaixa;

@Stateless
public class OrcamentoDAOImpl implements OrcamentoDAO {

	@PersistenceContext
	protected EntityManager em;

	@Override
	public BigDecimal getValorRecebimento(Date dataInicio, Date dataFim, ContaCaixa contaCaixa) throws Exception {
		String jpql = "SELECT SUM(o.valorRecebido) FROM FinParcelaRecebimento o WHERE o.dataRecebimento BETWEEN :dataInicio AND :dataFim";
		if (contaCaixa != null) {
			jpql += " AND o.contaCaixa = :contaCaixa";
		}
		Query query = em.createQuery(jpql);
		query.setParameter("dataInicio", dataInicio);
		query.setParameter("dataFim", dataFim);
		if (contaCaixa != null) {
			query.setParameter("contaCaixa", contaCaixa);
		}
		BigDecimal soma = (BigDecimal) query.getSingleResult();
		return soma == null ? BigDecimal.ZERO : soma;
	}

	@Override
	public BigDecimal getValorPagamento(Date dataInicio, Date dataFim, ContaCaixa contaCaixa) throws Exception {
		String jpql = "SELECT SUM(o.valorPago) FROM FinParcelaPagamento o WHERE o.dataPagamento BETWEEN :dataInicio AND :dataFim";
		if (contaCaixa != null) {
			jpql += " AND o.contaCaixa = :contaCaixa";
		}
		Query query = em.createQuery(jpql);
		query.setParameter("dataInicio", dataInicio);
		query.setParameter("dataFim", dataFim);
		if (contaCaixa != null) {
			query.setParameter("contaCaixa", contaCaixa);
		}
		BigDecimal soma = (BigDecimal) query.getSingleResult();
		return soma == null ? BigDecimal.ZERO : soma;
	}

}