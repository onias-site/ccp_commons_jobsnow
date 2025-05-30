package com.ccp.exceptions.db.utils;

import com.ccp.especifications.db.utils.CcpEntityField;
import com.ccp.especifications.db.utils.decorators.configurations.CcpEntityDecorators;
import com.ccp.especifications.db.utils.decorators.configurations.CcpEntityExpurgable;

@SuppressWarnings("serial")
public class CcpErrorEntityIncorrectClassConfiguration extends RuntimeException{
	
	public CcpErrorEntityIncorrectClassConfiguration(Class<?> configurationClass, IncorrectEntityClassConfigurationType incorrectEntityClassConfigurationType) {
		super(incorrectEntityClassConfigurationType.getMessage(configurationClass));
	}
	
	public static enum IncorrectEntityClassConfigurationType{
		mustDeclarePublicStaticEnum {
			String getMessage(String className) {
				String message = "The class '" + className + "' must declare a public static enum";	
				return message;
			}
		},
		mustDeclareAnEnumCalledFields {
			String getMessage(String className) {
				String message = "The class '" + className + "' must declare a public static enum called 'FIELDS'";	
				return message;
			}
		},
		mustHaveAnEnumThatImplementsTheInterface {
			String getMessage(String className) {
				String message = "The class '" + className + "' must have an enum that implements the interface " + CcpEntityField.class.getName();	
				return message;
			}
		},
		thisClassIsAnnotedByExpurgableAndDecoratorsAtSameTime {
			String getMessage(String className) {
				String message = "The class '" + className + "' can not be annoted by '" 
						+ CcpEntityDecorators.class.getName() + "' annotation and '" + CcpEntityExpurgable.class.getName() + "' at the same time";	
				return message;
			}
		}
		;
		abstract String getMessage(String className);
		String getMessage(Class<?> configurationClass) {
			String className = configurationClass.getName();
			String message = this.getMessage(className);
			return message;
		}
		
	}
}
