package com.ccp.especifications.db.utils.entity.decorators.engine2;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.utils.entity.CcpEntity2;
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpEntityOlyReadable;

class DecoratorReadOnlyEntity extends CcpEntityDelegator implements CcpDecoratorEntity<CcpEntityOlyReadable>{
	
	final Class<?>  clazz;
	
	public DecoratorReadOnlyEntity(CcpEntity2 entity, Class<?> clazz) {
		super(entity, 7);
		this.clazz = clazz;
	}
	
	public boolean isThisEntityDecorated(Class<CcpEntityOlyReadable> annotation) {
		boolean annotationPresent = this.clazz.isAnnotationPresent(annotation);
		return annotationPresent;
	}

	public CcpEntityOlyReadable getAnnotation() {
		CcpEntityOlyReadable annotation = this.clazz.getAnnotation(CcpEntityOlyReadable.class);
		return annotation;
	}
	
	
	public CcpJsonRepresentation delete(CcpJsonRepresentation json) {
		CcpJsonRepresentation throwException = this.throwException();
		return throwException;
	}
	
	public CcpJsonRepresentation deleteAnyWhere(CcpJsonRepresentation json) {
		CcpJsonRepresentation throwException = this.throwException();
		return throwException;
	}
	

	public CcpJsonRepresentation save(CcpJsonRepresentation json) {
		CcpJsonRepresentation throwException = this.throwException();
		return throwException;
	}

}
