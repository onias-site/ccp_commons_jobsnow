package com.ccp.especifications.db.crud;

import java.util.Collection;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonFieldName;

/**
 * Step of the search fluent chain right after {@code loadThisIdFromEntity}: the record of the entity, when it exists,
 * is added to the JSON under {@code _entities.<entity>}.
 */
public class CcpSelectLoadDataFromEntity {

	/** The search parameters. */
	private final Collection<CcpJsonRepresentation> parametersToSearch;
	/** The statements declared so far. */
	private final CcpJsonRepresentation statements;

	/**
	 * Keeps the state of the search being declared.
	 * @param parametersToSearch the search parameters
	 * @param statements the statements declared so far
	 */
	CcpSelectLoadDataFromEntity(Collection<CcpJsonRepresentation> parametersToSearch, CcpJsonRepresentation statements) {
		this.parametersToSearch = parametersToSearch;
		this.statements = statements;
	}

	/**
	 * Declares one more statement.
	 * @return the statement step of the chain
	 */
	public CcpSelectProcedure and() {
		CcpSelectProcedure procedure = new CcpSelectProcedure(this.parametersToSearch, this.statements);
		return procedure;
	}

	/**
	 * Ends the declaration, naming the fields returned by the search.
	 * @param fields the fields kept in the result
	 * @return the final step of the chain
	 */
	public CcpSelectFinally andFinally(CcpJsonFieldName... fields) {
		CcpSelectFinally finalStep = new CcpSelectFinally(this.parametersToSearch, this.statements, fields);
		return finalStep;
	}
}
