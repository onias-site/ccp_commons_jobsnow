package com.ccp.especifications.db.utils.entity.decorators.enums;

/**
 * Momento de execução de side effects em operações de entidade: {@code before} (antes da operação
 * principal) e {@code after} (depois). Usado como atributo em {@code @CcpEntityOperation} e
 * {@code @CcpEntityDataTransfer}.
 */
public enum CcpEntityOperationStepType {
	after, before
}
