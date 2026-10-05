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

/**
 * Statement step of the search fluent chain: declares which entities are queried, which presence conditions are
 * checked and which actions run. Statements are evaluated in the order they are declared.
 */
public class CcpSelectProcedure {

	/** Fields of a statement. */
	enum JsonFieldNames implements CcpJsonFieldName {
		/** The entity of a statement. */
		entity}

	/** The search parameters. */
	private final Collection<CcpJsonRepresentation> parametersToSearch;
	/** The statements declared so far. */
	private final CcpJsonRepresentation statements;

	/**
	 * Keeps the state of the search being declared.
	 * @param parametersToSearch the search parameters
	 * @param statements the statements declared so far
	 */
	CcpSelectProcedure(Collection<CcpJsonRepresentation> parametersToSearch, CcpJsonRepresentation statements) {
		this.parametersToSearch = parametersToSearch;
		this.statements = statements;
	}

	/**
	 * Declares that the record of the entity, when it exists, must be added to the JSON under
	 * {@code _entities.<entity>}.
	 * @param entity the entity to load
	 * @return the next step of the chain
	 */
	public CcpSelectLoadDataFromEntity loadThisIdFromEntity(CcpEntity entity) {
		CcpJsonRepresentation entityStatement = CcpOtherConstants.EMPTY_JSON.put(JsonFieldNames.entity, entity);
		CcpJsonRepresentation updatedStatements = this.statements.addToList(CcpJsonCommonsFields.statements, entityStatement);
		CcpSelectLoadDataFromEntity loadDataStep = new CcpSelectLoadDataFromEntity(this.parametersToSearch, updatedStatements);
		return loadDataStep;
	}

	/**
	 * Declares a condition that holds when the record exists in the entity.
	 * @param entity the entity checked
	 * @return the step that declares what happens when the condition holds
	 */
	public CcpSelectFoundInEntity ifThisIdIsPresentInEntity(CcpEntity entity) {
		CcpJsonRepresentation foundStatement = CcpOtherConstants.EMPTY_JSON.put(CcpJsonCommonsFields.found, true);
		CcpJsonRepresentation entityFoundStatement = foundStatement.put(JsonFieldNames.entity, entity);
		CcpJsonRepresentation updatedStatements = this.statements.addToList(CcpJsonCommonsFields.statements, entityFoundStatement);
		CcpSelectFoundInEntity foundStep = new CcpSelectFoundInEntity(this.parametersToSearch, updatedStatements);
		return foundStep;
	}

	/**
	 * Declares a condition that holds when the record does not exist in the entity.
	 * @param entity the entity checked
	 * @return the step that declares what happens when the condition holds
	 */
	public CcpSelectFoundInEntity ifThisIdIsNotPresentInEntity(CcpEntity entity) {
		CcpJsonRepresentation notFoundStatement = CcpOtherConstants.EMPTY_JSON.put(CcpJsonCommonsFields.found, false);
		CcpJsonRepresentation entityNotFoundStatement = notFoundStatement.put(JsonFieldNames.entity, entity);
		CcpJsonRepresentation updatedStatements = this.statements.addToList(CcpJsonCommonsFields.statements, entityNotFoundStatement);
		CcpSelectFoundInEntity notFoundStep = new CcpSelectFoundInEntity(this.parametersToSearch, updatedStatements);
		return notFoundStep;
	}

	/**
	 * Declares an unconditional action, run over the JSON at this point of the flow.
	 * @param action the action to run
	 * @return the next step of the chain
	 */
	public CcpSelectNextStep executeAction(CcpBusiness action) {
		CcpSelectNextStep nextStep = this.addStatement("action", action);
		return nextStep;
	}

	/**
	 * Appends a new statement holding only the key/value.
	 * @param key the statement key
	 * @param obj the statement value
	 * @return the next step of the chain
	 */
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
