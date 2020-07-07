package com.t2tierp.util;

import javax.faces.application.FacesMessage;
import javax.faces.application.FacesMessage.Severity;
import javax.faces.context.FacesContext;

public class FacesContextUtil {
	
	public static void adiconaMensagem(Severity severity, String mensagem, String msg2) {
		FacesMessage message = new FacesMessage(severity, mensagem, msg2);
		FacesContext.getCurrentInstance().addMessage(null, message);
	}
	
}
