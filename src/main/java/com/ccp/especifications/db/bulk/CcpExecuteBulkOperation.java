package com.ccp.especifications.db.bulk;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.db.crud.CcpCrud;
import com.ccp.especifications.db.crud.CcpHandleWithSearchResultsInTheEntity;
import com.ccp.especifications.db.crud.CcpSelectUnionAll;
import com.ccp.especifications.db.utils.entity.CcpEntity;

/**
 * Contrato de alto nível que combina uma busca {@code unionAll} no banco com a execução subsequente
 * de operações bulk. Permite processar múltiplas entidades em uma única consulta e então enviar os
 * itens resultantes para o bulk, orquestrando busca e escrita de forma integrada.
 */
public interface CcpExecuteBulkOperation {

	/**
	 * Extrai o conjunto de entidades a buscar a partir dos handlers, executa um {@code unionAll} para
	 * todas elas, aplica cada handler ao resultado (coletando os {@link CcpBulkItem}s) e dispara a
	 * operação bulk com todos os itens coletados.
	 *
	 * @param json JSON com os parâmetros de busca
	 * @param functionToDeleteKeysInTheCache função para invalidar chaves de cache
	 * @param handlers handlers que definem entidades e lógica de criação/atualização/exclusão
	 * @return o {@link CcpSelectUnionAll} com os dados do resultado da busca
	 */
	@SuppressWarnings("unchecked")
	default CcpSelectUnionAll executeSelectUnionAllThenExecuteBulkOperation(CcpJsonRepresentation json,  Consumer<String[]> functionToDeleteKeysInTheCache, CcpHandleWithSearchResultsInTheEntity<List<CcpBulkItem>> ... handlers) {
		Set<CcpEntity> collect = Arrays.asList(handlers).stream().map(x -> x.getEntityToSearch()).collect(Collectors.toSet());
		CcpEntity[] array = collect.toArray(new CcpEntity[collect.size()]);
		CcpCrud crud = CcpDependencyInjection.getDependency(CcpCrud.class);
		CcpSelectUnionAll unionAll = crud.unionAll(json, functionToDeleteKeysInTheCache, array);
		
		List<CcpBulkItem> all = new ArrayList<>();
		
		for (CcpHandleWithSearchResultsInTheEntity<List<CcpBulkItem>> handler : handlers) {
			List<CcpBulkItem> list =  unionAll.handleRecordInUnionAll(json, handler);
			all.addAll(list);
		}
		
		
		this.executeBulk(all, functionToDeleteKeysInTheCache);

		return unionAll;
	}

	/**
	 * Executa efetivamente as operações bulk para a coleção de itens fornecida, delegando ao executor
	 * concreto e invalidando as chaves de cache necessárias.
	 *
	 * @param items coleção de itens bulk a processar
	 * @param functionToDeleteKeysInTheCache função para invalidar chaves de cache
	 * @return esta instância para encadeamento
	 */
	CcpExecuteBulkOperation executeBulk(Collection<CcpBulkItem> items,  Consumer<String[]> functionToDeleteKeysInTheCache);
}
