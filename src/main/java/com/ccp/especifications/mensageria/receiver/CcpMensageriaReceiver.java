package com.ccp.especifications.mensageria.receiver;

import java.lang.reflect.Constructor;
import java.util.function.Consumer;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.bulk.CcpExecuteBulkOperation;
import com.ccp.especifications.db.utils.CcpEntity;
import com.ccp.especifications.db.utils.decorators.engine.CcpEntityConfigurator;
import com.ccp.exceptions.mensageria.receiver.CcpErrorMensageriaInvalidName;

public abstract class CcpMensageriaReceiver {
	
	private final String operationTypeFieldName;
	private final String operationFieldName;
	
	public CcpMensageriaReceiver(String operationTypeFieldName,  String operationFieldName) {
		this.operationTypeFieldName = operationTypeFieldName;
		this.operationFieldName = operationFieldName;
	}

	public CcpTopic getProcess(String processName, CcpJsonRepresentation json){
		
		Object newInstance;
		Class<?> forName;
		try {
			forName = Class.forName(processName);
			Constructor<?> constructor = forName.getDeclaredConstructor();
			constructor.setAccessible(true);
			newInstance = constructor.newInstance();
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
		if(newInstance instanceof CcpTopic topic) {
			return topic;
		}
		
		boolean invalidTopic = newInstance instanceof CcpEntityConfigurator == false;
	
		if(invalidTopic) {
			throw new CcpErrorMensageriaInvalidName(processName);
		}		
		
	
		CcpEntityConfigurator configurator = (CcpEntityConfigurator)newInstance;
		CcpEntity entity = configurator.getEntity();
		String operationType = json.getAsString(this.operationTypeFieldName);
		CcpMensageriaOperationType valueOf = CcpMensageriaOperationType.valueOf(operationType);
		CcpExecuteBulkOperation executeBulkOperation = this.getExecuteBulkOperation();
		Consumer<String[]> functionToDeleteKeysInTheCache = this.getFunctionToDeleteKeysInTheCache();
		CcpTopic jnEntityTopic = valueOf.getTopicType(entity, this.operationFieldName, executeBulkOperation, functionToDeleteKeysInTheCache);
		return jnEntityTopic;
	}
	

	public abstract CcpExecuteBulkOperation getExecuteBulkOperation();
	
	public abstract Consumer<String[]> getFunctionToDeleteKeysInTheCache();
	
	public static CcpMensageriaReceiver getInstance(CcpJsonRepresentation json) {
		String mensageriaReceiver = json.getAsString("mensageriaReceiver");
		try {
			Class<?> forName = Class.forName(mensageriaReceiver);
			Constructor<?> declaredConstructor = forName.getDeclaredConstructor();
			declaredConstructor.setAccessible(true);
			Object newInstance = declaredConstructor.newInstance();
			return (CcpMensageriaReceiver) newInstance;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
