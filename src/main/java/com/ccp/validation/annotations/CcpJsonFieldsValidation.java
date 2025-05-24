package com.ccp.validation.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.METHOD, ElementType.TYPE })
public @interface CcpJsonFieldsValidation {

	SimpleObject[] simpleObject() default {};

	SimpleArray[] simpleArray() default {};

	AllowedValues[] allowedValues() default {};

	ObjectNumbers[] objectNumbers() default {};

	ArrayNumbers[] arrayNumbers() default {};

	ObjectTextSize[] objectTextSize() default {};

	ArrayTextSize[] arrayTextSize() default {};

	ArraySize[] arraySize() default {};
	
	Year[] year() default {};

	Day[] day() default {};
	
	Regex[] regex() default {};
	
	Class<?> rulesClass() default Dumb.class;
	
	class Dumb {

	}
	

}
