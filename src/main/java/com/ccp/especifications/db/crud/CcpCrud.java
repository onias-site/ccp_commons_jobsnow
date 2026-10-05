package com.ccp.especifications.db.crud;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.cache.CcpCacheDecorator;
import com.ccp.especifications.db.utils.entity.CcpEntity;
import com.ccp.especifications.db.utils.entity.decorators.engine.CcpErrorEntityPrimaryKeyIsMissing;

/**
 * Central database access contract (Elasticsearch). Provides basic CRUD operations
 * (find by id, save, exists, delete) and the mechanism to search several entities
 * at once through {@code unionAll}, with automatic integration with cache invalidation.
 */
public interface CcpCrud {

	/**
	 * Reads one document by id.
	 * @param entityName the entity (index) name
	 * @param id the document id
	 * @return the document
	 */
	CcpJsonRepresentation getOneById(String entityName, String id);

	/**
	 * Returns the executor of union-all searches of the database.
	 * @return the union-all executor
	 */
	CcpUnionAllExecutor getUnionAllExecutor();

	/**
	 * Invalidates the cache keys of the records searched and then searches, in a single call, the records of every entity
	 * whose id can be computed from each of the JSONs.
	 * @param jsons the search parameters
	 * @param functionToDeleteKeysInTheCache receives the cache keys to invalidate
	 * @param entities the entities searched
	 * @return the condensed result of the search
	 */
	default CcpSelectUnionAll unionAll(CcpJsonRepresentation[] jsons, Consumer<String[]> functionToDeleteKeysInTheCache, CcpEntity... entities) {
		this.deleteKeysInCache(jsons, functionToDeleteKeysInTheCache, entities);
		List<CcpJsonRepresentation> jsonList = Arrays.asList(jsons);
		CcpUnionAllExecutor unionAllExecutor = this.getUnionAllExecutor();
		CcpSelectUnionAll unionAll = unionAllExecutor.unionAll(jsonList, entities);
		return unionAll;
	}

	/**
	 * Single-JSON variant of {@link #unionAll(CcpJsonRepresentation[], Consumer, CcpEntity...)}.
	 * @param json the search parameters
	 * @param functionToDeleteKeysInTheCache receives the cache keys to invalidate
	 * @param entities the entities searched
	 * @return the condensed result of the search
	 */
	default CcpSelectUnionAll unionAll(CcpJsonRepresentation json, Consumer<String[]> functionToDeleteKeysInTheCache, CcpEntity... entities) {
		CcpJsonRepresentation[] jsons = new CcpJsonRepresentation[]{json};
		CcpSelectUnionAll unionAll = this.unionAll(jsons, functionToDeleteKeysInTheCache, entities);
		return unionAll;
	}

	/**
	 * Creates or replaces the document.
	 * @param entityName the entity (index) name
	 * @param json the document data
	 * @param id the document id
	 * @return the raw response of the database, to be read by {@link #isInsertedDocument(CcpJsonRepresentation)}
	 */
	CcpJsonRepresentation save(String entityName, CcpJsonRepresentation json, String id);

	/**
	 * Tells whether the response returned by {@code save} corresponds to the insertion of a new document,
	 * rather than to the update of a document that already existed. The implementer of this contract is the only one
	 * that knows the format of the database response and the HTTP status that comes with it, which is why the
	 * response is interpreted here.
	 *
	 * @param saveResponse the response returned by {@code save}
	 * @return {@code true} if the document was inserted, {@code false} if it was updated
	 */
	boolean isInsertedDocument(CcpJsonRepresentation saveResponse);

	/**
	 * Tells whether the document exists.
	 * @param entityName the entity (index) name
	 * @param id the document id
	 * @return {@code true} when the document exists
	 */
	boolean exists(String entityName, String id);

	/**
	 * Deletes the document.
	 * @param entityName the entity (index) name
	 * @param id the document id
	 * @return {@code true} when the document existed and was deleted, {@code false} when it was not found
	 */
	boolean delete(String entityName, String id);

	/**
	 * Computes the cache key of every pair entity/JSON whose primary key is complete (pairs with missing primary key fields
	 * are skipped) and hands the distinct keys to the given function.
	 * @param jsons the search parameters
	 * @param functionToDeleteKeysInTheCache receives the cache keys to invalidate
	 * @param entities the entities searched
	 * @return this instance
	 */
	default CcpCrud deleteKeysInCache(CcpJsonRepresentation[] jsons, Consumer<String[]> functionToDeleteKeysInTheCache, CcpEntity... entities) {
		Set<String> keysToDeleteInCache = new HashSet<>();
		for (CcpEntity entity : entities) {
			for (CcpJsonRepresentation json : jsons) {
				try {
					String recordId = entity.calculateId(json);
					CcpCacheDecorator cache = new CcpCacheDecorator(entity, recordId);
					keysToDeleteInCache.add(cache.key);
				} catch (CcpErrorEntityPrimaryKeyIsMissing e) {
				}
			}
		}
		int keysToDeleteInCacheSize = keysToDeleteInCache.size();
		String[] keysToDeleteArray = keysToDeleteInCache.toArray(new String[keysToDeleteInCacheSize]);
		functionToDeleteKeysInTheCache.accept(keysToDeleteArray);
		return this;
	}
}
