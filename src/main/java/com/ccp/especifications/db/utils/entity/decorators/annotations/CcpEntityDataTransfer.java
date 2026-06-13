package com.ccp.especifications.db.utils.entity.decorators.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.ccp.especifications.db.utils.entity.decorators.enums.CcpEntityDecoratorTransferType;
import com.ccp.especifications.db.utils.entity.decorators.enums.CcpEntityOperationStepType;
import com.ccp.especifications.db.utils.entity.decorators.enums.CcpEntityType;

/**
 * Configura uma transferência de dados entre entidades ({@code copyDataTo} ou {@code transferDataTo}).
 * Define a origem, o destino, o tipo de transferência, o momento de execução (antes/depois da operação
 * principal) e os negócios a executar com seus tratadores de exceção.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface CcpEntityDataTransfer {

	/**
	 * Tratadores de exceção específicos desta transferência.
	 */
	CcpExceptionFlow[] transferHandlers();

	/**
	 * Entidade de origem da transferência (mainEntity ou twinEntity).
	 */
	CcpEntityType from();

	/**
	 * Tipo de transferência ({@code copyDataTo} ou {@code transferDataTo}).
	 */
	CcpEntityDecoratorTransferType transferType();

	/**
	 * Momento de execução: {@code before} (antes) ou {@code after} (depois) da operação principal.
	 */
	CcpEntityOperationStepType when();

	/**
	 * Classes de negócio a executar durante a transferência.
	 */
	@SuppressWarnings("rawtypes")
	Class[] execute();

	/**
	 * Classe de configuração da entidade destino.
	 */
	Class<?> to();

}
