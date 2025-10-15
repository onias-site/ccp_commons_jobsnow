package com.ccp.especifications.db.bulk;

import java.util.List;

import com.ccp.decorators.CcpJsonRepresentation;

@SuppressWarnings("serial")
public class CcpBulkErrors extends RuntimeException{

	public CcpBulkErrors(List<CcpJsonRepresentation> failedRecords) {
		super(failedRecords.toString());
	}
	
}
