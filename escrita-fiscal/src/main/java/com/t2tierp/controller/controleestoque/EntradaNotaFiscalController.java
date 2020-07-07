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
package com.t2tierp.controller.controleestoque;

import java.io.File;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.io.FileUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import com.t2tierp.controller.AbstractController;
import com.t2tierp.controller.T2TiLazyDataModel;
import com.t2tierp.controller.nfe.NfeCabecalhoDataModel;
import com.t2tierp.model.bean.cadastros.Produto;
import com.t2tierp.model.bean.cadastros.Transportadora;
import com.t2tierp.model.bean.nfe.NfeCabecalho;
import com.t2tierp.model.bean.nfe.NfeCteReferenciado;
import com.t2tierp.model.bean.nfe.NfeCupomFiscalReferenciado;
import com.t2tierp.model.bean.nfe.NfeDetalhe;
import com.t2tierp.model.bean.nfe.NfeDetalheImpostoIcms;
import com.t2tierp.model.bean.nfe.NfeDuplicata;
import com.t2tierp.model.bean.nfe.NfeEmitente;
import com.t2tierp.model.bean.nfe.NfeFatura;
import com.t2tierp.model.bean.nfe.NfeLocalEntrega;
import com.t2tierp.model.bean.nfe.NfeLocalRetirada;
import com.t2tierp.model.bean.nfe.NfeNfReferenciada;
import com.t2tierp.model.bean.nfe.NfeProdRuralReferenciada;
import com.t2tierp.model.bean.nfe.NfeReferenciada;
import com.t2tierp.model.bean.nfe.NfeTransporte;
import com.t2tierp.model.bean.nfe.NfeTransporteReboque;
import com.t2tierp.model.bean.nfe.NfeTransporteVolume;
import com.t2tierp.model.bean.tributacao.TributOperacaoFiscal;
import com.t2tierp.model.dao.InterfaceDAO;
import com.t2tierp.model.dao.controleestoque.ControleEstoqueDAO;
import com.t2tierp.util.FacesContextUtil;

@ManagedBean
@ViewScoped
public class EntradaNotaFiscalController extends AbstractController<NfeCabecalho> implements Serializable {

	private static final long serialVersionUID = 1L;
	private NfeCabecalhoDataModel dataModel;
	private NfeDetalhe nfeDetalhe;
	private NfeDetalhe nfeDetalheSelecionado;
	private NfeDetalheImpostoIcms nfeDetalheImpostoIcms = new NfeDetalheImpostoIcms();
	private NfeTransporteReboque nfeTransporteReboque;
	private NfeTransporteReboque nfeTransporteReboqueSelecionado;
	private NfeEmitente nfeEmitente;

	@EJB
	private InterfaceDAO<TributOperacaoFiscal> operacaoFiscalDao;
	@EJB
	private InterfaceDAO<Produto> produtoDao;
	@EJB
	private ControleEstoqueDAO controleEstoqueDao;
	@EJB
	private InterfaceDAO<NfeDetalhe> nfeDetalheDao;
	@EJB
	private InterfaceDAO<Transportadora> transportadoraDao;

	@Override
	public Class<NfeCabecalho> getClazz() {
		return NfeCabecalho.class;
	}

	@Override
	public String getFuncaoBase() {
		return "NFE_CABECALHO";
	}

	@Override
	public T2TiLazyDataModel<NfeCabecalho> getDataModel() {
		if (dataModel == null) {
			dataModel = new NfeCabecalhoDataModel();
			dataModel.setClazz(getClazz());
			dataModel.setDao(dao);
		}
		return dataModel;
	}

	@Override
	public void incluir() {
		super.incluir();
		getObjeto().setEmitente(new NfeEmitente());
		getObjeto().getEmitente().setNfeCabecalho(getObjeto());
		getObjeto().setLocalEntrega(new NfeLocalEntrega());
		getObjeto().getLocalEntrega().setNfeCabecalho(getObjeto());
		getObjeto().setLocalRetirada(new NfeLocalRetirada());
		getObjeto().getLocalRetirada().setNfeCabecalho(getObjeto());
		getObjeto().setTransporte(new NfeTransporte());
		getObjeto().getTransporte().setNfeCabecalho(getObjeto());
		getObjeto().setFatura(new NfeFatura());
		getObjeto().getFatura().setNfeCabecalho(getObjeto());

		getObjeto().setListaNfeReferenciada(new HashSet<NfeReferenciada>());
		getObjeto().setListaNfReferenciada(new HashSet<NfeNfReferenciada>());
		getObjeto().setListaCteReferenciado(new HashSet<NfeCteReferenciado>());
		getObjeto().setListaProdRuralReferenciada(new HashSet<NfeProdRuralReferenciada>());
		getObjeto().setListaCupomFiscalReferenciado(new HashSet<NfeCupomFiscalReferenciado>());
		getObjeto().getTransporte().setListaTransporteReboque(new HashSet<NfeTransporteReboque>());
		getObjeto().getTransporte().setListaTransporteVolume(new HashSet<NfeTransporteVolume>());
		getObjeto().setListaDuplicata(new HashSet<NfeDuplicata>());
		getObjeto().setListaNfeDetalhe(new ArrayList<NfeDetalhe>());

		
		valoresPadrao();
	}

	@Override
	public void salvar() {
		try {
			if (verificaProdutoNaoCadastrado()) {
				FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_ERROR, "Foi informado produto que ainda não foi cadastrado.", "Procedimento não realizado.");
			} else {
				HashMap<String, Object> filtro = new HashMap<>();
				filtro.put("nfeCabecalho", getObjeto());
				String atributos[] = new String[] { "produto", "quantidadeComercial" };
				List<NfeDetalhe> listaNfeDetOld = nfeDetalheDao.getBeans(NfeDetalhe.class, 0, 0, null, null, filtro, atributos);
				for (NfeDetalhe detalhe : listaNfeDetOld) {
					controleEstoqueDao.atualizaEstoque(detalhe.getProduto().getId(), detalhe.getQuantidadeComercial());
				}
			}
			controleEstoqueDao.atualizaEstoque(getObjeto().getListaNfeDetalhe());
			super.salvar();
		} catch (Exception e) {
			e.printStackTrace();
			FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro ao salvar o registro", e.getMessage());
		}
	}

	private boolean verificaProdutoNaoCadastrado() {
        boolean produtoNaoCadastrado = false;
        try {
            for (NfeDetalhe d : getObjeto().getListaNfeDetalhe()) {
                Produto produto = produtoDao.getBean(Produto.class, "gtin", d.getGtin());
            	
                if (produto != null) {
                    d.setProduto(produto);
                    d.setProdutoCadastrado(true);
                } else {
                    d.setProdutoCadastrado(false);
                    produtoNaoCadastrado = true;
                }
            }
        } catch(Exception e) {
        	e.printStackTrace();
        	FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro ao buscar os dados do produto.", e.getMessage());
        }
        return produtoNaoCadastrado;
	}
	
	private void valoresPadrao() {
		getObjeto().setTipoOperacao(0);
		getObjeto().setStatusNota(0);
		getObjeto().setFormatoImpressaoDanfe(1);
		getObjeto().setConsumidorOperacao(1);
		getObjeto().setConsumidorPresenca(1);
		getObjeto().setTipoEmissao(1);
		getObjeto().setFinalidadeEmissao(1);
		getObjeto().setIndicadorFormaPagamento(0);
		getObjeto().setLocalDestino(1);
		getObjeto().getTransporte().setModalidadeFrete(0);

		Date dataAtual = new Date();
		getObjeto().setEmpresa(FacesContextUtil.getEmpresaUsuario());
		getObjeto().setUfEmitente(getObjeto().getEmpresa().getCodigoIbgeUf());
		getObjeto().setDataHoraEmissao(dataAtual);
		getObjeto().setDataHoraEntradaSaida(dataAtual);

		getObjeto().setBaseCalculoIcms(BigDecimal.ZERO);
		getObjeto().setValorIcms(BigDecimal.ZERO);
		getObjeto().setValorTotalProdutos(BigDecimal.ZERO);
		getObjeto().setBaseCalculoIcmsSt(BigDecimal.ZERO);
		getObjeto().setValorIcmsSt(BigDecimal.ZERO);
		getObjeto().setValorIpi(BigDecimal.ZERO);
		getObjeto().setValorPis(BigDecimal.ZERO);
		getObjeto().setValorCofins(BigDecimal.ZERO);
		getObjeto().setValorFrete(BigDecimal.ZERO);
		getObjeto().setValorSeguro(BigDecimal.ZERO);
		getObjeto().setValorDespesasAcessorias(BigDecimal.ZERO);
		getObjeto().setValorDesconto(BigDecimal.ZERO);
		getObjeto().setValorTotal(BigDecimal.ZERO);
		getObjeto().setValorImpostoImportacao(BigDecimal.ZERO);
		getObjeto().setBaseCalculoIssqn(BigDecimal.ZERO);
		getObjeto().setValorIssqn(BigDecimal.ZERO);
		getObjeto().setValorPisIssqn(BigDecimal.ZERO);
		getObjeto().setValorCofinsIssqn(BigDecimal.ZERO);
		getObjeto().setValorServicos(BigDecimal.ZERO);
		getObjeto().setValorRetidoPis(BigDecimal.ZERO);
		getObjeto().setValorRetidoCofins(BigDecimal.ZERO);
		getObjeto().setValorRetidoCsll(BigDecimal.ZERO);
		getObjeto().setBaseCalculoIrrf(BigDecimal.ZERO);
		getObjeto().setValorRetidoIrrf(BigDecimal.ZERO);
		getObjeto().setBaseCalculoPrevidencia(BigDecimal.ZERO);
		getObjeto().setValorRetidoPrevidencia(BigDecimal.ZERO);
		getObjeto().setValorIcmsDesonerado(BigDecimal.ZERO);
	}

	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void importaXML(FileUploadEvent event) {
        try {
        	UploadedFile arquivoUpload = event.getFile();

            File file = File.createTempFile("nfe", ".xml");
            FileUtils.copyInputStreamToFile(arquivoUpload.getInputstream(), file);

            if (file != null) {
                ImportaXMLNFe importaXml = new ImportaXMLNFe();
                Map map = importaXml.importarXmlNFe(file);
                setObjeto((NfeCabecalho) map.get("cabecalho"));

                getObjeto().setEmitente((NfeEmitente) map.get("emitente"));

                getObjeto().setListaNfeDetalhe((ArrayList) map.get("detalhe"));

                getObjeto().setListaNfeReferenciada((HashSet) map.get("nfeReferenciada"));

                getObjeto().setListaNfReferenciada((HashSet) map.get("nf1Referenciada"));

                getObjeto().setListaProdRuralReferenciada((HashSet) map.get("prodRuralReferenciada"));

                getObjeto().setListaCteReferenciado((HashSet) map.get("cteReferenciado"));

                getObjeto().setLocalEntrega((NfeLocalEntrega) map.get("localEntrega"));

                getObjeto().setLocalRetirada((NfeLocalRetirada) map.get("localRetirada"));

                getObjeto().setTransporte((NfeTransporte) map.get("transporte"));

                getObjeto().getTransporte().setListaTransporteReboque((HashSet) map.get("transporteReboque"));

                getObjeto().getTransporte().setListaTransporteVolume((HashSet) map.get("transporteVolume"));

                getObjeto().setFatura((NfeFatura) map.get("fatura"));

                getObjeto().setListaDuplicata((HashSet) map.get("duplicata"));

                verificaProdutoNaoCadastrado();

                FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_INFO, "Dados importados com sucesso!", "");
            }
        } catch (Exception e) {
            e.printStackTrace();
            FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_ERROR, "Erro ao importar os dados!", e.getMessage());
        }
	}
	
	public void incluiProduto() {
		nfeDetalhe = new NfeDetalhe();
		nfeDetalhe.setNfeCabecalho(getObjeto());
	}

	public void alteraProduto() {
		nfeDetalhe = nfeDetalheSelecionado;
	}

	public void salvaProduto() {
		try {
			if (!getObjeto().getListaNfeDetalhe().contains(nfeDetalhe)) {
				getObjeto().getListaNfeDetalhe().add(nfeDetalhe);
				FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_INFO, "Registro incluído!", null);
			} else {
				FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_INFO, "Registro alterado!", null);
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro ao salvar o registro", e.getMessage());
		}
	}

	public void excluirProduto() {
		if (nfeDetalheSelecionado == null) {
			FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_INFO, "Nenhum registro selecionado!", null);
		} else {
			getObjeto().getListaNfeDetalhe().remove(nfeDetalheSelecionado);
			FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_INFO, "Registro excluído!", null);
		}
	}
	
	public void detalheImposto() {
		setNfeDetalheImpostoIcms(nfeDetalheSelecionado.getNfeDetalheImpostoIcms());
	}

	public List<Transportadora> getListaTransportadora(String nome) {
		List<Transportadora> listaTransportadora = new ArrayList<>();
		try {
			listaTransportadora = transportadoraDao.getBeansLike(Transportadora.class, "pessoa.nome", nome);
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return listaTransportadora;
	}
	
	public List<Produto> getListaProduto(String descricao) {
		List<Produto> listaProduto = new ArrayList<>();
		try {
			listaProduto = produtoDao.getBeansLike(Produto.class, "descricaoPdv", descricao);
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return listaProduto;
	}
	
	public NfeDetalhe getNfeDetalhe() {
		return nfeDetalhe;
	}

	public void setNfeDetalhe(NfeDetalhe nfeDetalhe) {
		this.nfeDetalhe = nfeDetalhe;
	}

	public NfeDetalhe getNfeDetalheSelecionado() {
		return nfeDetalheSelecionado;
	}

	public void setNfeDetalheSelecionado(NfeDetalhe nfeDetalheSelecionado) {
		this.nfeDetalheSelecionado = nfeDetalheSelecionado;
	}

	public NfeTransporteReboque getNfeTransporteReboque() {
		return nfeTransporteReboque;
	}

	public void setNfeTransporteReboque(NfeTransporteReboque nfeTransporteReboque) {
		this.nfeTransporteReboque = nfeTransporteReboque;
	}

	public NfeTransporteReboque getNfeTransporteReboqueSelecionado() {
		return nfeTransporteReboqueSelecionado;
	}

	public void setNfeTransporteReboqueSelecionado(NfeTransporteReboque nfeTransporteReboqueSelecionado) {
		this.nfeTransporteReboqueSelecionado = nfeTransporteReboqueSelecionado;
	}

	public NfeDetalheImpostoIcms getNfeDetalheImpostoIcms() {
		return nfeDetalheImpostoIcms;
	}

	public void setNfeDetalheImpostoIcms(NfeDetalheImpostoIcms nfeDetalheImpostoIcms) {
		this.nfeDetalheImpostoIcms = nfeDetalheImpostoIcms;
	}

	public NfeEmitente getNfeEmitente() {
		return nfeEmitente;
	}

	public void setNfeEmitente(NfeEmitente nfeEmitente) {
		this.nfeEmitente = nfeEmitente;
	}

}
