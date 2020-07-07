package com.t2tierp.model.dao.contabilidade;

import java.util.List;

import javax.ejb.Local;

import com.t2tierp.model.bean.contabilidade.ViewBalancoPatrimonialVO;
import com.t2tierp.model.bean.financeiro.ViewFinMovimentoCaixaBancoID;

@Local
public interface ContabilidadeDAO {

	List<ViewBalancoPatrimonialVO> getBalancoPatrimonial(String ano) throws Exception;
	
	List<ViewFinMovimentoCaixaBancoID> getLivroCaixa(String periodo) throws Exception;
	
}
