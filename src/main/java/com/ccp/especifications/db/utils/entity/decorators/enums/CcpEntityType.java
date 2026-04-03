package com.ccp.especifications.db.utils.entity.decorators.enums;

import java.lang.reflect.Field;

import com.ccp.especifications.db.utils.entity.CcpEntity;
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpEntityTwin;
import com.ccp.especifications.db.utils.entity.decorators.engine.CcpEntityDetails;

public enum CcpEntityType {
	mainEntity {
		public String extractEntityName(Class<?> clazz) {
			try {
				Field declaredField = clazz.getDeclaredField("ENTITY");
				CcpEntity entity =  (CcpEntity)declaredField.get(null);
				CcpEntityDetails entityDetails = entity.getEntityDetails();
				return entityDetails.entityName;
				
			} catch (Exception e) {
				throw new RuntimeException(e);
			}
		}
	},	
	twinEntity {
		public String extractEntityName(Class<?> clazz) {
			CcpEntityTwin annotation = clazz.getAnnotation(CcpEntityTwin.class);
			boolean isNotTwinEntity = annotation == null;
			
			if(isNotTwinEntity) {
				return "";
			}
			
			String twinEntityName = annotation.twinEntityName();
			return twinEntityName;
		}
	}
	;
	public abstract String extractEntityName(Class<?> clazz);

}
