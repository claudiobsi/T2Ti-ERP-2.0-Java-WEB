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
import java.util.Calendar;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.model.DefaultScheduleEvent;
import org.primefaces.model.DefaultScheduleModel;
import org.primefaces.model.ScheduleModel;

import com.t2tierp.controller.AbstractController;
import com.t2tierp.model.bean.cadastros.Colaborador;
import com.t2tierp.util.FacesContextUtil;

@ManagedBean
@ViewScoped
public class AgendaAniversarianteController extends AbstractController<Colaborador> implements Serializable {

    private static final long serialVersionUID = 1L;

    private ScheduleModel eventModel = new DefaultScheduleModel();
    
    @Override
    public Class<Colaborador> getClazz() {
        return Colaborador.class;
    }

    @Override
    public String getFuncaoBase() {
        return "AGENDA_ANIVERSARIANTE";
    }

    @Override
    @PostConstruct
    public void init() {
    	super.init();
    	try {
			atualizaCalendario();
		} catch (Exception e) {
			e.printStackTrace();
			FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro!", e.getMessage());
		}
    }
    
	private void atualizaCalendario() throws Exception {
		eventModel.clear();
		List<Colaborador> colaboradores = dao.getBeans(Colaborador.class);
		
        Calendar calendar = Calendar.getInstance();
        calendar.add(Calendar.MONTH, -2);

        for (int i = 0; i < 120; i++) {
            for (Colaborador c : colaboradores) {
                if (c.getPessoa().getPessoaFisica() != null) {
                    Calendar dataAniversario = Calendar.getInstance();
                    dataAniversario.setTime(c.getPessoa().getPessoaFisica().getDataNascimento());
                    if ((dataAniversario.get(Calendar.DAY_OF_MONTH) == calendar.get(Calendar.DAY_OF_MONTH))
                            && (dataAniversario.get(Calendar.MONTH) == calendar.get(Calendar.MONTH))) {
                    	DefaultScheduleEvent event = new DefaultScheduleEvent(c.getPessoa().getNome(), calendar.getTime(), calendar.getTime(), "eventoCalendarioAniversario");
            			event.setAllDay(true);
            			eventModel.addEvent(event);
                    }
                }
            }
            calendar.add(Calendar.DAY_OF_MONTH, 1);
        }
	}

	public ScheduleModel getEventModel() {
		return eventModel;
	}

	public void setEventModel(ScheduleModel eventModel) {
		this.eventModel = eventModel;
	}    
}
