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
package com.t2tierp.controller.contabilidade;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.t2tierp.controller.AbstractController;
import com.t2tierp.model.bean.contabilidade.ContabilConta;
import com.t2tierp.model.bean.contabilidade.ContabilHistorico;
import com.t2tierp.model.bean.contabilidade.ContabilLancamentoCabecalho;
import com.t2tierp.model.bean.contabilidade.ContabilLancamentoDetalhe;
import com.t2tierp.model.bean.contabilidade.ContabilLote;
import com.t2tierp.model.dao.InterfaceDAO;
import com.t2tierp.util.FacesContextUtil;

@ManagedBean
@ViewScoped
public class ContabilLancamentoCabecalhoController extends AbstractController<ContabilLancamentoCabecalho> implements Serializable {

    private static final long serialVersionUID = 1L;
    @EJB
    private InterfaceDAO<ContabilLote> contabilLoteDao;
    @EJB
    private InterfaceDAO<ContabilConta> contabilContaDao;
    @EJB
    private InterfaceDAO<ContabilHistorico> contabilHistoricoDao;
	
    private ContabilLancamentoDetalhe contabilLancamentoDetalhe;
	private ContabilLancamentoDetalhe contabilLancamentoDetalheSelecionado;

    @Override
    public Class<ContabilLancamentoCabecalho> getClazz() {
        return ContabilLancamentoCabecalho.class;
    }

    @Override
    public String getFuncaoBase() {
        return "CONTABIL_LANCAMENTO_CABECALHO";
    }

    @Override
    public void incluir() {
        super.incluir();
        getObjeto().setEmpresa(FacesContextUtil.getEmpresaUsuario());
        getObjeto().setListaContabilLancamentoDetalhe(new HashSet<ContabilLancamentoDetalhe>());
	}

    @Override
    public void salvar() {
    	try {
    		verificaLancamentos(getObjeto().getTipo(), getObjeto().getListaContabilLancamentoDetalhe());
    		super.salvar();
    	} catch(Exception e) {
    		FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_ERROR, e.getMessage(), "");
    	}
    }
    
    private void verificaLancamentos(String tipoLancamento, Set<ContabilLancamentoDetalhe> listaDetalhe) throws Exception {
        BigDecimal creditos = BigDecimal.ZERO;
        BigDecimal debitos = BigDecimal.ZERO;
        int quantidadeCreditos = 0;
        int quantidadeDebitos = 0;
        String mensagem = "";

        for (ContabilLancamentoDetalhe d : listaDetalhe) {
            if (d.getTipo().equals("C")) {
                quantidadeCreditos += 1;
                creditos = creditos.add(d.getValor());
            }
            if (d.getTipo().equals("D")) {
                quantidadeDebitos += 1;
                debitos = debitos.add(d.getValor());
            }
        }

        //Verifica os totais
        if (creditos.compareTo(debitos) != 0) {
            mensagem = "Total de créditos difere do total de débitos.";
        }

        //Verifica os tipos de lançamento
        //UDVC - Um Débito para Vários Créditos
        if (tipoLancamento.equals("UDVC")) {
            if (quantidadeDebitos > 1) {
                mensagem += "\nUDVC - Mais do que um débito informado.";
            }
            if (quantidadeDebitos < 1) {
                mensagem += "\nUDVC -  - Nenhum débito informado.";
            }
            if (quantidadeCreditos < 1) {
                mensagem += "\nUDVC - Nenhum crédito informado.";
            }
            if (quantidadeCreditos == 1) {
                mensagem += "\nUDVC - Apenas um crédito informado.";
            }
        }

        // UCVD - Um Crédito para Vários Débitos
        if (tipoLancamento.equals("UCVD")) {
            if (quantidadeCreditos > 1) {
                mensagem += "\nUCVD - Mais do que um crédito informado.";
            }
            if (quantidadeCreditos < 1) {
                mensagem += "\nUCVD - Nenhum crédito informado.";
            }
            if (quantidadeDebitos < 1) {
                mensagem += "\nUCVD - Nenhum débito informado.";
            }
            if (quantidadeDebitos == 1) {
                mensagem += "\nUCVD - Apenas um débito informado.";
            }
        }

        // UDUC - Um Débito para Um Crédito
        if (tipoLancamento.equals("UDUC")) {
            if (quantidadeCreditos > 1) {
                mensagem += "\nUDUC - Mais do que um crédito informado.";
            }
            if (quantidadeDebitos > 1) {
                mensagem += "\nUDUC - Mais do que um crédito informado.";
            }
            if (quantidadeCreditos < 1) {
                mensagem += "\nUDUC - Nenhum crédito informado.";
            }
            if (quantidadeDebitos < 1) {
                mensagem += "\nUDUC - Nenhum débito informado.";
            }
        }

        // VDVC - Vários Débitos para Vários Créditos
        if (tipoLancamento.equals("VDVC")) {
            if (quantidadeCreditos < 1) {
                mensagem += "\nVDVC - Nenhum crédito informado.";
            }
            if (quantidadeDebitos < 1) {
                mensagem += "\nVDVC - Nenhum débito informado.";
            }
            if (quantidadeCreditos == 1) {
                mensagem += "\nVDVC - Apenas um crédito informado.";
            }
            if (quantidadeDebitos == 1) {
                mensagem += "\nVDVC - Apenas um débito informado.";
            }
        }

        if (!mensagem.isEmpty()) {
            throw new Exception("Ocorreram erros na validação dos dados informados.\n" + mensagem);
        }
    }
    
    public void incluirContabilLancamentoDetalhe() {
        contabilLancamentoDetalhe = new ContabilLancamentoDetalhe();
        contabilLancamentoDetalhe.setContabilLancamentoCabecalho(getObjeto());
	}

	public void alterarContabilLancamentoDetalhe() {
        contabilLancamentoDetalhe = contabilLancamentoDetalheSelecionado;
	}

	public void salvarContabilLancamentoDetalhe() {
        if (contabilLancamentoDetalhe.getId() == null) {
            getObjeto().getListaContabilLancamentoDetalhe().add(contabilLancamentoDetalhe);
        }
        salvar("Registro salvo com sucesso!");
	}

	public void excluirContabilLancamentoDetalhe() {
        if (contabilLancamentoDetalheSelecionado == null || contabilLancamentoDetalheSelecionado.getId() == null) {
            FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_INFO, "Nenhum registro selecionado!", null);
        } else {
            getObjeto().getListaContabilLancamentoDetalhe().remove(contabilLancamentoDetalheSelecionado);
            salvar("Registro excluído com sucesso!");
        }
	}
    
    public List<ContabilLote> getListaContabilLote(String nome) {
        List<ContabilLote> listaContabilLote = new ArrayList<>();
        try {
            listaContabilLote = contabilLoteDao.getBeansLike(ContabilLote.class, "descricao", nome);
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return listaContabilLote;
    }

    public List<ContabilConta> getListaContabilConta(String nome) {
        List<ContabilConta> listaContabilConta = new ArrayList<>();
        try {
            listaContabilConta = contabilContaDao.getBeansLike(ContabilConta.class, "descricao", nome);
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return listaContabilConta;
    }

    public List<ContabilHistorico> getListaContabilHistorico(String nome) {
        List<ContabilHistorico> listaContabilHistorico = new ArrayList<>();
        try {
            listaContabilHistorico = contabilHistoricoDao.getBeansLike(ContabilHistorico.class, "historico", nome);
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return listaContabilHistorico;
    }
    
	public ContabilLancamentoDetalhe getContabilLancamentoDetalhe() {
		return contabilLancamentoDetalhe;
	}

	public void setContabilLancamentoDetalhe(ContabilLancamentoDetalhe contabilLancamentoDetalhe) {
		this.contabilLancamentoDetalhe = contabilLancamentoDetalhe;
	}

	public ContabilLancamentoDetalhe getContabilLancamentoDetalheSelecionado() {
		return contabilLancamentoDetalheSelecionado;
	}

	public void setContabilLancamentoDetalheSelecionado(ContabilLancamentoDetalhe contabilLancamentoDetalheSelecionado) {
		this.contabilLancamentoDetalheSelecionado = contabilLancamentoDetalheSelecionado;
	}

}
