package com.ccp.especifications.db.utils.entity.decorators.engine2;

import java.util.List;

import com.ccp.business.CcpBusiness;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.bulk.CcpBulkEntityOperationType;
import com.ccp.especifications.db.bulk.CcpBulkItem;
import com.ccp.especifications.db.crud.CcpSelectUnionAll;
import com.ccp.especifications.db.utils.entity.CcpEntity2;

public class CcpEntityDelegator implements CcpEntity2{
	protected final CcpEntity2 entity;
	

	public CcpEntityDelegator(CcpEntity2 entity) {
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

	public String[] getEntitiesToSelect() {
		String[] entitiesToSelect = this.entity.getEntitiesToSelect();
		return entitiesToSelect;
	}

	public CcpJsonRepresentation getOneById(CcpJsonRepresentation json) {
		CcpJsonRepresentation oneById = this.entity.getOneById(json);
		return oneById;
	}

	public CcpJsonRepresentation getOneByIdAnyWhere(CcpJsonRepresentation json) {
		CcpJsonRepresentation oneByIdAnywhere = this.entity.getOneByIdAnyWhere(json);
		return oneByIdAnywhere;
	}

	public CcpJsonRepresentation getOneByIdOrHandleItIfThisIdWasNotFound(CcpJsonRepresentation json, CcpBusiness ifNotFound) {
		CcpJsonRepresentation oneByIdOrHandleItIfThisIdWasNotFound = this.entity.getOneByIdOrHandleItIfThisIdWasNotFound(json, ifNotFound);
		return oneByIdOrHandleItIfThisIdWasNotFound;
	}

	public List<CcpJsonRepresentation> getParametersToSearch(CcpJsonRepresentation json) {
		List<CcpJsonRepresentation> parametersToSearch = this.entity.getParametersToSearch(json);
		return parametersToSearch;
	}

	public CcpJsonRepresentation getRecordFromUnionAll(CcpSelectUnionAll unionAll, CcpJsonRepresentation json) {
		CcpJsonRepresentation recordFromUnionAll = this.entity.getRecordFromUnionAll(unionAll, json);
		return recordFromUnionAll;
	}

	public CcpJsonRepresentation getRequiredEntityRow(CcpSelectUnionAll unionAll, CcpJsonRepresentation json) {
		CcpJsonRepresentation requiredEntityRow = this.entity.getRequiredEntityRow(unionAll, json);
		return requiredEntityRow;
	}

	public CcpEntity2 getTwinEntity() {
		CcpEntity2 twinEntity = this.entity.getTwinEntity();
		return twinEntity;
	}

	public CcpEntity2 getWrapedEntity() {
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
		String string = this.entity.toString();
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

	public List<CcpEntity2> getAssociatedEntities() {
		List<CcpEntity2> associatedEntities = this.entity.getAssociatedEntities();
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
	
}
