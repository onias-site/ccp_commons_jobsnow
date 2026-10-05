package com.ccp.especifications.db.utils.entity.fields.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks an entity field as part of the primary key: the id of the document is the SHA-1 of the values of these fields,
 * sorted by field name.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
public @interface CcpEntityFieldPrimaryKey {
}
