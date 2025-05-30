package com.ccp.validation.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
public @interface CcpJsonFieldsValidation {

	CcpSimpleObject[] simpleObject() default {};

	CcpSimpleArray[] simpleArray() default {};

	CcpAllowedValues[] allowedValues() default {};

	CcpObjectNumbers[] objectNumbers() default {};

	CcpArrayNumbers[] arrayNumbers() default {};

	CcpObjectTextSize[] objectTextSize() default {};

	CcpArrayTextSize[] arrayTextSize() default {};

	CcpArraySize[] arraySize() default {};
	
	CcpYear[] year() default {};

	CcpDay[] day() default {};
	
	CcpRegex[] regex() default {};
	
	Class<?> rulesClass() default Dumb.class;
	
	class Dumb {

	}
	

}
