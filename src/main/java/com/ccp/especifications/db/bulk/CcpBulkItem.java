package com.ccp.especifications.db.bulk;

import com.ccp.constants.CcpOtherConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonRepresentation.CcpJsonFieldName;
import com.ccp.especifications.db.utils.entity.CcpEntity;
import com.ccp.especifications.db.utils.entity.decorators.engine.CcpEntityMetaData;

enum CcpBulkItemFields implements CcpJsonFieldName{
	id,  entity, operation 
}


/**
 * Representa um único item (registro) dentro de uma operação bulk, agregando o JSON do registro,
 * o tipo de operação ({@link CcpBulkEntityOperationType}), a entidade de destino ({@link CcpEntity})
 * e o identificador calculado. Utilizado como unidade de trabalho no pipeline de bulk operations.
 */
public class CcpBulkItem {
	enum JsonFieldNames implements CcpJsonFieldName{
		json
	}

	public final CcpBulkEntityOperationType operation;
	public final CcpJsonRepresentation json;
	public final CcpEntity entity;
	public final String id;
	
	/**
	 * Cria um novo item copiando todos os campos de {@code other}, mas substituindo o tipo de operação.
	 * Útil no reprocessamento (ex.: trocar {@code create} por {@code update}).
	 *
	 * @param other item original a ser copiado
	 * @param operation novo tipo de operação
	 */
	public CcpBulkItem(CcpBulkItem other, CcpBulkEntityOperationType operation) {
		this(other.json, operation, other.entity, other.id);
	}

	/**
	 * Cria um item com todos os campos explicitamente informados.
	 *
	 * @param json dados do registro
	 * @param operation tipo de operação bulk
	 * @param entity entidade de destino
	 * @param id identificador do registro
	 */
	public CcpBulkItem(CcpJsonRepresentation json, CcpBulkEntityOperationType operation, CcpEntity entity, String id) {
		this.operation = operation;
		this.entity = entity;
		this.json = json;
		this.id = id;
	}

	/**
	 * Retorna representação textual contendo entity, operation e id do item, omitindo o JSON completo.
	 *
	 * @return representação textual do item
	 */
	public String toString() {
		CcpJsonRepresentation put = this.asMap();
		CcpJsonRepresentation jsonPiece = put.getJsonPiece(CcpBulkItemFields.values());
		String string = jsonPiece.toString();
		return string;
	}

	/**
	 * Serializa o item para um {@link CcpJsonRepresentation} contendo os campos {@code operation},
	 * {@code entity} (nome), {@code json} e {@code id}.
	 *
	 * @return JSON representando este item
	 */
	public CcpJsonRepresentation asMap() {
		CcpEntityMetaData entityDetails = this.entity.getEntityMetaData();
		CcpJsonRepresentation put = CcpOtherConstants.EMPTY_JSON
				.put(CcpBulkItemFields.operation, this.operation)
				.put(CcpBulkItemFields.entity, entityDetails.entityName)
				.put(JsonFieldNames.json, this.json)
				.put(CcpBulkItemFields.id, this.id);
		return put;
	}
	
	/**
	 * Calcula o hash baseado na combinação {@code entity + "_" + id}, garantindo unicidade por entidade e identificador.
	 *
	 * @return hash do item
	 */
	public int hashCode() {
		String string = this.entity + "_" + this.id ;
		int hashCode = string.hashCode();
		return hashCode;
	}

	/**
	 * Compara dois {@link CcpBulkItem} por entidade e id; retorna {@code false} se o objeto for de
	 * tipo diferente ou se entidade/id diferirem.
	 *
	 * @param obj objeto a comparar
	 * @return {@code true} se entidade e id forem iguais
	 */
	public boolean equals(Object obj) {
		try {
			CcpBulkItem other = (CcpBulkItem)obj;
			
			boolean differentEntity = false == other.entity.equals(this.entity);
			
			if(differentEntity) {
				return false;
			}
			
			boolean differentId = false == other.id.equals(this.id);
			
			if(differentId) {
				return false;
			}
			
			return true;
		} catch (Exception e) {
			return false;
		}
	}
}
