package com.ccp.especifications.db.crud;

import com.ccp.business.CcpBusiness;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonFieldName;
import com.ccp.process.CcpProcessStatus;

import com.ccp.json.fields.validation.CcpJsonCommonsFields;

/**
 * Business that replaces the {@code status} field of a statement, which carries a {@code CcpProcessStatus}, with
 * {@code statusName} and {@code statusNumber}, to describe the flow of a search in error messages.
 */
class FunctionPutStatus implements CcpBusiness {
	/** Fields of a statement. */
	enum JsonFieldNames implements CcpJsonFieldName{
		/** The status of the statement. */
		status
	}

	/** The single instance. */
	public static final FunctionPutStatus INSTANCE = new FunctionPutStatus();

	/** Singleton. */
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
