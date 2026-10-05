package com.ccp.especifications.db.utils.entity.decorators.enums;

/**
 * Every combination of {@code operationPhase}, {@code transferType} and {@code entityPhase} of
 * {@code @CcpEntityDataTransfer}. Each item holds the values its name expresses, so the annotation declares a single
 * constant instead of three fields.
 * <p>
 * The name of each item reads as a sentence: {@code [operationPhase][transferType]From[entityPhase]}.
 */
public enum CcpEntityDataTransferType {
	/** Runs {@code after} the {@code transferDataTo} operation when the entity is the main entity. */
	afterTransferDataFromMainEntity(CcpEntityOperationPhase._after, CcpEntityDecoratorTransferType.transferDataTo, CcpEntityPhase.mainEntity),
	/** Runs {@code after} the {@code transferDataTo} operation when the entity is the twin entity. */
	afterTransferDataFromTwinEntity(CcpEntityOperationPhase._after, CcpEntityDecoratorTransferType.transferDataTo, CcpEntityPhase.twinEntity),
	/** Runs {@code after} the {@code copyDataTo} operation when the entity is the main entity. */
	afterCopyDataFromMainEntity(CcpEntityOperationPhase._after, CcpEntityDecoratorTransferType.copyDataTo, CcpEntityPhase.mainEntity),
	/** Runs {@code after} the {@code copyDataTo} operation when the entity is the twin entity. */
	afterCopyDataFromTwinEntity(CcpEntityOperationPhase._after, CcpEntityDecoratorTransferType.copyDataTo, CcpEntityPhase.twinEntity),
	/** Runs {@code before} the {@code transferDataTo} operation when the entity is the main entity. */
	beforeTransferDataFromMainEntity(CcpEntityOperationPhase._before, CcpEntityDecoratorTransferType.transferDataTo, CcpEntityPhase.mainEntity),
	/** Runs {@code before} the {@code transferDataTo} operation when the entity is the twin entity. */
	beforeTransferDataFromTwinEntity(CcpEntityOperationPhase._before, CcpEntityDecoratorTransferType.transferDataTo, CcpEntityPhase.twinEntity),
	/** Runs {@code before} the {@code copyDataTo} operation when the entity is the main entity. */
	beforeCopyDataFromMainEntity(CcpEntityOperationPhase._before, CcpEntityDecoratorTransferType.copyDataTo, CcpEntityPhase.mainEntity),
	/** Runs {@code before} the {@code copyDataTo} operation when the entity is the twin entity. */
	beforeCopyDataFromTwinEntity(CcpEntityOperationPhase._before, CcpEntityDecoratorTransferType.copyDataTo, CcpEntityPhase.twinEntity)
	;

	/** When the side effect runs: {@code _before} or {@code _after} the transfer. */
	public final CcpEntityOperationPhase operationPhase;

	/** The kind of transfer: {@code copyDataTo} or {@code transferDataTo}. */
	public final CcpEntityDecoratorTransferType transferType;

	/** The source entity: {@code mainEntity} or {@code twinEntity}. */
	public final CcpEntityPhase entityPhase;

	/**
	 * Associates the item with the values its name expresses.
	 * @param operationPhase when the side effect runs
	 * @param transferType the kind of transfer
	 * @param entityPhase the source entity
	 */
	private CcpEntityDataTransferType(
			CcpEntityOperationPhase operationPhase,
			CcpEntityDecoratorTransferType transferType,
			CcpEntityPhase entityPhase) {
		this.operationPhase = operationPhase;
		this.transferType = transferType;
		this.entityPhase = entityPhase;
	}
}
