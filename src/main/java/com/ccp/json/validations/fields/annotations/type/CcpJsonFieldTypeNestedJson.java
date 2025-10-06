package com.ccp.json.validations.fields.annotations.type;

import static java.lang.annotation.ElementType.FIELD;
import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import java.lang.reflect.Field;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.json.validations.fields.enums.CcpJsonFieldErrorHandleType;
import com.ccp.json.validations.fields.enums.CcpJsonFieldType;
import com.ccp.json.validations.fields.interfaces.CcpJsonFieldValidatorInterface;

@Retention(RUNTIME)
@Target(FIELD)
public @interface CcpJsonFieldTypeNestedJson {
	Class<?> validationClass() default NoOp.class;
	boolean allowsEmptyJson() default true;
}
class NoOp implements CcpJsonFieldValidatorInterface{

	
	public CcpJsonFieldErrorHandleType getErrorHandleType() {
		return CcpJsonFieldErrorHandleType.continueFieldValidation;
	}

	
	public String getErrorMessage(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
		return "";
	}

	
	public boolean hasError(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
		return false;
	}

	
	public Object getRuleExplanation(Field field, CcpJsonFieldType type) {
		return "";
	}

	
	public boolean hasRuleExplanation(Field field, CcpJsonFieldType type) {
		return false;
	}
	
}
