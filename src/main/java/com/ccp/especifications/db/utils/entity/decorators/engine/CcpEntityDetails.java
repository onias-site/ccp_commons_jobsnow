package com.ccp.especifications.db.utils.entity.decorators.engine;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.stream.Collectors;

import com.ccp.business.CcpBusiness;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.bulk.CcpBulkEntityOperationType;
import com.ccp.especifications.db.bulk.CcpBulkItem;
import com.ccp.especifications.db.utils.entity.CcpEntity;
import com.ccp.especifications.db.utils.entity.CcpEntityOperationType;
import com.ccp.especifications.db.utils.entity.fields.CcpEntityField;
import com.ccp.especifications.db.utils.entity.fields.CcpErrorEntityPrimaryKeyIsMissing;

public final class CcpEntityDetails {

	public final Class<?>  configurationClass; 
	public final List<String> primaryKeyNames;
	public final CcpEntityField[] allFields;
	private final CcpEntity entity;
	public final String entityName;
	
	CcpEntityDetails(Class<?> configurationClass, Function<Class<?>, String> entntyNameProducer){
		
		this.configurationClass = configurationClass;
				
		this.allFields = CcpEntityFactory.getFields(configurationClass);
		
		this.entityName = entntyNameProducer.apply(configurationClass);
		
		this.primaryKeyNames = Arrays.asList(this.allFields).stream().filter(field -> field.primaryKey).map(field -> field.name()).collect(Collectors.toList());
		
		this.entity = null;
	}

	CcpEntityDetails(Class<?> configurationClass, List<String> primaryKeyNames, CcpEntityField[] allFields,	String entityName, CcpEntity entity) {
		this.configurationClass = configurationClass;
		this.primaryKeyNames = primaryKeyNames;
		this.entityName = entityName;
		this.allFields = allFields;
		this.entity = entity;
	}

	CcpEntityDetails associateEntity() {
		try {
			Field field = this.configurationClass.getDeclaredField("ENTITY");
			Object object = field.get(null);
			CcpEntity entity = (CcpEntity) object;
			
			return new CcpEntityDetails(this.configurationClass, this.primaryKeyNames, this.allFields, this.entityName, entity);
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public CcpBusiness getOperationCallback(CcpEntityOperationType operation){
		//FIXME
		return json -> operation.execute(null, json);
	}

	public CcpJsonRepresentation getPrimaryKeyValues(CcpJsonRepresentation json) {
		
		boolean primaryKeyMissing = false == json.containsAllFields(this.primaryKeyNames);
		
		if(primaryKeyMissing) {
			//FIXME
			throw new CcpErrorEntityPrimaryKeyIsMissing(null, json);
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
		//FIXME
		CcpBulkItem item = new CcpBulkItem(json, operation, null);
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

	public List<CcpBulkItem> getBulkItemsList(CcpJsonRepresentation json, CcpBulkEntityOperationType operation) {
		CcpJsonRepresentation handledJson = this.entity.getHandledJson(json);
		CcpJsonRepresentation onlyExistingFields = this.getOnlyExistingFields(handledJson);
		List<CcpBulkItem> bulkItems = this.entity.toBulkItems(onlyExistingFields, operation);
		return bulkItems;
	}

	
}
