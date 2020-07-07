package com.t2tierp.controller.ged;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

import com.t2tierp.model.bean.ged.Arquivo;

public class GedDocumentoScanner implements Serializable {

	private static final long serialVersionUID = 1L;

	private static Map<String, Arquivo> documentos = new HashMap<>();
	
	public static void addDocumento(String codigoRequisicao, Arquivo arquivo) {
		documentos.put(codigoRequisicao, arquivo);
	}

	public static Arquivo getDocumento(String codigoRequisicao) {
		Arquivo arquivo = documentos.get(codigoRequisicao);
		documentos.remove(codigoRequisicao);
		return arquivo;
	}
	
}
