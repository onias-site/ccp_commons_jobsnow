package com.ccp.especifications.db.utils.entity.decorators.engine2;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.TreeMap;

import com.ccp.business.CcpBusiness;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.bulk.CcpBulkEntityOperationType;
import com.ccp.especifications.db.bulk.CcpBulkItem;
import com.ccp.especifications.db.utils.entity.CcpEntity;
import com.ccp.especifications.db.utils.entity.CcpEntityOperationType;
import com.ccp.especifications.db.utils.entity.fields.CcpEntityField;
import com.ccp.especifications.db.utils.entity.fields.CcpErrorEntityPrimaryKeyIsMissing;

public class CcpEntityDetails {
	public final List<String> primaryKeyNames;
	public final CcpEntityField[] allFields;
	public final Class<?> fieldsClass;
	public final String entityClass;
	public final String entityName;
	public final CcpEntity entity;
	
	public static CcpEntityDetails getEntityDetails(CcpEntity entity) {
		//FIXME
		return null;
	}
	
	//FIXME
	public CcpEntityDetails(CcpEntity entity) {
		this.entity = entity;
		this.allFields = null;
		this.entityClass = null;
		this.entityName = null;
		this.fieldsClass = null;
		this.primaryKeyNames = null;
	}
	
	public CcpBusiness getOperationCallback(CcpEntityOperationType operation){
		return json -> operation.execute(this.entity, json);
	}

	public CcpJsonRepresentation getPrimaryKeyValues(CcpJsonRepresentation json) {
		
		boolean primaryKeyMissing = false == json.containsAllFields(this.primaryKeyNames);
		
		if(primaryKeyMissing) {
			throw new CcpErrorEntityPrimaryKeyIsMissing(this.entity, json);
		}
		
		CcpJsonRepresentation jsonPiece = json.getJsonPiece(this.primaryKeyNames);
		return jsonPiece;
	}

	
	public CcpBulkItem toSaveBulkItem(CcpJsonRepresentation json) {
		CcpBulkItem item = this.toBulkItem(json, CcpBulkEntityOperationType.create);
		return item;
	}

	public CcpBulkItem toDeleteBulkItem(CcpJsonRepresentation json) {
		CcpBulkItem item = this.toBulkItem(json, CcpBulkEntityOperationType.delete);
		return item;
	}

	private CcpBulkItem toBulkItem(CcpJsonRepresentation json, CcpBulkEntityOperationType operation) {
		CcpBulkItem item = new CcpBulkItem(json, operation, this.entity);
		return item;
	}
	
	public ArrayList<Object> getSortedPrimaryKeyValues(CcpJsonRepresentation json) {

		CcpJsonRepresentation primaryKeyValues = this.getPrimaryKeyValues(json);
		
		TreeMap<String, Object> treeMap = new TreeMap<>(primaryKeyValues.content);
		Collection<Object> values2 = treeMap.values();
		ArrayList<Object> onlyPrimaryKeys = new ArrayList<>(values2);
		return onlyPrimaryKeys;
	}

	public CcpJsonRepresentation getOnlyExistingFields(CcpJsonRepresentation json) {
		CcpJsonRepresentation subMap = json.getJsonPiece(this.allFields);
		return subMap;
	}

}
