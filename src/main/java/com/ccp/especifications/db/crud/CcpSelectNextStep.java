package com.ccp.especifications.db.crud;

import java.util.Collection;

import com.ccp.decorators.CcpJsonRepresentation;

public class CcpSelectNextStep {
	private final Collection<CcpJsonRepresentation> parametersToSearch;
	private final CcpJsonRepresentation statements;

	CcpSelectNextStep(Collection<CcpJsonRepresentation> parametersToSearch, CcpJsonRepresentation statements) {
		this.parametersToSearch = parametersToSearch;
		this.statements = statements;
	}
	
	public CcpSelectFinally andFinallyReturningTheseFields(Collection<String> fields) {
		String[] array = fields.toArray(new String[fields.size()]);
		CcpSelectFinally ccpSelectFinally = new CcpSelectFinally(this.parametersToSearch, this.statements, array);
		return ccpSelectFinally;
	}
	
	
	public CcpSelectFinally andFinallyReturningTheseFields(String... fields) {
		CcpSelectFinally ccpSelectFinally = new CcpSelectFinally(this.parametersToSearch, this.statements, fields);
		return ccpSelectFinally;
	}
	
	public CcpSelectProcedure and() {
		CcpSelectProcedure ccpSelectProcedure = new CcpSelectProcedure(this.parametersToSearch, this.statements);
		return ccpSelectProcedure;
	}
	
}
