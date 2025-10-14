package com.ccp.especifications.db.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.ccp.business.CcpBusiness;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpReflectionConstructorDecorator;
import com.ccp.especifications.db.bulk.CcpBulkItem;
import com.ccp.especifications.db.bulk.CcpExecuteBulkOperation;
import com.ccp.especifications.db.crud.CcpHandleWithSearchResultsInTheEntity;
import com.ccp.especifications.db.utils.decorators.annotations.CcpEntitySpecifications;

public enum CcpEntityOperationType
{
	save {
		
		public Class<?> getJsonValidationClass(CcpEntity entity) {
			Class<?> jsonValidationClass = entity.getJsonValidationClass();
			return jsonValidationClass;
		}
		
		public CcpJsonRepresentation execute(CcpEntity entity, CcpJsonRepresentation json) {
			entity.save(json);
			return json;
		}

		public List<CcpBusiness> getStepsAfter(Class<?> entityClass) {
			CcpEntitySpecifications especifications = super.getEspecifications(entityClass);
			Class<?>[] callBacks = especifications.afterSaveRecord();
			List<CcpBusiness> collect = Arrays.asList(callBacks).stream().map(x -> instanciateFunction(x)).collect(Collectors.toList());
			return collect;
		}
		
		public List<CcpBusiness> getStepsBefore(Class<?> entityClass) {
			CcpEntitySpecifications especifications = super.getEspecifications(entityClass);
			Class<?>[] callBacks = especifications.beforeSaveRecord();
			List<CcpBusiness> collect = Arrays.asList(callBacks).stream().map(x -> instanciateFunction(x)).collect(Collectors.toList());
			return collect;
		}

	},
	delete {

		public CcpJsonRepresentation execute(CcpEntity entity, CcpJsonRepresentation json) {
			entity.delete(json);
			return json;
		}

		public List<CcpBusiness> getStepsAfter(Class<?> entityClass) {
			CcpEntitySpecifications especifications = super.getEspecifications(entityClass);
			Class<?>[] callBacks = especifications.afterDeleteRecord();
			List<CcpBusiness> collect = Arrays.asList(callBacks).stream().map(x -> instanciateFunction(x)).collect(Collectors.toList());
			return  collect;
		}

		public List<CcpBusiness> getStepsBefore(Class<?> entityClass) {
			return new ArrayList<>();
		}

	}
	,
	transferToReverseEntity{

		@SuppressWarnings("unchecked")
		public CcpBusiness getTopicHandler(CcpEntity entity, CcpExecuteBulkOperation executeBulkOperation, Consumer<String[]> functionToDeleteKeysInTheCache) {
			
			CcpBusiness topicHandler = jsn -> {
				CcpHandleWithSearchResultsInTheEntity<List<CcpBulkItem>> bulkHandler = entity.getTransferRecordToReverseEntity();
				executeBulkOperation.executeSelectUnionAllThenExecuteBulkOperation(jsn, functionToDeleteKeysInTheCache, bulkHandler);
				return jsn;
			};
			
			return topicHandler;
		}

		public List<CcpBusiness> getStepsBefore(Class<?> entityClass) {
			return new ArrayList<>();
		}

		public List<CcpBusiness> getStepsAfter(Class<?> entityClass) {
			return new ArrayList<>();
		}

		public CcpJsonRepresentation execute(CcpEntity entity, CcpJsonRepresentation json) {
			CcpJsonRepresentation result = entity.transferToReverseEntity(json);
			return result;
		}
		
	},
	;

	public abstract CcpJsonRepresentation execute(CcpEntity entity, CcpJsonRepresentation json);

	public CcpBusiness getTopicHandler(CcpEntity entity, CcpExecuteBulkOperation executeBulkOperation, Consumer<String[]> functionToDeleteKeysInTheCache) {
		CcpBusiness topicHandler = jsn -> this.execute(entity, jsn);
		return topicHandler;
	}

	public abstract List<CcpBusiness> getStepsBefore(Class<?> entityClass);

	public abstract List<CcpBusiness> getStepsAfter(Class<?> entityClass);

	public static CcpBusiness instanciateFunction(Class<?> x) {
		CcpReflectionConstructorDecorator reflection = new CcpReflectionConstructorDecorator(x);

		CcpBusiness newInstance = reflection.newInstance();
		
		return newInstance;
	}
	
	public static CcpEntitySpecifications getEspecifications(Class<?> entityClass) {
		boolean annotationIsNotPresent = false == entityClass.isAnnotationPresent(CcpEntitySpecifications.class);
		if(annotationIsNotPresent) {
			throw new RuntimeException("The class '" + entityClass + "' is not annoted by " + CcpEntitySpecifications.class.getName());
		}
		CcpEntitySpecifications annotation = entityClass.getAnnotation(CcpEntitySpecifications.class);
		return annotation;
	}

	public Class<?> getJsonValidationClass(CcpEntity entity){
		return this.getClass();
	}
	
	
}
