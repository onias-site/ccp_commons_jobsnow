package com.ccp.especifications.db.crud;

import java.util.Collection;
import java.util.List;

import com.ccp.business.CcpBusiness;
import com.ccp.constants.CcpOtherConstants;
import com.ccp.decorators.CcpFieldName;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonFieldName;
import com.ccp.especifications.db.utils.entity.CcpEntity;

import com.ccp.json.fields.validation.CcpJsonCommonsFields;

public class CcpSelectProcedure {

	enum JsonFieldNames implements CcpJsonFieldName { entity}

	private final Collection<CcpJsonRepresentation> parametersToSearch;
	private final CcpJsonRepresentation statements;

	CcpSelectProcedure(Collection<CcpJsonRepresentation> parametersToSearch, CcpJsonRepresentation statements) {
		this.parametersToSearch = parametersToSearch;
		this.statements = statements;
	}

	public CcpSelectLoadDataFromEntity loadThisIdFromEntity(CcpEntity entity) {
		CcpJsonRepresentation entityStatement = CcpOtherConstants.EMPTY_JSON.put(JsonFieldNames.entity, entity);
		CcpJsonRepresentation updatedStatements = this.statements.addToList(CcpJsonCommonsFields.statements, entityStatement);
		CcpSelectLoadDataFromEntity loadDataStep = new CcpSelectLoadDataFromEntity(this.parametersToSearch, updatedStatements);
		return loadDataStep;
	}

	public CcpSelectFoundInEntity ifThisIdIsPresentInEntity(CcpEntity entity) {
		CcpJsonRepresentation foundStatement = CcpOtherConstants.EMPTY_JSON.put(CcpJsonCommonsFields.found, true);
		CcpJsonRepresentation entityFoundStatement = foundStatement.put(JsonFieldNames.entity, entity);
		CcpJsonRepresentation updatedStatements = this.statements.addToList(CcpJsonCommonsFields.statements, entityFoundStatement);
		CcpSelectFoundInEntity foundStep = new CcpSelectFoundInEntity(this.parametersToSearch, updatedStatements);
		return foundStep;
	}

	public CcpSelectFoundInEntity ifThisIdIsNotPresentInEntity(CcpEntity entity) {
		CcpJsonRepresentation notFoundStatement = CcpOtherConstants.EMPTY_JSON.put(CcpJsonCommonsFields.found, false);
		CcpJsonRepresentation entityNotFoundStatement = notFoundStatement.put(JsonFieldNames.entity, entity);
		CcpJsonRepresentation updatedStatements = this.statements.addToList(CcpJsonCommonsFields.statements, entityNotFoundStatement);
		CcpSelectFoundInEntity notFoundStep = new CcpSelectFoundInEntity(this.parametersToSearch, updatedStatements);
		return notFoundStep;
	}

	public CcpSelectNextStep executeAction(CcpBusiness action) {
		CcpSelectNextStep nextStep = this.addStatement("action", action);
		return nextStep;
	}

	private CcpSelectNextStep addStatement(String key, Object obj) {
		List<CcpJsonRepresentation> statementList = this.statements.getAsJsonList(CcpJsonCommonsFields.statements);
		CcpFieldName statementKey = new CcpFieldName(key);
		CcpJsonRepresentation newStatement = CcpOtherConstants.EMPTY_JSON.put(statementKey, obj);
		statementList.add(newStatement);
		CcpJsonRepresentation newStatements = this.statements.put(CcpJsonCommonsFields.statements, statementList);
		CcpSelectNextStep nextStep = new CcpSelectNextStep(this.parametersToSearch, newStatements);
		return nextStep;
	}
}
