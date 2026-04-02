
package com.ccp.especifications.db.bulk;

import java.util.function.Function;

import com.ccp.constantes.CcpOtherConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.utils.entity.CcpEntity;
import com.ccp.especifications.db.utils.entity.decorators.engine.CcpEntityDetails;

public enum CcpBulkEntityOperationType {

	create(false, true, CcpOtherConstants.EMPTY_JSON.getDynamicVersion().put("409", (Function<CcpBulkItem,CcpBulkItem>) x -> replaceCreateToUpdate(x))), 
	update(true, true, CcpOtherConstants.EMPTY_JSON.getDynamicVersion().put("404", (Function<CcpBulkItem,CcpBulkItem>) x -> replaceUpdateToCreate(x))), 
	delete(false,true, CcpOtherConstants.EMPTY_JSON.getDynamicVersion().put("404", (Function<CcpBulkItem,CcpBulkItem>) x -> 
	{
		throw new CcpErrorBulkEntityRecordNotFound(x.entity, x.json);
	})),
	noop(false, false, CcpOtherConstants.EMPTY_JSON),
	;
	public final boolean createsVersionsToSameRecord;
	private final CcpJsonRepresentation handlers;
	public final boolean persistable;
	
	public boolean doesNotCreateVersionsToSameRecord(CcpEntityDetails entityDetails) {
		
		boolean isNotAnUpdatableEntity = entityDetails.isNotAnUpdatableEntity();
		
		if(isNotAnUpdatableEntity) {
			return true;
		}
		
		return false == this.createsVersionsToSameRecord;
	} 
	private CcpBulkEntityOperationType(boolean createsVersionsToSameRecord, boolean persistable, CcpJsonRepresentation handlers) {
		this.createsVersionsToSameRecord = createsVersionsToSameRecord;
		this.persistable = persistable;
		this.handlers = handlers;
	}
	private static CcpBulkItem replaceCreateToUpdate(CcpBulkItem x) {
		CcpBulkItem ccpBulkItem = new CcpBulkItem(x, CcpBulkEntityOperationType.update);
		return ccpBulkItem;
	}
	private static CcpBulkItem replaceUpdateToCreate(CcpBulkItem x) {
		CcpBulkItem ccpBulkItem = new CcpBulkItem(x, CcpBulkEntityOperationType.create);
		return ccpBulkItem;
	}
	
	public CcpBulkItem getReprocess(Function<CcpBulkOperationResult, CcpJsonRepresentation> reprocessJsonProducer, CcpBulkOperationResult result, CcpEntity entityToReprocess) {
		
		int status = result.status();
		boolean statusNotMapped = false == this.handlers.getDynamicVersion().containsAllFields("" + status);
		
		if(statusNotMapped) {
			CcpJsonRepresentation json = reprocessJsonProducer.apply(result);
			String calculateId = entityToReprocess.calculateId(json);
			CcpBulkItem ccpBulkItem = new CcpBulkItem(json, CcpBulkEntityOperationType.create, entityToReprocess, calculateId);
			return ccpBulkItem;
		}
		
		Function<CcpBulkItem,CcpBulkItem> handler = this.handlers.getDynamicVersion().getAsObject("" + status);
		CcpBulkItem bulkItem = result.getBulkItem();
		CcpBulkItem apply = handler.apply(bulkItem);
		return apply;
	}
}
