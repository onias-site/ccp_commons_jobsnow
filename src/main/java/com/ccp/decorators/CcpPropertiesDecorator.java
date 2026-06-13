package com.ccp.decorators;

import java.io.InputStream;

/**
 * Decorator especializado em leitura de arquivos de configuração (formato {@code .properties} ou JSON).
 * Delega a abertura do stream para {@code CcpInputStreamDecorator} e converte o resultado em
 * {@code CcpJsonRepresentation}, permitindo acessar configurações como um mapa JSON uniforme.
 */
public class CcpPropertiesDecorator implements CcpDecorator<CcpInputStreamDecorator> {

	private final CcpInputStreamDecorator content;

	/**
	 * Encapsula o identificador do recurso.
	 */
	protected CcpPropertiesDecorator(String content) {
		this.content = new CcpInputStreamDecorator(content);
	}

	private CcpJsonRepresentation getMapInInputStream(InputStream is) {
		CcpJsonRepresentation response = new CcpJsonRepresentation(is);
		return response;

	}
	
	/**
	 * Carrega as configurações a partir de uma variável de ambiente.
	 */
	public CcpJsonRepresentation environmentVariables() {
		InputStream is = this.content.environmentVariables();
		CcpJsonRepresentation result = this.getMapInInputStream(is);
		return result;
	}

	/**
	 * Carrega as configurações a partir do classpath.
	 */
	public CcpJsonRepresentation classLoader() {
		InputStream is = this.content.classLoader();
		CcpJsonRepresentation result = this.getMapInInputStream(is);
		return result;
	}

	/**
	 * Carrega as configurações a partir de um arquivo no sistema de arquivos.
	 */
	public CcpJsonRepresentation file() {
		InputStream is = this.content.file();
		CcpJsonRepresentation result = this.getMapInInputStream(is);
		return result;
	}

	/**
	 * Tenta as três fontes em sequência e retorna as configurações da primeira disponível.
	 */
	public CcpJsonRepresentation environmentVariablesOrClassLoaderOrFile() {
		InputStream is = this.content.fromEnvironmentVariablesOrClassLoaderOrFile();
		CcpJsonRepresentation result = this.getMapInInputStream(is);
		return result;
	}

	/**
	 * Implementação de {@code CcpDecorator}; retorna o decorator de stream interno.
	 */
	public CcpInputStreamDecorator getContent() {
		return this.content;
	}
	
	
	
}
