package com.ccp.especifications.db.utils;

import java.util.List;
import java.util.function.Consumer;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.bulk.CcpBulkOperationResult;
import com.ccp.especifications.db.utils.entity.fields.CcpErrorDbUtilsIncorrectEntityFields;
import com.ccp.especifications.http.CcpHttpMethods;
import com.ccp.especifications.http.CcpHttpResponseTransform;

/**
 * Contrato de baixo nível para execução de requisições ao banco de dados (Elasticsearch). Abstrai o
 * transporte HTTP e os detalhes de conexão, fornecendo métodos para executar requisições com diferentes
 * assinaturas e utilitários de configuração.
 */
public interface CcpDbRequester {

	/**
	 * Executa uma requisição HTTP com corpo JSON, recursos (índices), status esperado e transforma a resposta.
	 */
	<V> V executeHttpRequest(String trace, String url, CcpHttpMethods method, Integer expectedStatus, CcpJsonRepresentation body, String[] resources, CcpHttpResponseTransform<V> transformer);

	/**
	 * Variante que aceita o corpo como string e cabeçalhos explícitos.
	 */
	<V> V executeHttpRequest(String trace, String url, CcpHttpMethods method, Integer expectedStatus, String body, CcpJsonRepresentation headers, CcpHttpResponseTransform<V> transformer);

	/**
	 * Variante que aceita um JSON de fluxos de tratamento de erros em vez de status esperado fixo.
	 */
	<V> V executeHttpRequest(String trace, String url, CcpHttpMethods method, CcpJsonRepresentation flows, CcpJsonRepresentation body, CcpHttpResponseTransform<V> transformer);

	/**
	 * Variante sem lista de recursos.
	 */
	<V> V executeHttpRequest(String trace, String url, CcpHttpMethods method, Integer expectedStatus, CcpJsonRepresentation body, CcpHttpResponseTransform<V> transformer);

	/**
	 * Executa a configuração inicial do banco de dados (criação de índices/mapeamentos), relatando erros
	 * de mapeamento e erros gerais via consumers.
	 */
	List<CcpBulkOperationResult> executeDatabaseSetup(String pathToJavaClasses, String hostFolder, String pathToCreateEntityScript, Consumer<CcpErrorDbUtilsIncorrectEntityFields> whenIsIncorrectMapping, Consumer<Throwable> whenOccursAnError);

	/**
	 * Retorna os detalhes de conexão com o banco (host, porta, credenciais, etc.).
	 */
	CcpJsonRepresentation getConnectionDetails();

	/**
	 * Retorna o nome do campo usado para identificar o índice/entidade nas requisições de multi-get.
	 */
	String getFieldNameToEntity();

	/**
	 * Retorna o nome do campo usado para identificar o ID do documento nas requisições de multi-get.
	 */
	String getFieldNameToId();

	/**
	 * Cria os índices/tabelas no banco com base nos scripts e classes informados, registrando erros
	 * nos destinos indicados.
	 */
	CcpDbRequester createTables(String pathToCreateEntityScript, String pathToJavaClasses, String mappingJnEntitiesErrors,
			String insertErrors);
}
