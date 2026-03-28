package com.ccp.especifications.db.utils.entity.decorators.engine2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ccp.business.CcpBusiness;
import com.ccp.especifications.db.utils.entity.CcpEntity2;
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpEntityOlyReadable;

class DecoratorReadOnlyEntity extends CcpEntityDelegator implements CcpDecoratorEntity<CcpEntityOlyReadable>{
	
	final Class<?>  clazz;
	
	public DecoratorReadOnlyEntity(CcpEntity2 entity, Class<?> clazz) {
		super(entity);
		this.clazz = clazz;
	}
	
	public boolean isThisEntityDecorated(Class<CcpEntityOlyReadable> annotation) {
		boolean annotationPresent = this.clazz.isAnnotationPresent(annotation);
		return annotationPresent;
	}

	public List<CcpBusiness> getFlow() {
		return new ArrayList<>();
	}

	public Map<Class<?>, List<CcpBusiness>> getExceptionHandlers() {
		Map<Class<?>, List<CcpBusiness>> result = new HashMap<>();
		return result;
	}

	public CcpEntityOlyReadable getAnnotation() {
		CcpEntityOlyReadable annotation = this.clazz.getAnnotation(CcpEntityOlyReadable.class);
		return annotation;
	}
	
	
	
}
