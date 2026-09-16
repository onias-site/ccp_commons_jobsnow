package com.ccp.especifications.db.crud;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.cache.CcpCacheDecorator;
import com.ccp.especifications.db.utils.entity.CcpEntity;
import com.ccp.especifications.db.utils.entity.decorators.engine.CcpErrorEntityPrimaryKeyIsMissing;

/**
 * Contrato central de acesso ao banco de dados (Elasticsearch). Fornece operações CRUD básicas
 * (busca por id, save, exists, delete) e o mecanismo de busca em múltiplas entidades
 * simultaneamente via {@code unionAll}, com integração automática à invalidação de cache.
 */
public interface CcpCrud {

	CcpJsonRepresentation getOneById(String entityName, String id);

	CcpUnionAllExecutor getUnionAllExecutor();

	default CcpSelectUnionAll unionAll(CcpJsonRepresentation[] jsons, Consumer<String[]> functionToDeleteKeysInTheCache, CcpEntity... entities) {
		this.deleteKeysInCache(jsons, functionToDeleteKeysInTheCache, entities);
		List<CcpJsonRepresentation> asList = Arrays.asList(jsons);
		CcpUnionAllExecutor unionAllExecutor = this.getUnionAllExecutor();
		CcpSelectUnionAll unionAll = unionAllExecutor.unionAll(asList, entities);
		return unionAll;
	}

	default CcpSelectUnionAll unionAll(CcpJsonRepresentation json, Consumer<String[]> functionToDeleteKeysInTheCache, CcpEntity... entities) {
		CcpJsonRepresentation[] jsons = new CcpJsonRepresentation[]{json};
		CcpSelectUnionAll unionAll = this.unionAll(jsons, functionToDeleteKeysInTheCache, entities);
		return unionAll;
	}

	CcpJsonRepresentation save(String entityName, CcpJsonRepresentation json, String id);

	/**
	 * Informa se a resposta devolvida por {@code save} corresponde à inclusão de um documento novo,
	 * e não à atualização de um documento que já existia. Quem implementa este contrato é o único que
	 * conhece o formato da resposta do banco e o status HTTP que a acompanha, por isso é aqui que a
	 * resposta é interpretada.
	 *
	 * @param saveResponse a resposta devolvida por {@code save}
	 * @return {@code true} se o documento foi incluído, {@code false} se ele foi atualizado
	 */
	boolean isInsertedDocument(CcpJsonRepresentation saveResponse);

	boolean exists(String entityName, String id);

	boolean delete(String entityName, String id);

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
		int keysToDeleteInCacheSize = keysToDeleteInCache.size();
		String[] array = keysToDeleteInCache.toArray(new String[keysToDeleteInCacheSize]);
		functionToDeleteKeysInTheCache.accept(array);
		return this;
	}
}
