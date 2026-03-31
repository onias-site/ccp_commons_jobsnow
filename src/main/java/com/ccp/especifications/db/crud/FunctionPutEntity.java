package com.ccp.especifications.db.crud;

import com.ccp.business.CcpBusiness;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonRepresentation.CcpJsonFieldName;
import com.ccp.especifications.db.utils.entity.CcpEntity;
import com.ccp.especifications.db.utils.entity.decorators.engine.CcpEntityDetails;

class FunctionPutEntity implements CcpBusiness{
	enum JsonFieldNames implements CcpJsonFieldName{
		entity
	}

	public static final FunctionPutEntity INSTANCE = new FunctionPutEntity();
	
	private FunctionPutEntity() {}
	
	public CcpJsonRepresentation apply(CcpJsonRepresentation j) {
		
		CcpEntity ent = j.getAsObject(JsonFieldNames.entity);
		CcpEntityDetails entityDetails = ent.getEntityDetails();
		String entityName = entityDetails.entityName;
		CcpJsonRepresentation put2 = j.put(JsonFieldNames.entity, entityName);
		return put2;
		
	}

}
