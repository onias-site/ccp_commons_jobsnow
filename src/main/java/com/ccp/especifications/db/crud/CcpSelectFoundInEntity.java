package com.ccp.especifications.db.crud;

import java.util.Collection;
import java.util.List;

import com.ccp.business.CcpBusiness;
import com.ccp.decorators.CcpFieldName;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonFieldName;
import com.ccp.process.CcpProcessStatus;

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
		CcpSelectNextStep addStatement = this.addStatement("action", action);
		return addStatement;
	}

	public CcpSelectNextStep returnStatus(CcpProcessStatus status) {
		CcpSelectNextStep addStatement2 = this.addStatement("status", status);
		return addStatement2;
	}

	private CcpSelectNextStep addStatement(String key, Object obj) {
		List<CcpJsonRepresentation> list = this.statements.getAsJsonList(JsonFieldNames.statements);
		int listSize = list.size();
		int listSizeMenos = listSize - 1;
		CcpJsonRepresentation lastStatement = list.get(listSizeMenos);
		CcpFieldName ccpFieldName = new CcpFieldName(key);
		CcpJsonRepresentation put = lastStatement.put(ccpFieldName, obj);
		int listSize2 = list.size();
		int listSize2Menos = listSize2 - 1;
		List<CcpJsonRepresentation> subList = list.subList(0, listSize2Menos);
		subList.add(put);
		CcpJsonRepresentation newStatements = this.statements.put(JsonFieldNames.statements, subList);
		CcpSelectNextStep ccpSelectNextStep = new CcpSelectNextStep(this.parametersToSearch, newStatements);
		return ccpSelectNextStep;
	}
}
