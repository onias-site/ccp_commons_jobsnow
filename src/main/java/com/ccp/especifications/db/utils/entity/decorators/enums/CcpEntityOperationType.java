package com.ccp.especifications.db.utils.entity.decorators.enums;

/**
 * Every combination of {@code operationPhase}, {@code operationType} and {@code entityPhase} of
 * {@code @CcpEntityOperation}. Each item holds the values its name expresses, so the annotation declares a single
 * constant instead of three fields.
 * <p>
 * The name of each item reads as a sentence: {@code [operationPhase][operationType]From[entityPhase]}.
 * <p>
 * {@code afterSave} runs on every {@code save}, whatever it returns; {@code afterInsert} only when {@code save}
 * returns {@code true} and {@code afterUpdate} only when it returns {@code false}. There is no {@code beforeInsert} nor
 * {@code beforeUpdate}, because before the {@code save} its outcome is not known yet.
 */
public enum CcpEntityOperationType {
	/** Runs {@code after} the {@code save} operation when the entity is the main entity. */
	afterSaveFromMainEntity(CcpEntityOperationPhase._after, CcpEntityDecoratorOperationType.save, CcpEntityPhase.mainEntity),
	/** Runs {@code after} the {@code save} operation when the entity is the twin entity. */
	afterSaveFromTwinEntity(CcpEntityOperationPhase._after, CcpEntityDecoratorOperationType.save, CcpEntityPhase.twinEntity),
	/** Runs {@code after} the {@code save} (only when it inserts, see {@code CcpEntityDecoratorOperationType.insert}) operation when the entity is the main entity. */
	afterInsertFromMainEntity(CcpEntityOperationPhase._after, CcpEntityDecoratorOperationType.insert, CcpEntityPhase.mainEntity),
	/** Runs {@code after} the {@code save} (only when it inserts, see {@code CcpEntityDecoratorOperationType.insert}) operation when the entity is the twin entity. */
	afterInsertFromTwinEntity(CcpEntityOperationPhase._after, CcpEntityDecoratorOperationType.insert, CcpEntityPhase.twinEntity),
	/** Runs {@code after} the {@code save} (only when it updates, see {@code CcpEntityDecoratorOperationType.update}) operation when the entity is the main entity. */
	afterUpdateFromMainEntity(CcpEntityOperationPhase._after, CcpEntityDecoratorOperationType.update, CcpEntityPhase.mainEntity),
	/** Runs {@code after} the {@code save} (only when it updates, see {@code CcpEntityDecoratorOperationType.update}) operation when the entity is the twin entity. */
	afterUpdateFromTwinEntity(CcpEntityOperationPhase._after, CcpEntityDecoratorOperationType.update, CcpEntityPhase.twinEntity),
	/** Runs {@code after} the {@code delete} operation when the entity is the main entity. */
	afterDeleteFromMainEntity(CcpEntityOperationPhase._after, CcpEntityDecoratorOperationType.delete, CcpEntityPhase.mainEntity),
	/** Runs {@code after} the {@code delete} operation when the entity is the twin entity. */
	afterDeleteFromTwinEntity(CcpEntityOperationPhase._after, CcpEntityDecoratorOperationType.delete, CcpEntityPhase.twinEntity),
	/** Runs {@code after} the {@code deleteAnyWhere} operation when the entity is the main entity. */
	afterDeleteAnyWhereFromMainEntity(CcpEntityOperationPhase._after, CcpEntityDecoratorOperationType.deleteAnyWhere, CcpEntityPhase.mainEntity),
	/** Runs {@code after} the {@code deleteAnyWhere} operation when the entity is the twin entity. */
	afterDeleteAnyWhereFromTwinEntity(CcpEntityOperationPhase._after, CcpEntityDecoratorOperationType.deleteAnyWhere, CcpEntityPhase.twinEntity),
	/** Runs {@code before} the {@code save} operation when the entity is the main entity. */
	beforeSaveFromMainEntity(CcpEntityOperationPhase._before, CcpEntityDecoratorOperationType.save, CcpEntityPhase.mainEntity),
	/** Runs {@code before} the {@code save} operation when the entity is the twin entity. */
	beforeSaveFromTwinEntity(CcpEntityOperationPhase._before, CcpEntityDecoratorOperationType.save, CcpEntityPhase.twinEntity),
	/** Runs {@code before} the {@code delete} operation when the entity is the main entity. */
	beforeDeleteFromMainEntity(CcpEntityOperationPhase._before, CcpEntityDecoratorOperationType.delete, CcpEntityPhase.mainEntity),
	/** Runs {@code before} the {@code delete} operation when the entity is the twin entity. */
	beforeDeleteFromTwinEntity(CcpEntityOperationPhase._before, CcpEntityDecoratorOperationType.delete, CcpEntityPhase.twinEntity),
	/** Runs {@code before} the {@code deleteAnyWhere} operation when the entity is the main entity. */
	beforeDeleteAnyWhereFromMainEntity(CcpEntityOperationPhase._before, CcpEntityDecoratorOperationType.deleteAnyWhere, CcpEntityPhase.mainEntity),
	/** Runs {@code before} the {@code deleteAnyWhere} operation when the entity is the twin entity. */
	beforeDeleteAnyWhereFromTwinEntity(CcpEntityOperationPhase._before, CcpEntityDecoratorOperationType.deleteAnyWhere, CcpEntityPhase.twinEntity)
	;

	/** When the side effect runs: {@code _before} or {@code _after} the operation. */
	public final CcpEntityOperationPhase operationPhase;

	/** The operation: {@code save}, {@code insert}, {@code update}, {@code delete} or {@code deleteAnyWhere}. */
	public final CcpEntityDecoratorOperationType operationType;

	/** The source entity: {@code mainEntity} or {@code twinEntity}. */
	public final CcpEntityPhase entityPhase;

	/**
	 * Associates the item with the values its name expresses.
	 * @param operationPhase when the side effect runs
	 * @param operationType the operation
	 * @param entityPhase the source entity
	 */
	private CcpEntityOperationType(
			CcpEntityOperationPhase operationPhase,
			CcpEntityDecoratorOperationType operationType,
			CcpEntityPhase entityPhase) {
		this.operationPhase = operationPhase;
		this.operationType = operationType;
		this.entityPhase = entityPhase;
	}
}
