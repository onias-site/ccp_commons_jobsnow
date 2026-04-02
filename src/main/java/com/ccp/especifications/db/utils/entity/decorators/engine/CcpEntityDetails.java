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
import com.ccp.constantes.CcpOtherConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.db.bulk.CcpErrorBulkEntityRecordNotFound;
import com.ccp.especifications.db.crud.CcpCrud;
import com.ccp.especifications.db.crud.CcpSelectUnionAll;
import com.ccp.especifications.db.crud.CcpUnionAllExecutor;
import com.ccp.especifications.db.utils.entity.CcpEntity;
import com.ccp.especifications.db.utils.entity.CcpEntityOperationType;
import com.ccp.especifications.db.utils.entity.fields.CcpEntityField;
import com.ccp.especifications.db.utils.entity.fields.CcpErrorEntityPrimaryKeyIsMissing;

public final class CcpEntityDetails {

	public final List<String> onlyUpdatableFields;
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

		this.onlyUpdatableFields = Arrays.asList(this.allFields).stream().filter(field -> field.updatable).map(field -> field.name()).collect(Collectors.toList());
		
		this.entity = null;
	}

	CcpEntityDetails(Class<?> configurationClass, List<String> primaryKeyNames, List<String> onlyUpdatableFields, CcpEntityField[] allFields,	String entityName, CcpEntity entity) {
		this.onlyUpdatableFields = onlyUpdatableFields;
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
			
			return new CcpEntityDetails(this.configurationClass, this.primaryKeyNames, this.onlyUpdatableFields, this.allFields, this.entityName, entity);
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public CcpBusiness getOperationCallback(CcpEntityOperationType operation){
		return json -> operation.execute(this.entity, json);
	}

	public CcpJsonRepresentation getOnlyUpdatableFields(CcpJsonRepresentation json) {
		CcpJsonRepresentation jsonPiece = json.getJsonPiece(this.onlyUpdatableFields);
		return jsonPiece;
	}

	public CcpJsonRepresentation getPrimaryKeyValues(CcpJsonRepresentation json) {
		
		boolean primaryKeyMissing = false == json.containsAllFields(this.primaryKeyNames);
		
		if(primaryKeyMissing) {
			throw new CcpErrorEntityPrimaryKeyIsMissing(this.entity, json);
		}
		
		CcpJsonRepresentation jsonPiece = json.getJsonPiece(this.primaryKeyNames);
		return jsonPiece;
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
	
	public String[] getEntitiesToSelect() {
		List<CcpEntity> associatedEntities = this.entity.getAssociatedEntities();
		List<String> collect = associatedEntities.stream().map(x -> x.getEntityDetails().entityName).collect(Collectors.toList());
		String[] array = collect.toArray(new String[collect.size()]);
		return array;
	}
	
	public CcpJsonRepresentation getOneByIdOrHandleItIfThisIdWasNotFound(CcpJsonRepresentation json, CcpBusiness ifNotFound) {
		try {
			CcpCrud crud = CcpDependencyInjection.getDependency(CcpCrud.class);
			String calculateId = this.entity.calculateId(json);
			CcpJsonRepresentation oneById = crud.getOneById(this.entityName, calculateId);
			return oneById;
			
		} catch (CcpErrorBulkEntityRecordNotFound e) {
			CcpJsonRepresentation execute = ifNotFound.apply(json);
			return execute;
		}
	}

	public CcpJsonRepresentation getRequiredEntityRow(CcpSelectUnionAll unionAll, CcpJsonRepresentation json) {
		
		boolean notFound = false == this.entity.isPresentInThisUnionAll(unionAll, json);

		if(notFound) {
			throw new CcpErrorBulkEntityRecordNotFound(this.entity, json);
		}
		
		CcpJsonRepresentation entityRow = this.entity.getRecordFromUnionAll(unionAll, json);
		
		return entityRow;
	}
	
	public boolean isNotAnUpdatableEntity() {
		int fieldsLength = this.allFields.length;
		int primaryKeyNamesLength = this.primaryKeyNames.size();
		int onlyUpdatableFieldsLength = this.onlyUpdatableFields.size();
		int remainingFields = fieldsLength - primaryKeyNamesLength - onlyUpdatableFieldsLength;
		
		boolean isNotAnUpdatableEntity = remainingFields <= 0;
		return isNotAnUpdatableEntity;
	}

	
	public CcpJsonRepresentation getMultipleByIds(Collection<CcpJsonRepresentation> asList) {
		boolean hasNoIdsToSearch = asList.isEmpty();

		if (hasNoIdsToSearch) {
			return CcpOtherConstants.EMPTY_JSON;
		}

		CcpCrud crud = CcpDependencyInjection.getDependency(CcpCrud.class);

		CcpUnionAllExecutor unionAllExecutor = crud.getUnionAllExecutor();

		CcpSelectUnionAll unionAll = unionAllExecutor.unionAll(asList, this.entity);
		CcpJsonRepresentation innerJson = unionAll.condensed.getDynamicVersion().getInnerJson(this.entityName);
		return innerJson;
	}

}
