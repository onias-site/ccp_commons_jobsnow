package com.ccp.especifications.mensageria.receiver;

import java.util.function.Consumer;
import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpReflectionConstructorDecorator;
import com.ccp.decorators.CcpStringDecorator;
import com.ccp.especifications.db.bulk.CcpExecuteBulkOperation;
import com.ccp.especifications.db.utils.CcpEntity;
import com.ccp.especifications.db.utils.decorators.engine.CcpEntityConfigurator;

public abstract class CcpMensageriaReceiver {
	
	private final String operationTypeFieldName;
	private final String operationFieldName;
	
	public CcpMensageriaReceiver(String operationTypeFieldName,  String operationFieldName) {
		this.operationTypeFieldName = operationTypeFieldName;
		this.operationFieldName = operationFieldName;
	}

	public Function<CcpJsonRepresentation, CcpJsonRepresentation> getProcess(String processName, CcpJsonRepresentation json){
		CcpReflectionConstructorDecorator reflection = new CcpStringDecorator(processName).reflection();

		Object newInstance = reflection.newInstance();
		
		if(newInstance instanceof CcpTopic topic) {
			return topic;
		}
		
		boolean invalidTopic = newInstance instanceof CcpEntityConfigurator == false;
	
		if(invalidTopic) {
			throw new CcpErrorMensageriaInvalidName(processName);
		}		
		
	
		CcpEntityConfigurator configurator = (CcpEntityConfigurator)newInstance;
		CcpEntity entity = configurator.getEntity();
		String operationType = json.getDynamicVersion().getAsString(this.operationTypeFieldName);
		CcpMensageriaOperationType valueOf = CcpMensageriaOperationType.valueOf(operationType);
		CcpExecuteBulkOperation executeBulkOperation = this.getExecuteBulkOperation();
		Consumer<String[]> functionToDeleteKeysInTheCache = this.getFunctionToDeleteKeysInTheCache();
		Function<CcpJsonRepresentation, CcpJsonRepresentation> jnEntityTopic = valueOf.getTopicType(entity, this.operationFieldName, executeBulkOperation, functionToDeleteKeysInTheCache);
		return jnEntityTopic;
	}
	

	public abstract CcpExecuteBulkOperation getExecuteBulkOperation();
	
	public abstract Consumer<String[]> getFunctionToDeleteKeysInTheCache();
	
	public static CcpMensageriaReceiver getInstance(CcpJsonRepresentation json) {
		
		CcpReflectionConstructorDecorator reflection = new CcpReflectionConstructorDecorator(json, "mensageriaReceiver");
		
		CcpMensageriaReceiver newInstance = reflection.newInstance();
		
		return newInstance;
	}
}
