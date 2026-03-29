package com.ccp.especifications.db.utils.entity.decorators.engine2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ccp.business.CcpBusiness;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.utils.entity.CcpEntity2;
import com.ccp.especifications.db.utils.entity.annotations.CcpEntityExceptionsFlow;
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpEntityAfterDelete;

class DecoratorAfterDeleteEntity extends CcpEntityDelegator implements CcpDecoratorEntityFlow<CcpEntityAfterDelete>{
	
	final Class<?>  clazz;
	
	public DecoratorAfterDeleteEntity(CcpEntity2 entity, Class<?> clazz) {
		super(entity, 4);
		this.clazz = clazz;
	}
	
	public boolean isThisEntityDecorated(Class<CcpEntityAfterDelete> annotation) {
		boolean annotationPresent = this.clazz.isAnnotationPresent(annotation);
		return annotationPresent;
	}

	public List<CcpBusiness> getFlow() {
		CcpEntityAfterDelete annotation = this.getAnnotation();
		Class<?>[] flow = annotation.flow();
		 List<CcpBusiness> allBusiness = this.getAllBusiness(flow);
		return allBusiness;
	}

	public Map<Class<?>, List<CcpBusiness>> getExceptionHandlers() {
		CcpEntityAfterDelete annotation = this.getAnnotation();
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

	public CcpEntityAfterDelete getAnnotation() {
		CcpEntityAfterDelete annotation = this.clazz.getAnnotation(CcpEntityAfterDelete.class);
		return annotation;
	}
	
	public CcpJsonRepresentation delete(CcpJsonRepresentation json) {
		
		CcpJsonRepresentation delete = this.entity.delete(json);

		CcpJsonRepresentation executeFlow = this.executeFlow(delete);
		
		return executeFlow;
	}


}
