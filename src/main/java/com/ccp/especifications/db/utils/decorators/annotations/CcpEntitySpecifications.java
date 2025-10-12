package com.ccp.especifications.db.utils.decorators.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
@SuppressWarnings("rawtypes")
public @interface CcpEntitySpecifications {

	Class[] afterDeleteRecord();
	Class[] afterSaveRecord();
	Class[] beforeSaveRecord();
	boolean cacheableEntity();
	Class<?> entityValidation();
	Class<?> entityFieldsTransformers();
	

}
