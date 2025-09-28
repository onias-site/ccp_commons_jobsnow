package com.ccp.json.validations.fields.interfaces;

import java.lang.reflect.Field;

public interface CcpJsonFieldValidatorCatalog {

	CcpJsonFieldValidatorInterface[] getValidations(Field field);	
}
