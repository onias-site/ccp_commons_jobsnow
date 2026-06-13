package com.ccp.especifications.db.utils.entity.decorators.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Configura o padrão de entidade twin, onde um registro migra entre dois índices (entidade principal e
 * entidade twin) conforme seu estado. Define a função de limpeza de cache, o executor de bulk e o nome
 * do índice twin.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
@SuppressWarnings("rawtypes")
public @interface CcpEntityTwin {

	/**
	 * Classe que implementa a lógica de invalidação de cache ao mover registros entre entidades.
	 */
	Class functionToDeleteKeysInTheCacheClass ();

	/**
	 * Classe que implementa o executor de operações bulk para esta entidade.
	 */
	Class bulkExecutorClass ();

	/**
	 * Nome do índice twin (índice de destino/origem alternativo).
	 */
	String twinEntityName();

}
