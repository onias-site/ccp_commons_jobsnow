package com.ccp.especifications.db.crud;

import com.ccp.business.CcpBusiness;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonRepresentation.CcpJsonFieldName;
import com.ccp.process.CcpProcessStatus;

/**
 * Business interno (package-private) que converte o campo {@code status} — que contém um {@code CcpProcessStatus}
 * — em dois campos textuais: {@code statusName} (nome do enum) e {@code statusNumber} (valor numérico).
 * Remove o campo original {@code status} do resultado.
 */
class FunctionPutStatus implements CcpBusiness {
	enum JsonFieldNames implements CcpJsonFieldName{
		status, statusName, statusNumber
	}

	public static final FunctionPutStatus INSTANCE = new FunctionPutStatus();

	private FunctionPutStatus() {}

	/**
	 * Lê o {@code CcpProcessStatus} do campo {@code status}, adiciona {@code statusName} e {@code statusNumber}
	 * ao JSON e remove o campo {@code status} original.
	 */
	public CcpJsonRepresentation apply(CcpJsonRepresentation j) {
		CcpProcessStatus stats = j.getAsObject(JsonFieldNames.status);
		CcpJsonRepresentation put3 = j.put(JsonFieldNames.statusName, stats.name())
				.put(JsonFieldNames.statusNumber, stats.asNumber());
		CcpJsonRepresentation removeField = put3.removeFields(JsonFieldNames.status);
		return removeField;

	}

}
