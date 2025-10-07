package com.ccp.especifications.db.bulk.handlers;

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.bulk.CcpBulkItem;
import com.ccp.especifications.db.bulk.CcpEntityBulkOperationType;
import com.ccp.especifications.db.crud.CcpHandleWithSearchResultsInTheEntity;
import com.ccp.especifications.db.utils.CcpEntity;
import com.ccp.especifications.db.utils.CcpEntityCrudOperationType;
import com.ccp.especifications.db.utils.decorators.configurations.CcpEntityOperationSpecification;
import com.ccp.especifications.db.utils.decorators.configurations.CcpEntitySpecifications;
import com.ccp.especifications.db.utils.decorators.configurations.CcpEntityTransferOperationEspecification;

public class CcpEntityBulkHandlerTransferRecordToReverseEntity implements CcpHandleWithSearchResultsInTheEntity<List<CcpBulkItem>>{
	
	private final String transferType;
	
	private final Class<?> entityClass;

	public CcpEntityBulkHandlerTransferRecordToReverseEntity(Boolean inactivate,
			Class<?> entityClass) {
		this.transferType = inactivate ? "inactivate" : "reactivate";
		this.entityClass = entityClass;
	}

	public List<CcpBulkItem> whenRecordWasFoundInTheEntitySearch(CcpJsonRepresentation json, CcpJsonRepresentation recordFound) {
	
		CcpEntity entityToSearch = this.getEntityToSearch();
		CcpEntity twinEntity = entityToSearch.getTwinEntity();
		CcpBulkItem itemTo = twinEntity.getMainBulkItem(json, CcpEntityBulkOperationType.create);
		CcpBulkItem itemFrom = entityToSearch.getMainBulkItem(json, CcpEntityBulkOperationType.delete);
		List<CcpBulkItem> asList = Arrays.asList(itemTo, itemFrom);
		
		return asList;
	}

	//LATER NO CASO DO LOGOUT FAZER UM TESTE DE TOKEN NAO ENCONTRADO
	public List<CcpBulkItem> whenRecordWasNotFoundInTheEntitySearch(CcpJsonRepresentation json) {
		CcpEntity twinEntity = this.getEntityToSearch().getTwinEntity();
		CcpBulkItem itemTo = twinEntity.getMainBulkItem(json, CcpEntityBulkOperationType.create);
		List<CcpBulkItem> asList = Arrays.asList(itemTo);
		return asList;
	}

	public CcpEntity getEntityToSearch() {
		try {
			Field declaredField = this.entityClass.getDeclaredField("ENTITY");
			Object object = declaredField.get(null);
			CcpEntity object2 = (CcpEntity) object;
			boolean itIsInvokingTwinEntity = "reactivate".equals(this.transferType);
			
			if(itIsInvokingTwinEntity) {
				CcpEntity twinEntity = object2.getTwinEntity();
				return twinEntity;
			}
			return object2;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public List<Function<CcpJsonRepresentation, CcpJsonRepresentation>> doAfterSavingIfRecordIsNotFound() {
		List<Function<CcpJsonRepresentation, CcpJsonRepresentation>> functions = this.getFunctions("whenRecordToTransferIsNotFound", "afterOperation");
		return functions;
	}
	
	public List<Function<CcpJsonRepresentation, CcpJsonRepresentation>> doAfterSavingIfRecordIsFound() {
		List<Function<CcpJsonRepresentation, CcpJsonRepresentation>> functions = this.getFunctions("whenRecordToTransferIsFound", "afterOperation");
		return functions;
	}
	@SuppressWarnings("unchecked")
	private <T> T invokeAnnotationMethod(Annotation annotation, String methodName) {
		try {
			Class<? extends Annotation> annotationType = annotation.annotationType();
			Method declaredMethod = annotationType.getDeclaredMethod(methodName);
			return (T) declaredMethod.invoke(annotation);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	private List<Function<CcpJsonRepresentation, CcpJsonRepresentation>> getFunctions(String operationSpecification, String callbackName){
		try {

			CcpEntitySpecifications configuration = this.entityClass.getAnnotation(CcpEntitySpecifications.class);

			CcpEntityTransferOperationEspecification cfg =  this.invokeAnnotationMethod(configuration, this.transferType);
		
			CcpEntityOperationSpecification operationSpecificationValue = this.invokeAnnotationMethod(cfg, operationSpecification);
		
			Class<?>[] invoke = this.invokeAnnotationMethod(operationSpecificationValue, callbackName);

			List<Class<?>> asList = Arrays.asList(invoke);
			
			List<Function<CcpJsonRepresentation, CcpJsonRepresentation>> functions = asList.stream().map(x -> CcpEntityCrudOperationType.instanciateFunction(x)).collect(Collectors.toList());
			
			return functions;
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
