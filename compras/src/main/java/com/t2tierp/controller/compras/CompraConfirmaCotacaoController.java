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
package com.t2tierp.controller.compras;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.PrintStream;
import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.ejb.EJB;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ViewScoped;

import org.apache.commons.io.FileUtils;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.RowEditEvent;
import org.primefaces.model.UploadedFile;

import com.t2tierp.controller.AbstractController;
import com.t2tierp.controller.T2TiLazyDataModel;
import com.t2tierp.model.bean.compras.CompraCotacao;
import com.t2tierp.model.bean.compras.CompraCotacaoDetalhe;
import com.t2tierp.model.bean.compras.CompraFornecedorCotacao;
import com.t2tierp.model.dao.InterfaceDAO;
import com.t2tierp.util.FacesContextUtil;

@ManagedBean
@ViewScoped
public class CompraConfirmaCotacaoController extends AbstractController<CompraCotacao> implements Serializable {

	private static final long serialVersionUID = 1L;
	private CompraConfirmaCotacaoDataModel dataModel;

	@EJB
	private InterfaceDAO<CompraFornecedorCotacao> compraFornecedorCotacaoDao;
	private CompraFornecedorCotacao compraFornecedorCotacao;
	private CompraFornecedorCotacao compraFornecedorCotacaoSelecionado;

	@Override
	public Class<CompraCotacao> getClazz() {
		return CompraCotacao.class;
	}

	@Override
	public String getFuncaoBase() {
		return "COMPRA_CONFIRMA_COTACAO";
	}

	@Override
	public T2TiLazyDataModel<CompraCotacao> getDataModel() {
		if (dataModel == null) {
			dataModel = new CompraConfirmaCotacaoDataModel();
			dataModel.setClazz(getClazz());
			dataModel.setDao(dao);
		}
		return dataModel;
	}
	
	@Override
	public void salvar() {
		try {
			setObjeto(dao.getBeanJoinFetch(getObjeto().getId(), getClazz()));

			BigDecimal valorTotalItens;
            boolean fornecedorContemValor = false;
            for (CompraFornecedorCotacao f : getObjeto().getListaCompraFornecedorCotacao()) {
                if (f.getValorSubtotal() != null && f.getTotal() != null) {
    				f = compraFornecedorCotacaoDao.getBeanJoinFetch(f.getId(), CompraFornecedorCotacao.class);
                    valorTotalItens = BigDecimal.ZERO;
    				for (CompraCotacaoDetalhe d : f.getListaCompraCotacaoDetalhe()) {
    					valorTotalItens = valorTotalItens.add(d.getValorTotal());
    				}
                    if (f.getTotal().compareTo(valorTotalItens) != 0) {
                        throw new Exception("Valor Total dos Itens do fornecedor '" + f.getFornecedor().getPessoa().getNome() + "' não corresponde ao total informado!\nValor Calculado: " + valorTotalItens);
                    }
                    fornecedorContemValor = true;
                }
            }

            if (!fornecedorContemValor){
                throw new Exception("Nenhum fornecedor informou valores de cotação");
            }

            getObjeto().setSituacao("C");
			
            dao.merge(getObjeto());

            voltar();
			
            FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_INFO, "Cotação confirmada com sucesso!", "");
		} catch (Exception e) {
			e.printStackTrace();
			FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro ao salvar o registro!", e.getMessage());
		}
	}

	public void selecionarFornecedor() {
		try {
			compraFornecedorCotacao = compraFornecedorCotacaoDao.getBeanJoinFetch(compraFornecedorCotacaoSelecionado.getId(), CompraFornecedorCotacao.class);
		} catch (Exception e) {
			e.printStackTrace();
			FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro ao buscar os dados do fornecedor!", e.getMessage());
		}
	}

	public void salvarDadosFornecedor(RowEditEvent event) {
		try {
			if (event != null) {
				compraFornecedorCotacao = (CompraFornecedorCotacao) event.getObject();
			}
			compraFornecedorCotacaoDao.merge(compraFornecedorCotacao);
			FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_INFO, "Dados do fornecedor salvo com sucesso!", null);
		} catch (Exception e) {
			e.printStackTrace();
			FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_ERROR, "Ocorreu um erro ao salvar o registro!", e.getMessage());
		}
	}

	public void alteraItemFornecedor(RowEditEvent event) {
		salvarDadosFornecedor(null);
	}

	public void exportaCsvFornecedor() {
		try {
			final int BUFFER = 2048;
			BufferedInputStream origin = null;
			File arquivoZip = File.createTempFile("fornecedores", ".zip");
			arquivoZip.deleteOnExit();
			FileOutputStream dest = new FileOutputStream(arquivoZip);
			ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));
			byte data[] = new byte[BUFFER];
			String nomeArquivo;
			String linhasArquivo;
			for (CompraFornecedorCotacao f : getObjeto().getListaCompraFornecedorCotacao()) {
				nomeArquivo = "Fornecedor_" + f.getCompraCotacao().getId() + "_" + f.getId();
				linhasArquivo = "Id Produto;Nome Produto;Quantidade;Valor Unitário;Valor Subtotal;Taxa Desconto;Valor Desconto;Valor Total" + System.getProperty("line.separator");
				f = compraFornecedorCotacaoDao.getBeanJoinFetch(f.getId(), CompraFornecedorCotacao.class);
				for (CompraCotacaoDetalhe d : f.getListaCompraCotacaoDetalhe()) {
					linhasArquivo += d.getProduto().getId() + ";" + d.getProduto().getNome() + ";" + d.getQuantidade() + ";" + "0;0;0;0;0" + System.getProperty("line.separator");
				}
				File arquivoCSV = File.createTempFile(nomeArquivo, ".csv");
				arquivoCSV.deleteOnExit();

				PrintStream printStream = new PrintStream(arquivoCSV);
				printStream.print(linhasArquivo);
				printStream.close();

				//adiciona ao arquivo compactado
				FileInputStream fi = new FileInputStream(arquivoCSV);
				origin = new BufferedInputStream(fi, 20);
				ZipEntry entry = new ZipEntry(nomeArquivo + ".csv");
				out.putNextEntry(entry);
				int count;
				while ((count = origin.read(data, 0, BUFFER)) != -1) {
					out.write(data, 0, count);
				}
				origin.close();
			}
			out.close();

			FacesContextUtil.downloadArquivo(arquivoZip, "fornecedores.zip");
		} catch (Exception e) {
			e.printStackTrace();
			FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_ERROR, "Erro na exportação do arquivo.!", e.getMessage());
		}
	}

	public void importaCsvFornecedor(FileUploadEvent event) throws Exception {
		try {
			if (compraFornecedorCotacaoSelecionado == null) {
				throw new Exception("Nenhum fornecedor selecionado!");
			}

			UploadedFile arquivo = event.getFile();

			compraFornecedorCotacao = compraFornecedorCotacaoDao.getBeanJoinFetch(compraFornecedorCotacaoSelecionado.getId(), CompraFornecedorCotacao.class);
			File file = File.createTempFile("file", ".tmp");
			FileUtils.copyInputStreamToFile(arquivo.getInputstream(), file) ;
			List<String> lines = FileUtils.readLines(file);

			int idProduto;
			BigDecimal valorSubtotal = BigDecimal.ZERO;
			BigDecimal taxaDesconto = BigDecimal.ZERO;
			BigDecimal valorDesconto = BigDecimal.ZERO;
			BigDecimal valorTotal = BigDecimal.ZERO;
			String linhaArquivo[];
			List<String> linhaErro = new ArrayList<String>();
			for (int i = 0; i < lines.size(); i++) {
				if (i != 0) {
					linhaArquivo = lines.get(i).split(";");
					for (CompraCotacaoDetalhe d : compraFornecedorCotacao.getListaCompraCotacaoDetalhe()) {
						try {
							idProduto = Integer.valueOf(linhaArquivo[0]);
							if (d.getProduto().getId().intValue() == idProduto) {
								d.setValorUnitario(BigDecimal.valueOf(Double.valueOf(linhaArquivo[3])));
								d.setValorSubtotal(BigDecimal.valueOf(Double.valueOf(linhaArquivo[4])));
								valorSubtotal = valorSubtotal.add(d.getValorSubtotal());
								d.setTaxaDesconto(BigDecimal.valueOf(Double.valueOf(linhaArquivo[5])));
								taxaDesconto = taxaDesconto.add(d.getTaxaDesconto());
								d.setValorDesconto(BigDecimal.valueOf(Double.valueOf(linhaArquivo[6])));
								valorDesconto = valorDesconto.add(d.getValorDesconto());
								d.setValorTotal(BigDecimal.valueOf(Double.valueOf(linhaArquivo[7])));
								valorTotal = valorTotal.add(d.getValorTotal());

								break;
							}
						} catch (Exception e) {
							linhaErro.add("Linha " + (i + 1) + ": " + lines.get(i) + "\n" + "Erro: " + e.getMessage() + "\n");
						}
					}
				}
			}
			compraFornecedorCotacao.setValorSubtotal(valorSubtotal);
			compraFornecedorCotacao.setTaxaDesconto(taxaDesconto);
			compraFornecedorCotacao.setValorDesconto(valorDesconto);
			compraFornecedorCotacao.setTotal(valorTotal);

			compraFornecedorCotacaoDao.merge(compraFornecedorCotacao);

			String mensagem = "";
			if (!linhaErro.isEmpty()) {
				for (int i = 0; i < linhaErro.size(); i++) {
					mensagem += linhaErro.get(i);
				}
				throw new Exception(mensagem);
			}
			FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_INFO, "Arquivo importado com sucesso.!", null);
		} catch (Exception e) {
			e.printStackTrace();
			FacesContextUtil.adiconaMensagem(FacesMessage.SEVERITY_ERROR, "Erro na importação do arquivo.!", e.getMessage());
		}

	}

	public CompraFornecedorCotacao getCompraFornecedorCotacaoSelecionado() {
		return compraFornecedorCotacaoSelecionado;
	}

	public void setCompraFornecedorCotacaoSelecionado(CompraFornecedorCotacao compraFornecedorCotacaoSelecionado) {
		this.compraFornecedorCotacaoSelecionado = compraFornecedorCotacaoSelecionado;
	}

	public CompraFornecedorCotacao getCompraFornecedorCotacao() {
		return compraFornecedorCotacao;
	}

	public void setCompraFornecedorCotacao(CompraFornecedorCotacao compraFornecedorCotacao) {
		this.compraFornecedorCotacao = compraFornecedorCotacao;
	}

}
