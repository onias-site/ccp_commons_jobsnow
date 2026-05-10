package com.ccp.especifications.db.utils.entity.decorators.engine;

import com.ccp.especifications.db.utils.entity.CcpEntity;

class DefaultImplementationEntity implements CcpEntity{
	final CcpEntityMetaData entityDetails;
	
	public DefaultImplementationEntity(CcpEntityMetaData entityDetails) {
		this.entityDetails = entityDetails;
	}

	public String toString() {
		CcpEntityMetaData entityDetails = this.getEntityMetaData();
		return entityDetails.entityName;
	}
	
	public boolean equals(Object obj) {
		if(obj instanceof CcpEntity other) {
			CcpEntityMetaData entityDetails = this.getEntityMetaData();
			CcpEntityMetaData entityDetails2 = other.getEntityMetaData();
			boolean equals = entityDetails.entityName.equals(entityDetails2.entityName);
			return equals;
		}
		return false;
	}

	public int hashCode() {
		CcpEntityMetaData entityDetails = this.getEntityMetaData();
		int hashCode = entityDetails.entityName.hashCode();
		return hashCode;
	}

	public CcpEntityMetaData getEntityMetaData() {
		CcpEntityMetaData addEntity = this.entityDetails.associateEntity();
		return addEntity;
	}
}
