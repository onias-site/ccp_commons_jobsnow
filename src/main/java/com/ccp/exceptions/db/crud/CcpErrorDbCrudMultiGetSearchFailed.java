package com.ccp.exceptions.db.crud;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonRepresentation.CcpJsonFieldName;
enum CcpErrorDbCrudMultiGetSearchFailedConstants  implements CcpJsonFieldName{
	type, reason
}
@SuppressWarnings("serial")
public class CcpErrorDbCrudMultiGetSearchFailed extends RuntimeException {

	public CcpErrorDbCrudMultiGetSearchFailed(CcpJsonRepresentation error) {
		super(error.getAsString(CcpErrorDbCrudMultiGetSearchFailedConstants.type) + ". Reason: " + error.getAsString(CcpErrorDbCrudMultiGetSearchFailedConstants.reason));
	}
	
}
