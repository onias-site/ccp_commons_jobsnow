package com.ccp.especifications.db.utils.entity.decorators.enums;

/**
 * Prevê todas as combinações possíveis dos campos {@code operationPhase}, {@code transferType} e
 * {@code entityPhase} de {@code @CcpEntityDataTransfer}. Cada item encapsula os valores que o seu
 * nome expressa, de modo que a anotação declare uma única constante em vez dos três campos.
 *
 * <p>O nome de cada item é lido como uma frase: {@code [operationPhase][transferType]From
 * [entityPhase]}.
 */
public enum CcpEntityDataTransferType {
	afterTransferDataFromMainEntity(CcpEntityOperationPhase._after, CcpEntityDecoratorTransferType.transferDataTo, CcpEntityPhase.mainEntity),
	afterTransferDataFromTwinEntity(CcpEntityOperationPhase._after, CcpEntityDecoratorTransferType.transferDataTo, CcpEntityPhase.twinEntity),
	afterCopyDataFromMainEntity(CcpEntityOperationPhase._after, CcpEntityDecoratorTransferType.copyDataTo, CcpEntityPhase.mainEntity),
	afterCopyDataFromTwinEntity(CcpEntityOperationPhase._after, CcpEntityDecoratorTransferType.copyDataTo, CcpEntityPhase.twinEntity),
	beforeTransferDataFromMainEntity(CcpEntityOperationPhase._before, CcpEntityDecoratorTransferType.transferDataTo, CcpEntityPhase.mainEntity),
	beforeTransferDataFromTwinEntity(CcpEntityOperationPhase._before, CcpEntityDecoratorTransferType.transferDataTo, CcpEntityPhase.twinEntity),
	beforeCopyDataFromMainEntity(CcpEntityOperationPhase._before, CcpEntityDecoratorTransferType.copyDataTo, CcpEntityPhase.mainEntity),
	beforeCopyDataFromTwinEntity(CcpEntityOperationPhase._before, CcpEntityDecoratorTransferType.copyDataTo, CcpEntityPhase.twinEntity)
	;

	/**
	 * Momento de execução: {@code _before} (antes) ou {@code _after} (depois) da transferência.
	 */
	public final CcpEntityOperationPhase operationPhase;

	/**
	 * Tipo da transferência: {@code copyDataTo} ou {@code transferDataTo}.
	 */
	public final CcpEntityDecoratorTransferType transferType;

	/**
	 * Entidade de origem (mainEntity ou twinEntity).
	 */
	public final CcpEntityPhase entityPhase;

	private CcpEntityDataTransferType(
			CcpEntityOperationPhase operationPhase,
			CcpEntityDecoratorTransferType transferType,
			CcpEntityPhase entityPhase) {
		this.operationPhase = operationPhase;
		this.transferType = transferType;
		this.entityPhase = entityPhase;
	}
}
