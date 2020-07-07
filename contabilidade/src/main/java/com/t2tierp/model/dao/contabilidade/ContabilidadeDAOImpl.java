package com.t2tierp.model.dao.contabilidade;

import java.util.List;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import com.t2tierp.model.bean.contabilidade.ViewBalancoPatrimonialVO;
import com.t2tierp.model.bean.financeiro.ViewFinMovimentoCaixaBancoID;

@Stateless
public class ContabilidadeDAOImpl implements ContabilidadeDAO {

	@PersistenceContext
	protected EntityManager em;
	
	public ContabilidadeDAOImpl() {
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<ViewBalancoPatrimonialVO> getBalancoPatrimonial(String ano) throws Exception {
        String sql = "SELECT id, classificacao, descricao, "
                + "SUM((CASE WHEN ano=:anoAnterior THEN valor ELSE 0 END)) AS ANO_ANTERIOR,"
                + "SUM((CASE WHEN ano=:anoAtual THEN valor ELSE 0 END)) AS ANO_ATUAL "
                //+ "SUM(IF(ano=:anoAnterior, valor, NULL)) AS ANO_ANTERIOR, "
                //+ "SUM(IF(ano=:anoAtual, valor, NULL)) AS ANO_ATUAL "
                + "FROM view_balanco_patrimonial "
                + "GROUP BY classificacao, descricao, id "
                + "order by classificacao";

        String anoAnterior = String.valueOf(Integer.valueOf(ano) - 1);
		Query query = em.createNativeQuery(sql, ViewBalancoPatrimonialVO.class);
		query.setParameter("anoAtual", ano);
		query.setParameter("anoAnterior", anoAnterior);
		List<ViewBalancoPatrimonialVO> listaBalanco = query.getResultList();
		return listaBalanco;		
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ViewFinMovimentoCaixaBancoID> getLivroCaixa(String periodo) throws Exception {
        String sql = "select * from VIEW_FIN_MOVIMENTO_CAIXA_BANCO "
                + "where to_char(DATA_PAGO_RECEBIDO, 'MM/YYYY') = :PERIODO "
                //+ "where DATE_FORMAT(DATA_PAGO_RECEBIDO,'%m/%Y') = :PERIODO "
                + "order by OPERACAO";
        Query query = em.createNativeQuery(sql, ViewFinMovimentoCaixaBancoID.class);
		query.setParameter("PERIODO", periodo);
		List<ViewFinMovimentoCaixaBancoID> listaCaixa = query.getResultList();
		return listaCaixa;
	}

}
