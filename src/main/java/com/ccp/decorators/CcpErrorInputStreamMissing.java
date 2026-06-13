package com.ccp.decorators;

/**
 * Exceção lançada quando um recurso (arquivo, variável de ambiente ou recurso do classpath) referenciado para abertura
 * de {@code InputStream} não existe ou está vazio.
 */
@SuppressWarnings("serial")
public class CcpErrorInputStreamMissing extends RuntimeException{
	/**
	 * Monta a mensagem indicando o caminho/nome do recurso ausente.
	 * @param filePath o caminho ou nome do recurso ausente
	 */
	public CcpErrorInputStreamMissing(String filePath) {
		super("The file '" + filePath + "' is missing");
	}
}
