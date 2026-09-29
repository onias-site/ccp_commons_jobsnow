package com.ccp.especifications.db.bulk;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.db.crud.CcpCrud;
import com.ccp.especifications.db.crud.CcpHandleWithSearchResultsInTheEntity;
import com.ccp.especifications.db.crud.CcpSelectUnionAll;
import com.ccp.especifications.db.utils.entity.CcpEntity;
import java.util.stream.Stream;

/**
 * High-level contract that combines a {@code unionAll} search on the database with the subsequent
 * execution of bulk operations. Allows processing several entities in a single query and then sending the
 * resulting items to the bulk, orchestrating search and write in an integrated way.
 */
public interface CcpExecuteBulkOperation {

	/**
	 * Extracts the set of entities to search from the handlers, runs a {@code unionAll} over
	 * all of them, applies each handler to the result (collecting the {@link CcpBulkItem}s) and triggers the
	 * bulk operation with every collected item.
	 *
	 * @param json JSON with the search parameters
	 * @param functionToDeleteKeysInTheCache function that invalidates cache keys
	 * @param handlers handlers that define the entities and the create/update/delete logic
	 * @return the {@link CcpSelectUnionAll} with the search result data
	 */
	@SuppressWarnings("unchecked")
	default CcpSelectUnionAll executeSelectUnionAllThenExecuteBulkOperation(CcpJsonRepresentation json,  Consumer<String[]> functionToDeleteKeysInTheCache, CcpHandleWithSearchResultsInTheEntity<List<CcpBulkItem>> ... handlers) {
		Stream<CcpHandleWithSearchResultsInTheEntity<List<CcpBulkItem>>> handlersStream = Arrays.asList(handlers).stream();
		var entitiesToSearchStream = handlersStream.map(x -> x.getEntityToSearch());
		Set<CcpEntity> entitiesToSearch = entitiesToSearchStream.collect(Collectors.toSet());
		int entitiesCount = entitiesToSearch.size();
		CcpEntity[] entitiesToSearchArray = entitiesToSearch.toArray(new CcpEntity[entitiesCount]);
		CcpCrud crud = CcpDependencyInjection.getDependency(CcpCrud.class); 
		CcpSelectUnionAll unionAll = crud.unionAll(json, functionToDeleteKeysInTheCache, entitiesToSearchArray);
		
		List<CcpBulkItem> allBulkItems = new ArrayList<>();
		
		for (CcpHandleWithSearchResultsInTheEntity<List<CcpBulkItem>> handler : handlers) {
			List<CcpBulkItem> handlerBulkItems =  unionAll.handleRecordInUnionAll(json, handler);
			allBulkItems.addAll(handlerBulkItems);
		} 
		
		
		this.executeBulk(allBulkItems, functionToDeleteKeysInTheCache);

		return unionAll;
	}

	/**
	 * Actually executes the bulk operations for the given collection of items, delegating to the concrete
	 * executor and invalidating the necessary cache keys.
	 *
	 * @param items collection of bulk items to process
	 * @param functionToDeleteKeysInTheCache function that invalidates cache keys
	 * @return this instance, for chaining
	 */
	CcpExecuteBulkOperation executeBulk(Collection<CcpBulkItem> items,  Consumer<String[]> functionToDeleteKeysInTheCache);
}
