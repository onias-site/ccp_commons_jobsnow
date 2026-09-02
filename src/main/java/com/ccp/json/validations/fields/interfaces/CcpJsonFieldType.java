package com.ccp.json.validations.fields.interfaces;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import com.ccp.decorators.CcpFieldName;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.json.validations.fields.enums.CcpJsonFieldError;
import com.ccp.json.validations.fields.enums.CcpJsonFieldsValidationContext;

/**
 * Contrato central para tipos de campo de validação JSON. Orquestra verificação de tipo,
 * acumulação de erros e geração de explicações de regras.
 */
public interface CcpJsonFieldType {

	/**
	 * Verifica se o valor do campo é compatível com o tipo esperado.
	 * @param fieldName nome do campo a verificar
	 * @return predicado que testa compatibilidade de tipo
	 */
	abstract Predicate<CcpJsonRepresentation> evaluateCompatibleType(String fieldName);

	
	/**
	 * Executa validações e retorna se há erros para o campo.
	 * @param json JSON sendo validado
	 * @param field campo a validar
	 * @param context contexto de validação (single ou collection)
	 * @return true se houver erros
	 */
	default boolean hasErrors(CcpJsonRepresentation json, Field field, CcpJsonFieldsValidationContext context) {
		java.lang.String fieldName = field.getName();
		CcpFieldName ccpFieldName = new CcpFieldName(fieldName);
		boolean containsAllFields = json.containsAllFields(ccpFieldName);

		boolean thisFieldIsAbsent = false == containsAllFields;
		
		if(thisFieldIsAbsent) {
			return false;
		}
		List<CcpJsonFieldValidatorInterface> validations = this.getAllValidations(field);
		for (CcpJsonFieldValidatorInterface validation : validations) {
			boolean validValidationContext = validation.isValidValidationContext(context);
		
			boolean isNotValidValidationContext = false == validValidationContext;
			
			if(isNotValidValidationContext) {
				continue;
			}
			boolean ruleExplanation2 = validation.hasRuleExplanation(field, this);

			boolean hasNoRules = false == ruleExplanation2;
			
			if(hasNoRules) {
				continue;
			}
			
			boolean hasError = validation.hasError(json, field, this);
			if(hasError) {
				return true;
			}
		}
		return false;
	}
	
	/**
	 * Acumula erros de validação no JSON de erros.
	 * @param errors JSON de erros acumulados
	 * @param json JSON sendo validado
	 * @param field campo a validar
	 * @param context contexto de validação
	 * @return JSON de erros atualizado
	 */
	default CcpJsonRepresentation getErrors(CcpJsonRepresentation errors, CcpJsonRepresentation json, Field field, CcpJsonFieldsValidationContext context) {

		java.lang.String fieldName = field.getName();
		CcpFieldName ccpFieldName2 = new CcpFieldName(fieldName);
		boolean containsAllFields2 = json.containsAllFields(ccpFieldName2);

		boolean fieldIsNotPresent = false == containsAllFields2;
		if(fieldIsNotPresent) {
			return errors;
		}

		List<CcpJsonFieldValidatorInterface> validations = this.getAllValidations(field);
		
		for (CcpJsonFieldValidatorInterface errorType : validations) {
			boolean validValidationContext2 = errorType.isValidValidationContext(context);
			boolean isInvalidContextValidation = false == validValidationContext2;
			
			if(isInvalidContextValidation) {
				continue;
			}
			boolean ruleExplanation3 = this.hasRuleExplanation(field);

			boolean hasNoRules = false == ruleExplanation3;
			if(hasNoRules) {
				continue;
			}
			boolean error = errorType.hasError(json, field, this);

			boolean hasNoErrors = false == error;
			if(hasNoErrors) {
				continue;
			}
			errors = errorType.getErrors(errors, json, field, this);
		}
		
		return errors;
	}

	/**
	 * Adiciona explicações de regras ao JSON de regras.
	 * @param ruleExplanation JSON de explicações de regras
	 * @param field campo sendo documentado
	 * @return JSON de regras atualizado
	 */
	default CcpJsonRepresentation updateRuleExplanation(CcpJsonRepresentation ruleExplanation, Field field) {
		boolean ruleExplanation4 = this.hasRuleExplanation(field);
		boolean hasNoRulesExplanations = false == ruleExplanation4;
		if(hasNoRulesExplanations) {
			return ruleExplanation;
		}
		List<CcpJsonFieldValidatorInterface> validations = this.getAllValidations(field);
		
		for (CcpJsonFieldValidatorInterface errorType : validations) {
			ruleExplanation = errorType.updateRuleExplanation(ruleExplanation, field, this);
		}
		return ruleExplanation;
	}

	private List<CcpJsonFieldValidatorInterface> getAllValidations(Field field) {
		
		List<CcpJsonFieldValidatorInterface> validations = this.getDefaultValidations();
		CcpJsonFieldValidatorInterface[] errorTypes2 = this.getErrorTypes();
		List<CcpJsonFieldValidatorInterface> errorTypes = Arrays.asList(errorTypes2);
		validations.addAll(errorTypes);
		
		return validations;
	}

	/**
	 * Retorna os validadores padrão: {@code incompatibleType} e {@code validateCollectionOrSigleValue}.
	 * @return lista de validadores padrão
	 */
	default List<CcpJsonFieldValidatorInterface> getDefaultValidations(){
		List<CcpJsonFieldValidatorInterface> asList = new ArrayList<>(Arrays.asList(CcpJsonFieldError.incompatibleType, CcpJsonFieldError.validateCollectionOrSigleValue));
		return asList;
	}
	/**
	 * Retorna os validadores específicos do tipo.
	 * @return array de validadores específicos
	 */
	CcpJsonFieldValidatorInterface[] getErrorTypes();

	/**
	 * Indica se há regras ativas para o campo.
	 * @param field campo a verificar
	 * @return true se houver regras de validação ativas
	 */
	abstract boolean hasRuleExplanation(Field field);

	/**
	 * Nome do tipo de campo.
	 * @return nome do tipo
	 */
	String name();

}
