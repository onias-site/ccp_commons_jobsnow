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
 * Interface auxiliar compartilhada por {@code CcpEntityDecoratorOperationType} e
 * {@code CcpEntityDecoratorTransferType}. Fornece a lógica de execução de negócios com suporte a
 * tratamento de exceções configurado via {@code @CcpExceptionFlow}.
 */
interface OperationWriter {

	/**
	 * Executa {@code business} com o JSON informado, capturando exceções previstas em
	 * {@code exceptionHandlers} e executando os negócios de fallback correspondentes.
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

	/** Constrói o mapa de handlers a partir de um array de {@code @CcpExceptionFlow}. */
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
	
	/** Instancia via reflexão a classe informada como {@code CcpBusiness}. */
	default CcpBusiness getBusiness(Class<?> clazz) {
		CcpReflectionConstructorDecorator ccpReflectionConstructorDecorator = new CcpReflectionConstructorDecorator(clazz);
		CcpBusiness newInstance = ccpReflectionConstructorDecorator.newInstance();
		return newInstance;
	}


}
