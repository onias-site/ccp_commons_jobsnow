package com.ccp.especifications.db.utils.entity.fields.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a field of the primary key ({@link CcpEntityFieldPrimaryKey}) whose value is written by the businesses run before
 * the save, not by the caller (since 2026-11-27): the validation of the fields runs before those businesses, so the field
 * is not required in the input of the save, as the other fields of the primary key are. The id still needs it: a save
 * that reaches the database without it fails when the id is calculated.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.FIELD })
public @interface CcpEntityFieldWrittenBeforeTheSave {
}
