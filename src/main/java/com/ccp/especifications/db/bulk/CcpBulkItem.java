package com.ccp.especifications.db.bulk;

import com.ccp.business.CcpBusiness;
import com.ccp.constantes.CcpOtherConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonRepresentation.CcpJsonFieldName;
import com.ccp.especifications.db.utils.entity.CcpEntity;
import com.ccp.especifications.db.utils.entity.fields.CcpEntityJsonTransformerError;

enum CcpBulkItemFields implements CcpJsonFieldName{
	id,  entity, operation 
}


public class CcpBulkItem {
	enum JsonFieldNames implements CcpJsonFieldName{
		json
	}

	public final CcpBulkEntityOperationType operation;
	public final CcpJsonRepresentation json;
	public final CcpEntity entity;
	public final String id;
	
	public CcpBulkItem(CcpJsonRepresentation json, CcpBulkEntityOperationType operation, CcpEntity entity, String id) {
		this(json, operation, entity, id, operation.getTransformers(entity));
	}
	
	public CcpBulkItem(CcpJsonRepresentation json, CcpBulkEntityOperationType operation, CcpEntity entity, String id, CcpBusiness... transformers) {
		CcpJsonRepresentation transformedJson = json;
		for (CcpBusiness transformer : transformers) {
			try {
				transformedJson = transformer.apply(json);
			} catch (CcpEntityJsonTransformerError e) {
			}
			
		}
		this.json = transformedJson;
		this.operation = operation;
		this.entity = entity;
		this.id = id;
	}


	public String toString() {
		CcpJsonRepresentation put = this.asMap();
		CcpJsonRepresentation jsonPiece = put.getJsonPiece(CcpBulkItemFields.values());
		String string = jsonPiece.toString();
		return string;
	}

	public CcpJsonRepresentation asMap() {
		String entityName = this.entity.getEntityName();
		CcpJsonRepresentation put = CcpOtherConstants.EMPTY_JSON
				.put(CcpBulkItemFields.operation, this.operation)
				.put(CcpBulkItemFields.entity, entityName)
				.put(JsonFieldNames.json, this.json)
				.put(CcpBulkItemFields.id, this.id);
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
			
			boolean differentEntity = false == other.entity.equals(this.entity);
			
			if(differentEntity) {
				return false;
			}
			
			boolean differentId = false == other.id.equals(this.id);
			
			if(differentId) {
				return false;
			}
			
			boolean differentOperation = false == other.operation.equals(this.operation);
			
			if(differentOperation) {
				return false;
			}
			
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
