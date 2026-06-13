package com.ccp.especifications.db.utils.entity.decorators.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Agrupa múltiplas configurações de {@code @CcpEntityDataTransfer} em uma entidade, além de definir
 * tratadores de exceção globais para todas as transferências.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface CcpEntityDataTransfers {

	/**
	 * Tratadores de exceção globais aplicados a todas as transferências.
	 */
	CcpExceptionFlow[] globalHandlers();

	/**
	 * Array de transferências configuradas para a entidade.
	 */
	CcpEntityDataTransfer[] transfers();

}
