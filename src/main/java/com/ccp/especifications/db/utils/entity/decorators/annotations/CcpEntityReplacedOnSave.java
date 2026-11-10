package com.ccp.especifications.db.utils.entity.decorators.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks an entity whose {@code save} replaces the stored document instead of merging into it: a field left out of the
 * json is removed from the record (by default, {@code CcpCrud.save} keeps it). Use it on entities written always whole,
 * whose optional fields may be cleared by the user (the resume: a salary no longer accepted, the last job erased).
 * When present, the {@code DecoratorReplacedOnSaveEntity} decorator is applied, the innermost of the chain.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface CcpEntityReplacedOnSave{}
