package com.ccp.especifications.cache;

import java.util.function.Function;

import com.ccp.decorators.CcpJsonRepresentation;

/**
 * Contrato de acesso ao sistema de cache (ex.: GCP Memcache). Define operações básicas de leitura,
 * escrita e remoção de valores em cache, identificados por chave string, com suporte a tempo de
 * expiração e fallback automático quando a chave não está presente.
 */
public interface CcpCache {

	/**
	 * Recupera o valor armazenado para a chave informada, retornando {@code null} se não existir.
	 *
	 * @param key a chave de cache
	 * @return o valor armazenado ou {@code null} se ausente
	 */
	 Object get(String key) ;

	/**
	 * Tenta recuperar o valor do cache pela chave; se não houver entrada (ou após deletá-la),
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

		Object object = this.delete(key);

		if (object != null) {
			return (V) object;
		}
		V value = taskToGetValue.apply(json);
		this.put(key, value, cacheSeconds);

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
		
		if(object == null) {
			return defaultValue;
		}
		return (V) object;
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
		
		if(object == null) {
			throw e;
		}
		
		return (V) object;
	}
	
	/**
	 * Verifica se existe algum valor associado à chave no cache.
	 *
	 * @param key a chave de cache
	 * @return {@code true} se o valor estiver presente
	 */
	default boolean isPresent(String key) {
		boolean isPresent = this.get(key) != null;
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
	 * Remove a entrada da chave informada do cache e retorna o valor que estava armazenado.
	 *
	 * @param key a chave de cache
	 * @return o valor removido ou {@code null} se não havia entrada
	 */
	<V> V delete(String key);
}
