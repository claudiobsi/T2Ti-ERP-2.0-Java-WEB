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
package com.t2tierp.controller.patrimonio;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashSet;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.t2tierp.controller.AbstractController;
import com.t2tierp.model.bean.cadastros.Colaborador;
import com.t2tierp.model.bean.cadastros.Fornecedor;
import com.t2tierp.model.bean.cadastros.Setor;
import com.t2tierp.model.bean.contabilidade.CentroResultado;
import com.t2tierp.model.bean.patrimonio.PatrimBem;
import com.t2tierp.model.bean.patrimonio.PatrimDepreciacaoBem;
import com.t2tierp.model.bean.patrimonio.PatrimDocumentoBem;
import com.t2tierp.model.bean.patrimonio.PatrimEstadoConservacao;
import com.t2tierp.model.bean.patrimonio.PatrimGrupoBem;
import com.t2tierp.model.bean.patrimonio.PatrimMovimentacaoBem;
import com.t2tierp.model.bean.patrimonio.PatrimTipoAquisicaoBem;
import com.t2tierp.model.bean.patrimonio.PatrimTipoMovimentacao;
import com.t2tierp.model.dao.InterfaceDAO;
import com.t2tierp.util.FacesContextUtil;

@ManagedBean
@ViewScoped
public class PatrimBemController extends AbstractController<PatrimBem> implements Serializable {

    private static final long serialVersionUID = 1L;
    @EJB
    private InterfaceDAO<PatrimTipoAquisicaoBem> patrimTipoAquisicaoBemDao;
    @EJB
    private InterfaceDAO<PatrimEstadoConservacao> patrimEstadoConservacaoDao;
    @EJB
    private InterfaceDAO<PatrimGrupoBem> patrimGrupoBemDao;
    @EJB
    private InterfaceDAO<Setor> setorDao;
    @EJB
    private InterfaceDAO<Fornecedor> fornecedorDao;
    @EJB
    private InterfaceDAO<Colaborador> colaboradorDao;
    @EJB
    private InterfaceDAO<PatrimTipoMovimentacao> tipoMovimentacaoDao;

	private PatrimDocumentoBem patrimDocumentoBem;
	private PatrimDocumentoBem patrimDocumentoBemSelecionado;
	private PatrimMovimentacaoBem patrimMovimentacaoBem;
	private PatrimMovimentacaoBem patrimMovimentacaoBemSelecionado;
    
    @Override
    public Class<PatrimBem> getClazz() {
        return PatrimBem.class;
    }

    @Override
    public String getFuncaoBase() {
        return "PATRIM_BEM";
    }
    
    @Override
    public void incluir() {
        super.incluir();

        //Exercicio: incluir na janela PatrimBemDetalhe campo para que o usuário selecione o centro de resultado
        CentroResultado centroResultado = new CentroResultado();
        centroResultado.setId(1);
        getObjeto().setCentroResultado(centroResultado);
        //*************************

        getObjeto().setListaPatrimDocumentoBem(new HashSet<PatrimDocumentoBem>());
        getObjeto().setListaPatrimDepreciacaoBem(new HashSet<PatrimDepreciacaoBem>());
        getObjeto().setListaPatrimMovimentacaoBem(new HashSet<PatrimMovimentacaoBem>());
	}    

	public void incluirPatrimDocumentoBem() {
        patrimDocumentoBem = new PatrimDocumentoBem();
        patrimDocumentoBem.setPatrimBem(getObjeto());
	}

	public void alterarPatrimDocumentoBem() {
        patrimDocumentoBem = patrimDocumentoBemSelecionado;
	}

	public void salvarPatrimDocumentoBem() {
        if (patrimDocumentoBem.getId() == null) {
            getObjeto().getListaPatrimDocumentoBem().add(patrimDocumentoBem);
        }
        salvar("Registro salvo com sucesso!");
	}
	
	public void excluirPatrimDocumentoBem() {
        if (patrimDocumentoBemSelecionado == null || patrimDocumentoBemSelecionado.getId() == null) {
            FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_INFO, "Nenhum registro selecionado!", null);
        } else {
            getObjeto().getListaPatrimDocumentoBem().remove(patrimDocumentoBemSelecionado);
            salvar("Registro excluído com sucesso!");
        }
	}
    
	public void incluirPatrimMovimentacaoBem() {
        patrimMovimentacaoBem = new PatrimMovimentacaoBem();
        patrimMovimentacaoBem.setPatrimBem(getObjeto());
	}

	public void alterarPatrimMovimentacaoBem() {
        patrimMovimentacaoBem = patrimMovimentacaoBemSelecionado;
	}

	public void salvarPatrimMovimentacaoBem() {
        if (patrimMovimentacaoBem.getId() == null) {
            getObjeto().getListaPatrimMovimentacaoBem().add(patrimMovimentacaoBem);
        }
        salvar("Registro salvo com sucesso!");
	}
	public void excluirPatrimMovimentacaoBem() {
        if (patrimMovimentacaoBemSelecionado == null || patrimMovimentacaoBemSelecionado.getId() == null) {
            FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_INFO, "Nenhum registro selecionado!", null);
        } else {
            getObjeto().getListaPatrimMovimentacaoBem().remove(patrimMovimentacaoBemSelecionado);
            salvar("Registro excluído com sucesso!");
        }
	}
	
    public List<PatrimTipoAquisicaoBem> getListaPatrimTipoAquisicaoBem(String nome) {
        List<PatrimTipoAquisicaoBem> listaPatrimTipoAquisicaoBem = new ArrayList<>();
        try {
            listaPatrimTipoAquisicaoBem = patrimTipoAquisicaoBemDao.getBeansLike(PatrimTipoAquisicaoBem.class, "nome", nome);
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return listaPatrimTipoAquisicaoBem;
    }

    public List<PatrimEstadoConservacao> getListaPatrimEstadoConservacao(String nome) {
        List<PatrimEstadoConservacao> listaPatrimEstadoConservacao = new ArrayList<>();
        try {
            listaPatrimEstadoConservacao = patrimEstadoConservacaoDao.getBeansLike(PatrimEstadoConservacao.class, "nome", nome);
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return listaPatrimEstadoConservacao;
    }

    public List<PatrimGrupoBem> getListaPatrimGrupoBem(String nome) {
        List<PatrimGrupoBem> listaPatrimGrupoBem = new ArrayList<>();
        try {
            listaPatrimGrupoBem = patrimGrupoBemDao.getBeansLike(PatrimGrupoBem.class, "nome", nome);
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return listaPatrimGrupoBem;
    }

    public List<Setor> getListaSetor(String nome) {
        List<Setor> listaSetor = new ArrayList<>();
        try {
            listaSetor = setorDao.getBeansLike(Setor.class, "nome", nome);
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return listaSetor;
    }

    public List<Fornecedor> getListaFornecedor(String nome) {
        List<Fornecedor> listaFornecedor = new ArrayList<>();
        try {
            listaFornecedor = fornecedorDao.getBeansLike(Fornecedor.class, "pessoa.nome", nome);
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return listaFornecedor;
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

    public List<PatrimTipoMovimentacao> getListaPatrimTipoMovimentacao(String nome) {
        List<PatrimTipoMovimentacao> listaPatrimTipoMovimentacao = new ArrayList<>();
        try {
            listaPatrimTipoMovimentacao = tipoMovimentacaoDao.getBeansLike(PatrimTipoMovimentacao.class, "nome", nome);
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return listaPatrimTipoMovimentacao;
    }
    
    public void calculaDepreciacao() {
        try {
        	PatrimBem bem = getObjeto();

            Calendar dataAtual = Calendar.getInstance();
            PatrimDepreciacaoBem depreciacao = new PatrimDepreciacaoBem();

            depreciacao.setDataDepreciacao(dataAtual.getTime());
            depreciacao.setDias(dataAtual.get(Calendar.DAY_OF_MONTH));

            String tipoDepreciacao = bem.getTipoDepreciacao();
            //Normal
            if (tipoDepreciacao.equals("N")){
                if (bem.getTaxaMensalDepreciacao() == null){
                    throw new Exception("Taxa mensal de depreciação não definida!");
                }
                depreciacao.setIndice(BigDecimal.valueOf(depreciacao.getDias().doubleValue() / 30).multiply(bem.getTaxaMensalDepreciacao()));
                depreciacao.setTaxa(bem.getTaxaMensalDepreciacao());
            }

            //Acelerada
            if (tipoDepreciacao.equals("A")){
                if (bem.getTaxaDepreciacaoAcelerada() == null){
                    throw new Exception("Taxa de depreciação acelerada não definida!");
                }
                depreciacao.setIndice(BigDecimal.valueOf(depreciacao.getDias().doubleValue() / 30).multiply(bem.getTaxaDepreciacaoAcelerada()));
                depreciacao.setTaxa(bem.getTaxaDepreciacaoAcelerada());
            }
            
            //Incentivada
            if (tipoDepreciacao.equals("I")){
                if (bem.getTaxaDepreciacaoIncentivada() == null){
                    throw new Exception("Taxa de depreciação incentivada não definida!");
                }
                depreciacao.setIndice(BigDecimal.valueOf(depreciacao.getDias().doubleValue() / 30).multiply(bem.getTaxaDepreciacaoIncentivada()));
                depreciacao.setTaxa(bem.getTaxaDepreciacaoIncentivada());
            }

            if (bem.getValorOriginal() == null){
                throw new Exception("Valor Original do bem não definido!");
            }
            depreciacao.setValor(bem.getValorOriginal().multiply(depreciacao.getIndice()));
            depreciacao.setDepreciacaoAcumulada(bem.getValorOriginal().subtract(depreciacao.getValor()));

            depreciacao.setPatrimBem(getObjeto());
            getObjeto().getListaPatrimDepreciacaoBem().add(depreciacao);
            
            dao.merge(getObjeto());
        } catch(Exception e) {
        	e.printStackTrace();
        	FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_ERROR, "Erro ao realizar o calculo!", e.getMessage());
        }
    }
    
    public PatrimDocumentoBem getPatrimDocumentoBem() {
        return patrimDocumentoBem;
    }

    public void setPatrimDocumentoBem(PatrimDocumentoBem patrimDocumentoBem) {
        this.patrimDocumentoBem = patrimDocumentoBem;
    }

    public PatrimDocumentoBem getPatrimDocumentoBemSelecionado() {
        return patrimDocumentoBemSelecionado;
    }

    public void setPatrimDocumentoBemSelecionado(PatrimDocumentoBem patrimDocumentoBemSelecionado) {
        this.patrimDocumentoBemSelecionado = patrimDocumentoBemSelecionado;
    }

    public PatrimMovimentacaoBem getPatrimMovimentacaoBem() {
        return patrimMovimentacaoBem;
    }

    public void setPatrimMovimentacaoBem(PatrimMovimentacaoBem patrimMovimentacaoBem) {
        this.patrimMovimentacaoBem = patrimMovimentacaoBem;
    }    

    public PatrimMovimentacaoBem getPatrimMovimentacaoBemSelecionado() {
        return patrimMovimentacaoBemSelecionado;
    }

    public void setPatrimMovimentacaoBemSelecionado(PatrimMovimentacaoBem patrimMovimentacaoBemSelecionado) {
        this.patrimMovimentacaoBemSelecionado = patrimMovimentacaoBemSelecionado;
    }    
    
}
