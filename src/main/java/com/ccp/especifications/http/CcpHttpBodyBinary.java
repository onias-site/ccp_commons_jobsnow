package com.ccp.especifications.http;

/**
 * Representa uma parte binária de uma requisição HTTP multipart
 * (tipo de conteúdo, nome do campo, nome do arquivo e bytes).
 */
public class CcpHttpBodyBinary {

	public final CcpHttpContentType contentType;
	public final String fileName;
	public final Byte[] bytes;
	public final String name;

	/**
	 * Inicializa os atributos da parte binária.
	 * @param contentType tipo de conteúdo do arquivo
	 * @param name nome do campo no formulário multipart
	 * @param fileName nome do arquivo
	 * @param bytes conteúdo binário do arquivo
	 */
	public CcpHttpBodyBinary(CcpHttpContentType contentType, String name, String fileName, Byte[] bytes) {
		
		this.contentType = contentType;
		this.fileName = fileName;
		this.bytes = bytes;
		this.name = name;
	}
	
	/**
	 * Converte {@code Byte[]} (wrapper) para {@code byte[]} primitivo.
	 * @return array de bytes primitivos
	 */
	public byte[] getBytes() {
		
		int k = 0;
		byte[] result = new byte[this.bytes.length];
		
		for (Byte _byte : this.bytes) {
			result[k++] = _byte; 
		}
		return result;
	}
}
