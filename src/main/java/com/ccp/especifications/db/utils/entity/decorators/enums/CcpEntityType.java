package com.ccp.especifications.db.utils.entity.decorators.enums;

import java.lang.reflect.Field;

import com.ccp.especifications.db.utils.entity.CcpEntity;
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpEntityTwin;
import com.ccp.especifications.db.utils.entity.decorators.engine.CcpEntityMetaData;

/**
 * Identifica a origem de uma entidade em configurações de anotação: {@code mainEntity} (a entidade
 * principal declarada no campo {@code ENTITY} da classe configuradora) ou {@code twinEntity}
 * (a entidade twin definida em {@code @CcpEntityTwin}). Usada como atributo em
 * {@code @CcpEntityOperation} e {@code @CcpEntityDataTransfer}.
 */
public enum CcpEntityType {
	mainEntity {
		public String extractEntityName(Class<?> clazz) {
			try {
				Field declaredField = clazz.getDeclaredField("ENTITY");
				CcpEntity entity =  (CcpEntity)declaredField.get(null);
				CcpEntityMetaData entityDetails = entity.getEntityMetaData();
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
	/**
	 * Extrai o nome da entidade (nome do índice) a partir da classe configuradora fornecida.
	 * @param clazz a classe configuradora da entidade
	 */
	public abstract String extractEntityName(Class<?> clazz);

}
