package com.ccp.especifications.db.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpReflectionConstructorDecorator;
import com.ccp.especifications.db.utils.decorators.configurations.CcpEntitySpecifications;
import com.ccp.especifications.mensageria.receiver.CcpBusiness;

public enum CcpEntityCrudOperationType implements CcpDbUtilJsonValidation
{
	save {
		public CcpJsonRepresentation execute(CcpEntity entity, CcpJsonRepresentation json) {
			entity.createOrUpdate(json);
			return json;
		}

		public List<CcpBusiness> getStepsAfter(Class<?> entityClass) {
			CcpEntitySpecifications especifications = super.getEspecifications(entityClass);
			Class<?>[] callBacks = especifications.afterSaveRecord();
			List<CcpBusiness> collect = Arrays.asList(callBacks).stream().map(x -> instanciateFunction(x)).collect(Collectors.toList());
			return collect;
		}
		
		public Class<?> getJsonValidationClass(CcpEntity entity) {
			Class<?> jsonValidationClass = entity.getJsonValidationClass();
			return jsonValidationClass;
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
	}
	,
	
	none {

		public CcpJsonRepresentation execute(CcpEntity entity, CcpJsonRepresentation json) {
			return json;
		}


		public List<CcpBusiness> getStepsAfter(Class<?> entityClass) {
			return new ArrayList<>();
		}

	}
	
	;

	public abstract CcpJsonRepresentation execute(CcpEntity entity, CcpJsonRepresentation json);

	public abstract List<CcpBusiness> getStepsAfter(Class<?> entityClass);

	public static CcpBusiness instanciateFunction(Class<?> x) {
		CcpReflectionConstructorDecorator reflection = new CcpReflectionConstructorDecorator(x);

		CcpBusiness newInstance = reflection.newInstance();
		
		return newInstance;
	}
	
	public static CcpEntitySpecifications getEspecifications(Class<?> entityClass) {
		if(entityClass.isAnnotationPresent(CcpEntitySpecifications.class) == false) {
			throw new RuntimeException("The class '" + entityClass + "' is not annoted by " + CcpEntitySpecifications.class.getName());
		}
		CcpEntitySpecifications annotation = entityClass.getAnnotation(CcpEntitySpecifications.class);
		return annotation;
	}
}
