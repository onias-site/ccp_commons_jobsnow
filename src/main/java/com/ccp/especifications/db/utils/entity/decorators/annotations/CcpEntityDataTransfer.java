package com.ccp.especifications.db.utils.entity.decorators.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.ccp.especifications.db.utils.entity.decorators.enums.CcpEntityDataTransferType;

/**
 * Configura uma transferência de dados entre entidades ({@code copyDataTo} ou {@code transferDataTo}).
 * A combinação de momento de execução, tipo de transferência e entidade de origem vem encapsulada em
 * um único item de {@code CcpEntityDataTransferType}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface CcpEntityDataTransfer {

	/**
	 * Combinação de {@code operationPhase}, {@code transferType} e {@code entityPhase} desta
	 * transferência.
	 */
	CcpEntityDataTransferType operationType();

	/**
	 * Tratadores de exceção específicos desta transferência.
	 */
	CcpExceptionFlow[] transferHandlers();

	/**
	 * Classes de negócio a executar durante a transferência.
	 */
	@SuppressWarnings("rawtypes")
	Class[] execute();

	/**
	 * Classe de configuração da entidade destino.
	 */
	Class<?> targetEntity();

}
