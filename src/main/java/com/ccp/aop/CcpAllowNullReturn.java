package com.ccp.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marks a method that may legitimately return {@code null}.
 * <p>
 * The null-check aspect skips the return verification of every method annotated with it; without the
 * annotation, a {@code null} return raises {@link CcpNullReturnException}.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CcpAllowNullReturn {}
