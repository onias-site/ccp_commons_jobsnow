package com.ccp.especifications.db.query;

import java.util.List;
import java.util.function.Consumer;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.http.CcpHttpMethods;
/**
 * Contrato de execução de queries no Elasticsearch.
 * Define todas as operações de consulta que podem ser realizadas com base em um CcpQueryOptions (a query construída) e os nomes dos índices alvo.
 */
public interface CcpQueryExecutor {

	/**
	 * Executa uma agregação de termos e retorna estatísticas dos valores do campo informado.
	 */
	CcpJsonRepresentation getTermsStatis(CcpQueryOptions elasticQuery, String[] resourcesNames, String fieldName);

	/**
	 * Apaga todos os documentos que correspondam à query nos índices informados.
	 */
	CcpJsonRepresentation delete(CcpQueryOptions elasticQuery, String... resourcesNames);
	
	/**
	 * Atualiza os documentos correspondentes à query com os novos valores fornecidos.
	 */
	CcpJsonRepresentation update(CcpQueryOptions elasticQuery, String[] resourcesNames, CcpJsonRepresentation newValues) ;
	
	/**
	 * Itera sobre os resultados da query em lotes usando scroll, passando cada lote para o consumer informado.
	 */
	CcpQueryExecutor consumeQueryResult(CcpQueryOptions elasticQuery, String[] resourcesNames, String scrollTime, Long size,
			Consumer<List<CcpJsonRepresentation>> consumer, String...fields);

	/**
	 * Retorna o total de documentos que correspondem à query.
	 */
	long total(CcpQueryOptions elasticQuery, String[] resourcesNames);

	/**
	 * Retorna os resultados da query como uma lista de documentos JSON.
	 */
	List<CcpJsonRepresentation> getResultAsList(CcpQueryOptions elasticQuery, String[] resourcesNames, String... fieldsToSearch);
	
	/**
	 * Retorna os resultados agrupados como um mapa indexado pelo campo informado.
	 */
	CcpJsonRepresentation getResultAsMap(CcpQueryOptions elasticQuery, String[] resourcesNames, String field);

	/**
	 * Executa a query via uma requisição HTTP personalizada e retorna o resultado bruto.
	 */
	CcpJsonRepresentation getResultAsPackage(String url, CcpHttpMethods method, int expectedStatus, CcpQueryOptions elasticQuery, String[] resourcesNames, String ...array);

	/**
	 * Variante de getResultAsMap com semântica ligeiramente diferente de agrupamento.
	 */
	CcpJsonRepresentation getMap(CcpQueryOptions elasticQuery, String[] resourcesNames, String field);
	
	/**
	 * Executa a query e retorna apenas o bloco de agregações do resultado.
	 */
	CcpJsonRepresentation getAggregations(CcpQueryOptions elasticQuery, String... resourcesNames) ;

	/**
	 * Variante de scroll que entrega um documento por vez ao consumer (em vez de listas).
	 */
	CcpQueryExecutor consumeQueryResult(CcpQueryOptions elasticQuery, String[] resourcesNames, String scrollTime, Integer size,
			Consumer<CcpJsonRepresentation> consumer, String... fields);
	

}
