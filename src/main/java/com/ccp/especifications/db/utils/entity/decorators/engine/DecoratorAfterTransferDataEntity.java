package com.ccp.especifications.db.utils.entity.decorators.engine;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.utils.entity.CcpEntity;
import com.ccp.especifications.db.utils.entity.decorators.enums.CcpEntityDecoratorTransferType;

/**
 * Decorator que intercepta as operações de transferência de dados ({@code copyDataTo} e
 * {@code transferDataTo}) para executar apenas os fluxos {@code after} configurados em
 * {@code @CcpEntityDataTransfers}. Fica na parte interna da cadeia (prioridade baixa) para que os side
 * effects posteriores só aconteçam depois que a transferência de fato tiver ocorrido. O fluxo
 * {@code before} é responsabilidade de {@code DecoratorBeforeTransferDataEntity}.
 */
class DecoratorAfterTransferDataEntity extends CcpEntityDelegator {

	private final Class<?> clazz;

	public DecoratorAfterTransferDataEntity(CcpEntity entity, Class<?> clazz) {
		super(entity);
		this.clazz = clazz;
	}

	public boolean copyDataTo(CcpJsonRepresentation json, CcpEntity entityToTransferData) {
		boolean execute = CcpEntityDecoratorTransferType.copyDataTo.executeAfter(json, this.clazz, this.entity, entityToTransferData);
		return execute;
	}

	public boolean transferDataTo(CcpJsonRepresentation json, CcpEntity entityToTransferData) {
		boolean execute = CcpEntityDecoratorTransferType.transferDataTo.executeAfter(json, this.clazz, this.entity, entityToTransferData);
		return execute;
	}
}
