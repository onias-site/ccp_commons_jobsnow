
package com.ccp.especifications.db.bulk;

import java.util.function.Function;

import com.ccp.constantes.CcpOtherConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonRepresentation.CcpJsonFieldName;
import com.ccp.especifications.db.utils.entity.CcpEntity;
import com.ccp.process.CcpProcessStatusDefault;

/**
 * Enumera os tipos de operação bulk possíveis sobre uma entidade do banco ({@code create},
 * {@code update}, {@code delete}, {@code noop}) e encapsula a lógica de reprocessamento automático
 * para situações de conflito ou registro não encontrado — por exemplo, promovendo {@code create}
 * para {@code update} em caso de conflito, e vice-versa.
 */
public enum CcpBulkEntityOperationType implements CcpJsonFieldName{

	/** Operação de criação; em caso de conflito (registro já existe), converte automaticamente para {@code update}. */
	create(1, false, CcpOtherConstants.EMPTY_JSON.put(CcpProcessStatusDefault.CONFLICT.asJsonFieldName(), (Function<CcpBulkItem,CcpBulkItem>) x -> replaceCreateToUpdate(x))),
	/** Operação de atualização ({@code createsVersionsToSameRecord = true}); em caso de registro não encontrado, converte automaticamente para {@code create}. */
	update(2, true, CcpOtherConstants.EMPTY_JSON.put(CcpProcessStatusDefault.NOT_FOUND.asJsonFieldName(), (Function<CcpBulkItem,CcpBulkItem>) x -> replaceUpdateToCreate(x))),
	/** Operação de exclusão; em caso de registro não encontrado, lança {@link CcpErrorBulkEntityRecordNotFound}. */
	delete(3, false, CcpOtherConstants.EMPTY_JSON.put(CcpProcessStatusDefault.NOT_FOUND.asJsonFieldName(), (Function<CcpBulkItem,CcpBulkItem>) x ->
	{
		throw new CcpErrorBulkEntityRecordNotFound(x.entity, x.json);
	})),
	/** Operação nula/sem efeito; usada para marcar registros que existem mas não precisam ser alterados. */
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
	
	/**
	 * Avalia o status retornado pela operação bulk; se o status não tem handler mapeado, gera um novo
	 * {@link CcpBulkItem} de criação via {@code reprocessJsonProducer}; caso contrário, aplica o handler
	 * correspondente (ex.: troca create para update) e retorna o item reprocessado.
	 *
	 * @param reprocessJsonProducer função que produz o JSON para reprocessamento quando o status não é mapeado
	 * @param result resultado da operação bulk original
	 * @param entityToReprocess entidade destino do reprocessamento
	 * @return item bulk reprocessado
	 */
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
