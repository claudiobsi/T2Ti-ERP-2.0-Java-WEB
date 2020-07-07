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
package com.t2tierp.controller.folhapagamento;

import java.io.File;
import java.io.Serializable;
import java.util.Collection;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.io.FileUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import com.t2tierp.controller.AbstractController;
import com.t2tierp.edi.cnab240.bb.DetalheLoteSegmentoT;
import com.t2tierp.edi.cnab240.bb.GerarArquivoRemessaPagamentoBB;
import com.t2tierp.edi.cnab240.bb.LeArquivoRetornoPagamentoBB;
import com.t2tierp.model.bean.cadastros.Colaborador;
import com.t2tierp.model.bean.cadastros.Empresa;
import com.t2tierp.util.FacesContextUtil;

@ManagedBean
@ViewScoped
public class FolhaEDIController extends AbstractController<Colaborador> implements Serializable {

	private static final long serialVersionUID = 1L;

	@Override
	public Class<Colaborador> getClazz() {
		return Colaborador.class;
	}

	@Override
	public String getFuncaoBase() {
		return "FOLHA_EDI";
	}

	public void gerarRemessaEDI() {
		try {
			Empresa empresa = FacesContextUtil.getEmpresaUsuario();
			List<Colaborador> colaboradores = dao.getBeans(getClazz());

			File file = File.createTempFile("cnab240", ".txt");
			file.deleteOnExit();

			GerarArquivoRemessaPagamentoBB arquivoRemessa = new GerarArquivoRemessaPagamentoBB();
			arquivoRemessa.gerarArquivoRemessa(colaboradores, empresa, file);
			FacesContextUtil.downloadArquivo(file, "cnab240.txt");
		} catch (Exception e) {
			e.printStackTrace();
			FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro ao gerar o arquivo!", e.getMessage());
		}
	}

	public void lerRetornoEDI(FileUploadEvent event) {
		try {
			UploadedFile arquivo = event.getFile();
			File file = File.createTempFile("cnab240", ".txt");
			FileUtils.copyInputStreamToFile(arquivo.getInputstream(), file);
			
			LeArquivoRetornoPagamentoBB arquivoRetorno = new LeArquivoRetornoPagamentoBB();
			Collection<DetalheLoteSegmentoT> listaSegmentoT = arquivoRetorno.leArquivoRetorno(file);
			
			for(DetalheLoteSegmentoT d: listaSegmentoT) {
				//exercício: realizar o processamento do retorno
			}
			
			FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_INFO, "Arquivo processado com sucesso.", "");
		} catch (Exception e) {
			e.printStackTrace();
			FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro ao ler arquivo!", e.getMessage());
		}
	}
}
