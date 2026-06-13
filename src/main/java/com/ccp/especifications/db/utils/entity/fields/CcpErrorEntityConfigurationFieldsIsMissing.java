package com.ccp.especifications.db.utils.entity.fields;

/**
 * Exceção lançada quando a classe configuradora de uma entidade não declara a inner class enum
 * {@code Fields} obrigatória, impedindo que {@code CcpEntityFactory} extraia os campos da entidade.
 */
@SuppressWarnings("serial")
public class CcpErrorEntityConfigurationFieldsIsMissing extends RuntimeException{

	/** Gera a mensagem indicando qual classe está com a declaração ausente. */
	public CcpErrorEntityConfigurationFieldsIsMissing(Class<?> configurationClass) {
		super("The class '" + configurationClass.getName() + "' must declare a public static enum called 'FIELDS'");
	}
}
