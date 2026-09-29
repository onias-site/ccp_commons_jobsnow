package com.ccp.especifications.db.utils.entity.decorators.enums;

/**
 * Prevê todas as combinações possíveis dos campos {@code operationPhase}, {@code operationType} e
 * {@code entityPhase} de {@code @CcpEntityOperation}. Cada item encapsula os valores que o seu nome
 * expressa, de modo que a anotação declare uma única constante em vez dos três campos.
 *
 * <p>O nome de cada item é lido como uma frase: {@code [operationPhase][operationType]From
 * [entityPhase]}.
 *
 * <p>{@code afterSave} dispara em qualquer {@code save}, independentemente do retorno;
 * {@code afterInsert} só quando o {@code save} devolve {@code true} e {@code afterUpdate} só quando
 * devolve {@code false}. Não há {@code beforeInsert} nem {@code beforeUpdate}, porque antes do
 * {@code save} o retorno ainda não é conhecido.
 */
public enum CcpEntityOperationType {
	afterSaveFromMainEntity(CcpEntityOperationPhase._after, CcpEntityDecoratorOperationType.save, CcpEntityPhase.mainEntity),
	afterSaveFromTwinEntity(CcpEntityOperationPhase._after, CcpEntityDecoratorOperationType.save, CcpEntityPhase.twinEntity),
	afterInsertFromMainEntity(CcpEntityOperationPhase._after, CcpEntityDecoratorOperationType.insert, CcpEntityPhase.mainEntity),
	afterInsertFromTwinEntity(CcpEntityOperationPhase._after, CcpEntityDecoratorOperationType.insert, CcpEntityPhase.twinEntity),
	afterUpdateFromMainEntity(CcpEntityOperationPhase._after, CcpEntityDecoratorOperationType.update, CcpEntityPhase.mainEntity),
	afterUpdateFromTwinEntity(CcpEntityOperationPhase._after, CcpEntityDecoratorOperationType.update, CcpEntityPhase.twinEntity),
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
	 * Tipo da operação: {@code save}, {@code insert}, {@code update}, {@code delete} ou
	 * {@code deleteAnyWhere}.
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
