package com.ccp.especifications.http;

/**
 * Representa uma parte textual de uma requisição HTTP multipart.
 */
public class CcpHttpBodyText {
	public final CcpHttpContentType contentType;
	public final String name;
	public final String text;

	/**
	 * Inicializa os atributos da parte textual.
	 * @param contentType tipo de conteúdo do texto
	 * @param name nome do campo no formulário multipart
	 * @param text conteúdo textual
	 */
	public CcpHttpBodyText(CcpHttpContentType contentType, String name, String text) {
		this.contentType = contentType;
		this.name = name;
		this.text = text;
	}
	
	
}
