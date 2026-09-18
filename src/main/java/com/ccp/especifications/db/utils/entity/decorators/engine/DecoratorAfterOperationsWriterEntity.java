package com.ccp.especifications.db.utils.entity.decorators.engine;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.utils.entity.CcpEntity;
import com.ccp.especifications.db.utils.entity.decorators.enums.CcpEntityDecoratorOperationType;

/**
 * Decorator que intercepta as operações de escrita ({@code save}, {@code delete},
 * {@code deleteAnyWhere}) para executar apenas os fluxos {@code after} configurados em
 * {@code @CcpEntityOperations}. Fica na parte interna da cadeia (prioridade baixa) para que os side
 * effects posteriores só aconteçam depois que a gravação de fato tiver ocorrido. O fluxo
 * {@code before} é responsabilidade de {@code DecoratorBeforeOperationsWriterEntity}.
 */
class DecoratorAfterOperationsWriterEntity extends CcpEntityDelegator {

	private final Class<?> clazz;

	public DecoratorAfterOperationsWriterEntity(CcpEntity entity, Class<?> clazz) {
		super(entity);
		this.clazz = clazz;
	}

	public boolean save(CcpJsonRepresentation json) {
		boolean execute = CcpEntityDecoratorOperationType.save.executeAfter(json, this.clazz, this.entity);
		return execute;
	}

	public boolean delete(CcpJsonRepresentation json) {
		boolean execute = CcpEntityDecoratorOperationType.delete.executeAfter(json, this.clazz, this.entity);
		return execute;
	}

	public boolean deleteAnyWhere(CcpJsonRepresentation json) {
		boolean execute = CcpEntityDecoratorOperationType.deleteAnyWhere.executeAfter(json, this.clazz, this.entity);
		return execute;
	}
}
