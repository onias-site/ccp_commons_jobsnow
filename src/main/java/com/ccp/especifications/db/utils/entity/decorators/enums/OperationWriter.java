package com.ccp.especifications.db.utils.entity.decorators.enums;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.ccp.business.CcpBusiness;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpReflectionConstructorDecorator;
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpExceptionFlow;

interface OperationWriter {
	
	default CcpJsonRepresentation executeBusiness(CcpJsonRepresentation json, CcpBusiness business, Map<Class<?>, List<CcpBusiness>> exceptionHandlers) {
		try {
			CcpJsonRepresentation apply = business.apply(json);
			return apply;
		} catch (RuntimeException e) {
			Class<? extends RuntimeException> clazz = e.getClass();
			List<CcpBusiness> list = exceptionHandlers.get(clazz);
			
			boolean notForeseen = list == null;
			
			if(notForeseen) {
				throw e;
			}
			
			for (CcpBusiness ccpBusiness : list) {
				json = this.executeBusiness(json, ccpBusiness, exceptionHandlers);
			}
			return json;
		}
	}

	default Map<Class<?>, List<CcpBusiness>> getExceptionHandlers(CcpExceptionFlow[] flows){
		
		Map<Class<?>, List<CcpBusiness>> result = new HashMap<>();
		
		for (CcpExceptionFlow flow : flows) {
			Class<?> whenThrowing = flow.whenThrowing();
			Class<?>[] thenExecute = flow.thenExecute();
			var asList = Arrays.asList(thenExecute)
					.stream()
					.map(x -> this.getBusiness(x))
					.collect(Collectors.toList())
					;
			
			result.put(whenThrowing, asList);
		}
		return result;
	}
	
	default CcpBusiness getBusiness(Class<?> clazz) {
		CcpReflectionConstructorDecorator ccpReflectionConstructorDecorator = new CcpReflectionConstructorDecorator(clazz);
		CcpBusiness newInstance = ccpReflectionConstructorDecorator.newInstance();
		return newInstance;
	}


}
