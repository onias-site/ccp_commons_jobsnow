package com.ccp.especifications.db.utils.entity.decorators.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import com.ccp.especifications.db.utils.entity.decorators.enums.CcpEntityDecoratorTransferType;
import com.ccp.especifications.db.utils.entity.decorators.enums.CcpEntityOperationStepType;
import com.ccp.especifications.db.utils.entity.decorators.enums.CcpEntityType;

@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface CcpEntityDataTransfer {
	
	CcpExceptionFlow[] transferHandlers();
	
	CcpEntityType from();

	CcpEntityDecoratorTransferType transferType();

	CcpEntityOperationStepType when();

	@SuppressWarnings("rawtypes")
	Class[] execute();
	
	Class<?> to();
	
}
