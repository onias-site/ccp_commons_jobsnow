package com.ccp.especifications.cache;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

import com.ccp.business.CcpBusiness;
import com.ccp.decorators.CcpJsonRepresentation;

/**
 * Contract of the cache system (e.g. GCP Memcache). Defines reading, writing and removal of values identified by a text
 * key, with expiration time and automatic fallback when the key is not present.
 */
public interface CcpCache {

	 /**
	  * Returns the value stored under the key.
	  * @param key the cache key
	  * @return the stored value, or {@code null} when the key is not in the cache
	  */
	 Object get(String key) ;

		/**
		 * Returns the value cached under the key; on a miss, runs {@code taskToGetValue}, caches its result with the given
		 * expiration and returns it.
		 * @param <V> the type of the value
		 * @param key the cache key
		 * @param json input passed to the fallback function
		 * @param taskToGetValue function run on a cache miss
		 * @param cacheSeconds expiration in seconds
		 * @return the cached value or the one produced by the function
		 */
		@SuppressWarnings("unchecked")
		default <V> V get(String key, CcpJsonRepresentation json, Function<CcpJsonRepresentation, V> taskToGetValue, int cacheSeconds) {

			Object object = this.get(key);
			boolean foundInCache = object != null;

			if (foundInCache) {
				V v = (V) object;
				return v;
			}
			V value = taskToGetValue.apply(json);
			this.put(key, value, cacheSeconds);

			return value;
		}

		/**
		 * JSON variant of the read-through: on a miss, runs {@code taskToGetValue.execute(json)} (validation included), caches
		 * the map of the result and returns it; on a hit, rebuilds the JSON from the cached map, or parses the cached text.
		 * @param key the cache key
		 * @param json input of the business on a miss
		 * @param taskToGetValue business run on a cache miss
		 * @param cacheSeconds expiration in seconds
		 * @return the cached or computed JSON
		 */
		@SuppressWarnings("unchecked")
		default CcpJsonRepresentation get(String key, CcpJsonRepresentation json, CcpBusiness taskToGetValue, int cacheSeconds) {

			Object object = this.get(key);
			boolean missingInCache = object == null;

			if (missingInCache) {
				CcpJsonRepresentation value = taskToGetValue.execute(json);
				this.put(key, value.content, cacheSeconds);
				return value;
			}
			
			if(object instanceof Map map) {
				CcpJsonRepresentation value = new CcpJsonRepresentation(map);
				return value;
			}
			String toString = object.toString();

			CcpJsonRepresentation value = new CcpJsonRepresentation(toString);
			return value;
			
		}
	
	
	/**
	 * Returns the value cached under the key, or the default value on a miss, without throwing.
	 * @param <V> the type of the value
	 * @param key the cache key
	 * @param defaultValue value returned on a miss
	 * @return the stored value or {@code defaultValue}
	 */
	@SuppressWarnings("unchecked")
	default <V> V getOrDefault(String key, V defaultValue) {
		Object object = this.get(key);
		boolean missingInCache2 = object == null;

		if(missingInCache2) {
			return defaultValue;
		}
		V v2 = (V) object;
		return v2;
	}
	
	/**
	 * Returns the value cached under the key, throwing the given exception on a miss.
	 * @param <V> the type of the value
	 * @param key the cache key
	 * @param e exception thrown on a miss
	 * @return the stored value
	 */
	@SuppressWarnings("unchecked")
	default <V> V getOrThrowException(String key, RuntimeException e) {
		Object object = this.get(key);
		boolean missingInCache3 = object == null;

		if(missingInCache3) {
			throw e;
		}
		V v3 = (V) object;

		return v3;
	}
	
	/**
	 * Tells whether there is a value cached under the key.
	 * @param key the cache key
	 * @return {@code true} when the value is present
	 */
	default boolean isPresent(String key) {
		var get = this.get(key);
		boolean isPresent = get != null;
		return isPresent;
	}

	/**
	 * Stores the value under the key, expiring after the given seconds.
	 * @param key the cache key
	 * @param value the value to store
	 * @param secondsDelay expiration in seconds
	 * @return this instance, for chaining
	 */
	CcpCache put(String key, Object value, int secondsDelay);

	/**
	 * Removes the entry of the key.
	 * <p>
	 * On purpose it does not return the removed value: returning it would force the implementation to read before
	 * deleting (two round trips to the cache server instead of one), and no caller used the return value: every removal
	 * point ({@code JnDeleteKeysFromCache} and the ones of {@code DecoratorCacheEntity}) calls this method as a standalone
	 * command.
	 * @param key the cache key
	 */
	void delete(String key);

	/**
	 * Removes all the given keys at once.
	 * <p>
	 * The default implementation deletes them one by one, so that any {@code CcpCache} keeps working unchanged.
	 * Implementations that talk to a real cache server should override it with the provider's batch operation: the
	 * invalidation runs before <b>every</b> search ({@code CcpCrud.deleteKeysInCache}), and a search touching nine
	 * entities becomes nine network round trips when it could be one.
	 * @param keys the keys to remove
	 */
	default void deleteAll(Collection<String> keys) {
		for (String key : keys) {
			this.delete(key);
		}
	}
}
