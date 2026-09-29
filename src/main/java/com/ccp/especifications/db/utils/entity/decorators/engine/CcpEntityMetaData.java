package com.ccp.especifications.db.utils.entity.decorators.engine;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.ccp.business.CcpBusiness;
import com.ccp.constants.CcpOtherConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.db.bulk.CcpBulkEntityOperationType;
import com.ccp.especifications.db.bulk.CcpBulkItem;
import com.ccp.especifications.db.bulk.CcpErrorBulkEntityRecordNotFound;
import com.ccp.especifications.db.crud.CcpCrud;
import com.ccp.especifications.db.crud.CcpSelectUnionAll;
import com.ccp.especifications.db.crud.CcpUnionAllExecutor;
import com.ccp.especifications.db.utils.entity.CcpEntity;
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpEntityTwin;
import com.ccp.especifications.db.utils.entity.fields.CcpEntityField;
import java.util.stream.Stream;

/**
 * Holds all the metadata of an entity: index name, configurator class, fields, primary
 * key and updatable fields. Provides utility operations for ID computation, field
 * extraction, search and assembly of bulk items. Instances are created and used internally by
 * {@code CcpEntityFactory} and {@code DefaultImplementationEntity}.
 */
public final class CcpEntityMetaData { 

	public final List<String> onlyUpdatableFields;
	public final Class<?>  configurationClass; 
	public final List<String> primaryKeyNames;
	public final CcpEntityField[] allFields;
	public final CcpEntity entity;
	public final String entityName;
	
	CcpEntityMetaData(Class<?> configurationClass, Function<Class<?>, String> entityNameProducer){
		
		this.configurationClass = configurationClass;
				
		this.allFields = CcpEntityFactory.getFields(configurationClass);
		
		this.entityName = entityNameProducer.apply(configurationClass);
		Stream<CcpEntityField> allFieldsStream = Arrays.asList(this.allFields).stream();
		var primaryKeyFieldsStream = allFieldsStream.filter(field -> field.primaryKey);
		var primaryKeyNamesStream = primaryKeyFieldsStream.map(field -> field.name());

		this.primaryKeyNames = primaryKeyNamesStream.collect(Collectors.toList());
		Stream<CcpEntityField> fieldsStream = Arrays.asList(this.allFields).stream();
		var nonPrimaryKeyFieldsStream = fieldsStream
				.filter(field -> false == field.primaryKey);
				var updatableFieldsStream = nonPrimaryKeyFieldsStream
				.filter(field -> field.updatable);
				var updatableFieldNamesStream = updatableFieldsStream
				
				.map(field -> field.name());

				this.onlyUpdatableFields = updatableFieldNamesStream
				.collect(Collectors.toList());
		
		this.entity = null;
	}

	CcpEntityMetaData(Class<?> configurationClass, List<String> primaryKeyNames, List<String> onlyUpdatableFields, CcpEntityField[] allFields,	String entityName, CcpEntity entity) {
		this.onlyUpdatableFields = onlyUpdatableFields;
		this.configurationClass = configurationClass;
		this.primaryKeyNames = primaryKeyNames;
		this.entityName = entityName;
		this.allFields = allFields;
		this.entity = entity;
	}

	boolean isTwinEntity() {
		
		CcpEntityTwin annotation = this.configurationClass.getAnnotation(CcpEntityTwin.class);
		boolean isNotATwinConfiguration = annotation == null;

		if(isNotATwinConfiguration) {
			return false;
		}
		
		String twinEntityName = annotation.twinEntityName();
		boolean isTheTwinEntity = this.entityName.equals(twinEntityName);
		return isTheTwinEntity;
	}
	
	
	CcpEntityMetaData associateEntity() {
		boolean isTwin = this.isTwinEntity();
		if(isTwin) {
			CcpEntity twin = CcpEntityFactory.getEntity(this.configurationClass, x -> x.getAnnotation(CcpEntityTwin.class).twinEntityName());
			CcpEntityMetaData twinMetaData = new CcpEntityMetaData(this.configurationClass, this.primaryKeyNames, this.onlyUpdatableFields, this.allFields, this.entityName, twin);
			return twinMetaData;
		}
		
		Field field = this.configurationClass.getDeclaredField("ENTITY");
		Object object = field.get(null);
		CcpEntity entity = (CcpEntity) object;
		CcpEntityMetaData mainMetaData = new CcpEntityMetaData(this.configurationClass, this.primaryKeyNames, this.onlyUpdatableFields, this.allFields, this.entityName, entity);
		return mainMetaData;
		
	}

	/** Returns a JSON containing only the updatable fields (non primary key) present in {@code json}. */
	public CcpJsonRepresentation getOnlyUpdatableFields(CcpJsonRepresentation json) {
		CcpJsonRepresentation jsonPiece = json.getJsonPiece(this.onlyUpdatableFields);
		return jsonPiece;
	}

	private CcpJsonRepresentation getPrimaryKeyValues(CcpJsonRepresentation json) {
		boolean containsPrimaryKey = json.containsAllFields(this.primaryKeyNames);
	
		boolean primaryKeyMissing = false == containsPrimaryKey;
		
		if(primaryKeyMissing) {
			CcpErrorEntityPrimaryKeyIsMissing primaryKeyMissingError = new CcpErrorEntityPrimaryKeyIsMissing(this.entity, json);
			throw primaryKeyMissingError;
		}
		
		CcpJsonRepresentation jsonPiece = json.getJsonPiece(this.primaryKeyNames);
		return jsonPiece;
	}

	/** Returns the values of the primary key fields sorted alphabetically by field name. */
	public ArrayList<Object> getSortedPrimaryKeyValues(CcpJsonRepresentation json) {

		CcpJsonRepresentation primaryKeyValues = this.getPrimaryKeyValues(json);
		
		TreeMap<String, Object> treeMap = new TreeMap<>(primaryKeyValues.content);
		Collection<Object> sortedValues = treeMap.values();
		ArrayList<Object> onlyPrimaryKeys = new ArrayList<>(sortedValues);
		return onlyPrimaryKeys;
	}

	/** Returns a JSON containing only the fields declared in this entity's metadata. */
	public CcpJsonRepresentation getOnlyExistingFields(CcpJsonRepresentation json) {
		CcpJsonRepresentation existingFieldsJson = json.getJsonPiece(this.allFields);
		return existingFieldsJson;
	}
	
	/** Returns the index names of every associated entity (twin included). */
	public String[] getEntitiesToSelect() {
		List<CcpEntity> associatedEntities = this.entity.getAssociatedEntities();
		Stream<CcpEntity> associatedEntitiesStream = associatedEntities.stream();
		var entityNamesStream = associatedEntitiesStream.map(x -> x.getEntityMetaData().entityName);
		List<String> entityNames = entityNamesStream.collect(Collectors.toList());
		int entityNamesCount = entityNames.size();
		String[] entityNamesArray = entityNames.toArray(new String[entityNamesCount]);
		return entityNamesArray;
	}
	
	/**
	 * Finds the document by the computed ID; if it is not found, executes {@code ifNotFound} and returns
	 * its result.
	 */
	public CcpJsonRepresentation getOneByIdOrHandleItIfThisIdWasNotFound(CcpJsonRepresentation json, CcpBusiness ifNotFound) {
		try {
			CcpCrud crud = CcpDependencyInjection.getDependency(CcpCrud.class);
			String recordId = this.entity.calculateId(json);
			CcpJsonRepresentation oneById = crud.getOneById(this.entityName, recordId);
			return oneById;
			
		} catch (CcpErrorBulkEntityRecordNotFound e) {
			CcpJsonRepresentation notFoundResult = ifNotFound.execute(json);
			return notFoundResult;
		}
	}

	/** Returns {@code true} if the entity has no updatable fields (effectively read-only). */
	public boolean isNotAnUpdatableEntity() {
		boolean hasNoUpdatableFields = this.onlyUpdatableFields.isEmpty();
		return hasNoUpdatableFields;
	}

	/** Finds several documents by their IDs and returns the results grouped by this entity. */
	public CcpJsonRepresentation getMultipleByIds(Collection<CcpJsonRepresentation> jsonsToSearch) {
		boolean hasNoIdsToSearch = jsonsToSearch.isEmpty();

		if (hasNoIdsToSearch) {
			return CcpOtherConstants.EMPTY_JSON; 
		}
 
		CcpCrud crud = CcpDependencyInjection.getDependency(CcpCrud.class);

		CcpUnionAllExecutor unionAllExecutor = crud.getUnionAllExecutor();

		CcpSelectUnionAll unionAll = unionAllExecutor.unionAll(jsonsToSearch, this.entity);
		CcpJsonRepresentation entityRowsGroupedById = unionAll.getEntityRowsGroupedById(this.entity);
		return entityRowsGroupedById;
	}
	
	public CcpJsonRepresentation getPrimaryKeyValues(Supplier<CcpJsonRepresentation> jsonSupplier) {
		
		CcpJsonRepresentation json = jsonSupplier.get();
		
		CcpJsonRepresentation primaryKeyValues = this.getPrimaryKeyValues(json);
		
		return primaryKeyValues;
	}

	public String name() {
		return this.entityName;
	}
	
	public String toString() {
		return this.entityName;
	}


	public CcpBulkItem toCreateBulkItem(CcpJsonRepresentation json) {
		String id = this.entity.calculateId(json);
		CcpBulkItem bulkItem = new CcpBulkItem(json, CcpBulkEntityOperationType.create, this.entity, id);
		return bulkItem;
	}
	public CcpBulkItem toUpdateBulkItem(CcpJsonRepresentation json) {
		String id = this.entity.calculateId(json);
		CcpBulkItem bulkItem = new CcpBulkItem(json, CcpBulkEntityOperationType.update, this.entity, id);
		return bulkItem;
	}
	
	
	public CcpBulkItem toDeleteBulkItem(CcpJsonRepresentation json) {
		String id = this.entity.calculateId(json);
		CcpBulkItem bulkItem = new CcpBulkItem(json, CcpBulkEntityOperationType.delete, this.entity, id);
		return bulkItem;
	}

	
}
