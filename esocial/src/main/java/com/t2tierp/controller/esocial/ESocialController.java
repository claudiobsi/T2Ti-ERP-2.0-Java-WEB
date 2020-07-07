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
 * all copies or substantial portions of the Software
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
package com.t2tierp.controller.esocial;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.Serializable;
import java.security.KeyStore;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;

import com.t2tierp.controller.AbstractController;
import com.t2tierp.model.bean.cadastros.Empresa;
import com.t2tierp.model.bean.cadastros.EmpresaEndereco;
import com.t2tierp.model.bean.nfe.NfeConfiguracao;
import com.t2tierp.util.FacesContextUtil;

@ManagedBean
@ViewScoped
public class ESocialController extends AbstractController<NfeConfiguracao> implements Serializable {

	private static final long serialVersionUID = 1L;
	
	private Date dataInicial;
	private Date dataFinal;	
    private Empresa empresa;
    private EmpresaEndereco endereco;
    private String arquivosSelecionados[];
    private List<String> arquivos;	
    
	@Override
	public Class<NfeConfiguracao> getClazz() {
		return NfeConfiguracao.class;
	}

	@Override
	public String getFuncaoBase() {
		return "ESOCIAL";
	}

	@PostConstruct
	@Override
	public void init() {
		super.init();
		
		arquivos = new ArrayList<>();
		arquivos.add("S-1000");
		arquivos.add("S-1010");
	}
	
	public void geraESocial() {
		try {
			NfeConfiguracao nfeConfiguracao = dao.getBean(1, NfeConfiguracao.class);
            if (nfeConfiguracao == null) {
                throw new Exception("Configuração NF-e não definida.");
            }

            KeyStore ks = KeyStore.getInstance("PKCS12");
            char[] senha = nfeConfiguracao.getCertificadoDigitalSenha().toCharArray();
            ks.load(new FileInputStream(new File(nfeConfiguracao.getCertificadoDigitalCaminho())), senha);
            String alias = ks.aliases().nextElement();

            GeraXmlESocial geraXmlESocial = new GeraXmlESocial(alias, ks, senha);
            ValidaXmlESocial validaXmleSocial = new ValidaXmlESocial();
            ServletContext context = (ServletContext) FacesContext.getCurrentInstance().getExternalContext().getContext();
            
			empresa = FacesContextUtil.getEmpresaUsuario();
			for (EmpresaEndereco e : empresa.getListaEndereco()) {
				if (e.getPrincipal().equals("S")) {
					endereco = e;
				}
			}

            File arquivoZip = File.createTempFile("eSocial", ".txt");
            arquivoZip.deleteOnExit();
			FileOutputStream dest = new FileOutputStream(arquivoZip);
			ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));
			
			String xml;
			for (String a : arquivosSelecionados) {
				if (a.equals("S-1000")) {
		            xml = geraXmlESocial.gerarESocial1000(empresa, endereco, dataInicial, dataFinal);

		            try {
		                validaXmleSocial.validaXmlESocial1000(xml, context);
		            } catch (Exception e) {
		                throw new Exception("Erro na validação do XML (S-1000)\n" + e.getMessage());
		            }
		            ZipEntry zipEntry = new ZipEntry(a + ".xml");
		            out.putNextEntry(zipEntry);
		            out.write(xml.getBytes("UTF-8"));
				}
				if (a.equals("S-1010")) {
		            xml = geraXmlESocial.gerarESocial1010(empresa, endereco, dataInicial, dataFinal);

		            try {
		                validaXmleSocial.validaXmlESocial1010(xml, context);
		            } catch (Exception e) {
		                throw new Exception("Erro na validação do XML (S-1010)\n" + e.getMessage());
		            }
		            ZipEntry zipEntry = new ZipEntry(a + ".xml");
		            out.putNextEntry(zipEntry);
		            out.write(xml.getBytes("UTF-8"));
				}
			}
			out.close();

			FacesContextUtil.downloadArquivo(arquivoZip, "eSocial.zip");
		} catch (Exception e) {
			e.printStackTrace();
			FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro!", e.getMessage());
		} 
	}

	public Date getDataInicial() {
		return dataInicial;
	}

	public void setDataInicial(Date dataInicial) {
		this.dataInicial = dataInicial;
	}

	public Date getDataFinal() {
		return dataFinal;
	}

	public void setDataFinal(Date dataFinal) {
		this.dataFinal = dataFinal;
	}

	public String[] getArquivosSelecionados() {
		return arquivosSelecionados;
	}

	public void setArquivosSelecionados(String arquivosSelecionados[]) {
		this.arquivosSelecionados = arquivosSelecionados;
	}

	public List<String> getArquivos() {
		return arquivos;
	}

	public void setArquivos(List<String> arquivos) {
		this.arquivos = arquivos;
	}

}
