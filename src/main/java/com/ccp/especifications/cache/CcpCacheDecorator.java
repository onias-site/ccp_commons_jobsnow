package com.ccp.especifications.cache;

import java.util.Set;
import java.util.function.Function;

import com.ccp.constantes.CcpOtherConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.db.bulk.CcpBulkItem;
import com.ccp.especifications.db.utils.entity.CcpEntity;

public final class CcpCacheDecorator {
	
	private final CcpCache cache = CcpDependencyInjection.getDependency(CcpCache.class);
	
	private final CcpJsonRepresentation cacheParameters;

	public final String key;
	
	public CcpCacheDecorator(CcpBulkItem bulkItem) {
		this(bulkItem.entity, bulkItem.id);
	}
	
	public CcpCacheDecorator(CcpEntity entity, CcpJsonRepresentation json) {
		this(entity, entity.calculateCacheId(json));
	}

	
	public CcpCacheDecorator(CcpEntity entity, String id) {
		String entityName = entity.getEntityName();
		this.cacheParameters = CcpOtherConstants.EMPTY_JSON;
		this.key = "records.entity." + entityName + ".id." + id ;
	}
	
	public CcpCacheDecorator(String key) {
		this.cacheParameters = CcpOtherConstants.EMPTY_JSON;
		this.key = key;
	}
	
	private CcpCacheDecorator(CcpJsonRepresentation json, String key) {
		this.cacheParameters = json;
		this.key = key;
	}

	public <V> V get(Function<CcpJsonRepresentation,V> taskToGetValue, int cacheSeconds) {
		return this.cache.get(this.key, this.cacheParameters, taskToGetValue, cacheSeconds);
	}

	public <V> V getOrDefault(V defaultValue) {
		return this.cache.getOrDefault(this.key, defaultValue);
	}

	public <V> V getOrThrowException(RuntimeException e) {
		return this.cache.getOrThrowException(this.key, e);
	}

	public boolean isPresentInTheCache() {
		return this.cache.isPresent(this.key);
	}

	public CcpCacheDecorator put(Object value, int secondsDelay) {
		this.cache.put(this.key, value, secondsDelay);
		return this;
	}

	public <V> V delete() {
		return this.cache.delete(this.key);
	}
	
	public CcpCacheDecorator incrementKey(String key, Object value) {
		String _key = this.key + "." + key + "." + value;
		CcpJsonRepresentation put = this.cacheParameters.getDynamicVersion().put(key, value);
		CcpCacheDecorator ccpCacheDecorator = new CcpCacheDecorator(put, _key);
		return ccpCacheDecorator;
	}
	
	public CcpCacheDecorator incrementKeys(CcpJsonRepresentation json, String... keys) {
		
		CcpJsonRepresentation jsonPiece = json.getDynamicVersion().getJsonPiece(keys);
		
		CcpCacheDecorator result = this.incrementKeys(jsonPiece);
		
		return result;
	}

	public CcpCacheDecorator incrementKeys(CcpJsonRepresentation jsonPiece) {
		CcpCacheDecorator result = this;
		
		Set<String> keySet = jsonPiece.fieldSet();
		
		for (String key : keySet) {
			Object value = jsonPiece.getDynamicVersion().get(key);
			result = result.incrementKey(key, value);
		}
		return result;
	}
	
	public String toString() {
		return this.key;
	}
}
