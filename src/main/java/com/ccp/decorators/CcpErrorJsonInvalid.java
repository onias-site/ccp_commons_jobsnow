package com.ccp.decorators;

/**
 * Exceção lançada quando uma string não pode ser desserializada como JSON válido.
 */
@SuppressWarnings("serial")
public class CcpErrorJsonInvalid extends RuntimeException {
	/**
	 * Monta a mensagem indicando a string inválida e encadeia a exceção original como causa.
	 * @param json a string que falhou na desserialização
	 * @param e a exceção original
	 */
	public CcpErrorJsonInvalid(String json, Throwable e) {
		super("The following json is an invalid json: " + json , e);

	}
}
