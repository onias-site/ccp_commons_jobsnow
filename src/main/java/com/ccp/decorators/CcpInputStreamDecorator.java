package com.ccp.decorators;

import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.net.URL;


/**
 * Decorator sobre um identificador de recurso (nome de variável de ambiente, caminho de arquivo ou recurso no classpath)
 * que resolve e abre um {@code InputStream} de diferentes fontes. Suporta fallback automático entre as fontes.
 */
public class CcpInputStreamDecorator implements CcpDecorator<String> {

	private final String content;

	/**
	 * Encapsula o identificador do recurso.
	 * @param content o nome ou caminho do recurso
	 */
	protected CcpInputStreamDecorator(String content) {
		this.content = content;
	}
	
	public String toString() {
		return this.content;
	}

	/**
	 * Lê a variável de ambiente cujo nome é o conteúdo encapsulado. Se o valor for um caminho de arquivo existente,
	 * abre o arquivo; caso contrário, converte o valor em {@code ByteArrayInputStream}.
	 * Lança {@code CcpErrorInputStreamMissing} se a variável não existir ou estiver vazia.
	 */
	public InputStream environmentVariables() {
		
		String getenv = System.getenv(this.content);
		boolean getenvIgual = getenv == null;

		if(getenvIgual) {
			CcpErrorInputStreamMissing ccpErrorInputStreamMissing = new CcpErrorInputStreamMissing(this.content);
			throw ccpErrorInputStreamMissing;
		}
		String getenvTrim = getenv.trim();
		boolean getenvTrimEmpty = getenvTrim.isEmpty();

		if(getenvTrimEmpty) {
			CcpErrorInputStreamMissing ccpErrorInputStreamMissing2 = new CcpErrorInputStreamMissing(this.content);
			throw ccpErrorInputStreamMissing2;
		}

		CcpStringDecorator csd = new CcpStringDecorator(getenv);
		CcpFileDecorator file = csd.file();
		boolean fileFile = file.isFile();

		if(fileFile) {
			CcpInputStreamDecorator inputStreamFrom = csd.inputStreamFrom();
			InputStream file2 = inputStreamFrom.file();
			return file2;
		}
		
		byte[] bytes = getenv.getBytes();
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
		return byteArrayInputStream;
	}
	
	/**
	 * Abre o recurso como arquivo do classpath (via {@code ClassLoader.getResource}).
	 * Lança {@code CcpErrorInputStreamMissing} se o recurso não for encontrado.
	 */
	public InputStream classLoader() {
		Class<? extends CcpInputStreamDecorator> class1 = this.getClass();
		ClassLoader classLoader = class1.getClassLoader();
		URL resource = classLoader.getResource(this.content);
		boolean resourceIgual = resource == null;
		if(resourceIgual) {
			CcpErrorInputStreamMissing ccpErrorInputStreamMissing3 = new CcpErrorInputStreamMissing(this.content);
			throw ccpErrorInputStreamMissing3;
		}
		InputStream stream = resource.openStream(); 
		return stream;
	} 
	
	/**
	 * Abre o arquivo do sistema de arquivos pelo caminho encapsulado.
	 * Lança {@code CcpErrorInputStreamMissing} se o arquivo não existir.
	 */
	public InputStream file() {
		CcpStringDecorator ccpStringDecorator = new CcpStringDecorator(this.content);
		CcpFileDecorator file = ccpStringDecorator.file();
		boolean exists = file.exists();
		boolean notExists = false == exists;
		if(notExists) {
			CcpErrorInputStreamMissing ccpErrorInputStreamMissing4 = new CcpErrorInputStreamMissing(this.content);
			throw ccpErrorInputStreamMissing4;
		}
		FileInputStream fileInputStream = new FileInputStream(this.content);
		return fileInputStream;
	}
	
	/**
	 * Converte o conteúdo textual diretamente em {@code ByteArrayInputStream}.
	 */
	public InputStream byteArray() {
		byte[] bytes = this.content.getBytes();
		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(bytes);
		return byteArrayInputStream;
	}
	
	/**
	 * Tenta as três fontes em sequência (variável de ambiente → classpath → arquivo) e retorna o primeiro {@code InputStream} encontrado com sucesso.
	 */
	public InputStream fromEnvironmentVariablesOrClassLoaderOrFile() {

		try {
			InputStream is = this.environmentVariables();
			return is;
		} catch (CcpErrorInputStreamMissing e) {

		}
		
		try {
			InputStream is = this.classLoader();
			return is;
		} catch (CcpErrorInputStreamMissing e) {

		}
		InputStream is = this.file();
		return is;
	}

	/**
	 * Implementação de {@code CcpDecorator}; retorna o identificador do recurso.
	 */
	public String getContent() {
		return this.content;
	}

}
