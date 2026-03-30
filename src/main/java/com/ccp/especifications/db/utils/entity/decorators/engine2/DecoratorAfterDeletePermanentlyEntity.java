package com.ccp.especifications.db.utils.entity.decorators.engine2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ccp.business.CcpBusiness;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.utils.entity.CcpEntity2;
import com.ccp.especifications.db.utils.entity.annotations.CcpEntityExceptionsFlow;
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpEntityAfterDeletePermanently;

class DecoratorAfterDeletePermanentlyEntity extends CcpEntityDelegator implements CcpDecoratorEntityFlow<CcpEntityAfterDeletePermanently>{
	
	final Class<?>  clazz;
	
	public DecoratorAfterDeletePermanentlyEntity(CcpEntity2 entity, Class<?> clazz) {
		super(entity);
		this.clazz = clazz;
	}
	
	public boolean isThisEntityDecorated(Class<CcpEntityAfterDeletePermanently> annotation) {
		boolean annotationPresent = this.clazz.isAnnotationPresent(annotation);
		return annotationPresent;
	}

	public List<CcpBusiness> getFlow() {
		CcpEntityAfterDeletePermanently annotation = this.getAnnotation();
		Class<?>[] flow = annotation.flow();
		 List<CcpBusiness> allBusiness = this.getAllBusiness(flow);
		return allBusiness;
	}

	public Map<Class<?>, List<CcpBusiness>> getExceptionHandlers() {
		CcpEntityAfterDeletePermanently annotation = this.getAnnotation();
		CcpEntityExceptionsFlow[] exceptionHandlers = annotation.exceptionHandlers();
		Map<Class<?>, List<CcpBusiness>> result = new HashMap<>();
		for (CcpEntityExceptionsFlow exceptionHandler : exceptionHandlers) {
			 Class<?>[] thenExecute = exceptionHandler.thenExecute();
			 List<CcpBusiness> allBusiness = this.getAllBusiness(thenExecute);
			 Class<?> whenThrowing = exceptionHandler.whenThrowing();
			 result.put(whenThrowing, allBusiness);
		
		}
		return result;
	}

	public CcpEntityAfterDeletePermanently getAnnotation() {
		CcpEntityAfterDeletePermanently annotation = this.clazz.getAnnotation(CcpEntityAfterDeletePermanently.class);
		return annotation;
	}
	
	public CcpJsonRepresentation deleteAnyWhere(CcpJsonRepresentation json) {
		CcpJsonRepresentation deleteAnyWhere = super.deleteAnyWhere(json);
		CcpJsonRepresentation executeFlow = this.executeFlow(deleteAnyWhere);
		return executeFlow;
	}
}
