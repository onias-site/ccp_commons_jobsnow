package com.ccp.especifications.db.utils.entity.decorators.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marca uma entidade para ter histórico de versões. O atributo indica a classe responsável
 * por gerenciar o versionamento dos registros.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface CcpEntityVersionable {
	/** Classe que implementa a lógica de versionamento para esta entidade. */
	Class<?> value();
}
