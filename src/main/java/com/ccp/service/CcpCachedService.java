package com.ccp.service;

import java.util.Map;

import com.ccp.decorators.CcpHashDecorator;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonFieldName;
import com.ccp.decorators.CcpStringDecorator;
import com.ccp.especifications.cache.CcpCacheDecorator;
import com.ccp.hash.CcpHashAlgorithm;

/**
 * Wraps a {@link CcpService} with a cache: the result is cached under the SHA-1 of the value of one input field, so
 * equal values of that field reuse the result within the cache period.
 * <p>
 * The cache key is only the hash of the field value; it does not include the service name.
 */
public class CcpCachedService{

	/** Fields added by this class to the output. */
	enum JsonFieldNames implements CcpJsonFieldName {
		/** SHA-1 of the cached field value, i.e. the cache key used. */
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
		CcpStringDecorator cacheKey = json.getAsStringDecorator(this.fieldToCache);
		CcpHashDecorator hash = cacheKey.hash();
		String hashValue = hash.asString(CcpHashAlgorithm.SHA1);
		CcpCacheDecorator ccd = new CcpCacheDecorator(hashValue);
		CcpJsonRepresentation value = ccd.get(this.service, json, this.cacheSeconds);
		CcpJsonRepresentation put = value.put(JsonFieldNames.cacheHash, hashValue);
		return put.content;
	}
}
