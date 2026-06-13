package com.ccp.decorators;

/**
 * Exceção lançada quando se tenta obter o valor de um campo obrigatório de um {@code CcpJsonRepresentation} e esse campo está ausente.
 */
@SuppressWarnings("serial")
public class CcpErrorJsonFieldNotFound extends RuntimeException {
	/**
	 * Monta a mensagem informando qual campo estava ausente e o conteúdo completo do JSON no momento do erro.
	 * @param field o nome do campo ausente
	 * @param json o JSON no momento do erro
	 */
	public CcpErrorJsonFieldNotFound(String field, CcpJsonRepresentation json) {
		super("The value is absent to the field " + field + " in json: " + json);
	}
}
