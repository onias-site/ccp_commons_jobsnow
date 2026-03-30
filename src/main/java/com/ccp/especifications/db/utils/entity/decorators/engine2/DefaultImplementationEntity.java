package com.ccp.especifications.db.utils.entity.decorators.engine2;

import com.ccp.especifications.db.utils.entity.CcpEntity2;

class DefaultImplementationEntity implements CcpEntity2{
	final CcpEntityDetails entityDetails;
	
	public DefaultImplementationEntity(CcpEntityDetails entityDetails) {
		this.entityDetails = entityDetails;
	}

	public String toString() {
		CcpEntityDetails entityDetails = this.getEntityDetails();
		return entityDetails.entityName;
	}
	
	public boolean equals(Object obj) {
		if(obj instanceof CcpEntity2 other) {
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

	public int getDecoratorPriority() {
		return 0;
	}

	public CcpEntityDetails getEntityDetails() {
		CcpEntityDetails addEntity = this.entityDetails.associateEntity();
		return addEntity;
	}
	
	
}
