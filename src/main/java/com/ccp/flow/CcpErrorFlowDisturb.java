package com.ccp.flow;

import com.ccp.constants.CcpOtherConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonFieldName;
import com.ccp.process.CcpProcessStatus;
import com.ccp.json.fields.validation.CcpJsonCommonsFields;

/**
 * Exceção de controle de fluxo de negócio. Não representa um erro técnico, mas sim uma saída
 * alternativa de um processo (similar a um status HTTP de erro). Carrega o status
 * ({@code CcpProcessStatus}), o JSON com dados de contexto e campos relevantes, permitindo que
 * a cadeia {@code CcpTreeFlow} capture e trate a situação de forma declarativa.
 */
@SuppressWarnings("serial")
public class CcpErrorFlowDisturb extends RuntimeException{
	
	public final CcpJsonRepresentation json;
	
	public final CcpProcessStatus status;
	
	public final CcpJsonFieldName[] fields;

	/**
	 * Cria a exceção com JSON vazio e o status informado.
	 * @param status o status do processo
	 * @param fields campos relevantes do contexto
	 */
	public CcpErrorFlowDisturb(CcpProcessStatus status, CcpJsonFieldName... fields) {
		this(CcpOtherConstants.EMPTY_JSON, status, fields);
	}

	/**
	 * Cria a exceção com JSON de contexto completo.
	 * @param json o JSON com dados de contexto
	 * @param status o status do processo
	 * @param fields campos relevantes do contexto
	 */
	public CcpErrorFlowDisturb(CcpJsonRepresentation json, CcpProcessStatus status, CcpJsonFieldName... fields) {
		super(getErrorMessage(json, status));
		this.json = json;
		this.status = status;
		this.fields = fields;
	}

	private static String getErrorMessage(CcpJsonRepresentation json, CcpProcessStatus status) {
		return json.getOrDefault(CcpJsonCommonsFields.reason, () -> json.put(CcpJsonCommonsFields.statusNumber, status.asNumber()).put(CcpJsonCommonsFields.statusName, status.name()).asPrettyJson());
	}

	/**
	 * Cria a exceção com JSON de contexto completo e mensagem personalizada.
	 * @param json o JSON com dados de contexto
	 * @param status o status do processo
	 * @param message mensagem personalizada da exceção
	 * @param fields campos relevantes do contexto
	 */
	public CcpErrorFlowDisturb(CcpJsonRepresentation json, CcpProcessStatus status, String message, CcpJsonFieldName... fields) {
		super(message);
		this.json = json;
		this.status = status;
		this.fields = fields;
	}
	
	
}
