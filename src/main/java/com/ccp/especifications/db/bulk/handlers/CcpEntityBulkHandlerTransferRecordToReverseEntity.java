package com.ccp.especifications.db.bulk.handlers;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpReflectionConstructorDecorator;
import com.ccp.decorators.CcpReflectionMethodDecorator;
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

	//DOUBT DÁ PRA ESVAZIAR ESSA LISTA SEM CAUSAR REPERCUÇÕE???
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
	
	private List<Function<CcpJsonRepresentation, CcpJsonRepresentation>> getFunctions(String operationSpecification, String callbackName){
		try {
			//TODO PASSAR PARA O DECORATOR
			CcpEntitySpecifications configuration = this.entityClass.getAnnotation(CcpEntitySpecifications.class);
			
			
			
			CcpReflectionMethodDecorator methodTransferType = new CcpReflectionConstructorDecorator(CcpEntitySpecifications.class)
					.fromInstance(configuration).fromDeclaredMethod(this.transferType);
			CcpEntityTransferOperationEspecification cfg = methodTransferType.invokeFromMethod();
			
			CcpReflectionMethodDecorator methodOperationSpecification = new CcpReflectionConstructorDecorator(CcpEntityTransferOperationEspecification.class)
					.fromInstance(cfg).fromDeclaredMethod(operationSpecification);
			CcpEntityOperationSpecification operationSpecificationValue = methodOperationSpecification.invokeFromMethod(configuration);

			CcpReflectionMethodDecorator methodCallback = new CcpReflectionConstructorDecorator(CcpEntityOperationSpecification.class)
					.fromInstance(operationSpecificationValue).fromDeclaredMethod(callbackName);
			Class<?>[] invoke = methodCallback.invokeFromMethod();

			
			List<Class<?>> asList = Arrays.asList(invoke);
			List<Function<CcpJsonRepresentation, CcpJsonRepresentation>> functions = asList.stream().map(x -> CcpEntityCrudOperationType.instanciateFunction(x)).collect(Collectors.toList());
			return functions;
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
