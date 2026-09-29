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
	 * Runs a terms aggregation and returns statistics of the values of the given field.
	 */
	CcpJsonRepresentation getTermsStatis(CcpQueryOptions elasticQuery, String[] resourcesNames, String fieldName);

	/**
	 * Deletes every document matching the query in the given indexes.
	 */
	CcpJsonRepresentation delete(CcpQueryOptions elasticQuery, String... resourcesNames);
	
	/**
	 * Updates the documents matching the query with the given new values.
	 */
	CcpJsonRepresentation update(CcpQueryOptions elasticQuery, String[] resourcesNames, CcpJsonRepresentation newValues) ;
	
	/**
	 * Iterates over the query results in batches using scroll, passing each batch to the given consumer.
	 */
	CcpQueryExecutor consumeQueryResult(CcpQueryOptions elasticQuery, String[] resourcesNames, String scrollTime, Long size,
			Consumer<List<CcpJsonRepresentation>> consumer, String...fields);

	/**
	 * Returns the total number of documents matching the query.
	 */
	long total(CcpQueryOptions elasticQuery, String[] resourcesNames);

	/**
	 * Returns the query results as a list of JSON documents.
	 */
	List<CcpJsonRepresentation> getResultAsList(CcpQueryOptions elasticQuery, String[] resourcesNames, String... fieldsToSearch);
	
	/**
	 * Returns the results grouped as a map indexed by the given field.
	 */
	CcpJsonRepresentation getResultAsMap(CcpQueryOptions elasticQuery, String[] resourcesNames, String field);

	/**
	 * Runs the query through a custom HTTP request and returns the raw result.
	 */
	CcpJsonRepresentation getResultAsPackage(String url, CcpHttpMethods method, int expectedStatus, CcpQueryOptions elasticQuery, String[] resourcesNames, String ...array);

	/**
	 * Variant of getResultAsMap with slightly different grouping semantics.
	 */
	CcpJsonRepresentation getMap(CcpQueryOptions elasticQuery, String[] resourcesNames, String field);
	
	/**
	 * Runs the query and returns only the aggregations block of the result.
	 */
	CcpJsonRepresentation getAggregations(CcpQueryOptions elasticQuery, String... resourcesNames) ;

	/**
	 * Scroll variant that hands one document at a time to the consumer (instead of lists).
	
	 */
	CcpQueryExecutor consumeQueryResult(CcpQueryOptions elasticQuery, String[] resourcesNames, String scrollTime, Integer size,
			Consumer<CcpJsonRepresentation> consumer, String... fields);
	

}
