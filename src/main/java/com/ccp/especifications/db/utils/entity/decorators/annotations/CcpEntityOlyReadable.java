package com.ccp.especifications.db.utils.entity.decorators.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks an entity as read-only. When present, the {@code DecoratorReadOnlyEntity} decorator is applied: {@code save},
 * {@code delete}, {@code deleteAnyWhere} and {@code transferDataTo} do nothing and return {@code false}, without
 * throwing.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface CcpEntityOlyReadable{}
