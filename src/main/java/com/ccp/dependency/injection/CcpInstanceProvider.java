package com.ccp.dependency.injection;

/**
 * Contract of the dependency factories used by {@code CcpDependencyInjection}. Each implementation knows how to build
 * and deliver the concrete instance of a given interface {@code T}.
 * @param <T> type delivered by the provider
 */
public interface CcpInstanceProvider<T> { 
	/**
	 * Builds and returns the concrete instance of {@code T}.
	 * @return the instance
	 */
	T getInstance();
}
