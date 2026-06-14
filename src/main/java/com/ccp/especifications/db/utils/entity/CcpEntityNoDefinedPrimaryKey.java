package com.ccp.especifications.db.utils.entity;

@SuppressWarnings("serial")
public class CcpEntityNoDefinedPrimaryKey extends RuntimeException{
	public final CcpEntity entity;

	public CcpEntityNoDefinedPrimaryKey(CcpEntity entity) {
		super("The entity '" + entity.getEntityMetaData().entityName + "' has no defined primary key in his mapping");
		this.entity = entity;
	}
	
}
