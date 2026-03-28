package com.ccp.especifications.db.utils.entity.decorators.engine2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ccp.business.CcpBusiness;
import com.ccp.especifications.db.utils.entity.CcpEntity2;
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpEntityFieldsValidator;

class DecoratorFieldsValidatorEntity extends CcpEntityDelegator implements CcpDecoratorEntity<CcpEntityFieldsValidator>{
	
	final Class<?>  clazz;
	
	public DecoratorFieldsValidatorEntity(CcpEntity2 entity, Class<?> clazz) {
		super(entity);
		this.clazz = clazz;
	}
	
	public boolean isThisEntityDecorated(Class<CcpEntityFieldsValidator> annotation) {
		boolean annotationPresent = this.clazz.isAnnotationPresent(annotation);
		return annotationPresent;
	}

	public List<CcpBusiness> getFlow() {

		return new ArrayList<>();
	}

	public Map<Class<?>, List<CcpBusiness>> getExceptionHandlers() {

		return new HashMap<>();
	}

	public CcpEntityFieldsValidator getAnnotation() {
		CcpEntityFieldsValidator annotation = this.clazz.getAnnotation(CcpEntityFieldsValidator.class);
		return annotation;
	}
	
	
	
}
