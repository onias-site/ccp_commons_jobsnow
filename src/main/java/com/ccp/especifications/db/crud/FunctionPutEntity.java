package com.ccp.especifications.db.crud;

import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.utils.CcpEntity;

class FunctionPutEntity implements Function<CcpJsonRepresentation, CcpJsonRepresentation> {

	public static final FunctionPutEntity INSTANCE = new FunctionPutEntity();
	
	private FunctionPutEntity() {}
	
	public CcpJsonRepresentation apply(CcpJsonRepresentation j) {
		
		CcpEntity ent = j.getAsObject("entity");
		String entityName = ent.getEntityName();
		CcpJsonRepresentation put2 = j.put("entity", entityName);
		return put2;
		
	}

}
