package com.ccp.especifications.db.crud;

import java.util.Collection;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonRepresentation;

public class CcpSelectNextStep {
	private final CcpJsonRepresentation id;
	private final CcpJsonRepresentation statements;

	CcpSelectNextStep(CcpJsonRepresentation id, CcpJsonRepresentation statements) {
		this.id = id;
		this.statements = statements;
	}
	
	public CcpSelectFinally andFinallyReturningTheseFields(Collection<String> fields) {
		String[] array = fields.toArray(new String[fields.size()]);
		CcpSelectFinally ccpSelectFinally = new CcpSelectFinally(this.id, this.statements, array);
		return ccpSelectFinally;
	}
	
	
	public CcpSelectFinally andFinallyReturningTheseFields(String... fields) {
		CcpSelectFinally ccpSelectFinally = new CcpSelectFinally(this.id, this.statements, fields);
		return ccpSelectFinally;
	}
	
	public CcpSelectProcedure and() {
		CcpSelectProcedure ccpSelectProcedure = new CcpSelectProcedure(this.id, this.statements);
		return ccpSelectProcedure;
	}
	
}
