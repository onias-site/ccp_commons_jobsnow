package com.ccp.especifications.db.utils.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import com.ccp.business.CcpBusiness;
import com.ccp.constantes.CcpOtherConstants;
import com.ccp.decorators.CcpHashDecorator;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpStringDecorator;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.db.bulk.CcpBulkEntityOperationType;
import com.ccp.especifications.db.bulk.CcpBulkItem;
import com.ccp.especifications.db.bulk.CcpErrorBulkEntityRecordNotFound;
import com.ccp.especifications.db.crud.CcpCrud;
import com.ccp.especifications.db.crud.CcpSelectUnionAll;
import com.ccp.especifications.db.utils.CcpDbRequester;
import com.ccp.especifications.db.utils.entity.CcpEntity.JsonFieldNames;
import com.ccp.especifications.db.utils.entity.decorators.engine2.CcpEntityDetails;
import com.ccp.flow.CcpErrorFlowDisturb;
import com.ccp.process.CcpProcessStatusDefault;
import com.ccp.utils.CcpHashAlgorithm;

public interface CcpEntity2 {
	default String calculateId(CcpJsonRepresentation json) {
		CcpEntityDetails entityDetails = this.getEntityDetails();
		
		boolean hasNoPrimaryKey = entityDetails.primaryKeyNames.isEmpty();
		
		if(hasNoPrimaryKey) {
			String string = UUID.randomUUID().toString();
			String hash = new CcpStringDecorator(string).hash().asString(CcpHashAlgorithm.SHA1);
			return hash;
		}
		
		ArrayList<Object> sortedPrimaryKeyValues = entityDetails.getSortedPrimaryKeyValues(json);
		
		String replace = sortedPrimaryKeyValues.toString().replace("[", "").replace("]", "");
		CcpHashDecorator hash2 = new CcpStringDecorator(replace).hash();
		String hash = hash2.asString(CcpHashAlgorithm.SHA1);
		return hash;
	}

	default CcpEntityDetails getEntityDetails() {
		//FIXME
		return CcpEntityDetails.getEntityDetails(null);
	}
	
	default CcpJsonRepresentation delete(CcpJsonRepresentation json) {

		CcpCrud crud = CcpDependencyInjection.getDependency(CcpCrud.class);
		String calculateId = this.calculateId(json);
		CcpEntityDetails entityDetails = this.getEntityDetails();
		crud.delete(entityDetails.entityName, calculateId);
		return json;
	}

	default CcpJsonRepresentation deleteAnyWhere(CcpJsonRepresentation json) {
		CcpJsonRepresentation delete = this.delete(json);
		return delete;
	}
	
	default String[] getEntitiesToSelect() {
		CcpEntityDetails entityDetails = this.getEntityDetails();
		String[] resourcesNames = new String[]{entityDetails.entityName};
		return resourcesNames;
	}

	default CcpJsonRepresentation getOneById(CcpJsonRepresentation json) {
		CcpEntityDetails entityDetails = this.getEntityDetails();
		CcpJsonRepresentation md = this.getOneByIdOrHandleItIfThisIdWasNotFound(json, x -> {throw new CcpErrorFlowDisturb(x.put(JsonFieldNames.entity, entityDetails.entityName), CcpProcessStatusDefault.NOT_FOUND);});
		return md;
	}
	
	default CcpJsonRepresentation getOneByIdAnywhere(CcpJsonRepresentation json) {
		CcpJsonRepresentation oneById = this.getOneById(json);
		return oneById;
	}
	
	default CcpJsonRepresentation getOneByIdOrHandleItIfThisIdWasNotFound(CcpJsonRepresentation json, CcpBusiness ifNotFound) {
		try {
			CcpCrud crud = CcpDependencyInjection.getDependency(CcpCrud.class);
			String calculateId = this.calculateId(json);
			CcpEntityDetails entityDetails = this.getEntityDetails();
			CcpJsonRepresentation oneById = crud.getOneById(entityDetails.entityName, calculateId);
			return oneById;
			
		} catch (CcpErrorBulkEntityRecordNotFound e) {
			CcpJsonRepresentation execute = ifNotFound.apply(json);
			return execute;
		}
	}
	default List<CcpJsonRepresentation> getParametersToSearch(CcpJsonRepresentation json) {
		
		String id = this.calculateId(json);

		CcpDbRequester dependency = CcpDependencyInjection.getDependency(CcpDbRequester.class);
		
		String fieldNameToEntity = dependency.getFieldNameToEntity();
		String fieldNameToId = dependency.getFieldNameToId();
		
		CcpEntityDetails entityDetails = this.getEntityDetails();
		
		CcpJsonRepresentation mainRecord = CcpOtherConstants.EMPTY_JSON
		.getDynamicVersion().put(fieldNameToEntity, entityDetails.entityName)
		.getDynamicVersion().put(fieldNameToId, id)
		;
		List<CcpJsonRepresentation> asList = Arrays.asList(mainRecord);
		return asList;
	}
	
	default CcpJsonRepresentation getRecordFromUnionAll(CcpSelectUnionAll unionAll, CcpJsonRepresentation json) {

		String id = this.calculateId(json);
		CcpEntityDetails entityDetails = this.getEntityDetails();
		
		CcpJsonRepresentation jsonValue = unionAll.getEntityRow(entityDetails.entityName, id);
		
		return jsonValue;
	}
	
	default CcpJsonRepresentation getRequiredEntityRow(CcpSelectUnionAll unionAll, CcpJsonRepresentation json) {
		
		boolean notFound = false == this.isPresentInThisUnionAll(unionAll, json);

		if(notFound) {
			//FIXME
			throw new CcpErrorBulkEntityRecordNotFound(null, json);
		}
		
		CcpJsonRepresentation entityRow = this.getRecordFromUnionAll(unionAll, json);
		
		return entityRow;
	}
	
	default CcpEntity getTwinEntity() {
		throw new UnsupportedOperationException();
	}
	
	default CcpEntity2 getWrapedEntity() {
		return this;
	}
	
	default boolean isPresentInThisUnionAll(CcpSelectUnionAll unionAll, CcpJsonRepresentation json) {
	
		CcpEntityDetails entityDetails = this.getEntityDetails();
	
		String id = this.calculateId(json);

		boolean present = unionAll.isPresent(entityDetails.entityName, id);
		
		return present;
	}

	default CcpJsonRepresentation save(CcpJsonRepresentation json) {
		CcpEntityDetails entityDetails = this.getEntityDetails();
		CcpJsonRepresentation onlyExistingFields = entityDetails.getOnlyExistingFields(json);
		String id = this.calculateId(json);
		CcpCrud crud = CcpDependencyInjection.getDependency(CcpCrud.class);
		crud.save(entityDetails.entityName, onlyExistingFields, id);
		return json;
	}

	default List<CcpBulkItem> toSaveBulkItems(CcpJsonRepresentation json) {
		String calculateId = this.calculateId(json);
		//FIXME
		CcpBulkItem ccpBulkItem = new CcpBulkItem(json, CcpBulkEntityOperationType.create, null, calculateId);
		return Arrays.asList(ccpBulkItem);
	}

	default List<CcpBulkItem> toDeleteBulkItems(CcpJsonRepresentation json) {
		String calculateId = this.calculateId(json);
		//FIXME
		CcpBulkItem ccpBulkItem = new CcpBulkItem(json, CcpBulkEntityOperationType.delete, null, calculateId);
		return Arrays.asList(ccpBulkItem);
	}


}
