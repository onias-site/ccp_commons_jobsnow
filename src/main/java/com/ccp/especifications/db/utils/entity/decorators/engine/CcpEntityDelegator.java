package com.ccp.especifications.db.utils.entity.decorators.engine;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.bulk.CcpBulkEntityOperationType;
import com.ccp.especifications.db.bulk.CcpBulkItem;
import com.ccp.especifications.db.crud.CcpSelectUnionAll;
import com.ccp.especifications.db.utils.entity.CcpEntity;

public class CcpEntityDelegator implements CcpEntity{
	
	protected final CcpEntity entity;

	public CcpEntityDelegator(CcpEntity entity) {
		this.entity = entity;
	}

	public String calculateId(CcpJsonRepresentation json) {
		String calculateId = this.entity.calculateId(json);
		return calculateId;
	}

	public CcpEntityDetails getEntityDetails() {
		CcpEntityDetails entityDetails = this.entity.getEntityDetails();
		return entityDetails;
	}

	public CcpJsonRepresentation delete(CcpJsonRepresentation json) {
		CcpJsonRepresentation delete = this.entity.delete(json);
		return delete;
	}

	public CcpJsonRepresentation deleteAnyWhere(CcpJsonRepresentation json) {
		CcpJsonRepresentation deleteAnyWhere = this.entity.deleteAnyWhere(json);
		return deleteAnyWhere;
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

	public CcpJsonRepresentation getRecordFromUnionAll(CcpSelectUnionAll unionAll, CcpJsonRepresentation json) {
		CcpJsonRepresentation recordFromUnionAll = this.entity.getRecordFromUnionAll(unionAll, json);
		return recordFromUnionAll;
	}


	public CcpEntity getTwinEntity() {
		CcpEntity twinEntity = this.entity.getTwinEntity();
		return twinEntity;
	}

	public CcpEntity getWrapedEntity() {
		return this.entity;
	}

	public boolean isPresentInThisUnionAll(CcpSelectUnionAll unionAll, CcpJsonRepresentation json) {
		boolean presentInThisUnionAll = this.entity.isPresentInThisUnionAll(unionAll, json);
		return presentInThisUnionAll;
	}

	public CcpJsonRepresentation save(CcpJsonRepresentation json) {
		CcpJsonRepresentation save = this.entity.save(json);
		return save;
	}

	public String toString() {
		
		CcpEntity wrapedEntity = this;
		Set<String> set = new LinkedHashSet<>();
		set.add(this.getClass().getSimpleName());
		do {
			
		}while(set.add((wrapedEntity = wrapedEntity.getWrapedEntity()).getClass().getSimpleName()));
		String replace = set.toString().replace(" ", "").replace(",", "->");
		CcpEntityDetails entityDetails = this.getEntityDetails();
		String string = entityDetails.entityName + " = " + replace;
		return string;
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
		T throwException = this.entity.throwException();
		return throwException;
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

	public CcpJsonRepresentation copyDataTo(CcpJsonRepresentation json, CcpEntity entities) {
		CcpJsonRepresentation copyDataTo = this.entity.copyDataTo(json, entities);
		return copyDataTo;
	}

	public CcpJsonRepresentation transferDataTo(CcpJsonRepresentation json, CcpEntity entities) {
		CcpJsonRepresentation transferDataTo = this.entity.transferDataTo(json, entities);
		return transferDataTo;
	}
}
