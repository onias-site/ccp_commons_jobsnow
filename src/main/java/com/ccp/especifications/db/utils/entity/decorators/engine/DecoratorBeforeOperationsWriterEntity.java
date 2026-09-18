package com.ccp.especifications.db.utils.entity.decorators.engine;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.utils.entity.CcpEntity;
import com.ccp.especifications.db.utils.entity.decorators.enums.CcpEntityDecoratorOperationType;

/**
 * Decorator que intercepta as operações de escrita ({@code save}, {@code delete},
 * {@code deleteAnyWhere}) para executar apenas os fluxos {@code before} configurados em
 * {@code @CcpEntityOperations}. Fica na parte externa da cadeia (prioridade alta) para que os side
 * effects prévios aconteçam antes dos demais decorators e o JSON por eles produzido seja o que segue
 * para dentro. O fluxo {@code after} é responsabilidade de {@code DecoratorAfterOperationsWriterEntity}.
 */
class DecoratorBeforeOperationsWriterEntity extends CcpEntityDelegator {

	private final Class<?> clazz;

	public DecoratorBeforeOperationsWriterEntity(CcpEntity entity, Class<?> clazz) {
		super(entity);
		this.clazz = clazz;
	}

	public boolean save(CcpJsonRepresentation json) {
		boolean execute = CcpEntityDecoratorOperationType.save.executeBefore(json, this.clazz, this.entity);
		return execute;
	}

	public boolean delete(CcpJsonRepresentation json) {
		boolean execute = CcpEntityDecoratorOperationType.delete.executeBefore(json, this.clazz, this.entity);
		return execute;
	}

	public boolean deleteAnyWhere(CcpJsonRepresentation json) {
		boolean execute = CcpEntityDecoratorOperationType.deleteAnyWhere.executeBefore(json, this.clazz, this.entity);
		return execute;
	}
}
