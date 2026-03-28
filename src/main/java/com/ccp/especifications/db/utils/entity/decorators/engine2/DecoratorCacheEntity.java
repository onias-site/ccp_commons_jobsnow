package com.ccp.especifications.db.utils.entity.decorators.engine2;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ccp.business.CcpBusiness;
import com.ccp.especifications.db.utils.entity.CcpEntity2;
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpEntityCache;

class DecoratorCacheEntity extends CcpEntityDelegator implements CcpDecoratorEntity<CcpEntityCache>{
	
	final Class<?>  clazz;
	
	public DecoratorCacheEntity(CcpEntity2 entity, Class<?> clazz) {
		super(entity);
		this.clazz = clazz;
	}
	
	public boolean isThisEntityDecorated(Class<CcpEntityCache> annotation) {
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

	public CcpEntityCache getAnnotation() {
		CcpEntityCache annotation = this.clazz.getAnnotation(CcpEntityCache.class);
		return annotation;
	}
	
	
	
}
