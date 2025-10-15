package com.ccp.especifications.db.bulk;

import java.util.List;

import com.ccp.decorators.CcpJsonRepresentation;

@SuppressWarnings("serial")
public class CcpErrorBulkItemNotFound extends RuntimeException {
	
	public CcpErrorBulkItemNotFound(CcpBulkItem bulkItem) {
		super( String.format("Id '%s' from entity '%s' not found.", bulkItem.id, bulkItem.entity.getEntityName()));
	}

	public CcpErrorBulkItemNotFound(CcpBulkItem bulkItem, List<CcpJsonRepresentation> result) {
		super( String.format("Id '%s' from entity '%s' not found. Complete list: " + result, bulkItem.id, bulkItem.entity.getEntityName()));
	}
	
}
