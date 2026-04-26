package com.ccp.especifications.db.bulk;

import com.ccp.constantes.CcpOtherConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonRepresentation.CcpJsonFieldName;
import com.ccp.especifications.db.utils.entity.CcpEntity;
import com.ccp.especifications.db.utils.entity.decorators.engine.CcpEntityDetails;

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
	
	public CcpBulkItem(CcpBulkItem other, CcpBulkEntityOperationType operation) {
		this(other.json, operation, other.entity, other.id);
	}

	public CcpBulkItem(CcpJsonRepresentation json, CcpBulkEntityOperationType operation, CcpEntity entity, String id) {
		this.operation = operation;
		this.entity = entity;
		this.json = json;
		this.id = id;
	}

	public String toString() {
		CcpJsonRepresentation put = this.asMap();
		CcpJsonRepresentation jsonPiece = put.getJsonPiece(CcpBulkItemFields.values());
		String string = jsonPiece.toString();
		return string;
	}

	public CcpJsonRepresentation asMap() {
		CcpEntityDetails entityDetails = this.entity.getEntityDetails();
		CcpJsonRepresentation put = CcpOtherConstants.EMPTY_JSON
				.put(CcpBulkItemFields.operation, this.operation)
				.put(CcpBulkItemFields.entity, entityDetails.entityName)
				.put(JsonFieldNames.json, this.json)
				.put(CcpBulkItemFields.id, this.id);
		return put;
	}
	
	public int hashCode() {
		String string = this.entity + "_" + this.id ;
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
			
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
