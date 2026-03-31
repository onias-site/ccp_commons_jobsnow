package com.ccp.especifications.db.utils.entity.decorators.engine;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.utils.entity.CcpEntity;
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpEntityFieldsValidator;
import com.ccp.json.validations.global.engine.CcpJsonValidatorEngine;

class DecoratorFieldsValidatorEntity extends CcpEntityDelegator{
	
	final Class<?>  clazz;
	
	public DecoratorFieldsValidatorEntity(CcpEntity entity, Class<?> clazz) {
		super(entity);
		CcpEntityFieldsValidator annotation = clazz.getAnnotation(CcpEntityFieldsValidator.class);
		this.clazz = annotation.classReferenceWithTheFields();
	}

	public CcpJsonRepresentation getHandledJson(CcpJsonRepresentation json) {

		CcpEntityFieldsValidator annotation = this.clazz.getAnnotation(CcpEntityFieldsValidator.class);
		Class<?> jsonValidationClass = annotation.classReferenceWithTheFields();
		String featureName = this.clazz.getName();
		CcpJsonValidatorEngine.INSTANCE.validateJson(jsonValidationClass, json, featureName);
		return json;
	}

	public CcpJsonRepresentation save(CcpJsonRepresentation json) {
		CcpJsonRepresentation handledJson = this.getHandledJson(json);
		CcpJsonRepresentation save = this.entity.save(handledJson);
		return save;
	}
}
