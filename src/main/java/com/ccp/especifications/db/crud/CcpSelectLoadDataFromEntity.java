package com.ccp.especifications.db.crud;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonRepresentation;

public class CcpSelectLoadDataFromEntity {
	private final CcpJsonRepresentation id;
	private final CcpJsonRepresentation statements;

	CcpSelectLoadDataFromEntity(CcpJsonRepresentation id, CcpJsonRepresentation statements) {
		this.statements = statements;
		this.id = id;

	}

	public CcpSelectProcedure and() {
		return new CcpSelectProcedure(this.id, this.statements);
	}
	
	public CcpSelectFinally andFinally(String... fields) {
		return new CcpSelectFinally(this.id, this.statements, fields);
	}
	


}
