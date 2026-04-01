package com.ccp.especifications.db.utils.entity.decorators.engine;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.utils.entity.CcpEntity;

class DecoratorReadOnlyEntity extends CcpEntityDelegator {
	
	public DecoratorReadOnlyEntity(CcpEntity entity) {
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

	public CcpJsonRepresentation transferDataTo(CcpJsonRepresentation json, CcpEntity... entities) {
		CcpJsonRepresentation throwException = this.throwException();
		return throwException;
	}
}
