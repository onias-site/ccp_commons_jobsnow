package com.ccp.especifications.db.query;

import java.util.List;
import java.util.function.Consumer;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.http.CcpHttpMethods;
public interface CcpQueryExecutor {

	CcpJsonRepresentation getTermsStatis(CcpDbQueryOptions elasticQuery, String[] resourcesNames, String fieldName);

	CcpJsonRepresentation delete(CcpDbQueryOptions elasticQuery, String[] resourcesNames);
	
	CcpJsonRepresentation update(CcpDbQueryOptions elasticQuery, String[] resourcesNames, CcpJsonRepresentation newValues) ;
	
	CcpQueryExecutor consumeQueryResult(CcpDbQueryOptions elasticQuery, String[] resourcesNames, String scrollTime, Long size, 
			Consumer<List<CcpJsonRepresentation>> consumer, String...fields);

	long total(CcpDbQueryOptions elasticQuery, String[] resourcesNames);

	List<CcpJsonRepresentation> getResultAsList(CcpDbQueryOptions elasticQuery, String[] resourcesNames, String... fieldsToSearch);
	
	CcpJsonRepresentation getResultAsMap(CcpDbQueryOptions elasticQuery, String[] resourcesNames, String field);

	CcpJsonRepresentation getResultAsPackage(String url, CcpHttpMethods method, int expectedStatus, CcpDbQueryOptions elasticQuery, String[] resourcesNames, String ...array);

	CcpJsonRepresentation getMap(CcpDbQueryOptions elasticQuery, String[] resourcesNames, String field);
	
	CcpJsonRepresentation getAggregations(CcpDbQueryOptions elasticQuery, String... resourcesNames) ;

	CcpQueryExecutor consumeQueryResult(CcpDbQueryOptions elasticQuery, String[] resourcesNames, String scrollTime, Integer size,
			Consumer<CcpJsonRepresentation> consumer, String... fields);
	

}
