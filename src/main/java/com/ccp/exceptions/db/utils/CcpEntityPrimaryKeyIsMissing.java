package com.ccp.exceptions.db.utils;

import java.util.List;
import java.util.Set;

import com.ccp.decorators.CcpCollectionDecorator;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.utils.CcpEntity;

@SuppressWarnings("serial")
public class CcpEntityPrimaryKeyIsMissing extends RuntimeException{

	public CcpEntityPrimaryKeyIsMissing(CcpEntity entity, CcpJsonRepresentation json) {
		super(getMessage(entity, json));
	}
	
	private static String getMessage(CcpEntity entity, CcpJsonRepresentation json) {
		List<String> onlyPrimaryKey = entity.getPrimaryKeyNames();

		Set<String> fieldSet = json.fieldSet();
		CcpCollectionDecorator ccd = new CcpCollectionDecorator(onlyPrimaryKey);
		List<String> primaryKeyMissing = ccd.getExclusiveList(fieldSet);
		String entityName = entity.getEntityName();
		
		String message = String.format("The json %s does not provide the required keys '%s' the entity '%s'", json, primaryKeyMissing, entityName);
		
		return message;

	}
}
