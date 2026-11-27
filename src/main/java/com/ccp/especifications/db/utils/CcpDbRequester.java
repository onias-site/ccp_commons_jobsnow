package com.ccp.especifications.db.utils;

import java.util.List;
import java.util.function.Consumer;

import com.ccp.decorators.CcpJsonFieldName;
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

	/** The fields of the answer of {@link #deleteTheBackupsBeforeTheLatest(String)}. */
	public static enum BackupFields implements CcpJsonFieldName {
		/** The name of the most recent backup that went well, the only one kept; absent when there is none. */
		latestBackup,
		/** The names of the backups deleted. */
		deletedBackups,
	}

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
	 * Creates the indexes/tables in the database based on the given scripts and classes, recording the mapping
	 * errors and the results of the seed records in the given destinations.
	 */
	CcpDbRequester createTables(String pathToCreateEntityScript, String pathToJavaClasses, String mappingJnEntitiesErrors,
			String insertResults);

	/**
	 * Orders the database to back up the given entities into a bucket of files. The database writes the backup itself,
	 * straight to the bucket, so no byte of it goes through this process (in Elasticsearch, a snapshot in a repository
	 * that points to the bucket: a folder of {@code path.repo} on a local machine, a GCS bucket in the cloud).
	 * @param bucketName the bucket that receives the backup
	 * @param entityNamePatterns the names of the entities in the backup, {@code *} standing for any text (for example
	 * {@code jn_*})
	 * @return the details of the backup: its name, the entities in it and how it went
	 */
	CcpJsonRepresentation backup(String bucketName, List<String> entityNamePatterns);

	/**
	 * Deletes from the bucket every backup but the most recent one that went well (since 2026-11-27, so that the bucket
	 * keeps a single backup). Without any backup that went well nothing is deleted, and a backup still running is never
	 * deleted.
	 * @param bucketName the bucket of the backups
	 * @return {@code latestBackup}, the backup kept (absent when there is none), and {@code deletedBackups}, the names of
	 * the backups deleted
	 */
	CcpJsonRepresentation deleteTheBackupsBeforeTheLatest(String bucketName);

	/**
	 * Makes what was written to the entity visible to the searches right away, for a search that must see a record
	 * written moments ago. Reads by id do not need it.
	 * @param entityName the name of the entity
	 */
	void refresh(String entityName);
}
