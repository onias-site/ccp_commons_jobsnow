package com.ccp.especifications.db.query;

import java.util.List;
import java.util.function.Consumer;

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

	public CcpJsonRepresentation getResultAsPackage(String url, CcpHttpMethods method, int expectedStatus, String... array) {
		CcpJsonRepresentation resultAsPackage = this.requestExecutor.getResultAsPackage(url, method, expectedStatus, this.elasticQuery, this.resourcesNames, array);
		return resultAsPackage;
	}

	public CcpJsonRepresentation getTermsStatis(String fieldName) {
		CcpJsonRepresentation termsStatis = this.requestExecutor.getTermsStatis(this.elasticQuery, this.resourcesNames, fieldName);
		return termsStatis;
	}

	public CcpJsonRepresentation delete() {
		CcpJsonRepresentation delete = this.requestExecutor.delete(this.elasticQuery, this.resourcesNames);
		return delete;
	}

	public CcpJsonRepresentation update(CcpJsonRepresentation newValues) {
		CcpJsonRepresentation update = this.requestExecutor.update(this.elasticQuery, this.resourcesNames, newValues);
		return update;
	}

	public CcpQueryExecutorDecorator consumeQueryResult(String scrollTime, int size,
			Consumer<CcpJsonRepresentation> consumer, String... fields) {
		this.requestExecutor.consumeQueryResult(this.elasticQuery, this.resourcesNames, scrollTime, size, consumer, fields);
		return this;
	}

	public long total() {
		long total = this.requestExecutor.total(this.elasticQuery, this.resourcesNames);
		return total;
	}

	public List<CcpJsonRepresentation> getResultAsList(String... fieldsToSearch) {
		List<CcpJsonRepresentation> resultAsList = this.requestExecutor.getResultAsList(this.elasticQuery, this.resourcesNames, fieldsToSearch);
		return resultAsList;
	}

	public CcpJsonRepresentation getResultAsMap(String field) {
		CcpJsonRepresentation resultAsMap = this.requestExecutor.getResultAsMap(this.elasticQuery, this.resourcesNames, field);
		return resultAsMap;
	}

	public CcpJsonRepresentation getMap(String field) {
		CcpJsonRepresentation requestExecutorMap = this.requestExecutor.getMap(this.elasticQuery, this.resourcesNames, field);
		return requestExecutorMap;
	}

	public CcpJsonRepresentation getAggregations() {
		CcpJsonRepresentation aggregations = requestExecutor.getAggregations(this.elasticQuery, this.resourcesNames);
		return aggregations;
	}
}
