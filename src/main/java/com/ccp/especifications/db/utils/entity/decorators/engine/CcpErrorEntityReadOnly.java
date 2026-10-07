package com.ccp.especifications.db.utils.entity.decorators.engine;

import com.ccp.especifications.db.bulk.CcpBulkEntityOperationType;
import com.ccp.especifications.db.utils.entity.CcpEntity;

/**
 * Raised when a write to a read-only entity ({@code @CcpEntityOlyReadable}) is asked through the entity API, outside
 * the flow of the decorators that own it. Those decorators build their bulk items by hand ({@code new CcpBulkItem}) and
 * are never refused.
 */
@SuppressWarnings("serial")
public class CcpErrorEntityReadOnly extends RuntimeException {
	/** The metadata of the entity. */
	public final CcpEntityMetaData entityMetadata;

	/**
	 * Builds the error naming the entity and the refused operation.
	 * @param entity the read-only entity
	 * @param operation the refused operation
	 */
	CcpErrorEntityReadOnly(CcpEntity entity, CcpBulkEntityOperationType operation) {
		super(getMessage(entity, operation));
		this.entityMetadata = entity.getEntityMetaData();
	}

	/**
	 * Builds the message naming the entity and the refused operation.
	 * @param entity the read-only entity
	 * @param operation the refused operation
	 * @return the message
	 */
	private static String getMessage(CcpEntity entity, CcpBulkEntityOperationType operation) {
		CcpEntityMetaData entityDetails = entity.getEntityMetaData();
		String message = String.format("The entity '%s' is read only: the bulk operation '%s' can not be asked through its API", entityDetails.entityName, operation);
		return message;
	}
}
