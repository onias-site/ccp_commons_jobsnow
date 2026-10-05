package com.ccp.json.validations.global.enums;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.json.CcpJsonHandler;
import com.ccp.json.validations.global.annotations.CcpJsonGlobalValidations;
import com.ccp.json.validations.global.annotations.CcpJsonValidationFieldList;
import com.ccp.json.validations.global.interfaces.CcpJsonValidator;

/**
 * Default global validations: {@code requiredAtLeastOne} requires at least one field of each group and
 * {@code requiresAllOrNone} requires each group to be complete or absent.
 */
public enum CcpJsonValidatorDefaults implements CcpJsonValidator{

	/** At least one field of each {@code requiresAtLeastOne} group must be present. */
	requiredAtLeastOne{

		/**
		 * Tells whether some group has none of its fields.
		 * @param json the JSON
		 * @param clazz the validation class
		 * @return {@code true} when a group is missing
		 */
		public boolean hasError(CcpJsonRepresentation json, Class<?> clazz) {
			
			CcpJsonGlobalValidations annotation = clazz.getAnnotation(CcpJsonGlobalValidations.class);
			CcpJsonValidationFieldList[] validations = annotation.requiresAtLeastOne();
			
			for (CcpJsonValidationFieldList validation : validations) {
				
				String[] oneOfThem = getItemsFromAnnotation(validation);
				boolean containsAnyFields = json.containsAnyFields(Arrays.asList(oneOfThem));

				boolean hasError = false == containsAnyFields;
				
				if(hasError) {
					return true;
				}
			}
			return false;
		}

		/**
		 * Describes each group that has none of its fields.
		 * @param json the JSON
		 * @param clazz the validation class
		 * @return one message per missing group
		 */
		public List<String> getErrorMessage(CcpJsonRepresentation json, Class<?> clazz) {
			CcpJsonGlobalValidations annotation = clazz.getAnnotation(CcpJsonGlobalValidations.class);
			CcpJsonValidationFieldList[] requiredAtLeastOne = annotation.requiresAtLeastOne();
			List<String> errors = new ArrayList<>();
			for (CcpJsonValidationFieldList validation : requiredAtLeastOne) {
				
				String[] oneOfThem = getItemsFromAnnotation(validation);
				
				boolean hasNoError = json.containsAnyFields(Arrays.asList(oneOfThem));
				
				if(hasNoError) {
					continue;
				}
				String toString = Arrays.asList(oneOfThem).toString();
				String error = "It is missing one of them fields in the current json: " + toString;
				errors.add(error);
			}
			return errors;
		}

		/**
		 * Explains each group.
		 * @param clazz the validation class
		 * @return one explanation per group
		 */
		public List<String> getRuleExplanation(Class<?> clazz) {
			CcpJsonGlobalValidations annotation = clazz.getAnnotation(CcpJsonGlobalValidations.class);
			CcpJsonValidationFieldList[] requiredAtLeastOne = annotation.requiresAtLeastOne();
			List<String> rules = new ArrayList<>();
			for (CcpJsonValidationFieldList validation : requiredAtLeastOne) {
				
				String[] oneOfThem = getItemsFromAnnotation(validation);
				String toString2 = Arrays.asList(oneOfThem).toString();
				String rule = "The provided json must has one of this following fields: " + toString2;
				rules.add(rule);
			}
			return rules;
		}

		/**
		 * Not critical: the other validations go on.
		 * @param json the JSON
		 * @param clazz the validation class
		 * @return {@code false}
		 */
		public boolean isCriticalValidation(CcpJsonRepresentation json, Class<?> clazz) {
			return false;
		}
	},
	
	/** Each {@code requiresAllOrNone} group must be either complete or absent. */
	requiresAllOrNone{

		/**
		 * Tells whether some group is only partially present.
		 * @param json the JSON
		 * @param clazz the validation class
		 * @return {@code true} when a group is incomplete
		 */
		public boolean hasError(CcpJsonRepresentation json, Class<?> clazz) {
	
			CcpJsonGlobalValidations annotation = clazz.getAnnotation(CcpJsonGlobalValidations.class);
			CcpJsonValidationFieldList[] validations = annotation.requiresAllOrNone();
			
			for (CcpJsonValidationFieldList validation : validations) {
				
				String[] array = getItemsFromAnnotation(validation);
				boolean containsAnyFields2 = json.containsAnyFields(Arrays.asList(array));

				boolean containsNeitherOfThisFields = false == containsAnyFields2;
				
				if(containsNeitherOfThisFields) {
					continue;
				}
				boolean containsAllFields = json.containsAllFields(Arrays.asList(array));

				boolean isMissingAnyField = false == containsAllFields;
				
				if(isMissingAnyField) {
					return true;
				}
			}
			return false;
		}

		/**
		 * Describes, for each incomplete group, the fields present and the missing ones.
		 * @param json the JSON
		 * @param clazz the validation class
		 * @return one message per incomplete group
		 */
		public List<String> getErrorMessage(CcpJsonRepresentation json, Class<?> clazz) {
			
			List<String> errors = new ArrayList<>();
			
			CcpJsonGlobalValidations annotation = clazz.getAnnotation(CcpJsonGlobalValidations.class);
			CcpJsonValidationFieldList[] validations = annotation.requiresAllOrNone();
			
			for (CcpJsonValidationFieldList validation : validations) {
				
				String[] array = getItemsFromAnnotation(validation);
				boolean containsAnyFields3 = json.containsAnyFields(Arrays.asList(array));

				boolean containsNeitherOfThisFields = false == containsAnyFields3;
				
				if(containsNeitherOfThisFields) {
					continue;
				}
				boolean containsAllFields2 = json.containsAllFields(Arrays.asList(array));

				boolean isMissingAnyField = false == containsAllFields2;
				
				if(isMissingAnyField) {
					CcpJsonRepresentation jsonPiece = json.getJsonPiece(Arrays.asList(array));
					Set<String> presentFields = jsonPiece.fieldSet();
					List<String> asList = Arrays.asList(array);
					List<String> missingFields = new ArrayList<String>(asList);
					missingFields.removeAll(presentFields);
					String presentFieldsMessage = "This provided json contains the following fields: " + presentFields;
					String presentFieldsAndMissingLabel = presentFieldsMessage + ", but not contains the following fields: ";
					String presentAndMissingFieldsMessage = presentFieldsAndMissingLabel + missingFields;
					errors.add(presentAndMissingFieldsMessage);
				}
			}
			return errors;
		}

		/**
		 * Not critical: the other validations go on.
		 * @param json the JSON
		 * @param clazz the validation class
		 * @return {@code false}
		 */
		public boolean isCriticalValidation(CcpJsonRepresentation json, Class<?> clazz) {
			return false;
		}

		/**
		 * Explains each group.
		 * @param clazz the validation class
		 * @return one explanation per group
		 */
		public Object getRuleExplanation(Class<?> clazz) {
			CcpJsonGlobalValidations annotation = clazz.getAnnotation(CcpJsonGlobalValidations.class);
			CcpJsonValidationFieldList[] list = annotation.requiresAllOrNone();
			List<String> rules = new ArrayList<>();
			for (CcpJsonValidationFieldList validation : list) {

				String[] oneOfThem = getItemsFromAnnotation(validation);
				String toString3 = Arrays.asList(oneOfThem).toString();
				String allOrNoneRuleStart = "The provided json must has all (or none) of this following fields: " + toString3;
				String rule = allOrNoneRuleStart + ". If provide one of them, so must provide all of them";
				rules.add(rule);
			}
			return rules;
		}};

		/**
		 * Returns the names of the constants of every enum of the group.
		 * @param validation the group
		 * @return the field names
		 */
		protected static String[] getItemsFromAnnotation(CcpJsonValidationFieldList validation) {
		
			Set<String> set = new HashSet<>();
			Class<?>[] classes = validation.value();
			CcpJsonHandler dependency = CcpDependencyInjection.getDependency(CcpJsonHandler.class);
			
			for (Class<?> class1 : classes) {
				try {
					
					Method declaredMethod = class1.getDeclaredMethod("values");
					Object[] invoke = (Object[])declaredMethod.invoke(null);
					List<String> list = dependency.fromJson(Arrays.asList(invoke).toString());
					set.addAll(list); 
				} catch (Exception e) {
					throw new RuntimeException(e);
				}
				
			}
			
			String[] array = set.toArray(new String[set.size()]);
			return array;
		}
}
