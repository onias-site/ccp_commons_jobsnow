package com.ccp.especifications.cache;

import java.util.Arrays;
import java.util.Set;
import java.util.function.Function;

import com.ccp.business.CcpBusiness;
import com.ccp.constants.CcpOtherConstants;
import com.ccp.decorators.CcpFieldName;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.db.bulk.CcpBulkItem;
import com.ccp.especifications.db.utils.entity.CcpEntity;
import com.ccp.especifications.db.utils.entity.decorators.engine.CcpEntityMetaData;

/**
 * Wrapper orientado a objetos sobre {@link CcpCache} que encapsula tanto a chave de cache quanto
 * parâmetros contextuais ({@link CcpJsonRepresentation}). Simplifica o uso do cache nas camadas de
 * negócio, permitindo construir chaves compostas de forma fluente a partir de entidades e
 * identificadores, sem expor diretamente a interface {@link CcpCache}.
 */
public final class CcpCacheDecorator {
	
	private final CcpCache cache = CcpDependencyInjection.getDependency(CcpCache.class);
	
	private final CcpJsonRepresentation cacheParameters;

	public final String key;
	
	/**
	 * Cria o decorator a partir de um {@link CcpBulkItem}, derivando a chave cache do nome da entidade e do id do item.
	 *
	 * @param bulkItem item bulk cujos metadados definem a chave de cache
	 */
	public CcpCacheDecorator(CcpBulkItem bulkItem) {
		this(bulkItem.entity, bulkItem.id);
	}
	
	/**
	 * Cria o decorator com chave no formato {@code records.entity.<nome>.id.<id>}.
	 *
	 * @param entity entidade cujo nome compõe a chave
	 * @param id identificador do registro
	 */
	public CcpCacheDecorator(CcpEntity entity, String id) {
		CcpEntityMetaData entityDetails = entity.getEntityMetaData();
		this.cacheParameters = CcpOtherConstants.EMPTY_JSON;
		this.key = "records.entity." + entityDetails.entityName + ".id." + id ;
	}
	
	/**
	 * Cria o decorator diretamente com uma chave arbitrária.
	 *
	 * @param key chave de cache a ser usada
	 */
	public CcpCacheDecorator(String key) {
		this.cacheParameters = CcpOtherConstants.EMPTY_JSON;
		this.key = key;
	}
	
	private CcpCacheDecorator(CcpJsonRepresentation json, String key) {
		this.cacheParameters = json;
		this.key = key;
	}

	/**
	 * Delega para {@link CcpCache#get} usando a chave e parâmetros internos; executa
	 * {@code taskToGetValue} se o cache estiver vazio e armazena o resultado.
	 *
	 * @param taskToGetValue função executada quando o cache está vazio
	 * @param cacheSeconds TTL em segundos para o valor gravado
	 * @return o valor obtido do cache ou produzido pela função
	 */
	public <V> V get(Function<CcpJsonRepresentation,V> taskToGetValue, int cacheSeconds) {
		return this.cache.get(this.key, this.cacheParameters, taskToGetValue, cacheSeconds);
	}

	public CcpJsonRepresentation get(CcpBusiness taskToGetValue, CcpJsonRepresentation json, int cacheSeconds) {
		return this.cache.get(this.key, json, taskToGetValue, cacheSeconds);
	}

	/**
	 * Retorna o valor em cache ou {@code defaultValue} caso ausente.
	 *
	 * @param defaultValue valor retornado quando a chave não está no cache
	 * @return o valor armazenado ou {@code defaultValue}
	 */
	public <V> V getOrDefault(V defaultValue) {
		return this.cache.getOrDefault(this.key, defaultValue);
	}

	/**
	 * Retorna o valor em cache ou lança a exceção fornecida caso ausente.
	 *
	 * @param e exceção lançada quando a chave não está no cache
	 * @return o valor armazenado
	 */
	public <V> V getOrThrowException(RuntimeException e) {
		return this.cache.getOrThrowException(this.key, e);
	}

	/**
	 * Verifica se existe valor para a chave deste decorator no cache.
	 *
	 * @return {@code true} se o valor estiver presente no cache
	 */
	public boolean isPresentInTheCache() {
		return this.cache.isPresent(this.key);
	}

	/**
	 * Grava {@code value} no cache com TTL e retorna {@code this} para encadeamento.
	 *
	 * @param value valor a armazenar
	 * @param secondsDelay TTL em segundos
	 * @return esta instância para encadeamento
	 */
	public CcpCacheDecorator put(Object value, int secondsDelay) {
		this.cache.put(this.key, value, secondsDelay);
		return this;
	}

	/**
	 * Remove e retorna o valor associado à chave deste decorator no cache.
	 *
	 * @return o valor removido ou {@code null} se não havia entrada
	 */
	public <V> V delete() {
		return this.cache.delete(this.key);
	}
	
	/**
	 * Cria um novo decorator com chave estendida por {@code .<key>.<value>} e com o par adicionado
	 * ao JSON de parâmetros, permitindo chaves hierárquicas/compostas.
	 *
	 * @param key segmento de chave a acrescentar
	 * @param value valor correspondente ao segmento
	 * @return novo decorator com chave acumulada
	 */
	public CcpCacheDecorator incrementKey(String key, Object value) {
		String _key = this.key + "." + key + "." + value;
		CcpJsonRepresentation put = this.cacheParameters.put(new CcpFieldName(key), value);
		CcpCacheDecorator ccpCacheDecorator = new CcpCacheDecorator(put, _key);
		return ccpCacheDecorator;
	}
	
	/**
	 * Extrai um subconjunto de campos do JSON fornecido e aplica {@link #incrementKey} para cada um,
	 * retornando um novo decorator com chave acumulada.
	 *
	 * @param json JSON de onde os campos serão extraídos
	 * @param keys nomes dos campos a incluir na chave
	 * @return novo decorator com chave acumulada
	 */
	public CcpCacheDecorator incrementKeys(CcpJsonRepresentation json, String... keys) {
		
		CcpJsonRepresentation jsonPiece = json.getJsonPiece(Arrays.asList(keys));
		
		CcpCacheDecorator result = this.incrementKeys(jsonPiece);
		
		return result;
	}

	/**
	 * Itera sobre todos os campos do JSON informado e aplica {@link #incrementKey} para cada par campo/valor.
	 *
	 * @param jsonPiece JSON cujos campos compõem a extensão da chave
	 * @return novo decorator com chave acumulada
	 */
	public CcpCacheDecorator incrementKeys(CcpJsonRepresentation jsonPiece) {
		CcpCacheDecorator result = this;
		
		Set<String> keySet = jsonPiece.fieldSet();
		
		for (String key : keySet) {
			Object value = jsonPiece.get(new CcpFieldName(key));
			result = result.incrementKey(key, value);
		}
		return result;
	}
	
	/**
	 * Retorna a chave de cache atual do decorator.
	 *
	 * @return a chave de cache
	 */
	public String toString() {
		return this.key;
	}
}
