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
package com.t2tierp.controller.agendacorporativa;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleEvent;
import org.primefaces.model.ScheduleModel;

import com.t2tierp.controller.AbstractController;
import com.t2tierp.model.bean.agendacorporativa.AgendaCategoriaCompromisso;
import com.t2tierp.model.bean.agendacorporativa.AgendaCompromisso;
import com.t2tierp.model.bean.agendacorporativa.AgendaCompromissoConvidado;
import com.t2tierp.model.bean.agendacorporativa.ReuniaoSala;
import com.t2tierp.model.bean.agendacorporativa.ReuniaoSalaEvento;
import com.t2tierp.model.bean.cadastros.Colaborador;
import com.t2tierp.model.dao.Filtro;
import com.t2tierp.model.dao.InterfaceDAO;
import com.t2tierp.util.FacesContextUtil;

@ManagedBean
@ViewScoped
public class AgendaCompromissoController extends AbstractController<AgendaCompromisso> implements Serializable {

	private static final long serialVersionUID = 1L;

	@EJB
	private InterfaceDAO<Colaborador> colaboradorDao;
	@EJB
	private InterfaceDAO<AgendaCategoriaCompromisso> agendaCategoriaCompromissoDao;
	@EJB
	private InterfaceDAO<ReuniaoSala> reuniaoSalaDao;

	private AgendaCompromissoConvidado agendaCompromissoConvidado;
	private AgendaCompromissoConvidado agendaCompromissoConvidadoSelecionado;

	private ReuniaoSalaEvento reuniaoSalaEvento;
	
	private Integer recorrente;
	private Integer quantidadeOcorrencia;

	private ScheduleModel eventModel = new DefaultScheduleModel();

	private ScheduleEvent eventoAdicionado;

	@Override
	public Class<AgendaCompromisso> getClazz() {
		return AgendaCompromisso.class;
	}

	@Override
	public String getFuncaoBase() {
		return "AGENDA_COMPROMISSO";
	}

	@Override
	public void incluir() {
		super.incluir();
		recorrente = 0;
		getObjeto().setListaAgendaCompromissoConvidado(new HashSet<AgendaCompromissoConvidado>());
	}

	@Override
	public void salvar(String mensagem) {
		try {
			super.salvar(mensagem);
			incluiCompromissoConvidado();
			incluiCompromissoRecorrente();
			atualizaCalendario();
		} catch (Exception e) {
			FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_ERROR, "Erro!", e.getMessage());
		}
	}

	public void incluirAgendaCompromissoConvidado() {
		agendaCompromissoConvidado = new AgendaCompromissoConvidado();
		agendaCompromissoConvidado.setAgendaCompromisso(getObjeto());
	}

	public void salvarAgendaCompromissoConvidado() {
		if (!getObjeto().getListaAgendaCompromissoConvidado().contains(agendaCompromissoConvidado)) {
			getObjeto().getListaAgendaCompromissoConvidado().add(agendaCompromissoConvidado);
		}
	}

	public void incluirReuniaoSalaEvento() {
		if (getObjeto().getReuniaoSalaEvento() == null) {
			reuniaoSalaEvento = new ReuniaoSalaEvento();
			reuniaoSalaEvento.setAgendaCompromisso(getObjeto());
			reuniaoSalaEvento.setDataReserva(getObjeto().getDataCompromisso());
		} else {
			reuniaoSalaEvento = getObjeto().getReuniaoSalaEvento();
		}
	}

	public void salvarReuniaoSalaEvento() {
		try {
			getObjeto().setReuniaoSalaEvento(reuniaoSalaEvento);
			FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_INFO, "Sala reuniao definida com sucesso.", "");
		} catch (Exception e) {
			FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_ERROR, "Erro!", e.getMessage());
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

	public List<AgendaCategoriaCompromisso> getListaAgendaCategoriaCompromisso(String nome) {
		List<AgendaCategoriaCompromisso> listaAgendaCategoriaCompromisso = new ArrayList<>();
		try {
			listaAgendaCategoriaCompromisso = agendaCategoriaCompromissoDao.getBeansLike(AgendaCategoriaCompromisso.class, "nome", nome);
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return listaAgendaCategoriaCompromisso;
	}

	private void incluiCompromissoConvidado() throws Exception {
		for (AgendaCompromissoConvidado c : getObjeto().getListaAgendaCompromissoConvidado()) {
			AgendaCompromisso compromisso = new AgendaCompromisso();
			compromisso.setColaborador(c.getColaborador());
			compromisso.setAgendaCategoriaCompromisso(getObjeto().getAgendaCategoriaCompromisso());
			compromisso.setDataCompromisso(getObjeto().getDataCompromisso());
			compromisso.setDescricao(getObjeto().getDescricao());
			compromisso.setDuracao(getObjeto().getDuracao());
			compromisso.setHora(getObjeto().getHora());
			compromisso.setOnde(getObjeto().getOnde());
			compromisso.setTipo(getObjeto().getTipo());

			dao.persist(compromisso);
		}
	}

	private void incluiCompromissoRecorrente() throws Exception {
		if (quantidadeOcorrencia != null && recorrente != null) {
			int campoSomar = 0;
			switch (recorrente) {
			case 1: {
				campoSomar = Calendar.DAY_OF_MONTH;
				break;
			}
			case 2: {
				campoSomar = Calendar.WEEK_OF_MONTH;
				break;
			}
			case 3: {
				campoSomar = Calendar.MONTH;
				break;
			}
			case 4: {
				campoSomar = Calendar.YEAR;
				break;
			}
			default: {
				break;
			}
			}

			if (campoSomar != 0) {
				Calendar dataCompromisso = Calendar.getInstance();
				dataCompromisso.setTime(getObjeto().getDataCompromisso());

				for (int i = 0; i < quantidadeOcorrencia; i++) {
					AgendaCompromisso compromisso = new AgendaCompromisso();
					compromisso.setColaborador(getObjeto().getColaborador());
					compromisso.setAgendaCategoriaCompromisso(getObjeto().getAgendaCategoriaCompromisso());
					compromisso.setDescricao(getObjeto().getDescricao());
					compromisso.setDuracao(getObjeto().getDuracao());
					compromisso.setHora(getObjeto().getHora());
					compromisso.setOnde(getObjeto().getOnde());
					compromisso.setTipo(getObjeto().getTipo());

					dataCompromisso.add(campoSomar, 1);
					compromisso.setDataCompromisso(dataCompromisso.getTime());

					dao.persist(compromisso);
				}
			}
		}
	}

	public void atualizaCalendario() throws Exception {
		eventModel.clear();
		List<Filtro> filtros = new ArrayList<>();
		filtros.add(new Filtro(Filtro.AND, "colaborador", Filtro.IGUAL, getObjeto().getColaborador()));
		List<AgendaCompromisso> compromissos = dao.getBeans(AgendaCompromisso.class, filtros);
		for (AgendaCompromisso c : compromissos) {
			String styleClass;
			switch (c.getAgendaCategoriaCompromisso().getCor()) {
			case "Amarelo": {
				styleClass = "eventoCalendarioAmarelo";
				break;
			}
			case "Azul": {
				styleClass = "eventoCalendarioAzul";
				break;
			}
			case "Branco": {
				styleClass = "eventoCalendarioBranco";
				break;
			}
			case "Verde": {
				styleClass = "eventoCalendarioVerde";
				break;
			}
			case "Vermelho": {
				styleClass = "eventoCalendarioVermelho";
				break;
			}
			default: {
				styleClass = "eventoCalendarioPreto";
				break;
			}
			}

			ScheduleEvent event = new DefaultScheduleEvent(c.getDescricao(), c.getDataCompromisso(), c.getDataCompromisso(), styleClass);

			eventModel.addEvent(event);
		}
	}

	public void onDateSelect(SelectEvent selectEvent) {
		eventoAdicionado = new DefaultScheduleEvent("", (Date) selectEvent.getObject(), (Date) selectEvent.getObject());
	}

	public void adicionaEvento() {
		eventModel.addEvent(eventoAdicionado);

		eventoAdicionado = new DefaultScheduleEvent();
	}

	public List<ReuniaoSala> getListaReuniaoSala(String nome) {
		List<ReuniaoSala> listaReuniaoSala = new ArrayList<>();
		try {
			listaReuniaoSala = reuniaoSalaDao.getBeansLike(ReuniaoSala.class, "predio", nome);
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return listaReuniaoSala;
	}

	public Integer getRecorrente() {
		return recorrente;
	}

	public void setRecorrente(Integer recorrente) {
		this.recorrente = recorrente;
	}

	public Integer getQuantidadeOcorrencia() {
		return quantidadeOcorrencia;
	}

	public void setQuantidadeOcorrencia(Integer quantidadeOcorrencia) {
		this.quantidadeOcorrencia = quantidadeOcorrencia;
	}

	public AgendaCompromissoConvidado getAgendaCompromissoConvidado() {
		return agendaCompromissoConvidado;
	}

	public void setAgendaCompromissoConvidado(AgendaCompromissoConvidado agendaCompromissoConvidado) {
		this.agendaCompromissoConvidado = agendaCompromissoConvidado;
	}

	public AgendaCompromissoConvidado getAgendaCompromissoConvidadoSelecionado() {
		return agendaCompromissoConvidadoSelecionado;
	}

	public void setAgendaCompromissoConvidadoSelecionado(AgendaCompromissoConvidado agendaCompromissoConvidadoSelecionado) {
		this.agendaCompromissoConvidadoSelecionado = agendaCompromissoConvidadoSelecionado;
	}

	public ScheduleModel getEventModel() {
		return eventModel;
	}

	public void setEventModel(ScheduleModel eventModel) {
		this.eventModel = eventModel;
	}

	public ScheduleEvent getEventoAdicionado() {
		return eventoAdicionado;
	}

	public void setEventoAdicionado(ScheduleEvent eventoAdicionado) {
		this.eventoAdicionado = eventoAdicionado;
	}

	public ReuniaoSalaEvento getReuniaoSalaEvento() {
		return reuniaoSalaEvento;
	}

	public void setReuniaoSalaEvento(ReuniaoSalaEvento reuniaoSalaEvento) {
		this.reuniaoSalaEvento = reuniaoSalaEvento;
	}

}
