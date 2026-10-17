package com.ccp.especifications.db.utils.entity.decorators.annotations.transfers;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Holds the {@code @CcpAfterCopyDataFromTwinEntity} repeated on an entity, one per target entity. Written by the compiler when the
 * annotation appears more than once; never written by hand.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface CcpAfterCopyDataFromTwinEntityList {

	/**
	 * The repeated annotations.
	 * @return the repeated annotations
	 */
	CcpAfterCopyDataFromTwinEntity[] value();
}
