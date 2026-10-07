package com.ccp.especifications.db.utils.entity.decorators.engine;

import java.util.List;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.bulk.CcpBulkEntityOperationType;
import com.ccp.especifications.db.bulk.CcpBulkItem;
import com.ccp.especifications.db.utils.entity.CcpEntity;

/**
 * Decorator of the entities marked with {@code @CcpEntityOlyReadable}: {@code save}, {@code delete},
 * {@code deleteAnyWhere} and {@code transferDataTo} do nothing and return {@code false}, silently (no exception), and
 * {@code toBulkItems} refuses every write operation with {@link CcpErrorEntityReadOnly}.
 * <p>
 * A read-only entity is still written, on purpose, by the decorators that own it (the history of the versionable, the
 * records to reprocess, the copies of the disposable): they build their bulk items by hand
 * ({@code new CcpBulkItem(json, operation, ENTITY, id)}) and never go through the entity API. What is refused is a write
 * asked through the API ({@code ENTITY.toBulkItems}, {@code CcpBulkExecutor.addRecord}, or a {@code copyDataTo} whose
 * target is the read-only entity) — that is, a write outside the flow. Until 2026-10-06 {@code toBulkItems} was not
 * blocked, and the read-only protection was only apparent.
 */
class DecoratorReadOnlyEntity extends CcpEntityDelegator {

	/**
	 * Wraps the entity.
	 * @param entity the wrapped entity
	 * @param clazz the configurator class (unused)
	 */
	public DecoratorReadOnlyEntity(CcpEntity entity, Class<?> clazz) {
		super(entity);
	}

	/**
	 * Blocked: does nothing.
	 * @param json the record
	 * @return always {@code false}
	 */
	public boolean delete(CcpJsonRepresentation json) {
		return false;
	}

	/**
	 * Blocked: does nothing.
	 * @param json the record
	 * @return always {@code false}
	 */
	public boolean deleteAnyWhere(CcpJsonRepresentation json) {
		return false;
	}


	/**
	 * Blocked: does nothing.
	 * @param json the record
	 * @return always {@code false}
	 */
	public boolean save(CcpJsonRepresentation json) {
		return false;
	}

	/**
	 * Transferring removes the record from here, so it is a write and stays blocked. Copying does not write to this
	 * entity and remains allowed. Until 2026-09-27 this method received {@code CcpEntity...} and did not override the
	 * interface method, so the block was never applied.
	 * @param json the record
	 * @param entityToTransferData the target entity
	 * @return always {@code false}
	 */
	public boolean transferDataTo(CcpJsonRepresentation json, CcpEntity entityToTransferData) {
		return false;
	}

	/**
	 * Refuses the write operations; {@code noop} (used by the bulk only to read) goes on.
	 * @param json the record
	 * @param operation the bulk operation
	 * @return the items of a {@code noop}
	 * @throws CcpErrorEntityReadOnly for {@code create}, {@code update} and {@code delete}
	 */
	public List<CcpBulkItem> toBulkItems(CcpJsonRepresentation json, CcpBulkEntityOperationType operation) {
		boolean isAWrite = false == CcpBulkEntityOperationType.noop.equals(operation);

		if(isAWrite) {
			CcpErrorEntityReadOnly readOnly = new CcpErrorEntityReadOnly(this, operation);
			throw readOnly;
		}

		List<CcpBulkItem> bulkItems = super.toBulkItems(json, operation);
		return bulkItems;
	}
}
