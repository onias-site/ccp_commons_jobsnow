package com.ccp.especifications.mensageria.receiver;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.utils.CcpEntity;
import com.ccp.especifications.db.utils.CcpEntityCrudOperationType;

class EntityCrud implements CcpTopic{
	private final String operationFieldName;

	private final CcpEntity entity;
	
	public EntityCrud(CcpEntity entity, String operationFieldName) {
		this.operationFieldName = operationFieldName;
		this.entity = entity;

	}

	public CcpJsonRepresentation apply(CcpJsonRepresentation json) {
		String operation = json.getAsString(this.operationFieldName);
		CcpEntityCrudOperationType valueOf = CcpEntityCrudOperationType.valueOf(operation);
		CcpJsonRepresentation apply = valueOf.execute(this.entity, json);
		return apply;
	}
	
}
