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
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.io.FileUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import com.t2tierp.controller.AbstractController;
import com.t2tierp.model.bean.cadastros.ContaCaixa;
import com.t2tierp.model.bean.financeiro.FinChequeEmitido;
import com.t2tierp.model.bean.financeiro.FinExtratoContaBanco;
import com.t2tierp.model.bean.financeiro.FinParcelaPagamento;
import com.t2tierp.model.bean.financeiro.FinParcelaRecebimento;
import com.t2tierp.model.dao.Filtro;
import com.t2tierp.model.dao.InterfaceDAO;
import com.t2tierp.util.FacesContextUtil;

@ManagedBean
@ViewScoped
public class FinExtratoContaBancoController extends AbstractController<ContaCaixa> implements Serializable {

	private static final long serialVersionUID = 1L;
	@EJB
	private InterfaceDAO<FinExtratoContaBanco> extratoContaBancoDao;
	@EJB
	private InterfaceDAO<FinParcelaPagamento> parcelaPagamentoDao;
	@EJB
	private InterfaceDAO<FinParcelaRecebimento> parcelaRecebimentoDao;
	@EJB
	private InterfaceDAO<FinChequeEmitido> chequeEmitidoDao;
	
	
	private List<FinExtratoContaBanco> extratoContaBanco;
	private Date periodo;

	@Override
	public Class<ContaCaixa> getClazz() {
		return ContaCaixa.class;
	}

	@Override
	public String getFuncaoBase() {
		return "FIN_EXTRATO_CONTA_BANCO";
	}
	
	@Override
	public void voltar() {
		super.voltar();
		
		extratoContaBanco = new ArrayList<>();
		periodo = null;
	}

	public void buscaDados() {
		try {
			if (mesAno() == null) {
				FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_INFO, "Necessário informar um período válido!", "");
			} else {
				List<Filtro> filtros = new ArrayList<>();
				filtros.add(new Filtro(Filtro.AND, "contaCaixa", Filtro.IGUAL, getObjeto()));
				filtros.add(new Filtro(Filtro.AND, "mesAno", Filtro.IGUAL, mesAno()));

				extratoContaBanco = extratoContaBancoDao.getBeans(FinExtratoContaBanco.class, filtros);
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_ERROR, "Erro ao buscar os dados!", e.getMessage());
		}
	}

    public void salvaExtrato() {
        try {
        	if (mesAno() == null) {
                throw new Exception("Período inválido!");
            }
            
            if (extratoContaBanco.isEmpty()) {
                throw new Exception("Nenhum registro para salvar!");
            }
            
            for (FinExtratoContaBanco e : extratoContaBanco) {
                e.setMesAno(mesAno());
                e.setMes(mesAno().substring(0, 2));
                e.setAno(mesAno().substring(3, 7));
            	
            	extratoContaBancoDao.merge(e);
            }
            FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_INFO, "Extrato Salvo com sucesso!", "");
        } catch(Exception e) {
        	e.printStackTrace();
        	FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_ERROR, "Erro ao salvar o extrato!", e.getMessage());
        }
    }	

    public void conciliaLancamentos() {
        try {
            if (extratoContaBanco.isEmpty()) {
                throw new Exception("Nenhum lançamento para conciliar!");
            }
            
            for (FinExtratoContaBanco e : extratoContaBanco) {
                if (!e.getHistorico().contains("Cheque")) {
    				List<Filtro> filtros = new ArrayList<>();
    				filtros.add(new Filtro(Filtro.AND, "contaCaixa", Filtro.IGUAL, getObjeto()));
    				
                	if (e.getValor().compareTo(BigDecimal.ZERO) < 0) {
        				filtros.add(new Filtro(Filtro.AND, "dataPagamento", Filtro.IGUAL, e.getDataMovimento()));
        				filtros.add(new Filtro(Filtro.AND, "valorPago", Filtro.IGUAL, e.getValor().negate()));
        				if (parcelaPagamentoDao.getBeans(FinParcelaPagamento.class, filtros).isEmpty()) {
        					e.setConciliado("N");
                        } else {
                            e.setConciliado("S");
                        }
                    } else {
        				filtros.add(new Filtro(Filtro.AND, "dataRecebimento", Filtro.IGUAL, e.getDataMovimento()));
        				filtros.add(new Filtro(Filtro.AND, "valorRecebido", Filtro.IGUAL, e.getValor()));
        				if (parcelaRecebimentoDao.getBeans(FinParcelaRecebimento.class, filtros).isEmpty()) {
        					e.setConciliado("N");
                        } else {
                            e.setConciliado("S");
                        }
                    }
                }
            }
            FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_INFO, "Conciliação realizada!", "");
        } catch(Exception e) {
        	e.printStackTrace();
        	FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_ERROR, "Erro ao conciliar lançamentos!", e.getMessage());
        }
    }	

    public void conciliaCheques() {
        try {
            if (extratoContaBanco.isEmpty()) {
                throw new Exception("Nenhum lançamento para conciliar!");
            }
            
            for (FinExtratoContaBanco e : extratoContaBanco) {
                if (e.getHistorico().contains("Cheque")) {
    				List<Filtro> filtros = new ArrayList<>();
    				filtros.add(new Filtro(Filtro.AND, "cheque.numero", Filtro.IGUAL, Integer.valueOf(e.getDocumento())));
    				filtros.add(new Filtro(Filtro.AND, "cheque.talonarioCheque.contaCaixa", Filtro.IGUAL, getObjeto()));
    				
    				FinChequeEmitido chequeEmitido = chequeEmitidoDao.getBean(FinChequeEmitido.class, filtros);
    				
                    if (chequeEmitido != null) {
                        e.setConciliado("S");
                        if (chequeEmitido.getValor().compareTo(e.getValor().negate()) != 0){
                            e.setObservacao("VALOR DO CHEQUE NO EXTRATO DIFERE DO VALOR ARMAZENADO NO BANCO DE DADOS - CHEQUE NAO CONCILIADO");
                            e.setConciliado("N");
                        }
                    } else {
                        e.setConciliado("N");
                    }
                }
            }
            FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_INFO, "Conciliação realizada!", "");
        } catch(Exception e) {
        	e.printStackTrace();
        	FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_ERROR, "Erro ao conciliar cheque!", e.getMessage());
        }
    }	
    
	public void importaOFX(FileUploadEvent event) {
		try {
			UploadedFile arquivo = event.getFile();
			File file = File.createTempFile("extrato", ".ofx");
			FileUtils.copyInputStreamToFile(arquivo.getInputstream(), file);

            ImportaOFX importa = new ImportaOFX();
            extratoContaBanco = importa.importaArquivoOFX(file);
            //seta os dados do extrato
            for (FinExtratoContaBanco e : extratoContaBanco) {
                e.setContaCaixa(getObjeto());
            }
			FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_INFO, "Extrato importado com sucesso.!", null);
		} catch (Exception e) {
			e.printStackTrace();
			FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_ERROR, "Erro ao importar o extrato.!", e.getMessage());
		}
	}    
    
    private String mesAno() {
        try {
            if (periodo == null) {
                return null;
            }
            Calendar dataValida = Calendar.getInstance();
            dataValida.setLenient(false);
            dataValida.setTime(periodo);

            SimpleDateFormat dateFormat = new SimpleDateFormat("MM/yyyy");
            return dateFormat.format(dataValida.getTime());
        } catch (Exception e) {
            return null;
        }
    }

    public String getTotais() {
        if (extratoContaBanco != null) {
        	BigDecimal creditos = BigDecimal.ZERO;
            BigDecimal debitos = BigDecimal.ZERO;
            BigDecimal saldo = BigDecimal.ZERO;
            for (FinExtratoContaBanco e : extratoContaBanco) {
                if (e.getValor().compareTo(BigDecimal.ZERO) == -1) {
                    debitos = debitos.add(e.getValor());
                } else {
                    creditos = creditos.add(e.getValor());
                }
            }
            saldo = creditos.add(debitos);
            
            DecimalFormat decimalFormat = new DecimalFormat("#,###,##0.00");
            String texto = "|   Créditos: R$ " + decimalFormat.format(creditos);
            texto += "|   Débitos: R$ " + decimalFormat.format(debitos);
            texto += "|   Saldo: R$ " + decimalFormat.format(saldo) + "   |";
        	return texto;
        }
        return "";
    }    
    
	public List<FinExtratoContaBanco> getExtratoContaBanco() {
		return extratoContaBanco;
	}

	public void setExtratoContaBanco(List<FinExtratoContaBanco> extratoContaBanco) {
		this.extratoContaBanco = extratoContaBanco;
	}

	public Date getPeriodo() {
		return periodo;
	}

	public void setPeriodo(Date periodo) {
		this.periodo = periodo;
	}

}
