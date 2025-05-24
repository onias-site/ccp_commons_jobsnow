package com.ccp.especifications.db.utils.decorators.configurations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface CcpEntitySpecifications {

	CcpEntityTransferOperationEspecification inactivate();
	CcpEntityTransferOperationEspecification reactivate();
	CcpEntityOperationSpecification delete();
	CcpEntityOperationSpecification save();
	Class<?> classWithFieldsValidationsRules();

	boolean cacheableEntity();
	
}
