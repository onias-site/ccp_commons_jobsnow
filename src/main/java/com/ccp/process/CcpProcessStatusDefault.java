package com.ccp.process;

import com.ccp.decorators.CcpFieldName;
import com.ccp.decorators.CcpJsonRepresentation.CcpJsonFieldName;

/**
 * Conjunto padrão de status de processo HTTP-like usados em todo o sistema jobsnow.
 * Implementa {@code CcpProcessStatus} e fornece os códigos mais comuns:
 * {@code OK(200)}, {@code CREATED(201)}, {@code UPDATED(204)}, {@code REDIRECT(301)},
 * {@code INACTIVE_RECORD(302)}, {@code BAD_REQUEST(400)}, {@code UNHAUTHORIZED(401)},
 * {@code NOT_FOUND(404)}, {@code CONFLICT(409)}, {@code UNPROCESSABLE_ENTITY(422)}.
 */
public enum CcpProcessStatusDefault implements CcpProcessStatus{

	INACTIVE_RECORD(302),
	UNHAUTHORIZED(401),
	BAD_REQUEST(400),
	NOT_FOUND(404),
	CONFLICT(409),
	REDIRECT(301),
	OK(200),
	CREATED(201),
	UPDATED(204),
	UNPROCESSABLE_ENTITY(422)
	;

	/** Retorna o código HTTP associado ao status. */
	public int asNumber() {
		return this.status;
	}
	
	
	
	private CcpProcessStatusDefault(int status) {
		this.status = status;
	}


	/**
	 * Retorna um {@code CcpJsonFieldName} cujo valor é o código numérico como string (ex.: {@code "200"}),
	 * permitindo uso como chave de campo JSON.
	 */
	public CcpJsonFieldName asJsonFieldName() {
		CcpJsonFieldName ccpJsonFieldName = new CcpFieldName(this.status);
		return ccpJsonFieldName; 
	}
	
	private final int status;
}
