package com.ccp.json.validations.fields.interfaces;

import java.lang.reflect.Field;

import com.ccp.constants.CcpOtherConstants;
import com.ccp.decorators.CcpFieldName;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonRepresentation.CcpJsonFieldName;
import com.ccp.json.validations.fields.enums.CcpJsonFieldErrorHandleType;
import com.ccp.json.validations.fields.enums.CcpJsonFieldsValidationContext;

/**
 * Contrato para validadores individuais de campo. Define verificação de erro, mensagem,
 * explicação de regra e acumulação de erros em JSON.
 */
public interface CcpJsonFieldValidatorInterface {

	/**
	 * Indica se o validador é aplicável ao contexto de validação fornecido.
	 * @param context contexto de validação (single ou collection)
	 * @return true se aplicável (padrão: sempre)
	 */
	default boolean isValidValidationContext(CcpJsonFieldsValidationContext context) {
		return true;
	}

	/**
	 * Estratégia de tratamento ao encontrar erro.
	 * @return tipo de tratamento de erro
	 */
	CcpJsonFieldErrorHandleType getErrorHandleType() ;

	/**
	 * Identificador do validador.
	 * @return nome do validador
	 */
	String name();

	/**
	 * Mensagem descritiva do erro encontrado.
	 * @param json JSON sendo validado
	 * @param field campo com erro
	 * @param type tipo de campo
	 * @return mensagem de erro
	 */
	String getErrorMessage(CcpJsonRepresentation json, Field field, CcpJsonFieldType type);

	/**
	 * Verifica ocorrência do erro para o campo.
	 * @param json JSON sendo validado
	 * @param field campo a verificar
	 * @param type tipo de campo
	 * @return true se houver erro
	 */
	boolean hasError(CcpJsonRepresentation json, Field field, CcpJsonFieldType type);

	/**
	 * Explica a regra em linguagem natural.
	 * @param field campo sendo documentado
	 * @param type tipo de campo
	 * @return explicação da regra
	 */
	Object getRuleExplanation(Field field, CcpJsonFieldType type);

	/**
	 * Retorna o objeto de erro para o campo.
	 * @param json JSON sendo validado
	 * @param field campo com erro
	 * @param type tipo de campo
	 * @return objeto de erro
	 */
	default Object getError(CcpJsonRepresentation json, Field field, CcpJsonFieldType type) {
		String errorMessage = this.getErrorMessage(json, field, type);
		return errorMessage;
	}
	
	/**
	 * Adiciona erro ao JSON de erros e aplica estratégia de tratamento.
	 * @param errors JSON de erros acumulados
	 * @param json JSON sendo validado
	 * @param field campo com erro
	 * @param type tipo de campo
	 * @return JSON de erros atualizado
	 */
	default CcpJsonRepresentation getErrors(CcpJsonRepresentation errors, CcpJsonRepresentation json,  Field field, CcpJsonFieldType type) {

		boolean hasNoError = false == this.hasError(json, field, type);

		if (hasNoError) {
			return errors;
		}

		String fieldName = field.getName();
		
		Object error = this.getError(json, field, type);
		
		CcpJsonRepresentation errorObject = CcpOtherConstants.EMPTY_JSON
				.put(ErrorFields.errorName, this.name())
				.put(ErrorFields.errorDescription, error);

		CcpJsonRepresentation updatedErrors = errors.addToList(new CcpFieldName(fieldName), errorObject);

		CcpJsonFieldErrorHandleType errorHandleType = this.getErrorHandleType();
		
		errorHandleType.maybeBreakValidation(updatedErrors);
		
		return updatedErrors;
	}
	
	/**
	 * Adiciona explicação desta regra ao JSON de regras.
	 * @param allRules JSON de todas as regras acumuladas
	 * @param field campo sendo documentado
	 * @param type tipo de campo
	 * @return JSON de regras atualizado
	 */
	default CcpJsonRepresentation updateRuleExplanation(CcpJsonRepresentation allRules, Field field, CcpJsonFieldType type) {

		boolean hasNoRules = false == this.hasRuleExplanation(field, type);
		
		if(hasNoRules) {
			return allRules;
		}
		
		String fieldName = field.getName();
		
		Object ruleExplanation = this.getRuleExplanation(field, type);
		if(ruleExplanation.toString().trim().isEmpty()) {
			return allRules;
		}
		CcpJsonRepresentation rule = CcpOtherConstants.EMPTY_JSON
				.put(RuleFields.ruleName, this.name())
				.put(RuleFields.ruleDescription, ruleExplanation);
		CcpJsonRepresentation updatedRuleExplanation = allRules.addToList(new CcpFieldName(fieldName), rule);
		
		return updatedRuleExplanation;
	}
	
	boolean hasRuleExplanation(Field field, CcpJsonFieldType type) ;
	enum RuleFields implements CcpJsonFieldName{
		ruleName, ruleDescription
		;
	}
	enum ErrorFields implements CcpJsonFieldName{
		errorName, errorDescription
		;
	}
}
