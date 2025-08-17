package com.ccp.especifications.db.bulk;

import java.util.List;

import com.ccp.decorators.CcpJsonRepresentation;

@SuppressWarnings("serial")
public class CcpDbBulkErrors extends RuntimeException{

	public CcpDbBulkErrors(List<CcpJsonRepresentation> failedRecords) {
		super(failedRecords.toString());
	}
	
}
