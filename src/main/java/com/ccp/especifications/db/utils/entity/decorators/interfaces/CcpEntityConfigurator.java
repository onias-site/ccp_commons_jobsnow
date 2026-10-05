package com.ccp.especifications.db.utils.entity.decorators.interfaces;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.bulk.CcpBulkItem;
import com.ccp.especifications.db.bulk.CcpBulkEntityOperationType;
import com.ccp.especifications.db.utils.entity.CcpEntity;

/**
 * Contract of the entity configurator classes: gives the {@code CcpEntity} declared in the static {@code ENTITY} field of
 * the class, the records seeded by the database setup and helpers to build {@code create} bulk items.
 */
public interface CcpEntityConfigurator {

	/**
	 * Returns the records inserted by the database setup.
	 * @return the bulk items to insert; empty by default
	 */
	default List<CcpBulkItem> getFirstRecordsToInsert(){
		return new ArrayList<>();
	}
	/**
	 * Parses each JSON text and builds its {@code create} bulk items in the entity.
	 * @param entity the target entity
	 * @param jsons the records as JSON texts
	 * @return the bulk items
	 */
	default List<CcpBulkItem> toCreateBulkItems(CcpEntity entity, String... jsons){
		var response = new ArrayList<CcpBulkItem>();
		for (String string : jsons) {
			CcpJsonRepresentation json = new CcpJsonRepresentation(string);
			List<CcpBulkItem> bulkItems = entity.toBulkItems(json, CcpBulkEntityOperationType.create);
			response.addAll(bulkItems);
		}
		return response;
	}
	/**
	 * Builds the {@code create} bulk items of each record in the entity.
	 * @param entity the target entity
	 * @param jsons the records
	 * @return the bulk items
	 */
	default List<CcpBulkItem> toCreateBulkItems(CcpEntity entity, CcpJsonRepresentation... jsons){
		var response = new ArrayList<CcpBulkItem>();
		for (CcpJsonRepresentation json : jsons) {
			List<CcpBulkItem> bulkItems = entity.toBulkItems(json, CcpBulkEntityOperationType.create);
			response.addAll(bulkItems);
		}
		return response;
	}
	
	/**
	 * Returns the {@code CcpEntity} declared in the static {@code ENTITY} field of this class.
	 * @return the entity
	 */
	default CcpEntity getEntity() {
		Class<? extends CcpEntityConfigurator> class1 = this.getClass();
		Field declaredField = class1.getDeclaredField("ENTITY");
		declaredField.setAccessible(true);
		Object object = declaredField.get(null);
		CcpEntity ccpEntity = (CcpEntity) object;
		return ccpEntity;
		
	}
}
