package com.ccp.especifications.db.utils.entity.decorators.engine;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Supplier;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.bulk.CcpBulkEntityOperationType;
import com.ccp.especifications.db.bulk.CcpBulkItem;
import com.ccp.especifications.db.crud.CcpSelectUnionAll;
import com.ccp.especifications.db.utils.entity.CcpEntity;
import com.ccp.especifications.db.utils.entity.decorators.interfaces.CcpEntityDecoratorType;

/**
 * Base implementation of the Decorator pattern for {@code CcpEntity}. Wraps an entity and delegates
 * every operation to it, serving as the extension point for the specialized decorators
 * (cache, twin, versioning, etc.). The {@code toString()} method shows the chain of applied
 * decorators.
 */
public class CcpEntityDelegator implements CcpEntity{

	protected final CcpEntity entity;

	/** Wraps the entity targeted by the delegation. */
	public CcpEntityDelegator(CcpEntity entity) {
		this.entity = entity;
	}

	public String calculateId(CcpJsonRepresentation json) {
		String recordId = this.entity.calculateId(json);
		return recordId;
	}

	public CcpEntityMetaData getEntityMetaData() {
		CcpEntityMetaData entityDetails = this.entity.getEntityMetaData();
		return entityDetails;
	}

	public boolean delete(CcpJsonRepresentation json) {
		boolean deleted = this.entity.delete(json);
		return deleted;
	}

	public boolean deleteAnyWhere(CcpJsonRepresentation json) {
		boolean deleted = this.entity.deleteAnyWhere(json);
		return deleted;
	}

	public CcpJsonRepresentation getOneById(CcpJsonRepresentation json) {
		CcpJsonRepresentation oneById = this.entity.getOneById(json);
		return oneById;
	}

	public CcpJsonRepresentation getOneByIdAnyWhere(CcpJsonRepresentation json) {
		CcpJsonRepresentation oneByIdAnywhere = this.entity.getOneByIdAnyWhere(json);
		return oneByIdAnywhere;
	}

	public List<CcpJsonRepresentation> getParametersToSearch(CcpJsonRepresentation json) {
		List<CcpJsonRepresentation> parametersToSearch = this.entity.getParametersToSearch(json);
		return parametersToSearch;
	}

	public CcpEntity getTwinEntity(CcpEntityDecoratorType... decoratorsToAvoid) {
		CcpEntity twinEntity = this.entity.getTwinEntity(decoratorsToAvoid);
		return twinEntity;
	}

	public CcpEntity getWrapedEntity() {
		return this.entity;
	}

	public boolean isPresentInThisUnionAll(CcpSelectUnionAll unionAll, CcpJsonRepresentation json) {
		boolean presentInThisUnionAll = this.entity.isPresentInThisUnionAll(unionAll, json);
		return presentInThisUnionAll;
	}

	public boolean save(CcpJsonRepresentation json) {
		boolean inserted = this.entity.save(json);
		return inserted;
	}

	public String toString() {
		
		CcpEntity wrapedEntity = this;
		Set<String> decoratorNames = new LinkedHashSet<>();
		var clazz = this.getClass();
		var simpleName = clazz.getSimpleName();
		decoratorNames.add(simpleName);
		do {
			
		}while(decoratorNames.add((wrapedEntity = wrapedEntity.getWrapedEntity()).getClass().getSimpleName()));
		String decoratorNamesAsText = decoratorNames.toString();
		String withoutSpaces = decoratorNamesAsText.replace(" ", "");
		String decoratorChain = withoutSpaces.replace(",", "->");
		CcpEntityMetaData entityDetails = this.getEntityMetaData();
		String entityNameWithSeparator = entityDetails.entityName + " = ";
		String description = entityNameWithSeparator + decoratorChain;
		return description + "\n"; 
	}
	
	public boolean equals(Object obj) {
		boolean equals = this.entity.equals(obj);
		return equals;
	}
	
	public int hashCode() {
		int hashCode = this.entity.hashCode();
		return hashCode;
	}
	
	public boolean exists(CcpJsonRepresentation json) {
		boolean exists = this.entity.exists(json);
		return exists;
	}

	public <T> T throwException() {
		T result = this.entity.throwException();
		return result;
	}

	public List<CcpEntity> getAssociatedEntities() {
		List<CcpEntity> associatedEntities = this.entity.getAssociatedEntities();
		return associatedEntities;
	}

	public CcpJsonRepresentation getHandledJson(CcpJsonRepresentation json) {
		CcpJsonRepresentation handledJson = this.entity.getHandledJson(json);
		return handledJson;
	}

	public List<CcpBulkItem> toBulkItems(CcpJsonRepresentation json, CcpBulkEntityOperationType operation) {
		List<CcpBulkItem> bulkItems = this.entity.toBulkItems(json, operation);
		return bulkItems;
	}

	public boolean copyDataTo(CcpJsonRepresentation json, CcpEntity entities) {
		boolean copied = this.entity.copyDataTo(json, entities);
		return copied;
	}

	public boolean transferDataTo(CcpJsonRepresentation json, CcpEntity entities) {
		boolean transferred = this.entity.transferDataTo(json, entities);
		return transferred;
	}

	public CcpJsonRepresentation validateJson(CcpJsonRepresentation json) {
		CcpJsonRepresentation validatedJson = this.entity.validateJson(json);
		return validatedJson;
	}

	public CcpJsonRepresentation getIdToSearchDisposableRecord(CcpJsonRepresentation json) {
		CcpJsonRepresentation idToSearchDisposableRecord = this.entity.getIdToSearchDisposableRecord(json);
		return idToSearchDisposableRecord;
	}

	public CcpJsonRepresentation getRecordFromUnionAll(CcpSelectUnionAll unionAll, Supplier<CcpJsonRepresentation> jsonSupplier) {
		CcpJsonRepresentation recordFromUnionAll = this.entity.getRecordFromUnionAll(unionAll, jsonSupplier);
		return recordFromUnionAll;
	}

	public String getValue() {
		String value = this.entity.getValue();
		return value;
	}

	public String name() {
		String name = this.entity.name();
		return name;
	}
	
	
}
