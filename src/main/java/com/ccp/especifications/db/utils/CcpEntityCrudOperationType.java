package com.ccp.especifications.db.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpReflectionConstructorDecorator;
import com.ccp.especifications.db.utils.decorators.configurations.CcpEntityOperationSpecification;
import com.ccp.especifications.db.utils.decorators.configurations.CcpEntitySpecifications;

public enum CcpEntityCrudOperationType 
{
	save {
		public CcpJsonRepresentation execute(CcpEntity entity, CcpJsonRepresentation json) {
			entity.createOrUpdate(json);
			return json;
		}

		public List<Function<CcpJsonRepresentation, CcpJsonRepresentation>> getStepsAfter(Class<?> entityClass) {
			CcpEntitySpecifications especifications = super.getEspecifications(entityClass);
			CcpEntityOperationSpecification operation = especifications.save();
			Class<?>[] callBacks = operation.afterOperation();
			List<Function<CcpJsonRepresentation, CcpJsonRepresentation>> collect = Arrays.asList(callBacks).stream().map(x -> instanciateFunction(x)).collect(Collectors.toList());
			return  collect;
		}
	},
	delete {

		public CcpJsonRepresentation execute(CcpEntity entity, CcpJsonRepresentation json) {
			entity.delete(json);
			return json;
		}

		public List<Function<CcpJsonRepresentation, CcpJsonRepresentation>> getStepsAfter(Class<?> entityClass) {
			CcpEntitySpecifications especifications = super.getEspecifications(entityClass);
			CcpEntityOperationSpecification operation = especifications.delete();
			Class<?>[] callBacks = operation.afterOperation();
			List<Function<CcpJsonRepresentation, CcpJsonRepresentation>> collect = Arrays.asList(callBacks).stream().map(x -> instanciateFunction(x)).collect(Collectors.toList());
			return  collect;
		}
	}
	,
	
	none {

		public CcpJsonRepresentation execute(CcpEntity entity, CcpJsonRepresentation json) {
			return json;
		}


		public List<Function<CcpJsonRepresentation, CcpJsonRepresentation>> getStepsAfter(Class<?> entityClass) {
			return new ArrayList<>();
		}

	}
	
	;

	public abstract CcpJsonRepresentation execute(CcpEntity entity, CcpJsonRepresentation json);

	public abstract List<Function<CcpJsonRepresentation, CcpJsonRepresentation>> getStepsAfter(Class<?> entityClass);

	public static Function<CcpJsonRepresentation, CcpJsonRepresentation> instanciateFunction(Class<?> x) {
		CcpReflectionConstructorDecorator reflection = new CcpReflectionConstructorDecorator(x);

		Function<CcpJsonRepresentation, CcpJsonRepresentation> newInstance = reflection.newInstance();
		
		return newInstance;
	}
	
	public static CcpEntitySpecifications getEspecifications(Class<?> entityClass) {
		if(entityClass.isAnnotationPresent(CcpEntitySpecifications.class) == false) {
			throw new RuntimeException("The class '" + entityClass + "' is not annoted by " + CcpEntitySpecifications.class.getName());
		}
		//TODO PASSAR PARA O DECORATOR
		CcpEntitySpecifications annotation = entityClass.getAnnotation(CcpEntitySpecifications.class);
		return annotation;
	}
	


}
