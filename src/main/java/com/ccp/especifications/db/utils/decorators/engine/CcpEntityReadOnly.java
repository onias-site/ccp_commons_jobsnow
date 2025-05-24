package com.ccp.especifications.db.utils.decorators.engine;

import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.utils.CcpEntity;
import com.ccp.especifications.db.utils.CcpEntityCrudOperationType;

public final class CcpEntityReadOnly extends CcpEntityDelegator  implements CcpEntityDecoratorFactory {

	protected CcpEntityReadOnly(CcpEntity entity) {
		super(entity);
	}

	private CcpEntityReadOnly() {
		super(null);
	}
	
	public boolean create(CcpJsonRepresentation json) {
		throw new UnsupportedOperationException("The entity '" + this.entity.getEntityName() + "' is just to read only");
	}

	public CcpJsonRepresentation createOrUpdate(CcpJsonRepresentation json, String id) {
		throw new UnsupportedOperationException("The entity '" + this.entity.getEntityName() + "' is just to read only");
	}
	
	public CcpJsonRepresentation createOrUpdate(CcpJsonRepresentation json) {
		throw new UnsupportedOperationException("The entity '" + this.entity.getEntityName() + "' is just to read only");
	}
	
	public CcpJsonRepresentation delete(CcpJsonRepresentation json) {
		throw new UnsupportedOperationException("The entity '" + this.entity.getEntityName() + "' is just to read only");
	}

	public boolean delete(String id) {
		throw new UnsupportedOperationException("The entity '" + this.entity.getEntityName() + "' is just to read only");
	}
	
	
	
	public Function<CcpJsonRepresentation, CcpJsonRepresentation> getOperationCallback(CcpEntityCrudOperationType operation){
		return json -> operation.execute(this, json);
	}

	
	public CcpEntity getEntity(CcpEntity entity) {
		CcpEntityReadOnly jnEntityVersionable = new CcpEntityReadOnly(entity);
		return jnEntityVersionable;
	}
	
}
