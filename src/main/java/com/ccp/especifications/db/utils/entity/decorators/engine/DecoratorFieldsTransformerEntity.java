package com.ccp.especifications.db.utils.entity.decorators.engine;

import java.util.List;
import java.util.function.Supplier;

import com.ccp.constantes.CcpOtherConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.crud.CcpSelectUnionAll;
import com.ccp.especifications.db.utils.entity.CcpEntity;
import com.ccp.especifications.db.utils.entity.fields.CcpEntityField;
import com.ccp.especifications.db.utils.entity.fields.CcpEntityJsonTransformerError;

class DecoratorFieldsTransformerEntity extends CcpEntityDelegator {
	
	public DecoratorFieldsTransformerEntity(CcpEntity entity, Class<?> clazz) {
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
	
	private static class AlreadyTransformedJson extends CcpJsonRepresentation{
		private AlreadyTransformedJson(CcpJsonRepresentation json) {
			super(json.content);
		}
		
		public CcpJsonRepresentation redoJson(CcpJsonRepresentation json) {
			return new AlreadyTransformedJson(json);
		}
	}
	
	public CcpJsonRepresentation getHandledJson(CcpJsonRepresentation json) {
		
		boolean alreadyTransformedJson = json instanceof AlreadyTransformedJson;
		
		if(alreadyTransformedJson) {
			return json;
		}
		
		CcpJsonRepresentation result = json;
		CcpEntityMetaData entityDetails = this.getEntityMetaData();
		for (CcpEntityField field : entityDetails.allFields) {
			
			boolean doNothing = field.transformer == CcpOtherConstants.DO_NOTHING;
			
			if(doNothing) {
				continue;
			}
			
			try {
				result = field.transformer.execute(result);
			} catch (CcpEntityJsonTransformerError e) {
			
			}
		}
		return new AlreadyTransformedJson(result);
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
		Supplier<CcpJsonRepresentation> jsonSupplier = () -> this.getHandledJson(json);
		var result = this.entity.getRecordFromUnionAll(unionAll, jsonSupplier);
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
	
	public CcpJsonRepresentation transferDataTo(CcpJsonRepresentation json, CcpEntity entities) {
		CcpJsonRepresentation handledJson = this.getHandledJson(json);
		CcpJsonRepresentation result = this.entity.transferDataTo(handledJson, entities);
		return result;
	}
	
	public CcpJsonRepresentation copyDataTo(CcpJsonRepresentation json, CcpEntity entities) {
		CcpJsonRepresentation handledJson = this.getHandledJson(json);
		CcpJsonRepresentation result = this.entity.copyDataTo(handledJson, entities);
		return result;
	}

}
