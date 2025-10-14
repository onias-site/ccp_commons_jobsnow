package com.ccp.especifications.db.crud;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.cache.CcpCacheDecorator;
import com.ccp.especifications.db.utils.CcpEntity;
import com.ccp.especifications.db.utils.CcpErrorEntityPrimaryKeyIsMissing;

public interface CcpCrud {
	CcpJsonRepresentation getOneById(String entityName, String id);

	CcpUnionAllExecutor getUnionAllExecutor();
	
	default CcpSelectUnionAll unionBetweenMainAndTwinEntities(CcpJsonRepresentation json, Consumer<String[]> functionToDeleteKeysInTheCache, CcpEntity entity) {
		CcpEntity[] thisEntityAndHisTwinEntity = entity.getThisEntityAndHisTwinEntity();
		CcpSelectUnionAll unionAll = this.unionAll(json, functionToDeleteKeysInTheCache, thisEntityAndHisTwinEntity);
		return unionAll;
	}
	
	default CcpSelectUnionAll unionAll(CcpJsonRepresentation[] jsons, Consumer<String[]> functionToDeleteKeysInTheCache, CcpEntity... entities) {
		this.deleteKeysInCache(jsons, functionToDeleteKeysInTheCache,  entities);
		List<CcpJsonRepresentation> asList = Arrays.asList(jsons);
		CcpUnionAllExecutor unionAllExecutor = this.getUnionAllExecutor();
		CcpSelectUnionAll unionAll = unionAllExecutor.unionAll(asList, entities);
		return unionAll;
	}


	
	default CcpSelectUnionAll unionAll(CcpJsonRepresentation json, Consumer<String[]> functionToDeleteKeysInTheCache, CcpEntity... entities) {
		CcpJsonRepresentation[] jsons = new CcpJsonRepresentation[] {json};
		
		CcpSelectUnionAll unionAll = this.unionAll(jsons, functionToDeleteKeysInTheCache, entities);
		return unionAll;
	}


	CcpJsonRepresentation save(String entityName, CcpJsonRepresentation json, String id);

	boolean exists(String entityName, String id);

	boolean delete(String entityName, String id); 
	
	default CcpCrud deleteKeysInCache(CcpJsonRepresentation[] jsons, Consumer<String[]> functionToDeleteKeysInTheCache, CcpEntity... entities) {
		Set<String> keysToDeleteInCache = new HashSet<>();
		for (CcpEntity entity : entities) {
			for (CcpJsonRepresentation json : jsons) {
				try {
					CcpCacheDecorator cache = new CcpCacheDecorator(entity, json);
					keysToDeleteInCache.add(cache.key);
				} catch (CcpErrorEntityPrimaryKeyIsMissing e) {

				}
			}
		}
		
		String[] array = keysToDeleteInCache.toArray(new String[keysToDeleteInCache.size()]);
		functionToDeleteKeysInTheCache.accept(array);
		return this;
	}
	

}
