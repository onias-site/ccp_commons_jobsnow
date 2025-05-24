package com.ccp.especifications.mensageria.receiver;

import java.lang.reflect.Constructor;
import java.util.function.Consumer;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.bulk.CcpExecuteBulkOperation;
import com.ccp.especifications.db.utils.CcpEntity;
import com.ccp.exceptions.mensageria.receiver.CcpInvalidTopic;

public class CcpMensageriaReceiver {
	
	private final String operationTypeFieldName;
	private final String operationFieldName;
	
	public CcpMensageriaReceiver(String operationTypeFieldName,  String operationFieldName) {
		this.operationTypeFieldName = operationTypeFieldName;
		this.operationFieldName = operationFieldName;
	}

	public CcpTopic getProcess(String processName, CcpJsonRepresentation json){
		
		Object newInstance;

		try {
			Class<?> forName = Class.forName(processName);
			Constructor<?> constructor = forName.getConstructor();
			constructor.setAccessible(true);
			newInstance = constructor.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		if(newInstance instanceof CcpTopic topic) {
			return topic;
		}
		
		boolean invalidTopic = newInstance instanceof CcpEntity == false;
	
		if(invalidTopic) {
			throw new CcpInvalidTopic(processName);
		}		
		
		CcpEntity entity = (CcpEntity)newInstance;
		String operationType = json.getAsString(this.operationTypeFieldName);
		CcpMensageriaOperationType valueOf = CcpMensageriaOperationType.valueOf(operationType);
		CcpExecuteBulkOperation executeBulkOperation = this.getExecuteBulkOperation();
		Consumer<String[]> functionToDeleteKeysInTheCache = this.getFunctionToDeleteKeysInTheCache();
		CcpTopic jnEntityTopic = valueOf.getTopicType(entity, this.operationFieldName, executeBulkOperation, functionToDeleteKeysInTheCache);
		return jnEntityTopic;
	}
	

	public CcpExecuteBulkOperation getExecuteBulkOperation() {
		throw new UnsupportedOperationException();
	}
	
	public Consumer<String[]> getFunctionToDeleteKeysInTheCache(){
		throw new UnsupportedOperationException();
	}
}
