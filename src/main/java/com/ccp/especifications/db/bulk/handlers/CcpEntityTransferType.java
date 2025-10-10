package com.ccp.especifications.db.bulk.handlers;

import com.ccp.especifications.db.utils.CcpEntity;

public enum CcpEntityTransferType {
	Inactivate {
		CcpEntity getEntity(CcpEntity entity) {
			return entity;
		}
	},
	Reactivate {
		CcpEntity getEntity(CcpEntity entity) {
			CcpEntity twinEntity = entity.getTwinEntity();
			return twinEntity;
		}
	}
	;
	abstract CcpEntity getEntity(CcpEntity entity);
}
