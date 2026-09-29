package com.ccp.especifications.db.bulk;

import java.util.function.Supplier;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.utils.entity.CcpEntity;
import com.ccp.especifications.db.utils.entity.decorators.engine.CcpEntityMetaData;

/**
 * Exception thrown when a delete or update bulk operation tries to access a record that does not
 * exist in the database entity. The message includes the id and the entity name and,
 * optionally, the values that make up the primary key.
 */
@SuppressWarnings("serial")
public class CcpErrorBulkEntityRecordNotFound extends RuntimeException{

	/**
	 * Creates the exception directly from the entity name and the id that was not found.
	 *
	 * @param entityName entity name
	 * @param id identifier that was not found
	 */
	public CcpErrorBulkEntityRecordNotFound(String entityName, String id) {
		super(getErrorMessage(entityName, id));
	}

	/**
	 * Creates the exception computing the id from the JSON and the entity, including in the message the
	 * values of the primary key fields that were used to compose the id.
	 *
	 * @param entity entity where the record was not found
	 * @param json JSON with the values used to compute the id
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
		Supplier<CcpJsonRepresentation> jsonSupplier = json.getJsonSupplier();
		CcpJsonRepresentation primaryKeyValues = entityDetails.getPrimaryKeyValues(jsonSupplier);
		String id = entity.calculateId(json);
		
		String errorMessage = String.format("Does not exist an id '%s' registered in the entity '%s'. Values to compose this id are: %s ", 
				id,
				entityDetails.entityName,
				primaryKeyValues);

		return errorMessage;
	}
	
}
