package com.ccp.business;

import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonFieldName;
import com.ccp.json.defaultvalues.engine.CcpJsonFieldDefaultValuesEngine;
import com.ccp.json.validations.global.engine.CcpJsonValidatorEngine;

/**
 * Central contract of every business rule of the system. It extends
 * {@code Function<CcpJsonRepresentation, CcpJsonRepresentation>}, so every business operation receives and returns
 * the JSON map that flows through the system, and adds input validation, default values and asynchronous execution
 * control.
 */
public interface CcpBusiness extends Function<CcpJsonRepresentation, CcpJsonRepresentation>, CcpJsonFieldName{

	/**
	 * Tells whether this operation may be persisted as an asynchronous task.
	 * @return {@code true} by default; implementations override it to forbid asynchronous execution
	 */
	default boolean canBeSavedAsAsyncTask() {
		return true;
	}
	
	/**
	 * Returns the class whose annotations describe the validation rules and default values of the input JSON.
	 * @return the implementation class itself by default, so annotations placed on it are found by reflection
	 */
	default Class<?> getJsonValidationClass(){
		Class<? extends CcpBusiness> class1 = this.getClass();
		return class1;
	}
	
	/**
	 * Safe entry point of the business rule: validates the input JSON with {@code CcpJsonValidatorEngine}
	 * (raising the validation error when a rule is broken), fills the absent fields that declare a default value with
	 * {@code CcpJsonFieldDefaultValuesEngine} and only then calls {@link #apply(Object) apply}.
	 * <p>
	 * Prefer this method over calling {@code apply} directly whenever the input comes from outside.
	 * @param json the input JSON
	 * @return the JSON produced by {@code apply}
	 */
	default CcpJsonRepresentation execute(CcpJsonRepresentation json) {
		var clazz = this.getClass();
		String className = clazz.getName(); 
		Class<?> jsonValidationClass = this.getJsonValidationClass();
		CcpJsonValidatorEngine.INSTANCE.validateJson(jsonValidationClass, json, className);

		CcpJsonRepresentation jsonWithDefaultValues = CcpJsonFieldDefaultValuesEngine.INSTANCE.putDefaultValues(jsonValidationClass, json);

		CcpJsonRepresentation apply = this.apply(jsonWithDefaultValues);
		return apply;
	}  
	/**
	 * Returns the identifier of this business rule, used in logs, validation messages and asynchronous tasks.
	 * @return the fully qualified name of the implementation class
	 */
	default String name() {
		var clazz2 = this.getClass();
		var clazz2Name = clazz2.getName();
		return clazz2Name;
	}
	
}
