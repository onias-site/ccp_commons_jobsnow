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

/**
 * Helper shared by {@code CcpEntityDecoratorOperationType} and {@code CcpEntityDecoratorTransferType}: runs businesses
 * with the exception handling configured through {@code @CcpExceptionFlow}.
 */
interface OperationWriter {

	/**
	 * Runs the business; when it throws an exception whose exact class has handlers, runs the handlers in sequence
	 * instead. Exceptions without handlers (including subclasses of a handled class) are rethrown.
	 * @param json the input JSON
	 * @param business the business
	 * @param exceptionHandlers handlers by exception class
	 * @return the output of the business or of the last handler
	 */
	default CcpJsonRepresentation executeBusiness(CcpJsonRepresentation json, CcpBusiness business, Map<Class<?>, List<CcpBusiness>> exceptionHandlers) {
		try {
			CcpJsonRepresentation apply = business.execute(json);
			return apply;
		} catch (RuntimeException e) {
			CcpJsonRepresentation handledJson = this.handleException(json, e, exceptionHandlers);
			return handledJson;
		}
	}

	/**
	 * Like {@link #executeBusiness(CcpJsonRepresentation, CcpBusiness, Map)}, but for a business that guards
	 * the operation (the {@code before} flow): once a foreseen exception is handled, the operation it guards
	 * must not happen, so {@link CcpErrorEntityOperationCanceled} is thrown for the caller to give up on it.
	 */
	default CcpJsonRepresentation executeBusinessCancelingTheOperationWhenHandled(CcpJsonRepresentation json, CcpBusiness business, Map<Class<?>, List<CcpBusiness>> exceptionHandlers) {
		try {
			CcpJsonRepresentation apply = business.execute(json);
			return apply;
		} catch (RuntimeException e) {
			this.handleException(json, e, exceptionHandlers);
			CcpErrorEntityOperationCanceled operationCanceled = new CcpErrorEntityOperationCanceled(e);
			throw operationCanceled;
		}
	}

	/**
	 * Executes, in sequence, the handlers foreseen for the exception's type, or rethrows it when none is.
	 */
	private CcpJsonRepresentation handleException(CcpJsonRepresentation json, RuntimeException e, Map<Class<?>, List<CcpBusiness>> exceptionHandlers) {
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

	/**
	 * Builds the handler map from the {@code @CcpExceptionFlow} items: exception class to the instantiated businesses.
	 * @param flows the configured flows
	 * @return the handlers by exception class
	 */
	default Map<Class<?>, List<CcpBusiness>> getExceptionHandlers(CcpExceptionFlow[] flows){
		
		Map<Class<?>, List<CcpBusiness>> result = new HashMap<>();
		
		for (CcpExceptionFlow flow : flows) {
			Class<?> whenThrowing = flow.whenThrowing();
			Class<?>[] thenExecute = flow.thenExecute();
			var stream = Arrays.asList(thenExecute)
					.stream();
					var streamMap = stream
					.map(x -> this.getBusiness(x));
					var asList = streamMap
					.collect(Collectors.toList())
					;
			
			result.put(whenThrowing, asList);
		}
		return result;
	}
	
	/**
	 * Instantiates the class as a {@code CcpBusiness} through its no-arg constructor.
	 * @param clazz the business class
	 * @return the business
	 */
	default CcpBusiness getBusiness(Class<?> clazz) {
		CcpReflectionConstructorDecorator ccpReflectionConstructorDecorator = new CcpReflectionConstructorDecorator(clazz);
		CcpBusiness newInstance = ccpReflectionConstructorDecorator.newInstance();
		return newInstance;
	}


}
