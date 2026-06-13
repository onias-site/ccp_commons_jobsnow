package com.ccp.especifications.mensageria.receiver;

/**
 * Exceção lançada quando o nome de processo informado ao receiver não corresponde a nenhum tópico válido.
 */
@SuppressWarnings("serial")
public class CcpErrorMensageriaInvalidName extends RuntimeException {

	/**
	 * Inclui o nome do processo inválido na mensagem de erro.
	 * @param processName nome do processo que não corresponde a um tópico válido
	 */
	public CcpErrorMensageriaInvalidName(String processName) {
		super("The process '" + processName + "' is an invalid topic");
	}
}
