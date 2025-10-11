package com.ccp.especifications.db.utils;

import com.ccp.especifications.db.utils.decorators.annotations.CcpEntityDecorators;
import com.ccp.especifications.db.utils.decorators.annotations.CcpEntityExpurgable;

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
