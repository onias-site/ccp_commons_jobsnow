package com.ccp.especifications.db.utils.entity.decorators.engine2;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.utils.entity.CcpEntity2;

class DecoratorReadOnlyEntity extends CcpEntityDelegator {
	
	public DecoratorReadOnlyEntity(CcpEntity2 entity) {
		super(entity);
	}
	
	public CcpJsonRepresentation delete(CcpJsonRepresentation json) {
		CcpJsonRepresentation throwException = this.throwException();
		return throwException;
	}
	
	public CcpJsonRepresentation deleteAnyWhere(CcpJsonRepresentation json) {
		CcpJsonRepresentation throwException = this.throwException();
		return throwException;
	}
	

	public CcpJsonRepresentation save(CcpJsonRepresentation json) {
		CcpJsonRepresentation throwException = this.throwException();
		return throwException;
	}

}
