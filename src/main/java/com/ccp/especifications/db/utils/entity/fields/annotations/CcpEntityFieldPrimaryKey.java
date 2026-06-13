package com.ccp.especifications.db.utils.entity.fields.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marca um campo de entidade como parte da chave primária. O ID do documento no Elasticsearch é
 * calculado a partir dos valores SHA-1 dos campos com esta anotação, em ordem alfabética.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
public @interface CcpEntityFieldPrimaryKey {
}
