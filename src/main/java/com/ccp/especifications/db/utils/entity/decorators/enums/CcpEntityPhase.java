package com.ccp.especifications.db.utils.entity.decorators.enums;

import java.lang.reflect.Field;

import com.ccp.especifications.db.utils.entity.CcpEntity;
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpEntityTwin;
import com.ccp.especifications.db.utils.entity.decorators.engine.CcpEntityMetaData;

/**
 * Which side of an entity an annotation item refers to: {@code mainEntity} (the entity in the static {@code ENTITY}
 * field of the configurator class) or {@code twinEntity} (the twin named by {@code @CcpEntityTwin}).
 */
public enum CcpEntityPhase {
	/** The main entity of the configurator class. */
	mainEntity {
		/**
		 * Returns the name of the {@code ENTITY} of the configurator class.
		 * @param clazz the configurator class
		 * @return the main entity name
		 */
		public String extractEntityName(Class<?> clazz) {
			Field declaredField = clazz.getDeclaredField("ENTITY");
			var get = declaredField.get(null);
			CcpEntity entity =  (CcpEntity)get;
			CcpEntityMetaData entityDetails = entity.getEntityMetaData();
			return entityDetails.entityName;
			
		}
	},	
	/** The twin entity named by {@code @CcpEntityTwin}. */
	twinEntity {
		/**
		 * Returns the {@code twinEntityName} of {@code @CcpEntityTwin}.
		 * @param clazz the configurator class
		 * @return the twin entity name, or {@code ""} when the class has no twin
		 */
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
	/**
	 * Returns the entity name of this side for the configurator class.
	 * @param clazz the configurator class
	 * @return the entity name
	 */
	public abstract String extractEntityName(Class<?> clazz);

}
