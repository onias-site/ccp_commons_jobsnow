package com.ccp.especifications.db.utils.entity.decorators.engine2;

import java.util.List;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.bulk.CcpBulkEntityOperationType;
import com.ccp.especifications.db.bulk.CcpBulkItem;
import com.ccp.especifications.db.crud.CcpSelectUnionAll;
import com.ccp.especifications.db.utils.entity.CcpEntity2;
import com.ccp.especifications.db.utils.entity.fields.CcpEntityField;
import com.ccp.especifications.db.utils.entity.fields.CcpEntityJsonTransformerError;

class DecoratorFieldsTransformerEntity extends CcpEntityDelegator {
	
	public DecoratorFieldsTransformerEntity(CcpEntity2 entity, Class<?> clazz) {
		super(entity);
	}
	
	public CcpJsonRepresentation delete(CcpJsonRepresentation json) {
		CcpJsonRepresentation transformedJsonByEachFieldInJson = this.getHandledJson(json);
		var result = this.entity.delete(transformedJsonByEachFieldInJson);
		return result;
	}

	public boolean exists(CcpJsonRepresentation json) {
		CcpJsonRepresentation transformedJsonByEachFieldInJson = this.getHandledJson(json);
		var result = this.entity.exists(transformedJsonByEachFieldInJson);
		return result;
	}

	public CcpJsonRepresentation getHandledJson(CcpJsonRepresentation json) {
		CcpJsonRepresentation result = json;
		CcpEntityDetails entityDetails = this.getEntityDetails();
		for (CcpEntityField field : entityDetails.allFields) {
			try {
				result = field.transformer.apply(result);
			} catch (CcpEntityJsonTransformerError e) {
			
			}
		}
		return result;
	}

	public CcpJsonRepresentation getOneById(CcpJsonRepresentation json) {
		CcpJsonRepresentation transformedJsonByEachFieldInJson = this.getHandledJson(json);
		var result = this.entity.getOneById(transformedJsonByEachFieldInJson);
		return result;
	}
	

	public List<CcpJsonRepresentation> getParametersToSearch(CcpJsonRepresentation json) {
		CcpJsonRepresentation transformedJsonByEachFieldInJson = this.getHandledJson(json);
		var result = this.entity.getParametersToSearch(transformedJsonByEachFieldInJson);
		return result;
	}	

	public CcpJsonRepresentation getRecordFromUnionAll(CcpSelectUnionAll unionAll, CcpJsonRepresentation json) {
		CcpJsonRepresentation transformedJsonByEachFieldInJson = this.getHandledJson(json);
		var result = this.entity.getRecordFromUnionAll(unionAll, transformedJsonByEachFieldInJson);
		return result;
	}
	
	public boolean isPresentInThisUnionAll(CcpSelectUnionAll unionAll, CcpJsonRepresentation json) {
		CcpJsonRepresentation transformedJsonByEachFieldInJson = this.getHandledJson(json);
		var result = this.entity.isPresentInThisUnionAll(unionAll, transformedJsonByEachFieldInJson);
		return result;
	}
	
	public CcpJsonRepresentation save(CcpJsonRepresentation json) {
		CcpJsonRepresentation transformedJsonByEachFieldInJson = this.getHandledJson(json);
		var result = this.entity.save(transformedJsonByEachFieldInJson);
		return result;
	}
	
	public List<CcpBulkItem> toBulkItems(CcpJsonRepresentation json, CcpBulkEntityOperationType operation) {
		CcpJsonRepresentation transformedJsonByEachFieldInJson = this.getHandledJson(json);
		var result = this.entity.toBulkItems(transformedJsonByEachFieldInJson, operation);
		return result;
	}
}
