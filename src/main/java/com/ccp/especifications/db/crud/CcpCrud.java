package com.ccp.especifications.db.crud;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.cache.CcpCacheDecorator;
import com.ccp.especifications.db.utils.entity.CcpEntity;
import com.ccp.especifications.db.utils.entity.fields.CcpErrorEntityPrimaryKeyIsMissing;

/**
 * Contrato central de acesso ao banco de dados (Elasticsearch). Fornece operações CRUD básicas
 * (busca por id, save, exists, delete) e o mecanismo de busca em múltiplas entidades
 * simultaneamente via {@code unionAll}, com integração automática à invalidação de cache.
 */
public interface CcpCrud {

	/**
	 * Recupera diretamente um registro do banco pelo nome da entidade e pelo id.
	 *
	 * @param entityName nome da entidade/índice
	 * @param id identificador do registro
	 * @return JSON do registro encontrado
	 */
	CcpJsonRepresentation getOneById(String entityName, String id);

	/**
	 * Retorna o executor responsável por realizar buscas {@code unionAll} (multi-entidade em batch).
	 *
	 * @return executor de unionAll
	 */
	CcpUnionAllExecutor getUnionAllExecutor();

	/**
	 * Invalida as chaves de cache relevantes e realiza uma busca em todas as entidades informadas
	 * para todos os JSONs fornecidos, retornando um {@link CcpSelectUnionAll} com os resultados condensados.
	 *
	 * @param jsons array de JSONs de busca
	 * @param functionToDeleteKeysInTheCache função para invalidar chaves de cache
	 * @param entities entidades a consultar
	 * @return resultado condensado da busca
	 */
	default CcpSelectUnionAll unionAll(CcpJsonRepresentation[] jsons, Consumer<String[]> functionToDeleteKeysInTheCache, CcpEntity... entities) {
		this.deleteKeysInCache(jsons, functionToDeleteKeysInTheCache,  entities);
		List<CcpJsonRepresentation> asList = Arrays.asList(jsons);
		CcpUnionAllExecutor unionAllExecutor = this.getUnionAllExecutor();
		CcpSelectUnionAll unionAll = unionAllExecutor.unionAll(asList, entities);
		return unionAll;
	}

	/**
	 * Versão simplificada do {@code unionAll} para um único JSON de busca.
	 *
	 * @param json JSON de busca
	 * @param functionToDeleteKeysInTheCache função para invalidar chaves de cache
	 * @param entities entidades a consultar
	 * @return resultado condensado da busca
	 */
	default CcpSelectUnionAll unionAll(CcpJsonRepresentation json, Consumer<String[]> functionToDeleteKeysInTheCache, CcpEntity... entities) {
		CcpJsonRepresentation[] jsons = new CcpJsonRepresentation[] {json};
		
		CcpSelectUnionAll unionAll = this.unionAll(jsons, functionToDeleteKeysInTheCache, entities);
		return unionAll;
	}

	/**
	 * Persiste o JSON informado na entidade/índice com o id fornecido e retorna o registro salvo.
	 *
	 * @param entityName nome da entidade/índice
	 * @param json dados do registro
	 * @param id identificador do registro
	 * @return JSON do registro salvo
	 */
	CcpJsonRepresentation save(String entityName, CcpJsonRepresentation json, String id);

	/**
	 * Verifica se existe um documento com o id informado na entidade/índice especificado.
	 *
	 * @param entityName nome da entidade/índice
	 * @param id identificador do registro
	 * @return {@code true} se o documento existir
	 */
	boolean exists(String entityName, String id);

	/**
	 * Remove o documento com o id informado da entidade/índice.
	 *
	 * @param entityName nome da entidade/índice
	 * @param id identificador do registro
	 * @return {@code true} se a exclusão foi bem-sucedida
	 */
	boolean delete(String entityName, String id);

	/**
	 * Calcula as chaves de cache correspondentes a cada combinação entidade/json e as passa para a
	 * função de invalidação.
	 *
	 * @param jsons array de JSONs de busca
	 * @param functionToDeleteKeysInTheCache função para invalidar chaves de cache
	 * @param entities entidades a processar
	 * @return esta instância para encadeamento
	 */
	default CcpCrud deleteKeysInCache(CcpJsonRepresentation[] jsons, Consumer<String[]> functionToDeleteKeysInTheCache, CcpEntity... entities) {
		Set<String> keysToDeleteInCache = new HashSet<>();
		for (CcpEntity entity : entities) {
			for (CcpJsonRepresentation json : jsons) {
				try {
					String calculateId = entity.calculateId(json);
					CcpCacheDecorator cache = new CcpCacheDecorator(entity, calculateId);
					keysToDeleteInCache.add(cache.key);
				} catch (CcpErrorEntityPrimaryKeyIsMissing e) {

				}
			}
		}
		
		String[] array = keysToDeleteInCache.toArray(new String[keysToDeleteInCache.size()]);
		functionToDeleteKeysInTheCache.accept(array);
		return this;
	}
	

}
