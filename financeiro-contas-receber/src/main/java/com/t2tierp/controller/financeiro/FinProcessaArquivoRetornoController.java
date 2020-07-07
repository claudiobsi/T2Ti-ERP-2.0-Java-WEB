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
package com.t2tierp.controller.financeiro;

import java.io.File;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.io.FileUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import com.t2tierp.controller.AbstractController;
import com.t2tierp.edi.cnab240.bb.DetalheLoteSegmentoT;
import com.t2tierp.edi.cnab240.bb.DetalheLoteSegmentoU;
import com.t2tierp.edi.cnab240.bb.LeArquivoRetornoBB;
import com.t2tierp.model.bean.administrativo.AdmParametro;
import com.t2tierp.model.bean.financeiro.FinParcelaReceber;
import com.t2tierp.model.bean.financeiro.FinParcelaRecebimento;
import com.t2tierp.model.bean.financeiro.FinProcessamentoRetorno;
import com.t2tierp.model.bean.financeiro.FinStatusParcela;
import com.t2tierp.model.bean.financeiro.FinTipoRecebimento;
import com.t2tierp.model.dao.Filtro;
import com.t2tierp.model.dao.InterfaceDAO;
import com.t2tierp.util.FacesContextUtil;

@ManagedBean
@ViewScoped
public class FinProcessaArquivoRetornoController extends AbstractController<FinProcessamentoRetorno> implements Serializable {

	private static final long serialVersionUID = 1L;
	@EJB
	private InterfaceDAO<FinTipoRecebimento> finTipoRecebimentoDao;
	@EJB
	private InterfaceDAO<AdmParametro> admParametroDao;
	@EJB
	private InterfaceDAO<FinStatusParcela> finStatusParcelaDao;
	@EJB
	private InterfaceDAO<FinParcelaReceber> finParcelaReceberDao;

	private List<FinProcessamentoRetorno> resultadoProcessamento;

	@Override
	public Class<FinProcessamentoRetorno> getClazz() {
		return FinProcessamentoRetorno.class;
	}

	@Override
	public String getFuncaoBase() {
		return "PROCESSA_ARQUIVO_RETORNO";
	}

	public void processaRetorno(FileUploadEvent event) {
		try {
			UploadedFile arquivo = event.getFile();
			File file = File.createTempFile("retorno", ".txt");
			FileUtils.copyInputStreamToFile(arquivo.getInputstream(), file);

			LeArquivoRetornoBB arquivoRetorno = new LeArquivoRetornoBB();
			Collection<DetalheLoteSegmentoT> listaSegmentoT = arquivoRetorno.leArquivoRetorno(file);

			List<Filtro> filtros = new ArrayList<>();

			filtros.add(new Filtro(Filtro.AND, "tipo", Filtro.IGUAL, "01"));
			FinTipoRecebimento tipoRecebimento = finTipoRecebimentoDao.getBean(FinTipoRecebimento.class, filtros);
			if (tipoRecebimento == null) {
				throw new Exception("Tipo de recebimento não cadastrado. Entre em contato com a Software House");
			}

			filtros = new ArrayList<>();
			filtros.add(new Filtro(Filtro.AND, "empresa", Filtro.IGUAL, FacesContextUtil.getEmpresaUsuario()));
			AdmParametro admParametro = admParametroDao.getBean(AdmParametro.class, filtros);
			if (admParametro == null) {
				throw new Exception("Parâmetros administrativos não cadastrados.\nEntre em contato com a Software House.");
			}

			FinStatusParcela statusParcela = finStatusParcelaDao.getBean(admParametro.getFinParcelaQuitado(), FinStatusParcela.class);
			if (statusParcela == null) {
				throw new Exception("O status de parcela 'QUITADO' não está cadastrado.\nEntre em contato com a Software House.");
			}
			
            DetalheLoteSegmentoT segmentoT;
            DetalheLoteSegmentoU segmentoU;

            resultadoProcessamento = new ArrayList<>();
            FinProcessamentoRetorno retorno;
            FinParcelaReceber parcelaReceber;
            FinParcelaRecebimento parcelaRecebimento;
            for (Iterator<DetalheLoteSegmentoT> i = listaSegmentoT.iterator(); i.hasNext();) {
                segmentoT = i.next();

                segmentoU = segmentoT.getSegmentoU();

                retorno = new FinProcessamentoRetorno();
                retorno.setNossoNumero(segmentoT.getIdentificadorTitulo());

    			filtros = new ArrayList<>();
    			filtros.add(new Filtro(Filtro.AND, "boletoNossoNumero", Filtro.IGUAL, segmentoT.getIdentificadorTitulo().trim()));

                parcelaReceber = finParcelaReceberDao.getBean(FinParcelaReceber.class, filtros);

                if (parcelaReceber == null) {
                    retorno.setResultado("Nosso Número não localizado no banco de dados.");
                } else {
                    parcelaRecebimento = new FinParcelaRecebimento();
                    parcelaRecebimento.setFinTipoRecebimento(tipoRecebimento);
                    parcelaRecebimento.setFinParcelaReceber(parcelaReceber);
                    parcelaRecebimento.setContaCaixa(parcelaReceber.getContaCaixa());
                    parcelaRecebimento.setDataRecebimento(segmentoU.getDataEfetivacaoCredito());
                    parcelaRecebimento.setValorMulta(BigDecimal.valueOf(segmentoU.getJurosMultaEncargos() / 100));
                    parcelaRecebimento.setValorDesconto(BigDecimal.valueOf(segmentoU.getValorDesconto() / 100));
                    parcelaRecebimento.setHistorico("RECEBIDO VIA EDI BANCARIO");
                    parcelaRecebimento.setValorRecebido(BigDecimal.valueOf(segmentoU.getValorLiquidoCreditado() / 100));

                    parcelaReceber.setFinStatusParcela(statusParcela);
                    parcelaReceber.getListaFinParcelaRecebimento().add(parcelaRecebimento);

                    finParcelaReceberDao.merge(parcelaReceber);

                    retorno.setResultado("Título processado com sucesso.");
                }
                resultadoProcessamento.add(retorno);
            }
			FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_INFO, "Arquivo processado com sucesso.!", null);
		} catch (Exception e) {
			e.printStackTrace();
			FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_ERROR, "Erro no processamento do arquivo.!", e.getMessage());
		}
	}

	public List<FinProcessamentoRetorno> getResultadoProcessamento() {
		return resultadoProcessamento;
	}

	public void setResultadoProcessamento(List<FinProcessamentoRetorno> resultadoProcessamento) {
		this.resultadoProcessamento = resultadoProcessamento;
	}

}
