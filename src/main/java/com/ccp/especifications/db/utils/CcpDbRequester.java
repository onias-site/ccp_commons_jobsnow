package com.ccp.especifications.db.utils;

import java.util.List;
import java.util.function.Consumer;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.bulk.CcpBulkOperationResult;
import com.ccp.especifications.http.CcpHttpMethods;
import com.ccp.especifications.http.CcpHttpResponseTransform;
import com.ccp.especifications.db.utils.entity.fields.CcpErrorDbUtilsIncorrectEntityFields;

/**
 * Low-level contract for executing requests against the database (Elasticsearch). Abstracts the
 * HTTP transport and the connection details, providing methods to execute requests with different
 * signatures, plus configuration utilities.
 */
public interface CcpDbRequester {

	/**
	 * Executes an HTTP request with a JSON body, resources (indexes) and expected status, and transforms the response.
	 */
	<V> V executeHttpRequest(String trace, String url, CcpHttpMethods method, Integer expectedStatus, CcpJsonRepresentation body, String[] resources, CcpHttpResponseTransform<V> transformer);

	/**
	 * Variant that accepts the body as a string and explicit headers.
	 */
	<V> V executeHttpRequest(String trace, String url, CcpHttpMethods method, Integer expectedStatus, String body, CcpJsonRepresentation headers, CcpHttpResponseTransform<V> transformer);

	/**
	 * Variant that accepts a JSON of error-handling flows instead of a fixed expected status.
	 */
	<V> V executeHttpRequest(String trace, String url, CcpHttpMethods method, CcpJsonRepresentation flows, CcpJsonRepresentation body, CcpHttpResponseTransform<V> transformer);

	/**
	 * Variant without a list of resources.
	 */
	<V> V executeHttpRequest(String trace, String url, CcpHttpMethods method, Integer expectedStatus, CcpJsonRepresentation body, CcpHttpResponseTransform<V> transformer);

	/**
	 * Runs the initial database setup (creation of indexes/mappings), reporting mapping errors
	 * and general errors through consumers.
	 */
	List<CcpBulkOperationResult> executeDatabaseSetup(String pathToJavaClasses, String hostFolder, String pathToCreateEntityScript, Consumer<CcpErrorDbUtilsIncorrectEntityFields> whenIsIncorrectMapping, Consumer<Throwable> whenOccursAnError);

	/**
	 * Returns the database connection details (host, port, credentials, etc.).
	 */
	CcpJsonRepresentation getConnectionDetails();

	/**
	 * Returns the name of the field used to identify the index/entity in multi-get requests.
	 */
	String getFieldNameToEntity();

	/**
	 * Returns the name of the field used to identify the document ID in multi-get requests.
	 */
	String getFieldNameToId();

	/**
	 * Creates the indexes/tables in the database based on the given scripts and classes, recording errors
	 * in the given destinations.
	 */
	CcpDbRequester createTables(String pathToCreateEntityScript, String pathToJavaClasses, String mappingJnEntitiesErrors,
			String insertErrors);
}
