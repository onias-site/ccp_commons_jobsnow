package com.ccp.especifications.db.utils.entity.decorators.engine2;

public interface CcpDecoratorEntity<CcpAnnotation> {

	boolean isThisEntityDecorated(Class<CcpAnnotation> annotation);
	CcpAnnotation getAnnotation();
}
