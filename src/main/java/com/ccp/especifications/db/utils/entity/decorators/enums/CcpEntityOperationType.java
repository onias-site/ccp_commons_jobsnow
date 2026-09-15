package com.ccp.especifications.db.utils.entity.decorators.enums;

/**
 * Prevê todas as combinações possíveis dos campos {@code operationPhase}, {@code operationType} e
 * {@code entityPhase} de {@code @CcpEntityOperation}. Cada item encapsula os valores que o seu nome
 * expressa, de modo que a anotação declare uma única constante em vez dos três campos.
 *
 * <p>O nome de cada item é lido como uma frase: {@code [operationPhase][operationType]From
 * [entityPhase]}.
 */
public enum CcpEntityOperationType {
	afterSaveFromMainEntity(CcpEntityOperationPhase._after, CcpEntityDecoratorOperationType.save, CcpEntityPhase.mainEntity),
	afterSaveFromTwinEntity(CcpEntityOperationPhase._after, CcpEntityDecoratorOperationType.save, CcpEntityPhase.twinEntity),
	afterDeleteFromMainEntity(CcpEntityOperationPhase._after, CcpEntityDecoratorOperationType.delete, CcpEntityPhase.mainEntity),
	afterDeleteFromTwinEntity(CcpEntityOperationPhase._after, CcpEntityDecoratorOperationType.delete, CcpEntityPhase.twinEntity),
	afterDeleteAnyWhereFromMainEntity(CcpEntityOperationPhase._after, CcpEntityDecoratorOperationType.deleteAnyWhere, CcpEntityPhase.mainEntity),
	afterDeleteAnyWhereFromTwinEntity(CcpEntityOperationPhase._after, CcpEntityDecoratorOperationType.deleteAnyWhere, CcpEntityPhase.twinEntity),
	beforeSaveFromMainEntity(CcpEntityOperationPhase._before, CcpEntityDecoratorOperationType.save, CcpEntityPhase.mainEntity),
	beforeSaveFromTwinEntity(CcpEntityOperationPhase._before, CcpEntityDecoratorOperationType.save, CcpEntityPhase.twinEntity),
	beforeDeleteFromMainEntity(CcpEntityOperationPhase._before, CcpEntityDecoratorOperationType.delete, CcpEntityPhase.mainEntity),
	beforeDeleteFromTwinEntity(CcpEntityOperationPhase._before, CcpEntityDecoratorOperationType.delete, CcpEntityPhase.twinEntity),
	beforeDeleteAnyWhereFromMainEntity(CcpEntityOperationPhase._before, CcpEntityDecoratorOperationType.deleteAnyWhere, CcpEntityPhase.mainEntity),
	beforeDeleteAnyWhereFromTwinEntity(CcpEntityOperationPhase._before, CcpEntityDecoratorOperationType.deleteAnyWhere, CcpEntityPhase.twinEntity)
	;

	/**
	 * Momento de execução: {@code _before} (antes) ou {@code _after} (depois) da operação.
	 */
	public final CcpEntityOperationPhase operationPhase;

	/**
	 * Tipo da operação: {@code save}, {@code delete} ou {@code deleteAnyWhere}.
	 */
	public final CcpEntityDecoratorOperationType operationType;

	/**
	 * Entidade de origem (mainEntity ou twinEntity).
	 */
	public final CcpEntityPhase entityPhase;

	private CcpEntityOperationType(
			CcpEntityOperationPhase operationPhase,
			CcpEntityDecoratorOperationType operationType,
			CcpEntityPhase entityPhase) {
		this.operationPhase = operationPhase;
		this.operationType = operationType;
		this.entityPhase = entityPhase;
	}
}
