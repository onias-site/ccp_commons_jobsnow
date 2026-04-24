package com.ccp.especifications.db.crud;

import java.util.Arrays;
import java.util.Collection;

import com.ccp.constantes.CcpOtherConstants;
import com.ccp.decorators.CcpJsonRepresentation;

public class CcpGetEntityId {

	private final Collection<CcpJsonRepresentation> parametersToSearch;

	public CcpGetEntityId(CcpJsonRepresentation... parametersToSearch) {
		this.parametersToSearch = Arrays.asList(parametersToSearch);
	}
	public CcpSelectProcedure toBeginProcedureAnd() {
		return new CcpSelectProcedure(this.parametersToSearch, CcpOtherConstants.EMPTY_JSON);
	}


}
