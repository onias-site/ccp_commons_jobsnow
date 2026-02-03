package com.ccp.especifications.db.crud;

import com.ccp.business.CcpBusiness;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonRepresentation.CcpJsonFieldName;
import com.ccp.process.CcpProcessStatus;
class FunctionPutStatus implements CcpBusiness {
	enum JsonFieldNames implements CcpJsonFieldName{
		status, statusName, statusNumber
	}

	public static final FunctionPutStatus INSTANCE = new FunctionPutStatus();
	
	private FunctionPutStatus() {}
	
	public CcpJsonRepresentation apply(CcpJsonRepresentation j) {
		CcpProcessStatus stats = j.getAsObject(JsonFieldNames.status);
		CcpJsonRepresentation put3 = j.put(JsonFieldNames.statusName, stats.name())
				.put(JsonFieldNames.statusNumber, stats.asNumber());
		CcpJsonRepresentation removeField = put3.removeField(JsonFieldNames.status);
		return removeField;
		
	}

}
