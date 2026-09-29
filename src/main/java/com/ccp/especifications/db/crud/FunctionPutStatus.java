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
	 * Reads the {@code CcpProcessStatus} from the {@code status} field, adds {@code statusName} and {@code statusNumber}
	 * to the JSON and removes the original {@code status} field.
	 */
	public CcpJsonRepresentation apply(CcpJsonRepresentation statement) {
		CcpProcessStatus status = statement.getAsObject(JsonFieldNames.status);
		String statusName = status.name();
		CcpJsonRepresentation statementWithStatusName = statement.put(CcpJsonCommonsFields.statusName, statusName);
		int asNumber = status.asNumber();
		CcpJsonRepresentation statementWithStatusNumber = statementWithStatusName
				.put(CcpJsonCommonsFields.statusNumber, asNumber);
		CcpJsonRepresentation statementWithoutStatus = statementWithStatusNumber.removeFields(JsonFieldNames.status);
		return statementWithoutStatus;

	}

}
