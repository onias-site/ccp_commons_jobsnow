package com.ccp.especifications.db.utils.entity.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
@SuppressWarnings("rawtypes")
public @interface CcpEntitySpecifications {

	Class[] afterSaveRecord();
	CcpEntitySaveFlow[] flow();
	Class[] beforeSaveRecord();
	Class<?> entityValidation();
	Class[] afterDeleteRecord();
	Class<?> entityFieldsTransformers();
	

}
