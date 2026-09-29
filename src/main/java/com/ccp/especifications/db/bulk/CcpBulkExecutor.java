package com.ccp.especifications.db.bulk;

import java.util.List;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.utils.entity.CcpEntity;

/**
 * Contract for accumulating and executing bulk operations on the database. Allows adding items
 * one by one or in batches, clearing the buffer and triggering the execution, returning the result of
 * each operation.
 */
public interface CcpBulkExecutor {

	/**
	 * Removes every item accumulated in the bulk operation buffer, resetting the executor.
	 *
	 * @return this instance, for chaining
	 */
	CcpBulkExecutor clearRecords();

	/**
	 * Converts the JSON and the entity into {@link CcpBulkItem}s and adds each one to the buffer.
	 *
	 * @param json record data
	 * @param operation bulk operation type
	 * @param entity target entity
	 * @return this instance, for chaining
	 */
	default CcpBulkExecutor addRecord(CcpJsonRepresentation json, CcpBulkEntityOperationType operation, CcpEntity entity) {
		
		List<CcpBulkItem> bulkItems =  entity.toBulkItems(json, operation);
		
		CcpBulkExecutor executor = this;
		
		for (CcpBulkItem bulkItem : bulkItems) {
			executor = this.addRecord(bulkItem);
		}
		
		return executor;
	}
	
	/**
	 * Adds a single, already built {@link CcpBulkItem} to the executor buffer.
	 *
	 * @param bulkItem item to add
	 * @return this instance, for chaining
	 */
	CcpBulkExecutor addRecord(CcpBulkItem bulkItem);

	/**
	 * Adds a list of {@link CcpBulkItem}s to the buffer, iterating and calling {@link #addRecord} for each one.
	 *
	 * @param items list of items to add
	 * @return this instance, for chaining
	 */
	default CcpBulkExecutor addRecords(List<CcpBulkItem> items) {
		CcpBulkExecutor bulk = this;
		for (CcpBulkItem item : items) {
			bulk = bulk.addRecord(item);
		}
		return bulk;
	}
	
	/**
	 * Adds several JSON records to the buffer, converting each one according to the given operation and entity.
	 *
	 * @param records list of JSONs to add
	 * @param operation bulk operation type
	 * @param entity target entity
	 * @return this instance, for chaining
	 */
	default CcpBulkExecutor addRecords(List<CcpJsonRepresentation> records, CcpBulkEntityOperationType operation, CcpEntity entity) {
		CcpBulkExecutor bulk = this;
		for (CcpJsonRepresentation jsonRecord : records) {
			bulk = bulk.addRecord(jsonRecord, operation, entity);
		}
		return bulk;
	}
	
	/**
	 * Executes the accumulated bulk operations and returns the list of results of each processed item.
	 *
	 * @return list of bulk operation results
	 */
	List<CcpBulkOperationResult> getBulkOperationResult();
	
}
