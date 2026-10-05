package com.ccp.especifications.db.query;

import java.util.List;
import java.util.function.Consumer;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.http.CcpHttpMethods;

/**
 * Binds a built query to the target indexes and delegates each operation to the {@code CcpQueryExecutor} registered in
 * the dependency injection, so callers do not pass the query and the indexes again and again.
 */
public class CcpQueryExecutorDecorator {
	/** The executor registered in the dependency injection. */
	private final CcpQueryExecutor requestExecutor = CcpDependencyInjection.getDependency(CcpQueryExecutor.class);
	/** The target indexes. */
	private final String[] resourcesNames;
	/** The built query. */
	private final CcpQueryOptions elasticQuery;

	/**
	 * Binds the query to the indexes.
	 * @param elasticQuery the built query
	 * @param resourcesNames the target indexes
	 */
	protected CcpQueryExecutorDecorator(CcpQueryOptions elasticQuery, String... resourcesNames) {
		this.resourcesNames = resourcesNames;
		this.elasticQuery = elasticQuery;
	}

	/**
	 * See {@link CcpQueryExecutor#getResultAsPackage}.
	 * @param url the request path
	 * @param method the HTTP method
	 * @param expectedStatus the expected HTTP status
	 * @param array the fields to return
	 * @return the raw result
	 */
	public CcpJsonRepresentation getResultAsPackage(String url, CcpHttpMethods method, int expectedStatus, String... array) {
		CcpJsonRepresentation resultAsPackage = this.requestExecutor.getResultAsPackage(url, method, expectedStatus, this.elasticQuery, this.resourcesNames, array);
		return resultAsPackage;
	}

	/**
	 * See {@link CcpQueryExecutor#getTermsStatis}.
	 * @param fieldName the field
	 * @return the statistics of the field
	 */
	public CcpJsonRepresentation getTermsStatis(String fieldName) {
		CcpJsonRepresentation termsStatis = this.requestExecutor.getTermsStatis(this.elasticQuery, this.resourcesNames, fieldName);
		return termsStatis;
	}

	/**
	 * Deletes every document matching the query in the indexes.
	 * @return the response of the database
	 */
	public CcpJsonRepresentation delete() {
		CcpJsonRepresentation delete = this.requestExecutor.delete(this.elasticQuery, this.resourcesNames);
		return delete;
	}

	/**
	 * Updates the documents matching the query with the new values.
	 * @param newValues the new values
	 * @return the response of the database
	 */
	public CcpJsonRepresentation update(CcpJsonRepresentation newValues) {
		CcpJsonRepresentation update = this.requestExecutor.update(this.elasticQuery, this.resourcesNames, newValues);
		return update;
	}

	/**
	 * Iterates over every document matching the query with scroll, one document at a time.
	 * @param scrollTime the expiration of the scroll context (e.g. "1m")
	 * @param size the batch size
	 * @param consumer receives each document
	 * @param fields the fields returned
	 * @return this decorator
	 */
	public CcpQueryExecutorDecorator consumeQueryResult(String scrollTime, int size,
			Consumer<CcpJsonRepresentation> consumer, String... fields) {
		this.requestExecutor.consumeQueryResult(this.elasticQuery, this.resourcesNames, scrollTime, size, consumer, fields);
		return this;
	}

	/**
	 * Counts the documents matching the query.
	 * @return the total
	 */
	public long total() {
		long total = this.requestExecutor.total(this.elasticQuery, this.resourcesNames);
		return total;
	}

	/**
	 * Returns the documents matching the query.
	 * @param fieldsToSearch the fields returned
	 * @return the documents
	 */
	public List<CcpJsonRepresentation> getResultAsList(String... fieldsToSearch) {
		List<CcpJsonRepresentation> resultAsList = this.requestExecutor.getResultAsList(this.elasticQuery, this.resourcesNames, fieldsToSearch);
		return resultAsList;
	}

	/**
	 * See {@link CcpQueryExecutor#getResultAsMap}.
	 * @param field the grouping field
	 * @return the documents grouped by the field
	 */
	public CcpJsonRepresentation getResultAsMap(String field) {
		CcpJsonRepresentation resultAsMap = this.requestExecutor.getResultAsMap(this.elasticQuery, this.resourcesNames, field);
		return resultAsMap;
	}

	/**
	 * See {@link CcpQueryExecutor#getMap}.
	 * @param field the grouping field
	 * @return the documents grouped by the field
	 */
	public CcpJsonRepresentation getMap(String field) {
		CcpJsonRepresentation requestExecutorMap = this.requestExecutor.getMap(this.elasticQuery, this.resourcesNames, field);
		return requestExecutorMap;
	}

	/**
	 * Runs the query and returns only its aggregations.
	 * @return the aggregations block
	 */
	public CcpJsonRepresentation getAggregations() {
		CcpJsonRepresentation aggregations = requestExecutor.getAggregations(this.elasticQuery, this.resourcesNames);
		return aggregations;
	}
}
