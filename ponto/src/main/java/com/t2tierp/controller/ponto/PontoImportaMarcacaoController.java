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
import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.io.FileUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.UploadedFile;

import com.t2tierp.controller.AbstractController;
import com.t2tierp.model.bean.cadastros.Colaborador;
import com.t2tierp.model.bean.ponto.AFDTipo3;
import com.t2tierp.model.bean.ponto.PontoClassificacaoJornada;
import com.t2tierp.model.bean.ponto.PontoEscala;
import com.t2tierp.model.bean.ponto.PontoFechamentoJornada;
import com.t2tierp.model.bean.ponto.PontoHorario;
import com.t2tierp.model.bean.ponto.PontoMarcacao;
import com.t2tierp.model.bean.ponto.PontoParametro;
import com.t2tierp.model.bean.ponto.PontoRelogio;
import com.t2tierp.model.bean.ponto.PontoTurma;
import com.t2tierp.model.dao.Filtro;
import com.t2tierp.model.dao.InterfaceDAO;
import com.t2tierp.ponto.afd.ImportaArquivoAFD;
import com.t2tierp.util.Biblioteca;
import com.t2tierp.util.FacesContextUtil;

@ManagedBean
@ViewScoped
public class PontoImportaMarcacaoController extends AbstractController<PontoFechamentoJornada> implements Serializable {

	private static final long serialVersionUID = 1L;
	@EJB
	private InterfaceDAO<Colaborador> colaboradorDao;
	@EJB
	private InterfaceDAO<PontoRelogio> pontoRelogioDao;
	@EJB
	private InterfaceDAO<PontoMarcacao> pontoMarcacaoDao;
	@EJB
	private InterfaceDAO<PontoTurma> pontoTurmaDao;
	@EJB
	private InterfaceDAO<PontoHorario> pontoHorarioDao;
	@EJB
	private InterfaceDAO<PontoClassificacaoJornada> pontoClassificacaoJornadaDao;
	@EJB
	private InterfaceDAO<PontoParametro> pontoParametroDao;

	private List<AFDTipo3> listaAFDTipo3;
	private AFDTipo3 afdTipo3Selecionado;
	private PontoMarcacao pontoMarcacao;
	private PontoMarcacao pontoMarcacaoSelecionado;
	private List<PontoMarcacao> listaMarcacaoIncluida;
	private List<PontoFechamentoJornada> listaFechamentoJornada;

	@Override
	public Class<PontoFechamentoJornada> getClazz() {
		return PontoFechamentoJornada.class;
	}

	@Override
	public String getFuncaoBase() {
		return "PONTO_IMPORTA_MARCACAO";
	}

	public void incluirPontoMarcacao() {
		pontoMarcacao = new PontoMarcacao();
		if (listaMarcacaoIncluida == null) {
			listaMarcacaoIncluida = new ArrayList<>();
		}
	}

	public void alterarPontoMarcacao() {
		pontoMarcacao = pontoMarcacaoSelecionado;
	}

	public void salvarPontoMarcacao() {
		if (pontoMarcacao.getId() == null) {
			pontoMarcacao.setId(listaMarcacaoIncluida.size() + 1);
			listaMarcacaoIncluida.add(pontoMarcacao);
		}
	}

	public void excluirPontoMarcacao() {
		if (pontoMarcacaoSelecionado == null || pontoMarcacaoSelecionado.getId() == null) {
			FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_INFO, "Nenhum registro selecionado!", null);
		} else {
			listaMarcacaoIncluida.remove(pontoMarcacaoSelecionado);
			FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_INFO, "Registro excluído com sucesso!", null);
		}
	}

	public void importarARquivoAFD(FileUploadEvent event) throws Exception {
		try {
			UploadedFile arquivo = event.getFile();
			File file = File.createTempFile("afd", ".txt");
			FileUtils.copyInputStreamToFile(arquivo.getInputstream(), file);

			ImportaArquivoAFD importa = new ImportaArquivoAFD();
			listaAFDTipo3 = importa.leArquivoAFD(file);

			FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_INFO, "Arquivo importado com sucesso.!", null);
		} catch (Exception e) {
			e.printStackTrace();
			FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_ERROR, "Erro na importação do arquivo.!", e.getMessage());
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void processarFechamento() {
		try {
			if (listaAFDTipo3 == null) {
				throw new Exception("Nenhum registro para processar.");
			}

			if (!listaAFDTipo3.isEmpty()) {
				for (int i = (listaAFDTipo3.size() - 1); i > 0; i--) {
					if (listaAFDTipo3.get(i).getSequencial().intValue() == 0) {
						listaAFDTipo3.remove(i);
					}
				}

				if (listaMarcacaoIncluida != null) {
					AFDTipo3 tipo3;
					for (PontoMarcacao p : listaMarcacaoIncluida) {
						tipo3 = new AFDTipo3();
						//tipo3.setColaborador(p.getColaborador());
						tipo3.setPisEmpregado(p.getColaborador().getPisNumero());
						tipo3.setDataMarcacao(p.getDataMarcacao());
						tipo3.setHoraMarcacao(p.getHoraMarcacao());
						//tipo3.setSequencial(0l);
						tipo3.setTipoRegistro("I");
						tipo3.setJustificativa(p.getJustificativa());
						tipo3.setDesconsiderar(false);

						listaAFDTipo3.add(tipo3);
					}
				}

				//busca os dados do REP
				PontoRelogio relogio = pontoRelogioDao.getBean(PontoRelogio.class, "numeroSerie", listaAFDTipo3.get(0).getNumeroSerieRelogioPonto());

				Colaborador colaborador;
				PontoMarcacao pontoMarcacao;
				for (AFDTipo3 afd : listaAFDTipo3) {
					if (relogio == null) {
						afd.setSituacaoRegistro("Relógio de Ponto não encontrado na base de dados!");
					} else {
						//busca os dados do colaborador
						colaborador = colaboradorDao.getBean(Colaborador.class, "pisNumero", afd.getPisEmpregado());

						if (colaborador == null) {
							afd.setSituacaoRegistro("Nr. PIS não cadastrado!");
						} else {
							afd.setColaborador(colaborador);
							if (colaborador.getCodigoTurmaPonto() == null) {
								afd.setSituacaoRegistro("Colaborador está sem o código da turma cadastrada!");
							} else {
								//Verifica se o registro já foi armazenado no banco de dados
								List<Filtro> filtros = new ArrayList<>();
								filtros.add(new Filtro(Filtro.AND, "colaborador", Filtro.IGUAL, colaborador));
								filtros.add(new Filtro(Filtro.AND, "pontoRelogio", Filtro.IGUAL, relogio));
								filtros.add(new Filtro(Filtro.AND, "dataMarcacao", Filtro.IGUAL, afd.getDataMarcacao()));
								filtros.add(new Filtro(Filtro.AND, "horaMarcacao", Filtro.IGUAL, afd.getHoraMarcacao()));

								PontoMarcacao marc = pontoMarcacaoDao.getBean(PontoMarcacao.class, filtros);

								//busca os horários
								PontoTurma turma = pontoTurmaDao.getBean(PontoTurma.class, "codigo", colaborador.getCodigoTurmaPonto());

								if (marc == null) {
									if (turma != null) {
										pontoMarcacao = new PontoMarcacao();
										pontoMarcacao.setColaborador(colaborador);
										pontoMarcacao.setPontoRelogio(relogio);
										if (afd.getSequencial() != null) {
											pontoMarcacao.setNsr(afd.getSequencial().intValue());
										}
										pontoMarcacao.setDataMarcacao(afd.getDataMarcacao());
										pontoMarcacao.setHoraMarcacao(afd.getHoraMarcacao());
										try {
											Map m = defineTipoMarcacaoHorario(turma.getPontoEscala(), pontoMarcacao);
											String parEntradaSaida = (String) m.get("parEntradaSaida");
											if (afd.getDesconsiderar()) {
												pontoMarcacao.setTipoMarcacao("D");
											} else {
												pontoMarcacao.setTipoMarcacao(parEntradaSaida.substring(0, 1));
											}
											pontoMarcacao.setTipoRegistro(afd.getTipoRegistro());
											pontoMarcacao.setParEntradaSaida(parEntradaSaida);
											pontoMarcacao.setJustificativa(afd.getJustificativa());

											pontoMarcacaoDao.persist(pontoMarcacao);

											afd.setColaborador(colaborador);
											afd.setSituacaoRegistro("Registro incluído!");
											afd.setPontoHorario((PontoHorario) m.get("pontoHorario"));
											afd.setParEntradaSaida(parEntradaSaida);
										} catch (Exception e) {
											afd.setSituacaoRegistro(e.getMessage());
										}
									}
								} else {
									afd.setSituacaoRegistro("Registro já incluído anteriormente!");
									afd.setParEntradaSaida(marc.getParEntradaSaida());
									Map m = defineTipoMarcacaoHorario(turma.getPontoEscala(), marc);
									afd.setPontoHorario((PontoHorario) m.get("pontoHorario"));

									if (afd.getDesconsiderar()) {
										marc.setTipoMarcacao("D");
									} else {
										marc.setTipoMarcacao(afd.getParEntradaSaida().substring(0, 1));
									}
									marc.setTipoRegistro(afd.getTipoRegistro());
									marc.setJustificativa(afd.getJustificativa());

									pontoMarcacaoDao.merge(marc);
								}
							}
						}
					}
				}

				//busca a classificacao de jornada padrao
				List<PontoClassificacaoJornada> listaClassificacao = pontoClassificacaoJornadaDao.getBeans(PontoClassificacaoJornada.class, "padrao", "S");
				if (listaClassificacao.isEmpty()) {
					throw new Exception("Nenhuma classificação de jornada cadastrada como padrão!");
				}
				PontoClassificacaoJornada classificacao = listaClassificacao.get(0);
				//ordena a lista pelo numero do PIS e Data da Marcacao
				Collections.sort(listaAFDTipo3, new Comparator() {

					public int compare(Object o1, Object o2) {
						AFDTipo3 m1 = (AFDTipo3) o1;
						AFDTipo3 m2 = (AFDTipo3) o2;
						int resultado = m1.getPisEmpregado().compareTo(m2.getPisEmpregado());
						if (resultado != 0) {
							return resultado;
						}
						resultado = m1.getDataMarcacao().compareTo(m2.getDataMarcacao());
						return resultado;
					}
				});

				String numeroPis = "";
				Calendar dataMarcacao = Calendar.getInstance();
				AFDTipo3 marcacao;
				PontoFechamentoJornada fechamentoJornada = null;
				listaFechamentoJornada = new ArrayList<>();
				for (int i = 0; i < listaAFDTipo3.size(); i++) {
					marcacao = listaAFDTipo3.get(i);
					if (!marcacao.getDesconsiderar() && marcacao.getPontoHorario() != null) {
						if ((!marcacao.getPisEmpregado().equals(numeroPis)) || (marcacao.getDataMarcacao().compareTo(dataMarcacao.getTime()) != 0)) {
							numeroPis = marcacao.getPisEmpregado();
							dataMarcacao.setTime(marcacao.getDataMarcacao());

							fechamentoJornada = new PontoFechamentoJornada();
							fechamentoJornada.setPontoClassificacaoJornada(classificacao);
							fechamentoJornada.setColaborador(marcacao.getColaborador());
							fechamentoJornada.setDataFechamento(dataMarcacao.getTime());
							switch (dataMarcacao.get(Calendar.DAY_OF_WEEK)) {
							case 1:
								fechamentoJornada.setDiaSemana("DOMINGO");
								break;
							case 2:
								fechamentoJornada.setDiaSemana("SEGUNDA");
								break;
							case 3:
								fechamentoJornada.setDiaSemana("TERCA");
								break;
							case 4:
								fechamentoJornada.setDiaSemana("QUARTA");
								break;
							case 5:
								fechamentoJornada.setDiaSemana("QUINTA");
								break;
							case 6:
								fechamentoJornada.setDiaSemana("SEXTA");
								break;
							case 7:
								fechamentoJornada.setDiaSemana("SABADO");
								break;
							}
							fechamentoJornada.setCodigoHorario(marcacao.getPontoHorario().getCodigo());
							fechamentoJornada.setCargaHorariaEsperada(marcacao.getPontoHorario().getCargaHoraria());
							fechamentoJornada.setHoraInicioJornada(marcacao.getPontoHorario().getHoraInicioJornada());
							fechamentoJornada.setHoraFimJornada(marcacao.getPontoHorario().getHoraFimJornada());

							listaFechamentoJornada.add(fechamentoJornada);
						}
						defineTipoRegistro(fechamentoJornada, marcacao);
					}
				}

				//realiza os calculos necessarios
				Calendar dataFechamento = Calendar.getInstance();
				for (int i = 0; i < listaFechamentoJornada.size(); i++) {
					fechamentoJornada = listaFechamentoJornada.get(i);
					dataFechamento.setTime(fechamentoJornada.getDataFechamento());
					int mes = dataFechamento.get(Calendar.MONTH) + 1;
					int ano = dataFechamento.get(Calendar.YEAR);
					String mesAno = mes < 10 ? "0" + mes + "/" + ano : mes + "/" + ano;

					PontoParametro parametro = pontoParametroDao.getBean(PontoParametro.class, "mesAno", mesAno);
					if (parametro == null) {
						throw new Exception("Não existe parâmetros cadastrados para o período " + mesAno);
					}
					//verifica se os pares Entrada/Saida foram registrados
					verificaParesFechamento(fechamentoJornada);

					//calcula a quantidade de horas trabalhadas
					fechamentoJornada.setCargaHorariaTotal(calculaHorasTrabalhados(fechamentoJornada));
					fechamentoJornada.setCargaHorariaNoturna(calculaCargaHorariaNoturna(fechamentoJornada, parametro));
					fechamentoJornada.setCargaHorariaDiurna(calculaCargaHorariaDiurna(fechamentoJornada));
					calculaHorasExtras(fechamentoJornada, parametro);
				}
			}

			FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_INFO, "Processamento realizado com sucesso.!", "");
		} catch (Exception e) {
			e.printStackTrace();
			FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_ERROR, "Erro ao processar o fechamento.!", e.getMessage());
		}
	}

	@SuppressWarnings({ "rawtypes", "unchecked" })
	private Map defineTipoMarcacaoHorario(PontoEscala escala, PontoMarcacao marcacao) throws Exception {
		Calendar dataMarcacao = Calendar.getInstance();
		dataMarcacao.setTime(marcacao.getDataMarcacao());
		dataMarcacao.set(Calendar.HOUR_OF_DAY, Integer.valueOf(marcacao.getHoraMarcacao().substring(0, 2)));
		dataMarcacao.set(Calendar.MINUTE, Integer.valueOf(marcacao.getHoraMarcacao().substring(3, 5)));
		dataMarcacao.set(Calendar.SECOND, Integer.valueOf(marcacao.getHoraMarcacao().substring(6, 8)));

		//busca o horario de acordo com o dia da semana
		String codigoHorario = "";
		switch (dataMarcacao.get(Calendar.DAY_OF_WEEK)) {
		case 1: {//domingo
			codigoHorario = escala.getCodigoHorarioDomingo();
			break;
		}
		case 2: {//seguna
			codigoHorario = escala.getCodigoHorarioSegunda();
			break;
		}
		case 3: {//terca
			codigoHorario = escala.getCodigoHorarioTerca();
			break;
		}
		case 4: {//quarta
			codigoHorario = escala.getCodigoHorarioQuarta();
			break;
		}
		case 5: {//quinta
			codigoHorario = escala.getCodigoHorarioQuinta();
			break;
		}
		case 6: {//sexta
			codigoHorario = escala.getCodigoHorarioSexta();
			break;
		}
		case 7: {//sabado
			codigoHorario = escala.getCodigoHorarioSabado();
			break;
		}
		}
		PontoHorario horario = pontoHorarioDao.getBean(PontoHorario.class, "codigo", codigoHorario);
		if (horario == null) {
			throw new Exception("Código do horário cadastrado na escala não corresponde a um horário armazenado!");
		}

		SimpleDateFormat formatoHora = new SimpleDateFormat("HH:mm:ss");
		int segundosHoraMarcada = Biblioteca.getHoraSeg(dataMarcacao);
		int segundosHoraHorario = 0;
		int diferençaSegundos = 0;
		int diferençaSegundosAnterior = 0;
		String parEntradaSaida = null;
		Calendar dataHorario = Calendar.getInstance();
		//verifica qual registro foi efetuado de acordo com os horários cadastrados
		//será realizada a diferença entre os segundos da marcaçao e os segundos do horário cadastrado
		//o tipo de marcação a ser registrada é o que der a menor diferença entre os registros
		if (horario.getEntrada01() != null) {
			dataHorario.setTime(formatoHora.parse(horario.getEntrada01()));
			segundosHoraHorario = Biblioteca.getHoraSeg(dataHorario);
			diferençaSegundos = Math.abs(segundosHoraMarcada - segundosHoraHorario);
			diferençaSegundosAnterior = diferençaSegundos;
			parEntradaSaida = "E1";
		}
		if (horario.getSaida01() != null) {
			dataHorario.setTime(formatoHora.parse(horario.getSaida01()));
			segundosHoraHorario = Biblioteca.getHoraSeg(dataHorario);
			diferençaSegundos = Math.abs(segundosHoraMarcada - segundosHoraHorario);
			if (diferençaSegundos < diferençaSegundosAnterior) {
				diferençaSegundosAnterior = diferençaSegundos;
				parEntradaSaida = "S1";
			}
		}
		if (horario.getEntrada02() != null) {
			dataHorario.setTime(formatoHora.parse(horario.getEntrada02()));
			segundosHoraHorario = Biblioteca.getHoraSeg(dataHorario);
			diferençaSegundos = Math.abs(segundosHoraMarcada - segundosHoraHorario);
			if (diferençaSegundos < diferençaSegundosAnterior) {
				diferençaSegundosAnterior = diferençaSegundos;
				parEntradaSaida = "E2";
			}
		}
		if (horario.getSaida02() != null) {
			dataHorario.setTime(formatoHora.parse(horario.getSaida02()));
			segundosHoraHorario = Biblioteca.getHoraSeg(dataHorario);
			diferençaSegundos = Math.abs(segundosHoraMarcada - segundosHoraHorario);
			if (diferençaSegundos < diferençaSegundosAnterior) {
				diferençaSegundosAnterior = diferençaSegundos;
				parEntradaSaida = "S2";
			}
		}
		if (horario.getEntrada03() != null) {
			dataHorario.setTime(formatoHora.parse(horario.getEntrada03()));
			segundosHoraHorario = Biblioteca.getHoraSeg(dataHorario);
			diferençaSegundos = Math.abs(segundosHoraMarcada - segundosHoraHorario);
			if (diferençaSegundos < diferençaSegundosAnterior) {
				diferençaSegundosAnterior = diferençaSegundos;
				parEntradaSaida = "E3";
			}
		}
		if (horario.getSaida03() != null) {
			dataHorario.setTime(formatoHora.parse(horario.getSaida03()));
			segundosHoraHorario = Biblioteca.getHoraSeg(dataHorario);
			diferençaSegundos = Math.abs(segundosHoraMarcada - segundosHoraHorario);
			if (diferençaSegundos < diferençaSegundosAnterior) {
				diferençaSegundosAnterior = diferençaSegundos;
				parEntradaSaida = "S3";
			}
		}
		if (horario.getEntrada04() != null) {
			dataHorario.setTime(formatoHora.parse(horario.getEntrada04()));
			segundosHoraHorario = Biblioteca.getHoraSeg(dataHorario);
			diferençaSegundos = Math.abs(segundosHoraMarcada - segundosHoraHorario);
			if (diferençaSegundos < diferençaSegundosAnterior) {
				diferençaSegundosAnterior = diferençaSegundos;
				parEntradaSaida = "E4";
			}
		}
		if (horario.getSaida04() != null) {
			dataHorario.setTime(formatoHora.parse(horario.getSaida04()));
			segundosHoraHorario = Biblioteca.getHoraSeg(dataHorario);
			diferençaSegundos = Math.abs(segundosHoraMarcada - segundosHoraHorario);
			if (diferençaSegundos < diferençaSegundosAnterior) {
				diferençaSegundosAnterior = diferençaSegundos;
				parEntradaSaida = "S4";
			}
		}
		if (horario.getEntrada05() != null) {
			dataHorario.setTime(formatoHora.parse(horario.getEntrada05()));
			segundosHoraHorario = Biblioteca.getHoraSeg(dataHorario);
			diferençaSegundos = Math.abs(segundosHoraMarcada - segundosHoraHorario);
			if (diferençaSegundos < diferençaSegundosAnterior) {
				diferençaSegundosAnterior = diferençaSegundos;
				parEntradaSaida = "E5";
			}
		}
		if (horario.getSaida05() != null) {
			dataHorario.setTime(formatoHora.parse(horario.getSaida05()));
			segundosHoraHorario = Biblioteca.getHoraSeg(dataHorario);
			diferençaSegundos = Math.abs(segundosHoraMarcada - segundosHoraHorario);
			if (diferençaSegundos < diferençaSegundosAnterior) {
				diferençaSegundosAnterior = diferençaSegundos;
				parEntradaSaida = "S5";
			}
		}
		Map resultado = new HashMap();
		resultado.put("pontoHorario", horario);
		resultado.put("parEntradaSaida", parEntradaSaida);

		return resultado;
	}

	private void defineTipoRegistro(PontoFechamentoJornada fechamento, AFDTipo3 marcacao) {
		if (marcacao.getParEntradaSaida() != null) {
			if (marcacao.getParEntradaSaida().equals("E1")) {
				fechamento.setEntrada01(marcacao.getHoraMarcacao());
			}
			if (marcacao.getParEntradaSaida().equals("S1")) {
				fechamento.setSaida01(marcacao.getHoraMarcacao());
			}
			if (marcacao.getParEntradaSaida().equals("E2")) {
				fechamento.setEntrada02(marcacao.getHoraMarcacao());
			}
			if (marcacao.getParEntradaSaida().equals("S2")) {
				fechamento.setSaida02(marcacao.getHoraMarcacao());
			}
			if (marcacao.getParEntradaSaida().equals("E3")) {
				fechamento.setEntrada03(marcacao.getHoraMarcacao());
			}
			if (marcacao.getParEntradaSaida().equals("S3")) {
				fechamento.setSaida03(marcacao.getHoraMarcacao());
			}
			if (marcacao.getParEntradaSaida().equals("E4")) {
				fechamento.setEntrada04(marcacao.getHoraMarcacao());
			}
			if (marcacao.getParEntradaSaida().equals("S4")) {
				fechamento.setSaida04(marcacao.getHoraMarcacao());
			}
			if (marcacao.getParEntradaSaida().equals("E5")) {
				fechamento.setEntrada05(marcacao.getHoraMarcacao());
			}
			if (marcacao.getParEntradaSaida().equals("S5")) {
				fechamento.setSaida05(marcacao.getHoraMarcacao());
			}
		}
	}

	private void verificaParesFechamento(PontoFechamentoJornada fechamento) {
		if (fechamento.getEntrada01() != null && fechamento.getSaida01() == null) {
			fechamento.setObservacao("Registro de entrada sem o registro de saída correspondente!");
		}
		if (fechamento.getEntrada01() == null && fechamento.getSaida01() != null) {
			fechamento.setObservacao("Registro de saida sem o registro de entrada correspondente!");
		}
		if (fechamento.getEntrada02() != null && fechamento.getSaida02() == null) {
			fechamento.setObservacao("Registro de entrada sem o registro de saída correspondente!");
		}
		if (fechamento.getEntrada02() == null && fechamento.getSaida02() != null) {
			fechamento.setObservacao("Registro de saida sem o registro de entrada correspondente!");
		}
		if (fechamento.getEntrada03() != null && fechamento.getSaida03() == null) {
			fechamento.setObservacao("Registro de entrada sem o registro de saída correspondente!");
		}
		if (fechamento.getEntrada03() == null && fechamento.getSaida03() != null) {
			fechamento.setObservacao("Registro de saida sem o registro de entrada correspondente!");
		}
		if (fechamento.getEntrada04() != null && fechamento.getSaida04() == null) {
			fechamento.setObservacao("Registro de entrada sem o registro de saída correspondente!");
		}
		if (fechamento.getEntrada04() == null && fechamento.getSaida04() != null) {
			fechamento.setObservacao("Registro de saida sem o registro de entrada correspondente!");
		}
		if (fechamento.getEntrada05() != null && fechamento.getSaida05() == null) {
			fechamento.setObservacao("Registro de entrada sem o registro de saída correspondente!");
		}
		if (fechamento.getEntrada05() == null && fechamento.getSaida05() != null) {
			fechamento.setObservacao("Registro de saida sem o registro de entrada correspondente!");
		}
	}

	private String calculaHorasTrabalhados(PontoFechamentoJornada fechamento) {
		int segundosTrabalhados = 0;
		Calendar dataC = Calendar.getInstance();
		//par E1/S1
		if (fechamento.getEntrada01() != null && fechamento.getSaida01() != null) {
			dataC = Biblioteca.horaStrToCalendar(fechamento.getSaida01());
			segundosTrabalhados += Biblioteca.getHoraSeg(dataC);

			dataC = Biblioteca.horaStrToCalendar(fechamento.getEntrada01());
			segundosTrabalhados -= Biblioteca.getHoraSeg(dataC);
		}

		//par E2/S2
		if (fechamento.getEntrada02() != null && fechamento.getSaida02() != null) {
			dataC = Biblioteca.horaStrToCalendar(fechamento.getSaida02());
			segundosTrabalhados += Biblioteca.getHoraSeg(dataC);

			dataC = Biblioteca.horaStrToCalendar(fechamento.getEntrada02());
			segundosTrabalhados -= Biblioteca.getHoraSeg(dataC);
		}
		//par E3/S3
		if (fechamento.getEntrada03() != null && fechamento.getSaida03() != null) {
			dataC = Biblioteca.horaStrToCalendar(fechamento.getSaida03());
			segundosTrabalhados += Biblioteca.getHoraSeg(dataC);

			dataC = Biblioteca.horaStrToCalendar(fechamento.getEntrada03());
			segundosTrabalhados -= Biblioteca.getHoraSeg(dataC);
		}
		//par E4/S4
		if (fechamento.getEntrada04() != null && fechamento.getSaida04() != null) {
			dataC = Biblioteca.horaStrToCalendar(fechamento.getSaida04());
			segundosTrabalhados += Biblioteca.getHoraSeg(dataC);

			dataC = Biblioteca.horaStrToCalendar(fechamento.getEntrada01());
			segundosTrabalhados -= Biblioteca.getHoraSeg(dataC);
		}
		//par E5/S5
		if (fechamento.getEntrada05() != null && fechamento.getSaida05() != null) {
			dataC = Biblioteca.horaStrToCalendar(fechamento.getSaida05());
			segundosTrabalhados += Biblioteca.getHoraSeg(dataC);

			dataC = Biblioteca.horaStrToCalendar(fechamento.getEntrada01());
			segundosTrabalhados -= Biblioteca.getHoraSeg(dataC);
		}
		return Biblioteca.getHoraMinutoSegundo(segundosTrabalhados);
	}

	private String calculaCargaHorariaNoturna(PontoFechamentoJornada fechamento, PontoParametro parametro) {
		int segundosHoraNoturnaTrabalhada = 0;
		int segundosHoraSaida = 0;
		int segundosHoraEntrada = 0;
		int inicioHoraNoturna = Biblioteca.getHoraSeg(Biblioteca.horaStrToCalendar(parametro.getHoraNoturnaInicio()));
		int fimHoraNoturna = Biblioteca.getHoraSeg(Biblioteca.horaStrToCalendar(parametro.getHoraNoturnaFim()));

		//par E1/S1
		if (fechamento.getEntrada01() != null && fechamento.getSaida01() != null) {
			segundosHoraEntrada = Biblioteca.getHoraSeg(Biblioteca.horaStrToCalendar(fechamento.getEntrada01()));
			segundosHoraSaida = Biblioteca.getHoraSeg(Biblioteca.horaStrToCalendar(fechamento.getSaida01()));

			segundosHoraNoturnaTrabalhada += calculaSegundosHoraNoturna(segundosHoraEntrada, segundosHoraSaida, inicioHoraNoturna, fimHoraNoturna);
		}
		//par E2/S2
		if (fechamento.getEntrada02() != null && fechamento.getSaida02() != null) {
			segundosHoraEntrada = Biblioteca.getHoraSeg(Biblioteca.horaStrToCalendar(fechamento.getEntrada02()));
			segundosHoraSaida = Biblioteca.getHoraSeg(Biblioteca.horaStrToCalendar(fechamento.getSaida02()));

			segundosHoraNoturnaTrabalhada += calculaSegundosHoraNoturna(segundosHoraEntrada, segundosHoraSaida, inicioHoraNoturna, fimHoraNoturna);
		}
		//par E3/S3
		if (fechamento.getEntrada03() != null && fechamento.getSaida03() != null) {
			segundosHoraEntrada = Biblioteca.getHoraSeg(Biblioteca.horaStrToCalendar(fechamento.getEntrada03()));
			segundosHoraSaida = Biblioteca.getHoraSeg(Biblioteca.horaStrToCalendar(fechamento.getSaida03()));

			segundosHoraNoturnaTrabalhada += calculaSegundosHoraNoturna(segundosHoraEntrada, segundosHoraSaida, inicioHoraNoturna, fimHoraNoturna);
		}
		//par E4/S4
		if (fechamento.getEntrada04() != null && fechamento.getSaida04() != null) {
			segundosHoraEntrada = Biblioteca.getHoraSeg(Biblioteca.horaStrToCalendar(fechamento.getEntrada04()));
			segundosHoraSaida = Biblioteca.getHoraSeg(Biblioteca.horaStrToCalendar(fechamento.getSaida04()));

			segundosHoraNoturnaTrabalhada += calculaSegundosHoraNoturna(segundosHoraEntrada, segundosHoraSaida, inicioHoraNoturna, fimHoraNoturna);
		}
		//par E5/S5
		if (fechamento.getEntrada05() != null && fechamento.getSaida05() != null) {
			segundosHoraEntrada = Biblioteca.getHoraSeg(Biblioteca.horaStrToCalendar(fechamento.getEntrada05()));
			segundosHoraSaida = Biblioteca.getHoraSeg(Biblioteca.horaStrToCalendar(fechamento.getSaida05()));

			segundosHoraNoturnaTrabalhada += calculaSegundosHoraNoturna(segundosHoraEntrada, segundosHoraSaida, inicioHoraNoturna, fimHoraNoturna);
		}

		return Biblioteca.getHoraMinutoSegundo(segundosHoraNoturnaTrabalhada);
	}

	private int calculaSegundosHoraNoturna(int segundosHoraEntrada, int segundosHoraSaida, int inicioHoraNoturna, int fimHoraNoturna) {
		if (segundosHoraSaida > inicioHoraNoturna) {
			if (segundosHoraEntrada > inicioHoraNoturna) {
				return segundosHoraSaida - segundosHoraEntrada;
			} else {
				return segundosHoraSaida - inicioHoraNoturna;
			}
		}
		if (segundosHoraEntrada < fimHoraNoturna) {
			if (segundosHoraSaida < fimHoraNoturna) {
				return segundosHoraSaida - segundosHoraEntrada;
			} else {
				return fimHoraNoturna - segundosHoraEntrada;
			}
		}
		return 0;
	}

	private String calculaCargaHorariaDiurna(PontoFechamentoJornada fechamento) {
		int segundosCargaHorariaTotal = Biblioteca.getHoraSeg(Biblioteca.horaStrToCalendar(fechamento.getCargaHorariaTotal()));
		int segundosCargaHorariaNoturna = Biblioteca.getHoraSeg(Biblioteca.horaStrToCalendar(fechamento.getCargaHorariaNoturna()));

		return Biblioteca.getHoraMinutoSegundo(segundosCargaHorariaTotal - segundosCargaHorariaNoturna);
	}

	private void calculaHorasExtras(PontoFechamentoJornada fechamento, PontoParametro parametro) {

		int segundosTrabalhados = Biblioteca.getHoraSeg(Biblioteca.horaStrToCalendar(fechamento.getCargaHorariaTotal()));
		int segundosCargaHoraria = Biblioteca.getHoraSeg(Biblioteca.horaStrToCalendar(fechamento.getCargaHorariaEsperada()));
		int cargaHorariaNoturna = Biblioteca.getHoraSeg(Biblioteca.horaStrToCalendar(fechamento.getCargaHorariaNoturna()));
		int totalHoraExtra = 0;
		if (segundosTrabalhados > segundosCargaHoraria) {
			totalHoraExtra = segundosTrabalhados - segundosCargaHoraria;
			if (parametro.getTratamentoHoraMais().equals("E")) {
				if (cargaHorariaNoturna > 0) {
					if (cargaHorariaNoturna == totalHoraExtra) {
						fechamento.setHoraExtra01(Biblioteca.getHoraMinutoSegundo(cargaHorariaNoturna));
						fechamento.setPercentualHoraExtra01(parametro.getPercentualHeNoturna());
						fechamento.setModalidadeHoraExtra01("N");
					} else if (cargaHorariaNoturna < totalHoraExtra) {
						fechamento.setHoraExtra01(Biblioteca.getHoraMinutoSegundo(cargaHorariaNoturna));
						fechamento.setPercentualHoraExtra01(parametro.getPercentualHeNoturna());
						fechamento.setModalidadeHoraExtra01("N");

						fechamento.setHoraExtra02(Biblioteca.getHoraMinutoSegundo(totalHoraExtra - cargaHorariaNoturna));
						fechamento.setPercentualHoraExtra02(parametro.getPercentualHeDiurna());
						fechamento.setModalidadeHoraExtra02("D");
					}
				} else {
					fechamento.setPercentualHoraExtra01(parametro.getPercentualHeDiurna());
					fechamento.setModalidadeHoraExtra01("D");
				}
			} else if (parametro.getTratamentoHoraMais().equals("B")) {
				fechamento.setCompensar("1");
				if (cargaHorariaNoturna > 0) {
					if ((cargaHorariaNoturna == totalHoraExtra) || cargaHorariaNoturna > totalHoraExtra) {
						fechamento.setBancoHoras(Biblioteca.getHoraMinutoSegundo(totalHoraExtra * (8 / 7)));
					} else if (cargaHorariaNoturna < totalHoraExtra) {
						fechamento.setBancoHoras(Biblioteca.getHoraMinutoSegundo((cargaHorariaNoturna * (8 / 7)) + (totalHoraExtra - cargaHorariaNoturna)));
					}
				} else {
					fechamento.setBancoHoras(Biblioteca.getHoraMinutoSegundo(totalHoraExtra));
				}
			}
		} else if (segundosTrabalhados < segundosCargaHoraria) {
			totalHoraExtra = segundosCargaHoraria - segundosTrabalhados;
			if (parametro.getTratamentoHoraMenos().equals("F")) {
				fechamento.setFaltaAtraso(Biblioteca.getHoraMinutoSegundo(totalHoraExtra));
			} else {
				fechamento.setCompensar("2");
				fechamento.setBancoHoras(Biblioteca.getHoraMinutoSegundo(totalHoraExtra));
			}
		}
	}

	public void gravaFechamento() {
		try {
	        boolean registroPendente = false;

	        if (!listaFechamentoJornada.isEmpty()) {
	            for (int i = 0; i < listaFechamentoJornada.size(); i++) {
	                if (listaFechamentoJornada.get(i).getObservacao() != null) {
	                    registroPendente = true;
	                    break;
	                }
	            }
	            if (registroPendente) {
	            	FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_WARN, "Existem registros de entrada e/ou saída pendentes!", "Gravação não efetuada.");
	            } else {
	            	for (PontoFechamentoJornada f : listaFechamentoJornada) {
	            		dao.persist(f);
	            	}
	                FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_INFO, "Fechamento salvo com sucesso!", "");
	            }
	        } else {
	        	FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_WARN, "Nenhum registro para gravar.", "");
	        }
		} catch (Exception e) {
			e.printStackTrace();
			FacesContextUtil.adicionaMensagem(FacesMessage.SEVERITY_ERROR, "Erro ao gravar o fechamento.!", e.getMessage());
		}
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

	public List<AFDTipo3> getListaAFDTipo3() {
		return listaAFDTipo3;
	}

	public void setListaAFDTipo3(List<AFDTipo3> listaAFDTipo3) {
		this.listaAFDTipo3 = listaAFDTipo3;
	}

	public AFDTipo3 getAfdTipo3Selecionado() {
		return afdTipo3Selecionado;
	}

	public void setAfdTipo3Selecionado(AFDTipo3 afdTipo3Selecionado) {
		this.afdTipo3Selecionado = afdTipo3Selecionado;
	}

	public List<PontoMarcacao> getListaMarcacaoIncluida() {
		return listaMarcacaoIncluida;
	}

	public void setListaMarcacaoIncluida(List<PontoMarcacao> listaMarcacaoIncluida) {
		this.listaMarcacaoIncluida = listaMarcacaoIncluida;
	}

	public PontoMarcacao getPontoMarcacao() {
		return pontoMarcacao;
	}

	public void setPontoMarcacao(PontoMarcacao pontoMarcacao) {
		this.pontoMarcacao = pontoMarcacao;
	}

	public PontoMarcacao getPontoMarcacaoSelecionado() {
		return pontoMarcacaoSelecionado;
	}

	public void setPontoMarcacaoSelecionado(PontoMarcacao pontoMarcacaoSelecionado) {
		this.pontoMarcacaoSelecionado = pontoMarcacaoSelecionado;
	}

	public List<PontoFechamentoJornada> getListaFechamentoJornada() {
		return listaFechamentoJornada;
	}

	public void setListaFechamentoJornada(List<PontoFechamentoJornada> listaFechamentoJornada) {
		this.listaFechamentoJornada = listaFechamentoJornada;
	}

}
