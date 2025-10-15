package com.ccp.especifications.db.utils.entity.decorators.engine;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import com.ccp.business.CcpBusiness;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpReflectionConstructorDecorator;
import com.ccp.especifications.db.bulk.CcpBulkItem;
import com.ccp.especifications.db.bulk.CcpBulkEntityOperationType;
import com.ccp.especifications.db.bulk.handlers.CcpEntityBulkHandlerTransferRecordToReverseEntity;
import com.ccp.especifications.db.utils.entity.CcpEntity;
import com.ccp.especifications.db.utils.entity.CcpEntityOperationType;
import com.ccp.especifications.db.utils.entity.annotations.CcpEntitySpecifications;
import com.ccp.especifications.db.utils.entity.fields.CcpEntityField;
import com.ccp.especifications.db.utils.entity.fields.CcpEntityJsonTransformerError;
import com.ccp.json.validations.global.engine.CcpJsonValidatorEngine;

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
		.map(x -> this.getMainBulkItem(x, CcpBulkEntityOperationType.create))
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

	public CcpJsonRepresentation getTransformedJsonByEachFieldInJson(CcpJsonRepresentation json) {
		CcpJsonRepresentation result = json;
		for (CcpEntityField field : this.fields) {
			try {
				result = field.transformer.apply(result);
			} catch (CcpEntityJsonTransformerError e) {
			}
		}
		return result;
	}

	public CcpJsonRepresentation getTransformedJsonAfterOperation(CcpJsonRepresentation json, CcpEntityOperationType operation) {
		List<CcpBusiness> steps = operation.getStepsAfter(this.entityClass);
		CcpJsonRepresentation result = this.getSteps(json, steps);
		return result;
	}

	public CcpJsonRepresentation getTransformedJsonBeforeOperation(CcpJsonRepresentation json, CcpEntityOperationType operation) {
		List<CcpBusiness> steps = operation.getStepsBefore(this.entityClass);
		CcpJsonRepresentation result = this.getSteps(json, steps);
		return result;
	}

	private CcpJsonRepresentation getSteps(CcpJsonRepresentation json, List<CcpBusiness> steps) {
		CcpJsonRepresentation result = json;
		for (CcpBusiness step : steps) {
			String featureName = step.getClass().getName();
			Class<?> jsonValidationClass = step.getJsonValidationClass();
			CcpJsonValidatorEngine.INSTANCE.validateJson(jsonValidationClass, result, featureName);
			try {
				result = step.apply(result);
			} catch (Exception e) {
				result = this.catchException(result, e);
			}
		}
		return result;
	}

	private CcpJsonRepresentation catchException(CcpJsonRepresentation result, Exception e) {
		CcpEntitySpecifications annotation = this.entityClass.getAnnotation(CcpEntitySpecifications.class);
		List<CcpBusiness> collect = Arrays.asList(annotation.flow())
		.stream().filter(f -> f.whenThrowing().equals(e.getClass()))
		.map(f -> new CcpReflectionConstructorDecorator(f.thenExecute()))
		.map(x -> (CcpBusiness)x.newInstance()).collect(Collectors.toList())
		;
		Set<Class<?>> set = new HashSet<Class<?>>();
		boolean found = false;
		for (CcpBusiness ccpBusiness : collect) {
			Class<? extends CcpBusiness> class1 = ccpBusiness.getClass();
			boolean alreadyAdded = false == set.add(class1);
			if(alreadyAdded) {
				continue;
			}
			result = ccpBusiness.apply(result);
			found = true;
		}
		
		if(found) {
			return result;
		}
		
		throw new RuntimeException(e);
	}

	public CcpEntity validateJson(CcpJsonRepresentation json) {
		CcpEntitySpecifications especifications = CcpEntityOperationType.getEspecifications(this.entityClass);
		Class<?> jsonValidationClass = especifications.entityValidation();
		String featureName = this.entityClass.getName();
		CcpJsonValidatorEngine.INSTANCE.validateJson(jsonValidationClass, json, featureName);
		return this;
	}

	public CcpEntityBulkHandlerTransferRecordToReverseEntity getTransferRecordToReverseEntity() {
		return this.entityTransferRecordToReverseEntity;
	}

	public Class<?> getConfigurationClass() {
		return this.entityClass;
	}
}
