package com.ccp.especifications.db.bulk;

import java.util.function.Supplier;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.utils.entity.CcpEntity;
import com.ccp.especifications.db.utils.entity.decorators.engine.CcpEntityMetaData;

/**
 * Exceção lançada quando uma operação bulk de delete ou update tenta acessar um registro que não
 * existe na entidade do banco de dados. A mensagem inclui o id e o nome da entidade, e
 * opcionalmente os valores que compõem a chave primária.
 */
@SuppressWarnings("serial")
public class CcpErrorBulkEntityRecordNotFound extends RuntimeException{

	/**
	 * Cria a exceção informando diretamente o nome da entidade e o id não encontrado.
	 *
	 * @param entityName nome da entidade
	 * @param id identificador não encontrado
	 */
	public CcpErrorBulkEntityRecordNotFound(String entityName, String id) {
		super(getErrorMessage(entityName, id));
	}

	/**
	 * Cria a exceção calculando o id a partir do JSON e da entidade, incluindo na mensagem os
	 * valores dos campos da chave primária que foram usados para compor o id.
	 *
	 * @param entity entidade onde o registro não foi encontrado
	 * @param json JSON com os valores usados para calcular o id
	 */
	public CcpErrorBulkEntityRecordNotFound(CcpEntity entity, CcpJsonRepresentation json) {
		super(getErrorMessage(entity, json));
	}
	

	private static String getErrorMessage(String entityName, String id) {
		String errorMessage = String.format("Does not exist an id '%s' registered in the entity '%s'.", 
				id,	entityName);

		return errorMessage;
	}


	private static String getErrorMessage(CcpEntity entity, CcpJsonRepresentation json) {

		CcpEntityMetaData entityDetails = entity.getEntityMetaData();
		Supplier<CcpJsonRepresentation> supplier = json.getJsonSupplier();
		CcpJsonRepresentation primaryKeyValues = entityDetails.getPrimaryKeyValues(supplier);
		String id = entity.calculateId(json);
		
		String errorMessage = String.format("Does not exist an id '%s' registered in the entity '%s'. Values to compose this id are: %s ", 
				id,
				entityDetails.entityName,
				primaryKeyValues);

		return errorMessage;
	}
	
}
