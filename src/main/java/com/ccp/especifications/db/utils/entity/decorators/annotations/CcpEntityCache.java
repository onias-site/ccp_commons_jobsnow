package com.ccp.especifications.db.utils.entity.decorators.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marca uma entidade para ter seus registros cacheados. O atributo {@code value} define o tempo de
 * expiração do cache em segundos.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface CcpEntityCache {

	/**
	 * Tempo de expiração do cache em segundos.
	 */
	int value();
}
