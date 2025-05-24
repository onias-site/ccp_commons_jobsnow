package com.ccp.especifications.db.utils.decorators.configurations;

public @interface CcpEntityTransferOperationEspecification {

	CcpEntityOperationSpecification whenRecordToTransferIsNotFound();
	CcpEntityOperationSpecification whenRecordToTransferIsFound();
	
}
