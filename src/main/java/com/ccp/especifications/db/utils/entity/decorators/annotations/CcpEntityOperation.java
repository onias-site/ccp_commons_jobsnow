package com.ccp.especifications.db.utils.entity.decorators.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.ccp.especifications.db.utils.entity.decorators.enums.CcpEntityOperationType;

/**
 * Configura uma operação com side effects para uma entidade (save, delete, deleteAnyWhere). A
 * combinação de momento de execução, tipo de operação e entidade de origem vem encapsulada em um
 * único item de {@code CcpEntityOperationType}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface CcpEntityOperation {

	/**
	 * Combinação de {@code operationPhase}, {@code operationType} e {@code entityPhase} desta
	 * operação.
	 */
	CcpEntityOperationType operationType();

	/**
	 * Tratadores de exceção específicos desta operação.
	 */
	CcpExceptionFlow[] operationHandlers();

	/**
	 * Classes de negócio a executar durante a operação.
	 */
	@SuppressWarnings("rawtypes")
	Class[] execute();

}
