package com.t2tierp.controller.sped;

import java.io.File;
import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.inject.Inject;

import com.t2tierp.model.bean.cadastros.Contador;
import com.t2tierp.model.dao.InterfaceDAO;
import com.t2tierp.util.FacesContextUtil;

@ManagedBean
@ViewScoped
public class SpedFiscalController implements Serializable {

	private static final long serialVersionUID = 1L;
	private Date dataInicial;
	private Date dataFinal;
	private String versao;
	private String finalidadeArquivo;
	private String perfil;
	private Integer inventario;
	private Integer idContador;
	private HashMap<String, Integer> contadores;
	@Inject
	private ArquivoSpedFiscal arquivoSpedFiscal;
	@EJB
	private InterfaceDAO<Contador> contadorDao;

	@PostConstruct
	public void init() {
		try {
			contadores = new HashMap<>();
			List<Contador> listaContador = contadorDao.getBeans(Contador.class);
			for (Contador c : listaContador) {
				contadores.put(c.getNome(), c.getId());
			}
		} catch (Exception e) {
			//e.printStackTrace();
			contadores.put("Ocorreu um erro ao buscar os dados de contadores", 0);
		}
	}

	public SpedFiscalController() {
	}

	public void geraSpedFiscal() {
		try {
			Calendar d1 = Calendar.getInstance();
			Calendar d2 = Calendar.getInstance();
			d1.setTime(dataInicial);
			d2.setTime(dataFinal);
			if (d2.before(d1)) {
				throw new Exception("Data inicial posterior a data final!");
			}
			File arquivo = arquivoSpedFiscal.geraArquivo(versao, finalidadeArquivo, perfil, inventario, dataInicial, dataFinal, idContador);
			FacesContextUtil.downloadArquivo(arquivo, "spedfiscal.txt");
		} catch (Exception ex) {
			ex.printStackTrace();
			FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro ao gerar o arquivo.", ex.getMessage());
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

	public String getVersao() {
		return versao;
	}

	public void setVersao(String versao) {
		this.versao = versao;
	}

	public String getFinalidadeArquivo() {
		return finalidadeArquivo;
	}

	public void setFinalidadeArquivo(String finalidadeArquivo) {
		this.finalidadeArquivo = finalidadeArquivo;
	}

	public String getPerfil() {
		return perfil;
	}

	public void setPerfil(String perfil) {
		this.perfil = perfil;
	}

	public Integer getInventario() {
		return inventario;
	}

	public void setInventario(Integer inventario) {
		this.inventario = inventario;
	}

	public Integer getIdContador() {
		return idContador;
	}

	public void setIdContador(Integer idContador) {
		this.idContador = idContador;
	}

	public HashMap<String, Integer> getContadores() {
		return contadores;
	}

}
