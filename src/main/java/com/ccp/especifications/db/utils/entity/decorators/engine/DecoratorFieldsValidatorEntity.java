package com.ccp.especifications.db.utils.entity.decorators.engine;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.utils.entity.CcpEntity;
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpEntityFieldsValidator;
import com.ccp.json.validations.global.engine.CcpJsonValidatorEngine;

/**
 * Decorator that validates the input JSON before the write operations ({@code save},
 * {@code transferDataTo}, {@code copyDataTo}) using the rules defined in
 * {@code @CcpEntityFieldsValidator}. Interrupts the operation if the JSON does not pass the validation.
 */
class DecoratorFieldsValidatorEntity extends CcpEntityDelegator{
	
	/** The configurator class that carries {@code @CcpEntityFieldsValidator}. */
	final Class<?>  clazz;
	
	/**
	 * Wraps the entity.
	 * @param entity the wrapped entity
	 * @param clazz the configurator class
	 */
	public DecoratorFieldsValidatorEntity(CcpEntity entity, Class<?> clazz) {
		super(entity);
		this.clazz = clazz;
	}

	/**
	 * Validates the JSON against the class named by {@code @CcpEntityFieldsValidator}.
	 * @param json the record
	 * @return the same JSON
	 * @throws com.ccp.json.validations.global.engine.CcpJsonValidationError when a rule is broken
	 */
	public CcpJsonRepresentation validateJson(CcpJsonRepresentation json) {

		CcpEntityFieldsValidator annotation = this.clazz.getAnnotation(CcpEntityFieldsValidator.class);
		Class<?> jsonValidationClass = annotation.classReferenceWithTheFields();
		String featureName = this.clazz.getName();
		CcpJsonValidatorEngine.INSTANCE.validateJson(jsonValidationClass, json, featureName);
		return json;
	}

	/**
	 * Validates the record and then saves it.
	 * @param json the record
	 * @return the outcome of the wrapped save
	 */
	public boolean save(CcpJsonRepresentation json) {
		this.validateJson(json);
		boolean inserted = this.entity.save(json);
		return inserted;
	}

	/**
	 * Validates the record and then transfers it.
	 * @param json the record
	 * @param entities the target entity
	 * @return the outcome of the wrapped transfer
	 */
	public boolean transferDataTo(CcpJsonRepresentation json, CcpEntity entities) {
		this.validateJson(json);
		boolean result = this.entity.transferDataTo(json, entities);
		return result;
	}

	/**
	 * Validates the record and then copies it.
	 * @param json the record
	 * @param entities the target entity
	 * @return the outcome of the wrapped copy
	 */
	public boolean copyDataTo(CcpJsonRepresentation json, CcpEntity entities) {
		this.validateJson(json);
		boolean result = this.entity.copyDataTo(json, entities);
		return result;
	}
}
