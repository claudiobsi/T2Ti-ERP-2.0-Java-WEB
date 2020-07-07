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
package com.t2tierp.controller.nfe;

import java.io.StringWriter;
import java.math.BigDecimal;
import java.security.KeyStore;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Set;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBElement;
import javax.xml.bind.Marshaller;

import br.inf.portalfiscal.nfe.envinfe.ObjectFactory;
import br.inf.portalfiscal.nfe.envinfe.TEnderEmi;
import br.inf.portalfiscal.nfe.envinfe.TEndereco;
import br.inf.portalfiscal.nfe.envinfe.TEnviNFe;
import br.inf.portalfiscal.nfe.envinfe.TIpi;
import br.inf.portalfiscal.nfe.envinfe.TLocal;
import br.inf.portalfiscal.nfe.envinfe.TNFe;
import br.inf.portalfiscal.nfe.envinfe.TUf;
import br.inf.portalfiscal.nfe.envinfe.TUfEmi;
import br.inf.portalfiscal.nfe.envinfe.TVeiculo;

import com.t2tierp.model.bean.cadastros.Empresa;
import com.t2tierp.model.bean.cadastros.EmpresaEndereco;
import com.t2tierp.model.bean.nfe.NfeCabecalho;
import com.t2tierp.model.bean.nfe.NfeCteReferenciado;
import com.t2tierp.model.bean.nfe.NfeCupomFiscalReferenciado;
import com.t2tierp.model.bean.nfe.NfeDestinatario;
import com.t2tierp.model.bean.nfe.NfeDetalhe;
import com.t2tierp.model.bean.nfe.NfeDuplicata;
import com.t2tierp.model.bean.nfe.NfeFatura;
import com.t2tierp.model.bean.nfe.NfeLocalEntrega;
import com.t2tierp.model.bean.nfe.NfeLocalRetirada;
import com.t2tierp.model.bean.nfe.NfeNfReferenciada;
import com.t2tierp.model.bean.nfe.NfeProdRuralReferenciada;
import com.t2tierp.model.bean.nfe.NfeReferenciada;
import com.t2tierp.model.bean.nfe.NfeTransporte;
import com.t2tierp.model.bean.nfe.NfeTransporteReboque;
import com.t2tierp.model.bean.nfe.NfeTransporteVolume;
import com.t2tierp.model.bean.nfe.NfeTransporteVolumeLacre;
import com.t2tierp.util.Biblioteca;

public class GeraXMLEnvio {

    public String gerarXmlEnvio(Empresa empresa, NfeCabecalho nfeCabecalho, String alias, KeyStore ks, char[] senha) throws Exception {
        NfeDestinatario destinatario = nfeCabecalho.getDestinatario();
        List<NfeDetalhe> listaNfeDetalhe = nfeCabecalho.getListaNfeDetalhe();
        Set<NfeReferenciada> listaNfeReferenciada = nfeCabecalho.getListaNfeReferenciada();
        Set<NfeNfReferenciada> listaNf1Referenciada = nfeCabecalho.getListaNfReferenciada();
        Set<NfeCteReferenciado> listaCteReferenciado = nfeCabecalho.getListaCteReferenciado();
        Set<NfeProdRuralReferenciada> listaProdRuralReferenciada = nfeCabecalho.getListaProdRuralReferenciada();
        Set<NfeCupomFiscalReferenciado> listaCupomFiscalReferenciado = nfeCabecalho.getListaCupomFiscalReferenciado();
        NfeLocalEntrega localEntrega = nfeCabecalho.getLocalEntrega();
        NfeLocalRetirada localRetirada = nfeCabecalho.getLocalRetirada();
        NfeTransporte transporte = nfeCabecalho.getTransporte();
        Set<NfeTransporteReboque> listaTransporteReboque = nfeCabecalho.getTransporte().getListaTransporteReboque();
        Set<NfeTransporteVolume> listaTransporteVolume = nfeCabecalho.getTransporte().getListaTransporteVolume();
        NfeFatura fatura = nfeCabecalho.getFatura();
        Set<NfeDuplicata> listaDuplicata = nfeCabecalho.getListaDuplicata();

        EmpresaEndereco empresaEndereco = new EmpresaEndereco();
        for (EmpresaEndereco e : empresa.getListaEndereco()) {
        	if (e.getPrincipal().equals("S")) {
        		empresaEndereco = e;
        		break;
        	}
        }
        
        SimpleDateFormat formatoDataHora = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX");
        DecimalFormatSymbols simboloDecimal = DecimalFormatSymbols.getInstance();
        simboloDecimal.setDecimalSeparator('.');
        DecimalFormat formatoQuantidade = new DecimalFormat("0.0000", simboloDecimal);
        DecimalFormat formatoValor = new DecimalFormat("0.00", simboloDecimal);

        ObjectFactory factory = new ObjectFactory();

        TNFe nfe = factory.createTNFe();

        TNFe.InfNFe infNfe = new TNFe.InfNFe();
        infNfe.setId("NFe" + nfeCabecalho.getChaveAcesso() + nfeCabecalho.getDigitoChaveAcesso());
        infNfe.setVersao("3.10");
        nfe.setInfNFe(infNfe);

        TNFe.InfNFe.Ide ide = new TNFe.InfNFe.Ide();
        nfe.getInfNFe().setIde(ide);

        //cabecalho
        ide.setCUF(empresa.getCodigoIbgeUf().toString());
        ide.setCNF(nfeCabecalho.getCodigoNumerico());
        ide.setNatOp(nfeCabecalho.getNaturezaOperacao());
        ide.setIndPag(String.valueOf(nfeCabecalho.getIndicadorFormaPagamento()));
        ide.setMod(nfeCabecalho.getCodigoModelo());
        ide.setSerie(Integer.valueOf(nfeCabecalho.getSerie()).toString());
        ide.setNNF(Integer.valueOf(nfeCabecalho.getNumero()).toString());
        ide.setDhEmi(formatoDataHora.format(nfeCabecalho.getDataHoraEmissao()));
        ide.setDhSaiEnt(formatoDataHora.format(nfeCabecalho.getDataHoraEntradaSaida()));
        ide.setTpNF(String.valueOf(nfeCabecalho.getTipoOperacao()));
        ide.setCMunFG(nfeCabecalho.getCodigoMunicipio().toString());
        ide.setTpImp(String.valueOf(nfeCabecalho.getFormatoImpressaoDanfe()));
        ide.setTpEmis(String.valueOf(nfeCabecalho.getTipoEmissao()));
        ide.setVerProc(nfeCabecalho.getVersaoProcessoEmissao());
        ide.setTpAmb(String.valueOf(nfeCabecalho.getAmbiente()));
        ide.setFinNFe(String.valueOf(nfeCabecalho.getFinalidadeEmissao()));
        ide.setProcEmi(String.valueOf(nfeCabecalho.getProcessoEmissao()));
        ide.setCDV(nfeCabecalho.getDigitoChaveAcesso());
        ide.setIdDest(String.valueOf(nfeCabecalho.getLocalDestino()));
        ide.setIndFinal(String.valueOf(nfeCabecalho.getConsumidorOperacao()));
        ide.setIndPres(String.valueOf(nfeCabecalho.getConsumidorPresenca()));

        //NFe Cabeçalho -- Totais
        TNFe.InfNFe.Total total = new TNFe.InfNFe.Total();
        nfe.getInfNFe().setTotal(total);

        TNFe.InfNFe.Total.ICMSTot icmsTot = new TNFe.InfNFe.Total.ICMSTot();
        nfe.getInfNFe().getTotal().setICMSTot(icmsTot);

        total.getICMSTot().setVBC(formatoValor.format(nfeCabecalho.getBaseCalculoIcms()));
        total.getICMSTot().setVICMS(formatoValor.format(nfeCabecalho.getValorIcms()));
        total.getICMSTot().setVBCST(formatoValor.format(nfeCabecalho.getBaseCalculoIcmsSt()));
        total.getICMSTot().setVST(formatoValor.format(nfeCabecalho.getValorIcmsSt()));
        total.getICMSTot().setVProd(formatoValor.format(nfeCabecalho.getValorTotalProdutos()));
        total.getICMSTot().setVFrete(formatoValor.format(nfeCabecalho.getValorFrete()));
        total.getICMSTot().setVSeg(formatoValor.format(nfeCabecalho.getValorSeguro()));
        total.getICMSTot().setVDesc(formatoValor.format(nfeCabecalho.getValorDesconto()));
        total.getICMSTot().setVII(formatoValor.format(nfeCabecalho.getValorImpostoImportacao()));
        total.getICMSTot().setVIPI(formatoValor.format(nfeCabecalho.getValorIpi()));
        total.getICMSTot().setVPIS(formatoValor.format(nfeCabecalho.getValorPis()));
        total.getICMSTot().setVCOFINS(formatoValor.format(nfeCabecalho.getValorCofins()));
        total.getICMSTot().setVOutro(formatoValor.format(nfeCabecalho.getValorDespesasAcessorias()));
        total.getICMSTot().setVNF(formatoValor.format(nfeCabecalho.getValorTotal()));
        total.getICMSTot().setVICMSDeson(formatoValor.format(nfeCabecalho.getValorIcmsDesonerado()));

        if (nfeCabecalho.getValorServicos().compareTo(BigDecimal.ZERO) > 0) {
            TNFe.InfNFe.Total.ISSQNtot issqnTot = new TNFe.InfNFe.Total.ISSQNtot();
            nfe.getInfNFe().getTotal().setISSQNtot(issqnTot);

            total.getISSQNtot().setVServ(formatoValor.format(nfeCabecalho.getValorServicos()));
            total.getISSQNtot().setVBC(formatoValor.format(nfeCabecalho.getBaseCalculoIssqn()));
            total.getISSQNtot().setVISS(formatoValor.format(nfeCabecalho.getValorIssqn()));
            total.getISSQNtot().setVPIS(formatoValor.format(nfeCabecalho.getValorPisIssqn()));
            total.getISSQNtot().setVCOFINS(formatoValor.format(nfeCabecalho.getValorCofinsIssqn()));
        }

        if (nfeCabecalho.getValorRetidoPis().compareTo(BigDecimal.ZERO) > 0) {
            TNFe.InfNFe.Total.RetTrib retTrib = new TNFe.InfNFe.Total.RetTrib();
            nfe.getInfNFe().getTotal().setRetTrib(retTrib);

            total.getRetTrib().setVRetPIS(formatoValor.format(nfeCabecalho.getValorRetidoPis()));
            total.getRetTrib().setVRetCOFINS(formatoValor.format(nfeCabecalho.getValorRetidoCofins()));
            total.getRetTrib().setVRetCSLL(formatoValor.format(nfeCabecalho.getValorRetidoCsll()));
            total.getRetTrib().setVBCIRRF(formatoValor.format(nfeCabecalho.getBaseCalculoIrrf()));
            total.getRetTrib().setVIRRF(formatoValor.format(nfeCabecalho.getValorRetidoIrrf()));
            total.getRetTrib().setVBCRetPrev(formatoValor.format(nfeCabecalho.getBaseCalculoPrevidencia()));
            total.getRetTrib().setVRetPrev(formatoValor.format(nfeCabecalho.getValorRetidoPrevidencia()));
        }

        //NFe Cabeçalho -- Informações Adicionais
        if (nfeCabecalho.getComexUfEmbarque() != null) {
            if (!nfeCabecalho.getComexUfEmbarque().trim().equals("")) {
                TNFe.InfNFe.Exporta exporta = new TNFe.InfNFe.Exporta();
                nfe.getInfNFe().setExporta(exporta);

                exporta.setUFSaidaPais(TUfEmi.valueOf(nfeCabecalho.getComexUfEmbarque()));
                exporta.setXLocDespacho(nfeCabecalho.getComexLocalEmbarque());
            }
        }

        if (nfeCabecalho.getCompraNotaEmpenho() != null) {
            if (!nfeCabecalho.getCompraNotaEmpenho().trim().equals("")) {
                TNFe.InfNFe.Compra compra = new TNFe.InfNFe.Compra();
                nfe.getInfNFe().setCompra(compra);

                compra.setXNEmp(nfeCabecalho.getCompraNotaEmpenho());
                compra.setXPed(nfeCabecalho.getCompraPedido());
                compra.setXCont(nfeCabecalho.getCompraContrato());
            }
        }

        TNFe.InfNFe.InfAdic infAdic = new TNFe.InfNFe.InfAdic();
        nfe.getInfNFe().setInfAdic(infAdic);

        infAdic.setInfAdFisco(nfeCabecalho.getInformacoesAddFisco() == null ? null : nfeCabecalho.getInformacoesAddFisco().equals("") ? null : nfeCabecalho.getInformacoesAddFisco());
        infAdic.setInfCpl(nfeCabecalho.getInformacoesAddContribuinte() == null ? null : nfeCabecalho.getInformacoesAddContribuinte().equals("") ? null : nfeCabecalho.getInformacoesAddContribuinte());

        //emitente
        TNFe.InfNFe.Emit emit = new TNFe.InfNFe.Emit();
        nfe.getInfNFe().setEmit(emit);
        TEnderEmi enderecoEmi = new TEnderEmi();
        nfe.getInfNFe().getEmit().setEnderEmit(enderecoEmi);

        emit.setCNPJ(empresa.getCnpj());
        emit.setXNome(empresa.getRazaoSocial());
        emit.setXFant(empresa.getNomeFantasia());
        emit.getEnderEmit().setXLgr(empresaEndereco.getLogradouro());
        emit.getEnderEmit().setNro(empresaEndereco.getNumero());
        emit.getEnderEmit().setXCpl(empresaEndereco.getComplemento());
        emit.getEnderEmit().setXBairro(empresaEndereco.getBairro());
        emit.getEnderEmit().setCMun(empresa.getCodigoIbgeCidade().toString());
        emit.getEnderEmit().setXMun(empresaEndereco.getCidade());
        emit.getEnderEmit().setUF(TUfEmi.fromValue(empresaEndereco.getUf()));
        emit.getEnderEmit().setCEP(empresaEndereco.getCep());
        emit.getEnderEmit().setCPais("1058");
        emit.getEnderEmit().setXPais("BRASIL");
        emit.getEnderEmit().setFone(empresaEndereco.getFone());
        emit.setIE(empresa.getInscricaoEstadual());
        emit.setIM(empresa.getInscricaoMunicipal());
        emit.setCRT(empresa.getCrt().toString());
        emit.setCNAE(empresa.getCodigoCnaePrincipal());

        //destinatario
        TNFe.InfNFe.Dest dest = new TNFe.InfNFe.Dest();
        nfe.getInfNFe().setDest(dest);
        TEndereco enderecoDest = new TEndereco();
        nfe.getInfNFe().getDest().setEnderDest(enderecoDest);

        if (destinatario.getCpfCnpj().length() == 14) {
            dest.setCNPJ(destinatario.getCpfCnpj());
        } else {
            dest.setCPF(destinatario.getCpfCnpj());
        }
        if (nfeCabecalho.getAmbiente().intValue() == 2) {
            dest.setXNome("NF-E EMITIDA EM AMBIENTE DE HOMOLOGACAO - SEM VALOR FISCAL");
        } else {
            dest.setXNome(destinatario.getNome());
        }
        dest.getEnderDest().setXLgr(destinatario.getLogradouro());
        dest.getEnderDest().setNro(destinatario.getNumero());
        dest.getEnderDest().setXCpl(destinatario.getComplemento() == null ? null : destinatario.getComplemento().equals("") ? null : destinatario.getComplemento());
        dest.getEnderDest().setXBairro(destinatario.getBairro());
        dest.getEnderDest().setCMun(destinatario.getCodigoMunicipio().toString());
        dest.getEnderDest().setXMun(destinatario.getNomeMunicipio());
        dest.getEnderDest().setUF(TUf.fromValue(destinatario.getUf()));
        dest.getEnderDest().setCEP(destinatario.getCep());
        dest.getEnderDest().setCPais(destinatario.getCodigoPais().toString());
        dest.getEnderDest().setXPais(destinatario.getNomePais());
        dest.getEnderDest().setFone(destinatario.getTelefone() == null ? null : destinatario.getTelefone().equals("") ? null : destinatario.getTelefone());
        if (destinatario.getCpfCnpj().length() == 14) {
            if (destinatario.getInscricaoEstadual().equals("")) {
                dest.setIndIEDest("2");
                //dest.setIE("ISENTO");
            } else {
                dest.setIndIEDest("1");
                dest.setIE(destinatario.getInscricaoEstadual());
            }

        } else {
            dest.setIndIEDest("2");
            //dest.setIE("ISENTO");
        }
        dest.setISUF(destinatario.getSuframa() == null ? null : String.valueOf(destinatario.getSuframa()));
        dest.setEmail(destinatario.getEmail() == null ? null : destinatario.getEmail().equals("") ? null : destinatario.getEmail());

        //Grupo de informacao das NF/NF-e referenciadas
        //NF-e Referenciadas
        for (NfeReferenciada n : listaNfeReferenciada) {
            TNFe.InfNFe.Ide.NFref nFref = new TNFe.InfNFe.Ide.NFref();
            nFref.setRefNFe(n.getChaveAcesso());

            ide.getNFref().add(nFref);
        }
        //NF 1/1A Referenciadas
        for (NfeNfReferenciada n : listaNf1Referenciada) {
            TNFe.InfNFe.Ide.NFref nFref = new TNFe.InfNFe.Ide.NFref();
            TNFe.InfNFe.Ide.NFref.RefNF refNf = new TNFe.InfNFe.Ide.NFref.RefNF();
            nFref.setRefNF(refNf);
            refNf.setCUF(n.getCodigoUf().toString());
            refNf.setAAMM(n.getAnoMes());
            refNf.setCNPJ(n.getCnpj());
            refNf.setMod(n.getModelo());
            refNf.setSerie(n.getSerie());
            refNf.setNNF(String.valueOf(n.getNumeroNf()));

            ide.getNFref().add(nFref);
        }

        //NF de produtor rural referenciada
        for (NfeProdRuralReferenciada n : listaProdRuralReferenciada) {
            TNFe.InfNFe.Ide.NFref nFref = new TNFe.InfNFe.Ide.NFref();
            TNFe.InfNFe.Ide.NFref.RefNFP refNFP = new TNFe.InfNFe.Ide.NFref.RefNFP();
            nFref.setRefNFP(refNFP);
            refNFP.setCUF(n.getCodigoUf().toString());
            refNFP.setAAMM(n.getAnoMes());
            refNFP.setCNPJ(n.getCnpj());
            refNFP.setMod(n.getModelo());
            refNFP.setSerie(n.getSerie());
            refNFP.setNNF(String.valueOf(n.getNumeroNf()));

            ide.getNFref().add(nFref);
        }

        //CTF-e Referenciados
        for (NfeCteReferenciado n : listaCteReferenciado) {
            TNFe.InfNFe.Ide.NFref nFref = new TNFe.InfNFe.Ide.NFref();
            nFref.setRefCTe(n.getChaveAcesso());

            ide.getNFref().add(nFref);
        }

        //Cupom Fiscal Referenciado
        for (NfeCupomFiscalReferenciado n : listaCupomFiscalReferenciado) {
            TNFe.InfNFe.Ide.NFref nFref = new TNFe.InfNFe.Ide.NFref();
            TNFe.InfNFe.Ide.NFref.RefECF refECF = new TNFe.InfNFe.Ide.NFref.RefECF();
            nFref.setRefECF(refECF);

            refECF.setMod(n.getModeloDocumentoFiscal());
            refECF.setNECF(n.getNumeroOrdemEcf().toString());
            refECF.setNCOO(n.getCoo().toString());

            ide.getNFref().add(nFref);
        }

        //Local Entrega
        if (localEntrega.getLogradouro() != null) {
            if (!localEntrega.getLogradouro().equals("")) {
                TLocal entrega = new TLocal();
                infNfe.setEntrega(entrega);
                if (localEntrega.getCpfCnpj().length() == 11) {
                    entrega.setCPF(localEntrega.getCpfCnpj());
                } else {
                    entrega.setCNPJ(localEntrega.getCpfCnpj());
                }
                entrega.setXLgr(localEntrega.getLogradouro());
                entrega.setNro(localEntrega.getNumero());
                entrega.setXCpl(localEntrega.getComplemento());
                entrega.setXBairro(localEntrega.getBairro());
                entrega.setCMun(localEntrega.getCodigoMunicipio().toString());
                entrega.setXMun(localEntrega.getNomeMunicipio());
                entrega.setUF(TUf.valueOf(localEntrega.getUf()));
            }
        }

        //Local Retirada
        if (localRetirada.getLogradouro() != null) {
            if (!localRetirada.getLogradouro().equals("")) {
                TLocal retirada = new TLocal();
                infNfe.setRetirada(retirada);
                if (localEntrega.getCpfCnpj().length() == 11) {
                    retirada.setCPF(localEntrega.getCpfCnpj());
                } else {
                    retirada.setCNPJ(localEntrega.getCpfCnpj());
                }
                retirada.setXLgr(localEntrega.getLogradouro());
                retirada.setNro(localEntrega.getNumero());
                retirada.setXCpl(localEntrega.getComplemento());
                retirada.setXBairro(localEntrega.getBairro());
                retirada.setCMun(localEntrega.getCodigoMunicipio().toString());
                retirada.setXMun(localEntrega.getNomeMunicipio());
                retirada.setUF(TUf.valueOf(localEntrega.getUf()));
            }
        }

        //Transporte
        TNFe.InfNFe.Transp transp = new TNFe.InfNFe.Transp();
        infNfe.setTransp(transp);

        transp.setModFrete(transporte.getModalidadeFrete() == null ? null : String.valueOf(transporte.getModalidadeFrete()));
        transp.setVagao(transporte.getVagao() == null ? null : transporte.getVagao().equals("") ? null : transporte.getVagao());
        transp.setBalsa(transporte.getBalsa() == null ? null : transporte.getBalsa().equals("") ? null : transporte.getBalsa());

        if (transporte.getTransportadora() != null && transporte.getTransportadora().getId() != null) {
            TNFe.InfNFe.Transp.Transporta transporta = new TNFe.InfNFe.Transp.Transporta();
            transp.setTransporta(transporta);
            if (transporte.getCpfCnpj().length() == 11) {
                transp.getTransporta().setCPF(transporte.getCpfCnpj());
            } else {
                transp.getTransporta().setCNPJ(transporte.getCpfCnpj());
            }
            transp.getTransporta().setXNome(transporte.getNome());
            transp.getTransporta().setIE(transporte.getInscricaoEstadual());
            transp.getTransporta().setXEnder(transporte.getEmpresaEndereco());
            transp.getTransporta().setXMun(transporte.getNomeMunicipio());
            transp.getTransporta().setUF(TUf.valueOf(transporte.getUf()));
        }

        if (transporte.getValorServico() != null) {
            TNFe.InfNFe.Transp.RetTransp retTransp = new TNFe.InfNFe.Transp.RetTransp();
            transp.setRetTransp(retTransp);

            transp.getRetTransp().setVServ(formatoValor.format(transporte.getValorServico()));
            transp.getRetTransp().setVBCRet(formatoValor.format(transporte.getValorBcRetencaoIcms()));
            transp.getRetTransp().setPICMSRet(formatoValor.format(transporte.getAliquotaRetencaoIcms()));
            transp.getRetTransp().setVICMSRet(formatoValor.format(transporte.getValorIcmsRetido()));
            transp.getRetTransp().setCFOP(transporte.getCfop().toString());
            transp.getRetTransp().setCMunFG(transporte.getMunicipio().toString());
        }

        if (transporte.getPlacaVeiculo() != null && !transporte.getPlacaVeiculo().equals("")) {
            TVeiculo veiculo = new TVeiculo();
            transp.setVeicTransp(veiculo);

            transp.getVeicTransp().setPlaca(transporte.getPlacaVeiculo());
            transp.getVeicTransp().setUF(TUf.valueOf(transporte.getUf()));
            transp.getVeicTransp().setRNTC(transporte.getRntcVeiculo());
        }

        for (NfeTransporteReboque n : listaTransporteReboque) {
            TVeiculo reboque = new TVeiculo();
            reboque.setPlaca(n.getPlaca());
            reboque.setUF(TUf.valueOf(n.getUf()));
            reboque.setRNTC(n.getRntc());

            transp.getReboque().add(reboque);
        }

        for (NfeTransporteVolume n : listaTransporteVolume) {
            TNFe.InfNFe.Transp.Vol volume = new TNFe.InfNFe.Transp.Vol();
            volume.setQVol(n.getQuantidade().toString());
            volume.setEsp(n.getEspecie());
            volume.setMarca(n.getMarca());
            volume.setPesoL(formatoQuantidade.format(n.getPesoLiquido()));
            volume.setPesoB(formatoQuantidade.format(n.getPesoBruto()));

            for (NfeTransporteVolumeLacre l : n.getListaTransporteVolumeLacre()) {
                TNFe.InfNFe.Transp.Vol.Lacres lacre = new TNFe.InfNFe.Transp.Vol.Lacres();
                volume.getLacres().add(lacre);

                lacre.setNLacre(l.getNumero());
            }

            transp.getVol().add(volume);
        }

        //Fatura
        if (fatura.getNumero() != null) {
            if (!fatura.getNumero().equals("")) {
                TNFe.InfNFe.Cobr cobr = new TNFe.InfNFe.Cobr();
                infNfe.setCobr(cobr);

                TNFe.InfNFe.Cobr.Fat fat = new TNFe.InfNFe.Cobr.Fat();
                cobr.setFat(fat);

                fat.setNFat(fatura.getNumero());
                fat.setVOrig(formatoValor.format(fatura.getValorOriginal()));
                fat.setVDesc(formatoValor.format(fatura.getValorDesconto()));
                fat.setVLiq(formatoValor.format(fatura.getValorLiquido()));
            }
        }

        //duplicatas
        for (NfeDuplicata n : listaDuplicata) {
            if (infNfe.getCobr() == null) {
                TNFe.InfNFe.Cobr cobr = new TNFe.InfNFe.Cobr();
                infNfe.setCobr(cobr);
            }

            TNFe.InfNFe.Cobr.Dup dup = new TNFe.InfNFe.Cobr.Dup();
            dup.setNDup(n.getNumero());
            dup.setDVenc(formatoDataHora.format(n.getDataVencimento()));
            dup.setVDup(formatoValor.format(n.getValor()));

            infNfe.getCobr().getDup().add(dup);
        }

        //detalhes
        List<TNFe.InfNFe.Det> listaDet = nfe.getInfNFe().getDet();
        TNFe.InfNFe.Det det;
        for (NfeDetalhe nfeDetalhe : listaNfeDetalhe) {

            det = new TNFe.InfNFe.Det();
            TNFe.InfNFe.Det.Prod prod = new TNFe.InfNFe.Det.Prod();
            det.setNItem(nfeDetalhe.getNumeroItem().toString());
            det.setProd(prod);

            det.getProd().setCProd(nfeDetalhe.getGtin());
            det.getProd().setCEAN(nfeDetalhe.getGtin());
            det.getProd().setXProd(nfeDetalhe.getNomeProduto());
            det.getProd().setNCM(nfeDetalhe.getNcm());
            //det.getProd().setEXTIPI(null);
            det.getProd().setCFOP(nfeDetalhe.getCfop().toString());
            det.getProd().setUCom(nfeDetalhe.getUnidadeComercial());
            det.getProd().setQCom(formatoQuantidade.format(nfeDetalhe.getQuantidadeComercial()));
            det.getProd().setVUnCom(nfeDetalhe.getValorUnitarioComercial().toPlainString());
            det.getProd().setVProd(formatoValor.format(nfeDetalhe.getValorTotal()));
            det.getProd().setCEANTrib(nfeDetalhe.getGtinUnidadeTributavel());
            det.getProd().setUTrib(nfeDetalhe.getUnidadeTributavel());
            det.getProd().setQTrib(formatoQuantidade.format(nfeDetalhe.getQuantidadeTributavel()));
            det.getProd().setVUnTrib(nfeDetalhe.getValorUnitarioTributavel().toPlainString());
            det.getProd().setVFrete(nfeDetalhe.getValorFrete().compareTo(BigDecimal.ZERO) == 0 ? null : formatoValor.format(nfeDetalhe.getValorFrete()));
            det.getProd().setVSeg(nfeDetalhe.getValorSeguro().compareTo(BigDecimal.ZERO) == 0 ? null : formatoValor.format(nfeDetalhe.getValorSeguro()));
            det.getProd().setVDesc(nfeDetalhe.getValorDesconto().compareTo(BigDecimal.ZERO) == 0 ? null : formatoValor.format(nfeDetalhe.getValorDesconto()));
            det.getProd().setVOutro(nfeDetalhe.getValorOutrasDespesas().compareTo(BigDecimal.ZERO) == 0 ? null : formatoValor.format(nfeDetalhe.getValorOutrasDespesas()));
            det.getProd().setIndTot(String.valueOf(nfeDetalhe.getEntraTotal()));
            det.setInfAdProd(nfeDetalhe.getInformacoesAdicionais());

            //Detalhes -- Impostos
            TNFe.InfNFe.Det.Imposto imposto = new TNFe.InfNFe.Det.Imposto();
            det.setImposto(imposto);

            // Se houver CFOP cadastrado na Operação Fiscal, a nota é de serviços
            if (nfeCabecalho.getTributOperacaoFiscal().getCfop() != null) {
                TNFe.InfNFe.Det.Imposto.ISSQN issqn = new TNFe.InfNFe.Det.Imposto.ISSQN();
                det.getImposto().getContent().add(factory.createTNFeInfNFeDetImpostoISSQN(issqn));

                issqn.setVBC(formatoValor.format(nfeDetalhe.getNfeDetalheImpostoIssqn().getBaseCalculoIssqn()));
                issqn.setVAliq(formatoValor.format(nfeDetalhe.getNfeDetalheImpostoIssqn().getAliquotaIssqn()));
                issqn.setVISSQN(formatoValor.format(nfeDetalhe.getNfeDetalheImpostoIssqn().getValorIssqn()));
                issqn.setCMunFG(nfeDetalhe.getNfeDetalheImpostoIssqn().getMunicipioIssqn().toString());
                issqn.setCListServ(nfeDetalhe.getNfeDetalheImpostoIssqn().getItemListaServicos().toString());
                //issqn.setCSitTrib(nfeDetalhe.getNfeDetalheImpostoIssqn().getTributacaoIssqn());
            } else {
                TNFe.InfNFe.Det.Imposto.ICMS icms = new TNFe.InfNFe.Det.Imposto.ICMS();
                det.getImposto().getContent().add(factory.createTNFeInfNFeDetImpostoICMS(icms));

                if (empresa.getCrt().equals("1")) {//Simples Nacional
                    String csosn = nfeDetalhe.getNfeDetalheImpostoIcms().getCsosn();

                    if (csosn.equals("101")) {
                        TNFe.InfNFe.Det.Imposto.ICMS.ICMSSN101 icms101 = new TNFe.InfNFe.Det.Imposto.ICMS.ICMSSN101();
                        icms.setICMSSN101(icms101);

                        icms101.setCSOSN(csosn);
                        icms101.setOrig(String.valueOf(nfeDetalhe.getNfeDetalheImpostoIcms().getOrigemMercadoria()));
                        icms101.setPCredSN(formatoValor.format(nfeDetalhe.getNfeDetalheImpostoIcms().getAliquotaCreditoIcmsSn()));
                        icms101.setVCredICMSSN(formatoValor.format(nfeDetalhe.getNfeDetalheImpostoIcms().getValorCreditoIcmsSn()));
                    }
                    if (csosn.equals("102") || csosn.equals("103") || csosn.equals("300") || csosn.equals("400")) {
                        TNFe.InfNFe.Det.Imposto.ICMS.ICMSSN102 icms102 = new TNFe.InfNFe.Det.Imposto.ICMS.ICMSSN102();
                        icms.setICMSSN102(icms102);

                        icms102.setCSOSN(csosn);
                        icms102.setOrig(String.valueOf(nfeDetalhe.getNfeDetalheImpostoIcms().getOrigemMercadoria()));
                    }
                    if (csosn.equals("201")) {
                        TNFe.InfNFe.Det.Imposto.ICMS.ICMSSN201 icms201 = new TNFe.InfNFe.Det.Imposto.ICMS.ICMSSN201();
                        icms.setICMSSN201(icms201);

                        icms201.setCSOSN(csosn);
                        icms201.setOrig(String.valueOf(nfeDetalhe.getNfeDetalheImpostoIcms().getOrigemMercadoria()));
                        icms201.setModBCST(String.valueOf(nfeDetalhe.getNfeDetalheImpostoIcms().getModalidadeBcIcmsSt()));
                        icms201.setPMVAST(formatoValor.format(nfeDetalhe.getNfeDetalheImpostoIcms().getPercentualMvaIcmsSt()));
                        icms201.setPRedBCST(formatoValor.format(nfeDetalhe.getNfeDetalheImpostoIcms().getPercentualReducaoBcIcmsSt()));
                        icms201.setVBCST(formatoValor.format(nfeDetalhe.getNfeDetalheImpostoIcms().getValorBaseCalculoIcmsSt()));
                        icms201.setPICMSST(formatoValor.format(nfeDetalhe.getNfeDetalheImpostoIcms().getAliquotaIcmsSt()));
                        icms201.setVICMSST(formatoValor.format(nfeDetalhe.getNfeDetalheImpostoIcms().getValorIcmsSt()));
                        icms201.setPCredSN(formatoValor.format(nfeDetalhe.getNfeDetalheImpostoIcms().getAliquotaCreditoIcmsSn()));
                        icms201.setVCredICMSSN(formatoValor.format(nfeDetalhe.getNfeDetalheImpostoIcms().getValorCreditoIcmsSn()));
                    }
                    if (csosn.equals("202") || csosn.equals("203")) {
                        TNFe.InfNFe.Det.Imposto.ICMS.ICMSSN202 icms202 = new TNFe.InfNFe.Det.Imposto.ICMS.ICMSSN202();
                        icms.setICMSSN202(icms202);

                        icms202.setCSOSN(csosn);
                        icms202.setOrig(String.valueOf(nfeDetalhe.getNfeDetalheImpostoIcms().getOrigemMercadoria()));
                        icms202.setModBCST(String.valueOf(nfeDetalhe.getNfeDetalheImpostoIcms().getModalidadeBcIcmsSt()));
                        icms202.setPMVAST(formatoValor.format(nfeDetalhe.getNfeDetalheImpostoIcms().getPercentualMvaIcmsSt()));
                        icms202.setPRedBCST(formatoValor.format(nfeDetalhe.getNfeDetalheImpostoIcms().getPercentualReducaoBcIcmsSt()));
                        icms202.setVBCST(formatoValor.format(nfeDetalhe.getNfeDetalheImpostoIcms().getValorBaseCalculoIcmsSt()));
                        icms202.setPICMSST(formatoValor.format(nfeDetalhe.getNfeDetalheImpostoIcms().getAliquotaIcmsSt()));
                        icms202.setVICMSST(formatoValor.format(nfeDetalhe.getNfeDetalheImpostoIcms().getValorIcmsSt()));
                    }
                    if (csosn.equals("500")) {
                        TNFe.InfNFe.Det.Imposto.ICMS.ICMSSN500 icms500 = new TNFe.InfNFe.Det.Imposto.ICMS.ICMSSN500();
                        icms.setICMSSN500(icms500);

                        icms500.setCSOSN(csosn);
                        icms500.setOrig(String.valueOf(nfeDetalhe.getNfeDetalheImpostoIcms().getOrigemMercadoria()));
                        icms500.setVBCSTRet(formatoValor.format(nfeDetalhe.getNfeDetalheImpostoIcms().getValorBcIcmsStRetido()));
                        icms500.setVICMSSTRet(formatoValor.format(nfeDetalhe.getNfeDetalheImpostoIcms().getValorIcmsStRetido()));
                    }
                    if (csosn.equals("900")) {
                        TNFe.InfNFe.Det.Imposto.ICMS.ICMSSN900 icms900 = new TNFe.InfNFe.Det.Imposto.ICMS.ICMSSN900();
                        icms.setICMSSN900(icms900);

                        icms900.setCSOSN(csosn);
                        icms900.setOrig(String.valueOf(nfeDetalhe.getNfeDetalheImpostoIcms().getOrigemMercadoria()));
                        icms900.setModBC(String.valueOf(nfeDetalhe.getNfeDetalheImpostoIcms().getModalidadeBcIcms()));
                        icms900.setVBC(formatoValor.format(nfeDetalhe.getNfeDetalheImpostoIcms().getBaseCalculoIcms()));
                        icms900.setPRedBC(formatoValor.format(nfeDetalhe.getNfeDetalheImpostoIcms().getTaxaReducaoBcIcms()));
                        icms900.setPICMS(formatoValor.format(nfeDetalhe.getNfeDetalheImpostoIcms().getAliquotaIcms()));
                        icms900.setVICMS(formatoValor.format(nfeDetalhe.getNfeDetalheImpostoIcms().getValorIcms()));
                        icms900.setModBCST(String.valueOf(nfeDetalhe.getNfeDetalheImpostoIcms().getModalidadeBcIcmsSt()));
                        icms900.setPMVAST(formatoValor.format(nfeDetalhe.getNfeDetalheImpostoIcms().getPercentualMvaIcmsSt()));
                        icms900.setPRedBCST(formatoValor.format(nfeDetalhe.getNfeDetalheImpostoIcms().getPercentualReducaoBcIcmsSt()));
                        icms900.setVBCST(formatoValor.format(nfeDetalhe.getNfeDetalheImpostoIcms().getValorBaseCalculoIcmsSt()));
                        icms900.setPICMSST(formatoValor.format(nfeDetalhe.getNfeDetalheImpostoIcms().getAliquotaIcmsSt()));
                        icms900.setVICMSST(formatoValor.format(nfeDetalhe.getNfeDetalheImpostoIcms().getValorIcmsSt()));
                        icms900.setPCredSN(formatoValor.format(nfeDetalhe.getNfeDetalheImpostoIcms().getAliquotaCreditoIcmsSn()));
                        icms900.setVCredICMSSN(formatoValor.format(nfeDetalhe.getNfeDetalheImpostoIcms().getValorCreditoIcmsSn()));
                    }
                } else {
                    String cst = nfeDetalhe.getNfeDetalheImpostoIcms().getCstIcms();

                    if (cst.equals("00")) {
                        TNFe.InfNFe.Det.Imposto.ICMS.ICMS00 icms00 = new TNFe.InfNFe.Det.Imposto.ICMS.ICMS00();
                        icms.setICMS00(icms00);

                        icms00.setCST(nfeDetalhe.getNfeDetalheImpostoIcms().getCstIcms());
                        icms00.setOrig(String.valueOf(nfeDetalhe.getNfeDetalheImpostoIcms().getOrigemMercadoria()));
                        icms00.setModBC(String.valueOf(nfeDetalhe.getNfeDetalheImpostoIcms().getModalidadeBcIcms()));
                        icms00.setVBC(formatoValor.format(nfeDetalhe.getNfeDetalheImpostoIcms().getBaseCalculoIcms()));
                        icms00.setPICMS(formatoValor.format(nfeDetalhe.getNfeDetalheImpostoIcms().getAliquotaIcms()));
                        icms00.setVICMS(formatoValor.format(nfeDetalhe.getNfeDetalheImpostoIcms().getValorIcms()));
                    }
                    if (cst.equals("10")) {
                        TNFe.InfNFe.Det.Imposto.ICMS.ICMS10 icms10 = new TNFe.InfNFe.Det.Imposto.ICMS.ICMS10();
                        icms.setICMS10(icms10);

                        icms10.setCST(nfeDetalhe.getNfeDetalheImpostoIcms().getCstIcms());
                        icms10.setOrig(String.valueOf(nfeDetalhe.getNfeDetalheImpostoIcms().getOrigemMercadoria()));
                        icms10.setModBC(String.valueOf(nfeDetalhe.getNfeDetalheImpostoIcms().getModalidadeBcIcms()));
                        icms10.setVBC(formatoValor.format(nfeDetalhe.getNfeDetalheImpostoIcms().getBaseCalculoIcms()));
                        icms10.setPICMS(formatoValor.format(nfeDetalhe.getNfeDetalheImpostoIcms().getAliquotaIcms()));
                        icms10.setVICMS(formatoValor.format(nfeDetalhe.getNfeDetalheImpostoIcms().getValorIcms()));
                        icms10.setModBCST(String.valueOf(nfeDetalhe.getNfeDetalheImpostoIcms().getModalidadeBcIcmsSt()));
                        icms10.setPMVAST(formatoValor.format(nfeDetalhe.getNfeDetalheImpostoIcms().getPercentualMvaIcmsSt()));
                        icms10.setPRedBCST(formatoValor.format(nfeDetalhe.getNfeDetalheImpostoIcms().getPercentualReducaoBcIcmsSt()));
                        icms10.setVBCST(formatoValor.format(nfeDetalhe.getNfeDetalheImpostoIcms().getValorBaseCalculoIcmsSt()));
                        icms10.setPICMSST(formatoValor.format(nfeDetalhe.getNfeDetalheImpostoIcms().getAliquotaIcmsSt()));
                        icms10.setVICMSST(formatoValor.format(nfeDetalhe.getNfeDetalheImpostoIcms().getValorIcmsSt()));
                    }
                    if (cst.equals("20")) {
                        TNFe.InfNFe.Det.Imposto.ICMS.ICMS20 icms20 = new TNFe.InfNFe.Det.Imposto.ICMS.ICMS20();
                        icms.setICMS20(icms20);

                        icms20.setCST(nfeDetalhe.getNfeDetalheImpostoIcms().getCstIcms());
                        icms20.setOrig(String.valueOf(nfeDetalhe.getNfeDetalheImpostoIcms().getOrigemMercadoria()));
                        icms20.setPRedBC(formatoValor.format(nfeDetalhe.getNfeDetalheImpostoIcms().getTaxaReducaoBcIcms()));
                        icms20.setModBC(String.valueOf(nfeDetalhe.getNfeDetalheImpostoIcms().getModalidadeBcIcms()));
                        icms20.setVBC(formatoValor.format(nfeDetalhe.getNfeDetalheImpostoIcms().getBaseCalculoIcms()));
                        icms20.setPICMS(formatoValor.format(nfeDetalhe.getNfeDetalheImpostoIcms().getAliquotaIcms()));
                        icms20.setVICMS(formatoValor.format(nfeDetalhe.getNfeDetalheImpostoIcms().getValorIcms()));
                    }
                    if (cst.equals("30")) {
                        TNFe.InfNFe.Det.Imposto.ICMS.ICMS30 icms30 = new TNFe.InfNFe.Det.Imposto.ICMS.ICMS30();
                        icms.setICMS30(icms30);

                        icms30.setCST(nfeDetalhe.getNfeDetalheImpostoIcms().getCstIcms());
                        icms30.setOrig(String.valueOf(nfeDetalhe.getNfeDetalheImpostoIcms().getOrigemMercadoria()));
                        icms30.setModBCST(String.valueOf(nfeDetalhe.getNfeDetalheImpostoIcms().getModalidadeBcIcmsSt()));
                        icms30.setPMVAST(formatoValor.format(nfeDetalhe.getNfeDetalheImpostoIcms().getPercentualMvaIcmsSt()));
                        icms30.setPRedBCST(formatoValor.format(nfeDetalhe.getNfeDetalheImpostoIcms().getPercentualReducaoBcIcmsSt()));
                        icms30.setVBCST(formatoValor.format(nfeDetalhe.getNfeDetalheImpostoIcms().getValorBaseCalculoIcmsSt()));
                        icms30.setPICMSST(formatoValor.format(nfeDetalhe.getNfeDetalheImpostoIcms().getAliquotaIcmsSt()));
                        icms30.setVICMSST(formatoValor.format(nfeDetalhe.getNfeDetalheImpostoIcms().getValorIcmsSt()));
                    }
                    if (cst.equals("40") || cst.equals("41") || cst.equals("50")) {
                        TNFe.InfNFe.Det.Imposto.ICMS.ICMS40 icms40 = new TNFe.InfNFe.Det.Imposto.ICMS.ICMS40();
                        icms.setICMS40(icms40);

                        icms40.setCST(nfeDetalhe.getNfeDetalheImpostoIcms().getCstIcms());
                        icms40.setOrig(String.valueOf(nfeDetalhe.getNfeDetalheImpostoIcms().getOrigemMercadoria()));
                        icms40.setMotDesICMS(String.valueOf(nfeDetalhe.getNfeDetalheImpostoIcms().getMotivoDesoneracaoIcms()));
                    }
                    if (cst.equals("51")) {
                        TNFe.InfNFe.Det.Imposto.ICMS.ICMS51 icms51 = new TNFe.InfNFe.Det.Imposto.ICMS.ICMS51();
                        icms.setICMS51(icms51);

                        icms51.setCST(nfeDetalhe.getNfeDetalheImpostoIcms().getCstIcms());
                        icms51.setOrig(String.valueOf(nfeDetalhe.getNfeDetalheImpostoIcms().getOrigemMercadoria()));
                        icms51.setPRedBC(formatoValor.format(nfeDetalhe.getNfeDetalheImpostoIcms().getTaxaReducaoBcIcms()));
                        icms51.setModBC(String.valueOf(nfeDetalhe.getNfeDetalheImpostoIcms().getModalidadeBcIcms()));
                        icms51.setVBC(formatoValor.format(nfeDetalhe.getNfeDetalheImpostoIcms().getBaseCalculoIcms()));
                        icms51.setPICMS(formatoValor.format(nfeDetalhe.getNfeDetalheImpostoIcms().getAliquotaIcms()));
                        icms51.setVICMS(formatoValor.format(nfeDetalhe.getNfeDetalheImpostoIcms().getValorIcms()));
                    }
                    if (cst.equals("60")) {
                        TNFe.InfNFe.Det.Imposto.ICMS.ICMS60 icms60 = new TNFe.InfNFe.Det.Imposto.ICMS.ICMS60();
                        icms.setICMS60(icms60);

                        icms60.setCST(cst);
                        icms60.setOrig(String.valueOf(nfeDetalhe.getNfeDetalheImpostoIcms().getOrigemMercadoria()));
                        icms60.setVBCSTRet(formatoValor.format(nfeDetalhe.getNfeDetalheImpostoIcms().getValorBcIcmsStRetido()));
                        icms60.setVICMSSTRet(formatoValor.format(nfeDetalhe.getNfeDetalheImpostoIcms().getValorIcmsStRetido()));
                    }
                    if (cst.equals("70")) {
                        TNFe.InfNFe.Det.Imposto.ICMS.ICMS70 icms70 = new TNFe.InfNFe.Det.Imposto.ICMS.ICMS70();
                        icms.setICMS70(icms70);

                        icms70.setCST(nfeDetalhe.getNfeDetalheImpostoIcms().getCstIcms());
                        icms70.setOrig(String.valueOf(nfeDetalhe.getNfeDetalheImpostoIcms().getOrigemMercadoria()));
                        icms70.setModBC(String.valueOf(nfeDetalhe.getNfeDetalheImpostoIcms().getModalidadeBcIcms()));
                        icms70.setPRedBC(formatoValor.format(nfeDetalhe.getNfeDetalheImpostoIcms().getTaxaReducaoBcIcms()));
                        icms70.setVBC(formatoValor.format(nfeDetalhe.getNfeDetalheImpostoIcms().getBaseCalculoIcms()));
                        icms70.setPICMS(formatoValor.format(nfeDetalhe.getNfeDetalheImpostoIcms().getAliquotaIcms()));
                        icms70.setVICMS(formatoValor.format(nfeDetalhe.getNfeDetalheImpostoIcms().getValorIcms()));
                        icms70.setModBCST(String.valueOf(nfeDetalhe.getNfeDetalheImpostoIcms().getModalidadeBcIcmsSt()));
                        icms70.setPMVAST(formatoValor.format(nfeDetalhe.getNfeDetalheImpostoIcms().getPercentualMvaIcmsSt()));
                        icms70.setPRedBCST(formatoValor.format(nfeDetalhe.getNfeDetalheImpostoIcms().getPercentualReducaoBcIcmsSt()));
                        icms70.setVBCST(formatoValor.format(nfeDetalhe.getNfeDetalheImpostoIcms().getValorBaseCalculoIcmsSt()));
                        icms70.setPICMSST(formatoValor.format(nfeDetalhe.getNfeDetalheImpostoIcms().getAliquotaIcmsSt()));
                        icms70.setVICMSST(formatoValor.format(nfeDetalhe.getNfeDetalheImpostoIcms().getValorIcmsSt()));
                    }
                    if (cst.equals("90")) {
                        TNFe.InfNFe.Det.Imposto.ICMS.ICMS90 icms90 = new TNFe.InfNFe.Det.Imposto.ICMS.ICMS90();
                        icms.setICMS90(icms90);

                        icms90.setCST(nfeDetalhe.getNfeDetalheImpostoIcms().getCstIcms());
                        icms90.setOrig(String.valueOf(nfeDetalhe.getNfeDetalheImpostoIcms().getOrigemMercadoria()));
                        icms90.setModBC(String.valueOf(nfeDetalhe.getNfeDetalheImpostoIcms().getModalidadeBcIcms()));
                        icms90.setPRedBC(formatoValor.format(nfeDetalhe.getNfeDetalheImpostoIcms().getTaxaReducaoBcIcms()));
                        icms90.setVBC(formatoValor.format(nfeDetalhe.getNfeDetalheImpostoIcms().getBaseCalculoIcms()));
                        icms90.setPICMS(formatoValor.format(nfeDetalhe.getNfeDetalheImpostoIcms().getAliquotaIcms()));
                        icms90.setVICMS(formatoValor.format(nfeDetalhe.getNfeDetalheImpostoIcms().getValorIcms()));
                        icms90.setModBCST(String.valueOf(nfeDetalhe.getNfeDetalheImpostoIcms().getModalidadeBcIcmsSt()));
                        icms90.setPMVAST(formatoValor.format(nfeDetalhe.getNfeDetalheImpostoIcms().getPercentualMvaIcmsSt()));
                        icms90.setPRedBCST(formatoValor.format(nfeDetalhe.getNfeDetalheImpostoIcms().getPercentualReducaoBcIcmsSt()));
                        icms90.setVBCST(formatoValor.format(nfeDetalhe.getNfeDetalheImpostoIcms().getValorBaseCalculoIcmsSt()));
                        icms90.setPICMSST(formatoValor.format(nfeDetalhe.getNfeDetalheImpostoIcms().getAliquotaIcmsSt()));
                        icms90.setVICMSST(formatoValor.format(nfeDetalhe.getNfeDetalheImpostoIcms().getValorIcmsSt()));
                    }

                    //IPI
                    if (nfeDetalhe.getNfeDetalheImpostoIpi().getValorIpi() != null) {
                        if (nfeDetalhe.getNfeDetalheImpostoIpi().getValorIpi().compareTo(BigDecimal.ZERO) > 0) {
                            TIpi ipi = factory.createTIpi();
                            det.getImposto().getContent().add(factory.createTNFeInfNFeDetImpostoIPI(ipi));

                            TIpi.IPITrib ipiTrib = new TIpi.IPITrib();
                            ipi.setIPITrib(ipiTrib);

                            ipiTrib.setCST(nfeDetalhe.getNfeDetalheImpostoIpi().getCstIpi());
                            ipiTrib.setVBC(formatoValor.format(nfeDetalhe.getNfeDetalheImpostoIpi().getValorBaseCalculoIpi()));
                            ipiTrib.setPIPI(formatoValor.format(nfeDetalhe.getNfeDetalheImpostoIpi().getAliquotaIpi()));
                            ipiTrib.setVIPI(formatoValor.format(nfeDetalhe.getNfeDetalheImpostoIpi().getValorIpi()));
                        }
                    }

                    //II
                    if (nfeDetalhe.getNfeDetalheImpostoIi().getValorImpostoImportacao() != null) {
                        TNFe.InfNFe.Det.Imposto.II ii = new TNFe.InfNFe.Det.Imposto.II();
                        det.getImposto().getContent().add(factory.createTNFeInfNFeDetImpostoII(ii));

                        ii.setVBC(formatoValor.format(nfeDetalhe.getNfeDetalheImpostoIi().getValorBcIi()));
                        ii.setVDespAdu(formatoValor.format(nfeDetalhe.getNfeDetalheImpostoIi().getValorDespesasAduaneiras()));
                        ii.setVII(formatoValor.format(nfeDetalhe.getNfeDetalheImpostoIi().getValorImpostoImportacao()));
                        ii.setVIOF(formatoValor.format(nfeDetalhe.getNfeDetalheImpostoIi().getValorIof()));
                    }

                    //PIS
                    if (nfeDetalhe.getNfeDetalheImpostoPis().getValorBaseCalculoPis() != null) {
                        TNFe.InfNFe.Det.Imposto.PIS pis = new TNFe.InfNFe.Det.Imposto.PIS();
                        det.getImposto().getContent().add(factory.createTNFeInfNFeDetImpostoPIS(pis));

                        if (nfeDetalhe.getNfeDetalheImpostoPis().getCstPis().equals("01") || nfeDetalhe.getNfeDetalheImpostoPis().getCstPis().equals("02")) {
                            TNFe.InfNFe.Det.Imposto.PIS.PISAliq pisAliq = new TNFe.InfNFe.Det.Imposto.PIS.PISAliq();
                            pis.setPISAliq(pisAliq);

                            pisAliq.setCST(nfeDetalhe.getNfeDetalheImpostoPis().getCstPis());
                            pisAliq.setVBC(formatoValor.format(nfeDetalhe.getNfeDetalheImpostoPis().getValorBaseCalculoPis()));
                            if (nfeDetalhe.getNfeDetalheImpostoPis().getCstPis().equals("01")) {
                                pisAliq.setPPIS(formatoValor.format(nfeDetalhe.getNfeDetalheImpostoPis().getAliquotaPisPercentual()));
                            } else {
                                pisAliq.setPPIS(formatoValor.format(nfeDetalhe.getNfeDetalheImpostoPis().getAliquotaPisReais()));
                            }
                            pisAliq.setVPIS(formatoValor.format(nfeDetalhe.getNfeDetalheImpostoPis().getValorPis()));
                        }
                    }

                    if (nfeDetalhe.getNfeDetalheImpostoCofins().getBaseCalculoCofins() != null) {
                        TNFe.InfNFe.Det.Imposto.COFINS cofins = new TNFe.InfNFe.Det.Imposto.COFINS();
                        imposto.getContent().add(factory.createTNFeInfNFeDetImpostoCOFINS(cofins));

                        if (nfeDetalhe.getNfeDetalheImpostoCofins().getCstCofins().equals("01") || nfeDetalhe.getNfeDetalheImpostoCofins().getCstCofins().equals("02")) {
                            TNFe.InfNFe.Det.Imposto.COFINS.COFINSAliq cofinsAliq = new TNFe.InfNFe.Det.Imposto.COFINS.COFINSAliq();
                            cofins.setCOFINSAliq(cofinsAliq);

                            cofinsAliq.setCST(nfeDetalhe.getNfeDetalheImpostoCofins().getCstCofins());
                            cofinsAliq.setVBC(formatoValor.format(nfeDetalhe.getNfeDetalheImpostoCofins().getBaseCalculoCofins()));
                            if (nfeDetalhe.getNfeDetalheImpostoCofins().getCstCofins().equals("01")) {
                                cofinsAliq.setPCOFINS(formatoValor.format(nfeDetalhe.getNfeDetalheImpostoCofins().getAliquotaCofinsPercentual()));
                            } else {
                                cofinsAliq.setPCOFINS(formatoValor.format(nfeDetalhe.getNfeDetalheImpostoCofins().getAliquotaCofinsReais()));
                            }
                            cofinsAliq.setVCOFINS(formatoValor.format(nfeDetalhe.getNfeDetalheImpostoCofins().getValorCofins()));
                        }
                    }
                }
            }

            listaDet.add(det);
        }

        TEnviNFe enviNfe = new TEnviNFe();
        enviNfe.setIdLote("1");
        enviNfe.setVersao("3.10");
        enviNfe.setIndSinc("0");
        enviNfe.getNFe().add(nfe);

        JAXBContext jc = JAXBContext.newInstance("br.inf.portalfiscal.nfe.envinfe");
        Marshaller marshaller = jc.createMarshaller();

        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, false);
        JAXBElement<TEnviNFe> element = factory.createEnviNFe(enviNfe);

        StringWriter writer = new StringWriter();
        marshaller.marshal(element, writer);

        String xmlEnviNfe = writer.toString();
        xmlEnviNfe = xmlEnviNfe.replaceAll("xmlns:ns2=\"http://www.w3.org/2000/09/xmldsig#\"", "");
        xmlEnviNfe = xmlEnviNfe.replaceAll("<NFe>", "<NFe xmlns=\"http://www.portalfiscal.inf.br/nfe\">");

        return Biblioteca.assinaXML(xmlEnviNfe, alias, ks, senha, "#NFe" + nfeCabecalho.getChaveAcesso() + nfeCabecalho.getDigitoChaveAcesso(), "NFe", "infNFe", "Id");
    }

}
