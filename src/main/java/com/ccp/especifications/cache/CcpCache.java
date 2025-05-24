package com.ccp.especifications.cache;

import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;

public interface CcpCache {

	 Object get(String key) ;
	
	@SuppressWarnings("unchecked")
	default <V> V get(String key, CcpJsonRepresentation json, Function<CcpJsonRepresentation, V> taskToGetValue, int cacheSeconds) {

		Object object = this.get(key);

		if (object != null) {
			return (V) object;
		}
		V value = taskToGetValue.apply(json);
		this.put(key, value, cacheSeconds);

		return value;
	}
	
	
	@SuppressWarnings("unchecked")
	default <V> V getOrDefault(String key, V defaultValue) {
		Object object = this.get(key);
		
		if(object == null) {
			return defaultValue;
		}
		return (V) object;
	}
	
	@SuppressWarnings("unchecked")
	default <V> V getOrThrowException(String key, RuntimeException e) {
		Object object = this.get(key);
		
		if(object == null) {
			throw e;
		}
		
		return (V) object;
	}
	
	default boolean isPresent(String key) {
		boolean isPresent = this.get(key) != null;
		return isPresent;
	}

	CcpCache put(String key, Object value, int secondsDelay);
	<V> V delete(String key);
}
