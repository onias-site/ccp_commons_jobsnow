package com.ccp.especifications.db.crud;

import java.util.Collection;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonRepresentation.CcpJsonFieldName;

public class CcpSelectNextStep {
	private final Collection<CcpJsonRepresentation> parametersToSearch;
	private final CcpJsonRepresentation statements;

	CcpSelectNextStep(Collection<CcpJsonRepresentation> parametersToSearch, CcpJsonRepresentation statements) {
		this.parametersToSearch = parametersToSearch;
		this.statements = statements;
	}
	
	public CcpSelectFinally andFinallyReturningTheseFields(Collection<CcpJsonFieldName> fields) {
		CcpJsonFieldName[] array = fields.toArray(new CcpJsonFieldName[fields.size()]);
		CcpSelectFinally ccpSelectFinally = new CcpSelectFinally(this.parametersToSearch, this.statements, array);
		return ccpSelectFinally;
	}
	
	
	public CcpSelectFinally andFinallyReturningTheseFields(CcpJsonFieldName... fields) {
		CcpSelectFinally ccpSelectFinally = new CcpSelectFinally(this.parametersToSearch, this.statements, fields);
		return ccpSelectFinally;
	}
	
	public CcpSelectProcedure and() {
		CcpSelectProcedure ccpSelectProcedure = new CcpSelectProcedure(this.parametersToSearch, this.statements);
		return ccpSelectProcedure;
	}
	
}
