package com.ccp.especifications.db.bulk;

import java.util.List;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.utils.entity.CcpEntity;

public interface CcpBulkExecutor {
	
	CcpBulkExecutor clearRecords();
	
	default CcpBulkExecutor addRecord(CcpJsonRepresentation json, CcpBulkEntityOperationType operation, CcpEntity entity) {
		
		List<CcpBulkItem> bulkItems =  entity.toBulkItems(json, operation);
		
		CcpBulkExecutor addRecord = this;
		
		for (CcpBulkItem bulkItem : bulkItems) {
			addRecord = this.addRecord(bulkItem);
		}
		
		return addRecord;
	}
	
	CcpBulkExecutor addRecord(CcpBulkItem bulkItem);

	default CcpBulkExecutor addRecords(List<CcpBulkItem> items) {
		CcpBulkExecutor bulk = this;
		for (CcpBulkItem item : items) {
			bulk = bulk.addRecord(item);
		}
		return bulk;
	}
	
	default CcpBulkExecutor addRecords(List<CcpJsonRepresentation> records, CcpBulkEntityOperationType operation, CcpEntity entity) {
		CcpBulkExecutor bulk = this;
		for (CcpJsonRepresentation _record : records) {
			bulk = bulk.addRecord(_record, operation, entity);
		}
		return bulk;
	}
	
	List<CcpBulkOperationResult> getBulkOperationResult();
	
}
