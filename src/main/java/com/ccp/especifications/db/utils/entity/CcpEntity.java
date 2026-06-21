package com.ccp.especifications.db.utils.entity;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Supplier;

import com.ccp.business.CcpBusiness;
import com.ccp.constants.CcpOtherConstants;
import com.ccp.decorators.CcpFieldName;
import com.ccp.decorators.CcpHashDecorator;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonRepresentation.CcpJsonFieldName;
import com.ccp.decorators.CcpStringDecorator;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.db.bulk.CcpBulkEntityOperationType;
import com.ccp.especifications.db.bulk.CcpBulkItem;
import com.ccp.especifications.db.crud.CcpCrud;
import com.ccp.especifications.db.crud.CcpSelectUnionAll;
import com.ccp.especifications.db.utils.CcpDbRequester;
import com.ccp.especifications.db.utils.entity.decorators.engine.CcpEntityMetaData;
import com.ccp.flow.CcpErrorFlowDisturb;
import com.ccp.hash.CcpHashAlgorithm;
import com.ccp.process.CcpProcessStatusDefault;

/**
 * Contrato central do sistema de entidades do jobsnow. Representa um índice/tabela no Elasticsearch e define
 * todas as operações CRUD, além de utilitários para cálculo de ID, busca em union-all, transferência de dados
 * e validação. Todo enum de entidade do sistema implementa esta interface.
 */
public interface CcpEntity  extends CcpJsonFieldName{
	public static enum JsonFieldNames implements CcpJsonFieldName{
		entity,
		_entities,

	}

	/**
	 * Retorna o nome da entidade (nome do índice no Elasticsearch) a partir dos metadados.
	 */
	default String name() {
		CcpEntityMetaData entityMetaData = this.getEntityMetaData();
		return entityMetaData.entityName;
	}

	/**
	 * Calcula o ID SHA-1 do documento com base nos campos de chave primária definidos nos metadados.
	 * Se não houver chave primária, gera um UUID aleatório.
	 */
	default String calculateId(CcpJsonRepresentation json) {
		CcpEntityMetaData entityDetails = this.getEntityMetaData();

		boolean hasNoPrimaryKey = entityDetails.primaryKeyNames.isEmpty();

		if(hasNoPrimaryKey) {
			throw new CcpEntityNoDefinedPrimaryKey(this);
		}

		ArrayList<Object> sortedPrimaryKeyValues = entityDetails.getSortedPrimaryKeyValues(json);

		String replace = sortedPrimaryKeyValues.toString().replace("[", "").replace("]", "");
		CcpHashDecorator hash2 = new CcpStringDecorator(replace).hash();
		String hash = hash2.asString(CcpHashAlgorithm.SHA1);
		return hash;
	}

	/**
	 * Copia dados desta entidade para outra entidade sem remover o registro original.
	 * Por padrão lança {@code UnsupportedOperationException}.
	 */
	default CcpJsonRepresentation copyDataTo(CcpJsonRepresentation json, CcpEntity entities) {
		return this.throwException();
	}

	/**
	 * Retorna os metadados da entidade (campos, chave primária, nome do índice, etc.).
	 * Método obrigatório a ser implementado por cada entidade.
	 */
	CcpEntityMetaData getEntityMetaData();

	/**
	 * Remove o documento correspondente ao JSON informado do índice desta entidade.
	 */
	default CcpJsonRepresentation delete(CcpJsonRepresentation json) {

		CcpCrud crud = CcpDependencyInjection.getDependency(CcpCrud.class);
		String calculateId = this.calculateId(json);
		CcpEntityMetaData entityDetails = this.getEntityMetaData();
		crud.delete(entityDetails.entityName, calculateId);
		return json;
	}

	/**
	 * Variante de delete sem restrições adicionais; delega para {@code delete} por padrão.
	 */
	default CcpJsonRepresentation deleteAnyWhere(CcpJsonRepresentation json) {
		CcpJsonRepresentation delete = this.delete(json);
		return delete;
	}

	/**
	 * Verifica se existe um documento com o ID calculado a partir do JSON informado.
	 */
	default boolean exists(CcpJsonRepresentation json) {
		CcpCrud crud = CcpDependencyInjection.getDependency(CcpCrud.class);
		String id = this.calculateId(json);
		CcpEntityMetaData entityDetails = this.getEntityMetaData();
		boolean exists = crud.exists(entityDetails.entityName, id);
		return exists;
	}

	/**
	 * Retorna a lista de entidades associadas. Por padrão, uma lista contendo apenas a própria entidade;
	 * sobrescrito em entidades com twin.
	 */
	default List<CcpEntity> getAssociatedEntities(){
		List<CcpEntity> asList = Arrays.asList(this);
		return asList;
	}

	/**
	 * Retorna o JSON possivelmente transformado antes de operações. Por padrão retorna o mesmo JSON sem alterações.
	 */
	default CcpJsonRepresentation getHandledJson(CcpJsonRepresentation json) {
		return json;
	}

	/**
	 * Busca um documento pelo ID calculado; lança {@code CcpErrorFlowDisturb} com status {@code NOT_FOUND}
	 * se não encontrado.
	 */
	default CcpJsonRepresentation getOneById(CcpJsonRepresentation json) {
		CcpEntityMetaData entityDetails = this.getEntityMetaData();
		CcpBusiness arara = x -> {
			CcpJsonRepresentation put = x.put(JsonFieldNames.entity, this);
			throw new CcpErrorFlowDisturb(put, CcpProcessStatusDefault.NOT_FOUND);
		};
		CcpJsonRepresentation md = entityDetails.getOneByIdOrHandleItIfThisIdWasNotFound(json, arara);
		return md;
	}

	/**
	 * Busca o documento e retorna envolvendo o resultado sob a chave da própria entidade.
	 */
	default CcpJsonRepresentation getOneByIdAnyWhere(CcpJsonRepresentation json) {
		CcpJsonRepresentation oneById = this.getOneById(json);
		CcpJsonRepresentation put = CcpOtherConstants.EMPTY_JSON.put(this, oneById);
		return put;
	}

	/**
	 * Monta a lista de parâmetros (entity + id) necessária para uma busca do tipo union-all.
	 */
	default List<CcpJsonRepresentation> getParametersToSearch(CcpJsonRepresentation json) {

		String id = this.calculateId(json);

		CcpDbRequester dependency = CcpDependencyInjection.getDependency(CcpDbRequester.class);

		String fieldNameToEntity = dependency.getFieldNameToEntity();
		String fieldNameToId = dependency.getFieldNameToId();

		CcpEntityMetaData entityDetails = this.getEntityMetaData();

		CcpJsonRepresentation mainRecord = CcpOtherConstants.EMPTY_JSON
		.put(new CcpFieldName(fieldNameToEntity), entityDetails.entityName)
		.put(new CcpFieldName(fieldNameToId), id)
		;
		List<CcpJsonRepresentation> asList = Arrays.asList(mainRecord);
		return asList;
	}

	/**
	 * Extrai o registro desta entidade de um resultado de union-all já executado.
	 */
	default CcpJsonRepresentation getRecordFromUnionAll(CcpSelectUnionAll unionAll, Supplier<CcpJsonRepresentation> jsonSupplier) {

		CcpJsonRepresentation json = jsonSupplier.get();

		CcpEntityMetaData entityDetails = this.getEntityMetaData();

		CcpJsonRepresentation handledJson = entityDetails.entity.getHandledJson(json);

		String id = this.calculateId(handledJson);

		CcpJsonRepresentation jsonValue = unionAll.getEntityRow(entityDetails.entityName, id);

		return jsonValue;
	}

	/**
	 * Retorna a entidade twin correspondente. Por padrão lança {@code UnsupportedOperationException}.
	 */
	default CcpEntity getTwinEntity() {
		CcpEntity throwException = this.throwException();
		return throwException;
	}

	/**
	 * Lança {@code UnsupportedOperationException} indicando que a operação não é suportada por esta entidade.
	 * Usado como proteção em operações de escrita em entidades somente leitura.
	 */
	default <T>T throwException() {
		CcpEntityMetaData entityDetails = this.getEntityMetaData();
		throw new UnsupportedOperationException("The entity '" + entityDetails.entityName + "' is just to read only");
	}

	/**
	 * Retorna a entidade "embrulhada" (a entidade base dentro de um decorator). Por padrão retorna {@code this}.
	 */
	default CcpEntity getWrapedEntity() {
		return this;
	}

	/**
	 * Verifica se o documento desta entidade está presente em um resultado de union-all.
	 */
	default boolean isPresentInThisUnionAll(CcpSelectUnionAll unionAll, CcpJsonRepresentation json) {

		CcpEntityMetaData entityDetails = this.getEntityMetaData();

		String id = this.calculateId(json);

		boolean present = unionAll.isPresent(entityDetails.entityName, id);

		return present;
	}

	/**
	 * Salva o documento no índice desta entidade, filtrando apenas os campos existentes nos metadados.
	 */
	default CcpJsonRepresentation save(CcpJsonRepresentation json) {
		CcpEntityMetaData entityDetails = this.getEntityMetaData();
		CcpJsonRepresentation onlyExistingFields = entityDetails.getOnlyExistingFields(json);
		String id = this.calculateId(json);
		CcpCrud crud = CcpDependencyInjection.getDependency(CcpCrud.class);
		crud.save(entityDetails.entityName, onlyExistingFields, id);
		return json;
	}

	/**
	 * Converte o JSON em itens de operação bulk para uso no executor de bulk.
	 */
	default List<CcpBulkItem> toBulkItems(CcpJsonRepresentation json, CcpBulkEntityOperationType operation) {
		CcpEntityMetaData entityDetails = this.getEntityMetaData();
		CcpJsonRepresentation onlyExistingFields = entityDetails.getOnlyExistingFields(json);
		String calculateId = this.calculateId(onlyExistingFields);
		CcpBulkItem ccpBulkItem = new CcpBulkItem(onlyExistingFields, operation, entityDetails.entity, calculateId);
		return Arrays.asList(ccpBulkItem);
	}

	/**
	 * Transfere (move) dados desta entidade para outra entidade, removendo o registro original.
	 * Por padrão lança {@code UnsupportedOperationException}.
	 */
	default CcpJsonRepresentation transferDataTo(CcpJsonRepresentation json, CcpEntity entities) {
		return this.throwException();
	}

	/**
	 * Valida o JSON antes de operações de escrita. Por padrão retorna o JSON sem validação.
	 */
	default CcpJsonRepresentation validateJson(CcpJsonRepresentation json) {
		return json;
	}

	/**
	 * Retorna o ID para buscar um registro descartável (disposable).
	 * Por padrão lança {@code UnsupportedOperationException}.
	 */
	default CcpJsonRepresentation getIdToSearchDisposableRecord(CcpJsonRepresentation json) {
		CcpJsonRepresentation throwException = this.throwException();
		return throwException;
	}

	@SuppressWarnings("serial")
	public static class CcpEntityNoDefinedPrimaryKey extends RuntimeException {
		public final CcpEntity entity;
		private CcpEntityNoDefinedPrimaryKey(CcpEntity entity) {
			super("The entity '" + entity.getEntityMetaData().entityName + "' has no defined primary key in his mapping");
			this.entity = entity;
		}
	}
}
