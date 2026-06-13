package com.ccp.decorators;

/**
 * Exceção lançada quando métodos de navegação por caminho em {@code CcpJsonRepresentation} recebem um array de campos vazio,
 * o que indica erro de programação (caminho não informado).
 */
@SuppressWarnings("serial")
public class CcpErrorJsonPathIsMissing extends RuntimeException {
	/**
	 * Monta a mensagem pedindo que o caminho seja preenchido, incluindo o JSON que recebeu a chamada.
	 * @param json o JSON no momento do erro
	 */
	public CcpErrorJsonPathIsMissing(CcpJsonRepresentation json) {
		super("The path is empty, please fill the missing path in the json: " + json);
	}
}
