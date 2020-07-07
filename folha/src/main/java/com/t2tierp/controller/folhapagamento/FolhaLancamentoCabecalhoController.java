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
package com.t2tierp.controller.folhapagamento;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.t2tierp.controller.AbstractController;
import com.t2tierp.model.bean.cadastros.Colaborador;
import com.t2tierp.model.bean.folhapagamento.FolhaEvento;
import com.t2tierp.model.bean.folhapagamento.FolhaLancamentoCabecalho;
import com.t2tierp.model.bean.folhapagamento.FolhaLancamentoDetalhe;
import com.t2tierp.model.dao.InterfaceDAO;
import com.t2tierp.util.Biblioteca;
import com.t2tierp.util.FacesContextUtil;

@ManagedBean
@ViewScoped
public class FolhaLancamentoCabecalhoController extends AbstractController<FolhaLancamentoCabecalho> implements Serializable {

	private static final long serialVersionUID = 1L;
	@EJB
	private InterfaceDAO<Colaborador> colaboradorDao;
	@EJB
	private InterfaceDAO<FolhaEvento> folhaEventoDao;

	private FolhaLancamentoDetalhe folhaLancamentoDetalhe;
	private FolhaLancamentoDetalhe folhaLancamentoDetalheSelecionado;

	@Override
	public Class<FolhaLancamentoCabecalho> getClazz() {
		return FolhaLancamentoCabecalho.class;
	}

	@Override
	public String getFuncaoBase() {
		return "FOLHA_LANCAMENTO_CABECALHO";
	}

	@Override
	public void incluir() {
		super.incluir();
		getObjeto().setListaFolhaLancamentoDetalhe(new HashSet<FolhaLancamentoDetalhe>());
	}

	public void incluirFolhaLancamentoDetalhe() {
		folhaLancamentoDetalhe = new FolhaLancamentoDetalhe();
		folhaLancamentoDetalhe.setFolhaLancamentoCabecalho(getObjeto());
	}

	public void alterarFolhaLancamentoDetalhe() {
		folhaLancamentoDetalhe = folhaLancamentoDetalheSelecionado;
	}

	public void salvarFolhaLancamentoDetalhe() {
		try {
			if (folhaLancamentoDetalhe.getId() == null) {
				getObjeto().getListaFolhaLancamentoDetalhe().add(folhaLancamentoDetalhe);
			}
			calculaEvento(folhaLancamentoDetalhe);
			salvar("Registro salvo com sucesso!");
		} catch (Exception e) {
			e.printStackTrace();
			FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro ao salvar o registro!", e.getMessage());
		}
	}

	public void excluirFolhaLancamentoDetalhe() {
		if (folhaLancamentoDetalheSelecionado == null || folhaLancamentoDetalheSelecionado.getId() == null) {
			FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_INFO, "Nenhum registro selecionado!", "");
		} else {
			getObjeto().getListaFolhaLancamentoDetalhe().remove(folhaLancamentoDetalheSelecionado);
			salvar("Registro excluído com sucesso!");
		}
	}

	private void calculaEvento(FolhaLancamentoDetalhe detalhe) throws Exception {
		/*
		 { Campo BASE_CALCULO da tabela FOLHA_EVENTO
		 01=Salário contratual: define que a base de cálculo deve ser calculada sobre o valor do salário contratual;
		 02=Salário mínimo: define que a base de cálculo deve ser calculada sobre o valor do salário mínimo;
		 03=Piso Salarial: define que a base de cálculo deve ser calculada sobre o valor do piso salarial definido no cadastro de sindicatos;
		 04=Líquido: define que a base de cálculo deve ser calculada sobre o líquido da folha;
		 }
		 { Os demais casos devem ser implementados a critério do Participante do T2Ti ERP }
		 */
		if (detalhe.getFolhaEvento().getBaseCalculo().equals("01")) {
			detalhe.setOrigem(getObjeto().getColaborador().getCargo().getSalario());
		}

		// Provento ou Desconto
		if (detalhe.getFolhaEvento().getTipo().equals("P")) {
			detalhe.setDesconto(BigDecimal.ZERO);
			BigDecimal provento = Biblioteca.multiplica(detalhe.getOrigem(), detalhe.getFolhaEvento().getTaxa());
			provento = Biblioteca.divide(provento, BigDecimal.valueOf(100));
			detalhe.setProvento(provento);
		} else {
			detalhe.setProvento(BigDecimal.ZERO);
			BigDecimal desconto = Biblioteca.multiplica(detalhe.getOrigem(), detalhe.getFolhaEvento().getTaxa());
			desconto = Biblioteca.divide(desconto, BigDecimal.valueOf(100));
			detalhe.setDesconto(desconto);
		}
	}

	public List<Colaborador> getListaColaborador(String nome) {
		List<Colaborador> listaColaborador = new ArrayList<>();
		try {
			listaColaborador = colaboradorDao.getBeansLike(Colaborador.class, "pessoa.nome", nome);
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return listaColaborador;
	}

	public List<FolhaEvento> getListaFolhaEvento(String nome) {
		List<FolhaEvento> listaFolhaEvento = new ArrayList<>();
		try {
			listaFolhaEvento = folhaEventoDao.getBeansLike(FolhaEvento.class, "nome", nome);
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return listaFolhaEvento;
	}

	public FolhaLancamentoDetalhe getFolhaLancamentoDetalhe() {
		return folhaLancamentoDetalhe;
	}

	public void setFolhaLancamentoDetalhe(FolhaLancamentoDetalhe folhaLancamentoDetalhe) {
		this.folhaLancamentoDetalhe = folhaLancamentoDetalhe;
	}

	public FolhaLancamentoDetalhe getFolhaLancamentoDetalheSelecionado() {
		return folhaLancamentoDetalheSelecionado;
	}

	public void setFolhaLancamentoDetalheSelecionado(FolhaLancamentoDetalhe folhaLancamentoDetalheSelecionado) {
		this.folhaLancamentoDetalheSelecionado = folhaLancamentoDetalheSelecionado;
	}

}
