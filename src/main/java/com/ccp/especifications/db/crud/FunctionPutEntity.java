package com.ccp.especifications.db.crud;

import com.ccp.business.CcpBusiness;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonFieldName;
import com.ccp.especifications.db.utils.entity.CcpEntity;
import com.ccp.especifications.db.utils.entity.decorators.engine.CcpEntityMetaData;

/**
 * Business that replaces the {@code entity} field of a statement, which carries a {@code CcpEntity}, with the entity
 * name, to describe the flow of a search in error messages.
 */
class FunctionPutEntity implements CcpBusiness{
	/** Fields of a statement. */
	enum JsonFieldNames implements CcpJsonFieldName{
		/** The entity of the statement. */
		entity
	}

	/** The single instance. */
	public static final FunctionPutEntity INSTANCE = new FunctionPutEntity();

	/** Singleton. */
	private FunctionPutEntity() {}

	/**
	 * Extracts the {@code CcpEntity} object from the {@code entity} field, gets its name through {@code getEntityMetaData()}
	 * and replaces the field with the textual name.
	 */
	public CcpJsonRepresentation apply(CcpJsonRepresentation statement) {

		CcpEntity entity = statement.getAsObject(JsonFieldNames.entity);
		CcpEntityMetaData entityDetails = entity.getEntityMetaData();
		String entityName = entityDetails.entityName;
		CcpJsonRepresentation statementWithEntityName = statement.put(JsonFieldNames.entity, entityName);
		return statementWithEntityName;
	}
}
