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
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpEntityTwin;
import com.ccp.especifications.db.utils.entity.fields.CcpEntityField;
import com.ccp.especifications.db.utils.entity.fields.CcpErrorEntityPrimaryKeyIsMissing;

public final class CcpEntityDetails {

	public final List<String> onlyUpdatableFields;
	public final Class<?>  configurationClass; 
	public final List<String> primaryKeyNames;
	public final CcpEntityField[] allFields;
	public final CcpEntity entity;
	public final String entityName;
	
	CcpEntityDetails(Class<?> configurationClass, Function<Class<?>, String> entityNameProducer){
		
		this.configurationClass = configurationClass;
				
		this.allFields = CcpEntityFactory.getFields(configurationClass);
		
		this.entityName = entityNameProducer.apply(configurationClass);
		
		this.primaryKeyNames = Arrays.asList(this.allFields).stream().filter(field -> field.primaryKey).map(field -> field.name()).collect(Collectors.toList());

		this.onlyUpdatableFields = Arrays.asList(this.allFields).stream()
				.filter(field -> false == field.primaryKey)
				.filter(field -> field.updatable)
				
				.map(field -> field.name())
				.collect(Collectors.toList());
		
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

	boolean isTwinEntity() {
		
		CcpEntityTwin annotation = this.configurationClass.getAnnotation(CcpEntityTwin.class);
		
		if(annotation == null) {
			return false;
		}
		
		String twinEntityName = annotation.twinEntityName();
		boolean equals = this.entityName.equals(twinEntityName);
		return equals;
	}
	
	
	CcpEntityDetails associateEntity() {
		try {
			boolean twinEntity = this.isTwinEntity();
			if(twinEntity) {
				CcpEntity twin = CcpEntityFactory.getEntity(this.configurationClass, x -> x.getAnnotation(CcpEntityTwin.class).twinEntityName());
				return new CcpEntityDetails(this.configurationClass, this.primaryKeyNames, this.onlyUpdatableFields, this.allFields, this.entityName, twin);
			}
			
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

	public boolean isNotAnUpdatableEntity() {
		boolean empty = this.onlyUpdatableFields.isEmpty();
		return empty;
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
