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
package com.t2tierp.controller.ponto;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
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
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;

import com.t2tierp.controller.AbstractController;
import com.t2tierp.model.bean.cadastros.Colaborador;
import com.t2tierp.model.bean.cadastros.Empresa;
import com.t2tierp.model.bean.cadastros.EmpresaEndereco;
import com.t2tierp.model.bean.ponto.EspelhoPontoPeriodoVO;
import com.t2tierp.model.bean.ponto.EspelhoPontoVO;
import com.t2tierp.model.bean.ponto.PontoClassificacaoJornada;
import com.t2tierp.model.bean.ponto.PontoFechamentoJornada;
import com.t2tierp.model.bean.ponto.PontoHorario;
import com.t2tierp.model.bean.ponto.PontoMarcacao;
import com.t2tierp.model.bean.ponto.PontoTurma;
import com.t2tierp.model.dao.InterfaceDAO;
import com.t2tierp.util.FacesContextUtil;

@ManagedBean
@ViewScoped
public class PontoEspelhoController extends AbstractController<Colaborador> implements Serializable {

	private static final long serialVersionUID = 1L;
	@EJB
	private InterfaceDAO<PontoFechamentoJornada> pontoFechamentoJornadaDao;
	@EJB
	private InterfaceDAO<PontoHorario> pontoHorarioDao;
	@EJB
	private InterfaceDAO<PontoClassificacaoJornada> pontoClassificacaoJornadaDao;
	@EJB
	private InterfaceDAO<PontoTurma> pontoTurmaDao;
	@EJB
	private InterfaceDAO<PontoMarcacao> pontoMarcacaoDao;
	@EJB
	private InterfaceDAO<EmpresaEndereco> empresaEnderecoDao;

	private List<PontoFechamentoJornada> listaFechamentoJornada;
	private Date dataInicial;
	private Date dataFinal;
	private Date dataClassificacaoJornada;
	private PontoClassificacaoJornada pontoClassificacaoJornada;

	@Override
	public Class<Colaborador> getClazz() {
		return Colaborador.class;
	}

	@Override
	public String getFuncaoBase() {
		return "PONTO_ESPELHO";
	}

	public void carregarDados() {
		try {
			//Exercício: Ordenar a lista abaixo de forma ascendente pelo campo 'dataFechamento'
			listaFechamentoJornada = pontoFechamentoJornadaDao.getBeans(PontoFechamentoJornada.class, "colaborador", getObjeto(), "dataFechamento", dataInicial, dataFinal);
		} catch (Exception e) {
			e.printStackTrace();
			FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_ERROR, "Erro ao buscar os dados!", e.getMessage());
		}
	}

	public void classificarJornada() {
		try {
			// busca os dados da escala e turma do colaborador selecionado
			PontoTurma turma = pontoTurmaDao.getBean(PontoTurma.class, "codigo", getObjeto().getCodigoTurmaPonto());

			if (turma == null) {
				throw new Exception("Colaborador está sem o código da turma cadastrada!");
			}

			PontoFechamentoJornada fechamento = new PontoFechamentoJornada();
			fechamento.setColaborador(getObjeto());
			fechamento.setDataFechamento(dataClassificacaoJornada);
			fechamento.setPontoClassificacaoJornada(pontoClassificacaoJornada);

			String codigoHorario = "";
			Calendar dataClassificar = Calendar.getInstance();
			dataClassificar.setTime(dataClassificacaoJornada);
			switch (dataClassificar.get(Calendar.DAY_OF_WEEK)) {
			case 1: {
				fechamento.setDiaSemana("DOMINGO");
				codigoHorario = turma.getPontoEscala().getCodigoHorarioDomingo();
				break;
			}
			case 2: {
				fechamento.setDiaSemana("SEGUNDA");
				codigoHorario = turma.getPontoEscala().getCodigoHorarioSegunda();
				break;
			}
			case 3: {
				fechamento.setDiaSemana("TERCA");
				codigoHorario = turma.getPontoEscala().getCodigoHorarioTerca();
				break;
			}
			case 4: {
				fechamento.setDiaSemana("QUARTA");
				codigoHorario = turma.getPontoEscala().getCodigoHorarioQuarta();
				break;
			}
			case 5: {
				fechamento.setDiaSemana("QUINTA");
				codigoHorario = turma.getPontoEscala().getCodigoHorarioQuinta();
				break;
			}
			case 6: {
				fechamento.setDiaSemana("SEXTA");
				codigoHorario = turma.getPontoEscala().getCodigoHorarioSexta();
				break;
			}
			case 7: {
				fechamento.setDiaSemana("SABADO");
				codigoHorario = turma.getPontoEscala().getCodigoHorarioSabado();
				break;
			}
			}

			PontoHorario horario = pontoHorarioDao.getBean(PontoHorario.class, "codigo", codigoHorario);
			if (horario == null) {
				throw new Exception("Código do horário cadastrado na escala não corresponde a um horário armazenado!");
			}

			fechamento.setCodigoHorario(horario.getCodigo());
			fechamento.setCargaHorariaEsperada(horario.getCargaHoraria());
			if (pontoClassificacaoJornada.getDescontarHoras().equals("S")) {
				fechamento.setFaltaAtraso(horario.getCargaHoraria());
			}
			fechamento.setHoraInicioJornada(horario.getHoraInicioJornada());
			fechamento.setHoraFimJornada(horario.getHoraFimJornada());
			fechamento.setObservacao("Registro incluído via sistema pelo usuário " + FacesContextUtil.getUsuarioSessao().getColaborador().getPessoa().getNome());

			pontoFechamentoJornadaDao.persist(fechamento);

			FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_INFO, "Classificação realizada com sucesso!", "");
		} catch (Exception e) {
			e.printStackTrace();
			FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_ERROR, "Erro ao classificar a jornada!", e.getMessage());
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void geraRelatorio() {
		try {
			List<EspelhoPontoVO> listaEspelho = new ArrayList<>();

			Empresa empresa = FacesContextUtil.getEmpresaUsuario();
			EspelhoPontoVO espelho = new EspelhoPontoVO();

			EmpresaEndereco enderecoPrincipal = empresaEnderecoDao.getBean(EmpresaEndereco.class, "empresa", empresa, "principal", "S");
			
			if (enderecoPrincipal == null) {
				throw new Exception("Endereço principal da empresa não definido.");
			}

			espelho.setCnpjEmpregador(empresa.getCnpj());
			espelho.setNomeEmpregador(empresa.getRazaoSocial());
			espelho.setEderecoEmpregador(enderecoPrincipal.getLogradouro());
			espelho.setDataEmissaoRelatorio(new Date());
			espelho.setPisEmpregado(getObjeto().getPisNumero());
			espelho.setNomeEmpregado(getObjeto().getPessoa().getNome());
			espelho.setDataAdmissao(getObjeto().getDataAdmissao());

			//busca os horarios do colaborador
			List<PontoHorario> listaHorario = horariosColaborador();

			//busca os registro de marcacoes do colaborador
			List<PontoMarcacao> listaMarcacoes = marcacoesColaborador();

			List<EspelhoPontoPeriodoVO> listaPeriodo = new ArrayList<>();
			List<PontoMarcacao> marcacoesTratadas;
			PontoMarcacao marcacao;
			EspelhoPontoPeriodoVO periodo;
			Calendar dataMarcacao = Calendar.getInstance();

			for (int i = 0; i < listaFechamentoJornada.size(); i++) {
				dataMarcacao.setTime(listaFechamentoJornada.get(i).getDataFechamento());
				periodo = new EspelhoPontoPeriodoVO();
				periodo.setDia(dataMarcacao.get(Calendar.DAY_OF_MONTH));
				periodo.setCodigoHorario(listaFechamentoJornada.get(i).getCodigoHorario());
				marcacoesTratadas = new ArrayList<>();

				for (int j = 0; j < listaMarcacoes.size(); j++) {
					marcacao = listaMarcacoes.get(j);
					if (marcacao.getDataMarcacao().compareTo(listaFechamentoJornada.get(i).getDataFechamento()) == 0) {
						if (marcacao.getTipoRegistro().equals("O")) {
							if (!marcacao.getTipoMarcacao().equals("D")) {
								if (marcacao.getParEntradaSaida().equals("E1")) {
									periodo.setMarcacaoEntrada01(marcacao.getHoraMarcacao());
								}
								if (marcacao.getParEntradaSaida().equals("S1")) {
									periodo.setMarcacaoSaida01(marcacao.getHoraMarcacao());
								}
								if (marcacao.getParEntradaSaida().equals("E2")) {
									periodo.setMarcacaoEntrada02(marcacao.getHoraMarcacao());
								}
								if (marcacao.getParEntradaSaida().equals("S2")) {
									periodo.setMarcacaoSaida02(marcacao.getHoraMarcacao());
								}
							}
						} else {
							marcacoesTratadas.add(marcacao);
						}
					}
				}
				periodo.setJornadaEntrada01(listaFechamentoJornada.get(i).getEntrada01());
				periodo.setJornadaSaida01(listaFechamentoJornada.get(i).getSaida01());
				periodo.setJornadaEntrada02(listaFechamentoJornada.get(i).getEntrada02());
				periodo.setJornadaSaida02(listaFechamentoJornada.get(i).getSaida02());
				periodo.setJornadaEntrada03(listaFechamentoJornada.get(i).getEntrada03());
				periodo.setJornadaSaida03(listaFechamentoJornada.get(i).getSaida03());
				periodo.setMarcacoesTratadas(marcacoesTratadas);

				listaPeriodo.add(periodo);
			}

			espelho.setListaHorarios(listaHorario);
			espelho.setListaPeriodo(listaPeriodo);
			listaEspelho.add(espelho);

			//gera o relatorio
			File file = File.createTempFile("espelho", ".pdf");
			file.deleteOnExit();
			
			Map parametros = new HashMap();
			parametros.put("SUBREPORT_DIR", this.getClass().getResource("/com/t2tierp/ponto/espelho/").toString());
			parametros.put("DATA_INICIO", dataInicial);
			parametros.put("DATA_FIM", dataFinal);

			JRBeanCollectionDataSource jrbean = new JRBeanCollectionDataSource(listaEspelho);
			JasperPrint jp = JasperFillManager.fillReport(this.getClass().getResourceAsStream("/com/t2tierp/ponto/espelho/espelho_ponto.jasper"), parametros, jrbean);
			
			OutputStream outPdf = new FileOutputStream(file);
			outPdf.write(JasperExportManager.exportReportToPdf(jp));
			outPdf.close();

			FacesContextUtil.downloadArquivo(file, "espelho.pdf");
		} catch (Exception e) {
			e.printStackTrace();
			FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro ao gerar o relatório!", e.getMessage());
		}
	}

	private List<PontoHorario> horariosColaborador() throws Exception {
		PontoTurma turma = pontoTurmaDao.getBean(PontoTurma.class, "codigo", getObjeto().getCodigoTurmaPonto());
		if (turma == null) {
			throw new Exception("Colaborador está sem o código da turma cadastrada!");
		}

		List<PontoHorario> listaHorarios = new ArrayList<>();

		PontoHorario horario;
		//horario domingo
		horario = pontoHorarioDao.getBean(PontoHorario.class, "codigo", turma.getPontoEscala().getCodigoHorarioDomingo());
		if (horario != null) {
			listaHorarios.add(horario);
		}
		//horario segunda
		horario = pontoHorarioDao.getBean(PontoHorario.class, "codigo", turma.getPontoEscala().getCodigoHorarioSegunda());
		if (horario != null) {
			if (!listaHorarios.contains(horario)) {
				listaHorarios.add(horario);
			}
		}
		//horario terca
		horario = pontoHorarioDao.getBean(PontoHorario.class, "codigo", turma.getPontoEscala().getCodigoHorarioTerca());
		if (horario != null) {
			if (!listaHorarios.contains(horario)) {
				listaHorarios.add(horario);
			}
		}
		//horario quarta
		horario = pontoHorarioDao.getBean(PontoHorario.class, "codigo", turma.getPontoEscala().getCodigoHorarioQuarta());
		if (horario != null) {
			if (!listaHorarios.contains(horario)) {
				listaHorarios.add(horario);
			}
		}
		//horario quinta
		horario = pontoHorarioDao.getBean(PontoHorario.class, "codigo", turma.getPontoEscala().getCodigoHorarioQuinta());
		if (horario != null) {
			if (!listaHorarios.contains(horario)) {
				listaHorarios.add(horario);
			}
		}
		//horario sexta
		horario = pontoHorarioDao.getBean(PontoHorario.class, "codigo", turma.getPontoEscala().getCodigoHorarioSexta());
		if (horario != null) {
			if (!listaHorarios.contains(horario)) {
				listaHorarios.add(horario);
			}
		}
		//horario sabado
		horario = pontoHorarioDao.getBean(PontoHorario.class, "codigo", turma.getPontoEscala().getCodigoHorarioSabado());
		if (horario != null) {
			if (!listaHorarios.contains(horario)) {
				listaHorarios.add(horario);
			}
		}

		return listaHorarios;
	}

	private List<PontoMarcacao> marcacoesColaborador() throws Exception {
		//Exercício: Ordenar a lista abaixo de forma ascendente pelos seguintes campos: 'dataMarcacao' e 'horaMarcacao'
		return pontoMarcacaoDao.getBeans(PontoMarcacao.class, "colaborador", getObjeto(), "dataMarcacao", dataInicial, dataFinal);
	}

	public List<PontoClassificacaoJornada> getListaPontoClassificacaoJornada(String nome) {
		List<PontoClassificacaoJornada> listaPontoClassificacaoJornada = new ArrayList<>();
		try {
			listaPontoClassificacaoJornada = pontoClassificacaoJornadaDao.getBeansLike(PontoClassificacaoJornada.class, "nome", nome);
		} catch (Exception e) {
			// e.printStackTrace();
		}
		return listaPontoClassificacaoJornada;
	}

	public List<PontoFechamentoJornada> getListaFechamentoJornada() {
		return listaFechamentoJornada;
	}

	public void setListaFechamentoJornada(List<PontoFechamentoJornada> listaFechamentoJornada) {
		this.listaFechamentoJornada = listaFechamentoJornada;
	}

	public Date getDataClassificacaoJornada() {
		return dataClassificacaoJornada;
	}

	public void setDataClassificacaoJornada(Date dataClassificacaoJornada) {
		this.dataClassificacaoJornada = dataClassificacaoJornada;
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

	public PontoClassificacaoJornada getPontoClassificacaoJornada() {
		return pontoClassificacaoJornada;
	}

	public void setPontoClassificacaoJornada(PontoClassificacaoJornada pontoClassificacaoJornada) {
		this.pontoClassificacaoJornada = pontoClassificacaoJornada;
	}

}
