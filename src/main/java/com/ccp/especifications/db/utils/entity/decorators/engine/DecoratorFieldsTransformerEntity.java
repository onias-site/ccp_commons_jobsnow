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
	
	/**
	 * Wraps the entity.
	 * @param entity the wrapped entity
	 * @param clazz the configurator class (unused)
	 */
	public DecoratorFieldsTransformerEntity(CcpEntity entity, Class<?> clazz) {
		super(entity);
	}
	
	/**
	 * Transforms the fields and then deletes.
	 * @param json the record
	 * @return the outcome of the wrapped delete
	 */
	public boolean delete(CcpJsonRepresentation json) {
		CcpJsonRepresentation transformedJsonByEachFieldInJson = this.getHandledJson(json);
		var result = this.entity.delete(transformedJsonByEachFieldInJson);
		return result;
	}

	/**
	 * Transforms the fields and then deletes everywhere.
	 * @param json the record
	 * @return the outcome of the wrapped deleteAnyWhere
	 */
	public boolean deleteAnyWhere(CcpJsonRepresentation json) {
		CcpJsonRepresentation transformedJsonByEachFieldInJson = this.getHandledJson(json);
		var result = this.entity.deleteAnyWhere(transformedJsonByEachFieldInJson);
		return result;
	}

	/**
	 * Transforms the fields and then checks the existence.
	 * @param json the record
	 * @return the outcome of the wrapped exists
	 */
	public boolean exists(CcpJsonRepresentation json) {
		CcpJsonRepresentation transformedJsonByEachFieldInJson = this.getHandledJson(json);
		var result = this.entity.exists(transformedJsonByEachFieldInJson);
		return result;
	}
	

	
	/**
	 * Applies, in field order, the transformer of every field that has one; a transformer that raises
	 * {@code CcpEntityJsonTransformerError} is skipped. An already transformed JSON is returned as is, and the result is
	 * marked as transformed.
	 * @param json the record
	 * @return the transformed record
	 */
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

	/**
	 * Transforms the fields and then reads the record.
	 * @param json the record (at least its primary key)
	 * @return the record
	 */
	public CcpJsonRepresentation getOneById(CcpJsonRepresentation json) {
		CcpJsonRepresentation transformedJsonByEachFieldInJson = this.getHandledJson(json);
		var result = this.entity.getOneById(transformedJsonByEachFieldInJson);
		return result;
	}
	

	/**
	 * Transforms the fields and then builds the search parameters.
	 * @param json the record
	 * @return the search parameters
	 */
	public List<CcpJsonRepresentation> getParametersToSearch(CcpJsonRepresentation json) {
		CcpJsonRepresentation transformedJsonByEachFieldInJson = this.getHandledJson(json);
		var result = this.entity.getParametersToSearch(transformedJsonByEachFieldInJson);
		return result;
	}	

	/**
	 * Transforms the fields and then checks the union-all result.
	 * @param unionAll the search result
	 * @param json the record
	 * @return {@code true} when the record was found
	 */
	public boolean isPresentInThisUnionAll(CcpSelectUnionAll unionAll, CcpJsonRepresentation json) {
		CcpJsonRepresentation transformedJsonByEachFieldInJson = this.getHandledJson(json);
		var result = this.entity.isPresentInThisUnionAll(unionAll, transformedJsonByEachFieldInJson);
		return result;
	}
	
	/**
	 * Transforms the fields and then saves.
	 * @param json the record
	 * @return the outcome of the wrapped save
	 */
	public boolean save(CcpJsonRepresentation json) {
		CcpJsonRepresentation transformedJsonByEachFieldInJson = this.getHandledJson(json);
		var result = this.entity.save(transformedJsonByEachFieldInJson);
		return result;
	}

	/**
	 * Transforms the fields and then transfers.
	 * @param json the record
	 * @param entities the target entity
	 * @return the outcome of the wrapped transfer
	 */
	public boolean transferDataTo(CcpJsonRepresentation json, CcpEntity entities) {
		CcpJsonRepresentation handledJson = this.getHandledJson(json);
		boolean result = this.entity.transferDataTo(handledJson, entities);
		return result;
	}

	/**
	 * Transforms the fields and then copies.
	 * @param json the record
	 * @param entities the target entity
	 * @return the outcome of the wrapped copy
	 */
	public boolean copyDataTo(CcpJsonRepresentation json, CcpEntity entities) {
		CcpJsonRepresentation handledJson = this.getHandledJson(json);
		boolean result = this.entity.copyDataTo(handledJson, entities);
		return result;
	}

}
