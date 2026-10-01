package com.ccp.especifications.db.utils.entity;

import java.util.function.Consumer;

import com.ccp.business.CcpBusiness;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonFieldName;
import com.ccp.decorators.CcpReflectionConstructorDecorator;
import com.ccp.decorators.CcpStringDecorator;
import com.ccp.especifications.db.bulk.CcpExecuteBulkOperation;
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpEntityFieldsValidator;
import com.ccp.especifications.db.utils.entity.decorators.engine.CcpEntityMetaData;
import com.ccp.especifications.db.utils.entity.decorators.interfaces.CcpEntityConfigurator;

/**
 * Defines the types of operation that can be executed on an entity: {@code save}, {@code delete},
 * {@code deleteAnyWhere}, {@code transferDataTo} and {@code copyDataTo}. Each constant implements the
 * {@code execute} method with the operation-specific logic, working as a command pattern.
 */
public enum CcpEntityOperationType {
	/**
	 * Persistence operation (creation or update) of the record.
	 */
	save {
		public CcpJsonRepresentation execute(CcpEntity entity, CcpJsonRepresentation json) {
			entity.save(json);
			return json;
		}
	},
	/**
	 * Record deletion operation.
	 */
	delete {
		public CcpJsonRepresentation execute(CcpEntity entity, CcpJsonRepresentation json) {
			entity.delete(json);
			return json;
		}

		public Class<?> getJsonValidationClass(CcpEntity entity){
			Class<?> withoutValidation = this.getWithoutValidation();
			return withoutValidation;
		}
	},
	/**
	 * Deletion operation without additional restrictions.
	 */
	deleteAnyWhere {
		@Override
		public CcpJsonRepresentation execute(CcpEntity entity, CcpJsonRepresentation json) {
			entity.deleteAnyWhere(json);
			return json;
		}

		public Class<?> getJsonValidationClass(CcpEntity entity){
			Class<?> withoutValidation = this.getWithoutValidation();
			return withoutValidation;
		}
	},
	/**
	 * Operation that transfers (moves) data from this entity to another, removing the source record.
	 */
	transferDataTo {
		public CcpJsonRepresentation execute(CcpEntity entity, CcpJsonRepresentation json) {
			CcpEntity entities = this.getEntities(json);
			entity.transferDataTo(json, entities);
			return json;
		}
	},
	/**
	 * Operation that copies data from this entity to another, without removing the source record.
	 */
	copyDataTo {
		public CcpJsonRepresentation execute(CcpEntity entity, CcpJsonRepresentation json) {
			CcpEntity entities = this.getEntities(json);
			entity.copyDataTo(json, entities);
			return json;
		}
	};

	/**
	 * The target entity of an asynchronous transfer or copy, sent as the name of its configuration class
	 * ({@code entityToTransfer}) and the name of the entity ({@code entityNameToTransfer}), which tells whether
	 * the target is the twin. Up to 2026-09-28 the entity object itself went in the message, and this method
	 * tried to load a class named after its {@code toString()} (a json) and to cast the configurator to
	 * {@code CcpEntity}, so no asynchronous transfer or copy ever worked.
	 */
	CcpEntity getEntities(CcpJsonRepresentation json) {
		String configurationClassName = json.getAsString(CcpEntityOperationType.Fields.entityToTransfer);
		String entityName = json.getAsString(CcpEntityOperationType.Fields.entityNameToTransfer);
		CcpStringDecorator classNameDecorator = new CcpStringDecorator(configurationClassName);
		CcpReflectionConstructorDecorator reflection = classNameDecorator.reflection();
		CcpEntityConfigurator configurator = reflection.newInstance();
		CcpEntity entity = configurator.getEntity();

		CcpEntityMetaData entityDetails = entity.getEntityMetaData();
		boolean isTheMainEntity = entityName.isEmpty() || entityName.equals(entityDetails.entityName);

		if(isTheMainEntity) {
			return entity;
		}

		CcpEntity twinEntity = entity.getTwinEntity();
		return twinEntity;
	}

	/**
	 * Puts in the json the target entity of a transfer or copy that will be executed asynchronously, in the form
	 * {@link #getEntities(CcpJsonRepresentation)} reads it back.
	 */
	public static CcpJsonRepresentation putEntityToTransfer(CcpJsonRepresentation json, CcpEntity targetEntity) {
		CcpEntityMetaData targetDetails = targetEntity.getEntityMetaData();
		String configurationClassName = targetDetails.configurationClass.getName();
		CcpJsonRepresentation jsonWithConfigurationClass = json.put(CcpEntityOperationType.Fields.entityToTransfer, configurationClassName);
		CcpJsonRepresentation jsonWithEntityName = jsonWithConfigurationClass.put(CcpEntityOperationType.Fields.entityNameToTransfer, targetDetails.entityName);
		return jsonWithEntityName;
	}

	/**
	 * Executes the operation represented by the constant on the given entity and JSON.
	 * Since the entity's write operations return only a boolean, the input JSON is
	 * passed along to keep the {@code CcpBusiness} contract.
	 */
	public abstract CcpJsonRepresentation execute(CcpEntity entity, CcpJsonRepresentation json);

	/**
	 * Returns a {@code CcpBusiness} that executes this operation on the given entity.
	 */
	public CcpBusiness getOperationCallback(CcpEntity entity) {
		CcpBusiness operationCallback = json -> this.execute(entity, json);
		return operationCallback;
	}

	/**
	 * Returns a {@code CcpBusiness} (lambda function) that encapsulates the execution of this operation on the given
	 * entity; used as a topic/message handler.
	 */
	public CcpBusiness getTopicHandler(CcpEntity entity, CcpExecuteBulkOperation executeBulkOperation, Consumer<String[]> functionToDeleteKeysInTheCache) {
		CcpBusiness topicHandler = json -> this.execute(entity, json);
		return topicHandler;
	}

	/**
	 * Instantiates, through reflection, a class that implements {@code CcpBusiness} and returns it.
	 */
	public static CcpBusiness instanciateFunction(Class<?> businessClass) {
		CcpReflectionConstructorDecorator reflection = new CcpReflectionConstructorDecorator(businessClass);

		CcpBusiness newInstance = reflection.newInstance();

		return newInstance;
	}

	/**
	 * Returns the JSON field validation class associated with the entity (annotated with
	 * {@code @CcpEntityFieldsValidator}). If there is no annotation, returns the class of the enum constant itself.
	 */
	public Class<?> getJsonValidationClass(CcpEntity entity){

		CcpEntityMetaData entityDetails = entity.getEntityMetaData();
		CcpEntityFieldsValidator annotation = entityDetails.configurationClass.getAnnotation(CcpEntityFieldsValidator.class);

		boolean entityWithoutValidation = annotation == null;

		if(entityWithoutValidation) {
			Class<?> withoutValidation = this.getWithoutValidation();
			return withoutValidation;
		}

		Class<?> jsonValidationClass = annotation.classReferenceWithTheFields();
		return jsonValidationClass;
	}

	/**
	 * The class of the enum constant itself, which declares no field and so validates nothing. The deletions
	 * ({@code delete} and {@code deleteAnyWhere}) use it even when the entity has rules: they need only the
	 * primary key, while the entity's rules describe the whole record. Up to 2026-09-30 an asynchronous
	 * deletion was validated against the whole record, so deleting by the primary key alone was refused
	 * (422) with the required non-key fields missing.
	 */
	Class<?> getWithoutValidation() {
		Class<? extends CcpEntityOperationType> clazz = this.getClass();
		return clazz;
	}

	public static enum Fields implements CcpJsonFieldName{
		entityToTransferTheData, entityToTransfer, entityNameToTransfer
	}
}
