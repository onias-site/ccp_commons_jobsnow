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
import com.ccp.especifications.db.utils.entity.decorators.engine.CcpEntityDecoratorTypes;
import com.ccp.especifications.db.utils.entity.decorators.engine.CcpEntityFactory;
import com.ccp.especifications.db.utils.entity.decorators.interfaces.CcpEntityConfigurator;

/**
 * Base para receptores de mensagens GCP PubSub. Resolve o processo de negócio correto a partir
 * do nome do tópico, suportando tanto {@code CcpBusiness} quanto {@code CcpEntityConfigurator} em modo assíncrono.
 */
public abstract class CcpMensageriaReceiver implements CcpJsonFieldName{

	private final String operationFieldName;

	/**
	 * Inicializa o nome do campo de operação no JSON.
	 * @param operationFieldName nome do campo que identifica a operação no JSON recebido
	 */
	public CcpMensageriaReceiver(String operationFieldName) {
		this.operationFieldName = operationFieldName;
	}

	/**
	 * Instancia o processo por reflexão e retorna o handler adequado.
	 * @param processName nome completo da classe do processo
	 * @param json JSON com os dados da mensagem recebida
	 * @return handler de negócio correspondente ao processo
	 */
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
		CcpEntity entity = CcpEntityFactory.getCustomEntity(configurator, CcpEntityDecoratorTypes.AsyncWriter);
		CcpExecuteBulkOperation executeBulkOperation = this.getExecuteBulkOperation();
		Consumer<String[]> functionToDeleteKeysInTheCache = this.getFunctionToDeleteKeysInTheCache();
		String operation = json.getAsString(this);
		CcpEntityOperationType valueOf = CcpEntityOperationType.valueOf(operation);
		CcpBusiness topicHandler = valueOf.getTopicHandler(entity, executeBulkOperation, functionToDeleteKeysInTheCache);
		return topicHandler;
	}
	

	/**
	 * Fornece o executor de operações bulk.
	 * @return executor de operações bulk
	 */
	public abstract CcpExecuteBulkOperation getExecuteBulkOperation();
	
	/**
	 * Fornece a função de invalidação de cache.
	 * @return função que recebe as chaves a invalidar no cache
	 */
	public abstract Consumer<String[]> getFunctionToDeleteKeysInTheCache();
	
	/**
	 * Cria instância concreta lendo o nome da classe no campo {@code mensageriaReceiver} do JSON.
	 * @param json JSON contendo o campo com o nome da classe concreta
	 * @return instância concreta de {@code CcpMensageriaReceiver}
	 */
	public static CcpMensageriaReceiver getInstance(CcpJsonRepresentation json) {
		
		CcpReflectionConstructorDecorator reflection = new CcpReflectionConstructorDecorator(json, Fields.mensageriaReceiver.name());
		
		CcpMensageriaReceiver newInstance = reflection.newInstance();
		
		return newInstance;
	}
	
	public static enum Fields implements CcpJsonFieldName{
		mensageriaReceiver
		;
		
	}
	/**
	 * Retorna o nome do campo de operação configurado.
	 * @return nome do campo de operação
	 */
	public String name() {
		return this.operationFieldName;
	}
}
