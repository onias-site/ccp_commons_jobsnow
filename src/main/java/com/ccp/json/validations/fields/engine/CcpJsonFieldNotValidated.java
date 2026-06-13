package com.ccp.json.validations.fields.engine;

/**
 * Exceção de controle de fluxo lançada quando um campo não possui nenhuma anotação de tipo
 * reconhecida. Capturada silenciosamente para pular o campo.
 */
@SuppressWarnings("serial")
public class CcpJsonFieldNotValidated extends RuntimeException {

}
