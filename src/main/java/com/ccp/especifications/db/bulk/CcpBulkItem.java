package com.ccp.especifications.db.bulk;

import java.util.function.Function;

import com.ccp.constantes.CcpOtherConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.utils.CcpEntity;
import com.ccp.exceptions.db.utils.CcpEntityJsonTransformerError;


enum CcpBulkItemConstants{
	id, json, entity, operation 
}
public class CcpBulkItem {

	public final CcpEntityBulkOperationType operation;
	public final CcpJsonRepresentation json;
	public final CcpEntity entity;
	public final String id;
	
	public CcpBulkItem(CcpJsonRepresentation json, CcpEntityBulkOperationType operation, CcpEntity entity, 
			String id) {
		this(json, operation, entity, id, x -> entity.getOnlyExistingFieldsAndHandledJson(x));
	}
	
	public CcpBulkItem(CcpJsonRepresentation json, CcpEntityBulkOperationType operation, CcpEntity entity, 
			String id, Function<CcpJsonRepresentation, CcpJsonRepresentation> transformer) {
		CcpJsonRepresentation transformedJson = json;
		try {
			transformedJson = transformer.apply(json);
		} catch (CcpEntityJsonTransformerError e) {
		}
		this.json = transformedJson;
		this.operation = operation;
		this.entity = entity;
		this.id = id;
	}


	public String toString() {
		CcpJsonRepresentation put = this.asMap();
		CcpJsonRepresentation jsonPiece = put.getJsonPiece("entity", "operation", "id");
		String string = jsonPiece.toString();
		return string;
	}

	public CcpJsonRepresentation asMap() {
		String entityName = this.entity.getEntityName();
		CcpJsonRepresentation put = CcpOtherConstants.EMPTY_JSON
				.put(CcpBulkItemConstants.operation, this.operation)
				.put(CcpBulkItemConstants.entity, entityName)
				.put(CcpBulkItemConstants.json, this.json)
				.put(CcpBulkItemConstants.id, this.id);
		return put;
	}
	
	public int hashCode() {
		String string = this.entity + "_" + this.id + "_" + this.operation;
		int hashCode = string.hashCode();
		return hashCode;
	}

	public boolean equals(Object obj) {
		try {
			CcpBulkItem other = (CcpBulkItem)obj;
			
			boolean differentEntity = other.entity.equals(this.entity) == false;
			
			if(differentEntity) {
				return false;
			}
			
			boolean differentId = other.id.equals(this.id) == false;
			
			if(differentId) {
				return false;
			}
			
			boolean differentOperation = other.operation.equals(this.operation) == false;
			
			if(differentOperation) {
				return false;
			}
			
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	public CcpBulkItem getSecondRecordToBulkOperation() {
		CcpBulkItem recordToBulkOperation = this.entity.getRecordCopyToBulkOperation(this.json, this.operation);
		return recordToBulkOperation;
	}

	
}
