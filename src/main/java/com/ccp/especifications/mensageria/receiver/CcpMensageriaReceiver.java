package com.ccp.especifications.mensageria.receiver;

import java.util.function.Consumer;

import com.ccp.business.CcpBusiness;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonRepresentation.CcpJsonFieldName;
import com.ccp.decorators.CcpReflectionConstructorDecorator;
import com.ccp.decorators.CcpStringDecorator;
import com.ccp.especifications.db.bulk.CcpExecuteBulkOperation;
import com.ccp.especifications.db.utils.entity.CcpEntity;
import com.ccp.especifications.db.utils.entity.CcpEntityOperationType;
import com.ccp.especifications.db.utils.entity.decorators.engine.CcpEntityConfigurator;

public abstract class CcpMensageriaReceiver {
	
	private final String operationFieldName;
	
	public CcpMensageriaReceiver(String operationFieldName) {
		this.operationFieldName = operationFieldName;
	}

	public CcpBusiness getProcess(String processName, CcpJsonRepresentation json){
		
		CcpReflectionConstructorDecorator reflection = new CcpStringDecorator(processName).reflection();

		Object newInstance = reflection.newInstance();
		
		if(newInstance instanceof CcpBusiness topic) {
			return topic;
		}
		
		boolean invalidTopic = false == newInstance instanceof CcpEntityConfigurator;
	
		if(invalidTopic) {
			throw new CcpErrorMensageriaInvalidName(processName);
		}
		
		CcpEntityConfigurator configurator = (CcpEntityConfigurator)newInstance;
		CcpEntity entity = configurator.getEntity().getWrapedEntity();
		CcpExecuteBulkOperation executeBulkOperation = this.getExecuteBulkOperation();
		Consumer<String[]> functionToDeleteKeysInTheCache = this.getFunctionToDeleteKeysInTheCache();
		String operation = json.getDynamicVersion().getAsString(this.operationFieldName);
		CcpEntityOperationType valueOf = CcpEntityOperationType.valueOf(operation);
		CcpBusiness topicHandler = valueOf.getTopicHandler(entity, executeBulkOperation, functionToDeleteKeysInTheCache);
		return topicHandler;
	}
	

	public abstract CcpExecuteBulkOperation getExecuteBulkOperation();
	
	public abstract Consumer<String[]> getFunctionToDeleteKeysInTheCache();
	
	public static CcpMensageriaReceiver getInstance(CcpJsonRepresentation json) {
		
		CcpReflectionConstructorDecorator reflection = new CcpReflectionConstructorDecorator(json, Fields.mensageriaReceiver.name());
		
		CcpMensageriaReceiver newInstance = reflection.newInstance();
		
		return newInstance;
	}
	
	public static enum Fields implements CcpJsonFieldName{
		mensageriaReceiver
		;
		
	}
}
