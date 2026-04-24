package com.ccp.especifications.db.crud;

import java.util.Collection;
import java.util.List;

import com.ccp.business.CcpBusiness;
import com.ccp.constantes.CcpOtherConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonRepresentation.CcpJsonFieldName;
import com.ccp.especifications.db.utils.entity.CcpEntity;
public class CcpSelectProcedure {
	enum JsonFieldNames implements CcpJsonFieldName{
		statements, entity, found
	}
	private final Collection<CcpJsonRepresentation> parametersToSearch;
	private final CcpJsonRepresentation statements;
	
	CcpSelectProcedure(Collection<CcpJsonRepresentation> parametersToSearch, CcpJsonRepresentation statements) {
		this.parametersToSearch = parametersToSearch;
		this.statements = statements;
	}

	public CcpSelectLoadDataFromEntity loadThisIdFromEntity(CcpEntity entity) {
		CcpJsonRepresentation addToList = this.statements.addToList(JsonFieldNames.statements, CcpOtherConstants.EMPTY_JSON.put(JsonFieldNames.entity, entity));
		return new CcpSelectLoadDataFromEntity(this.parametersToSearch, addToList);
	}

	public CcpSelectFoundInEntity ifThisIdIsPresentInEntity(CcpEntity entity) {
		CcpJsonRepresentation put = CcpOtherConstants.EMPTY_JSON.put(JsonFieldNames.found, true).put(JsonFieldNames.entity, entity);
		CcpJsonRepresentation addToList = this.statements.addToList(JsonFieldNames.statements, put);
		return new CcpSelectFoundInEntity(this.parametersToSearch, addToList);
	}
	
	public CcpSelectFoundInEntity ifThisIdIsNotPresentInEntity(CcpEntity entity) {
		CcpJsonRepresentation put = CcpOtherConstants.EMPTY_JSON.put(JsonFieldNames.found, false).put(JsonFieldNames.entity, entity);
		CcpJsonRepresentation addToList = this.statements.addToList(JsonFieldNames.statements, put);
		return new CcpSelectFoundInEntity(this.parametersToSearch, addToList);
	}
	
	public CcpSelectNextStep executeAction(CcpBusiness action) {
		return this.addStatement("action", action);
	}
	
	private CcpSelectNextStep addStatement(String key, Object obj) {
		List<CcpJsonRepresentation> list = this.statements.getAsJsonList(JsonFieldNames.statements);
		list.add(CcpOtherConstants.EMPTY_JSON.getDynamicVersion().put(key, obj));
		CcpJsonRepresentation newStatements = this.statements.put(JsonFieldNames.statements, list);
		return new CcpSelectNextStep(this.parametersToSearch, newStatements);
	}

 }
