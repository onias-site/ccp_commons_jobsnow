package com.ccp.especifications.db.crud;

import java.util.Collection;
import java.util.List;

import com.ccp.business.CcpBusiness;
import com.ccp.decorators.CcpFieldName;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonFieldName;
import com.ccp.process.CcpProcessStatus;

import com.ccp.json.fields.validation.CcpJsonCommonsFields;

/**
 * Step of the search fluent chain right after a presence condition ({@code ifThisIdIsPresentInEntity} or
 * {@code ifThisIdIsNotPresentInEntity}): declares what happens when the condition holds.
 */
public class CcpSelectFoundInEntity {

	/** Fields of the statements JSON. */
	public static enum JsonFieldNames implements CcpJsonFieldName {
		/** The list of declared statements. */
		statements
	}

	/** The search parameters. */
	private final Collection<CcpJsonRepresentation> parametersToSearch;
	/** The statements declared so far. */
	private final CcpJsonRepresentation statements;

	/**
	 * Keeps the state of the search being declared.
	 * @param parametersToSearch the search parameters
	 * @param statements the statements declared so far
	 */
	CcpSelectFoundInEntity(Collection<CcpJsonRepresentation> parametersToSearch, CcpJsonRepresentation statements) {
		this.parametersToSearch = parametersToSearch;
		this.statements = statements;
	}

	/**
	 * When the condition holds, runs the action (with the record found under {@code _entities.<entity>} when the condition
	 * is "present").
	 * @param action the action to run
	 * @return the next step of the chain
	 */
	public CcpSelectNextStep executeAction(CcpBusiness action) {
		CcpSelectNextStep nextStep = this.addStatement("action", action);
		return nextStep;
	}

	/**
	 * When the condition holds, interrupts the search by throwing {@code CcpErrorFlowDisturb} with this status.
	 * @param status the status of the interruption
	 * @return the next step of the chain
	 */
	public CcpSelectNextStep returnStatus(CcpProcessStatus status) {
		CcpSelectNextStep nextStep = this.addStatement("status", status);
		return nextStep;
	}

	/**
	 * Adds the key/value to the last declared statement.
	 * @param key {@code action} or {@code status}
	 * @param obj the action or the status
	 * @return the next step of the chain
	 */
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
