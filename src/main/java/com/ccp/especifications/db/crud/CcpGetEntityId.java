package com.ccp.especifications.db.crud;

import com.ccp.constantes.CcpOtherConstants;
import com.ccp.decorators.CcpJsonRepresentation;

public class CcpGetEntityId {

	private final CcpJsonRepresentation id;

	public CcpGetEntityId(CcpJsonRepresentation id) {
		this.id = id;
	}
	public CcpSelectProcedure toBeginProcedureAnd() {
		return new CcpSelectProcedure(this.id, CcpOtherConstants.EMPTY_JSON);
	}


}
