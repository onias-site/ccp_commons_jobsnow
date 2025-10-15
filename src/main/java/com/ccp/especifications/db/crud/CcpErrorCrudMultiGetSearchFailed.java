package com.ccp.especifications.db.crud;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonRepresentation.CcpJsonFieldName;
@SuppressWarnings("serial")
public class CcpErrorCrudMultiGetSearchFailed extends RuntimeException {
	enum JsonFieldNames implements CcpJsonFieldName{
		type, reason
	}

	public CcpErrorCrudMultiGetSearchFailed(CcpJsonRepresentation error) {
		super(error.getAsString(JsonFieldNames.type) + ". Reason: " + error.getAsString(JsonFieldNames.reason));
	}
	
}
