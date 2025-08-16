package com.ccp.especifications.db.crud;

import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonRepresentation.CcpJsonFieldName;
import com.ccp.especifications.db.utils.CcpEntity;
enum FunctionPutEntityConstants  implements CcpJsonFieldName{
	entity
	
}
class FunctionPutEntity implements Function<CcpJsonRepresentation, CcpJsonRepresentation> {

	public static final FunctionPutEntity INSTANCE = new FunctionPutEntity();
	
	private FunctionPutEntity() {}
	
	public CcpJsonRepresentation apply(CcpJsonRepresentation j) {
		
		CcpEntity ent = j.getAsObject(FunctionPutEntityConstants.entity);
		String entityName = ent.getEntityName();
		CcpJsonRepresentation put2 = j.put(FunctionPutEntityConstants.entity, entityName);
		return put2;
		
	}

}
