package com.ccp.json.validations.fields.interfaces;

import java.lang.reflect.Field;

import com.ccp.constants.CcpOtherConstants;
import com.ccp.decorators.CcpFieldName;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonFieldName;
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
		boolean error2 = this.hasError(json, field, type);

		boolean hasNoError = false == error2;

		if (hasNoError) {
			return errors;
		}

		String fieldName = field.getName();
		
		Object error = this.getError(json, field, type);
		String name = this.name();
		CcpJsonRepresentation put = CcpOtherConstants.EMPTY_JSON
				.put(CcpErrorFields.errorName, name);

				CcpJsonRepresentation errorObject = put
				.put(CcpErrorFields.errorDescription, error);
				CcpFieldName ccpFieldName = new CcpFieldName(fieldName);

				CcpJsonRepresentation updatedErrors = errors.addToList(ccpFieldName, errorObject);

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
		boolean ruleExplanation2 = this.hasRuleExplanation(field, type);

		boolean hasNoRules = false == ruleExplanation2;
		
		if(hasNoRules) {
			return allRules;
		}
		
		String fieldName = field.getName();
		
		Object ruleExplanation = this.getRuleExplanation(field, type);
		String toString = ruleExplanation.toString();
		String toStringTrim = toString.trim();
		boolean toStringTrimEmpty = toStringTrim.isEmpty();
		if(toStringTrimEmpty) {
			return allRules;
		}
		String name2 = this.name();
		CcpJsonRepresentation put2 = CcpOtherConstants.EMPTY_JSON
				.put(RuleFields.ruleName, name2);
				CcpJsonRepresentation rule = put2
				.put(RuleFields.ruleDescription, ruleExplanation);
				CcpFieldName ccpFieldName2 = new CcpFieldName(fieldName);
				CcpJsonRepresentation updatedRuleExplanation = allRules.addToList(ccpFieldName2, rule);
		
		return updatedRuleExplanation;
	}
	
	boolean hasRuleExplanation(Field field, CcpJsonFieldType type) ;
	enum RuleFields implements CcpJsonFieldName{
		ruleName, ruleDescription
		;
	}
	public static enum CcpErrorFields implements CcpJsonFieldName{
		errorName, errorDescription
		;
	}
}
