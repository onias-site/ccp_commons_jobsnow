package com.ccp.especifications.cache;

import java.util.Arrays;
import java.util.Collection;
import java.util.Set;
import java.util.function.Function;

import com.ccp.business.CcpBusiness;
import com.ccp.constants.CcpOtherConstants;
import com.ccp.decorators.CcpFieldName;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.db.bulk.CcpBulkItem;
import com.ccp.especifications.db.utils.entity.CcpEntity;
import com.ccp.especifications.db.utils.entity.decorators.engine.CcpEntityMetaData;

/**
 * Object-oriented wrapper over {@link CcpCache} that holds both the cache key and
 * contextual parameters ({@link CcpJsonRepresentation}). Simplifies the use of the cache in the business
 * layers, allowing composite keys to be built fluently from entities and
 * identifiers, without directly exposing the {@link CcpCache} interface.
 */
public final class CcpCacheDecorator {
	
	/** The cache implementation registered in the dependency injection. */
	private final CcpCache cache = CcpDependencyInjection.getDependency(CcpCache.class);
	
	/** The key segments accumulated by {@link #incrementKey(String, Object)}, passed to the fallback functions. */
	private final CcpJsonRepresentation cacheParameters;

	/** The cache key. */
	public final String key;
	
	/**
	 * Creates the decorator from a {@link CcpBulkItem}, deriving the cache key from the entity name and the item id.
	 *
	 * @param bulkItem bulk item whose metadata defines the cache key
	 */
	public CcpCacheDecorator(CcpBulkItem bulkItem) {
		this(bulkItem.entity, bulkItem.id);
	}
	
	/**
	 * Creates the decorator with a key in the format {@code records.entity.<name>.id.<id>}.
	 *
	 * @param entity entity whose name makes up the key
	 * @param id identifier of the record
	 */
	public CcpCacheDecorator(CcpEntity entity, String id) {
		CcpEntityMetaData entityDetails = entity.getEntityMetaData();
		this.cacheParameters = CcpOtherConstants.EMPTY_JSON;
		String entityKeyPrefix = "records.entity." + entityDetails.entityName;
		String recordKeyPrefix = entityKeyPrefix + ".id.";
		this.key = recordKeyPrefix + id ;
	}
	
	/**
	 * Creates the decorator directly with an arbitrary key.
	 *
	 * @param key cache key to use
	 */
	public CcpCacheDecorator(String key) {
		this.cacheParameters = CcpOtherConstants.EMPTY_JSON;
		this.key = key;
	}
	
	/**
	 * Creates a decorator with an already extended key and its accumulated parameters.
	 * @param json the accumulated parameters
	 * @param key the cache key
	 */
	private CcpCacheDecorator(CcpJsonRepresentation json, String key) {
		this.cacheParameters = json;
		this.key = key;
	}

	/**
	 * Delegates to {@link CcpCache#get} using the internal key and parameters; executes
	 * {@code taskToGetValue} if the cache is empty and stores the result.
	 *
	 * @param taskToGetValue function executed when the cache is empty
	 * @param cacheSeconds TTL in seconds for the stored value
	 * @return the value obtained from the cache or produced by the function
	 */
	public <V> V get(Function<CcpJsonRepresentation,V> taskToGetValue, int cacheSeconds) {
		return this.cache.get(this.key, this.cacheParameters, taskToGetValue, cacheSeconds);
	}

	/**
	 * JSON read-through on this key: see {@link CcpCache#get(String, CcpJsonRepresentation, CcpBusiness, int)}.
	 * @param taskToGetValue business run on a cache miss
	 * @param json input of the business on a miss
	 * @param cacheSeconds expiration in seconds
	 * @return the cached or computed JSON
	 */
	public CcpJsonRepresentation get(CcpBusiness taskToGetValue, CcpJsonRepresentation json, int cacheSeconds) {
		CcpJsonRepresentation cachedValue = this.cache.get(this.key, json, taskToGetValue, cacheSeconds);
		return cachedValue;
	}

	/**
	 * Returns the cached value or {@code defaultValue} when absent.
	 *
	 * @param defaultValue value returned when the key is not in the cache
	 * @return the stored value or {@code defaultValue}
	 */
	public <V> V getOrDefault(V defaultValue) {
		return this.cache.getOrDefault(this.key, defaultValue);
	}

	/**
	 * Returns the cached value or throws the given exception when absent.
	 *
	 * @param e exception thrown when the key is not in the cache
	 * @return the stored value
	 */
	public <V> V getOrThrowException(RuntimeException e) {
		return this.cache.getOrThrowException(this.key, e);
	}

	/**
	 * Checks whether there is a value in the cache for this decorator's key.
	 *
	 * @return {@code true} if the value is present in the cache
	 */
	public boolean isPresentInTheCache() {
		boolean cachePresent = this.cache.isPresent(this.key);
		return cachePresent;
	}

	/**
	 * Stores {@code value} in the cache with a TTL and returns {@code this} for chaining.
	 *
	 * @param value value to store
	 * @param secondsDelay TTL in seconds
	 * @return this instance, for chaining
	 */
	public CcpCacheDecorator put(Object value, int secondsDelay) {
		this.cache.put(this.key, value, secondsDelay);
		return this;
	}

	/**
	 * Removes from the cache the entry of this decorator's key.
	 */
	public void delete() {
		this.cache.delete(this.key);
	}

	/**
	 * Removes all the given keys at once, in a single round trip to the cache server
	 * when the implementation supports batches.
	 *
	 * <p>It is static because whoever invalidates the cache in bulk ({@code JnDeleteKeysFromCache}) starts from a
	 * set of keys that are already built, not from an entity: building a decorator per key just to
	 * delete it was exactly what turned one invalidation into N network round trips.</p>
	 *
	 * @param keys the keys to remove; an empty collection makes no call at all
	 */
	public static void deleteAll(Collection<String> keys) {

		boolean hasNothingToDelete = keys.isEmpty();

		if(hasNothingToDelete) {
			return;
		}

		CcpCache cache = CcpDependencyInjection.getDependency(CcpCache.class);
		cache.deleteAll(keys);
	}
	
	/**
	 * Creates a new decorator with the key extended by {@code .<key>.<value>} and with the pair added
	 * to the parameters JSON, allowing hierarchical/composite keys.
	 *
	 * @param key key segment to append
	 * @param value value matching the segment
	 * @return new decorator with the accumulated key
	 */
	public CcpCacheDecorator incrementKey(String key, Object value) {
		String currentKeyWithDot = this.key + ".";
		String keyWithSegment = currentKeyWithDot + key;
		String keyWithSegmentAndDot = keyWithSegment + ".";
		String extendedKey = keyWithSegmentAndDot + value;
		CcpFieldName ccpFieldName = new CcpFieldName(key);
		CcpJsonRepresentation extendedParameters = this.cacheParameters.put(ccpFieldName, value);
		CcpCacheDecorator ccpCacheDecorator = new CcpCacheDecorator(extendedParameters, extendedKey);
		return ccpCacheDecorator;
	}
	
	/**
	 * Extracts a subset of fields from the given JSON and applies {@link #incrementKey} to each one,
	 * returning a new decorator with the accumulated key.
	 *
	 * @param json JSON from which the fields will be extracted
	 * @param keys names of the fields to include in the key
	 * @return new decorator with the accumulated key
	 */
	public CcpCacheDecorator incrementKeys(CcpJsonRepresentation json, String... keys) {
		
		CcpJsonRepresentation jsonPiece = json.getJsonPiece(Arrays.asList(keys));
		
		CcpCacheDecorator result = this.incrementKeys(jsonPiece);
		
		return result;
	}

	/**
	 * Iterates over all the fields of the given JSON and applies {@link #incrementKey} to each field/value pair.
	 *
	 * @param jsonPiece JSON whose fields make up the key extension
	 * @return new decorator with the accumulated key
	 */
	public CcpCacheDecorator incrementKeys(CcpJsonRepresentation jsonPiece) {
		CcpCacheDecorator result = this;
		
		Set<String> keySet = jsonPiece.fieldSet();
		
		for (String key : keySet) {
			CcpFieldName ccpFieldName = new CcpFieldName(key);
			Object value = jsonPiece.get(ccpFieldName);
			result = result.incrementKey(key, value);
		}
		return result;
	}
	
	/**
	 * Returns the current cache key of the decorator.
	 *
	 * @return the cache key
	 */
	public String toString() {
		return this.key;
	}
}
