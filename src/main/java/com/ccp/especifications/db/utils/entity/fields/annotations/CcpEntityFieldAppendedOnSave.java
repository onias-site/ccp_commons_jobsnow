package com.ccp.especifications.db.utils.entity.fields.annotations;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;

/**
 * Marks a list field whose items each save adds to the stored ones (repeated items included), in an entity annotated
 * with {@code @CcpEntityArraysAppendedOnSave}.
 */
@Retention(RUNTIME)
public @interface CcpEntityFieldAppendedOnSave {
}
