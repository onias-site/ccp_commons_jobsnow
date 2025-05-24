package com.ccp.especifications.db.crud;

import java.util.List;
import java.util.function.Function;

import com.ccp.constantes.CcpOtherConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.utils.CcpEntity;

public class CcpSelectProcedure {
	private final CcpJsonRepresentation id;
	private final CcpJsonRepresentation statements;
	CcpSelectProcedure(CcpJsonRepresentation id, CcpJsonRepresentation statements) {
		this.statements = statements;
		this.id = id;
	}

	public CcpSelectLoadDataFromEntity loadThisIdFromEntity(CcpEntity entity) {
		CcpJsonRepresentation addToList = this.statements.addToList("statements", CcpOtherConstants.EMPTY_JSON.put("entity", entity));
		return new CcpSelectLoadDataFromEntity(this.id, addToList);
	}

	public CcpSelectFoundInEntity ifThisIdIsPresentInEntity(CcpEntity entity) {
		CcpJsonRepresentation put = CcpOtherConstants.EMPTY_JSON.put("found", true).put("entity", entity);
		CcpJsonRepresentation addToList = this.statements.addToList("statements", put);
		return new CcpSelectFoundInEntity(this.id, addToList);
	}
	
	public CcpSelectFoundInEntity ifThisIdIsNotPresentInEntity(CcpEntity entity) {
		CcpJsonRepresentation put = CcpOtherConstants.EMPTY_JSON.put("found", false).put("entity", entity);
		CcpJsonRepresentation addToList = this.statements.addToList("statements", put);
		return new CcpSelectFoundInEntity(this.id, addToList);
	}
	
	public CcpSelectNextStep executeAction(Function<CcpJsonRepresentation, CcpJsonRepresentation> action) {
		return this.addStatement("action", action);
	}
	
	private CcpSelectNextStep addStatement(String key, Object obj) {
		List<CcpJsonRepresentation> list = this.statements.getAsJsonList("statements");
		list.add(CcpOtherConstants.EMPTY_JSON.put(key, obj));
		CcpJsonRepresentation newStatements = this.statements.put("statements", list);
		return new CcpSelectNextStep(this.id, newStatements);
	}

 }
