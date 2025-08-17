package com.ccp.especifications.db.crud;

import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonRepresentation.CcpJsonFieldName;
import com.ccp.especifications.db.utils.CcpEntity;
class FunctionPutEntity implements Function<CcpJsonRepresentation, CcpJsonRepresentation> {
	enum JsonFieldNames implements CcpJsonFieldName{
		entity
	}

	public static final FunctionPutEntity INSTANCE = new FunctionPutEntity();
	
	private FunctionPutEntity() {}
	
	public CcpJsonRepresentation apply(CcpJsonRepresentation j) {
		
		CcpEntity ent = j.getAsObject(JsonFieldNames.entity);
		String entityName = ent.getEntityName();
		CcpJsonRepresentation put2 = j.put(JsonFieldNames.entity, entityName);
		return put2;
		
	}

}
