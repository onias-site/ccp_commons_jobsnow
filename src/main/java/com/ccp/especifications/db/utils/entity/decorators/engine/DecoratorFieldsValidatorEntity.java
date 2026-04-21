package com.ccp.especifications.db.utils.entity.decorators.engine;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.utils.entity.CcpEntity;
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpEntityFieldsValidator;
import com.ccp.json.validations.global.engine.CcpJsonValidatorEngine;

class DecoratorFieldsValidatorEntity extends CcpEntityDelegator{
	
	final Class<?>  clazz;
	
	public DecoratorFieldsValidatorEntity(CcpEntity entity, Class<?> clazz) {
		super(entity);
		this.clazz = clazz;
	}

	public CcpJsonRepresentation validateJson(CcpJsonRepresentation json) {

		CcpEntityFieldsValidator annotation = this.clazz.getAnnotation(CcpEntityFieldsValidator.class);
		Class<?> jsonValidationClass = annotation.classReferenceWithTheFields();
		String featureName = this.clazz.getName();
		CcpJsonValidatorEngine.INSTANCE.validateJson(jsonValidationClass, json, featureName);
		return json;
	}

	public CcpJsonRepresentation save(CcpJsonRepresentation json) {
		this.validateJson(json);
		CcpJsonRepresentation save = this.entity.save(json);
		return save;
	}

	public CcpJsonRepresentation transferDataTo(CcpJsonRepresentation json, CcpEntity entities) {
		this.validateJson(json);
		CcpJsonRepresentation result = this.entity.transferDataTo(json, entities);
		return result;
	}
	
	public CcpJsonRepresentation copyDataTo(CcpJsonRepresentation json, CcpEntity entities) {
		this.validateJson(json);
		CcpJsonRepresentation result = this.entity.copyDataTo(json, entities);
		return result;
	}
}
