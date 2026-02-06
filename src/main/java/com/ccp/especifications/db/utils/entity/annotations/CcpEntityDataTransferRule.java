package com.ccp.especifications.db.utils.entity.annotations;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;

@Retention(RUNTIME)
public @interface CcpEntityDataTransferRule {
	Class<?>[] thenExecuteTheFollowingFlow();
	Class<?> whenTransferingToEntity();
}
