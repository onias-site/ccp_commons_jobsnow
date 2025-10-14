package com.ccp.especifications.db.utils.decorators.engine;

import java.util.function.Consumer;

import com.ccp.business.CcpBusiness;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonRepresentation.CcpJsonFieldName;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.db.crud.CcpCrud;
import com.ccp.especifications.db.crud.CcpSelectUnionAll;
import com.ccp.especifications.db.utils.CcpEntity;
import com.ccp.flow.CcpErrorFlowDisturb;
import com.ccp.process.CcpProcessStatusDefault;


class DecoratorTwinEntity extends CcpEntityDelegator {
	enum JsonFieldNames implements CcpJsonFieldName{
		_entities
	}

	private final CcpEntity twin;
	
	public DecoratorTwinEntity(CcpEntity entity, CcpEntity twin) {
		super(entity);
		this.twin = twin;
	}

	public CcpEntity getTwinEntity() {
		DecoratorTwinEntity twin = new DecoratorTwinEntity(this.twin, this);
		return twin;
	}
	
	public String[] getEntitiesToSelect() {
		CcpEntity twinEntity = this.getTwinEntity();
		String twinName = twinEntity.getEntityName();
		String entityName = this.getEntityName();
		String[] resourcesNames = new String[]{entityName, twinName};
		return resourcesNames;
	}
	
	public CcpEntity[] getThisEntityAndHisTwinEntity() {
		CcpEntity twinEntity = this.getTwinEntity();
		CcpEntity[] ccpEntities = new CcpEntity[] {this, twinEntity};
		return ccpEntities;
	}
	public CcpJsonRepresentation getInnerJsonFromMainAndTwinEntities(CcpJsonRepresentation json) {
		String entityName = this.getEntityName();
		CcpEntity twinEntity = this.getTwinEntity();
		String twinEntityName = twinEntity.getEntityName();
		CcpJsonRepresentation j1 = json.getInnerJsonFromPath(JsonFieldNames._entities, entityName);
		CcpJsonRepresentation j2 = json.getInnerJsonFromPath(JsonFieldNames._entities, twinEntityName);
		CcpJsonRepresentation putAll = j1.putAll(j2);
		return putAll;
	}
	
	public CcpJsonRepresentation getData(CcpJsonRepresentation json, Consumer<String[]> functionToDeleteKeysInTheCache) {
		
		CcpCrud crud = CcpDependencyInjection.getDependency(CcpCrud.class);
		
		CcpSelectUnionAll searchResults = crud.unionBetweenMainAndTwinEntities(json, functionToDeleteKeysInTheCache, this);
		
		CcpEntity twinEntity = this.getTwinEntity();

		boolean inactive = twinEntity.isPresentInThisUnionAll(searchResults, json);
		
		if(inactive) {
			CcpJsonRepresentation requiredEntityRow = twinEntity.getRequiredEntityRow(searchResults, json);
			throw new CcpErrorFlowDisturb(requiredEntityRow, CcpProcessStatusDefault.INACTIVE_RECORD);
		}
		
		CcpJsonRepresentation requiredEntityRow = this.getRequiredEntityRow(searchResults, json);

		return requiredEntityRow;
	}
	
	private CcpEntity validateTwinEntity(CcpJsonRepresentation json) {
		
		CcpEntity twinEntity = this.getTwinEntity();
		boolean doesNotExist = false == twinEntity.exists(json);
		
		if(doesNotExist) {
			return this;
		}
		
		String id = twinEntity.calculateId(json);
		String errorMessage = String.format("The id '%s' has been moved from '%s' to '%s' ", id, this, twinEntity);
		throw new CcpErrorFlowDisturb(json, CcpProcessStatusDefault.REDIRECT, errorMessage, new String[0]);
	}
	
	public final CcpJsonRepresentation getOneById(CcpJsonRepresentation json) {
		this.validateTwinEntity(json);
		CcpJsonRepresentation oneById = this.entity.getOneById(json);
		return oneById;
	}
	
	public final CcpJsonRepresentation getOneById(CcpJsonRepresentation json, CcpBusiness ifNotFound) {
		this.validateTwinEntity(json);
		CcpJsonRepresentation oneById = this.entity.getOneById(json, ifNotFound);
		return oneById;
	}
	
	public CcpJsonRepresentation transferToReverseEntity(CcpJsonRepresentation json) {
		this.delete(json);
		CcpEntity twinEntity = this.getTwinEntity();
		twinEntity.save(json);
		return json;

	}
}
