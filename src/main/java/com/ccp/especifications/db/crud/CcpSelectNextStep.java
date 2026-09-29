package com.ccp.especifications.db.crud;

import java.util.Collection;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonFieldName;

public class CcpSelectNextStep {

	private final Collection<CcpJsonRepresentation> parametersToSearch;
	private final CcpJsonRepresentation statements;

	CcpSelectNextStep(Collection<CcpJsonRepresentation> parametersToSearch, CcpJsonRepresentation statements) {
		this.parametersToSearch = parametersToSearch;
		this.statements = statements;
	}

	public CcpSelectFinally andFinallyReturningTheseFields(Collection<CcpJsonFieldName> fields) {
		int fieldsCount = fields.size();
		CcpJsonFieldName[] fieldsArray = fields.toArray(new CcpJsonFieldName[fieldsCount]);
		CcpSelectFinally finalStep = new CcpSelectFinally(this.parametersToSearch, this.statements, fieldsArray);
		return finalStep;
	}

	public CcpSelectFinally andFinallyReturningTheseFields(CcpJsonFieldName... fields) {
		CcpSelectFinally finalStep = new CcpSelectFinally(this.parametersToSearch, this.statements, fields);
		return finalStep;
	}

	public CcpSelectProcedure and() {
		CcpSelectProcedure procedure = new CcpSelectProcedure(this.parametersToSearch, this.statements);
		return procedure;
	}
}
