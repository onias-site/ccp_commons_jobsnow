package com.ccp.process;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.business.CcpBusiness;

/**
 * {@code CcpBusiness} that always throws a predefined exception when executed. Useful in conditional flows where a
 * given branch must necessarily fail.
 */
public class CcpFunctionThrowException implements CcpBusiness{

	/** The exception thrown on every execution. */
	private final RuntimeException exception;
	
	/**
	 * Stores the exception that will be thrown.
	 * @param exception the exception thrown when {@code apply} is called
	 */
	public CcpFunctionThrowException(RuntimeException exception) {
		this.exception = exception;
	}

	/**
	 * Throws the stored exception immediately, without processing the JSON.
	 * @param json ignored
	 * @return never returns
	 */
	public CcpJsonRepresentation apply(CcpJsonRepresentation json) {
		throw this.exception;
	}

}
