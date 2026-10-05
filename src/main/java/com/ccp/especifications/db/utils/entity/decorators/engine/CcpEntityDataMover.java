package com.ccp.especifications.db.utils.entity.decorators.engine;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.db.bulk.CcpBulkEntityOperationType;
import com.ccp.especifications.db.bulk.CcpBulkExecutor;
import com.ccp.especifications.db.bulk.CcpBulkItem;
import com.ccp.especifications.db.bulk.CcpBulkOperationResult;
import com.ccp.especifications.db.bulk.CcpExecuteBulkOperation;
import com.ccp.especifications.db.utils.entity.CcpEntity;

/**
 * Single implementation of {@code transferDataTo} and {@code copyDataTo}, used both by
 * {@code CcpDefaultEntityDelegator} and by the innermost layer, {@code DefaultImplementationEntity}.
 * Without it in the inner layer, only entities with a decorator that extends the default delegator (twin,
 * versionable, disposable) transferred data; the others returned {@code false} without doing anything.
 *
 * <p>Rules: a missing source is not an error — nothing happens and the return value is {@code false}. What goes to the
 * target is the record stored in the source, with the fields of the received json on top of it. Removal from the source
 * (only in the transfer) and creation in the target go in a single bulk.
 */
final class CcpEntityDataMover {

	/** Utility class: not instantiable. */
	private CcpEntityDataMover() {}

	/**
	 * Moves the record from the source to the target (see the class rules).
	 * @param source the source entity
	 * @param target the target entity
	 * @param json the record (at least its primary key) plus fields that override the stored ones
	 * @param bulk runs the bulk
	 * @param functionToDeleteKeysInTheCache receives the cache keys to invalidate
	 * @return {@code false} when the record does not exist in the source, {@code true} otherwise
	 */
	static boolean transfer(CcpEntity source, CcpEntity target, CcpJsonRepresentation json, CcpExecuteBulkOperation bulk, Consumer<String[]> functionToDeleteKeysInTheCache) {
		boolean moved = move(source, target, json, bulk, functionToDeleteKeysInTheCache, true);
		return moved;
	}

	/**
	 * Copies the record from the source to the target, keeping it in the source (see the class rules).
	 * @param source the source entity
	 * @param target the target entity
	 * @param json the record (at least its primary key) plus fields that override the stored ones
	 * @param bulk runs the bulk
	 * @param functionToDeleteKeysInTheCache receives the cache keys to invalidate
	 * @return {@code false} when the record does not exist in the source, {@code true} otherwise
	 */
	static boolean copy(CcpEntity source, CcpEntity target, CcpJsonRepresentation json, CcpExecuteBulkOperation bulk, Consumer<String[]> functionToDeleteKeysInTheCache) {
		boolean copied = move(source, target, json, bulk, functionToDeleteKeysInTheCache, false);
		return copied;
	}

	/**
	 * Shared implementation of transfer and copy.
	 * @param source the source entity
	 * @param target the target entity
	 * @param json the record plus overriding fields
	 * @param bulk runs the bulk
	 * @param functionToDeleteKeysInTheCache receives the cache keys to invalidate
	 * @param removeFromSource whether the record is deleted from the source
	 * @return {@code false} when the record does not exist in the source, {@code true} otherwise
	 */
	private static boolean move(CcpEntity source, CcpEntity target, CcpJsonRepresentation json, CcpExecuteBulkOperation bulk, Consumer<String[]> functionToDeleteKeysInTheCache, boolean removeFromSource) {

		boolean sourceIsMissing = false == source.exists(json);

		if(sourceIsMissing) {
			return false;
		}

		CcpJsonRepresentation storedRecord = source.getOneById(json);
		CcpJsonRepresentation dataToMove = storedRecord.mergeWithAnotherJson(json);

		List<CcpBulkItem> items = new ArrayList<>();

		if(removeFromSource) {
			List<CcpBulkItem> toRemove = source.toBulkItems(dataToMove, CcpBulkEntityOperationType.delete);
			items.addAll(toRemove);
		}

		List<CcpBulkItem> toCreate = target.toBulkItems(dataToMove, CcpBulkEntityOperationType.create);
		items.addAll(toCreate);

		bulk.executeBulk(items, functionToDeleteKeysInTheCache);
		return true;
	}

	/**
	 * Bulk executor of the innermost layer, which has no executor configured through an annotation. A failure in
	 * any item throws an exception: a half-done transfer cannot go unnoticed.
	 */
	static final CcpExecuteBulkOperation DIRECT_BULK = new CcpExecuteBulkOperation() {

		public CcpExecuteBulkOperation executeBulk(Collection<CcpBulkItem> items, Consumer<String[]> functionToDeleteKeysInTheCache) {

			boolean nothingToDo = items.isEmpty();

			if(nothingToDo) {
				return this;
			}

			CcpBulkExecutor executor = CcpDependencyInjection.getDependency(CcpBulkExecutor.class);
			List<CcpBulkItem> itemsList = new ArrayList<>(items);
			CcpBulkExecutor withTheItems = executor.addRecords(itemsList);
			List<CcpBulkOperationResult> results = withTheItems.getBulkOperationResult();

			List<CcpJsonRepresentation> errors = results.stream()
					.filter(x -> x.hasError())
					.map(x -> x.getErrorDetails())
					.collect(Collectors.toList());

			boolean hasErrors = false == errors.isEmpty();

			if(hasErrors) {
				throw new CcpErrorEntityDataMoveFailed(errors);
			}

			return this;
		}
	};

	/** Cache invalidation of the innermost layer, which has no cache: does nothing. */
	static final Consumer<String[]> NO_CACHE_TO_CLEAN = keys -> {};

	/** Raised by {@link #DIRECT_BULK} when any item of the bulk fails. */
	@SuppressWarnings("serial")
	public static class CcpErrorEntityDataMoveFailed extends RuntimeException {
		/**
		 * Builds the error listing the details of the failed items.
		 * @param errors the error details of the failed items
		 */
		private CcpErrorEntityDataMoveFailed(List<CcpJsonRepresentation> errors) {
			super("The data transfer failed in the bulk: " + errors);
		}
	}
}
