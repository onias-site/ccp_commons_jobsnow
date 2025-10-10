package com.ccp.especifications.db.bulk.handlers;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.bulk.CcpBulkItem;
import com.ccp.especifications.db.bulk.CcpEntityBulkOperationType;
import com.ccp.especifications.db.crud.CcpHandleWithSearchResultsInTheEntity;
import com.ccp.especifications.db.utils.CcpEntity;
import com.ccp.especifications.db.utils.CcpEntityCrudOperationType;
import com.ccp.especifications.db.utils.decorators.configurations.CcpEntityTwin;
import com.ccp.especifications.mensageria.receiver.CcpTopic;

public class CcpEntityBulkHandlerTransferRecordToReverseEntity implements CcpHandleWithSearchResultsInTheEntity<List<CcpBulkItem>>{
	
	private final CcpEntityTransferType transferType;
	
	private final Class<?> entityClass;

	public CcpEntityBulkHandlerTransferRecordToReverseEntity(CcpEntityTransferType transferType, Class<?> entityClass) {
		this.transferType = transferType;
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
			//ATTENTION SE A CLASS NAO TIVER ESSE FIELD...
			Field declaredField = this.entityClass.getDeclaredField("ENTITY");
			Object object = declaredField.get(null);
			CcpEntity object2 = this.transferType.getEntity((CcpEntity) object);
			return object2;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public List<CcpTopic> doAfterSavingIfRecordIsNotFound() {
		List<CcpTopic> functions = this.getFunctions("afterOperation");
		return functions;
	}
	
	public List<CcpTopic> doAfterSavingIfRecordIsFound() {
		List<CcpTopic> functions = this.getFunctions("afterOperation");
		return functions;
	}
	@SuppressWarnings("unchecked")
	private <T> T invokeAnnotationMethod(String methodName) {
		try {
			CcpEntityTwin annotation = this.entityClass.getAnnotation(CcpEntityTwin.class);
			Method declaredMethod = CcpEntityTwin.class.getDeclaredMethod(methodName);
			return (T) declaredMethod.invoke(annotation);
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	private List<CcpTopic> getFunctions(String callbackName){
		try {

			Class<?>[] invoke = this.invokeAnnotationMethod(callbackName);

			List<Class<?>> asList = Arrays.asList(invoke);
			
			List<CcpTopic> functions = asList.stream().map(x -> CcpEntityCrudOperationType.instanciateFunction(x)).collect(Collectors.toList());
			
			return functions;
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
}
