package com.ccp.exceptions.db.bulk;

import java.util.List;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.bulk.CcpBulkItem;

@SuppressWarnings("serial")
public class CcpErrorDbBulkItemNotFound extends RuntimeException {
	
	public CcpErrorDbBulkItemNotFound(CcpBulkItem bulkItem) {
		super( String.format("Id '%s' from entity '%s' not found.", bulkItem.id, bulkItem.entity.getEntityName()));
	}

	public CcpErrorDbBulkItemNotFound(CcpBulkItem bulkItem, List<CcpJsonRepresentation> result) {
		super( String.format("Id '%s' from entity '%s' not found. Complete list: " + result, bulkItem.id, bulkItem.entity.getEntityName()));
	}
	
}
