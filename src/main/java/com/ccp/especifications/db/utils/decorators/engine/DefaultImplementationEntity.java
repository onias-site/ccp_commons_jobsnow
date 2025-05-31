package com.ccp.especifications.db.utils.decorators.engine;

import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.bulk.CcpBulkItem;
import com.ccp.especifications.db.bulk.CcpEntityBulkOperationType;
import com.ccp.especifications.db.bulk.handlers.CcpEntityBulkHandlerTransferRecordToReverseEntity;
import com.ccp.especifications.db.utils.CcpEntity;
import com.ccp.especifications.db.utils.CcpEntityCrudOperationType;
import com.ccp.especifications.db.utils.CcpEntityField;
import com.ccp.especifications.db.utils.decorators.configurations.CcpEntitySpecifications;
import com.ccp.exceptions.db.utils.CcpEntityJsonTransformerError;
import com.ccp.validation.CcpJsonFieldsValidations;

final class DefaultImplementationEntity implements CcpEntity{

	final CcpEntityBulkHandlerTransferRecordToReverseEntity entityTransferRecordToReverseEntity;
	final CcpEntityField[] fields;
	final Class<?> entityClass;
	final String entityName;

	public DefaultImplementationEntity(String entityName, Class<?> entityClass, CcpEntityBulkHandlerTransferRecordToReverseEntity entityTransferRecordToReverseEntity, CcpEntityField... fields) {
		this.entityTransferRecordToReverseEntity = entityTransferRecordToReverseEntity;
		this.entityClass = entityClass;
		this.entityName = entityName;
		this.fields = fields;
	}

	public String getEntityName() {
		return this.entityName;
	}

	public final CcpEntityField[] getFields() {
		return this.fields;
	}

	public String toString() {
		String entityName = this.getEntityName();
		return entityName;
	}
	
	protected List<CcpBulkItem> toCreateBulkItems(String... jsons){
		List<CcpBulkItem> collect = Arrays.asList(jsons)
		.stream().map(x -> new CcpJsonRepresentation(x))
		.map(x -> this.getMainBulkItem(x, CcpEntityBulkOperationType.create))
		.collect(Collectors.toList());
		return collect;
	}
	
	public final int hashCode() {
		String entityName = this.getEntityName();
		return entityName.hashCode();
	}
	
	public final boolean equals(Object obj) {
		try {
			String entityName = ((DefaultImplementationEntity)obj).getEntityName();
			String entityName2 = this.getEntityName();
			boolean equals = entityName.equals(entityName2);
			return equals;
		} catch (Exception e) {
			return false;
		}
	}

	public CcpJsonRepresentation getTransformedJsonBeforeAnyCrudOperations(CcpJsonRepresentation json) {
		CcpJsonRepresentation result = json;
		for (CcpEntityField field : this.fields) {
			try {
				Function<CcpJsonRepresentation, CcpJsonRepresentation> transformer = field.getTransformer();
				result = transformer.apply(result);
			} catch (CcpEntityJsonTransformerError e) {
			}
		}
		return result;
	}

	public CcpJsonRepresentation getTransformedJsonAfterOperation(CcpJsonRepresentation json, CcpEntityCrudOperationType operation) {
		List<Function<CcpJsonRepresentation, CcpJsonRepresentation>> stepsAfter = operation.getStepsAfter(this.entityClass);
		CcpJsonRepresentation result = json;
		for (Function<CcpJsonRepresentation, CcpJsonRepresentation> function : stepsAfter) {
			result = function.apply(result);
		}
		return result;
	}

	public CcpEntity validateJson(CcpJsonRepresentation json) {
		CcpEntitySpecifications especifications = CcpEntityCrudOperationType.getEspecifications(this.entityClass);
		Class<?> jsonValidationClass = especifications.classWithFieldsValidationsRules();
		String featureName = this.entityClass.getName()+ "." + this;
		CcpJsonFieldsValidations.validate(jsonValidationClass, json.content, featureName);
		return this;
	}

	public CcpEntityBulkHandlerTransferRecordToReverseEntity getTransferRecordToReverseEntity() {
		return this.entityTransferRecordToReverseEntity;
	}

	public Class<?> getConfigurationClass() {
		return this.entityClass;
	}

	
	
	
}
