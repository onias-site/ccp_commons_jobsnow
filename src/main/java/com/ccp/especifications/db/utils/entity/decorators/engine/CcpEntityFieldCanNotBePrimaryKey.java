package com.ccp.especifications.db.utils.entity.decorators.engine;

import com.ccp.especifications.db.utils.entity.fields.CcpJsonTransformersDefaultEntityField;

@SuppressWarnings("serial")
public class CcpEntityFieldCanNotBePrimaryKey extends RuntimeException {

	public CcpEntityFieldCanNotBePrimaryKey(CcpJsonTransformersDefaultEntityField defaultEntityField) {
		super("The field '" + defaultEntityField.name() + "' can not be a primary key");
	}

}
