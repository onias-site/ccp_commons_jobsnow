package com.ccp.especifications.db.utils.decorators.engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.bulk.CcpBulkItem;
import com.ccp.especifications.db.bulk.CcpEntityBulkOperationType;
import com.ccp.especifications.db.utils.CcpEntity;

public interface CcpEntityConfigurator {

	default List<CcpBulkItem> getFirstRecordsToInsert(){
		return new ArrayList<>();
	}
	
	default List<CcpBulkItem> toCreateBulkItems(CcpEntity entity, String... jsons){
		List<CcpBulkItem> collect = Arrays.asList(jsons).stream()
		.map(json -> new CcpJsonRepresentation(json))
		.map(json -> entity.getMainBulkItem(json, CcpEntityBulkOperationType.create))
		.collect(Collectors.toList());
		return collect;
	}
}
