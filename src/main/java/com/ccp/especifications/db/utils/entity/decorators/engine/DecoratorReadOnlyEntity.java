package com.ccp.especifications.db.utils.entity.decorators.engine;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.utils.entity.CcpEntity;

/**
 * Decorator que impede qualquer operação de escrita em entidades marcadas com
 * {@code @CcpEntityOlyReadable}. Os métodos {@code save}, {@code delete}, {@code deleteAnyWhere}
 * e {@code transferDataTo} lançam exceção ao serem chamados.
 */
class DecoratorReadOnlyEntity extends CcpEntityDelegator {
	
	public DecoratorReadOnlyEntity(CcpEntity entity, Class<?> clazz) {
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
