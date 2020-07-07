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
package com.t2tierp.controller.cadastros;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.FacesContext;

import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import com.t2tierp.controller.AbstractController;
import com.t2tierp.model.bean.cadastros.Almoxarifado;
import com.t2tierp.model.bean.cadastros.FichaTecnica;
import com.t2tierp.model.bean.cadastros.Produto;
import com.t2tierp.model.bean.cadastros.ProdutoMarca;
import com.t2tierp.model.bean.cadastros.ProdutoSubgrupo;
import com.t2tierp.model.bean.cadastros.UnidadeProduto;
import com.t2tierp.model.bean.tributacao.TributGrupoTributario;
import com.t2tierp.model.bean.tributacao.TributIcmsCustomCab;
import com.t2tierp.model.dao.Filtro;
import com.t2tierp.model.dao.InterfaceDAO;
import com.t2tierp.util.Biblioteca;
import com.t2tierp.util.FacesContextUtil;

@ManagedBean
@ViewScoped
public class ProdutoController extends AbstractController<Produto> implements Serializable {

	private static final long serialVersionUID = 1L;
	@EJB
	private InterfaceDAO<ProdutoSubgrupo> produtoSubGrupoDao;
	@EJB
	private InterfaceDAO<UnidadeProduto> unidadeProdutoDao;
	@EJB
	private InterfaceDAO<ProdutoMarca> produtoMarcaDao;
	@EJB
	private InterfaceDAO<Almoxarifado> almoxarifadoDao;
	@EJB
	private InterfaceDAO<TributIcmsCustomCab> icmsCustomCabDao;
	@EJB
	private InterfaceDAO<TributGrupoTributario> grupoTributarioDao;

	private FichaTecnica fichaTecnica;
	private FichaTecnica fichaTecnicaSelecionado;
	private Produto produtoFichaTecnica;
	
	@Override
	public Class<Produto> getClazz() {
		return Produto.class;
	}

	@Override
	public String getFuncaoBase() {
		return "PRODUTO";
	}

	@Override
	public void incluir() {
		super.incluir();
		getObjeto().setExcluido("N");
		getObjeto().setDataCadastro(new Date());
		
		getObjeto().setListaFichaTecnica(new HashSet<FichaTecnica>());
	}

	@Override
	public void salvar(String mensagem) {
		try {
			if (getObjeto().getTributGrupoTributario() == null && getObjeto().getTributIcmsCustomCab() == null) {
				FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_WARN, "É necesário informar o Grupo Tributário OU o ICMS Customizado.", "O registro não foi salvo.");
			} else {
				List<Filtro> filtros = new ArrayList<>();
				filtros.add(new Filtro(Filtro.AND, "gtin", Filtro.IGUAL, getObjeto().getGtin()));
				if (getObjeto().getId() != null) {
					filtros.add(new Filtro(Filtro.AND, "id", Filtro.DIFERENTE, getObjeto().getId()));
				}
				Produto p = dao.getBean(Produto.class, filtros);
				if (p != null) {
					FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_WARN, "Este GTIN já está sendo utilizado por outro produto.", "O registro não foi salvo.");
				} else {
					System.out.println("GTIN: '" + getObjeto().getGtin() + "'");
					super.salvar(mensagem);
				}
			}
		} catch (Exception e) {
			FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro ao salvar o registro!", e.getMessage());
		}
	}

	public void alteraTributacao() {
		getObjeto().setTributIcmsCustomCab(null);
		getObjeto().setTributGrupoTributario(null);
	}

	public void uploadImagem(FileUploadEvent event) {
		try {
			UploadedFile arquivo = event.getFile();
			String localArquivo = FacesContext.getCurrentInstance().getExternalContext().getApplicationContextPath() + "/imagens" + "/produto/" + getObjeto().getGtin() + arquivo.getFileName().substring(arquivo.getFileName().lastIndexOf("."));
			getObjeto().setImagem(localArquivo);
			localArquivo = FacesContext.getCurrentInstance().getExternalContext().getRealPath(System.getProperty("file.separator") + "imagens") + System.getProperty("file.separator") + "produto" + System.getProperty("file.separator") + getObjeto().getGtin() + arquivo.getFileName().substring(arquivo.getFileName().lastIndexOf("."));
			Biblioteca.copiaArquivo(arquivo.getInputstream(), localArquivo);
		} catch (Exception e) {
			FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro ao realizar o upload do arquivo!", e.getMessage());
			//e.printStackTrace();
		}
	}

	public void incluirFichaTecnica() {
        fichaTecnica = new FichaTecnica();
        fichaTecnica.setProduto(getObjeto());
	}

	public void alterarFichaTecnica() {
        fichaTecnica = fichaTecnicaSelecionado;
	}

	public void salvarFichaTecnica() {
		fichaTecnica.setDescricao(produtoFichaTecnica.getDescricao());
        fichaTecnica.setIdProdutoFilho(produtoFichaTecnica.getId());
        
        if (fichaTecnica.getId() == null) {
            getObjeto().getListaFichaTecnica().add(fichaTecnica);
        }
        salvar("Registro salvo com sucesso!");
	}

	public void excluirFichaTecnica() {
        if (fichaTecnicaSelecionado == null || fichaTecnicaSelecionado.getId() == null) {
            FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_INFO, "Nenhum registro selecionado!", null);
        } else {
            getObjeto().getListaFichaTecnica().remove(fichaTecnicaSelecionado);
            salvar("Registro excluído com sucesso!");
        }
	}
	
	public List<ProdutoSubgrupo> getListaSubGrupo(String nome) {
		List<ProdutoSubgrupo> listaProdutoSubGrupo = new ArrayList<>();
		try {
			listaProdutoSubGrupo = produtoSubGrupoDao.getBeansLike(ProdutoSubgrupo.class, "nome", nome);
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return listaProdutoSubGrupo;
	}

	public List<UnidadeProduto> getListaUnidadeProduto(String sigla) {
		List<UnidadeProduto> listaUnidadeProduto = new ArrayList<>();
		try {
			listaUnidadeProduto = unidadeProdutoDao.getBeansLike(UnidadeProduto.class, "sigla", sigla);
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return listaUnidadeProduto;
	}

	public List<ProdutoMarca> getListaProdutoMarca(String nome) {
		List<ProdutoMarca> listaProdutoMarca = new ArrayList<>();
		try {
			listaProdutoMarca = produtoMarcaDao.getBeansLike(ProdutoMarca.class, "nome", nome);
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return listaProdutoMarca;
	}

	public List<Almoxarifado> getListaAlmoxarifado(String nome) {
		List<Almoxarifado> listaAlmoxarifado = new ArrayList<>();
		try {
			listaAlmoxarifado = almoxarifadoDao.getBeansLike(Almoxarifado.class, "nome", nome);
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return listaAlmoxarifado;
	}

	public List<TributIcmsCustomCab> getListaTributIcmsCustomCab(String descricao) {
		List<TributIcmsCustomCab> listaTributIcmsCustomCab = new ArrayList<>();
		try {
			listaTributIcmsCustomCab = icmsCustomCabDao.getBeansLike(TributIcmsCustomCab.class, "descricao", descricao);
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return listaTributIcmsCustomCab;
	}

	public List<TributGrupoTributario> getListaTributGrupoTributario(String descricao) {
		List<TributGrupoTributario> listaTributGrupoTributario = new ArrayList<>();
		try {
			listaTributGrupoTributario = grupoTributarioDao.getBeansLike(TributGrupoTributario.class, "descricao", descricao);
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return listaTributGrupoTributario;
	}

	public List<Produto> getListaProduto(String nome) {
		List<Produto> listaProduto = new ArrayList<>();
		try {
			listaProduto = dao.getBeansLike(Produto.class, "descricao", nome);
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return listaProduto;
	}
	
	public FichaTecnica getFichaTecnica() {
		return fichaTecnica;
	}

	public void setFichaTecnica(FichaTecnica fichaTecnica) {
		this.fichaTecnica = fichaTecnica;
	}

	public FichaTecnica getFichaTecnicaSelecionado() {
		return fichaTecnicaSelecionado;
	}

	public void setFichaTecnicaSelecionado(FichaTecnica fichaTecnicaSelecionado) {
		this.fichaTecnicaSelecionado = fichaTecnicaSelecionado;
	}

	public Produto getProdutoFichaTecnica() {
		return produtoFichaTecnica;
	}

	public void setProdutoFichaTecnica(Produto produtoFichaTecnica) {
		this.produtoFichaTecnica = produtoFichaTecnica;
	}

}
