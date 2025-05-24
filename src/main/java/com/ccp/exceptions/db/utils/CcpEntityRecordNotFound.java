package com.ccp.exceptions.db.utils;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.utils.CcpEntity;

@SuppressWarnings("serial")
public class CcpEntityRecordNotFound extends RuntimeException{

	public CcpEntityRecordNotFound(String entityName, String id) {
		super(getErrorMessage(entityName, id));
	}

	public CcpEntityRecordNotFound(CcpEntity entity, CcpJsonRepresentation json) {
		super(getErrorMessage(entity, json));
	}
	

	private static String getErrorMessage(String entityName, String id) {
		String errorMessage = String.format("Does not exist an id '%s' registered in the entity '%s'.", 
				id,	entityName);

		return errorMessage;
	}


	private static String getErrorMessage(CcpEntity entity, CcpJsonRepresentation json) {

		CcpJsonRepresentation primaryKeyValues = entity.getPrimaryKeyValues(json);
		String entityName = entity.getEntityName();
		String id = entity.calculateId(json);
		
		String errorMessage = String.format("Does not exist an id '%s' registered in the entity '%s'. Values to compose this id are: %s ", 
				id,
				entityName,
				primaryKeyValues);

		return errorMessage;
	}
	
}
