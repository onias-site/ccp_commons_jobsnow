
package com.ccp.especifications.db.bulk;

import java.util.function.Function;

import com.ccp.business.CcpBusiness;
import com.ccp.constantes.CcpOtherConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.utils.entity.CcpEntity;
//FIXME TOSAVEBULKITEM ETC
public enum CcpBulkEntityOperationType {

	create(CcpOtherConstants.EMPTY_JSON.getDynamicVersion().put("409", (Function<CcpBulkItem,CcpBulkItem>) x -> replaceCreateToUpdate(x))), 
	update(CcpOtherConstants.EMPTY_JSON.getDynamicVersion().put("404", (Function<CcpBulkItem,CcpBulkItem>) x -> replaceUpdateToCreate(x))), 
	delete(CcpOtherConstants.EMPTY_JSON.getDynamicVersion().put("404", (Function<CcpBulkItem,CcpBulkItem>) x -> 
	{
		throw new CcpErrorBulkEntityRecordNotFound(x.entity, x.json);
	})){
		public CcpBusiness[] getTransformers(CcpEntity entity) {
			return new CcpBusiness[] { x -> entity.getOnlyExistingFieldsAndHandledJson(x)};
		}

	}
	;
	
	
	
	private final CcpJsonRepresentation handlers;

	private CcpBulkEntityOperationType(CcpJsonRepresentation handlers) {
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
			CcpBulkItem ccpBulkItem = entityToReprocess.toBulkItem(json, CcpBulkEntityOperationType.create);
			return ccpBulkItem;
		}
		
		Function<CcpBulkItem,CcpBulkItem> handler = this.handlers.getDynamicVersion().getAsObject("" + status);
		CcpBulkItem bulkItem = result.getBulkItem();
		CcpBulkItem apply = handler.apply(bulkItem);
		return apply;
	}
	
	public CcpBusiness[] getTransformers(CcpEntity entity) {
		return new CcpBusiness[] { x -> entity.validateJson(x), x -> entity.getOnlyExistingFieldsAndHandledJson(x)};
	}
}
