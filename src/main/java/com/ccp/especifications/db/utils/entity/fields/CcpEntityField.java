package com.ccp.especifications.db.utils.entity.fields;

import com.ccp.business.CcpBusiness;
import com.ccp.constants.CcpOtherConstants;
import com.ccp.decorators.CcpJsonFieldName;

/**
 * Metadata of an entity field: name, whether it is part of the primary key, whether it is updatable and the value
 * transformer applied before the operations. It implements {@code CcpJsonFieldName}, so it can be used directly to
 * access a {@code CcpJsonRepresentation}.
 */
public class CcpEntityField implements CcpJsonFieldName{

	/** The {@code timestamp} field, updatable, without transformer. */
	public static final CcpEntityField TIMESTAMP = new CcpEntityField("timestamp", false, true, CcpOtherConstants.DO_NOTHING);
	/** The {@code date} field, updatable, without transformer. */
	public static final CcpEntityField DATE = new CcpEntityField("date", false, true,  CcpOtherConstants.DO_NOTHING);
	
	/** The value transformer of the field ({@code CcpOtherConstants.DO_NOTHING} when there is none). */
	public final CcpBusiness transformer;
	/** Whether the field is part of the primary key. */
	public final boolean primaryKey;
	/** Whether the field can be overwritten by an update. */
	public final boolean updatable;
	/** The field name. */
	public final String name;
	
	/**
	 * Builds the field metadata.
	 * @param name the field name
	 * @param primaryKey whether the field is part of the primary key
	 * @param updatable whether the field can be overwritten by an update
	 * @param transformer the value transformer
	 */
	public CcpEntityField(String name, boolean primaryKey, boolean updatable, CcpBusiness transformer) {
		this.transformer = transformer;
		this.primaryKey = primaryKey;
		this.updatable = updatable;
		this.name = name;
	}
	
	/**
	 * Returns the field name.
	 * @return the field name
	 */
	public String name() {
		return this.name;
	}
	
	/**
	 * Returns the field name.
	 * @return the field name
	 */
	public String toString() {
		return this.name;
	}
}
