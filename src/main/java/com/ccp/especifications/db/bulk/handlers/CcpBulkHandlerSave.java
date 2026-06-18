package com.ccp.especifications.db.bulk.handlers;

import java.util.List;
import java.util.stream.Collectors;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.bulk.CcpBulkEntityOperationType;
import com.ccp.especifications.db.bulk.CcpBulkItem;
import com.ccp.especifications.db.crud.CcpHandleWithSearchResultsInTheEntity;
import com.ccp.especifications.db.utils.entity.CcpEntity;
import com.ccp.especifications.db.utils.entity.decorators.engine.CcpEntityMetaData;

/**
 * Handler bulk que implementa a lógica de "upsert" inteligente: se o registro existe, gera itens
 * de {@code update} mesclando os dados novos com os existentes (respeitando campos atualizáveis e
 * versão); se não existe, gera itens de {@code create}. Respeita as regras de imutabilidade
 * configuradas na entidade ({@code isNotAnUpdatableEntity}).
 */
public class CcpBulkHandlerSave implements CcpHandleWithSearchResultsInTheEntity<List<CcpBulkItem>>{

	private final CcpEntity mainEntity;

	/**
	 * Inicializa o handler com a entidade alvo do upsert.
	 *
	 * @param mainEntity entidade na qual os registros serão criados ou atualizados
	 */
	public CcpBulkHandlerSave(CcpEntity mainEntity) {
		this.mainEntity = mainEntity;
	}
	
	/**
	 * Gera itens de {@code update}; para entidades imutáveis retorna {@code noop}; para entidades
	 * versionáveis, mescla os dados novos sobre os existentes preservando apenas os campos atualizáveis.
	 *
	 * @param searchParameter parâmetros da busca com os novos dados
	 * @param recordFound dados do registro existente no banco
	 * @return lista de itens bulk de atualização ou noop
	 */
	public List<CcpBulkItem> whenRecordWasFoundInTheEntitySearch(CcpJsonRepresentation searchParameter,	CcpJsonRepresentation recordFound) {
		
		var asList = this.mainEntity
			.toBulkItems(searchParameter, CcpBulkEntityOperationType.update)
			.stream().map(x -> this.toUpdateRecord(searchParameter, recordFound, x))	
			.collect(Collectors.toList())
				;
		return asList;
	}

	private CcpBulkItem toUpdateRecord(CcpJsonRepresentation searchParameter, CcpJsonRepresentation recordFound , CcpBulkItem x) {

		CcpEntityMetaData entityDetails = x.entity.getEntityMetaData();
		
		boolean isNotAnUpdatableEntity = entityDetails.isNotAnUpdatableEntity();
		
		if(isNotAnUpdatableEntity) {
			CcpBulkItem updatedBulkItem = new CcpBulkItem(x.json, CcpBulkEntityOperationType.noop, x.entity, x.id);
			return updatedBulkItem;
		}
		
		if(false == x.operation.createsVersionsToSameRecord) {
			return x;
		}
		
		CcpJsonRepresentation updatablePiece = x.json.getJsonPiece(entityDetails.onlyUpdatableFields);
		CcpJsonRepresentation mergeWithAnotherJson2 = x.json.mergeWithAnotherJson(recordFound);
		CcpJsonRepresentation mergeWithAnotherJson = mergeWithAnotherJson2.mergeWithAnotherJson(updatablePiece);
		CcpJsonRepresentation onlyExistingFields = entityDetails.getOnlyExistingFields(mergeWithAnotherJson);
		CcpBulkItem updatedBulkItem = new CcpBulkItem(onlyExistingFields, x.operation, x.entity, x.id);

		return updatedBulkItem;
	}

	/**
	 * Gera itens de {@code create} com os dados do {@code searchParameter}.
	 *
	 * @param searchParameter parâmetros da busca usados para criar o registro
	 * @return lista de itens bulk de criação
	 */
	public List<CcpBulkItem> whenRecordWasNotFoundInTheEntitySearch(CcpJsonRepresentation searchParameter) {
		List<CcpBulkItem> asList = this.mainEntity.toBulkItems(searchParameter, CcpBulkEntityOperationType.create);
		return asList;
	}

	/**
	 * Retorna a entidade alvo.
	 *
	 * @return entidade alvo
	 */
	public CcpEntity getEntityToSearch() {
		return this.mainEntity;
	}
}
