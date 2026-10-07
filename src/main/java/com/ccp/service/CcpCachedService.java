package com.ccp.service;

import java.util.Map;

import com.ccp.decorators.CcpHashDecorator;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonFieldName;
import com.ccp.decorators.CcpStringDecorator;
import com.ccp.especifications.cache.CcpCacheDecorator;
import com.ccp.hash.CcpHashAlgorithm;

/**
 * Wraps a {@link CcpService} with a cache: the result is cached under the SHA-1 of the service (class and name), the
 * field and the value of that input field, so equal values of the field reuse the result of the same service within the
 * cache period.
 * <p>
 * Until 2026-10-06 the key was only the hash of the field value: two cached services receiving the same value (the same
 * e-mail, say) answered with each other's result.
 */
public class CcpCachedService{

	/** Fields added by this class to the output. */
	enum JsonFieldNames implements CcpJsonFieldName {
		/** The cache key used: SHA-1 of the service, the field and its value. */
		cacheHash
	}

	/** Input field whose value identifies the cache entry. */
	private final CcpJsonFieldName fieldToCache;
	/** Service executed when there is no cached result. */
	private final CcpService service;
	/** How long, in seconds, a result stays cached. */
	private final int cacheSeconds;

	/**
	 * Builds the cached service.
	 * @param fieldToCache input field whose value identifies the cache entry
	 * @param service service executed on a cache miss
	 * @param cacheSeconds how long a result stays cached
	 */
	public CcpCachedService(CcpJsonFieldName fieldToCache, CcpService service, int cacheSeconds) {
		this.fieldToCache = fieldToCache;
		this.cacheSeconds = cacheSeconds;
		this.service = service;
	}

	/**
	 * Returns the cached result for the value of {@code fieldToCache}, executing the service and caching its result on a
	 * miss, and adds {@code cacheHash} to the output.
	 * @param map the input
	 * @return the (possibly cached) output plus {@code cacheHash}
	 */
	public Map<String, Object> execute(Map<String, Object> map) {
		CcpJsonRepresentation json = new CcpJsonRepresentation(map);
		String fieldValue = json.getAsString(this.fieldToCache);
		String serviceClassName = this.service.getClass().getName();
		String serviceName = this.service.name();
		String fieldName = this.fieldToCache.getValue();
		String cacheKeyText = serviceClassName + "." + serviceName + "/" + fieldName + "/" + fieldValue;
		CcpStringDecorator cacheKey = new CcpStringDecorator(cacheKeyText);
		CcpHashDecorator hash = cacheKey.hash();
		String hashValue = hash.asString(CcpHashAlgorithm.SHA1);
		CcpCacheDecorator ccd = new CcpCacheDecorator(hashValue);
		CcpJsonRepresentation value = ccd.get(this.service, json, this.cacheSeconds);
		CcpJsonRepresentation put = value.put(JsonFieldNames.cacheHash, hashValue);
		return put.content;
	}
}
