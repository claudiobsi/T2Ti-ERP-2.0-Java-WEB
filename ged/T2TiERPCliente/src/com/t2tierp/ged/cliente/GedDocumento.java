package com.t2tierp.ged.cliente;

import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Base64;

import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

import com.google.gson.GsonBuilder;
import com.t2tierp.model.bean.ged.Arquivo;
import com.t2tierp.util.Biblioteca;

public class GedDocumento {

	private String serverURL;
	private String codigoRequisicao;
	
	public static void main(String[] args) {
		new GedDocumento().digitalizaDocumento();
	}

	public GedDocumento() {
		serverURL = System.getProperty("jnlp.serverURL");
		codigoRequisicao = System.getProperty("jnlp.codigoRequisicao");
	}
	
	public void digitalizaDocumento() {
		try {
			Arquivo arquivo = acessaScanner();
			enviaDocumento(converteJson(arquivo));
			JOptionPane.showMessageDialog(null, "Documento Enviado", "Informação do Sistema", JOptionPane.INFORMATION_MESSAGE);
		} catch (Exception e) {
			e.printStackTrace();
			JOptionPane.showMessageDialog(null, "Ocorreu um erro!\n" + e.getMessage(), "Erro do Sistema", JOptionPane.ERROR_MESSAGE);
		} finally {
			System.exit(0);
		}
	}

	private Arquivo acessaScanner() throws Exception {
		try {
			DigitalizaDocumento dd;
			dd = new DigitalizaDocumento();
			Object documento = dd.getDocumento();

			if (documento != null) {
				if (documento instanceof Exception) {
					throw (Exception) documento;
				}
				Arquivo arquivo = new Arquivo();
				if (documento instanceof BufferedImage) {
					//converte a imagem digitalizada em bytes
					ByteArrayOutputStream baos = new ByteArrayOutputStream();
					ImageIO.write((BufferedImage) documento, "jpg", baos);
					byte[] bytesOut = baos.toByteArray();

					arquivo.setFile(bytesOut);
					arquivo.setExtensao(".jpg");
					arquivo.setHash(getHashDocumento(arquivo));
				}
				if (documento instanceof File) {
					File file = (File) documento;
					arquivo.setFile(Biblioteca.getBytesFromFile(file));
					arquivo.setExtensao(getExtensaoArquivo(file.getAbsolutePath()));
					arquivo.setHash(getHashDocumento(arquivo));
				}
				return arquivo;
			} else {
				throw new Exception("Imagem não obtida no dispositivo");
			}
		} catch (Exception e) {
			e.printStackTrace();
			throw e;
		}
	}

	private String converteJson(Arquivo arquivo) throws Exception {
		arquivo.setFileBase64(Base64.getEncoder().encodeToString(arquivo.getFile()));
		arquivo.setFile(null);
		return new GsonBuilder().create().toJson(arquivo);
	}
	
	private void enviaDocumento(String documentoJson) throws Exception {
		URL url = new URL(serverURL + "/" + codigoRequisicao);

		HttpURLConnection connection = (HttpURLConnection) url.openConnection();
		connection.setConnectTimeout(5000);

		connection.setRequestMethod("POST");
		connection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
		connection.setDoOutput(true);

		OutputStream os = connection.getOutputStream();
		os.write(documentoJson.getBytes("UTF-8"));
		os.close();

		connection.connect();
		int status = connection.getResponseCode();
		if (status != HttpURLConnection.HTTP_OK) {
			throw new Exception("Status " + status);
		}
	}
	
	private String getHashDocumento(Arquivo arquivo) throws Exception {
		File file = File.createTempFile("arquivo", arquivo.getExtensao());
		file.deleteOnExit();
		Files.write(Paths.get(file.toURI()), arquivo.getFile());

		return Biblioteca.md5Arquivo(file.getAbsolutePath());
	}

	private String getExtensaoArquivo(String caminhoArquivo) {
		if (!caminhoArquivo.equals("")) {
			int indiceExtensao = caminhoArquivo.lastIndexOf(".");
			if (indiceExtensao > -1) {
				return caminhoArquivo.substring(indiceExtensao, caminhoArquivo.length());
			}
		}
		return "";
	}

}
