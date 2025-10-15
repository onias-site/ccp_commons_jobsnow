package com.ccp.especifications.db.utils.decorators.engine;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.utils.CcpEntity;
import com.ccp.especifications.db.utils.CcpEntityOperationType;
import com.ccp.business.CcpBusiness;
//TODO ANNOTATION PARA ESSA ENTITY

final class DecoratorReadOnlyEntity extends CcpEntityDelegator  implements CcpEntityDecoratorFactory {

	protected DecoratorReadOnlyEntity(CcpEntity entity) {
		super(entity);
	}

	private DecoratorReadOnlyEntity() {
		super(null);
	}
	
	public boolean create(CcpJsonRepresentation json) {
		throw new UnsupportedOperationException("The entity '" + this.entity.getEntityName() + "' is just to read only");
	}
	
	public CcpJsonRepresentation save(CcpJsonRepresentation json, CcpJsonRepresentation onlyExistingFields) {
		throw new UnsupportedOperationException("The entity '" + this.entity.getEntityName() + "' is just to read only");
	}
	
	public CcpJsonRepresentation delete(CcpJsonRepresentation json) {
		throw new UnsupportedOperationException("The entity '" + this.entity.getEntityName() + "' is just to read only");
	}

	public boolean delete(String id) {
		throw new UnsupportedOperationException("The entity '" + this.entity.getEntityName() + "' is just to read only");
	}
	
	public CcpBusiness getOperationCallback(CcpEntityOperationType operation){
		return json -> operation.execute(this, json);
	}

	public CcpEntity getEntity(CcpEntity entity) {
		DecoratorReadOnlyEntity ent = new DecoratorReadOnlyEntity(entity);
		return ent;
	}
	
}
