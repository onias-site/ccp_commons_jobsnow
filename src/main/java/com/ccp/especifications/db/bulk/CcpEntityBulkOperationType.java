
package com.ccp.especifications.db.bulk;

import java.util.function.Function;

import com.ccp.constantes.CcpOtherConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.utils.CcpEntity;
import com.ccp.exceptions.db.utils.CcpErrorBulkEntityRecordNotFound;

public enum CcpEntityBulkOperationType {

	create(CcpOtherConstants.EMPTY_JSON.getDynamicVersion().put("409", (Function<CcpBulkItem,CcpBulkItem>) x -> replaceCreateToUpdate(x))), 
	update(CcpOtherConstants.EMPTY_JSON.getDynamicVersion().put("404", (Function<CcpBulkItem,CcpBulkItem>) x -> replaceUpdateToCreate(x))), 
	delete(CcpOtherConstants.EMPTY_JSON.getDynamicVersion().put("404", (Function<CcpBulkItem,CcpBulkItem>) x -> 
	{
		throw new CcpErrorBulkEntityRecordNotFound(x.entity, x.json);
	}))
	;
	
	private final CcpJsonRepresentation handlers;

	private CcpEntityBulkOperationType(CcpJsonRepresentation handlers) {
		this.handlers = handlers;
	}
	private static CcpBulkItem replaceCreateToUpdate(CcpBulkItem x) {
		CcpBulkItem ccpBulkItem = new CcpBulkItem(x.json, CcpEntityBulkOperationType.update, x.entity, x.id
				, json -> x.entity.getOnlyExistingFields(json)
				);
		return ccpBulkItem;
	}
	private static CcpBulkItem replaceUpdateToCreate(CcpBulkItem x) {
		CcpBulkItem ccpBulkItem = new CcpBulkItem(x.json, CcpEntityBulkOperationType.create, x.entity, x.id);
		return ccpBulkItem;
	}
	
	public CcpBulkItem getReprocess(Function<CcpBulkOperationResult, CcpJsonRepresentation> reprocessJsonProducer, CcpBulkOperationResult result, CcpEntity entityToReprocess) {
		
		int status = result.status();
		boolean statusNotFound = this.handlers.getDynamicVersion().containsAllFields("" + status) == false;
		
		if(statusNotFound) {
			CcpJsonRepresentation json = reprocessJsonProducer.apply(result);
			CcpBulkItem ccpBulkItem = entityToReprocess.getMainBulkItem(json, CcpEntityBulkOperationType.create);
			return ccpBulkItem;
		}
		
		Function<CcpBulkItem,CcpBulkItem> handler = this.handlers.getDynamicVersion().getAsObject("" + status);
		CcpBulkItem bulkItem = result.getBulkItem();
		CcpBulkItem apply = handler.apply(bulkItem);
		return apply;
	}
}
