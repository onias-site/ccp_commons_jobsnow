package com.ccp.especifications.db.utils.entity;

import java.util.function.Consumer;

import com.ccp.business.CcpBusiness;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonRepresentation.CcpJsonFieldName;
import com.ccp.decorators.CcpReflectionConstructorDecorator;
import com.ccp.decorators.CcpStringDecorator;
import com.ccp.especifications.db.bulk.CcpExecuteBulkOperation;
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpEntityFieldsValidator;
import com.ccp.especifications.db.utils.entity.decorators.engine.CcpEntityMetaData;

/**
 * Define os tipos de operação que podem ser executados sobre uma entidade: {@code save}, {@code delete},
 * {@code deleteAnyWhere}, {@code transferDataTo} e {@code copyDataTo}. Cada constante implementa o método
 * {@code execute} com a lógica específica da operação, funcionando como um command pattern.
 */
public enum CcpEntityOperationType {
	/**
	 * Operação de persistência (criação ou atualização) do registro.
	 */
	save {
		public CcpJsonRepresentation execute(CcpEntity entity, CcpJsonRepresentation json) {
			CcpJsonRepresentation save = entity.save(json);
			return save;
		}
	},
	/**
	 * Operação de exclusão do registro.
	 */
	delete {
		public CcpJsonRepresentation execute(CcpEntity entity, CcpJsonRepresentation json) {
			CcpJsonRepresentation delete = entity.delete(json);
			return delete;
		}
	},
	/**
	 * Operação de exclusão sem restrições adicionais.
	 */
	deleteAnyWhere {
		@Override
		public CcpJsonRepresentation execute(CcpEntity entity, CcpJsonRepresentation json) {
			CcpJsonRepresentation deleteAnyWhere = entity.deleteAnyWhere(json);
			return deleteAnyWhere;
		}
	},
	/**
	 * Operação de transferência (mover) de dados desta entidade para outra, removendo o registro de origem.
	 */
	transferDataTo {
		public CcpJsonRepresentation execute(CcpEntity entity, CcpJsonRepresentation json) {
			CcpEntity entities = this.getEntities(json);
			CcpJsonRepresentation transferDataTo = entity.transferDataTo(json, entities);
			return transferDataTo;
		}
	},
	/**
	 * Operação de cópia de dados desta entidade para outra, sem remover o registro de origem.
	 */
	copyDataTo {
		public CcpJsonRepresentation execute(CcpEntity entity, CcpJsonRepresentation json) {
			CcpEntity entities = this.getEntities(json);
			CcpJsonRepresentation copyDataTo = entity.copyDataTo(json, entities);
			return copyDataTo;
		}
	};

	CcpEntity getEntities(CcpJsonRepresentation json) {
		String entityToTransfer = json.getAsString(CcpEntityOperationType.Fields.entityToTransfer);
		CcpEntity entity = new CcpStringDecorator(entityToTransfer).reflection().newInstance();
		return entity;
	}

	/**
	 * Executa a operação representada pela constante sobre a entidade e o JSON informados.
	 */
	public abstract CcpJsonRepresentation execute(CcpEntity entity, CcpJsonRepresentation json);

	/**
	 * Retorna um {@code CcpBusiness} (função lambda) que encapsula a execução desta operação sobre a entidade
	 * informada; usado como handler de tópicos/mensagens.
	 */
	public CcpBusiness getTopicHandler(CcpEntity entity, CcpExecuteBulkOperation executeBulkOperation, Consumer<String[]> functionToDeleteKeysInTheCache) {
		CcpBusiness topicHandler = jsn -> this.execute(entity, jsn);
		return topicHandler;
	}

	/**
	 * Instancia via reflexão uma classe que implementa {@code CcpBusiness} e a retorna.
	 */
	public static CcpBusiness instanciateFunction(Class<?> x) {
		CcpReflectionConstructorDecorator reflection = new CcpReflectionConstructorDecorator(x);

		CcpBusiness newInstance = reflection.newInstance();

		return newInstance;
	}

	/**
	 * Retorna a classe de validação de campos JSON associada à entidade (anotada com
	 * {@code @CcpEntityFieldsValidator}). Se não houver anotação, retorna a própria classe da constante de enum.
	 */
	public Class<?> getJsonValidationClass(CcpEntity entity){

		CcpEntityMetaData entityDetails = entity.getEntityMetaData();
		CcpEntityFieldsValidator annotation = entityDetails.configurationClass.getAnnotation(CcpEntityFieldsValidator.class);

		boolean entityWithoutValidation = annotation == null;

		if(entityWithoutValidation) {
			Class<? extends CcpEntityOperationType> clazz = this.getClass();
			return clazz;
		}

		Class<?> jsonValidationClass = annotation.classReferenceWithTheFields();
		return jsonValidationClass;
	}

	public static enum Fields implements CcpJsonFieldName{
		entityToTransferTheData, entityToTransfer
	}
}
