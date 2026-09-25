package com.ccp.especifications.cache;

import java.util.Collection;
import java.util.Map;
import java.util.function.Function;

import com.ccp.business.CcpBusiness;
import com.ccp.decorators.CcpJsonRepresentation;

/**
 * Contrato de acesso ao sistema de cache (ex.: GCP Memcache). Define operações básicas de leitura,
 * escrita e remoção de valores em cache, identificados por chave string, com suporte a tempo de
 * expiração e fallback automático quando a chave não está presente.
 */
public interface CcpCache {

	/**
	 * Recupera o valor armazenado para a chave informada.
	 *
	 * @param key a chave de cache
	 * @return o valor armazenado
	 */
	 Object get(String key) ;

	/**
	 * Tenta recuperar o valor do cache pela chave; se não houver entrada,
	 * executa {@code taskToGetValue} para obter o valor, grava o resultado no cache com o TTL
	 * informado e o retorna.
	 *
	 * @param key chave de cache
	 * @param json parâmetros contextuais passados à função de fallback
	 * @param taskToGetValue função executada quando a chave não está no cache
	 * @param cacheSeconds tempo de expiração em segundos
	 * @return o valor obtido do cache ou produzido pela função de fallback
	 */
		@SuppressWarnings("unchecked")
		default <V> V get(String key, CcpJsonRepresentation json, Function<CcpJsonRepresentation, V> taskToGetValue, int cacheSeconds) {

			Object object = this.get(key);
			boolean objectDiferente = object != null;

			if (objectDiferente) {
				V v = (V) object;
				return v;
			}
			V value = taskToGetValue.apply(json);
			this.put(key, value, cacheSeconds);

			return value;
		}

		@SuppressWarnings("unchecked")
		default CcpJsonRepresentation get(String key, CcpJsonRepresentation json, CcpBusiness taskToGetValue, int cacheSeconds) {

			Object object = this.get(key);
			boolean objectIgual = object == null;

			if (objectIgual) {
				CcpJsonRepresentation value = taskToGetValue.execute(json);
				this.put(key, value.content, cacheSeconds);
				return value;
			}
			
			if(object instanceof Map map) {
				CcpJsonRepresentation value = new CcpJsonRepresentation(map);
				return value;
			}
			String toString = object.toString();

			CcpJsonRepresentation value = new CcpJsonRepresentation(toString);
			return value;
			
		}
	
	
	/**
	 * Retorna o valor em cache para a chave ou, se ausente, retorna {@code defaultValue} sem lançar exceção.
	 *
	 * @param key a chave de cache
	 * @param defaultValue valor retornado quando a chave não está no cache
	 * @return o valor armazenado ou {@code defaultValue}
	 */
	@SuppressWarnings("unchecked")
	default <V> V getOrDefault(String key, V defaultValue) {
		Object object = this.get(key);
		boolean objectIgual2 = object == null;

		if(objectIgual2) {
			return defaultValue;
		}
		V v2 = (V) object;
		return v2;
	}
	
	/**
	 * Retorna o valor em cache para a chave; se ausente, lança a exceção fornecida,
	 * forçando o chamador a tratar o caso de cache miss.
	 *
	 * @param key a chave de cache
	 * @param e exceção lançada quando a chave não está no cache
	 * @return o valor armazenado
	 */
	@SuppressWarnings("unchecked")
	default <V> V getOrThrowException(String key, RuntimeException e) {
		Object object = this.get(key);
		boolean objectIgual3 = object == null;

		if(objectIgual3) {
			throw e;
		}
		V v3 = (V) object;

		return v3;
	}
	
	/**
	 * Verifica se existe algum valor associado à chave no cache.
	 *
	 * @param key a chave de cache
	 * @return {@code true} se o valor estiver presente
	 */
	default boolean isPresent(String key) {
		var get = this.get(key);
		boolean isPresent = get != null;
		return isPresent;
	}

	/**
	 * Armazena {@code value} no cache sob {@code key} com expiração em {@code secondsDelay} segundos.
	 *
	 * @param key chave de cache
	 * @param value valor a armazenar
	 * @param secondsDelay TTL em segundos
	 * @return a própria instância para encadeamento
	 */
	CcpCache put(String key, Object value, int secondsDelay);

	/**
	 * Remove a entrada da chave informada do cache.
	 *
	 * <p>Não devolve o valor removido de propósito. Devolvê-lo obriga a implementação a ler antes de
	 * apagar — duas idas ao servidor de cache em vez de uma — e nenhum dos chamadores usava o retorno:
	 * todos os pontos de remoção ({@code JnDeleteKeysFromCache} e os cinco de
	 * {@code DecoratorCacheEntity}) chamam este método como comando isolado.</p>
	 *
	 * @param key a chave de cache
	 */
	void delete(String key);

	/**
	 * Remove de uma vez todas as chaves informadas.
	 *
	 * <p>A implementação padrão apaga uma a uma, para que qualquer {@code CcpCache} continue
	 * funcionando sem alteração. Quem fala com um servidor de cache de verdade deve sobrescrever com a
	 * operação em lote do provedor: a invalidação dispara antes de <b>toda</b> busca
	 * ({@code CcpCrud.deleteKeysInCache}), e uma busca que toca nove entidades vira nove idas à rede
	 * quando poderia ser uma.</p>
	 *
	 * @param keys as chaves a remover
	 */
	default void deleteAll(Collection<String> keys) {
		for (String key : keys) {
			this.delete(key);
		}
	}
}
