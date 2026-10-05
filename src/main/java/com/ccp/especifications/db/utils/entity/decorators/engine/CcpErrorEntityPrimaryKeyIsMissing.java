package com.ccp.especifications.db.utils.entity.decorators.engine;

import java.util.List;
import java.util.Set;
import com.ccp.decorators.CcpCollectionDecorator;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.utils.entity.CcpEntity;

/** Raised when an id is computed from a JSON that does not have every primary key field of the entity. */
@SuppressWarnings("serial")
public class CcpErrorEntityPrimaryKeyIsMissing extends RuntimeException {
	/** The metadata of the entity. */
	public final CcpEntityMetaData entityMetadata;
	/**
	 * Builds the error naming the missing primary key fields.
	 * @param entity the entity
	 * @param json the incomplete JSON
	 */
	CcpErrorEntityPrimaryKeyIsMissing(CcpEntity entity, CcpJsonRepresentation json) {
		super(getMessage(entity, json));
		this.entityMetadata = entity.getEntityMetaData();
	}
	/**
	 * Builds the message naming the JSON, the missing primary key fields and the entity.
	 * @param entity the entity
	 * @param json the incomplete JSON
	 * @return the message
	 */
	private static String getMessage(CcpEntity entity, CcpJsonRepresentation json) {
		CcpEntityMetaData entityDetails = entity.getEntityMetaData();
		List<String> onlyPrimaryKey = entityDetails.primaryKeyNames;
		Set<String> fieldSet = json.fieldSet();
		CcpCollectionDecorator primaryKeyDecorator = new CcpCollectionDecorator(onlyPrimaryKey);
		List<String> primaryKeyMissing = primaryKeyDecorator.getExclusiveList(fieldSet);
		String entityName = entityDetails.entityName;
		String message = String.format("The json %s does not provide the required keys '%s' the entity '%s'", json, primaryKeyMissing, entityName);
		return message;
	}
}
