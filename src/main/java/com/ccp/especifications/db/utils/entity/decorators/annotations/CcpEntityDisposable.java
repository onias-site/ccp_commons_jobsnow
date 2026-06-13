package com.ccp.especifications.db.utils.entity.decorators.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.ccp.especifications.db.utils.entity.decorators.enums.CcpEntityExpurgableOptions;

/**
 * Marca uma entidade como descartável (com prazo de expiração automático). Define a fábrica responsável
 * por criar registros expiráveis e a granularidade temporal da expiração.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface CcpEntityDisposable {

	/**
	 * Classe factory responsável por criar e gerenciar os registros expiráveis.
	 */
	Class<?> expurgableEntityFactory();

	/**
	 * Granularidade do tempo de expiração (yearly, monthly, daily, hourly, minute, second, millisecond).
	 */
	CcpEntityExpurgableOptions expurgTime();

}
