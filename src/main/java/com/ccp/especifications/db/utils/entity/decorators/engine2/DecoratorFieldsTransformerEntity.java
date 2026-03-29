package com.ccp.especifications.db.utils.entity.decorators.engine2;

import com.ccp.especifications.db.utils.entity.CcpEntity2;
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpEntityFieldsTransformer;

class DecoratorFieldsTransformerEntity extends CcpEntityDelegator implements CcpDecoratorEntity<CcpEntityFieldsTransformer>{
	
	final Class<?>  clazz;
	
	public DecoratorFieldsTransformerEntity(CcpEntity2 entity, Class<?> clazz) {
		super(entity, 8);
		this.clazz = clazz;
	}
	
	public boolean isThisEntityDecorated(Class<CcpEntityFieldsTransformer> annotation) {
		boolean annotationPresent = this.clazz.isAnnotationPresent(annotation);
		return annotationPresent;
	}

	public CcpEntityFieldsTransformer getAnnotation() {
		CcpEntityFieldsTransformer annotation = this.clazz.getAnnotation(CcpEntityFieldsTransformer.class);
		return annotation;
	}
}
