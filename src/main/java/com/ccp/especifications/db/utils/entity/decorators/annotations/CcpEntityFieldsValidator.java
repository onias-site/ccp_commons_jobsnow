package com.ccp.especifications.db.utils.entity.decorators.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marca uma entidade para ter seus campos validados antes das operações de escrita. O atributo indica a
 * classe que contém as definições de validação de cada campo (o schema de campos válidos).
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface CcpEntityFieldsValidator {

	/**
	 * Classe que define os campos válidos e suas regras de validação para esta entidade.
	 */
	@SuppressWarnings("rawtypes")
	Class classReferenceWithTheFields();

}
