package com.ccp.especifications.db.crud;

import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.process.CcpProcessStatus;

class FunctionPutStatus implements Function<CcpJsonRepresentation, CcpJsonRepresentation> {

	public static final FunctionPutStatus INSTANCE = new FunctionPutStatus();
	
	private FunctionPutStatus() {}
	
	public CcpJsonRepresentation apply(CcpJsonRepresentation j) {
		CcpProcessStatus stats = j.getAsObject("status");
		CcpJsonRepresentation put3 = j.put("statusName", stats.name()).put("statusNumber", stats.asNumber());
		CcpJsonRepresentation removeField = put3.removeField("status");
		return removeField;
		
	}

}
