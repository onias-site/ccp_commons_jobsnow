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

	/** The wrapped entity (the next link of the decorator chain). */
	protected final CcpEntity entity;

	/** Wraps the entity targeted by the delegation. */
	public CcpEntityDelegator(CcpEntity entity) {
		this.entity = entity;
	}

	/** Delegates to the wrapped entity. {@inheritDoc} */
	public String calculateId(CcpJsonRepresentation json) {
		String recordId = this.entity.calculateId(json);
		return recordId;
	}

	/** Delegates to the wrapped entity. {@inheritDoc} */
	public CcpEntityMetaData getEntityMetaData() {
		CcpEntityMetaData entityDetails = this.entity.getEntityMetaData();
		return entityDetails;
	}

	/** Delegates to the wrapped entity. {@inheritDoc} */
	public boolean delete(CcpJsonRepresentation json) {
		boolean deleted = this.entity.delete(json);
		return deleted;
	}

	/** Delegates to the wrapped entity. {@inheritDoc} */
	public boolean deleteAnyWhere(CcpJsonRepresentation json) {
		boolean deleted = this.entity.deleteAnyWhere(json);
		return deleted;
	}

	/** Delegates to the wrapped entity. {@inheritDoc} */
	public CcpJsonRepresentation getOneById(CcpJsonRepresentation json) {
		CcpJsonRepresentation oneById = this.entity.getOneById(json);
		return oneById;
	}

	/** Delegates to the wrapped entity. {@inheritDoc} */
	public CcpJsonRepresentation getOneByIdAnyWhere(CcpJsonRepresentation json) {
		CcpJsonRepresentation oneByIdAnywhere = this.entity.getOneByIdAnyWhere(json);
		return oneByIdAnywhere;
	}

	/** Delegates to the wrapped entity. {@inheritDoc} */
	public List<CcpJsonRepresentation> getParametersToSearch(CcpJsonRepresentation json) {
		List<CcpJsonRepresentation> parametersToSearch = this.entity.getParametersToSearch(json);
		return parametersToSearch;
	}

	/** Delegates to the wrapped entity. {@inheritDoc} */
	public CcpEntity getTwinEntity(CcpEntityDecoratorType... decoratorsToAvoid) {
		CcpEntity twinEntity = this.entity.getTwinEntity(decoratorsToAvoid);
		return twinEntity;
	}

	/**
	 * Returns the wrapped entity, the next link of the decorator chain.
	 * @return the wrapped entity
	 */
	public CcpEntity getWrapedEntity() {
		return this.entity;
	}

	/** Delegates to the wrapped entity. {@inheritDoc} */
	public boolean isPresentInThisUnionAll(CcpSelectUnionAll unionAll, CcpJsonRepresentation json) {
		boolean presentInThisUnionAll = this.entity.isPresentInThisUnionAll(unionAll, json);
		return presentInThisUnionAll;
	}

	/** Delegates to the wrapped entity. {@inheritDoc} */
	public boolean save(CcpJsonRepresentation json) {
		boolean inserted = this.entity.save(json);
		return inserted;
	}

	/**
	 * Describes the entity name and the decorator chain, from this decorator inwards, as {@code name = A->B->C}.
	 * @return the description followed by a line feed
	 */
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
	
	/**
	 * Delegates to the wrapped entity, so a decorator equals what its wrapped entity equals (not symmetric).
	 * @param obj the other object
	 * @return the result of the wrapped entity
	 */
	public boolean equals(Object obj) {
		boolean equals = this.entity.equals(obj);
		return equals;
	}
	
	/**
	 * Delegates to the wrapped entity.
	 * @return the hash code of the wrapped entity
	 */
	public int hashCode() {
		int hashCode = this.entity.hashCode();
		return hashCode;
	}
	
	/** Delegates to the wrapped entity. {@inheritDoc} */
	public boolean exists(CcpJsonRepresentation json) {
		boolean exists = this.entity.exists(json);
		return exists;
	}

	/** Delegates to the wrapped entity. {@inheritDoc} */
	public <T> T throwException() {
		T result = this.entity.throwException();
		return result;
	}

	/** Delegates to the wrapped entity. {@inheritDoc} */
	public List<CcpEntity> getAssociatedEntities() {
		List<CcpEntity> associatedEntities = this.entity.getAssociatedEntities();
		return associatedEntities;
	}

	/** Delegates to the wrapped entity. {@inheritDoc} */
	public CcpJsonRepresentation getHandledJson(CcpJsonRepresentation json) {
		CcpJsonRepresentation handledJson = this.entity.getHandledJson(json);
		return handledJson;
	}

	/** Delegates to the wrapped entity. {@inheritDoc} */
	public List<CcpBulkItem> toBulkItems(CcpJsonRepresentation json, CcpBulkEntityOperationType operation) {
		List<CcpBulkItem> bulkItems = this.entity.toBulkItems(json, operation);
		return bulkItems;
	}

	/** Delegates to the wrapped entity. {@inheritDoc} */
	public boolean copyDataTo(CcpJsonRepresentation json, CcpEntity entities) {
		boolean copied = this.entity.copyDataTo(json, entities);
		return copied;
	}

	/** Delegates to the wrapped entity. {@inheritDoc} */
	public boolean transferDataTo(CcpJsonRepresentation json, CcpEntity entities) {
		boolean transferred = this.entity.transferDataTo(json, entities);
		return transferred;
	}

	/** Delegates to the wrapped entity. {@inheritDoc} */
	public CcpJsonRepresentation validateJson(CcpJsonRepresentation json) {
		CcpJsonRepresentation validatedJson = this.entity.validateJson(json);
		return validatedJson;
	}

	/** Delegates to the wrapped entity. {@inheritDoc} */
	public CcpJsonRepresentation getIdToSearchDisposableRecord(CcpJsonRepresentation json) {
		CcpJsonRepresentation idToSearchDisposableRecord = this.entity.getIdToSearchDisposableRecord(json);
		return idToSearchDisposableRecord;
	}

	/** Delegates to the wrapped entity. {@inheritDoc} */
	public CcpJsonRepresentation getRecordFromUnionAll(CcpSelectUnionAll unionAll, Supplier<CcpJsonRepresentation> jsonSupplier) {
		CcpJsonRepresentation recordFromUnionAll = this.entity.getRecordFromUnionAll(unionAll, jsonSupplier);
		return recordFromUnionAll;
	}

	/** Delegates to the wrapped entity. {@inheritDoc} */
	public String getValue() {
		String value = this.entity.getValue();
		return value;
	}

	/** Delegates to the wrapped entity. {@inheritDoc} */
	public String name() {
		String name = this.entity.name();
		return name;
	}
	
	
}
