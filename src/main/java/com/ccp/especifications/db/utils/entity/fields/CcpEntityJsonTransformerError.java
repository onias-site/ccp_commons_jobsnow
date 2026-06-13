package com.ccp.especifications.db.utils.entity.fields;

/**
 * Exceção lançada por um transformador de campo de entidade quando a transformação não pode ser
 * aplicada ao valor presente no JSON. Capturada silenciosamente por
 * {@code DecoratorFieldsTransformerEntity} para ignorar campos não transformáveis.
 */
@SuppressWarnings("serial")
public class CcpEntityJsonTransformerError extends RuntimeException{

	/** Armazena a mensagem descritiva do erro de transformação. */
	public CcpEntityJsonTransformerError(String message) {
		super(message);
	}
}
