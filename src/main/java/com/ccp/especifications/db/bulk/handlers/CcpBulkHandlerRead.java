package com.ccp.especifications.db.bulk.handlers;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.bulk.CcpBulkEntityOperationType;
import com.ccp.especifications.db.bulk.CcpBulkItem;
import com.ccp.especifications.db.crud.CcpHandleWithSearchResultsInTheEntity;
import com.ccp.especifications.db.utils.entity.CcpEntity;

/**
 * Handler bulk que implementa leitura sem modificação: se o registro existe, gera itens com
 * operação {@code noop} (sem efeito); se não existe, aplica uma função customizável (padrão: lista
 * vazia). Útil para incluir registros existentes em um lote sem alterá-los.
 */
public class CcpBulkHandlerRead implements CcpHandleWithSearchResultsInTheEntity<List<CcpBulkItem>>{

	private final CcpEntity entityToRead;

	private final Function<CcpBulkItem, List<CcpBulkItem>> whenRecordWasNotFoundInTheEntitySearch;

	/**
	 * Cria o handler com comportamento padrão ao não encontrar o registro: lista vazia.
	 *
	 * @param entityToRead entidade cujos registros serão lidos
	 */
	public CcpBulkHandlerRead(CcpEntity entityToRead) {
		this(entityToRead, json -> new ArrayList<>());
	}
	
	

	/**
	 * Cria o handler com comportamento customizado ao não encontrar o registro.
	 *
	 * @param entityToRead entidade cujos registros serão lidos
	 * @param whenRecordWasNotFoundInTheEntitySearch função aplicada quando o registro não é encontrado
	 */
	public CcpBulkHandlerRead(CcpEntity entityToRead, Function<CcpBulkItem, List<CcpBulkItem>> whenRecordWasNotFoundInTheEntitySearch) {
		this.entityToRead = entityToRead;
		this.whenRecordWasNotFoundInTheEntitySearch = whenRecordWasNotFoundInTheEntitySearch;
	}



	/**
	 * Gera itens bulk com operação {@code noop}, marcando o registro como "visto" sem alterar seu estado.
	 *
	 * @param json parâmetros da busca
	 * @param recordFound dados do registro encontrado
	 * @return lista de itens bulk com operação noop
	 */
	public List<CcpBulkItem> whenRecordWasFoundInTheEntitySearch(CcpJsonRepresentation json, CcpJsonRepresentation recordFound) {

		List<CcpBulkItem> asList = this.entityToRead.toBulkItems(json, CcpBulkEntityOperationType.noop);
		return asList;
	}

	/**
	 * Aplica a função de "não encontrado" fornecida no construtor ao item bulk correspondente.
	 *
	 * @param json parâmetros da busca
	 * @return resultado da função customizada de "não encontrado"
	 */
	public List<CcpBulkItem> whenRecordWasNotFoundInTheEntitySearch(CcpJsonRepresentation json) {
		String calculateId = this.entityToRead.calculateId(json);
		List<CcpBulkItem> apply = this.whenRecordWasNotFoundInTheEntitySearch.apply(new CcpBulkItem(json, CcpBulkEntityOperationType.delete, this.entityToRead, calculateId));
		return apply;
	}

	/**
	 * Retorna a entidade alvo de leitura.
	 *
	 * @return entidade alvo
	 */
	public CcpEntity getEntityToSearch() {
		return this.entityToRead;
	}
}
