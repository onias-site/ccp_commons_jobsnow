package com.ccp.especifications.db.utils.entity.decorators.engine2;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.ccp.business.CcpBusiness;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.utils.entity.CcpEntity2;
import com.ccp.especifications.db.utils.entity.annotations.CcpEntityExceptionsFlow;
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpEntityAfterSave;

class DecoratorAfterSaveEntity extends CcpEntityDelegator implements CcpDecoratorEntityFlow<CcpEntityAfterSave>{
	
	final Class<?>  clazz;
	
	public DecoratorAfterSaveEntity(CcpEntity2 entity, Class<?> clazz) {
		super(entity, 4);
		this.clazz = clazz;
	}
	
	public boolean isThisEntityDecorated(Class<CcpEntityAfterSave> annotation) {
		boolean annotationPresent = this.clazz.isAnnotationPresent(annotation);
		return annotationPresent;
	}

	public List<CcpBusiness> getFlow() {
		CcpEntityAfterSave annotation = this.getAnnotation();
		Class<?>[] flow = annotation.flow();
		 List<CcpBusiness> allBusiness = this.getAllBusiness(flow);
		return allBusiness;
	}

	public Map<Class<?>, List<CcpBusiness>> getExceptionHandlers() {
		CcpEntityAfterSave annotation = this.getAnnotation();
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

	public CcpEntityAfterSave getAnnotation() {
		CcpEntityAfterSave annotation = this.clazz.getAnnotation(CcpEntityAfterSave.class);
		return annotation;
	}
	
	public CcpJsonRepresentation save(CcpJsonRepresentation json) {
		CcpJsonRepresentation save = this.entity.save(json);
		CcpJsonRepresentation executeFlow = this.executeFlow(save);
		return executeFlow;
	}
	
}
