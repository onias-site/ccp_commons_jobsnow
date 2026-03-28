package com.ccp.especifications.db.utils.entity.decorators.engine2;

import com.ccp.especifications.db.utils.entity.CcpEntity2;

public class DefaultImplementationEntity implements CcpEntity2{

	public String toString() {
		CcpEntityDetails entityDetails = this.getEntityDetails();
		return entityDetails.entityName;
	}
	
	public boolean equals(Object obj) {
		if(obj instanceof CcpEntity2 other) {
			CcpEntityDetails entityDetails = this.getEntityDetails();
			CcpEntityDetails entityDetails2 = other.getEntityDetails();
			boolean equals = entityDetails.entityClass.equals(entityDetails2.entityClass);
			return equals;
		}
		return false;
	}

	public int hashCode() {
		CcpEntityDetails entityDetails = this.getEntityDetails();
		int hashCode = entityDetails.entityClass.hashCode();
		return hashCode;
	}
	
	
}
