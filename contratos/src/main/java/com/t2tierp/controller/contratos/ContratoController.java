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
package com.t2tierp.controller.contratos;

import java.io.File;
import java.io.Serializable;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.swing.text.MaskFormatter;

import com.t2tierp.controller.AbstractController;
import com.t2tierp.model.bean.cadastros.Empresa;
import com.t2tierp.model.bean.cadastros.EmpresaEndereco;
import com.t2tierp.model.bean.contratos.Contrato;
import com.t2tierp.model.bean.contratos.ContratoHistFaturamento;
import com.t2tierp.model.bean.contratos.ContratoHistoricoReajuste;
import com.t2tierp.model.bean.contratos.ContratoPrevFaturamento;
import com.t2tierp.model.bean.contratos.ContratoSolicitacaoServico;
import com.t2tierp.model.bean.contratos.ContratoTemplate;
import com.t2tierp.model.bean.contratos.TipoContrato;
import com.t2tierp.model.bean.contratos.ViewContratoDadosContratante;
import com.t2tierp.model.dao.InterfaceDAO;
import com.t2tierp.util.FacesContextUtil;

@ManagedBean
@ViewScoped
public class ContratoController extends AbstractController<Contrato> implements Serializable {

    private static final long serialVersionUID = 1L;
    @EJB
    private InterfaceDAO<ContratoSolicitacaoServico> solicitacaoServicoDao;
    @EJB
    private InterfaceDAO<TipoContrato> tipoContratoDao;
    @EJB
    private InterfaceDAO<ContratoTemplate> contratoTemplateDao;
    @EJB
    private InterfaceDAO<ViewContratoDadosContratante> dadosContratanteDao;

	private ContratoHistoricoReajuste contratoHistoricoReajuste;
	private ContratoHistoricoReajuste contratoHistoricoReajusteSelecionado;

	private ContratoPrevFaturamento contratoPrevFaturamento;
	private ContratoPrevFaturamento contratoPrevFaturamentoSelecionado;

	private ContratoHistFaturamento contratoHistFaturamento;
	private ContratoHistFaturamento contratoHistFaturamentoSelecionado;
	
	private ContratoTemplate template;
	private String conteudoTemplate;
	private String contrato;
	
	private final String diretorioTemplate = "modulos/contratos/template/";

    @Override
    public Class<Contrato> getClazz() {
        return Contrato.class;
    }

    @Override
    public String getFuncaoBase() {
        return "CONTRATO";
    }

    @Override
    public void incluir() {
        super.incluir();

        getObjeto().setListaContratoHistoricoReajuste(new HashSet<ContratoHistoricoReajuste>());
        getObjeto().setListaContratoPrevFaturamento(new HashSet<ContratoPrevFaturamento>());
        getObjeto().setListaContratoHistFaturamento(new HashSet<ContratoHistFaturamento>());
	}
    
    @Override
    public void salvar() {
    	try {
    		conteudoTemplate = new String(Files.readAllBytes(Paths.get(getURIArquivo())), "UTF-8");    		
    		
    		Empresa empresa = FacesContextUtil.getEmpresaUsuario();
    		EmpresaEndereco enderecoEmpresa = new EmpresaEndereco();
    		for (EmpresaEndereco e : empresa.getListaEndereco()) {
    			if (e.getPrincipal().equals("S")) {
    				enderecoEmpresa = e;
    			}
    		}
            
    		ViewContratoDadosContratante dadosContratante = dadosContratanteDao.getBean(ViewContratoDadosContratante.class, "idSolicitacao", getObjeto().getContratoSolicitacaoServico().getId());
    		
    		MaskFormatter formatoCnpj = new MaskFormatter("##.###.###/####-##");
            formatoCnpj.setValueContainsLiteralCharacters(false);
            MaskFormatter formatoCpf = new MaskFormatter("###.###.###-##");
            formatoCpf.setValueContainsLiteralCharacters(false);
            SimpleDateFormat formatoDataExtenso = new SimpleDateFormat("EEEE, dd 'de' MMMM 'de' yyyy");
            DecimalFormat formatoDecimal = new DecimalFormat("#,###,##0.00");
    		
            //define os termos a serem substituidos
            String termos[] = new String[]{
                //contratada
                "#CONTRATADA#",
                "#CNPJ_CONTRATADA#",
                "#ENDERECO_CONTRATADA#",
                "#CIDADE_CONTRATADA#",
                "#UF_CONTRATADA#",
                "#BAIRRO_CONTRATADA#",
                "#CEP_CONTRATADA#",
                //contratante
                "#CONTRATANTE#",
                "#CNPJ_CONTRATANTE#",
                "#ENDERECO_CONTRATANTE#",
                "#CIDADE_CONTRATANTE#",
                "#UF_CONTRATANTE#",
                "#BAIRRO_CONTRATANTE#",
                "#CEP_CONTRATANTE#",
                //outros
                "#VALOR_MENSAL#",
                "#DATA_EXTENSO#"
            };

            //busca os dados para substituicoes dos termos
            String substituicoes[] = new String[]{
                //contratada
                empresa.getRazaoSocial(),
                formatoCnpj.valueToString(empresa.getCnpj()),
                enderecoEmpresa.getLogradouro() + " " + enderecoEmpresa.getNumero() + " " + (enderecoEmpresa.getComplemento() == null ? "" : enderecoEmpresa.getComplemento()),
                enderecoEmpresa.getCidade(),
                enderecoEmpresa.getUf(),
                enderecoEmpresa.getBairro(),
                enderecoEmpresa.getCep(),
                //contratante
                dadosContratante.getNome(),
                dadosContratante.getCpfCnpj().length() == 11 ? formatoCpf.valueToString(dadosContratante.getCpfCnpj()) : formatoCnpj.valueToString(dadosContratante.getCpfCnpj()),
                dadosContratante.getLogradouro() + " " + dadosContratante.getNumero() + " " + (dadosContratante.getComplemento() == null ? "" : dadosContratante.getComplemento()),
                dadosContratante.getCidade(),
                dadosContratante.getUf(),
                dadosContratante.getBairro(),
                dadosContratante.getCep(),
                //outros
                formatoDecimal.format(getObjeto().getValor()),
                formatoDataExtenso.format(getObjeto().getDataInicioVigencia())
            };
            
            for (int i = 0; i < termos.length; i++) {
            	conteudoTemplate = conteudoTemplate.replaceAll(termos[i], substituicoes[i]);
            }
            contrato = conteudoTemplate;
            
            super.salvar();
    	} catch (Exception e) {
    		FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro!", e.getMessage());
    	}
    }

    private URI getURIArquivo() throws Exception {
		ExternalContext context = FacesContext.getCurrentInstance().getExternalContext();
		File file = new File(context.getRealPath(diretorioTemplate) + System.getProperty("file.separator") + template.getId() + ".rtf");
		return file.toURI();
    }    
    
	public void incluirContratoHistoricoReajuste() {
        contratoHistoricoReajuste = new ContratoHistoricoReajuste();
        contratoHistoricoReajuste.setContrato(getObjeto());
	}

	public void alterarContratoHistoricoReajuste() {
        contratoHistoricoReajuste = contratoHistoricoReajusteSelecionado;
	}

	public void salvarContratoHistoricoReajuste() {
        if (contratoHistoricoReajuste.getId() == null) {
            getObjeto().getListaContratoHistoricoReajuste().add(contratoHistoricoReajuste);
        }
        salvar("Registro salvo com sucesso!");
	}
	
	public void excluirContratoHistoricoReajuste() {
        if (contratoHistoricoReajusteSelecionado == null || contratoHistoricoReajusteSelecionado.getId() == null) {
            FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_INFO, "Nenhum registro selecionado!", null);
        } else {
            getObjeto().getListaContratoHistoricoReajuste().remove(contratoHistoricoReajusteSelecionado);
            salvar("Registro excluído com sucesso!");
        }
	}

	public void incluirContratoPrevFaturamento() {
        contratoPrevFaturamento = new ContratoPrevFaturamento();
        contratoPrevFaturamento.setContrato(getObjeto());
	}

	public void alterarContratoPrevFaturamento() {
        contratoPrevFaturamento = contratoPrevFaturamentoSelecionado;
	}

	public void salvarContratoPrevFaturamento() {
        if (contratoPrevFaturamento.getId() == null) {
            getObjeto().getListaContratoPrevFaturamento().add(contratoPrevFaturamento);
        }
        salvar("Registro salvo com sucesso!");
	}
	
	public void excluirContratoPrevFaturamento() {
        if (contratoPrevFaturamentoSelecionado == null || contratoPrevFaturamentoSelecionado.getId() == null) {
            FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_INFO, "Nenhum registro selecionado!", null);
        } else {
            getObjeto().getListaContratoPrevFaturamento().remove(contratoPrevFaturamentoSelecionado);
            salvar("Registro excluído com sucesso!");
        }
	}

	public void incluirContratoHistFaturamento() {
        contratoHistFaturamento = new ContratoHistFaturamento();
        contratoHistFaturamento.setContrato(getObjeto());
	}

	public void alterarContratoHistFaturamento() {
        contratoHistFaturamento = contratoHistFaturamentoSelecionado;
	}

	public void salvarContratoHistFaturamento() {
        if (contratoHistFaturamento.getId() == null) {
            getObjeto().getListaContratoHistFaturamento().add(contratoHistFaturamento);
        }
        salvar("Registro salvo com sucesso!");
	}
	
	public void excluirContratoHistFaturamento() {
        if (contratoHistFaturamentoSelecionado == null || contratoHistFaturamentoSelecionado.getId() == null) {
            FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_INFO, "Nenhum registro selecionado!", null);
        } else {
            getObjeto().getListaContratoHistFaturamento().remove(contratoHistFaturamentoSelecionado);
            salvar("Registro excluído com sucesso!");
        }
	}

    public List<ContratoSolicitacaoServico> getListaSolicitacaoServico(String nome) {
        List<ContratoSolicitacaoServico> listaSolicitacaoServico = new ArrayList<>();
        try {
            listaSolicitacaoServico = solicitacaoServicoDao.getBeansLike(ContratoSolicitacaoServico.class, "descricao", nome);
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return listaSolicitacaoServico;
    }

    public List<TipoContrato> getListaTipoContrato(String nome) {
        List<TipoContrato> listaTipoContrato = new ArrayList<>();
        try {
            listaTipoContrato = tipoContratoDao.getBeansLike(TipoContrato.class, "nome", nome);
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return listaTipoContrato;
    }

    public List<ContratoTemplate> getListaContratoTemplate(String nome) {
        List<ContratoTemplate> listaContratoTemplate = new ArrayList<>();
        try {
            listaContratoTemplate = contratoTemplateDao.getBeansLike(ContratoTemplate.class, "nome", nome);
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return listaContratoTemplate;
    }
    
    public ContratoHistoricoReajuste getContratoHistoricoReajuste() {
        return contratoHistoricoReajuste;
    }

    public void setContratoHistoricoReajuste(ContratoHistoricoReajuste contratoHistoricoReajuste) {
        this.contratoHistoricoReajuste = contratoHistoricoReajuste;
    }

    public ContratoPrevFaturamento getContratoPrevFaturamento() {
        return contratoPrevFaturamento;
    }

    public void setContratoPrevFaturamento(ContratoPrevFaturamento contratoPrevFaturamento) {
        this.contratoPrevFaturamento = contratoPrevFaturamento;
    }

    public ContratoHistFaturamento getContratoHistFaturamento() {
        return contratoHistFaturamento;
    }

    public void setContratoHistFaturamento(ContratoHistFaturamento contratoHistFaturamento) {
        this.contratoHistFaturamento = contratoHistFaturamento;
    }

	public ContratoHistoricoReajuste getContratoHistoricoReajusteSelecionado() {
		return contratoHistoricoReajusteSelecionado;
	}

	public void setContratoHistoricoReajusteSelecionado(ContratoHistoricoReajuste contratoHistoricoReajusteSelecionado) {
		this.contratoHistoricoReajusteSelecionado = contratoHistoricoReajusteSelecionado;
	}

	public ContratoPrevFaturamento getContratoPrevFaturamentoSelecionado() {
		return contratoPrevFaturamentoSelecionado;
	}

	public void setContratoPrevFaturamentoSelecionado(ContratoPrevFaturamento contratoPrevFaturamentoSelecionado) {
		this.contratoPrevFaturamentoSelecionado = contratoPrevFaturamentoSelecionado;
	}

	public ContratoHistFaturamento getContratoHistFaturamentoSelecionado() {
		return contratoHistFaturamentoSelecionado;
	}

	public void setContratoHistFaturamentoSelecionado(ContratoHistFaturamento contratoHistFaturamentoSelecionado) {
		this.contratoHistFaturamentoSelecionado = contratoHistFaturamentoSelecionado;
	}

	public ContratoTemplate getTemplate() {
		return template;
	}

	public void setTemplate(ContratoTemplate template) {
		this.template = template;
	}

	public String getContrato() {
		return contrato;
	}

	public void setContrato(String contrato) {
		this.contrato = contrato;
	}


}
