package com.ccp.especifications.db.utils.entity.decorators.engine;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.utils.entity.CcpEntity;
import com.ccp.especifications.db.utils.entity.decorators.enums.CcpEntityDecoratorTransferType;

/**
 * Decorator que intercepta as operações de transferência de dados ({@code copyDataTo} e
 * {@code transferDataTo}) para executar apenas os fluxos {@code before} configurados em
 * {@code @CcpEntityDataTransfers}. Fica na parte externa da cadeia (prioridade alta) para que os side
 * effects prévios aconteçam antes dos demais decorators e o JSON por eles produzido seja o que segue
 * para dentro. O fluxo {@code after} é responsabilidade de {@code DecoratorAfterTransferDataEntity}.
 */
class DecoratorBeforeTransferDataEntity extends CcpEntityDelegator {

	private final Class<?> clazz;

	public DecoratorBeforeTransferDataEntity(CcpEntity entity, Class<?> clazz) {
		super(entity);
		this.clazz = clazz;
	}

	public boolean copyDataTo(CcpJsonRepresentation json, CcpEntity entityToTransferData) {
		boolean execute = CcpEntityDecoratorTransferType.copyDataTo.executeBefore(json, this.clazz, this.entity, entityToTransferData);
		return execute;
	}

	public boolean transferDataTo(CcpJsonRepresentation json, CcpEntity entityToTransferData) {
		boolean execute = CcpEntityDecoratorTransferType.transferDataTo.executeBefore(json, this.clazz, this.entity, entityToTransferData);
		return execute;
	}
}
