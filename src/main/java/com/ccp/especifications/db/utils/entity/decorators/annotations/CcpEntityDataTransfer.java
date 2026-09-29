package com.ccp.especifications.db.utils.entity.decorators.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.ccp.especifications.db.utils.entity.decorators.enums.CcpEntityDataTransferType;

/**
 * Configures a data transfer between entities ({@code copyDataTo} or {@code transferDataTo}).
 * The combination of execution moment, transfer type and source entity comes encapsulated in
 * a single item of {@code CcpEntityDataTransferType}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface CcpEntityDataTransfer {

	/**
	 * Combination of {@code operationPhase}, {@code transferType} and {@code entityPhase} of this
	 * transfer.
	 */
	CcpEntityDataTransferType operationType();

	/**
	 * Exception handlers specific to this transfer.
	 */
	CcpExceptionFlow[] transferHandlers();

	/**
	 * Business classes to execute during the transfer.
	 */
	@SuppressWarnings("rawtypes")
	Class[] execute();

	/**
	 * Configuration class of the target entity.
	
	 */
	Class<?> targetEntity();

}
