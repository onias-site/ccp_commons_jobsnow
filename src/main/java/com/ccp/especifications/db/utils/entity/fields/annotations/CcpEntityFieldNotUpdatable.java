package com.ccp.especifications.db.utils.entity.fields.annotations;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;

/** Marks an entity field as not updatable: an update keeps the stored value of the field. */
@Retention(RUNTIME)
public @interface CcpEntityFieldNotUpdatable {
}
