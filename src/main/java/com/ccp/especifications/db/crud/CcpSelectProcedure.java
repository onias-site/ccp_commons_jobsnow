package com.ccp.especifications.db.crud;

import java.util.Collection;
import java.util.List;

import com.ccp.business.CcpBusiness;
import com.ccp.constants.CcpOtherConstants;
import com.ccp.decorators.CcpFieldName;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonFieldName;
import com.ccp.especifications.db.utils.entity.CcpEntity;

public class CcpSelectProcedure {

	enum JsonFieldNames implements CcpJsonFieldName {
		statements, entity, found
	}

	private final Collection<CcpJsonRepresentation> parametersToSearch;
	private final CcpJsonRepresentation statements;

	CcpSelectProcedure(Collection<CcpJsonRepresentation> parametersToSearch, CcpJsonRepresentation statements) {
		this.parametersToSearch = parametersToSearch;
		this.statements = statements;
	}

	public CcpSelectLoadDataFromEntity loadThisIdFromEntity(CcpEntity entity) {
		CcpJsonRepresentation put2 = CcpOtherConstants.EMPTY_JSON.put(JsonFieldNames.entity, entity);
		CcpJsonRepresentation addToList = this.statements.addToList(JsonFieldNames.statements, put2);
		CcpSelectLoadDataFromEntity ccpSelectLoadDataFromEntity = new CcpSelectLoadDataFromEntity(this.parametersToSearch, addToList);
		return ccpSelectLoadDataFromEntity;
	}

	public CcpSelectFoundInEntity ifThisIdIsPresentInEntity(CcpEntity entity) {
		CcpJsonRepresentation put3 = CcpOtherConstants.EMPTY_JSON.put(JsonFieldNames.found, true);
		CcpJsonRepresentation put = put3.put(JsonFieldNames.entity, entity);
		CcpJsonRepresentation addToList = this.statements.addToList(JsonFieldNames.statements, put);
		CcpSelectFoundInEntity ccpSelectFoundInEntity = new CcpSelectFoundInEntity(this.parametersToSearch, addToList);
		return ccpSelectFoundInEntity;
	}

	public CcpSelectFoundInEntity ifThisIdIsNotPresentInEntity(CcpEntity entity) {
		CcpJsonRepresentation put4 = CcpOtherConstants.EMPTY_JSON.put(JsonFieldNames.found, false);
		CcpJsonRepresentation put = put4.put(JsonFieldNames.entity, entity);
		CcpJsonRepresentation addToList = this.statements.addToList(JsonFieldNames.statements, put);
		CcpSelectFoundInEntity ccpSelectFoundInEntity2 = new CcpSelectFoundInEntity(this.parametersToSearch, addToList);
		return ccpSelectFoundInEntity2;
	}

	public CcpSelectNextStep executeAction(CcpBusiness action) {
		CcpSelectNextStep addStatement = this.addStatement("action", action);
		return addStatement;
	}

	private CcpSelectNextStep addStatement(String key, Object obj) {
		List<CcpJsonRepresentation> list = this.statements.getAsJsonList(JsonFieldNames.statements);
		CcpFieldName ccpFieldName = new CcpFieldName(key);
		CcpJsonRepresentation put5 = CcpOtherConstants.EMPTY_JSON.put(ccpFieldName, obj);
		list.add(put5);
		CcpJsonRepresentation newStatements = this.statements.put(JsonFieldNames.statements, list);
		CcpSelectNextStep ccpSelectNextStep = new CcpSelectNextStep(this.parametersToSearch, newStatements);
		return ccpSelectNextStep;
	}
}
