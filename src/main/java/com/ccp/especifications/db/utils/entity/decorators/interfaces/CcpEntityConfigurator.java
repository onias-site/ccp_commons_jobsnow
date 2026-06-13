package com.ccp.especifications.db.utils.entity.decorators.interfaces;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.bulk.CcpBulkItem;
import com.ccp.especifications.db.bulk.CcpBulkEntityOperationType;
import com.ccp.especifications.db.utils.entity.CcpEntity;

/**
 * Contrato para classes configuradoras de entidades. Permite obter a instância de {@code CcpEntity}
 * declarada no campo estático {@code ENTITY} da classe, além de fornecer utilitários para montar
 * itens bulk de criação a partir de strings JSON ou instâncias de {@code CcpJsonRepresentation}.
 */
public interface CcpEntityConfigurator {

	/** Retorna os registros iniciais a inserir no setup do banco. Por padrão, lista vazia. */
	default List<CcpBulkItem> getFirstRecordsToInsert(){
		return new ArrayList<>();
	}
	/** Converte strings JSON em itens bulk de criação para a entidade informada. */
	default List<CcpBulkItem> toCreateBulkItems(CcpEntity entity, String... jsons){
		var response = new ArrayList<CcpBulkItem>();
		for (String string : jsons) {
			CcpJsonRepresentation json = new CcpJsonRepresentation(string);
			List<CcpBulkItem> bulkItems = entity.toBulkItems(json, CcpBulkEntityOperationType.create);
			response.addAll(bulkItems);
		}
		return response;
	}
	/** Converte instâncias de {@code CcpJsonRepresentation} em itens bulk de criação. */
	default List<CcpBulkItem> toCreateBulkItems(CcpEntity entity, CcpJsonRepresentation... jsons){
		var response = new ArrayList<CcpBulkItem>();
		for (CcpJsonRepresentation json : jsons) {
			List<CcpBulkItem> bulkItems = entity.toBulkItems(json, CcpBulkEntityOperationType.create);
			response.addAll(bulkItems);
		}
		return response;
	}
	
	/** Retorna a instância de {@code CcpEntity} declarada no campo estático {@code ENTITY} desta classe. */
	default CcpEntity getEntity() {
		try {
			Class<? extends CcpEntityConfigurator> class1 = this.getClass();
			Field declaredField = class1.getDeclaredField("ENTITY");
			declaredField.setAccessible(true);
			Object object = declaredField.get(null);
			return (CcpEntity) object;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
	}
}
