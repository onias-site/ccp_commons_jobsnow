package com.ccp.especifications.db.crud;

import java.util.Collection;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonRepresentation.CcpJsonFieldName;

public class CcpSelectLoadDataFromEntity {
	private final Collection<CcpJsonRepresentation> parametersToSearch;
	private final CcpJsonRepresentation statements;

	CcpSelectLoadDataFromEntity(Collection<CcpJsonRepresentation> parametersToSearch, CcpJsonRepresentation statements) {
		this.parametersToSearch = parametersToSearch;
		this.statements = statements;

	}

	public CcpSelectProcedure and() {
		return new CcpSelectProcedure(this.parametersToSearch, this.statements);
	}
	
	public CcpSelectFinally andFinally(CcpJsonFieldName... fields) {
		return new CcpSelectFinally(this.parametersToSearch, this.statements, fields);
	}
	


}
