package com.ccp.especifications.db.utils.entity;

import com.ccp.business.CcpBusiness;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.flow.CcpErrorFlowDisturb;
import com.ccp.process.CcpProcessStatusDefault;

/**
 * Action of {@code CcpEntity.getOneById} when the record is not found: throws {@code CcpErrorFlowDisturb} with
 * {@code NOT_FOUND}, the entity put in the context.
 */
public class CcpEntityThrowNotFound implements CcpBusiness {

	/** The entity where the record was not found. */
	private final CcpEntity entity;

	/**
	 * Keeps the entity searched.
	 * @param entity the entity where the record was not found
	 */
	public CcpEntityThrowNotFound(CcpEntity entity) {
		this.entity = entity;
	}

	/**
	 * Throws the not found error.
	 * @param notFoundContext the context of the search
	 * @return never returns
	 */
	public CcpJsonRepresentation apply(CcpJsonRepresentation notFoundContext) {
		CcpJsonRepresentation notFoundContextWithEntity = notFoundContext.put(CcpEntity.JsonFieldNames.entity, this.entity);
		CcpErrorFlowDisturb notFoundError = new CcpErrorFlowDisturb(notFoundContextWithEntity, CcpProcessStatusDefault.NOT_FOUND);
		throw notFoundError;
	}
}
