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
package com.t2tierp.controller.pcp;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import com.t2tierp.controller.AbstractController;
import com.t2tierp.model.bean.cadastros.Colaborador;
import com.t2tierp.model.bean.cadastros.Produto;
import com.t2tierp.model.bean.patrimonio.PatrimBem;
import com.t2tierp.model.bean.pcp.PcpInstrucao;
import com.t2tierp.model.bean.pcp.PcpInstrucaoOp;
import com.t2tierp.model.bean.pcp.PcpOpCabecalho;
import com.t2tierp.model.bean.pcp.PcpOpDetalhe;
import com.t2tierp.model.bean.pcp.PcpServico;
import com.t2tierp.model.bean.pcp.PcpServicoColaborador;
import com.t2tierp.model.bean.pcp.PcpServicoEquipamento;
import com.t2tierp.model.dao.InterfaceDAO;
import com.t2tierp.util.FacesContextUtil;

@ManagedBean
@ViewScoped
public class PcpOpCabecalhoController extends AbstractController<PcpOpCabecalho> implements Serializable {

    private static final long serialVersionUID = 1L;
    @EJB
    private InterfaceDAO<Produto> produtoDao;
    @EJB
    private InterfaceDAO<Colaborador> colaboradorDao;
    @EJB
    private InterfaceDAO<PatrimBem> patrimBemDao;
    @EJB
    private InterfaceDAO<PcpInstrucao> pcpInstrucaoDao;
    @EJB
    private InterfaceDAO<PcpServico> pcpServicoDao;
    @EJB
    private InterfaceDAO<PcpOpDetalhe> pcpOpDetalheDao;
    
    
	private PcpOpDetalhe pcpOpDetalhe;
	private PcpOpDetalhe pcpOpDetalheSelecionado = new PcpOpDetalhe();

	private PcpServico pcpServico;
	private PcpServico pcpServicoSelecionado = new PcpServico();

	private PcpServicoColaborador pcpServicoColaborador;
	private PcpServicoColaborador pcpServicoColaboradorSelecionado;

	private PcpInstrucaoOp pcpInstrucaoOp;
	private PcpInstrucaoOp pcpInstrucaoOpSelecionado;

	private PcpServicoEquipamento pcpServicoEquipamento;
	private PcpServicoEquipamento pcpServicoEquipamentoSelecionado;

    @Override
    public Class<PcpOpCabecalho> getClazz() {
        return PcpOpCabecalho.class;
    }

    @Override
    public String getFuncaoBase() {
        return "PCP_OP_CABECALHO";
    }

    @Override
    public void incluir() {
        super.incluir();
        getObjeto().setEmpresa(FacesContextUtil.getEmpresaUsuario());
        getObjeto().setListaPcpOpDetalhe(new HashSet<>());
        getObjeto().setListaPcpInstrucaoOp(new HashSet<>());
	}

	public void incluirPcpOpDetalhe() {
        pcpOpDetalhe = new PcpOpDetalhe();
        pcpOpDetalhe.setPcpOpCabecalho(getObjeto());
	}

	public void alterarPcpOpDetalhe() {
        pcpOpDetalhe = pcpOpDetalheSelecionado;
	}

	public void salvarPcpOpDetalhe() {
        if (pcpOpDetalhe.getId() == null) {
            getObjeto().getListaPcpOpDetalhe().add(pcpOpDetalhe);
        }
        salvar("Registro salvo com sucesso!");
	}
	
	public void excluirPcpOpDetalhe() {
        if (pcpOpDetalheSelecionado == null || pcpOpDetalheSelecionado.getId() == null) {
            FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_INFO, "Nenhum registro selecionado!", "");
        } else {
            getObjeto().getListaPcpOpDetalhe().remove(pcpOpDetalheSelecionado);
            salvar("Registro excluído com sucesso!");
        }
	}

	public void incluirPcpServico() {
        if (pcpOpDetalheSelecionado != null) {
    		pcpServico = new PcpServico();
            pcpServico.setPcpOpDetalhe(pcpOpDetalheSelecionado);
        } else {
        	FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_WARN, "Necessário selecionar um produto!", "");
        }
	}

	public void alterarPcpServico() {
        pcpServico = pcpServicoSelecionado;
	}

	public void salvarPcpServico() {
        try {
    		pcpServico = pcpServicoDao.merge(pcpServico);
    		pcpOpDetalheSelecionado = pcpOpDetalheDao.getBean(pcpServico.getPcpOpDetalhe().getId(), PcpOpDetalhe.class);
            FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_INFO, "Registro salvo com sucesso!", "");
        } catch(Exception e) {
        	e.printStackTrace();
        	FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorre um erro!", e.getMessage());
        }
	}

	public void excluirPcpServico() {
        try {
            if (pcpServicoSelecionado == null || pcpServicoSelecionado.getId() == null) {
                FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_INFO, "Nenhum registro selecionado!", "");
            } else {
            	pcpServicoSelecionado.getPcpOpDetalhe().getListaPcpServico().remove(pcpServicoSelecionado);
            	pcpServicoDao.excluir(pcpServicoSelecionado, pcpServicoSelecionado.getId());
                FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_INFO, "Registro excluído com sucesso!", "");
            }
        } catch(Exception e) {
        	e.printStackTrace();
        	FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorre um erro!", e.getMessage());
        }
	}

	public void incluirPcpServicoColaborador() {
        if (pcpServicoSelecionado != null) {
            pcpServicoColaborador = new PcpServicoColaborador();
            pcpServicoColaborador.setPcpServico(pcpServicoSelecionado);
        } else {
        	FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_WARN, "Necessário selecionar um serviço!", "");
        }
	}

	public void alterarPcpServicoColaborador() {
        pcpServicoColaborador = pcpServicoColaboradorSelecionado;
	}

	public void salvarPcpServicoColaborador() {
        try {
            if (pcpServicoColaborador.getId() == null) {
            	pcpServicoColaborador.getPcpServico().getListaPcpServicoColaborador().add(pcpServicoColaborador);
            }
    		pcpServicoSelecionado = pcpServicoDao.merge(pcpServicoColaborador.getPcpServico());
            FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_INFO, "Registro salvo com sucesso!", "");
        } catch(Exception e) {
        	e.printStackTrace();
        	FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorre um erro!", e.getMessage());
        }
	}
	
	public void excluirPcpServicoColaborador() {
        try {
            if (pcpServicoColaboradorSelecionado == null || pcpServicoColaboradorSelecionado.getId() == null) {
                FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_INFO, "Nenhum registro selecionado!", "");
            } else {
            	pcpServicoColaboradorSelecionado.getPcpServico().getListaPcpServicoColaborador().remove(pcpServicoColaboradorSelecionado);
            	pcpServicoSelecionado = pcpServicoDao.merge(pcpServicoColaboradorSelecionado.getPcpServico());
                FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_INFO, "Registro excluído com sucesso!", "");
            }
        } catch(Exception e) {
        	e.printStackTrace();
        	FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorre um erro!", e.getMessage());
        }
	}

	public void incluirPcpServicoEquipamento() {
        if (pcpServicoSelecionado != null) {
            pcpServicoEquipamento = new PcpServicoEquipamento();
            pcpServicoEquipamento.setPcpServico(pcpServicoSelecionado);
        } else {
        	FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_WARN, "Necessário selecionar um serviço!", "");
        }
	}

	public void alterarPcpServicoEquipamento() {
        pcpServicoEquipamento = pcpServicoEquipamentoSelecionado;
	}

	public void salvarPcpServicoEquipamento() {
        try {
            if (pcpServicoEquipamento.getId() == null) {
            	pcpServicoEquipamento.getPcpServico().getListaPcpServicoEquipamento().add(pcpServicoEquipamento);
            }
            pcpServicoSelecionado = pcpServicoDao.merge(pcpServicoEquipamento.getPcpServico());
            FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_INFO, "Registro salvo com sucesso!", "");
        } catch(Exception e) {
        	e.printStackTrace();
        	FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorre um erro!", e.getMessage());
        }
	}
	
	public void excluirPcpServicoEquipamento() {
        try {
            if (pcpServicoEquipamentoSelecionado == null || pcpServicoEquipamentoSelecionado.getId() == null) {
                FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_INFO, "Nenhum registro selecionado!", "");
            } else {
            	pcpServicoEquipamentoSelecionado.getPcpServico().getListaPcpServicoColaborador().remove(pcpServicoEquipamentoSelecionado);
            	pcpServicoSelecionado = pcpServicoDao.merge(pcpServicoEquipamentoSelecionado.getPcpServico());
                FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_INFO, "Registro excluído com sucesso!", "");
            }
        } catch(Exception e) {
        	e.printStackTrace();
        	FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorre um erro!", e.getMessage());
        }
	}
	
	public void incluirPcpInstrucaoOp() {
        pcpInstrucaoOp = new PcpInstrucaoOp();
        pcpInstrucaoOp.setPcpOpCabecalho(getObjeto());
	}

	public void alterarPcpInstrucaoOp() {
        pcpInstrucaoOp = pcpInstrucaoOpSelecionado;
	}

	public void salvarPcpInstrucaoOp() {
        if (pcpInstrucaoOp.getId() == null) {
            getObjeto().getListaPcpInstrucaoOp().add(pcpInstrucaoOp);
        }
        salvar("Registro salvo com sucesso!");
	}
	
	public void excluirPcpInstrucaoOp() {
        if (pcpInstrucaoOpSelecionado == null || pcpInstrucaoOpSelecionado.getId() == null) {
            FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_INFO, "Nenhum registro selecionado!", "");
        } else {
            getObjeto().getListaPcpInstrucaoOp().remove(pcpInstrucaoOpSelecionado);
            salvar("Registro excluído com sucesso!");
        }
	}

    public List<Produto> getListaProduto(String nome) {
        List<Produto> listaProduto = new ArrayList<>();
        try {
            listaProduto = produtoDao.getBeansLike(Produto.class, "nome", nome);
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return listaProduto;
    }

    public List<Colaborador> getListaColaborador(String nome) {
        List<Colaborador> listaColaborador = new ArrayList<>();
        try {
            listaColaborador = colaboradorDao.getBeansLike(Colaborador.class, "pessoa.nome", nome);
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return listaColaborador;
    }    

    public List<PatrimBem> getListaPatrimBem(String nome) {
        List<PatrimBem> listaPatrimBem = new ArrayList<>();
        try {
            listaPatrimBem = patrimBemDao.getBeansLike(PatrimBem.class, "nome", nome);
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return listaPatrimBem;
    }
    
    public List<PcpInstrucao> getListaPcpInstrucao(String nome) {
        List<PcpInstrucao> listaPcpInstrucao = new ArrayList<>();
        try {
            listaPcpInstrucao = pcpInstrucaoDao.getBeansLike(PcpInstrucao.class, "descricao", nome);
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return listaPcpInstrucao;
    }    
    
    public PcpOpDetalhe getPcpOpDetalhe() {
        return pcpOpDetalhe;
    }

    public void setPcpOpDetalhe(PcpOpDetalhe pcpOpDetalhe) {
        this.pcpOpDetalhe = pcpOpDetalhe;
    }

    public PcpServico getPcpServico() {
        return pcpServico;
    }

    public void setPcpServico(PcpServico pcpServico) {
        this.pcpServico = pcpServico;
    }

    public PcpServicoColaborador getPcpServicoColaborador() {
        return pcpServicoColaborador;
    }

    public void setPcpServicoColaborador(PcpServicoColaborador pcpServicoColaborador) {
        this.pcpServicoColaborador = pcpServicoColaborador;
    }

    public PcpInstrucaoOp getPcpInstrucaoOp() {
        return pcpInstrucaoOp;
    }

    public void setPcpInstrucaoOp(PcpInstrucaoOp pcpInstrucaoOp) {
        this.pcpInstrucaoOp = pcpInstrucaoOp;
    }

    public PcpServicoEquipamento getPcpServicoEquipamento() {
        return pcpServicoEquipamento;
    }

    public PcpOpDetalhe getPcpOpDetalheSelecionado() {
		return pcpOpDetalheSelecionado;
	}

	public void setPcpOpDetalheSelecionado(PcpOpDetalhe pcpOpDetalheSelecionado) {
		this.pcpOpDetalheSelecionado = pcpOpDetalheSelecionado;
	}

	public PcpServico getPcpServicoSelecionado() {
		return pcpServicoSelecionado;
	}

	public void setPcpServicoSelecionado(PcpServico pcpServicoSelecionado) {
		this.pcpServicoSelecionado = pcpServicoSelecionado;
	}

	public PcpServicoColaborador getPcpServicoColaboradorSelecionado() {
		return pcpServicoColaboradorSelecionado;
	}

	public void setPcpServicoColaboradorSelecionado(PcpServicoColaborador pcpServicoColaboradorSelecionado) {
		this.pcpServicoColaboradorSelecionado = pcpServicoColaboradorSelecionado;
	}

	public PcpInstrucaoOp getPcpInstrucaoOpSelecionado() {
		return pcpInstrucaoOpSelecionado;
	}

	public void setPcpInstrucaoOpSelecionado(PcpInstrucaoOp pcpInstrucaoOpSelecionado) {
		this.pcpInstrucaoOpSelecionado = pcpInstrucaoOpSelecionado;
	}

	public PcpServicoEquipamento getPcpServicoEquipamentoSelecionado() {
		return pcpServicoEquipamentoSelecionado;
	}

	public void setPcpServicoEquipamentoSelecionado(PcpServicoEquipamento pcpServicoEquipamentoSelecionado) {
		this.pcpServicoEquipamentoSelecionado = pcpServicoEquipamentoSelecionado;
	}

	public void setPcpServicoEquipamento(PcpServicoEquipamento pcpServicoEquipamento) {
        this.pcpServicoEquipamento = pcpServicoEquipamento;
    }


}
