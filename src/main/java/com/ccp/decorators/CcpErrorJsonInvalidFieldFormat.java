package com.ccp.decorators;

/**
 * Exceção lançada quando o valor de um campo existe no JSON, mas não pode ser convertido para o tipo esperado
 * (por exemplo, tentar ler {@code "abc"} como {@code long}).
 */
@SuppressWarnings("serial")
public class CcpErrorJsonInvalidFieldFormat extends RuntimeException {

	/**
	 * Monta a mensagem indicando o valor encontrado, o nome do campo, o tipo esperado e o JSON completo.
	 * @param value o valor encontrado no campo
	 * @param fieldName o nome do campo
	 * @param fieldType o tipo esperado
	 * @param json o JSON no momento do erro
	 */
	public CcpErrorJsonInvalidFieldFormat(Object value, String fieldName, String fieldType, CcpJsonRepresentation json) {
		super("The value '" + value + "' from the field '" + fieldName + " is not a '" + fieldType + "' in the following json: " + json);
	}
	
}
