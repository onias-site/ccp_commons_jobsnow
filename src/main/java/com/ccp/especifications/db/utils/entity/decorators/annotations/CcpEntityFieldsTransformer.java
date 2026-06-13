package com.ccp.especifications.db.utils.entity.decorators.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marca uma entidade para ter seus campos transformados antes das operações. O atributo indica a classe
 * que contém as definições de transformação de cada campo.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface CcpEntityFieldsTransformer {

	/**
	 * Classe que contém os transformadores padrão para os campos desta entidade.
	 */
	@SuppressWarnings("rawtypes")
	Class classReferenceWithTheFields();

}
