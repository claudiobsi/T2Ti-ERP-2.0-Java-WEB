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
package com.t2tierp.controller.ordemservico;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import com.t2tierp.controller.AbstractController;
import com.t2tierp.model.bean.cadastros.Cliente;
import com.t2tierp.model.bean.cadastros.Colaborador;
import com.t2tierp.model.bean.cadastros.Produto;
import com.t2tierp.model.bean.ordemservico.OsAbertura;
import com.t2tierp.model.bean.ordemservico.OsAberturaEquipamento;
import com.t2tierp.model.bean.ordemservico.OsEquipamento;
import com.t2tierp.model.bean.ordemservico.OsEvolucao;
import com.t2tierp.model.bean.ordemservico.OsProdutoServico;
import com.t2tierp.model.bean.ordemservico.OsStatus;
import com.t2tierp.model.dao.InterfaceDAO;
import com.t2tierp.util.Biblioteca;
import com.t2tierp.util.FacesContextUtil;

@ManagedBean
@ViewScoped
public class OsAberturaController extends AbstractController<OsAbertura> implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private InterfaceDAO<OsStatus> osStatusDao;
    @EJB
    private InterfaceDAO<Colaborador> colaboradorDao;
    @EJB
    private InterfaceDAO<Cliente> clienteDao;
    @EJB
    private InterfaceDAO<OsEquipamento> osEquipamentoDao;
    @EJB
    private InterfaceDAO<Produto> produtoDao;
    
	private OsAberturaEquipamento osAberturaEquipamento = new OsAberturaEquipamento();
	private OsAberturaEquipamento osAberturaEquipamentoSelecionado;

	private OsProdutoServico osProdutoServico = new OsProdutoServico();
	private OsProdutoServico osProdutoServicoSelecionado;

	private OsEvolucao osEvolucao;
	private OsEvolucao osEvolucaoSelecionado;

	private List<OsAbertura> osSelecionadas;
	
    @Override
    public Class<OsAbertura> getClazz() {
        return OsAbertura.class;
    }

    @Override
    public String getFuncaoBase() {
        return "OS_ABERTURA";
    }

    @Override
    public void incluir() {
        super.incluir();

        getObjeto().setDataInicio(new Date());
        getObjeto().setHoraInicio(new SimpleDateFormat("HH:mm:ss").format(new Date()));
        
        getObjeto().setListaOsAberturaEquipamento(new HashSet<OsAberturaEquipamento>());
        getObjeto().setListaOsProdutoServico(new HashSet<OsProdutoServico>());
        getObjeto().setListaOsEvolucao(new HashSet<OsEvolucao>());
	}

	@Override
	public void alterar() {
		if (osSelecionadas.size() > 1) {
			FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_WARN, "Selecione somente um registro!", "");
		} else if (osSelecionadas.size() == 1) {
			setObjetoSelecionado(osSelecionadas.get(0));
			super.alterar();
		} else if (osSelecionadas.isEmpty()) {
			FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_WARN, "Nenhum registro selecionado!", "");
		}
	}
    
    @Override
    public void salvar() {
    	super.salvar();
		try {
			if (getObjeto().getId() != null && (getObjeto().getNumero() == null || getObjeto().getNumero().isEmpty())) {
				getObjeto().setNumero("OS" + new DecimalFormat("0000000").format(getObjeto().getId()));
				dao.merge(getObjeto());
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro ao salvar o registro!", e.getMessage());
		}
    }
    
	public void incluirOsAberturaEquipamento() {
        osAberturaEquipamento = new OsAberturaEquipamento();
        osAberturaEquipamento.setOsAbertura(getObjeto());
	}

	public void alterarOsAberturaEquipamento() {
        osAberturaEquipamento = osAberturaEquipamentoSelecionado;
	}

	public void salvarOsAberturaEquipamento() {
        if (osAberturaEquipamento.getId() == null) {
            getObjeto().getListaOsAberturaEquipamento().add(osAberturaEquipamento);
        }
        salvar("Registro salvo com sucesso!");
	}
	public void excluirOsAberturaEquipamento() {
        if (osAberturaEquipamentoSelecionado == null || osAberturaEquipamentoSelecionado.getId() == null) {
            FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_INFO, "Nenhum registro selecionado!", null);
        } else {
            getObjeto().getListaOsAberturaEquipamento().remove(osAberturaEquipamentoSelecionado);
            salvar("Registro excluído com sucesso!");
        }
	}

	public void incluirOsProdutoServico() {
        osProdutoServico = new OsProdutoServico();
        osProdutoServico.setOsAbertura(getObjeto());
	}

	public void alterarOsProdutoServico() {
        osProdutoServico = osProdutoServicoSelecionado;
	}

	public void salvarOsProdutoServico() {
        try {
    		calculaValores(osProdutoServico);
    		if (osProdutoServico.getId() == null) {
                getObjeto().getListaOsProdutoServico().add(osProdutoServico);
            }
            salvar("Registro salvo com sucesso!");
        } catch (Exception e) {
			e.printStackTrace();
			FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro ao salvar o registro!", e.getMessage());
        }
	}
	public void excluirOsProdutoServico() {
        if (osProdutoServicoSelecionado == null || osProdutoServicoSelecionado.getId() == null) {
            FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_INFO, "Nenhum registro selecionado!", null);
        } else {
            getObjeto().getListaOsProdutoServico().remove(osProdutoServicoSelecionado);
            salvar("Registro excluído com sucesso!");
        }
	}

    private void calculaValores(OsProdutoServico produtoServico) throws Exception {
        if (produtoServico.getProduto().getServico() != null && produtoServico.getProduto().getServico().equals("S")) {
            produtoServico.setTipo(1);
        } else {
            produtoServico.setTipo(0);
        }
        produtoServico.setValorUnitario(produtoServico.getProduto().getValorVenda());
        produtoServico.setValorSubtotal(Biblioteca.multiplica(produtoServico.getQuantidade(), produtoServico.getValorUnitario()));
        if (produtoServico.getTaxaDesconto() != null) {
            produtoServico.setValorDesconto(Biblioteca.multiplica(produtoServico.getValorSubtotal(), produtoServico.getTaxaDesconto()));
            produtoServico.setValorTotal(Biblioteca.subtrai(produtoServico.getValorSubtotal(), produtoServico.getValorDesconto()));
        } else {
            produtoServico.setTaxaDesconto(BigDecimal.ZERO);
            produtoServico.setValorDesconto(BigDecimal.ZERO);
            produtoServico.setValorTotal(produtoServico.getValorSubtotal());
        }
    }
	
	public void incluirOsEvolucao() {
        osEvolucao = new OsEvolucao();
        osEvolucao.setOsAbertura(getObjeto());
	}

	public void alterarOsEvolucao() {
        osEvolucao = osEvolucaoSelecionado;
	}

	public void salvarOsEvolucao() {
        if (osEvolucao.getId() == null) {
            getObjeto().getListaOsEvolucao().add(osEvolucao);
        }
        salvar("Registro salvo com sucesso!");
	}
	
	public void excluirOsEvolucao() {
        if (osEvolucaoSelecionado == null || osEvolucaoSelecionado.getId() == null) {
            FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_INFO, "Nenhum registro selecionado!", null);
        } else {
            getObjeto().getListaOsEvolucao().remove(osEvolucaoSelecionado);
            salvar("Registro excluído com sucesso!");
        }
	}

    @SuppressWarnings({ "rawtypes", "unchecked" })
	public void geraOrcamento() throws Exception {
        try {
            Map map = new HashMap();
            map.put("NOME_CLIENTE", getObjeto().getCliente().getPessoa().getNome());
            
            JRBeanCollectionDataSource jrbean = new JRBeanCollectionDataSource(getObjeto().getListaOsProdutoServico());
            JasperPrint jp = JasperFillManager.fillReport(this.getClass().getResourceAsStream("/relatorios/os_orcamento.jasper"), map, jrbean);

            File arquivoPdf = File.createTempFile("orcamento", ".pdf");
            
			OutputStream outPdf = new FileOutputStream(arquivoPdf);
			outPdf.write(JasperExportManager.exportReportToPdf(jp));
			outPdf.close();

			FacesContextUtil.downloadArquivo(arquivoPdf, "orcamento.pdf");
            
        } catch (Exception e) {
			e.printStackTrace();
			FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro!", e.getMessage());
        }
    }
	
    public void mesclarOS() throws Exception {
		try {
			if (osSelecionadas.size() <= 1) {
				FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_WARN, "Necess�rio selecionar 2 ou mais OS!", "");
			} else if (osSelecionadas.size() > 1) {
	            OsAbertura osMesclada = new OsAbertura();
	            osMesclada.setOsStatus(osSelecionadas.get(0).getOsStatus());
	            osMesclada.setColaborador(osSelecionadas.get(0).getColaborador());
	            osMesclada.setCliente(osSelecionadas.get(0).getCliente());
	            osMesclada.setDataInicio(osSelecionadas.get(0).getDataInicio());
	            osMesclada.setDataFim(osSelecionadas.get(0).getDataFim());
	            osMesclada.setDataPrevisao(osSelecionadas.get(0).getDataPrevisao());
	            osMesclada.setFoneContato(osSelecionadas.get(0).getFoneContato());
	            osMesclada.setHoraFim(osSelecionadas.get(0).getHoraFim());
	            osMesclada.setHoraInicio(osSelecionadas.get(0).getHoraInicio());
	            osMesclada.setHoraPrevisao(osSelecionadas.get(0).getHoraPrevisao());
	            osMesclada.setNomeContato(osSelecionadas.get(0).getNomeContato());
	            osMesclada.setObservacaoAbertura(osSelecionadas.get(0).getObservacaoAbertura());
	            osMesclada.setObservacaoCliente(osSelecionadas.get(0).getObservacaoCliente());
	            osMesclada.setListaOsAberturaEquipamento(new HashSet<>());
	            osMesclada.setListaOsEvolucao(new HashSet<>());
	            osMesclada.setListaOsProdutoServico(new HashSet<>());

	            for (OsAbertura a : osSelecionadas) {
	                for (OsEvolucao e : a.getListaOsEvolucao()) {
	                    e.setId(null);
	                    e.setOsAbertura(osMesclada);
	                    osMesclada.getListaOsEvolucao().add(e);
	                }
	                for (OsAberturaEquipamento e : a.getListaOsAberturaEquipamento()) {
	                    e.setId(null);
	                    e.setOsAbertura(osMesclada);
	                    osMesclada.getListaOsAberturaEquipamento().add(e);
	                }
	                for (OsProdutoServico e : a.getListaOsProdutoServico()) {
	                    e.setId(null);
	                    e.setOsAbertura(osMesclada);
	                    osMesclada.getListaOsProdutoServico().add(e);
	                }
	            }

	            dao.persist(osMesclada);
	            
	            osMesclada.setNumero("OS" + new DecimalFormat("0000000").format(osMesclada.getId()));

	            for (OsAbertura a : osSelecionadas) {
	                a.setObservacaoAbertura("MESCLADO PARA OS NR " + osMesclada.getNumero() + "\n" + a.getObservacaoAbertura());
	                dao.merge(a);
	            }

	            osMesclada = dao.merge(osMesclada);
				
	            osSelecionadas.clear();
	            osSelecionadas.add(osMesclada);
	            alterar();
	            
	            FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_INFO, "OS Mescladas!", "");
			}
		} catch (Exception e) {
			e.printStackTrace();
			FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro ao mesclar!", e.getMessage());
		}
    }
    
    public List<OsStatus> getListaOsStatus(String nome) {
        List<OsStatus> listaOsStatus = new ArrayList<>();
        try {
            listaOsStatus = osStatusDao.getBeansLike(OsStatus.class, "nome", nome);
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return listaOsStatus;
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

    public List<Cliente> getListaCliente(String nome) {
        List<Cliente> listaCliente = new ArrayList<>();
        try {
            listaCliente = clienteDao.getBeansLike(Cliente.class, "pessoa.nome", nome);
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return listaCliente;
    }
	
    public List<OsEquipamento> getListaOsEquipamento(String nome) {
        List<OsEquipamento> listaOsEquipamento = new ArrayList<>();
        try {
            listaOsEquipamento = osEquipamentoDao.getBeansLike(OsEquipamento.class, "nome", nome);
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return listaOsEquipamento;
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
    
    public OsAberturaEquipamento getOsAberturaEquipamento() {
        return osAberturaEquipamento;
    }

    public void setOsAberturaEquipamento(OsAberturaEquipamento osAberturaEquipamento) {
        this.osAberturaEquipamento = osAberturaEquipamento;
    }

    public OsProdutoServico getOsProdutoServico() {
        return osProdutoServico;
    }

    public void setOsProdutoServico(OsProdutoServico osProdutoServico) {
        this.osProdutoServico = osProdutoServico;
    }

    public OsEvolucao getOsEvolucao() {
        return osEvolucao;
    }

    public void setOsEvolucao(OsEvolucao osEvolucao) {
        this.osEvolucao = osEvolucao;
    }

	public OsAberturaEquipamento getOsAberturaEquipamentoSelecionado() {
		return osAberturaEquipamentoSelecionado;
	}

	public void setOsAberturaEquipamentoSelecionado(OsAberturaEquipamento osAberturaEquipamentoSelecionado) {
		this.osAberturaEquipamentoSelecionado = osAberturaEquipamentoSelecionado;
	}

	public OsProdutoServico getOsProdutoServicoSelecionado() {
		return osProdutoServicoSelecionado;
	}

	public void setOsProdutoServicoSelecionado(OsProdutoServico osProdutoServicoSelecionado) {
		this.osProdutoServicoSelecionado = osProdutoServicoSelecionado;
	}

	public OsEvolucao getOsEvolucaoSelecionado() {
		return osEvolucaoSelecionado;
	}

	public void setOsEvolucaoSelecionado(OsEvolucao osEvolucaoSelecionado) {
		this.osEvolucaoSelecionado = osEvolucaoSelecionado;
	}

	public List<OsAbertura> getOsSelecionadas() {
		return osSelecionadas;
	}

	public void setOsSelecionadas(List<OsAbertura> osSelecionadas) {
		this.osSelecionadas = osSelecionadas;
	}


}
