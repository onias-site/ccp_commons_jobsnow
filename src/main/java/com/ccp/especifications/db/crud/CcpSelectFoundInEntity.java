package com.ccp.especifications.db.crud;

import java.util.List;
import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonRepresentation.CcpJsonFieldName;
import com.ccp.process.CcpProcessStatus;

enum CcpSelectFoundInEntityConstants  implements CcpJsonFieldName{
	statements
	
}

public class CcpSelectFoundInEntity {
	private final CcpJsonRepresentation id;
	private final CcpJsonRepresentation statements;

	CcpSelectFoundInEntity(CcpJsonRepresentation id, CcpJsonRepresentation statements) {
		this.statements = statements;
		this.id = id;
	}

	public CcpSelectNextStep executeAction(Function<CcpJsonRepresentation, CcpJsonRepresentation> action) {
		return this.addStatement("action", action);
	}

	
	
	public CcpSelectNextStep returnStatus(CcpProcessStatus status) {
		return this.addStatement("status", status);
	}

	private CcpSelectNextStep addStatement(String key, Object obj) {
		List<CcpJsonRepresentation> list = this.statements.getAsJsonList(CcpSelectFoundInEntityConstants.statements);
		CcpJsonRepresentation lastStatement = list.get(list.size() - 1);
		CcpJsonRepresentation put = lastStatement.getDynamicVersion().put(key, obj);
		List<CcpJsonRepresentation> subList = list.subList(0, list.size() - 1);
		subList.add(put);
		CcpJsonRepresentation newStatements = this.statements.put(CcpSelectFoundInEntityConstants.statements, subList);
		return new CcpSelectNextStep(this.id, newStatements);
	}
	
}
