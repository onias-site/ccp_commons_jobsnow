package com.ccp.especifications.db.crud;

import com.ccp.business.CcpBusiness;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonFieldName;
import com.ccp.process.CcpProcessStatus;

import com.ccp.json.fields.validation.CcpJsonCommonsFields;

class FunctionPutStatus implements CcpBusiness {
	enum JsonFieldNames implements CcpJsonFieldName{
		status
	}

	public static final FunctionPutStatus INSTANCE = new FunctionPutStatus();

	private FunctionPutStatus() {}

	/**
	 * Lê o {@code CcpProcessStatus} do campo {@code status}, adiciona {@code statusName} e {@code statusNumber}
	 * ao JSON e remove o campo {@code status} original.
	 */
	public CcpJsonRepresentation apply(CcpJsonRepresentation j) {
		CcpProcessStatus stats = j.getAsObject(JsonFieldNames.status);
		String statsName = stats.name();
		CcpJsonRepresentation put = j.put(CcpJsonCommonsFields.statusName, statsName);
		int asNumber = stats.asNumber();
		CcpJsonRepresentation put3 = put
				.put(CcpJsonCommonsFields.statusNumber, asNumber);
		CcpJsonRepresentation removeField = put3.removeFields(JsonFieldNames.status);
		return removeField;

	}

}
