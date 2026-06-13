package com.ccp.especifications.db.utils.entity.decorators.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marca uma entidade para ter suas escritas realizadas de forma assíncrona. O atributo {@code value}
 * indica a classe responsável pela escrita assíncrona.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface CcpEntityAsyncWriter {

	/**
	 * Classe que implementa a lógica de escrita assíncrona para esta entidade.
	 */
	Class<?> value();
}
