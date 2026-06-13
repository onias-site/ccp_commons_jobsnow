package com.ccp.especifications.db.utils.entity.decorators.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.ccp.especifications.db.utils.entity.decorators.enums.CcpEntityOperationStepType;
import com.ccp.especifications.db.utils.entity.decorators.enums.CcpEntityDecoratorOperationType;
import com.ccp.especifications.db.utils.entity.decorators.enums.CcpEntityType;

/**
 * Configura uma operação com side effects para uma entidade (save, delete, deleteAnyWhere). Define a
 * origem, o tipo de operação, o momento de execução e os negócios a executar com seus tratadores de exceção.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface CcpEntityOperation {

	/**
	 * Tratadores de exceção específicos desta operação.
	 */
	CcpExceptionFlow[] operationHandlers();

	/**
	 * Entidade de origem (mainEntity ou twinEntity).
	 */
	CcpEntityType from();

	/**
	 * Tipo da operação: {@code save}, {@code delete} ou {@code deleteAnyWhere}.
	 */
	CcpEntityDecoratorOperationType operation();

	/**
	 * Momento de execução: {@code before} (antes) ou {@code after} (depois) da operação.
	 */
	CcpEntityOperationStepType when();

	/**
	 * Classes de negócio a executar durante a operação.
	 */
	@SuppressWarnings("rawtypes")
	Class[] execute();

}
