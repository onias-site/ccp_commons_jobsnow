package com.ccp.especifications.db.utils.entity;

import java.util.function.Consumer;

import com.ccp.business.CcpBusiness;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonRepresentation.CcpJsonFieldName;
import com.ccp.decorators.CcpReflectionConstructorDecorator;
import com.ccp.especifications.db.bulk.CcpExecuteBulkOperation;

public enum CcpEntityOperationType
{
	save {
		
		public CcpJsonRepresentation execute(CcpEntity entity, CcpJsonRepresentation json) {
			entity.save(json);
			return json;
		}


	},
	delete {

		public CcpJsonRepresentation execute(CcpEntity entity, CcpJsonRepresentation json) {
			entity.delete(json);
			return json;
		}
	}
	,
	 deleteAnyWhere {
		@Override
		public CcpJsonRepresentation execute(CcpEntity entity, CcpJsonRepresentation json) {
			// FIXME Auto-generated method stub
			return null;
		}
	}
	
	;

	public abstract CcpJsonRepresentation execute(CcpEntity entity, CcpJsonRepresentation json);

	public CcpBusiness getTopicHandler(CcpEntity entity, CcpExecuteBulkOperation executeBulkOperation, Consumer<String[]> functionToDeleteKeysInTheCache) {
		CcpBusiness topicHandler = jsn -> this.execute(entity, jsn);
		return topicHandler;
	}

	public static CcpBusiness instanciateFunction(Class<?> x) {
		CcpReflectionConstructorDecorator reflection = new CcpReflectionConstructorDecorator(x);

		CcpBusiness newInstance = reflection.newInstance();
		
		return newInstance;
	}
	

	public Class<?> getJsonValidationClass(CcpEntity entity){
		return this.getClass();
	}
	
	public static enum Fields implements CcpJsonFieldName{
		entityToTransferTheData
	}
}
