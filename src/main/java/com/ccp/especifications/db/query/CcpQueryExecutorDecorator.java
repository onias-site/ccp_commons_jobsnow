package com.ccp.especifications.db.query;

import java.util.List;
import java.util.function.Consumer;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.http.CcpHttpMethods;

/**
 * Decorator sobre CcpQueryExecutor que captura a query e os nomes de índices no construtor, simplificando as chamadas ao executor real.
 * Cada método delega ao CcpQueryExecutor injetado via DI sem precisar que o chamador repasse esses parâmetros repetidamente.
 */
public class CcpQueryExecutorDecorator {

	private final CcpQueryExecutor requestExecutor = CcpDependencyInjection.getDependency(CcpQueryExecutor.class); 
	
	private final String[] resourcesNames;
	
	private final CcpQueryOptions elasticQuery;

	protected CcpQueryExecutorDecorator(CcpQueryOptions elasticQuery, String... resourcesNames) {
		this.resourcesNames = resourcesNames;
		this.elasticQuery = elasticQuery;
	}

	/**
	 * Executa a query via HTTP personalizado com os índices e query pré-configurados.
	 */
	public CcpJsonRepresentation getResultAsPackage(String url, CcpHttpMethods method, int expectedStatus,  String... array) {
		return this.requestExecutor.getResultAsPackage(url, method, expectedStatus, this.elasticQuery, this.resourcesNames, array);
	}

	/**
	 * Delega para CcpQueryExecutor.getTermsStatis com os parâmetros fixados.
	 */
	public CcpJsonRepresentation getTermsStatis(String fieldName) {
		return this.requestExecutor.getTermsStatis(this.elasticQuery, this.resourcesNames, fieldName);
	}

	/**
	 * Delega para CcpQueryExecutor.delete com os parâmetros fixados.
	 */
	public CcpJsonRepresentation delete() {
		return this.requestExecutor.delete(this.elasticQuery, this.resourcesNames);
	}

	/**
	 * Delega para CcpQueryExecutor.update com os parâmetros fixados.
	 */
	public CcpJsonRepresentation update(CcpJsonRepresentation newValues) {
		return this.requestExecutor.update(this.elasticQuery, this.resourcesNames, newValues);
	}

	/**
	 * Itera resultados via scroll entregando um documento por vez ao consumer; retorna this para encadeamento.
	 */
	public CcpQueryExecutorDecorator consumeQueryResult(String scrollTime, int size,
			Consumer<CcpJsonRepresentation> consumer, String... fields) {
		this.requestExecutor.consumeQueryResult(this.elasticQuery, this.resourcesNames, scrollTime, size, consumer, fields);
		return this;
	}

	/**
	 * Retorna o total de documentos que correspondem à query pré-configurada.
	 */
	public long total() {
		return this.requestExecutor.total(this.elasticQuery, this.resourcesNames);
	}

	/**
	 * Retorna os resultados como lista de documentos JSON.
	 */
	public List<CcpJsonRepresentation> getResultAsList(String... fieldsToSearch) {
		return this.requestExecutor.getResultAsList(this.elasticQuery, this.resourcesNames, fieldsToSearch);
	}

	/**
	 * Retorna resultados agrupados como mapa indexado pelo campo informado.
	 */
	public CcpJsonRepresentation getResultAsMap(String field) {
		return this.requestExecutor.getResultAsMap(this.elasticQuery, this.resourcesNames, field);
	}

	/**
	 * Variante de agrupamento de resultados.
	 */
	public CcpJsonRepresentation getMap(String field) {
		return this.requestExecutor.getMap(this.elasticQuery, this.resourcesNames, field);
	}

	/**
	 * Retorna apenas o bloco de agregações do resultado da query.
	 */
	public CcpJsonRepresentation getAggregations() {
		return requestExecutor.getAggregations(this.elasticQuery, this.resourcesNames);
	};
	
	
	
	
}
