package com.ccp.especifications.db.crud;

import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.process.CcpProcessStatus;

class PutStatus implements Function<CcpJsonRepresentation, CcpJsonRepresentation> {

	public static final PutStatus INSTANCE = new PutStatus();
	
	private PutStatus() {}
	
	public CcpJsonRepresentation apply(CcpJsonRepresentation j) {
		CcpProcessStatus stats = j.getAsObject("status");
		CcpJsonRepresentation put3 = j.put("statusName", stats.name()).put("statusNumber", stats.asNumber());
		CcpJsonRepresentation removeField = put3.removeField("status");
		return removeField;
		
	}

}
