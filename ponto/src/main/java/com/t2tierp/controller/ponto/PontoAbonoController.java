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
package com.t2tierp.controller.ponto;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.t2tierp.controller.AbstractController;
import com.t2tierp.model.bean.cadastros.Colaborador;
import com.t2tierp.model.bean.ponto.PontoAbono;
import com.t2tierp.model.bean.ponto.PontoAbonoUtilizacao;
import com.t2tierp.model.dao.InterfaceDAO;
import com.t2tierp.util.FacesContextUtil;

@ManagedBean
@ViewScoped
public class PontoAbonoController extends AbstractController<PontoAbono> implements Serializable {

    private static final long serialVersionUID = 1L;
    @EJB
    private InterfaceDAO<Colaborador> colaboradorDao;

	private PontoAbonoUtilizacao pontoAbonoUtilizacao;
	private PontoAbonoUtilizacao pontoAbonoUtilizacaoSelecionado;
    
    
    @Override
    public Class<PontoAbono> getClazz() {
        return PontoAbono.class;
    }

    @Override
    public String getFuncaoBase() {
        return "PONTO_ABONO";
    }

    @Override
    public void incluir() {
        super.incluir();
        getObjeto().setListaPontoAbonoUtilizacao(new HashSet<PontoAbonoUtilizacao>());
	}
    
	public void incluirPontoAbonoUtilizacao() {
        pontoAbonoUtilizacao = new PontoAbonoUtilizacao();
        pontoAbonoUtilizacao.setPontoAbono(getObjeto());
	}

	public void alterarPontoAbonoUtilizacao() {
        pontoAbonoUtilizacao = pontoAbonoUtilizacaoSelecionado;
	}

	public void salvarPontoAbonoUtilizacao() {
        if (pontoAbonoUtilizacao.getId() == null) {
            getObjeto().getListaPontoAbonoUtilizacao().add(pontoAbonoUtilizacao);
        }
        salvar("Registro salvo com sucesso!");
	}

	public void excluirPontoAbonoUtilizacao() {
        if (pontoAbonoUtilizacaoSelecionado == null || pontoAbonoUtilizacaoSelecionado.getId() == null) {
            FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_INFO, "Nenhum registro selecionado!", null);
        } else {
            getObjeto().getListaPontoAbonoUtilizacao().remove(pontoAbonoUtilizacaoSelecionado);
            salvar("Registro excluído com sucesso!");
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

    public PontoAbonoUtilizacao getPontoAbonoUtilizacao() {
        return pontoAbonoUtilizacao;
    }

    public void setPontoAbonoUtilizacao(PontoAbonoUtilizacao pontoAbonoUtilizacao) {
        this.pontoAbonoUtilizacao = pontoAbonoUtilizacao;
    }

    public PontoAbonoUtilizacao getPontoAbonoUtilizacaoSelecionado() {
        return pontoAbonoUtilizacaoSelecionado;
    }

    public void setPontoAbonoUtilizacaoSelecionado(PontoAbonoUtilizacao pontoAbonoUtilizacaoSelecionado) {
        this.pontoAbonoUtilizacaoSelecionado = pontoAbonoUtilizacaoSelecionado;
    }
    
}
