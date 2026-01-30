package com.ccp.especifications.db.query;

import java.util.List;
import java.util.function.Consumer;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.http.CcpHttpMethods;
public interface CcpQueryExecutor {

	CcpJsonRepresentation getTermsStatis(CcpQueryOptions elasticQuery, String[] resourcesNames, String fieldName);

	CcpJsonRepresentation delete(CcpQueryOptions elasticQuery, String... resourcesNames);
	
	CcpJsonRepresentation update(CcpQueryOptions elasticQuery, String[] resourcesNames, CcpJsonRepresentation newValues) ;
	
	CcpQueryExecutor consumeQueryResult(CcpQueryOptions elasticQuery, String[] resourcesNames, String scrollTime, Long size, 
			Consumer<List<CcpJsonRepresentation>> consumer, String...fields);

	long total(CcpQueryOptions elasticQuery, String[] resourcesNames);

	List<CcpJsonRepresentation> getResultAsList(CcpQueryOptions elasticQuery, String[] resourcesNames, String... fieldsToSearch);
	
	CcpJsonRepresentation getResultAsMap(CcpQueryOptions elasticQuery, String[] resourcesNames, String field);

	CcpJsonRepresentation getResultAsPackage(String url, CcpHttpMethods method, int expectedStatus, CcpQueryOptions elasticQuery, String[] resourcesNames, String ...array);

	CcpJsonRepresentation getMap(CcpQueryOptions elasticQuery, String[] resourcesNames, String field);
	
	CcpJsonRepresentation getAggregations(CcpQueryOptions elasticQuery, String... resourcesNames) ;

	CcpQueryExecutor consumeQueryResult(CcpQueryOptions elasticQuery, String[] resourcesNames, String scrollTime, Integer size,
			Consumer<CcpJsonRepresentation> consumer, String... fields);
	

}
