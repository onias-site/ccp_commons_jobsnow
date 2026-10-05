package com.ccp.especifications.db.query;

import java.util.List;
import java.util.function.Consumer;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.http.CcpHttpMethods;
/**
 * Contract for executing queries on Elasticsearch.
 * Defines every query operation that can be performed based on a CcpQueryOptions (the built query) and the names of the target indexes.
 */
public interface CcpQueryExecutor {

	/**
	 * Runs a terms aggregation over the field and returns the statistics of its values.
	 * @param elasticQuery the query
	 * @param resourcesNames the indexes
	 * @param fieldName the field
	 * @return the statistics
	 */
	CcpJsonRepresentation getTermsStatis(CcpQueryOptions elasticQuery, String[] resourcesNames, String fieldName);

	/**
	 * Deletes every document matching the query in the indexes.
	 * @param elasticQuery the query
	 * @param resourcesNames the indexes
	 * @return the response of the database
	 */
	CcpJsonRepresentation delete(CcpQueryOptions elasticQuery, String... resourcesNames);
	
	/**
	 * Updates the documents matching the query with the new values.
	 * @param elasticQuery the query
	 * @param resourcesNames the indexes
	 * @param newValues the new values
	 * @return the response of the database
	 */
	CcpJsonRepresentation update(CcpQueryOptions elasticQuery, String[] resourcesNames, CcpJsonRepresentation newValues) ;
	
	/**
	 * Iterates over the results in batches with scroll, handing each batch to the consumer.
	 * @param elasticQuery the query
	 * @param resourcesNames the indexes
	 * @param scrollTime the expiration of the scroll context
	 * @param size the batch size
	 * @param consumer receives each batch
	 * @param fields the fields returned
	 * @return this executor
	 */
	CcpQueryExecutor consumeQueryResult(CcpQueryOptions elasticQuery, String[] resourcesNames, String scrollTime, Long size,
			Consumer<List<CcpJsonRepresentation>> consumer, String...fields);

	/**
	 * Counts the documents matching the query.
	 * @param elasticQuery the query
	 * @param resourcesNames the indexes
	 * @return the total
	 */
	long total(CcpQueryOptions elasticQuery, String[] resourcesNames);

	/**
	 * Returns the documents matching the query.
	 * @param elasticQuery the query
	 * @param resourcesNames the indexes
	 * @param fieldsToSearch the fields returned
	 * @return the documents
	 */
	List<CcpJsonRepresentation> getResultAsList(CcpQueryOptions elasticQuery, String[] resourcesNames, String... fieldsToSearch);
	
	/**
	 * Returns the documents grouped by the value of the field.
	 * @param elasticQuery the query
	 * @param resourcesNames the indexes
	 * @param field the grouping field
	 * @return the documents grouped by the field
	 */
	CcpJsonRepresentation getResultAsMap(CcpQueryOptions elasticQuery, String[] resourcesNames, String field);

	/**
	 * Runs the query through a custom HTTP request and returns the raw result.
	 * @param url the request path
	 * @param method the HTTP method
	 * @param expectedStatus the expected HTTP status
	 * @param elasticQuery the query
	 * @param resourcesNames the indexes
	 * @param array the fields returned
	 * @return the raw result
	 */
	CcpJsonRepresentation getResultAsPackage(String url, CcpHttpMethods method, int expectedStatus, CcpQueryOptions elasticQuery, String[] resourcesNames, String ...array);

	/**
	 * Variant of {@link #getResultAsMap} with a different grouping semantics.
	 * @param elasticQuery the query
	 * @param resourcesNames the indexes
	 * @param field the grouping field
	 * @return the documents grouped by the field
	 */
	CcpJsonRepresentation getMap(CcpQueryOptions elasticQuery, String[] resourcesNames, String field);
	
	/**
	 * Runs the query and returns only its aggregations block.
	 * @param elasticQuery the query
	 * @param resourcesNames the indexes
	 * @return the aggregations
	 */
	CcpJsonRepresentation getAggregations(CcpQueryOptions elasticQuery, String... resourcesNames) ;

	/**
	 * Iterates over the results with scroll, handing one document at a time to the consumer.
	 * @param elasticQuery the query
	 * @param resourcesNames the indexes
	 * @param scrollTime the expiration of the scroll context
	 * @param size the batch size
	 * @param consumer receives each document
	 * @param fields the fields returned
	 * @return this executor
	 */
	CcpQueryExecutor consumeQueryResult(CcpQueryOptions elasticQuery, String[] resourcesNames, String scrollTime, Integer size,
			Consumer<CcpJsonRepresentation> consumer, String... fields);
	

}
