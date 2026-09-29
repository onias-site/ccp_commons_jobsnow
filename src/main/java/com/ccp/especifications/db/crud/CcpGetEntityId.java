package com.ccp.especifications.db.crud;

import java.util.Arrays;
import java.util.Collection;

import com.ccp.constants.CcpOtherConstants;
import com.ccp.decorators.CcpJsonRepresentation;

/**
 * Entry point of the search fluent API. Receives one or more {@link CcpJsonRepresentation} as
 * search parameters and starts building a {@link CcpSelectProcedure}, allowing search conditions
 * to be chained in a readable way.
 */
public class CcpGetEntityId {

	private final Collection<CcpJsonRepresentation> parametersToSearch;

	public CcpGetEntityId(CcpJsonRepresentation... parametersToSearch) {
		this.parametersToSearch = Arrays.asList(parametersToSearch);
	}

	public CcpSelectProcedure toBeginProcedureAnd() {
		CcpSelectProcedure procedure = new CcpSelectProcedure(this.parametersToSearch, CcpOtherConstants.EMPTY_JSON);
		return procedure;
	}

	// ─── Fluent chain steps ───────────────────────────────────────────────────

	/**
	 * Core of the search fluent API. Allows declaring, in a chained and readable way, which entities
	 * to query, which presence conditions to check and which actions to execute in each case.
	 */


	/**
	 * Intermediate step of the fluent chain produced by {@link CcpSelectProcedure#loadThisIdFromEntity}.
	 * Represents the point after declaring that the data of an entity must be loaded.
	 */


	/**
	 * Intermediate step of the fluent chain produced by {@link CcpSelectProcedure#ifThisIdIsPresentInEntity}
	 * or {@link CcpSelectProcedure#ifThisIdIsNotPresentInEntity}. Allows defining what to do when
	 * the presence condition is met.
	 */


	/**
	 * Intermediate step of the fluent chain that appears after an action or status is defined in a
	 * statement. Allows adding further conditions to the flow or ending the chain.
	 */


	/**
	 * Final step of the search fluent chain. Executes every accumulated statement, coordinates the
	 * {@code unionAll} search, applies the flow rules and returns only the specified fields.
	 * Throws {@link CcpErrorFlowDisturb} when a flow condition is not met.
	 */

	
	// ─── Exceptions ───────────────────────────────────────────────────────────



	
	
}

/**
 * Internal (package-private) business that replaces the {@code entity} field of a JSON — which carries a
 * {@code CcpEntity} object — with the entity's textual name ({@code entityName}). Used as a preparation step before
 * persistence operations.
 */

/**
 * Internal (package-private) business that converts the {@code status} field — which holds a {@code CcpProcessStatus}
 * — into two textual fields: {@code statusName} (enum name) and {@code statusNumber} (numeric value).
 * Removes the original {@code status} field from the result.
 */
