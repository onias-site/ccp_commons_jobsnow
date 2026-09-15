package com.ccp.especifications.db.utils.entity.decorators.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Agrupa múltiplas configurações de {@code @CcpEntityOperation} em uma entidade, além de definir
 * tratadores de exceção globais para todas as operações.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface CcpEntityOperations {

	/**
	 * Array de operações configuradas para a entidade.
	 */
	CcpEntityOperation[] value();

	/**
	 * Tratadores de exceção globais aplicados a todas as operações.
	 */
	CcpExceptionFlow[] globalHandlers() default {};

}
