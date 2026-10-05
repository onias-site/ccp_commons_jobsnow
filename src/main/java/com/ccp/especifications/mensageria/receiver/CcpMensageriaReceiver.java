package com.ccp.especifications.mensageria.receiver;

import java.util.function.Consumer;

import com.ccp.business.CcpBusiness;
import com.ccp.decorators.CcpJsonFieldName;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpReflectionConstructorDecorator;
import com.ccp.decorators.CcpStringDecorator;
import com.ccp.especifications.db.bulk.CcpExecuteBulkOperation;
import com.ccp.especifications.db.utils.entity.CcpEntity;
import com.ccp.especifications.db.utils.entity.CcpEntityOperationType;
import com.ccp.especifications.db.utils.entity.decorators.engine.CcpEntityMetaData;
import com.ccp.especifications.db.utils.entity.decorators.enums.CcpEntityPhase;
import com.ccp.especifications.db.utils.entity.decorators.interfaces.CcpEntityConfigurator;

/**
 * Base of the receivers of PubSub messages. Resolves the business process of a message from its topic name: either a
 * {@code CcpBusiness}, or a {@code CcpEntityConfigurator} whose entity runs the {@code CcpEntityOperationType} named by
 * the operation field of the message (asynchronous writes). As a {@code CcpJsonFieldName}, the receiver is the name of
 * that operation field.
 */
public abstract class CcpMensageriaReceiver implements CcpJsonFieldName{

	/** The name of the field of the message that carries the entity operation. */
	private final String operationFieldName;

	/**
	 * Builds the receiver.
	 * @param operationFieldName the name of the field that carries the entity operation
	 */
	public CcpMensageriaReceiver(String operationFieldName) {
		this.operationFieldName = operationFieldName;
	}

	/**
	 * Instantiates the process by reflection and returns its handler: the process itself when it is a
	 * {@code CcpBusiness}; for an entity configurator, the handler of the operation named in the message, over the custom
	 * entity (or its twin, when {@code entityName} names the twin).
	 * @param processName the fully qualified class name of the process (the topic)
	 * @param json the message
	 * @return the business that handles the message
	 * @throws CcpErrorMensageriaInvalidName when the class is neither a business nor an entity configurator
	 */
	public CcpBusiness getProcess(String processName, CcpJsonRepresentation json){
		CcpStringDecorator ccpStringDecorator = new CcpStringDecorator(processName);
	
		CcpReflectionConstructorDecorator reflection = ccpStringDecorator.reflection();

		Object newInstance = reflection.newInstance();
		
		if(newInstance instanceof CcpBusiness topic) {
			return topic;
		}
		boolean isCcpEntityConfigurator = newInstance instanceof CcpEntityConfigurator;
		boolean invalidTopic = false == isCcpEntityConfigurator;
	
		if(invalidTopic) {
			CcpErrorMensageriaInvalidName ccpErrorMensageriaInvalidName = new CcpErrorMensageriaInvalidName(processName);
			throw ccpErrorMensageriaInvalidName;
		}
		
		CcpEntity entity = this.getEntity(json, newInstance);
		
		CcpExecuteBulkOperation executeBulkOperation = this.getExecuteBulkOperation();
		Consumer<String[]> functionToDeleteKeysInTheCache = this.getFunctionToDeleteKeysInTheCache();
		String operation = json.getAsString(this);
		CcpEntityOperationType valueOf = CcpEntityOperationType.valueOf(operation);
		CcpBusiness topicHandler = valueOf.getTopicHandler(entity, executeBulkOperation, functionToDeleteKeysInTheCache);
		return topicHandler;
	}

	/**
	 * Returns the custom entity of the configurator, or its twin when {@code entityName} of the message names the twin.
	 * @param json the message
	 * @param newInstance the entity configurator
	 * @return the target entity
	 */
	private CcpEntity getEntity(CcpJsonRepresentation json, Object newInstance) {
		CcpEntity entity = this.getCustomEntity(newInstance);
		CcpEntityMetaData entityMetaData = entity.getEntityMetaData();
		String twinEntityName = CcpEntityPhase.twinEntity.extractEntityName(entityMetaData.configurationClass);
		String entityName = json.getAsString(JsonFieldNames.entityName);
		boolean twinEntityNameEquals = twinEntityName.equals(entityName);
		boolean isNotTwinEntity = false == twinEntityNameEquals;
		
		if(isNotTwinEntity) {
			return entity;
		}
		
		CcpEntity twinEntity = this.getTwinEntity(entity);
		return twinEntity;
	}

	/**
	 * Returns the twin of the entity.
	 * @param entity the entity
	 * @return the twin entity
	 */
	protected abstract CcpEntity getTwinEntity(CcpEntity entity);

	/**
	 * Returns the entity of the configurator with the decorators appropriate to the asynchronous execution.
	 * @param newInstance the entity configurator
	 * @return the entity
	 */
	protected abstract CcpEntity getCustomEntity(Object newInstance);
	

	/**
	 * Returns the bulk executor.
	 * @return the bulk executor
	 */
	public abstract CcpExecuteBulkOperation getExecuteBulkOperation();
	
	/**
	 * Returns the cache invalidation function.
	 * @return the function that receives the cache keys to invalidate
	 */
	public abstract Consumer<String[]> getFunctionToDeleteKeysInTheCache();
	
	/**
	 * Instantiates the concrete receiver named in the {@code mensageriaReceiver} field of the JSON.
	 * @param json the JSON naming the receiver class
	 * @return the receiver
	 */
	public static CcpMensageriaReceiver getInstance(CcpJsonRepresentation json) {
		String mensageriaReceiverName = JsonFieldNames.mensageriaReceiver.name();
	
		CcpReflectionConstructorDecorator reflection = new CcpReflectionConstructorDecorator(json, mensageriaReceiverName);
		
		CcpMensageriaReceiver newInstance = reflection.newInstance();
		
		return newInstance;
	}
	
	/** Fields read by the receiver. */
	public static enum JsonFieldNames implements CcpJsonFieldName{
		/** The fully qualified class name of the concrete receiver. */
		mensageriaReceiver,
		/** The name of the target entity, which tells whether the twin is the target. */
		entityName
		;
		
	}
	/**
	 * Returns the name of the operation field.
	 * @return the operation field name
	 */
	public String name() {
		return this.operationFieldName;
	}

	/** Raised when the topic names a class that is neither a {@code CcpBusiness} nor an entity configurator. */
	@SuppressWarnings("serial")
	public static class CcpErrorMensageriaInvalidName extends RuntimeException {
		/**
		 * Builds the error naming the process.
		 * @param processName the class name of the process
		 */
		private CcpErrorMensageriaInvalidName(String processName) {
			super("The process '" + processName + "' is an invalid topic");
		}
	}
}
