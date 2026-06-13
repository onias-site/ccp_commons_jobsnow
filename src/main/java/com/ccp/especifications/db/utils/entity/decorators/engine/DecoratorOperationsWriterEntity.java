package com.ccp.especifications.db.utils.entity.decorators.engine;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.utils.entity.CcpEntity;
import com.ccp.especifications.db.utils.entity.decorators.enums.CcpEntityDecoratorOperationType;

/**
 * Decorator que intercepta as operações de escrita ({@code save}, {@code delete},
 * {@code deleteAnyWhere}) e executa os fluxos de side effects configurados em
 * {@code @CcpEntityOperations} (lógica {@code before}/{@code after}) via
 * {@code CcpEntityDecoratorOperationType}.
 */
class DecoratorOperationsWriterEntity extends CcpEntityDelegator {
	
	private final Class<?> clazz;
	
	public DecoratorOperationsWriterEntity(CcpEntity entity, Class<?> clazz) {
		super(entity);
		this.clazz = clazz;
	}

	public CcpJsonRepresentation save(CcpJsonRepresentation json) {
		CcpJsonRepresentation execute = CcpEntityDecoratorOperationType.save.execute(json, this.clazz, this.entity);
		return execute;
	}
	
	public CcpJsonRepresentation delete(CcpJsonRepresentation json) {
		CcpJsonRepresentation execute = CcpEntityDecoratorOperationType.delete.execute(json, this.clazz, this.entity);
		return execute;
	}
	
	public CcpJsonRepresentation deleteAnyWhere(CcpJsonRepresentation json) {
		CcpJsonRepresentation execute = CcpEntityDecoratorOperationType.deleteAnyWhere.execute(json, this.clazz, this.entity);
		return execute;
	}
}
