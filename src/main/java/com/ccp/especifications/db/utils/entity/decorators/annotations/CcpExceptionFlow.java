package com.ccp.especifications.db.utils.entity.decorators.annotations;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;

/**
 * Define um fluxo de tratamento de exceção: quando uma exceção do tipo {@code whenThrowing} for
 * lançada durante a execução de um negócio, os negócios listados em {@code thenExecute} serão
 * executados em sequência.
 */
@Retention(RUNTIME)
public @interface CcpExceptionFlow {
	/** Classes de negócio a executar quando a exceção configurada for capturada. */
	Class<?>[] thenExecute();
	/** Tipo de exceção que dispara este fluxo de tratamento. */
	Class<?> whenThrowing();
}
