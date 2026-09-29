package com.ccp.especifications.db.crud;

import java.util.Collection;
import java.util.List;

import com.ccp.business.CcpBusiness;
import com.ccp.decorators.CcpFieldName;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonFieldName;
import com.ccp.process.CcpProcessStatus;

import com.ccp.json.fields.validation.CcpJsonCommonsFields;

public class CcpSelectFoundInEntity {

	public static enum JsonFieldNames implements CcpJsonFieldName {
		statements
	}

	private final Collection<CcpJsonRepresentation> parametersToSearch;
	private final CcpJsonRepresentation statements;

	CcpSelectFoundInEntity(Collection<CcpJsonRepresentation> parametersToSearch, CcpJsonRepresentation statements) {
		this.parametersToSearch = parametersToSearch;
		this.statements = statements;
	}

	public CcpSelectNextStep executeAction(CcpBusiness action) {
		CcpSelectNextStep nextStep = this.addStatement("action", action);
		return nextStep;
	}

	public CcpSelectNextStep returnStatus(CcpProcessStatus status) {
		CcpSelectNextStep nextStep = this.addStatement("status", status);
		return nextStep;
	}

	private CcpSelectNextStep addStatement(String key, Object obj) {
		List<CcpJsonRepresentation> statementList = this.statements.getAsJsonList(CcpJsonCommonsFields.statements);
		int statementCount = statementList.size();
		int lastIndex = statementCount - 1;
		CcpJsonRepresentation lastStatement = statementList.get(lastIndex);
		CcpFieldName statementKey = new CcpFieldName(key);
		CcpJsonRepresentation updatedLastStatement = lastStatement.put(statementKey, obj);
		int currentStatementCount = statementList.size();
		int lastStatementIndex = currentStatementCount - 1;
		List<CcpJsonRepresentation> statementsWithoutLast = statementList.subList(0, lastStatementIndex);
		statementsWithoutLast.add(updatedLastStatement);
		CcpJsonRepresentation newStatements = this.statements.put(CcpJsonCommonsFields.statements, statementsWithoutLast);
		CcpSelectNextStep nextStep = new CcpSelectNextStep(this.parametersToSearch, newStatements);
		return nextStep;
	}
}
