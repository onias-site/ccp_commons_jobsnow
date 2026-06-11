package com.ccp.especifications.db.crud;

import java.util.Collection;
import java.util.List;

import com.ccp.business.CcpBusiness;
import com.ccp.decorators.CcpFieldName;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonRepresentation.CcpJsonFieldName;
import com.ccp.process.CcpProcessStatus;

//TODO TRANSFORMAR ESTA CLASSE EM JSONFIELDNAME E BUSINESS
public class CcpSelectFoundInEntity {
	public static enum JsonFieldNames implements CcpJsonFieldName{
		statements
	}

	private final Collection<CcpJsonRepresentation>	 parametersToSearch;
	private final CcpJsonRepresentation statements;

	CcpSelectFoundInEntity(Collection<CcpJsonRepresentation> parametersToSearch, CcpJsonRepresentation statements) {
		this.parametersToSearch = parametersToSearch;
		this.statements = statements;
	}

	public CcpSelectNextStep executeAction(CcpBusiness action) {
		return this.addStatement("action", action);
	}

	
	
	public CcpSelectNextStep returnStatus(CcpProcessStatus status) {
		return this.addStatement("status", status);
	}

	private CcpSelectNextStep addStatement(String key, Object obj) {
		List<CcpJsonRepresentation> list = this.statements.getAsJsonList(JsonFieldNames.statements);
		CcpJsonRepresentation lastStatement = list.get(list.size() - 1);
		CcpJsonRepresentation put = lastStatement.put(new CcpFieldName(key), obj);
		List<CcpJsonRepresentation> subList = list.subList(0, list.size() - 1);
		subList.add(put);
		CcpJsonRepresentation newStatements = this.statements.put(JsonFieldNames.statements, subList);
		return new CcpSelectNextStep(this.parametersToSearch, newStatements);
	}
	
}
