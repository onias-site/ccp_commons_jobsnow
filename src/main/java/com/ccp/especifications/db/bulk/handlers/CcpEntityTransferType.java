package com.ccp.especifications.db.bulk.handlers;

import com.ccp.especifications.db.utils.entity.CcpEntity;

public enum CcpEntityTransferType {
	Inactivate("afterRecordBeenTransportedFromMainToTwinEntity") {
		CcpEntity getEntity(CcpEntity entity) {
			return entity;
		}
	},
	Reactivate("afterRecordBeenTransportedFromTwinToMainEntity") {
		CcpEntity getEntity(CcpEntity entity) {
			CcpEntity twinEntity = entity.getTwinEntity();
			return twinEntity;
		}
	}
	;
	
	private CcpEntityTransferType(String callBack) {
		this.callBack = callBack;
	}
	public final String callBack; 
	abstract CcpEntity getEntity(CcpEntity entity);
}
