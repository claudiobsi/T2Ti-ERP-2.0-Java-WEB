package com.t2tierp.controller.ged;

import java.util.Base64;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.core.Response;

import com.t2tierp.model.bean.ged.Arquivo;

@RequestScoped
@Path("/documento")
public class GedWebService {

	@POST
	@Path("/envio/{codigoRequisicao}")
	@Consumes("application/json")
	public Response recebeDocumento(Arquivo arquivo, @PathParam("codigoRequisicao") String codigoRequisicao) {
		try {
			arquivo.setFile(Base64.getDecoder().decode(arquivo.getFileBase64()));
			GedDocumentoScanner.addDocumento(codigoRequisicao, arquivo);
		
			return Response.ok().build();
		} catch (Exception e) {
			e.printStackTrace();
			return Response.serverError().build();
		}
	}

}
