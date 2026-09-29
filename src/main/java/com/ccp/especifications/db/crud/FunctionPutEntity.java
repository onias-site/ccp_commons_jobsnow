package com.ccp.especifications.db.crud;

import com.ccp.business.CcpBusiness;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonFieldName;
import com.ccp.especifications.db.utils.entity.CcpEntity;
import com.ccp.especifications.db.utils.entity.decorators.engine.CcpEntityMetaData;

class FunctionPutEntity implements CcpBusiness{
	enum JsonFieldNames implements CcpJsonFieldName{
		entity
	}

	public static final FunctionPutEntity INSTANCE = new FunctionPutEntity();

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
