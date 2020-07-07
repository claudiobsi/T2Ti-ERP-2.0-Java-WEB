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

import org.primefaces.event.SelectEvent;

import com.t2tierp.controller.AbstractController;
import com.t2tierp.model.bean.ponto.PontoEscala;
import com.t2tierp.model.bean.ponto.PontoHorario;
import com.t2tierp.model.bean.ponto.PontoTurma;
import com.t2tierp.model.dao.InterfaceDAO;
import com.t2tierp.util.FacesContextUtil;

@ManagedBean
@ViewScoped
public class PontoEscalaController extends AbstractController<PontoEscala> implements Serializable {

    private static final long serialVersionUID = 1L;
	@EJB
	private InterfaceDAO<PontoHorario> pontoHorarioDao;
    
    private PontoHorario horarioDomingo;
    private PontoHorario horarioSegunda;
    private PontoHorario horarioTerca;
    private PontoHorario horarioQuarta;
    private PontoHorario horarioQuinta;
    private PontoHorario horarioSexta;
    private PontoHorario horarioSbado;
    
	private PontoTurma pontoTurma;
	private PontoTurma pontoTurmaSelecionado;

    @Override
    public Class<PontoEscala> getClazz() {
        return PontoEscala.class;
    }

    @Override
    public String getFuncaoBase() {
        return "PONTO_ESCALA";
    }
    
    @Override
    public void incluir() {
    	super.incluir();
    	getObjeto().setEmpresa(FacesContextUtil.getEmpresaUsuario());
    	getObjeto().setListaPontoTurma(new HashSet<PontoTurma>());
    }

    public void selecionaHorarioDomingo(SelectEvent event) {
    	PontoHorario horario = (PontoHorario) event.getObject();
    	getObjeto().setCodigoHorarioDomingo(horario.getCodigo());
    }

    public void selecionaHorarioSegunda(SelectEvent event) {
    	PontoHorario horario = (PontoHorario) event.getObject();
    	getObjeto().setCodigoHorarioSegunda(horario.getCodigo());
    	getObjeto().setCodigoHorarioTerca(horario.getCodigo());
    	getObjeto().setCodigoHorarioQuarta(horario.getCodigo());
    	getObjeto().setCodigoHorarioQuinta(horario.getCodigo());
    	getObjeto().setCodigoHorarioSexta(horario.getCodigo());
    }

    public void selecionaHorarioTerca(SelectEvent event) {
    	PontoHorario horario = (PontoHorario) event.getObject();
    	getObjeto().setCodigoHorarioTerca(horario.getCodigo());
    }

    public void selecionaHorarioQuarta(SelectEvent event) {
    	PontoHorario horario = (PontoHorario) event.getObject();
    	getObjeto().setCodigoHorarioQuarta(horario.getCodigo());
    }

    public void selecionaHorarioQuinta(SelectEvent event) {
    	PontoHorario horario = (PontoHorario) event.getObject();
    	getObjeto().setCodigoHorarioQuinta(horario.getCodigo());
    }

    public void selecionaHorarioSexta(SelectEvent event) {
    	PontoHorario horario = (PontoHorario) event.getObject();
    	getObjeto().setCodigoHorarioSexta(horario.getCodigo());
    }

    public void selecionaHorarioSabado(SelectEvent event) {
    	PontoHorario horario = (PontoHorario) event.getObject();
    	getObjeto().setCodigoHorarioSabado(horario.getCodigo());
    }

	public void incluirPontoTurma() {
        pontoTurma = new PontoTurma();
        pontoTurma.setPontoEscala(getObjeto());
	}

	public void alterarPontoTurma() {
        pontoTurma = pontoTurmaSelecionado;
	}

	public void salvarPontoTurma() {
        if (pontoTurma.getId() == null) {
            getObjeto().getListaPontoTurma().add(pontoTurma);
        }
        salvar("Registro salvo com sucesso!");
	}
	public void excluirPontoTurma() {
        if (pontoTurmaSelecionado == null || pontoTurmaSelecionado.getId() == null) {
            FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_INFO, "Nenhum registro selecionado!", null);
        } else {
            getObjeto().getListaPontoTurma().remove(pontoTurmaSelecionado);
            salvar("Registro excluído com sucesso!");
        }
	}
    
	public List<PontoHorario> getListaPontoHorario(String nome) {
		List<PontoHorario> listaPontoHorario = new ArrayList<>();
		try {
			listaPontoHorario = pontoHorarioDao.getBeansLike(PontoHorario.class, "nome", nome);
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return listaPontoHorario;
	}
    
	public PontoHorario getHorarioDomingo() {
		return horarioDomingo;
	}

	public void setHorarioDomingo(PontoHorario horarioDomingo) {
		this.horarioDomingo = horarioDomingo;
	}

	public PontoHorario getHorarioSegunda() {
		return horarioSegunda;
	}

	public void setHorarioSegunda(PontoHorario horarioSegunda) {
		this.horarioSegunda = horarioSegunda;
	}

	public PontoHorario getHorarioTerca() {
		return horarioTerca;
	}

	public void setHorarioTerca(PontoHorario horarioTerca) {
		this.horarioTerca = horarioTerca;
	}

	public PontoHorario getHorarioQuarta() {
		return horarioQuarta;
	}

	public void setHorarioQuarta(PontoHorario horarioQuarta) {
		this.horarioQuarta = horarioQuarta;
	}

	public PontoHorario getHorarioQuinta() {
		return horarioQuinta;
	}

	public void setHorarioQuinta(PontoHorario horarioQuinta) {
		this.horarioQuinta = horarioQuinta;
	}

	public PontoHorario getHorarioSexta() {
		return horarioSexta;
	}

	public void setHorarioSexta(PontoHorario horarioSexta) {
		this.horarioSexta = horarioSexta;
	}

	public PontoHorario getHorarioSbado() {
		return horarioSbado;
	}

	public void setHorarioSbado(PontoHorario horarioSbado) {
		this.horarioSbado = horarioSbado;
	}

    public PontoTurma getPontoTurma() {
        return pontoTurma;
    }

    public void setPontoTurma(PontoTurma pontoTurma) {
        this.pontoTurma = pontoTurma;
    }

    public PontoTurma getPontoTurmaSelecionado() {
        return pontoTurmaSelecionado;
    }

    public void setPontoTurmaSelecionado(PontoTurma pontoTurmaSelecionado) {
        this.pontoTurmaSelecionado = pontoTurmaSelecionado;
    }
    
}
