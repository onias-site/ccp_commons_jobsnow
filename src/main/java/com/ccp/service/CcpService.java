package com.ccp.service;

import java.util.Map;

import com.ccp.decorators.CcpErrorJsonInvalid;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.business.CcpBusiness;
import com.ccp.json.validations.global.engine.CcpJsonValidatorEngine;

/**
 * Especialização de {@code CcpBusiness} para serviços expostos externamente (ex.: endpoints REST).
 * Adiciona a obrigatoriedade de nome ({@code name()}) e validação de JSON de entrada antes da execução,
 * além de converter {@code Map<String, Object>} para {@code CcpJsonRepresentation} automaticamente.
 */
public interface CcpService extends CcpBusiness {

	/** Retorna a classe onde estão as anotações de validação do JSON de entrada para este serviço. */
	Class<?> getJsonValidationClass();

	/** Retorna o nome identificador do serviço (usado em logs e mensagens de validação). */
	String name();
	
	/**
	 * Converte o mapa em {@code CcpJsonRepresentation}, valida via {@code CcpJsonValidatorEngine},
	 * executa {@code apply()} e retorna o mapa de saída.
	 * Lança {@code CcpServiceJsonValidationError} se o JSON gerado for inválido durante o processamento.
	 * @param map o mapa de entrada
	 * @return o mapa resultante da execução
	 */
	default Map<String, Object> execute(Map<String, Object> map){
		CcpJsonRepresentation json = new CcpJsonRepresentation(map);
		Class<?> jsonValidationClass = this.getJsonValidationClass();
		String name = this.name();
		CcpJsonValidatorEngine.INSTANCE.validateJson(jsonValidationClass, json, name);
		try {
			CcpJsonRepresentation apply = this.apply(json);
			return apply.content;
		} catch (CcpErrorJsonInvalid e) {
			throw new CcpServiceJsonValidationError(e);
		}
	}
}
