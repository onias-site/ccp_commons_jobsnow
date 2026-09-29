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

	CcpJsonRepresentation getOneById(String entityName, String id);

	CcpUnionAllExecutor getUnionAllExecutor();

	default CcpSelectUnionAll unionAll(CcpJsonRepresentation[] jsons, Consumer<String[]> functionToDeleteKeysInTheCache, CcpEntity... entities) {
		this.deleteKeysInCache(jsons, functionToDeleteKeysInTheCache, entities);
		List<CcpJsonRepresentation> jsonList = Arrays.asList(jsons);
		CcpUnionAllExecutor unionAllExecutor = this.getUnionAllExecutor();
		CcpSelectUnionAll unionAll = unionAllExecutor.unionAll(jsonList, entities);
		return unionAll;
	}

	default CcpSelectUnionAll unionAll(CcpJsonRepresentation json, Consumer<String[]> functionToDeleteKeysInTheCache, CcpEntity... entities) {
		CcpJsonRepresentation[] jsons = new CcpJsonRepresentation[]{json};
		CcpSelectUnionAll unionAll = this.unionAll(jsons, functionToDeleteKeysInTheCache, entities);
		return unionAll;
	}

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

	boolean exists(String entityName, String id);

	boolean delete(String entityName, String id);

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
