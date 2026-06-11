package com.ccp.especifications.db.utils.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;
import java.util.function.Supplier;

import com.ccp.business.CcpBusiness;
import com.ccp.constantes.CcpOtherConstants;
import com.ccp.decorators.CcpFieldName;
import com.ccp.decorators.CcpHashDecorator;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonRepresentation.CcpJsonFieldName;
import com.ccp.decorators.CcpStringDecorator;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.db.bulk.CcpBulkEntityOperationType;
import com.ccp.especifications.db.bulk.CcpBulkItem;
import com.ccp.especifications.db.crud.CcpCrud;
import com.ccp.especifications.db.crud.CcpSelectUnionAll;
import com.ccp.especifications.db.utils.CcpDbRequester;
import com.ccp.especifications.db.utils.entity.decorators.engine.CcpEntityMetaData;
import com.ccp.flow.CcpErrorFlowDisturb;
import com.ccp.hash.CcpHashAlgorithm;
import com.ccp.process.CcpProcessStatusDefault;

public interface CcpEntity  extends CcpJsonFieldName{
	public static enum JsonFieldNames implements CcpJsonFieldName{
		entity,
		_entities,
		
	}
	
	default String name() {
		CcpEntityMetaData entityMetaData = this.getEntityMetaData();
		return entityMetaData.entityName;
	}
	
	default String calculateId(CcpJsonRepresentation json) {
		CcpEntityMetaData entityDetails = this.getEntityMetaData();
		
		boolean hasNoPrimaryKey = entityDetails.primaryKeyNames.isEmpty();
		
		if(hasNoPrimaryKey) {
			//TODO LANCAR EXCECAO NOVA
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

	default CcpJsonRepresentation copyDataTo(CcpJsonRepresentation json, CcpEntity entities) {
		return this.throwException();
	}

	CcpEntityMetaData getEntityMetaData();
	
	default CcpJsonRepresentation delete(CcpJsonRepresentation json) {

		CcpCrud crud = CcpDependencyInjection.getDependency(CcpCrud.class);
		String calculateId = this.calculateId(json);
		CcpEntityMetaData entityDetails = this.getEntityMetaData();
		crud.delete(entityDetails.entityName, calculateId);
		return json;
	}

	default CcpJsonRepresentation deleteAnyWhere(CcpJsonRepresentation json) {
		CcpJsonRepresentation delete = this.delete(json);
		return delete;
	}
	
	default boolean exists(CcpJsonRepresentation json) {
		CcpCrud crud = CcpDependencyInjection.getDependency(CcpCrud.class);
		String id = this.calculateId(json);
		CcpEntityMetaData entityDetails = this.getEntityMetaData();
		boolean exists = crud.exists(entityDetails.entityName, id);
		return exists;
	}

	default List<CcpEntity> getAssociatedEntities(){
		List<CcpEntity> asList = Arrays.asList(this);
		return asList;
	}
	
	default CcpJsonRepresentation getHandledJson(CcpJsonRepresentation json) {
		return json;
	}
	
	default CcpJsonRepresentation getOneById(CcpJsonRepresentation json) {
		CcpEntityMetaData entityDetails = this.getEntityMetaData();
		CcpBusiness arara = x -> {
			CcpJsonRepresentation put = x.put(JsonFieldNames.entity, this);
			throw new CcpErrorFlowDisturb(put, CcpProcessStatusDefault.NOT_FOUND);
		};
		CcpJsonRepresentation md = entityDetails.getOneByIdOrHandleItIfThisIdWasNotFound(json, arara);
		return md;
	}
	
	default CcpJsonRepresentation getOneByIdAnyWhere(CcpJsonRepresentation json) {
		CcpJsonRepresentation oneById = this.getOneById(json);
		CcpJsonRepresentation put = CcpOtherConstants.EMPTY_JSON.put(this, oneById);
		return put;
	}
	
	default List<CcpJsonRepresentation> getParametersToSearch(CcpJsonRepresentation json) {
		
		String id = this.calculateId(json);

		CcpDbRequester dependency = CcpDependencyInjection.getDependency(CcpDbRequester.class);
		
		String fieldNameToEntity = dependency.getFieldNameToEntity();
		String fieldNameToId = dependency.getFieldNameToId();
		
		CcpEntityMetaData entityDetails = this.getEntityMetaData();
		
		CcpJsonRepresentation mainRecord = CcpOtherConstants.EMPTY_JSON
		.put(new CcpFieldName(fieldNameToEntity), entityDetails.entityName)
		.put(new CcpFieldName(fieldNameToId), id)
		;
		List<CcpJsonRepresentation> asList = Arrays.asList(mainRecord);
		return asList;
	}

	default CcpJsonRepresentation getRecordFromUnionAll(CcpSelectUnionAll unionAll, Supplier<CcpJsonRepresentation> jsonSupplier) {

		CcpJsonRepresentation json = jsonSupplier.get();

		CcpEntityMetaData entityDetails = this.getEntityMetaData();
		
		CcpJsonRepresentation handledJson = entityDetails.entity.getHandledJson(json);
		
		String id = this.calculateId(handledJson);
		
		CcpJsonRepresentation jsonValue = unionAll.getEntityRow(entityDetails.entityName, id);
		
		return jsonValue;
	}
	
	default CcpEntity getTwinEntity() {
		CcpEntity throwException = this.throwException();
		return throwException;
	}

	default <T>T throwException() {
		CcpEntityMetaData entityDetails = this.getEntityMetaData();
		throw new UnsupportedOperationException("The entity '" + entityDetails.entityName + "' is just to read only");
	}
	
	default CcpEntity getWrapedEntity() {
		return this;
	}
	
	default boolean isPresentInThisUnionAll(CcpSelectUnionAll unionAll, CcpJsonRepresentation json) {
	
		CcpEntityMetaData entityDetails = this.getEntityMetaData();
	
		String id = this.calculateId(json);

		boolean present = unionAll.isPresent(entityDetails.entityName, id);
		
		return present;
	}

	default CcpJsonRepresentation save(CcpJsonRepresentation json) {
		CcpEntityMetaData entityDetails = this.getEntityMetaData();
		CcpJsonRepresentation onlyExistingFields = entityDetails.getOnlyExistingFields(json);
		String id = this.calculateId(json);
		CcpCrud crud = CcpDependencyInjection.getDependency(CcpCrud.class);
		crud.save(entityDetails.entityName, onlyExistingFields, id);
		return json;
	}

	default List<CcpBulkItem> toBulkItems(CcpJsonRepresentation json, CcpBulkEntityOperationType operation) {
		CcpEntityMetaData entityDetails = this.getEntityMetaData();
		CcpJsonRepresentation onlyExistingFields = entityDetails.getOnlyExistingFields(json);
		String calculateId = this.calculateId(onlyExistingFields);
		CcpBulkItem ccpBulkItem = new CcpBulkItem(onlyExistingFields, operation, entityDetails.entity, calculateId);
		return Arrays.asList(ccpBulkItem);
	}
	
	default CcpJsonRepresentation transferDataTo(CcpJsonRepresentation json, CcpEntity entities) {
		return this.throwException();
	}

	default CcpJsonRepresentation validateJson(CcpJsonRepresentation json) {
		return json;
	}
	
	default CcpJsonRepresentation getIdToSearchDisposableRecord(CcpJsonRepresentation json) {
		CcpJsonRepresentation throwException = this.throwException();
		return throwException;
	}
}
