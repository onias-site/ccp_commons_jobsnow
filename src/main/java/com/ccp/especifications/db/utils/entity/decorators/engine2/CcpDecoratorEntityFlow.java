package com.ccp.especifications.db.utils.entity.decorators.engine2;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ccp.business.CcpBusiness;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpReflectionConstructorDecorator;

public interface CcpDecoratorEntityFlow<CcpAnnotation> extends CcpDecoratorEntity<CcpAnnotation>{

	Map<Class<?>, List<CcpBusiness>> getExceptionHandlers();
	List<CcpBusiness> getFlow();
	
	default CcpBusiness getBusiness(Class<?> clazz) {
		CcpReflectionConstructorDecorator ccpReflectionConstructorDecorator = new CcpReflectionConstructorDecorator(clazz);
		CcpBusiness newInstance = ccpReflectionConstructorDecorator.newInstance();
		return newInstance;
	}
	
	default List<CcpBusiness> getAllBusiness(Class<?>[] thenExecute){
		List<CcpBusiness> collect = Arrays.asList(thenExecute).stream().map(clazz -> this.getBusiness(clazz)).collect(Collectors.toList());
		return collect;
	}
	
	default CcpJsonRepresentation executeBusiness(CcpJsonRepresentation json, CcpBusiness business) {
		try {
			CcpJsonRepresentation apply = business.apply(json);
			return apply;
		} catch (RuntimeException e) {
			Map<Class<?>, List<CcpBusiness>> exceptionHandlers = this.getExceptionHandlers();
			Class<? extends RuntimeException> clazz = e.getClass();
			List<CcpBusiness> list = exceptionHandlers.get(clazz);
			
			boolean notForeseen = list == null;
			
			if(notForeseen) {
				throw e;
			}
			
			for (CcpBusiness ccpBusiness : list) {
				json = this.executeBusiness(json, ccpBusiness);
			}
			return json;
		}
	}
	
	default CcpJsonRepresentation executeFlow(CcpJsonRepresentation json) {
		List<CcpBusiness> flow = this.getFlow();
		
		for (CcpBusiness business : flow) {
			json = this.executeBusiness(json, business);
		}
		
		return json;
	}


}
