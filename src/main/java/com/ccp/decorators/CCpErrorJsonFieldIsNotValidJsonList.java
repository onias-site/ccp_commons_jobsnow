package com.ccp.decorators;

import java.util.Arrays;

/**
 * Exceção lançada quando se tenta acessar um campo de um {@code CcpJsonRepresentation} esperando uma lista JSON,
 * mas o valor real é de outro tipo incompatível (por exemplo, um {@code Long} ou {@code String} comum).
 */
@SuppressWarnings("serial")
public class CCpErrorJsonFieldIsNotValidJsonList extends RuntimeException {
	/**
	 * Monta a mensagem de erro indicando qual caminho de campos foi acessado, qual tipo foi encontrado
	 * e qual era o JSON completo no momento do erro.
	 * @param json o JSON no momento do erro
	 * @param clazz o tipo real encontrado no campo
	 * @param path o caminho de campos acessado
	 */
	public CCpErrorJsonFieldIsNotValidJsonList(CcpJsonRepresentation json, Class<?> clazz, String... path) {
		super("The field path '" + Arrays.asList(path) + "' in the json does not represent a json type, instead, it represents a '" + clazz.getName() + "' in the json " + json);
	}
}
