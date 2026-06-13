package com.ccp.especifications.db.crud;

import com.ccp.business.CcpBusiness;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonRepresentation.CcpJsonFieldName;
import com.ccp.especifications.db.utils.entity.CcpEntity;
import com.ccp.especifications.db.utils.entity.decorators.engine.CcpEntityMetaData;

/**
 * Business interno (package-private) que substitui o campo {@code entity} de um JSON — que carrega um objeto
 * {@code CcpEntity} — pelo nome textual da entidade ({@code entityName}). Usado como passo de preparação antes
 * de operações de persistência.
 */
class FunctionPutEntity implements CcpBusiness{
	enum JsonFieldNames implements CcpJsonFieldName{
		entity
	}

	public static final FunctionPutEntity INSTANCE = new FunctionPutEntity();

	private FunctionPutEntity() {}

	/**
	 * Extrai o objeto {@code CcpEntity} do campo {@code entity}, obtém seu nome via {@code getEntityMetaData()}
	 * e substitui o campo pelo nome textual.
	 */
	public CcpJsonRepresentation apply(CcpJsonRepresentation j) {

		CcpEntity ent = j.getAsObject(JsonFieldNames.entity);
		CcpEntityMetaData entityDetails = ent.getEntityMetaData();
		String entityName = entityDetails.entityName;
		CcpJsonRepresentation put2 = j.put(JsonFieldNames.entity, entityName);
		return put2;

	}

}
