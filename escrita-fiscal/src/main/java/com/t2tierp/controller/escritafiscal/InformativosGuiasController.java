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
package com.t2tierp.controller.escritafiscal;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.swing.ImageIcon;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import com.t2tierp.controller.AbstractController;
import com.t2tierp.model.bean.cadastros.Empresa;
import com.t2tierp.model.bean.escritafiscal.Darf;
import com.t2tierp.model.bean.escritafiscal.FiscalLivro;
import com.t2tierp.util.FacesContextUtil;

@ManagedBean
@ViewScoped
public class InformativosGuiasController extends AbstractController<FiscalLivro> implements Serializable {

    private static final long serialVersionUID = 1L;
    private Darf darf;
    
    @Override
    public Class<FiscalLivro> getClazz() {
        return FiscalLivro.class;
    }

    @Override
    public String getFuncaoBase() {
        return "ESCRITA_FISCAL_INFORMATIVOS_GUIAS";
    }

    @PostConstruct
    @Override
    public void init() {
    	darf = new Darf();
    	super.init();
    }
    
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void geraDarf() {
		try {
			//gera o relatorio
			File file = File.createTempFile("darf", ".pdf");
			file.deleteOnExit();
			
	        List<Darf> listaDarf = new ArrayList<>();
	        Empresa empresa = FacesContextUtil.getEmpresaUsuario();
	        
	        darf.setRazaoSocial(empresa.getRazaoSocial());
	        darf.setTelefone("(61) 1234-5678");
	        darf.setCpfCnpj(empresa.getCnpj());

	        listaDarf.add(darf);

	        //gera a guia
	        ImageIcon logoReceita = new ImageIcon(this.getClass().getResource("/images/receita.jpg"));
	        Map parametros = new HashMap();
	        parametros.put("LOGO_RECEITA", logoReceita.getImage());

	        JRBeanCollectionDataSource jrbean = new JRBeanCollectionDataSource(listaDarf);
	        JasperPrint jp = JasperFillManager.fillReport(this.getClass().getResourceAsStream("/relatorios/darf/darf.jasper"), parametros, jrbean);
			
			OutputStream outPdf = new FileOutputStream(file);
			outPdf.write(JasperExportManager.exportReportToPdf(jp));
			outPdf.close();

			FacesContextUtil.downloadArquivo(file, "darf.pdf");
		} catch (Exception e) {
			e.printStackTrace();
			FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro ao gerar o relatório!", e.getMessage());
		}
	}    
    
	public Darf getDarf() {
		return darf;
	}

	public void setDarf(Darf darf) {
		this.darf = darf;
	}

}