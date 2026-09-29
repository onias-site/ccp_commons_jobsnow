package com.ccp.especifications.db.utils.entity.decorators.engine;

import java.util.List;

import com.ccp.constants.CcpOtherConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.crud.CcpSelectUnionAll;
import com.ccp.especifications.db.utils.entity.CcpEntity;
import com.ccp.especifications.db.utils.entity.fields.CcpEntityField;
import com.ccp.especifications.db.utils.entity.fields.CcpEntityJsonTransformerError;

/**
 * Decorator that applies field transformations (defined in {@code @CcpEntityFieldsTransformer})
 * to the JSON before any read or write operation. Uses the internal class
 * {@code AlreadyTransformedJson} to avoid re-transformations in recursive calls.
 */
class DecoratorFieldsTransformerEntity extends CcpEntityDelegator {
	
	public DecoratorFieldsTransformerEntity(CcpEntity entity, Class<?> clazz) {
		super(entity);
	}
	
	public boolean delete(CcpJsonRepresentation json) {
		CcpJsonRepresentation transformedJsonByEachFieldInJson = this.getHandledJson(json);
		var result = this.entity.delete(transformedJsonByEachFieldInJson);
		return result;
	}

	public boolean deleteAnyWhere(CcpJsonRepresentation json) {
		CcpJsonRepresentation transformedJsonByEachFieldInJson = this.getHandledJson(json);
		var result = this.entity.deleteAnyWhere(transformedJsonByEachFieldInJson);
		return result;
	}

	public boolean exists(CcpJsonRepresentation json) {
		CcpJsonRepresentation transformedJsonByEachFieldInJson = this.getHandledJson(json);
		var result = this.entity.exists(transformedJsonByEachFieldInJson);
		return result;
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
		AlreadyTransformedJson transformedJson = new AlreadyTransformedJson(result);
		return transformedJson;
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

	public boolean isPresentInThisUnionAll(CcpSelectUnionAll unionAll, CcpJsonRepresentation json) {
		CcpJsonRepresentation transformedJsonByEachFieldInJson = this.getHandledJson(json);
		var result = this.entity.isPresentInThisUnionAll(unionAll, transformedJsonByEachFieldInJson);
		return result;
	}
	
	public boolean save(CcpJsonRepresentation json) {
		CcpJsonRepresentation transformedJsonByEachFieldInJson = this.getHandledJson(json);
		var result = this.entity.save(transformedJsonByEachFieldInJson);
		return result;
	}

	public boolean transferDataTo(CcpJsonRepresentation json, CcpEntity entities) {
		CcpJsonRepresentation handledJson = this.getHandledJson(json);
		boolean result = this.entity.transferDataTo(handledJson, entities);
		return result;
	}

	public boolean copyDataTo(CcpJsonRepresentation json, CcpEntity entities) {
		CcpJsonRepresentation handledJson = this.getHandledJson(json);
		boolean result = this.entity.copyDataTo(handledJson, entities);
		return result;
	}

}
