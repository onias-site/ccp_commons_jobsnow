package com.ccp.especifications.db.utils.entity.decorators.engine;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.bulk.CcpBulkItem;
import com.ccp.especifications.db.bulk.CcpBulkEntityOperationType;
import com.ccp.especifications.db.utils.entity.CcpEntity;

public interface CcpEntityConfigurator {

	default List<CcpBulkItem> getFirstRecordsToInsert(){
		return new ArrayList<>();
	}
	
	default List<CcpBulkItem> toCreateBulkItems(CcpEntity entity, String... jsons){
		List<CcpBulkItem> collect = Arrays.asList(jsons).stream()
		.map(json -> new CcpJsonRepresentation(json))
		.map(json -> entity.getMainBulkItem(json, CcpBulkEntityOperationType.create))
		.collect(Collectors.toList());
		return collect;
	}
	
	default CcpEntity getEntity() {
		try {
			Class<? extends CcpEntityConfigurator> class1 = this.getClass();
			Field declaredField = class1.getDeclaredField("ENTITY");
			declaredField.setAccessible(true);
			Object object = declaredField.get(null);
			return (CcpEntity) object;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
	}
}
