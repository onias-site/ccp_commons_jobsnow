package com.ccp.especifications.db.utils.entity.fields.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Associa um transformador customizado a um campo específico de entidade, sobrescrevendo o
 * transformador padrão definido em {@code @CcpEntityFieldsTransformer}. A classe informada deve
 * implementar {@code CcpBusiness}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
public @interface CcpEntityFieldTransformer {
	/** Classe transformadora customizada para este campo. */
	Class<?> value();
}
