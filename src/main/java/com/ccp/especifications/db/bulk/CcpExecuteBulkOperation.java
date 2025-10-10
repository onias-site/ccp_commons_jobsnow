package com.ccp.especifications.db.bulk;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.db.crud.CcpCrud;
import com.ccp.especifications.db.crud.CcpHandleWithSearchResultsInTheEntity;
import com.ccp.especifications.db.crud.CcpSelectUnionAll;
import com.ccp.especifications.db.utils.CcpEntity;
import com.ccp.especifications.mensageria.receiver.CcpTopic;

public interface CcpExecuteBulkOperation {
	@SuppressWarnings("unchecked")
	default CcpSelectUnionAll executeSelectUnionAllThenExecuteBulkOperation(CcpJsonRepresentation json,  Consumer<String[]> functionToDeleteKeysInTheCache, CcpHandleWithSearchResultsInTheEntity<List<CcpBulkItem>> ... handlers) {
		Set<CcpEntity> collect = Arrays.asList(handlers).stream().map(x -> x.getEntityToSearch()).collect(Collectors.toSet());
		CcpEntity[] array = collect.toArray(new CcpEntity[collect.size()]);
		CcpCrud crud = CcpDependencyInjection.getDependency(CcpCrud.class);
		CcpSelectUnionAll unionAll = crud.unionAll(json, functionToDeleteKeysInTheCache, array);
		
		Set<CcpBulkItem> all = new HashSet<>();
		
		for (CcpHandleWithSearchResultsInTheEntity<List<CcpBulkItem>> handler : handlers) {
			List<CcpBulkItem> list =  unionAll.handleRecordInUnionAll(json, handler);
			all.addAll(list);
		}
		this.executeBulk(all);

		CcpJsonRepresentation data = json;
	
		for (CcpHandleWithSearchResultsInTheEntity<List<CcpBulkItem>> handler : handlers) {
			
			CcpEntity entityToSearch = handler.getEntityToSearch();
			
			boolean presentInThisUnionAll = entityToSearch.isPresentInThisUnionAll(unionAll, data);
			
			if(presentInThisUnionAll) {
				List<CcpTopic> doAfterSavingIfRecordIsFound = handler.doAfterSavingIfRecordIsFound();
				data = data.getTransformedJson(doAfterSavingIfRecordIsFound);
				continue;
			}
			List<CcpTopic> doAfterSavingIfRecordIsNotFound = handler.doAfterSavingIfRecordIsNotFound();
			data = data.getTransformedJson(doAfterSavingIfRecordIsNotFound);
		}
		
		return unionAll;
	}
	CcpExecuteBulkOperation executeBulk(Collection<CcpBulkItem> items);
}
