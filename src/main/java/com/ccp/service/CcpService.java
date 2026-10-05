package com.ccp.service;

import java.util.Map;

import com.ccp.decorators.CcpErrorJsonInvalid;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.business.CcpBusiness;
import com.ccp.json.validations.global.engine.CcpJsonValidatorEngine;

/**
 * Specialization of {@code CcpBusiness} for services exposed to the outside (e.g. REST endpoints). It makes the name
 * and the validation class mandatory and converts {@code Map<String, Object>} to {@code CcpJsonRepresentation}.
 */
public interface CcpService extends CcpBusiness {

	/**
	 * Returns the class holding the validation annotations of the input JSON of this service.
	 * @return the validation class
	 */
	Class<?> getJsonValidationClass();

	/**
	 * Returns the identifier of the service, used in logs and validation messages.
	 * @return the service name
	 */
	String name();
	
	/**
	 * Converts the map into {@code CcpJsonRepresentation}, validates it with {@code CcpJsonValidatorEngine}, runs
	 * {@code apply()} and returns the output map.
	 * <p>
	 * Unlike {@link CcpBusiness#execute(CcpJsonRepresentation)}, it does not fill default values.
	 * @param map the input map
	 * @return the output map
	 * @throws CcpServiceJsonValidationError when a JSON built during the processing is invalid
	 */
	default Map<String, Object> execute(Map<String, Object> map){
		CcpJsonRepresentation json = new CcpJsonRepresentation(map);
		Class<?> jsonValidationClass = this.getJsonValidationClass();
		String name = this.name();
		CcpJsonValidatorEngine.INSTANCE.validateJson(jsonValidationClass, json, name);
		try {
			CcpJsonRepresentation apply = this.apply(json);
			return apply.content;
		} catch (CcpErrorJsonInvalid e) {
			CcpServiceJsonValidationError ccpServiceJsonValidationError = new CcpServiceJsonValidationError(e);
			throw ccpServiceJsonValidationError;
		}
	}

	/** Wraps a JSON validation error raised during the execution of a service (not during the validation of its input). */
	@SuppressWarnings("serial")
	public static class CcpServiceJsonValidationError extends RuntimeException {
		/**
		 * Wraps the original validation error.
		 * @param e the original validation error
		 */
		private CcpServiceJsonValidationError(CcpErrorJsonInvalid e) {
			super(e);
		}
	}
}
