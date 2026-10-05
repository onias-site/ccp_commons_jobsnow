package com.ccp.especifications.db.utils.entity.decorators.engine;

import com.ccp.decorators.CcpJsonRepresentation;

/**
 * Marker JSON: its fields already went through the field transformers of the entity, so
 * {@code DecoratorFieldsTransformerEntity} does not transform it again.
 */
class AlreadyTransformedJson extends CcpJsonRepresentation{
	/**
	 * Marks the fields of the JSON as already transformed.
	 * @param json the transformed JSON
	 */
	AlreadyTransformedJson(CcpJsonRepresentation json) {
		super(json.content);
	}
	
	/**
	 * Keeps the marker when the content is rebuilt from another JSON.
	 * @param json the JSON whose content is used
	 * @return the new marked JSON
	 */
	public CcpJsonRepresentation redoJson(CcpJsonRepresentation json) {
		AlreadyTransformedJson alreadyTransformedJson = new AlreadyTransformedJson(json);
		return alreadyTransformedJson;
	}
}
