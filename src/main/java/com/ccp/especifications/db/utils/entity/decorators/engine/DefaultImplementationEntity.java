package com.ccp.especifications.db.utils.entity.decorators.engine;

import com.ccp.especifications.db.utils.entity.CcpEntity;

class DefaultImplementationEntity implements CcpEntity{
	final CcpEntityDetails entityDetails;
	
	public DefaultImplementationEntity(CcpEntityDetails entityDetails) {
		this.entityDetails = entityDetails;
	}

	public String toString() {
		CcpEntityDetails entityDetails = this.getEntityDetails();
		return entityDetails.entityName;
	}
	
	public boolean equals(Object obj) {
		if(obj instanceof CcpEntity other) {
			CcpEntityDetails entityDetails = this.getEntityDetails();
			CcpEntityDetails entityDetails2 = other.getEntityDetails();
			boolean equals = entityDetails.entityName.equals(entityDetails2.entityName);
			return equals;
		}
		return false;
	}

	public int hashCode() {
		CcpEntityDetails entityDetails = this.getEntityDetails();
		int hashCode = entityDetails.entityName.hashCode();
		return hashCode;
	}

	public CcpEntityDetails getEntityDetails() {
		CcpEntityDetails addEntity = this.entityDetails.associateEntity();
		return addEntity;
	}
}
