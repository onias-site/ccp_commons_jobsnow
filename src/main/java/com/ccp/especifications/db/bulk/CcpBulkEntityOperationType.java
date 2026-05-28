
package com.ccp.especifications.db.bulk;

import java.util.function.Function;

import com.ccp.constantes.CcpOtherConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonRepresentation.CcpJsonFieldName;
import com.ccp.especifications.db.utils.entity.CcpEntity;
import com.ccp.process.CcpProcessStatusDefault;

public enum CcpBulkEntityOperationType implements CcpJsonFieldName{

	create(1, false, CcpOtherConstants.EMPTY_JSON.put(CcpProcessStatusDefault.CONFLICT.asJsonFieldName(), (Function<CcpBulkItem,CcpBulkItem>) x -> replaceCreateToUpdate(x))), 
	update(2, true, CcpOtherConstants.EMPTY_JSON.put(CcpProcessStatusDefault.NOT_FOUND.asJsonFieldName(), (Function<CcpBulkItem,CcpBulkItem>) x -> replaceUpdateToCreate(x))), 
	delete(3, false, CcpOtherConstants.EMPTY_JSON.put(CcpProcessStatusDefault.NOT_FOUND.asJsonFieldName(), (Function<CcpBulkItem,CcpBulkItem>) x -> 
	{
		throw new CcpErrorBulkEntityRecordNotFound(x.entity, x.json);
	})),
	noop(0, false, CcpOtherConstants.EMPTY_JSON),
	;
	public final boolean createsVersionsToSameRecord;
	private final CcpJsonRepresentation handlers;
	public final int priority;

	private CcpBulkEntityOperationType(int priority, boolean createsVersionsToSameRecord, CcpJsonRepresentation handlers) {
		this.createsVersionsToSameRecord = createsVersionsToSameRecord;
		this.handlers = handlers;
		this.priority = priority;
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
		
		CcpJsonFieldName statusAsJsonFieldName = result.statusAsJsonFieldName();
		boolean statusNotMapped = false == this.handlers.containsAllFields(statusAsJsonFieldName);
		
		if(statusNotMapped) {
			CcpJsonRepresentation json = reprocessJsonProducer.apply(result);
			String calculateId = entityToReprocess.calculateId(json);
			CcpBulkItem ccpBulkItem = new CcpBulkItem(json, CcpBulkEntityOperationType.create, entityToReprocess, calculateId);
			return ccpBulkItem;
		}
		
		Function<CcpBulkItem,CcpBulkItem> handler = this.handlers.getAsObject(statusAsJsonFieldName);
		CcpBulkItem bulkItem = result.getBulkItem();
		CcpBulkItem apply = handler.apply(bulkItem);
		return apply;
	}
}
