package com.ccp.json.transformers;

import java.util.Arrays;

import com.ccp.business.CcpBusiness;
import com.ccp.decorators.CcpFieldName;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpStringDecorator;

/**
 * Interface utilitária de transformações de campos JSON. Estende {@code CcpBusiness} e fornece
 * métodos default para truncar textos, garantir valores mínimos, adicionar valores long e exigir
 * ao menos um campo de um grupo. Usada como base para implementações de transformadores de entidade.
 */
public interface CcpTransformers extends CcpBusiness {

	
	/** Trunca o valor do campo {@code field} para no máximo {@code limit} caracteres. */
	default CcpJsonRepresentation substring(CcpJsonRepresentation json, String field, int limit) {
		CcpFieldName ccpFieldName = new CcpFieldName(field);
		String value = json.getAsString(ccpFieldName);
		String valueTrim = value.trim();
		int valueTrimLength = valueTrim.length();
		boolean isValid = valueTrimLength <= limit;

		if (isValid) {
			return json;
		}

		String substring = value.substring(0, limit);
		CcpFieldName ccpFieldName2 = new CcpFieldName(field);
		CcpJsonRepresentation put = json.put(ccpFieldName2, substring);
		return put;
	}

	/** Garante que o campo numérico {@code field} seja ao menos {@code minValue}. */
	default CcpJsonRepresentation putMinValue(CcpJsonRepresentation json, String field, int minValue) {
		CcpFieldName ccpFieldName3 = new CcpFieldName(field);
		boolean containsAllFields = json.containsAllFields(ccpFieldName3);
		boolean isNotPresent = false == containsAllFields;
		if(isNotPresent) {
			return json;
		}
		CcpFieldName ccpFieldName4 = new CcpFieldName(field);

		Double value = json.getAsDoubleNumber(ccpFieldName4);
		boolean valueMaiorOuIgual = value >= minValue;

		if(valueMaiorOuIgual) {
			return json;
		}
		CcpFieldName ccpFieldName5 = new CcpFieldName(field);

		CcpJsonRepresentation put = json.put(ccpFieldName5, minValue);
		return put;
	}

	/** Adiciona {@code longValue} ao campo se o valor atual não for um número long válido. */
	default CcpJsonRepresentation addLongValue(CcpJsonRepresentation json, String field, Long longValue) {
		CcpFieldName ccpFieldName6 = new CcpFieldName(field);
		String value = json.getAsString(ccpFieldName6);
		CcpStringDecorator ccpStringDecorator = new CcpStringDecorator(value);

		boolean isLongNumber = ccpStringDecorator.isLongNumber();

		if(isLongNumber) {
			return json;
		}
		CcpFieldName ccpFieldName7 = new CcpFieldName(field);
		CcpJsonRepresentation put = json.put(ccpFieldName7, longValue);
		return put;

	}

	/** Adiciona o par {@code field}/{@code value} ao JSON caso nenhum dos {@code fields} esteja presente. */
	default CcpJsonRepresentation addRequiredAtLeastOne(CcpJsonRepresentation json, String field, Object value, String... fields) {
		boolean containsAnyFields = json.containsAnyFields(Arrays.asList(fields));
		if(containsAnyFields) {
			return json;
		}
		CcpFieldName ccpFieldName8 = new CcpFieldName(field);

		CcpJsonRepresentation put = json.put(ccpFieldName8, value);
		return put;
	}

}
