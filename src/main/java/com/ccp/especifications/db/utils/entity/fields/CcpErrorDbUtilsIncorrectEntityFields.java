package com.ccp.especifications.db.utils.entity.fields;

/**
 * Exceção lançada quando os campos de uma entidade não correspondem ao mapeamento esperado no banco
 * de dados. Usada durante a configuração inicial do banco via {@code CcpDbRequester.executeDatabaseSetup}.
 */
@SuppressWarnings("serial")
public class CcpErrorDbUtilsIncorrectEntityFields extends RuntimeException {

	/** Armazena a mensagem descritiva do erro de campos incorretos. */
	public CcpErrorDbUtilsIncorrectEntityFields(String messageError) {
		super(messageError);
	}
}
