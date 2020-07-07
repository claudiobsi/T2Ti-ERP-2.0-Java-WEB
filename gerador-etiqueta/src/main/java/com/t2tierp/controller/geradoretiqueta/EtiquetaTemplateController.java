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
package com.t2tierp.controller.geradoretiqueta;

import java.awt.Image;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.oned.EAN13Writer;
import com.google.zxing.qrcode.QRCodeWriter;
import com.t2tierp.controller.AbstractController;
import com.t2tierp.model.bean.geradoretiqueta.EtiquetaLayout;
import com.t2tierp.model.bean.geradoretiqueta.EtiquetaRelatorio;
import com.t2tierp.model.bean.geradoretiqueta.EtiquetaTemplate;
import com.t2tierp.model.dao.InterfaceDAO;
import com.t2tierp.model.dao.geradoretiqueta.EtiquetaDAO;
import com.t2tierp.util.FacesContextUtil;

@ManagedBean
@ViewScoped
public class EtiquetaTemplateController extends AbstractController<EtiquetaTemplate> implements Serializable {

	private static final long serialVersionUID = 1L;

	@EJB
	private InterfaceDAO<EtiquetaLayout> etiquetaLayoutDao;
	@EJB
	private EtiquetaDAO etiquetaDao;

	private List<String> tabelas;
	private String tabelaSelecionada;
	private List<String> campos;
	private String campoSelecionado;
	private Map<String, List<String>> listaTabelasCampos;

	@Override
	public Class<EtiquetaTemplate> getClazz() {
		return EtiquetaTemplate.class;
	}

	@Override
	public String getFuncaoBase() {
		return "ETIQUETA_TEMPLATE";
	}

	public void selecionaTabelaCampo() {
		try {
			if (listaTabelasCampos == null) {
				listaTabelasCampos = etiquetaDao.listaTabelas();
				tabelas = new ArrayList<String>(listaTabelasCampos.keySet());
				Collections.sort(tabelas);
			}
			tabelaSelecionada = getObjeto().getTabela();
			buscaCampos();
		} catch (Exception e) {
			e.printStackTrace();
			FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro", e.getMessage());
		}
	}

	public void buscaCampos() {
		try {
			campos = listaTabelasCampos.get(tabelaSelecionada);
		} catch (Exception e) {
			e.printStackTrace();
			FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro", e.getMessage());
		}
	}

	public void salvarTabelaCampo() {
		try {
			getObjeto().setTabela(tabelaSelecionada);
			getObjeto().setCampo(campoSelecionado);
		} catch (Exception e) {
			e.printStackTrace();
			FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro", e.getMessage());
		}
	}

	@SuppressWarnings("rawtypes")
	public void geraEtiqueta() {
		try {
			if (getObjeto().getId() == null) {
				FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_INFO, "Necessário salvar o template", "");
			} else {

				List listaRegistros = etiquetaDao.listaRegistros(getObjeto());

				List<EtiquetaRelatorio> listaRelatorio = new ArrayList<>();
				for (Object o : listaRegistros) {
					if (o != null) {
						for (int i = 0; i < getObjeto().getQuantidadeRepeticoes(); i++) {
							EtiquetaRelatorio er = new EtiquetaRelatorio();
							er.setTexto(o.toString());
							er.setCodigoBarra(geracodigoBarra(getObjeto().getFormato(), er.getTexto()));

							listaRelatorio.add(er);
						}
					}
				}

				File arquivoPdf = File.createTempFile("etiqueta", ".pdf");

				JRBeanCollectionDataSource jrbean = new JRBeanCollectionDataSource(listaRelatorio);
				JasperReport jr = GeradorEtiqueta.geraEtiqueta(getObjeto().getEtiquetaLayout());
				JasperPrint jp = JasperFillManager.fillReport(jr, new HashMap<>(), jrbean);

				OutputStream outPdf = new FileOutputStream(arquivoPdf);
				outPdf.write(JasperExportManager.exportReportToPdf(jp));
				outPdf.close();

				FacesContextUtil.downloadArquivo(arquivoPdf, "etiqueta.pdf");
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro", e.getMessage());
		}
	}

	private Image geracodigoBarra(int formato, String texto) throws Exception {
		if (formato == 0) {
			return MatrixToImageWriter.toBufferedImage(new EAN13Writer().encode(texto, BarcodeFormat.EAN_13, 180, 70));
		} else {
			return MatrixToImageWriter.toBufferedImage(new QRCodeWriter().encode(texto, BarcodeFormat.QR_CODE, 200, 200));
		}
	}

	public List<EtiquetaLayout> getListaEtiquetaLayout(String nome) {
		List<EtiquetaLayout> listaEtiquetaLayout = new ArrayList<>();
		try {
			listaEtiquetaLayout = etiquetaLayoutDao.getBeansLike(EtiquetaLayout.class, "codigoFabricante", nome);
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return listaEtiquetaLayout;
	}

	public List<String> getTabelas() {
		return tabelas;
	}

	public void setTabelas(List<String> tabelas) {
		this.tabelas = tabelas;
	}

	public List<String> getCampos() {
		return campos;
	}

	public void setCampos(List<String> campos) {
		this.campos = campos;
	}

	public String getTabelaSelecionada() {
		return tabelaSelecionada;
	}

	public void setTabelaSelecionada(String tabelaSelecionada) {
		this.tabelaSelecionada = tabelaSelecionada;
	}

	public String getCampoSelecionado() {
		return campoSelecionado;
	}

	public void setCampoSelecionado(String campoSelecionado) {
		this.campoSelecionado = campoSelecionado;
	}

}
