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
		String value = json.getAsString(new CcpFieldName(field));
		boolean isValid = value.trim().length() <= limit;

		if (isValid) {
			return json;
		}

		String substring = value.substring(0, limit);
		CcpJsonRepresentation put = json.put(new CcpFieldName(field), substring);
		return put;
	}

	/** Garante que o campo numérico {@code field} seja ao menos {@code minValue}. */
	default CcpJsonRepresentation putMinValue(CcpJsonRepresentation json, String field, int minValue) {
		boolean isNotPresent = false == json.containsAllFields(new CcpFieldName(field));
		if(isNotPresent) {
			return json;
		}

		Double value = json.getAsDoubleNumber(new CcpFieldName(field));

		if(value >= minValue) {
			return json;
		}

		CcpJsonRepresentation put = json.put(new CcpFieldName(field), minValue);
		return put;
	}

	/** Adiciona {@code longValue} ao campo se o valor atual não for um número long válido. */
	default CcpJsonRepresentation addLongValue(CcpJsonRepresentation json, String field, Long longValue) {
		String value = json.getAsString(new CcpFieldName(field));

		boolean isLongNumber = new CcpStringDecorator(value).isLongNumber();

		if(isLongNumber) {
			return json;
		}
		CcpJsonRepresentation put = json.put(new CcpFieldName(field), longValue);
		return put;

	}

	/** Adiciona o par {@code field}/{@code value} ao JSON caso nenhum dos {@code fields} esteja presente. */
	default CcpJsonRepresentation addRequiredAtLeastOne(CcpJsonRepresentation json, String field, Object value, String... fields) {
		boolean containsAnyFields = json.containsAnyFields(Arrays.asList(fields));
		if(containsAnyFields) {
			return json;
		}

		CcpJsonRepresentation put = json.put(new CcpFieldName(field), value);
		return put;
	}

}
