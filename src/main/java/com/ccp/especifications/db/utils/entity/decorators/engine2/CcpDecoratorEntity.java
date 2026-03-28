package com.ccp.especifications.db.utils.entity.decorators.engine2;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ccp.business.CcpBusiness;
import com.ccp.decorators.CcpReflectionConstructorDecorator;

public interface CcpDecoratorEntity<CcpAnnotation> {

	Map<Class<?>, List<CcpBusiness>> getExceptionHandlers();
	boolean isThisEntityDecorated(Class<CcpAnnotation> annotation);
	List<CcpBusiness> getFlow();
	CcpAnnotation getAnnotation();
	
	default CcpBusiness getBusiness(Class<?> clazz) {
		CcpReflectionConstructorDecorator ccpReflectionConstructorDecorator = new CcpReflectionConstructorDecorator(clazz);
		CcpBusiness newInstance = ccpReflectionConstructorDecorator.newInstance();
		return newInstance;
	}
	
	default List<CcpBusiness> getAllBusiness(Class<?>[] thenExecute){
		List<CcpBusiness> collect = Arrays.asList(thenExecute).stream().map(clazz -> this.getBusiness(clazz)).collect(Collectors.toList());
		return collect;
	}
}
