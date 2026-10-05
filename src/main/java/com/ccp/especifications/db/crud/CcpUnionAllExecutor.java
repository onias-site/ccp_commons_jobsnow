package com.ccp.especifications.db.crud;

import java.util.Collection;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.utils.entity.CcpEntity;

/**
 * Contract for executing UNION ALL queries on the database. Allows fetching
 * records of several entities in a single operation, returning a {@code CcpSelectUnionAll}
 * with the results.

 */
public interface CcpUnionAllExecutor {

	/**
	 * Searches, in a single call, the records of every entity whose id can be computed from each of the JSONs.
	 * @param values the search parameters
	 * @param entities the entities searched
	 * @return the condensed result
	 */
	CcpSelectUnionAll unionAll(Collection<CcpJsonRepresentation> values, CcpEntity... entities);
}
