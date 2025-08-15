package com.ccp.json.fields.validations.enums;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.util.function.Predicate;

import com.ccp.constantes.CcpOtherConstants;
import com.ccp.decorators.CcpJsonRepresentation;

enum CcpJsonFieldErrorTypesConstants{
	error, ruleExplanation, providedValue	
}

public enum CcpJsonFieldErrorTypes {
	incompatibleType(CcpJsonFieldErrorHandleType.breakFieldValidation) {
		public CcpJsonRepresentation getError(CcpJsonRepresentation json, Class<?> clazz, Field field,
				Class<? extends Annotation> annotation, CcpJsonFieldValueExtractor valueExtractor) {
			// TODO Auto-generated method stub
			return null;
		}

		public boolean hasError(CcpJsonRepresentation json, Class<?> clazz, Field field,
				Class<? extends Annotation> annotation, 
				Predicate<CcpJsonRepresentation> predicate,
				CcpJsonFieldValueExtractor valueExtractor) {
			
			String fieldName = field.getName();
			
			Object value = valueExtractor.getValue(json, fieldName);
			
			if(value == null) {
				return false;
			}

			boolean test = predicate.test(json);
			
			return test;
 		}

		@Override
		CcpJsonRepresentation getRuleExplanation(CcpJsonRepresentation json, Class<?> clazz, Field field,
				CcpJsonFieldValueExtractor valueExtractor) {
			// TODO Auto-generated method stub
			return null;
		}
	},
	annotationIsMissing(CcpJsonFieldErrorHandleType.breakFieldValidation) {
		public CcpJsonRepresentation getError(CcpJsonRepresentation json, Class<?> clazz, Field field,
				Class<? extends Annotation> annotation, CcpJsonFieldValueExtractor valueExtractor) {
			// TODO Auto-generated method stub
			return null;
		}

		public boolean hasError(CcpJsonRepresentation json, Class<?> clazz, Field field,
				Class<? extends Annotation> annotation, 
				Predicate<CcpJsonRepresentation> predicate,
				CcpJsonFieldValueExtractor valueExtractor) {
			
			boolean annotationIsMissing = field.isAnnotationPresent(annotation) == false;
			
			
			return annotationIsMissing;
		}

		@Override
		CcpJsonRepresentation getRuleExplanation(CcpJsonRepresentation json, Class<?> clazz, Field field,
				CcpJsonFieldValueExtractor valueExtractor) {
			// TODO Auto-generated method stub
			return null;
		}
	},
	requiredFieldIsMissing(CcpJsonFieldErrorHandleType.breakFieldValidation) {
		@Override
		CcpJsonRepresentation getRuleExplanation(CcpJsonRepresentation json, Class<?> clazz, Field field,
				CcpJsonFieldValueExtractor valueExtractor) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		CcpJsonRepresentation getError(CcpJsonRepresentation json, Class<?> clazz, Field field,
				Class<? extends Annotation> annotation, CcpJsonFieldValueExtractor valueExtractor) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		boolean hasError(CcpJsonRepresentation json, Class<?> clazz, Field field,
				Class<? extends Annotation> annotation, Predicate<CcpJsonRepresentation> predicate,
				CcpJsonFieldValueExtractor valueExtractor) {
			// TODO Auto-generated method stub
			return false;
		}
	},
	objectNumberMaxValue(CcpJsonFieldErrorHandleType.continueFieldValidation) {
		@Override
		CcpJsonRepresentation getRuleExplanation(CcpJsonRepresentation json, Class<?> clazz, Field field,
				CcpJsonFieldValueExtractor valueExtractor) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		CcpJsonRepresentation getError(CcpJsonRepresentation json, Class<?> clazz, Field field,
				Class<? extends Annotation> annotation, CcpJsonFieldValueExtractor valueExtractor) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		boolean hasError(CcpJsonRepresentation json, Class<?> clazz, Field field,
				Class<? extends Annotation> annotation, Predicate<CcpJsonRepresentation> predicate,
				CcpJsonFieldValueExtractor valueExtractor) {
			// TODO Auto-generated method stub
			return false;
		}
	},
	objectNumberMinValue(CcpJsonFieldErrorHandleType.continueFieldValidation) {
		@Override
		CcpJsonRepresentation getRuleExplanation(CcpJsonRepresentation json, Class<?> clazz, Field field,
				CcpJsonFieldValueExtractor valueExtractor) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		CcpJsonRepresentation getError(CcpJsonRepresentation json, Class<?> clazz, Field field,
				Class<? extends Annotation> annotation, CcpJsonFieldValueExtractor valueExtractor) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		boolean hasError(CcpJsonRepresentation json, Class<?> clazz, Field field,
				Class<? extends Annotation> annotation, Predicate<CcpJsonRepresentation> predicate,
				CcpJsonFieldValueExtractor valueExtractor) {
			// TODO Auto-generated method stub
			return false;
		}
	},
	objectNumberAllowed(CcpJsonFieldErrorHandleType.continueFieldValidation) {
		@Override
		CcpJsonRepresentation getRuleExplanation(CcpJsonRepresentation json, Class<?> clazz, Field field,
				CcpJsonFieldValueExtractor valueExtractor) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		CcpJsonRepresentation getError(CcpJsonRepresentation json, Class<?> clazz, Field field,
				Class<? extends Annotation> annotation, CcpJsonFieldValueExtractor valueExtractor) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		boolean hasError(CcpJsonRepresentation json, Class<?> clazz, Field field,
				Class<? extends Annotation> annotation, Predicate<CcpJsonRepresentation> predicate,
				CcpJsonFieldValueExtractor valueExtractor) {
			// TODO Auto-generated method stub
			return false;
		}
	},
	objectArrayMinSize(CcpJsonFieldErrorHandleType.continueFieldValidation) {
		@Override
		CcpJsonRepresentation getRuleExplanation(CcpJsonRepresentation json, Class<?> clazz, Field field,
				CcpJsonFieldValueExtractor valueExtractor) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		CcpJsonRepresentation getError(CcpJsonRepresentation json, Class<?> clazz, Field field,
				Class<? extends Annotation> annotation, CcpJsonFieldValueExtractor valueExtractor) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		boolean hasError(CcpJsonRepresentation json, Class<?> clazz, Field field,
				Class<? extends Annotation> annotation, Predicate<CcpJsonRepresentation> predicate,
				CcpJsonFieldValueExtractor valueExtractor) {
			// TODO Auto-generated method stub
			return false;
		}
	},
	objectArrayMaxSize(CcpJsonFieldErrorHandleType.continueFieldValidation) {
		@Override
		CcpJsonRepresentation getRuleExplanation(CcpJsonRepresentation json, Class<?> clazz, Field field,
				CcpJsonFieldValueExtractor valueExtractor) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		CcpJsonRepresentation getError(CcpJsonRepresentation json, Class<?> clazz, Field field,
				Class<? extends Annotation> annotation, CcpJsonFieldValueExtractor valueExtractor) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		boolean hasError(CcpJsonRepresentation json, Class<?> clazz, Field field,
				Class<? extends Annotation> annotation, Predicate<CcpJsonRepresentation> predicate,
				CcpJsonFieldValueExtractor valueExtractor) {
			// TODO Auto-generated method stub
			return false;
		}
	},
	objectArrayNonReapeted(CcpJsonFieldErrorHandleType.continueFieldValidation) {
		@Override
		CcpJsonRepresentation getRuleExplanation(CcpJsonRepresentation json, Class<?> clazz, Field field,
				CcpJsonFieldValueExtractor valueExtractor) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		CcpJsonRepresentation getError(CcpJsonRepresentation json, Class<?> clazz, Field field,
				Class<? extends Annotation> annotation, CcpJsonFieldValueExtractor valueExtractor) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		boolean hasError(CcpJsonRepresentation json, Class<?> clazz, Field field,
				Class<? extends Annotation> annotation, Predicate<CcpJsonRepresentation> predicate,
				CcpJsonFieldValueExtractor valueExtractor) {
			// TODO Auto-generated method stub
			return false;
		}
	},
	objectTextMinLength(CcpJsonFieldErrorHandleType.continueFieldValidation) {
		@Override
		CcpJsonRepresentation getRuleExplanation(CcpJsonRepresentation json, Class<?> clazz, Field field,
				CcpJsonFieldValueExtractor valueExtractor) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		CcpJsonRepresentation getError(CcpJsonRepresentation json, Class<?> clazz, Field field,
				Class<? extends Annotation> annotation, CcpJsonFieldValueExtractor valueExtractor) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		boolean hasError(CcpJsonRepresentation json, Class<?> clazz, Field field,
				Class<? extends Annotation> annotation, Predicate<CcpJsonRepresentation> predicate,
				CcpJsonFieldValueExtractor valueExtractor) {
			// TODO Auto-generated method stub
			return false;
		}
	},
	objectTextMaxLength(CcpJsonFieldErrorHandleType.continueFieldValidation) {
		@Override
		CcpJsonRepresentation getRuleExplanation(CcpJsonRepresentation json, Class<?> clazz, Field field,
				CcpJsonFieldValueExtractor valueExtractor) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		CcpJsonRepresentation getError(CcpJsonRepresentation json, Class<?> clazz, Field field,
				Class<? extends Annotation> annotation, CcpJsonFieldValueExtractor valueExtractor) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		boolean hasError(CcpJsonRepresentation json, Class<?> clazz, Field field,
				Class<? extends Annotation> annotation, Predicate<CcpJsonRepresentation> predicate,
				CcpJsonFieldValueExtractor valueExtractor) {
			// TODO Auto-generated method stub
			return false;
		}
	},
	objectTextAllowedValues(CcpJsonFieldErrorHandleType.continueFieldValidation) {
		@Override
		CcpJsonRepresentation getRuleExplanation(CcpJsonRepresentation json, Class<?> clazz, Field field,
				CcpJsonFieldValueExtractor valueExtractor) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		CcpJsonRepresentation getError(CcpJsonRepresentation json, Class<?> clazz, Field field,
				Class<? extends Annotation> annotation, CcpJsonFieldValueExtractor valueExtractor) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		boolean hasError(CcpJsonRepresentation json, Class<?> clazz, Field field,
				Class<? extends Annotation> annotation, Predicate<CcpJsonRepresentation> predicate,
				CcpJsonFieldValueExtractor valueExtractor) {
			// TODO Auto-generated method stub
			return false;
		}
	},
	objectTextRegex(CcpJsonFieldErrorHandleType.continueFieldValidation) {
		@Override
		CcpJsonRepresentation getRuleExplanation(CcpJsonRepresentation json, Class<?> clazz, Field field,
				CcpJsonFieldValueExtractor valueExtractor) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		CcpJsonRepresentation getError(CcpJsonRepresentation json, Class<?> clazz, Field field,
				Class<? extends Annotation> annotation, CcpJsonFieldValueExtractor valueExtractor) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		boolean hasError(CcpJsonRepresentation json, Class<?> clazz, Field field,
				Class<? extends Annotation> annotation, Predicate<CcpJsonRepresentation> predicate,
				CcpJsonFieldValueExtractor valueExtractor) {
			// TODO Auto-generated method stub
			return false;
		}
	},
	objectTimeMaxValue(CcpJsonFieldErrorHandleType.continueFieldValidation) {
		@Override
		CcpJsonRepresentation getRuleExplanation(CcpJsonRepresentation json, Class<?> clazz, Field field,
				CcpJsonFieldValueExtractor valueExtractor) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		CcpJsonRepresentation getError(CcpJsonRepresentation json, Class<?> clazz, Field field,
				Class<? extends Annotation> annotation, CcpJsonFieldValueExtractor valueExtractor) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		boolean hasError(CcpJsonRepresentation json, Class<?> clazz, Field field,
				Class<? extends Annotation> annotation, Predicate<CcpJsonRepresentation> predicate,
				CcpJsonFieldValueExtractor valueExtractor) {
			// TODO Auto-generated method stub
			return false;
		}
	},
	objectTimeMinValue(CcpJsonFieldErrorHandleType.continueFieldValidation) {
		@Override
		CcpJsonRepresentation getRuleExplanation(CcpJsonRepresentation json, Class<?> clazz, Field field,
				CcpJsonFieldValueExtractor valueExtractor) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		CcpJsonRepresentation getError(CcpJsonRepresentation json, Class<?> clazz, Field field,
				Class<? extends Annotation> annotation, CcpJsonFieldValueExtractor valueExtractor) {
			// TODO Auto-generated method stub
			return null;
		}

		@Override
		boolean hasError(CcpJsonRepresentation json, Class<?> clazz, Field field,
				Class<? extends Annotation> annotation, Predicate<CcpJsonRepresentation> predicate,
				CcpJsonFieldValueExtractor valueExtractor) {
			// TODO Auto-generated method stub
			return false;
		}
	},
	

	;
	
	private CcpJsonFieldErrorTypes(CcpJsonFieldErrorHandleType handleType) {
		this.errorHandleType = handleType;
	}


	private final CcpJsonFieldErrorHandleType errorHandleType;
	
	abstract CcpJsonRepresentation getRuleExplanation(
			CcpJsonRepresentation json,
			Class<?> clazz, 
			Field field,
			CcpJsonFieldValueExtractor valueExtractor 
			
			);
	
	abstract CcpJsonRepresentation getError(
			CcpJsonRepresentation json,
			Class<?> clazz, 
			Field field,
			Class<? extends Annotation> annotation,
			CcpJsonFieldValueExtractor valueExtractor 
			);

	abstract boolean hasError(
			CcpJsonRepresentation json,
			Class<?> clazz, 
			Field field,
			Class<? extends Annotation> annotation,
			Predicate<CcpJsonRepresentation> predicate,
			CcpJsonFieldValueExtractor valueExtractor 
			);
	
	
	public CcpJsonRepresentation evaluate(
			CcpJsonRepresentation errors,
			CcpJsonRepresentation json,
			Class<?> clazz, 
			Field fieldReflection,
			Class<? extends Annotation> annotation,
			Predicate<CcpJsonRepresentation> predicate,
			CcpJsonFieldValueExtractor valueExtractor 
			) {
		
		boolean hasNoError = this.hasError(json, clazz, fieldReflection, annotation, predicate, valueExtractor) == false;
		
		if(hasNoError) {
			return CcpOtherConstants.EMPTY_JSON;
		}
		
		String ruleName = this.name();
		String fieldName = fieldReflection.getName();
		
		CcpJsonRepresentation field = errors.getInnerJson(fieldName);
		CcpJsonRepresentation rule = field.getInnerJson(ruleName);
		
		Object providedValue = valueExtractor.getValue(json, fieldName);
		CcpJsonRepresentation withProvidedValue = rule.put(CcpJsonFieldErrorTypesConstants.providedValue, providedValue);

		CcpJsonRepresentation ruleExplanation = this.getRuleExplanation(rule, clazz, fieldReflection, valueExtractor);
		CcpJsonRepresentation withRuleExplanation = withProvidedValue.put(CcpJsonFieldErrorTypesConstants.ruleExplanation, ruleExplanation);
		
		CcpJsonRepresentation error = this.getError(json, clazz, fieldReflection, annotation, valueExtractor);
		CcpJsonRepresentation withError = withRuleExplanation.put(CcpJsonFieldErrorTypesConstants.error, error);
		
		CcpJsonRepresentation updatedField = field.put(this, withError);
//		FIXME CcpJsonRepresentation updatedErrors = errors.put(fieldName, updatedField);
		CcpJsonRepresentation updatedErrors = errors.put(null, updatedField);
		
		this.errorHandleType.breakValidation(updatedErrors);
		return updatedErrors;
	}

}
