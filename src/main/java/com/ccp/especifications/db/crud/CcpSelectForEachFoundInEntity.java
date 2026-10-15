package com.ccp.especifications.db.crud;

import java.util.Collection;
import java.util.List;

import com.ccp.business.CcpBusiness;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.json.fields.validation.CcpJsonCommonsFields;

/**
 * Step of the search fluent chain right after {@code forEachIdPresentInEntity}: declares the action run once for each
 * search parameter whose record exists in the entity.
 */
public class CcpSelectForEachFoundInEntity {

	/** The search parameters. */
	private final Collection<CcpJsonRepresentation> parametersToSearch;
	/** The statements declared so far. */
	private final CcpJsonRepresentation statements;

	/**
	 * Keeps the state of the search being declared.
	 * @param parametersToSearch the search parameters
	 * @param statements the statements declared so far
	 */
	CcpSelectForEachFoundInEntity(Collection<CcpJsonRepresentation> parametersToSearch, CcpJsonRepresentation statements) {
		this.parametersToSearch = parametersToSearch;
		this.statements = statements;
	}

	/**
	 * Runs the action once for each search parameter found in the entity. The action receives that parameter (not the
	 * JSON that flows through the chain) with its record under {@code _entities.<entity>}; what it returns is
	 * discarded, and the JSON that flows through the chain does not change.
	 * @param action the action to run
	 * @return the next step of the chain
	 */
	public CcpSelectNextStep executeAction(CcpBusiness action) {
		List<CcpJsonRepresentation> statementList = this.statements.getAsJsonList(CcpJsonCommonsFields.statements);
		int statementCount = statementList.size();
		int lastIndex = statementCount - 1;
		CcpJsonRepresentation lastStatement = statementList.get(lastIndex);
		CcpJsonRepresentation updatedLastStatement = lastStatement.put(CcpSelectFinally.JsonFieldNames.action, action);
		List<CcpJsonRepresentation> statementsWithoutLast = statementList.subList(0, lastIndex);
		statementsWithoutLast.add(updatedLastStatement);
		CcpJsonRepresentation newStatements = this.statements.put(CcpJsonCommonsFields.statements, statementsWithoutLast);
		CcpSelectNextStep nextStep = new CcpSelectNextStep(this.parametersToSearch, newStatements);
		return nextStep;
	}
}
