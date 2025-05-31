package com.ccp.especifications.mensageria.receiver;

import java.util.List;

import com.ccp.especifications.db.bulk.CcpBulkItem;
import com.ccp.especifications.db.bulk.handlers.CcpBulkHandlerDelete;
import com.ccp.especifications.db.bulk.handlers.CcpBulkHandlerSave;
import com.ccp.especifications.db.bulk.handlers.CcpBulkHandlerSaveTwin;
import com.ccp.especifications.db.bulk.handlers.CcpEntityBulkHandlerTransferRecordToReverseEntity;
import com.ccp.especifications.db.crud.CcpHandleWithSearchResultsInTheEntity;
import com.ccp.especifications.db.utils.CcpEntity;

public enum CcpBulkHandlers {

	save{
		public CcpHandleWithSearchResultsInTheEntity<List<CcpBulkItem>> getBulkHandler(CcpEntity entity) {
			return new CcpBulkHandlerSave(entity);
		}
	},
	delete{
		public CcpHandleWithSearchResultsInTheEntity<List<CcpBulkItem>> getBulkHandler(CcpEntity entity) {
			return new CcpBulkHandlerDelete(entity);
		}
	},
	saveTwin{
		public CcpHandleWithSearchResultsInTheEntity<List<CcpBulkItem>> getBulkHandler(CcpEntity entity) {
			return new CcpBulkHandlerSaveTwin(entity);
		}
	},
	transferToReverseEntity{
		public CcpHandleWithSearchResultsInTheEntity<List<CcpBulkItem>> getBulkHandler(CcpEntity entity) {
			CcpEntityBulkHandlerTransferRecordToReverseEntity transferRecordToReverseEntity = entity.getTransferRecordToReverseEntity();
			return transferRecordToReverseEntity;
		}
		
	}
	;
	public abstract CcpHandleWithSearchResultsInTheEntity<List<CcpBulkItem>> getBulkHandler(CcpEntity entity);
}
