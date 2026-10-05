package com.ccp.especifications.db.utils.entity.fields
;

import com.ccp.business.CcpBusiness;

/**
 * Contract of the default transformers of entity fields: a {@code CcpBusiness} that transforms the JSON, plus whether
 * it may be applied to a primary key field and the name of the field it applies to.
 */
public interface CcpJsonTransformersDefaultEntityField extends CcpBusiness{
	/**
	 * Tells whether this transformer may be applied to a primary key field.
	 * @return {@code true} when it can transform a primary key
	 */
	boolean canBePrimaryKey();
	/**
	 * Returns the name of the field this transformer applies to.
	 * @return the field name
	 */
	String name();
}
