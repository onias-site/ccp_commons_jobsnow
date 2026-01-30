package com.ccp.especifications.db.query;

import java.util.List;
import java.util.function.Consumer;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.http.CcpHttpMethods;

public class CcpQueryExecutorDecorator {

	private final CcpQueryExecutor requestExecutor = CcpDependencyInjection.getDependency(CcpQueryExecutor.class); 
	
	private final String[] resourcesNames;
	
	private final CcpQueryOptions elasticQuery;

	protected CcpQueryExecutorDecorator(CcpQueryOptions elasticQuery, String... resourcesNames) {
		this.resourcesNames = resourcesNames;
		this.elasticQuery = elasticQuery;
	}

	public CcpJsonRepresentation getResultAsPackage(String url, CcpHttpMethods method, int expectedStatus,  String... array) {
		return this.requestExecutor.getResultAsPackage(url, method, expectedStatus, this.elasticQuery, this.resourcesNames, array);
	}

	public CcpJsonRepresentation getTermsStatis(String fieldName) {
		return this.requestExecutor.getTermsStatis(this.elasticQuery, this.resourcesNames, fieldName);
	}

	public CcpJsonRepresentation delete() {
		return this.requestExecutor.delete(this.elasticQuery, this.resourcesNames);
	}

	public CcpJsonRepresentation update(CcpJsonRepresentation newValues) {
		return this.requestExecutor.update(this.elasticQuery, this.resourcesNames, newValues);
	}

	public CcpQueryExecutorDecorator consumeQueryResult(String scrollTime, int size,
			Consumer<CcpJsonRepresentation> consumer, String... fields) {
		this.requestExecutor.consumeQueryResult(this.elasticQuery, this.resourcesNames, scrollTime, size, consumer, fields);
		return this;
	}

	public long total() {
		return this.requestExecutor.total(this.elasticQuery, this.resourcesNames);
	}

	public List<CcpJsonRepresentation> getResultAsList(String... fieldsToSearch) {
		return this.requestExecutor.getResultAsList(this.elasticQuery, this.resourcesNames, fieldsToSearch);
	}

	public CcpJsonRepresentation getResultAsMap(String field) {
		return this.requestExecutor.getResultAsMap(this.elasticQuery, this.resourcesNames, field);
	}

	public CcpJsonRepresentation getMap(String field) {
		return this.requestExecutor.getMap(this.elasticQuery, this.resourcesNames, field);
	}

	public CcpJsonRepresentation getAggregations() {
		return requestExecutor.getAggregations(this.elasticQuery, this.resourcesNames);
	};
	
	
	
	
}
