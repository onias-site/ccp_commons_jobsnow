package com.ccp.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method or constructor whose parameters may legitimately receive {@code null}.
 * <p>
 * The null-check aspect skips the parameter verification of every member annotated with it; without the
 * annotation, a {@code null} argument raises {@link CcpNullParameterException}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD, ElementType.CONSTRUCTOR})
public @interface CcpAllowNullParameter {}
