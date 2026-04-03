package com.ccp.especifications.db.utils.entity.decorators.engine;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.utils.entity.CcpEntity;
import com.ccp.especifications.db.utils.entity.decorators.enums.CcpEntityDecoratorTransferType;

class DecoratorTransferDataEntity extends CcpEntityDelegator {
	
	private final Class<?> clazz;
	
	public DecoratorTransferDataEntity(CcpEntity entity, Class<?> clazz) {
		super(entity);
		this.clazz = clazz;
	}

	public CcpJsonRepresentation copyDataTo(CcpJsonRepresentation json, CcpEntity entityToTransferData) {
		CcpJsonRepresentation execute = CcpEntityDecoratorTransferType.copyDataTo.execute(json, this.clazz, this.entity, entityToTransferData);
		return execute;
	}
	
	public CcpJsonRepresentation transferDataTo(CcpJsonRepresentation json, CcpEntity entityToTransferData) {
		CcpJsonRepresentation execute = CcpEntityDecoratorTransferType.transferDataTo.execute(json, this.clazz, this.entity, entityToTransferData);
		return execute;
	}
}
