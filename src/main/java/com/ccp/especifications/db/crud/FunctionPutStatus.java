package com.ccp.especifications.db.crud;

import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonRepresentation.CcpJsonFieldName;
import com.ccp.process.CcpProcessStatus;
enum FunctionPutStatusConstants  implements CcpJsonFieldName{
	status, statusName, statusNumber
	
}
class FunctionPutStatus implements Function<CcpJsonRepresentation, CcpJsonRepresentation> {

	public static final FunctionPutStatus INSTANCE = new FunctionPutStatus();
	
	private FunctionPutStatus() {}
	
	public CcpJsonRepresentation apply(CcpJsonRepresentation j) {
		CcpProcessStatus stats = j.getAsObject(FunctionPutStatusConstants.status);
		CcpJsonRepresentation put3 = j.put(FunctionPutStatusConstants.statusName, stats.name())
				.put(FunctionPutStatusConstants.statusNumber, stats.asNumber());
		CcpJsonRepresentation removeField = put3.removeField(FunctionPutStatusConstants.status);
		return removeField;
		
	}

}
