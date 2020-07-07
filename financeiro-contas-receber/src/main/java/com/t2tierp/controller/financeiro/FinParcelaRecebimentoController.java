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
package com.t2tierp.controller.financeiro;

import java.io.Serializable;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.primefaces.event.SelectEvent;

import com.t2tierp.controller.AbstractController;
import com.t2tierp.model.bean.administrativo.AdmParametro;
import com.t2tierp.model.bean.cadastros.Cheque;
import com.t2tierp.model.bean.cadastros.ContaCaixa;
import com.t2tierp.model.bean.cadastros.Pessoa;
import com.t2tierp.model.bean.cadastros.PessoaCliente;
import com.t2tierp.model.bean.financeiro.FinChequeRecebido;
import com.t2tierp.model.bean.financeiro.FinParcelaReceber;
import com.t2tierp.model.bean.financeiro.FinParcelaRecebimento;
import com.t2tierp.model.bean.financeiro.FinStatusParcela;
import com.t2tierp.model.bean.financeiro.FinTipoRecebimento;
import com.t2tierp.model.dao.Filtro;
import com.t2tierp.model.dao.InterfaceDAO;
import com.t2tierp.util.FacesContextUtil;

@ManagedBean
@ViewScoped
public class FinParcelaRecebimentoController extends AbstractController<FinParcelaReceber> implements Serializable {

    private static final long serialVersionUID = 1L;

    @EJB
    private InterfaceDAO<FinTipoRecebimento> finTipoRecebimentoDao;
	@EJB
	private InterfaceDAO<ContaCaixa> contaCaixaDao;
	@EJB
	private InterfaceDAO<AdmParametro> admParametroDao;
	@EJB
	private InterfaceDAO<Cheque> chequeDao;
    @EJB
    private InterfaceDAO<FinChequeRecebido> finChequeRecebidoDao;
	@EJB
	private InterfaceDAO<FinStatusParcela> finStatusParcelaDao;
    @EJB
    private InterfaceDAO<PessoaCliente> pessoaDao;

	private FinParcelaRecebimento finParcelaRecebimento;
	private FinParcelaRecebimento finParcelaRecebimentoSelecionado;

	private FinChequeRecebido finChequeRecebido;
	
    private String strTipoBaixa;
    private List<FinParcelaReceber> parcelasSelecionadas;
	private boolean recebimentoCheque;
    
	private PessoaCliente pessoaCliente;
	
    @Override
    public Class<FinParcelaReceber> getClazz() {
        return FinParcelaReceber.class;
    }

    @Override
    public String getFuncaoBase() {
        return "FIN_PARCELA_RECEBIMENTO";
    }

	@Override
	public void alterar() {
		if (parcelasSelecionadas != null) {
			if (parcelasSelecionadas.isEmpty()) {
				FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_WARN, "Nenhuma parcela foi selecionada!", "");
			} else if (parcelasSelecionadas.size() == 1) {
				setObjetoSelecionado(parcelasSelecionadas.get(0));
				super.alterar();
				novoRecebimento();
			} else if (parcelasSelecionadas.size() > 1) {
				iniciaRecebimentoCheque();
			}
		}
	}
    
	private void novoRecebimento() {
		finParcelaRecebimento = new FinParcelaRecebimento();
		finParcelaRecebimento.setFinParcelaReceber(getObjeto());
		finParcelaRecebimento.setDataRecebimento(new Date());

		pessoaCliente = new PessoaCliente();
		
		strTipoBaixa = "T";
	}
    
	public void iniciaRecebimentoCheque() {
		Date dataAtual = new Date();
		BigDecimal totalParcelas = BigDecimal.ZERO;
		finChequeRecebido = new FinChequeRecebido();
		if (parcelasSelecionadas.size() > 1) {
			for (FinParcelaReceber p : parcelasSelecionadas) {
				if (p.getFinStatusParcela().getSituacao().equals("02")) {
					FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_WARN, "Foi selecionado parcela já quitada.", "Recebimento não realizado.");
					return;
				}
				if (p.getDataVencimento().before(dataAtual)) {
					FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_WARN, "Foi selecionado parcela já vencida.", "Recebimento não realizado.");
					return;
				}
				if (p.getValor() != null) {
					totalParcelas = totalParcelas.add(p.getValor());
				}
			}
			finChequeRecebido.setValor(totalParcelas);
		}
		recebimentoCheque = true;
	}
	
	public void cancelaRecebimentoCheque() {
		recebimentoCheque = false;
		pessoaCliente = new PessoaCliente();
	}

	public void finalizaRecebimentoCheque() {
		recebimentoCheque = false;
		incluirRecebimento();
	}
	
	public void iniciaRecebimento() {
		if (finParcelaRecebimento.getFinTipoRecebimento().getTipo().equals("02")) {
			iniciaRecebimentoCheque();
		} else {
			incluirRecebimento();
		}
	}
	
	private void incluirRecebimento() {
		try {
			List<Filtro> filtros = new ArrayList<>();
			filtros.add(new Filtro(Filtro.AND, "empresa", Filtro.IGUAL, FacesContextUtil.getEmpresaUsuario()));
			AdmParametro admParametro = admParametroDao.getBean(AdmParametro.class, filtros);

			FinStatusParcela statusParcela = null;
			if (admParametro == null) {
				throw new Exception("Parâmetros administrativos não encontrados. Entre em contato com a Software House.");
			}
			statusParcela = finStatusParcelaDao.getBean(admParametro.getFinParcelaQuitado(), FinStatusParcela.class);
			if (statusParcela == null) {
				throw new Exception("O status de parcela 'Quitado' não está cadastrado.\nEntre em contato com a Software House.");
			}

			if (parcelasSelecionadas.size() == 1 && !finParcelaRecebimento.getFinTipoRecebimento().getTipo().equals("02")) {
				calculaTotalRecebido();

				FinParcelaRecebimento recebimento = new FinParcelaRecebimento();
				recebimento.setFinParcelaReceber(finParcelaRecebimento.getFinParcelaReceber());
				recebimento.setContaCaixa(finParcelaRecebimento.getContaCaixa());
				recebimento.setDataRecebimento(finParcelaRecebimento.getDataRecebimento());
				recebimento.setFinTipoRecebimento(finParcelaRecebimento.getFinTipoRecebimento());
				recebimento.setHistorico(finParcelaRecebimento.getHistorico());
				recebimento.setTaxaDesconto(finParcelaRecebimento.getTaxaDesconto());
				recebimento.setTaxaJuro(finParcelaRecebimento.getTaxaJuro());
				recebimento.setTaxaMulta(finParcelaRecebimento.getTaxaMulta());
				recebimento.setValorDesconto(finParcelaRecebimento.getValorDesconto());
				recebimento.setValorJuro(finParcelaRecebimento.getValorJuro());
				recebimento.setValorMulta(finParcelaRecebimento.getValorMulta());
				recebimento.setValorRecebido(finParcelaRecebimento.getValorRecebido());

				if (strTipoBaixa.equals("P")) {
					statusParcela = finStatusParcelaDao.getBean(admParametro.getFinParcelaQuitadoParcial(), FinStatusParcela.class);
					if (statusParcela == null) {
						throw new Exception("O status de parcela 'Quitado Parcial' não está cadastrado.\nEntre em contato com a Software House.");
					}
				}

				getObjeto().setFinStatusParcela(statusParcela);
				getObjeto().getListaFinParcelaRecebimento().add(recebimento);
				salvar("Recebimento incluído com sucesso!");
				novoRecebimento();
			} else {
				filtros = new ArrayList<>();
				filtros.add(new Filtro(Filtro.AND, "tipo", Filtro.IGUAL, "02"));				
				FinTipoRecebimento tipoRecebimento = finTipoRecebimentoDao.getBean(FinTipoRecebimento.class, filtros);
				if (tipoRecebimento == null) {
					throw new Exception("Tipo de recebimento 'CHEQUE' não está cadastrado.\nEntre em contato com a Software House.");
				}
				
				finChequeRecebidoDao.persist(finChequeRecebido);
				
				for (FinParcelaReceber p : parcelasSelecionadas) {
					FinParcelaRecebimento recebimento = new FinParcelaRecebimento();
					recebimento.setFinTipoRecebimento(tipoRecebimento);
					recebimento.setFinParcelaReceber(p);
					recebimento.setFinChequeRecebido(finChequeRecebido);
					recebimento.setContaCaixa(finChequeRecebido.getContaCaixa());
					recebimento.setDataRecebimento(finChequeRecebido.getBomPara());
					recebimento.setHistorico(finChequeRecebido.getHistorico());
					recebimento.setValorRecebido(p.getValor());

					p.setFinStatusParcela(statusParcela);
					p.getListaFinParcelaRecebimento().add(recebimento);

					p = dao.merge(p);
				}

				if (parcelasSelecionadas.size() == 1) {
					setObjeto(dao.getBeanJoinFetch(getObjeto().getId(), getClazz()));
					novoRecebimento();
				}

				FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_INFO, "Recebimento efetuado com sucesso!", "");
			}
		} catch (Exception e) {
			FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_ERROR, "Erro ao incluir o pagamento!", e.getMessage());
		}
	}
	
	public void calculaTotalRecebido() throws Exception {
		BigDecimal valorJuro = BigDecimal.ZERO;
		BigDecimal valorMulta = BigDecimal.ZERO;
		BigDecimal valorDesconto = BigDecimal.ZERO;
		if (finParcelaRecebimento.getTaxaJuro() != null && finParcelaRecebimento.getDataRecebimento() != null) {
			Calendar dataRecebimento = Calendar.getInstance();
			dataRecebimento.setTime(finParcelaRecebimento.getDataRecebimento());
			Calendar dataVencimento = Calendar.getInstance();
			dataVencimento.setTime(finParcelaRecebimento.getFinParcelaReceber().getDataVencimento());
			if (dataVencimento.before(dataRecebimento)) {
				long diasAtraso = (dataRecebimento.getTimeInMillis() - dataVencimento.getTimeInMillis()) / 86400000l;
				//valorJuro = valor * ((taxaJuro / 30) / 100) * diasAtraso
				finParcelaRecebimento.setValorJuro(finParcelaRecebimento.getFinParcelaReceber().getValor().multiply(finParcelaRecebimento.getTaxaJuro().divide(BigDecimal.valueOf(30), RoundingMode.HALF_DOWN)).divide(BigDecimal.valueOf(100), RoundingMode.HALF_DOWN).multiply(BigDecimal.valueOf(diasAtraso)));
				valorJuro = finParcelaRecebimento.getValorJuro();
			}
		}
		finParcelaRecebimento.setValorJuro(valorJuro);

		if (finParcelaRecebimento.getTaxaMulta() != null) {
			finParcelaRecebimento.setValorMulta(finParcelaRecebimento.getFinParcelaReceber().getValor().multiply(finParcelaRecebimento.getTaxaMulta()).divide(BigDecimal.valueOf(100), RoundingMode.HALF_DOWN));
			valorMulta = finParcelaRecebimento.getValorMulta();
		} else {
			finParcelaRecebimento.setValorMulta(valorMulta);
		}

		if (finParcelaRecebimento.getTaxaDesconto() != null) {
			finParcelaRecebimento.setValorDesconto(finParcelaRecebimento.getFinParcelaReceber().getValor().multiply(finParcelaRecebimento.getTaxaDesconto()).divide(BigDecimal.valueOf(100), RoundingMode.HALF_DOWN));
			valorDesconto = finParcelaRecebimento.getValorDesconto();
		} else {
			finParcelaRecebimento.setValorDesconto(valorDesconto);
		}

		finParcelaRecebimento.setValorRecebido(finParcelaRecebimento.getFinParcelaReceber().getValor().add(valorJuro).add(valorMulta).subtract(valorDesconto));
	}

	public void excluirFinParcelaRecebimento() {
		if (finParcelaRecebimentoSelecionado == null || finParcelaRecebimentoSelecionado.getId() == null) {
			FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_INFO, "Nenhum registro selecionado!", null);
		} else {
			getObjeto().getListaFinParcelaRecebimento().remove(finParcelaRecebimentoSelecionado);
			salvar("Registro excluído com sucesso!");
		}
	}
    
    public List<FinTipoRecebimento> getListaFinTipoRecebimento(String nome) {
        List<FinTipoRecebimento> listaFinTipoRecebimento = new ArrayList<>();
        try {
            listaFinTipoRecebimento = finTipoRecebimentoDao.getBeansLike(FinTipoRecebimento.class, "descricao", nome);
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return listaFinTipoRecebimento;
    }

    public List<FinChequeRecebido> getListaFinChequeRecebido(String nome) {
        List<FinChequeRecebido> listaFinChequeRecebido = new ArrayList<>();
        try {
            listaFinChequeRecebido = finChequeRecebidoDao.getBeansLike(FinChequeRecebido.class, "nome", nome);
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return listaFinChequeRecebido;
    }

    public List<ContaCaixa> getListaContaCaixa(String nome) {
        List<ContaCaixa> listaContaCaixa = new ArrayList<>();
        try {
            listaContaCaixa = contaCaixaDao.getBeansLike(ContaCaixa.class, "nome", nome);
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return listaContaCaixa;
    }

    public List<PessoaCliente> getListaPessoa(String nome) {
        List<PessoaCliente> listaPessoa = new ArrayList<>();
        try {
            listaPessoa = pessoaDao.getBeansLike(PessoaCliente.class, "nome", nome);
        } catch (Exception e) {
            // e.printStackTrace();
        }
        return listaPessoa;
    }
    
    public void onSelecionaPessoa(SelectEvent event) {
    	Pessoa pessoa = new Pessoa();
		pessoa.setId(pessoaCliente.getIdPessoa());
		
		finChequeRecebido.setPessoa(pessoa);
    	finChequeRecebido.setNome(pessoaCliente.getNome());
    	finChequeRecebido.setCpfCnpj(pessoaCliente.getCpfCnpj());
    }
    
	public String getStrTipoBaixa() {
		return strTipoBaixa;
	}

	public void setStrTipoBaixa(String strTipoBaixa) {
		this.strTipoBaixa = strTipoBaixa;
	}

	public List<FinParcelaReceber> getParcelasSelecionadas() {
		return parcelasSelecionadas;
	}

	public void setParcelasSelecionadas(List<FinParcelaReceber> parcelasSelecionadas) {
		this.parcelasSelecionadas = parcelasSelecionadas;
	}

	public boolean isRecebimentoCheque() {
		return recebimentoCheque;
	}

	public void setRecebimentoCheque(boolean recebimentoCheque) {
		this.recebimentoCheque = recebimentoCheque;
	}

	public FinParcelaRecebimento getFinParcelaRecebimento() {
		return finParcelaRecebimento;
	}

	public void setFinParcelaRecebimento(FinParcelaRecebimento finParcelaRecebimento) {
		this.finParcelaRecebimento = finParcelaRecebimento;
	}

	public FinParcelaRecebimento getFinParcelaRecebimentoSelecionado() {
		return finParcelaRecebimentoSelecionado;
	}

	public void setFinParcelaRecebimentoSelecionado(FinParcelaRecebimento finParcelaRecebimentoSelecionado) {
		this.finParcelaRecebimentoSelecionado = finParcelaRecebimentoSelecionado;
	}

	public FinChequeRecebido getFinChequeRecebido() {
		return finChequeRecebido;
	}

	public void setFinChequeRecebido(FinChequeRecebido finChequeRecebido) {
		this.finChequeRecebido = finChequeRecebido;
	}

	public PessoaCliente getPessoaCliente() {
		return pessoaCliente;
	}

	public void setPessoaCliente(PessoaCliente pessoaCliente) {
		this.pessoaCliente = pessoaCliente;
	}

}
