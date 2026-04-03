package com.ccp.especifications.db.utils.entity;

import java.util.function.Consumer;

import com.ccp.business.CcpBusiness;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonRepresentation.CcpJsonFieldName;
import com.ccp.decorators.CcpReflectionConstructorDecorator;
import com.ccp.decorators.CcpStringDecorator;
import com.ccp.especifications.db.bulk.CcpExecuteBulkOperation;

public enum CcpEntityOperationType
	

{
	save {
		public CcpJsonRepresentation execute(CcpEntity entity, CcpJsonRepresentation json) {
			CcpJsonRepresentation save = entity.save(json);
			return save;
		}
	},
	delete {
		public CcpJsonRepresentation execute(CcpEntity entity, CcpJsonRepresentation json) {
			CcpJsonRepresentation delete = entity.delete(json);
			return delete;
		}
	}
	,
	 deleteAnyWhere {
		@Override
		public CcpJsonRepresentation execute(CcpEntity entity, CcpJsonRepresentation json) {
			CcpJsonRepresentation deleteAnyWhere = entity.deleteAnyWhere(json);
			return deleteAnyWhere;
		}
	}
	, transferDataTo {
		public CcpJsonRepresentation execute(CcpEntity entity, CcpJsonRepresentation json) {
			CcpEntity entities = this.getEntities(json);
			CcpJsonRepresentation transferDataTo = entity.transferDataTo(json, entities);
			return transferDataTo;
		}
	}
	, copyDataTo {
		public CcpJsonRepresentation execute(CcpEntity entity, CcpJsonRepresentation json) {
			CcpEntity entities = this.getEntities(json);
			CcpJsonRepresentation copyDataTo = entity.copyDataTo(json, entities);
			return copyDataTo;
		}
	}
	;
	
	CcpEntity getEntities(CcpJsonRepresentation json) {
		String entityToTransfer = json.getAsString(CcpEntityOperationType.Fields.entityToTransfer);
		CcpEntity entity = new CcpStringDecorator(entityToTransfer).reflection().newInstance();
		return entity;
	}
	
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
		entityToTransferTheData, entityToTransfer
	}
}
