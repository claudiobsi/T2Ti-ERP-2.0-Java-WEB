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
package com.t2tierp.controller;

import java.io.Serializable;
import java.util.HashMap;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;

import com.t2tierp.model.dao.InterfaceDAO;
import com.t2tierp.util.FacesContextUtil;

public abstract class AbstractController<T> implements Serializable {

	private static final long serialVersionUID = 1L;
	private T2TiLazyDataModel<T> dataModel;
	private T objetoSelecionado;
	private T objeto;
	private boolean telaGrid = true;
	// domínios
	public HashMap<String, String> simNao;
	public HashMap<String, String> tipoPessoa;
	public HashMap<String, String> sexo;
	public HashMap<String, String> tipoSangue;
	public HashMap<String, String> racaCor;
	public HashMap<String, String> crt;
	public HashMap<String, String> tipoRegimeEmpresa;
	public HashMap<String, Integer> tipoTelefone;
	public HashMap<String, String> clienteIndicadorPreco;
	public HashMap<String, String> clienteTipoFrete;
	public HashMap<String, String> clienteFormaDesconto;
	public HashMap<String, String> fornecedorLocalizacao;
	public HashMap<String, String> colaboradorFormaPagamento;
	public HashMap<String, String> tipoSindicato;
	public HashMap<String, String> tipoItemSped;
	public HashMap<String, String> produtoClasse;
	public HashMap<String, String> produtoTipo;
	public HashMap<String, String> produtoIat;
	public HashMap<String, String> produtoIppt;
	public HashMap<String, String> talonarioChequeStatus;
	public HashMap<String, String> chequeStatus;
	public HashMap<String, String> tipoContaCaixa;
	public HashMap<String, String> tipoFeriado;
	public HashMap<String, String> feriadoAbrangencia;
	public HashMap<String, String> codigoModeloNfe;
	public HashMap<String, Integer> localDestinoNfe;
	public HashMap<String, Integer> consumidorOperacaoNfe;
	public HashMap<String, Integer> consumidorPresencaNfe;
	public HashMap<String, Integer> tipoOperacaoNfe;
	public HashMap<String, Integer> tipoEmissaoNfe;
	public HashMap<String, Integer> finalidadeEmissaoNfe;
	public HashMap<String, Integer> formatoImpressaoDanfeNfe;
	public HashMap<String, Integer> indicadorFormaPagamentoNfe;
	public HashMap<String, Integer> statusNfe;
	public HashMap<String, Integer> modalidadeFreteNfe;
	public HashMap<String, Integer> origemMercadoriaNfe;
	public HashMap<String, String> compraSituacaoCotacao;
	public HashMap<String, String> compraFormaPagamento;
	public HashMap<String, String> compraTipoFrete;

	@EJB
	protected InterfaceDAO<T> dao;

	@PostConstruct
	public void init() {
		dataModel = new T2TiLazyDataModel<T>();
		dataModel.setClazz(getClazz());
		dataModel.setDao(dao);
		
		tipoPessoa = new HashMap<>();
		tipoPessoa.put("Fisica", "F");
		tipoPessoa.put("Jurídica", "J");

		simNao = new HashMap<>();
		simNao.put("Sim", "S");
		simNao.put("Não", "N");

		sexo = new HashMap<>();
		sexo.put("Masculino", "M");
		sexo.put("Feminino", "F");

		tipoSangue = new HashMap<>();
		tipoSangue.put("A+", "A+");
		tipoSangue.put("B+", "B+");
		tipoSangue.put("O+", "O+");
		tipoSangue.put("AB+", "AB+");
		tipoSangue.put("A-", "A-");
		tipoSangue.put("B-", "B-");
		tipoSangue.put("AB-", "AB-");
		tipoSangue.put("O-", "O-");

		racaCor = new HashMap<>();
		racaCor.put("Branco", "B");
		racaCor.put("Negro", "N");
		racaCor.put("Pardo", "P");
		racaCor.put("Indio", "I");

		crt = new HashMap<>();
		crt.put("1 - Simples Nacional", "1");
		crt.put("2 - Simples Nac - Excesso", "2");
		crt.put("3 - Regime Normal", "3");

		tipoRegimeEmpresa = new HashMap<>();
		tipoRegimeEmpresa.put("Lucro Real", "1");
		tipoRegimeEmpresa.put("Lucro Presumido", "2");
		tipoRegimeEmpresa.put("Simples Nacional", "3");

		tipoTelefone = new HashMap<>();
		tipoTelefone.put("Residencial", 0);
		tipoTelefone.put("Comercial", 1);
		tipoTelefone.put("Celular", 2);
		tipoTelefone.put("Outro", 3);

		clienteIndicadorPreco = new HashMap<>();
		clienteIndicadorPreco.put("Tabela", "T");
		clienteIndicadorPreco.put("Último Pedido", "P");

		clienteTipoFrete = new HashMap<>();
		clienteTipoFrete.put("Emitente", "E");
		clienteTipoFrete.put("Destinatario", "D");
		clienteTipoFrete.put("Sem frete", "S");
		clienteTipoFrete.put("Terceiros", "T");

		clienteFormaDesconto = new HashMap<>();
		clienteFormaDesconto.put("Produto", "P");
		clienteFormaDesconto.put("Fim do Pedido", "F");

		fornecedorLocalizacao = new HashMap<>();
		fornecedorLocalizacao.put("Nacional", "N");
		fornecedorLocalizacao.put("Exterior", "E");
		
		colaboradorFormaPagamento = new HashMap<>();
		colaboradorFormaPagamento.put("Dinheiro", "1");
		colaboradorFormaPagamento.put("Cheque", "2");
		colaboradorFormaPagamento.put("Conta", "3");
		
		tipoSindicato = new HashMap<>();
		tipoSindicato.put("Empregados", "E");
		tipoSindicato.put("Patronal", "P");

		tipoItemSped = new HashMap<>();
		tipoItemSped.put("Mercadoria para Revenda", "00");
		tipoItemSped.put("Matéria-Prima", "01");
		tipoItemSped.put("Embalagem", "02");
		tipoItemSped.put("Produto em Processo", "03");
		tipoItemSped.put("Produto Acabado", "04");
		tipoItemSped.put("Subproduto", "05");
		tipoItemSped.put("Produto Intermediário", "06");
		tipoItemSped.put("Material de Uso e Consumo", "07");
		tipoItemSped.put("Ativo Imobilizado", "08");
		tipoItemSped.put("Serviços", "09");
		tipoItemSped.put("Outros Insumos", "10");
		tipoItemSped.put("Outras", "99");
		
		produtoClasse = new HashMap<>();
		produtoClasse.put("A", "A");
		produtoClasse.put("B", "B");
		produtoClasse.put("C", "C");
		
		produtoTipo = new HashMap<>();
		produtoTipo.put("Venda", "V");
		produtoTipo.put("Composição", "C");
		produtoTipo.put("Produção", "P");
		produtoTipo.put("Insumo", "I");
		produtoTipo.put("Uso Proprio", "U");

		produtoIat = new HashMap<>();
		produtoIat.put("Arredondamento", "A");
		produtoIat.put("Truncamento", "T");

		produtoIppt = new HashMap<>();
		produtoIppt.put("Próprio", "P");
		produtoIppt.put("Terceiro", "T");
		
		talonarioChequeStatus = new HashMap<>();
		talonarioChequeStatus.put("Normal", "N");
		talonarioChequeStatus.put("Cancelado", "C");
		talonarioChequeStatus.put("Extraviado", "E");
		talonarioChequeStatus.put("Utilizado", "U");

		chequeStatus = new HashMap<>();
		chequeStatus.put("Em Ser", "E");
		chequeStatus.put("Baixado", "B");
		chequeStatus.put("Utilizado", "U");
		chequeStatus.put("Compensado", "C");
		chequeStatus.put("Cancelado", "N");
		
		tipoContaCaixa = new HashMap<>();
		tipoContaCaixa.put("Corrente", "C");
		tipoContaCaixa.put("Poupança", "P");
		tipoContaCaixa.put("Investimento", "I");
		tipoContaCaixa.put("Caixa Interno", "X");
		
		tipoFeriado = new HashMap<>();
		tipoFeriado.put("Fixo", "F");
		tipoFeriado.put("Móvel", "M");
		
		feriadoAbrangencia = new HashMap<>();
		feriadoAbrangencia.put("Federal", "F");
		feriadoAbrangencia.put("Estadual", "E");
		feriadoAbrangencia.put("Municipal", "M");
		
		codigoModeloNfe = new HashMap<>();
		codigoModeloNfe.put("Nota Fiscal Eletrônica - NFe", "55");
		
		localDestinoNfe = new HashMap<>();
		localDestinoNfe.put("Operação Interna", 1);
		localDestinoNfe.put("Operação Interestadual", 2);
		localDestinoNfe.put("Operação com Exterior", 3);
		
		consumidorOperacaoNfe = new HashMap<>();
		consumidorOperacaoNfe.put("Normal", 0);
		consumidorOperacaoNfe.put("Consumidor Final", 1);
		
		consumidorPresencaNfe = new HashMap<>();
		consumidorPresencaNfe.put("Operação Presencial", 1);
		consumidorPresencaNfe.put("Operação não Presencial - Internet", 2);
		consumidorPresencaNfe.put("Operação não Presencial - Teleatendimento", 3);
		consumidorPresencaNfe.put("Operação não Presencial - Outros", 9);
		consumidorPresencaNfe.put("Não se aplica", 0);
		
		tipoOperacaoNfe = new HashMap<>();
		tipoOperacaoNfe.put("Entrada", 0);
		tipoOperacaoNfe.put("Saída", 1);
		
		tipoEmissaoNfe = new HashMap<>();
		tipoEmissaoNfe.put("Normal", 1);
		tipoEmissaoNfe.put("Contigência", 2);
		tipoEmissaoNfe.put("Contingência SCAN", 3);
		tipoEmissaoNfe.put("Contingência DPEC", 4);
		tipoEmissaoNfe.put("Contingência FS-DA", 5);

		finalidadeEmissaoNfe = new HashMap<>();
		finalidadeEmissaoNfe.put("Normal", 1);
		finalidadeEmissaoNfe.put("Complementar", 2);
		finalidadeEmissaoNfe.put("Ajuste", 3);
		
		formatoImpressaoDanfeNfe = new HashMap<>();
		formatoImpressaoDanfeNfe.put("Retrato", 1);
		formatoImpressaoDanfeNfe.put("Paisagem", 1);
		
		indicadorFormaPagamentoNfe = new HashMap<>();
		indicadorFormaPagamentoNfe.put("A Vista", 0);
		indicadorFormaPagamentoNfe.put("A Prazo", 1);
		indicadorFormaPagamentoNfe.put("Outros", 2);
		
		statusNfe = new HashMap<>();
		statusNfe.put("Em Edição", 0);
		statusNfe.put("Salva", 1);
		statusNfe.put("Validada", 2);
		statusNfe.put("Assinada", 3);
		statusNfe.put("Enviada", 4);
		statusNfe.put("Autorizada", 5);
		statusNfe.put("Cancelada", 6);
		
		modalidadeFreteNfe = new HashMap<>();
		modalidadeFreteNfe.put("Conta Emitente", 0);
		modalidadeFreteNfe.put("Conta Destinatário", 1);
		modalidadeFreteNfe.put("Conta Terceiros", 2);
		modalidadeFreteNfe.put("Sem Frete", 9);
		
		origemMercadoriaNfe = new HashMap<>();
		origemMercadoriaNfe.put("Nacional", 0);
		origemMercadoriaNfe.put("Estrangeira - Importação direta", 1);
		origemMercadoriaNfe.put("Estrangeira - Adquirida no mercado interno", 2);
		
		compraSituacaoCotacao = new HashMap<>();
		compraSituacaoCotacao.put("Aberta", "A");
		compraSituacaoCotacao.put("Confirmada", "C");
		compraSituacaoCotacao.put("Fechada", "F");

		compraFormaPagamento = new HashMap<>();
		compraFormaPagamento.put("A Vista", "0");
		compraFormaPagamento.put("A Prazo", "1");
		compraFormaPagamento.put("Outros", "2");

		compraTipoFrete = new HashMap<>();
		compraTipoFrete.put("CIF", "C");
		compraTipoFrete.put("FOB", "F");
		
	}

	/* https://java.net/jira/browse/JAVASERVERFACES-1808 */
	@SuppressWarnings("rawtypes")
	public String keyFromValue(HashMap map, Object value) {
		for (Object o : map.keySet()) {
			if (map.get(o).equals(value)) {
				return String.valueOf(o);
			}
		}
		return null;
	}

	public abstract Class<T> getClazz();

	public abstract String getFuncaoBase();

	public T2TiLazyDataModel<T> getDataModel() {
		return dataModel;
	}

	public void incluir() {
		try {
			objeto = (T) getClazz().newInstance();
			telaGrid = false;
		} catch (InstantiationException | IllegalAccessException e) {
			FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro ao iniciar a inclusão do registro!", e.getMessage());
		}
	}

	public void alterar() {
		objeto = objetoSelecionado;
		telaGrid = false;
	}

	public void salvar() {
		salvar(null);
	}

	public void salvar(String mensagem) {
		try {
			objeto = dao.merge(objeto);
			FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_INFO, mensagem != null ? mensagem : "Registro salvo com sucesso!", null);
		} catch (Exception e) {
			e.printStackTrace();
			FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro ao salvar o registro!", e.getMessage());
		}
	}

	public void excluir() {
		try {
			Integer idObjeto = null;
			if (objetoSelecionado != null) {
				idObjeto = (Integer) getClazz().getMethod("getId").invoke(objetoSelecionado);
			}
			if (objetoSelecionado == null || idObjeto == null) {
				FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_INFO, "Nenhum registro selecionado!", null);
			} else {
				dao.excluir(objetoSelecionado, idObjeto);
				FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_INFO, "Registro excluído com sucesso!", null);
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro ao excluir o registro!", e.getMessage());
		}
	}

	public void voltar() {
		telaGrid = true;
	}

	public boolean isTelaGrid() {
		return telaGrid;
	}

	public boolean podeConsultar() {
		return FacesContextUtil.isUserInRole(getFuncaoBase() + "_CONSULTA") || FacesContextUtil.isUserInRole("ADMIN");
	}

	public boolean podeInserir() {
		return FacesContextUtil.isUserInRole(getFuncaoBase() + "_INSERE") || FacesContextUtil.isUserInRole("ADMIN");
	}

	public boolean podeAlterar() {
		return FacesContextUtil.isUserInRole(getFuncaoBase() + "_ALTERA") || FacesContextUtil.isUserInRole("ADMIN");
	}

	public boolean podeExcluir() {
		return FacesContextUtil.isUserInRole(getFuncaoBase() + "_EXCLUI") || FacesContextUtil.isUserInRole("ADMIN");
	}

	public T getObjetoSelecionado() {
		return objetoSelecionado;
	}

	public void setObjetoSelecionado(T objetoSelecionado) {
		this.objetoSelecionado = objetoSelecionado;
	}

	public T getObjeto() {
		return objeto;
	}

	public void setObjeto(T objeto) {
		this.objeto = objeto;
	}

	public HashMap<String, String> getSimNao() {
		return simNao;
	}

	public HashMap<String, String> getTipoPessoa() {
		return tipoPessoa;
	}

	public HashMap<String, String> getSexo() {
		return sexo;
	}

	public HashMap<String, String> getTipoSangue() {
		return tipoSangue;
	}

	public HashMap<String, String> getRacaCor() {
		return racaCor;
	}

	public HashMap<String, String> getCrt() {
		return crt;
	}

	public HashMap<String, String> getTipoRegimeEmpresa() {
		return tipoRegimeEmpresa;
	}

	public HashMap<String, Integer> getTipoTelefone() {
		return tipoTelefone;
	}

	public HashMap<String, String> getClienteIndicadorPreco() {
		return clienteIndicadorPreco;
	}

	public HashMap<String, String> getClienteTipoFrete() {
		return clienteTipoFrete;
	}

	public HashMap<String, String> getClienteFormaDesconto() {
		return clienteFormaDesconto;
	}

	public HashMap<String, String> getFornecedorLocalizacao() {
		return fornecedorLocalizacao;
	}

	public HashMap<String, String> getColaboradorFormaPagamento() {
		return colaboradorFormaPagamento;
	}

	public HashMap<String, String> getTipoSindicato() {
		return tipoSindicato;
	}

	public HashMap<String, String> getTipoItemSped() {
		return tipoItemSped;
	}

	public HashMap<String, String> getProdutoClasse() {
		return produtoClasse;
	}

	public HashMap<String, String> getProdutoTipo() {
		return produtoTipo;
	}

	public HashMap<String, String> getProdutoIat() {
		return produtoIat;
	}

	public HashMap<String, String> getProdutoIppt() {
		return produtoIppt;
	}

	public HashMap<String, String> getTalonarioChequeStatus() {
		return talonarioChequeStatus;
	}

	public HashMap<String, String> getChequeStatus() {
		return chequeStatus;
	}

	public HashMap<String, String> getTipoContaCaixa() {
		return tipoContaCaixa;
	}

	public HashMap<String, String> getTipoFeriado() {
		return tipoFeriado;
	}

	public HashMap<String, String> getFeriadoAbrangencia() {
		return feriadoAbrangencia;
	}

	public HashMap<String, String> getCodigoModeloNfe() {
		return codigoModeloNfe;
	}

	public HashMap<String, Integer> getLocalDestinoNfe() {
		return localDestinoNfe;
	}

	public HashMap<String, Integer> getConsumidorOperacaoNfe() {
		return consumidorOperacaoNfe;
	}

	public HashMap<String, Integer> getConsumidorPresencaNfe() {
		return consumidorPresencaNfe;
	}

	public HashMap<String, Integer> getTipoOperacaoNfe() {
		return tipoOperacaoNfe;
	}

	public HashMap<String, Integer> getTipoEmissaoNfe() {
		return tipoEmissaoNfe;
	}

	public HashMap<String, Integer> getFinalidadeEmissaoNfe() {
		return finalidadeEmissaoNfe;
	}

	public HashMap<String, Integer> getFormatoImpressaoDanfeNfe() {
		return formatoImpressaoDanfeNfe;
	}

	public HashMap<String, Integer> getIndicadorFormaPagamentoNfe() {
		return indicadorFormaPagamentoNfe;
	}

	public HashMap<String, Integer> getStatusNfe() {
		return statusNfe;
	}

	public HashMap<String, Integer> getModalidadeFreteNfe() {
		return modalidadeFreteNfe;
	}

	public HashMap<String, Integer> getOrigemMercadoriaNfe() {
		return origemMercadoriaNfe;
	}

	public HashMap<String, String> getCompraSituacaoCotacao() {
		return compraSituacaoCotacao;
	}

	public HashMap<String, String> getCompraFormaPagamento() {
		return compraFormaPagamento;
	}

	public HashMap<String, String> getCompraTipoFrete() {
		return compraTipoFrete;
	}
	
}