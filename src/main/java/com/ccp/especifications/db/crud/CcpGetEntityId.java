package com.ccp.especifications.db.crud;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.ccp.business.CcpBusiness;
import com.ccp.constants.CcpOtherConstants;
import com.ccp.decorators.CcpFieldName;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonRepresentation.CcpJsonFieldName;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.db.utils.CcpDbRequester;
import com.ccp.especifications.db.utils.entity.CcpEntity;
import com.ccp.especifications.db.utils.entity.decorators.engine.CcpEntityDecoratorTypes;
import com.ccp.especifications.db.utils.entity.decorators.engine.CcpEntityFactory;
import com.ccp.especifications.db.utils.entity.decorators.engine.CcpEntityMetaData;
import com.ccp.especifications.db.utils.entity.decorators.engine.CcpEntityMetaData.CcpErrorEntityPrimaryKeyIsMissing;
import com.ccp.flow.CcpErrorFlowDisturb;
import com.ccp.process.CcpProcessStatus;

/**
 * Ponto de entrada do fluent API de busca. Recebe um ou mais {@link CcpJsonRepresentation} como
 * parâmetros de busca e inicia a construção de um {@link CcpSelectProcedure}, permitindo encadear
 * condições de busca de forma legível.
 */
public class CcpGetEntityId {

	private final Collection<CcpJsonRepresentation> parametersToSearch;

	public CcpGetEntityId(CcpJsonRepresentation... parametersToSearch) {
		this.parametersToSearch = Arrays.asList(parametersToSearch);
	}

	public CcpSelectProcedure toBeginProcedureAnd() {
		return new CcpSelectProcedure(this.parametersToSearch, CcpOtherConstants.EMPTY_JSON);
	}

	// ─── Result holder ────────────────────────────────────────────────────────

	/**
	 * Representa o resultado condensado de uma busca {@code unionAll} — múltiplas entidades buscadas
	 * em uma única chamada ao banco. Internamente organiza os resultados em um mapa aninhado
	 * {@code { entidade → { id → dadosDoRegistro } }}.
	 */
	public static class CcpSelectUnionAll {

		enum JsonFieldNames implements CcpJsonFieldName {
			explainedSearch
		}

		public final CcpJsonRepresentation condensed;

		public CcpSelectUnionAll(CcpJsonRepresentation[] searchParameters, List<CcpJsonRepresentation> results, CcpEntity... entities) {

			CcpJsonRepresentation explainedSearch = CcpOtherConstants.EMPTY_JSON;

			for (CcpEntity entity : entities) {
				for (var searchParameter : searchParameters) {
					try {
						CcpEntityMetaData entityDetails = entity.getEntityMetaData();
						Supplier<CcpJsonRepresentation> supplier = searchParameter.getJsonSupplier();
						CcpJsonRepresentation primaryKeyValues = entityDetails.getPrimaryKeyValues(supplier);
						CcpEntity customEntity = CcpEntityFactory.getCustomEntity(entity, CcpEntityDecoratorTypes.FieldsValidator);
						CcpJsonRepresentation handledJson = customEntity.getHandledJson(primaryKeyValues);
						String id = entity.calculateId(handledJson);
						explainedSearch = explainedSearch.addToItem(entity, new CcpFieldName(id), primaryKeyValues);
					} catch (CcpErrorEntityPrimaryKeyIsMissing e) {
					}
				}
			}

			CcpDbRequester dependency = CcpDependencyInjection.getDependency(CcpDbRequester.class);
			String fieldNameToEntity = dependency.getFieldNameToEntity();
			String fieldNameToId = dependency.getFieldNameToId();

			CcpJsonRepresentation condensed = CcpOtherConstants.EMPTY_JSON;

			for (CcpJsonRepresentation result : results) {
				String id = result.getAsString(new CcpFieldName(fieldNameToId));
				String entityName = result.getAsString(new CcpFieldName(fieldNameToEntity));
				CcpJsonRepresentation removeKeys = result.removeFields(new CcpFieldName(fieldNameToEntity), new CcpFieldName(fieldNameToId));
				CcpJsonRepresentation innerJsonFromPath = explainedSearch.getInnerJsonFromPath(new CcpFieldName(entityName), new CcpFieldName(id));
				condensed = condensed.addToItem(new CcpFieldName(entityName), new CcpFieldName(id), removeKeys);
				condensed = condensed.addToItem(new CcpFieldName(entityName), new CcpFieldName(JsonFieldNames.explainedSearch + "." + id), innerJsonFromPath);
			}
			this.condensed = condensed;
		}

		public boolean isPresent(String entityName, String id) {
			boolean entityNotFound = false == this.condensed.containsAllFields(new CcpFieldName(entityName));
			if (entityNotFound) {
				return false;
			}
			CcpJsonRepresentation innerJson = this.condensed.getInnerJsonFromPath(new CcpFieldName(entityName), new CcpFieldName(id));
			boolean idNotFound = innerJson.isEmpty();
			if (idNotFound) {
				return false;
			}
			return true;
		}

		public <T> T handleRecordInUnionAll(CcpJsonRepresentation searchParameter, CcpHandleWithSearchResultsInTheEntity<T> handler) {
			CcpEntity entity = handler.getEntityToSearch();
			boolean recordNotFound = false == entity.isPresentInThisUnionAll(this, searchParameter);
			CcpJsonRepresentation handledJson = entity.getHandledJson(searchParameter);
			if (recordNotFound) {
				T whenRecordWasNotFoundInTheEntitySearch = handler.whenRecordWasNotFoundInTheEntitySearch(handledJson);
				return whenRecordWasNotFoundInTheEntitySearch;
			}
			Supplier<CcpJsonRepresentation> jsonSupplier = searchParameter.getJsonSupplier();
			CcpJsonRepresentation recordFound = entity.getRecordFromUnionAll(this, jsonSupplier);
			CcpJsonRepresentation apply = recordFound.mergeWithAnotherJson(handledJson);
			T whenRecordWasFoundInTheEntitySearch = handler.whenRecordWasFoundInTheEntitySearch(apply, recordFound);
			return whenRecordWasFoundInTheEntitySearch;
		}

		public CcpJsonRepresentation getEntityRow(String index, String id) {
			boolean indexNotFound = false == this.condensed.containsAllFields(new CcpFieldName(index));
			if (indexNotFound) {
				return CcpOtherConstants.EMPTY_JSON;
			}
			CcpJsonRepresentation innerJson = this.condensed.getInnerJson(new CcpFieldName(index));
			boolean idNotFound = false == innerJson.containsAllFields(new CcpFieldName(id));
			if (idNotFound) {
				return CcpOtherConstants.EMPTY_JSON;
			}
			CcpJsonRepresentation jsonValue = innerJson.getInnerJson(new CcpFieldName(id));
			return jsonValue;
		}

		public String toString() {
			return this.condensed.toString();
		}

		public List<CcpJsonRepresentation> getEntityRows(CcpEntity entity) {
			boolean indexNotFound = false == this.condensed.containsAllFields(entity);
			if (indexNotFound) {
				return new ArrayList<>();
			}
			CcpJsonRepresentation innerJson = this.condensed.getInnerJson(entity);
			Set<String> fieldSet = innerJson.fieldSet();
			CcpDbRequester dependency = CcpDependencyInjection.getDependency(CcpDbRequester.class);
			String fieldNameToId = dependency.getFieldNameToId();
			List<CcpJsonRepresentation> collect = fieldSet.stream().map(id -> innerJson.getInnerJson(new CcpFieldName(id)).put(new CcpFieldName(fieldNameToId), id)).collect(Collectors.toList());
			return collect;
		}
	}

	// ─── Fluent chain steps ───────────────────────────────────────────────────

	/**
	 * Núcleo do fluent API de busca. Permite declarar de forma encadeada e legível quais entidades
	 * consultar, quais condições de presença verificar e quais ações executar em cada caso.
	 */
	public static class CcpSelectProcedure {

		enum JsonFieldNames implements CcpJsonFieldName {
			statements, entity, found
		}

		private final Collection<CcpJsonRepresentation> parametersToSearch;
		private final CcpJsonRepresentation statements;

		CcpSelectProcedure(Collection<CcpJsonRepresentation> parametersToSearch, CcpJsonRepresentation statements) {
			this.parametersToSearch = parametersToSearch;
			this.statements = statements;
		}

		public CcpSelectLoadDataFromEntity loadThisIdFromEntity(CcpEntity entity) {
			CcpJsonRepresentation addToList = this.statements.addToList(JsonFieldNames.statements, CcpOtherConstants.EMPTY_JSON.put(JsonFieldNames.entity, entity));
			return new CcpSelectLoadDataFromEntity(this.parametersToSearch, addToList);
		}

		public CcpSelectFoundInEntity ifThisIdIsPresentInEntity(CcpEntity entity) {
			CcpJsonRepresentation put = CcpOtherConstants.EMPTY_JSON.put(JsonFieldNames.found, true).put(JsonFieldNames.entity, entity);
			CcpJsonRepresentation addToList = this.statements.addToList(JsonFieldNames.statements, put);
			return new CcpSelectFoundInEntity(this.parametersToSearch, addToList);
		}

		public CcpSelectFoundInEntity ifThisIdIsNotPresentInEntity(CcpEntity entity) {
			CcpJsonRepresentation put = CcpOtherConstants.EMPTY_JSON.put(JsonFieldNames.found, false).put(JsonFieldNames.entity, entity);
			CcpJsonRepresentation addToList = this.statements.addToList(JsonFieldNames.statements, put);
			return new CcpSelectFoundInEntity(this.parametersToSearch, addToList);
		}

		public CcpSelectNextStep executeAction(CcpBusiness action) {
			return this.addStatement("action", action);
		}

		private CcpSelectNextStep addStatement(String key, Object obj) {
			List<CcpJsonRepresentation> list = this.statements.getAsJsonList(JsonFieldNames.statements);
			list.add(CcpOtherConstants.EMPTY_JSON.put(new CcpFieldName(key), obj));
			CcpJsonRepresentation newStatements = this.statements.put(JsonFieldNames.statements, list);
			return new CcpSelectNextStep(this.parametersToSearch, newStatements);
		}
	}

	/**
	 * Etapa intermediária do fluent chain gerada por {@link CcpSelectProcedure#loadThisIdFromEntity}.
	 * Representa o ponto após declarar que os dados de uma entidade devem ser carregados.
	 */
	public static class CcpSelectLoadDataFromEntity {

		private final Collection<CcpJsonRepresentation> parametersToSearch;
		private final CcpJsonRepresentation statements;

		CcpSelectLoadDataFromEntity(Collection<CcpJsonRepresentation> parametersToSearch, CcpJsonRepresentation statements) {
			this.parametersToSearch = parametersToSearch;
			this.statements = statements;
		}

		public CcpSelectProcedure and() {
			return new CcpSelectProcedure(this.parametersToSearch, this.statements);
		}

		public CcpSelectFinally andFinally(CcpJsonFieldName... fields) {
			return new CcpSelectFinally(this.parametersToSearch, this.statements, fields);
		}
	}

	/**
	 * Etapa intermediária do fluent chain gerada por {@link CcpSelectProcedure#ifThisIdIsPresentInEntity}
	 * ou {@link CcpSelectProcedure#ifThisIdIsNotPresentInEntity}. Permite definir o que fazer quando
	 * a condição de presença for satisfeita.
	 */
	public static class CcpSelectFoundInEntity {

		public static enum JsonFieldNames implements CcpJsonFieldName {
			statements
		}

		private final Collection<CcpJsonRepresentation> parametersToSearch;
		private final CcpJsonRepresentation statements;

		CcpSelectFoundInEntity(Collection<CcpJsonRepresentation> parametersToSearch, CcpJsonRepresentation statements) {
			this.parametersToSearch = parametersToSearch;
			this.statements = statements;
		}

		public CcpSelectNextStep executeAction(CcpBusiness action) {
			return this.addStatement("action", action);
		}

		public CcpSelectNextStep returnStatus(CcpProcessStatus status) {
			return this.addStatement("status", status);
		}

		private CcpSelectNextStep addStatement(String key, Object obj) {
			List<CcpJsonRepresentation> list = this.statements.getAsJsonList(JsonFieldNames.statements);
			CcpJsonRepresentation lastStatement = list.get(list.size() - 1);
			CcpJsonRepresentation put = lastStatement.put(new CcpFieldName(key), obj);
			List<CcpJsonRepresentation> subList = list.subList(0, list.size() - 1);
			subList.add(put);
			CcpJsonRepresentation newStatements = this.statements.put(JsonFieldNames.statements, subList);
			return new CcpSelectNextStep(this.parametersToSearch, newStatements);
		}
	}

	/**
	 * Etapa intermediária do fluent chain que aparece após a definição de uma ação ou status em um
	 * statement. Permite continuar adicionando condições ao fluxo ou encerrar a cadeia.
	 */
	public static class CcpSelectNextStep {

		private final Collection<CcpJsonRepresentation> parametersToSearch;
		private final CcpJsonRepresentation statements;

		CcpSelectNextStep(Collection<CcpJsonRepresentation> parametersToSearch, CcpJsonRepresentation statements) {
			this.parametersToSearch = parametersToSearch;
			this.statements = statements;
		}

		public CcpSelectFinally andFinallyReturningTheseFields(Collection<CcpJsonFieldName> fields) {
			CcpJsonFieldName[] array = fields.toArray(new CcpJsonFieldName[fields.size()]);
			CcpSelectFinally ccpSelectFinally = new CcpSelectFinally(this.parametersToSearch, this.statements, array);
			return ccpSelectFinally;
		}

		public CcpSelectFinally andFinallyReturningTheseFields(CcpJsonFieldName... fields) {
			CcpSelectFinally ccpSelectFinally = new CcpSelectFinally(this.parametersToSearch, this.statements, fields);
			return ccpSelectFinally;
		}

		public CcpSelectProcedure and() {
			CcpSelectProcedure ccpSelectProcedure = new CcpSelectProcedure(this.parametersToSearch, this.statements);
			return ccpSelectProcedure;
		}
	}

	/**
	 * Etapa final do fluent chain de busca. Executa todos os statements acumulados, coordena a busca
	 * {@code unionAll}, aplica as regras do fluxo e retorna apenas os campos especificados.
	 * Lança {@link CcpErrorFlowDisturb} quando uma condição de fluxo não é satisfeita.
	 */
	public static class CcpSelectFinally {

		enum JsonFieldNames implements CcpJsonFieldName {
			statements, entity, action, found, status, message, errorDetails, flow, origin
		}

		private final Collection<CcpJsonRepresentation> parametersToSearch;
		private final CcpJsonRepresentation statements;
		private final CcpJsonFieldName[] fields;

		CcpSelectFinally(Collection<CcpJsonRepresentation> parametersToSearch, CcpJsonRepresentation statements, CcpJsonFieldName[] fields) {
			this.parametersToSearch = parametersToSearch;
			this.fields = fields;
			this.statements = statements;
		}

		public CcpSelectFinally endThisProcedure(CcpJsonFieldName context, CcpBusiness whenFlowError, CcpBusiness whenFlowSuccess, Consumer<String[]> functionToDeleteKeysInTheCache) {
			List<CcpJsonRepresentation> statements = this.statements.getAsJsonList(JsonFieldNames.statements);
			CcpJsonRepresentation[] array = statements.toArray(new CcpJsonRepresentation[statements.size()]);
			this.findById(context, whenFlowError, whenFlowSuccess, functionToDeleteKeysInTheCache, array);
			return this;
		}

		public CcpJsonRepresentation endThisProcedureRetrievingTheResultingData(CcpJsonFieldName context, CcpBusiness whenFlowError, CcpBusiness whenFlowSuccess, Consumer<String[]> functionToDeleteKeysInTheCache) {
			List<CcpJsonRepresentation> statements = this.statements.getAsJsonList(JsonFieldNames.statements);
			CcpJsonRepresentation[] array = statements.toArray(new CcpJsonRepresentation[statements.size()]);
			CcpJsonRepresentation findById = this.findById(context, whenFlowError, whenFlowSuccess, functionToDeleteKeysInTheCache, array);
			return findById;
		}

		private CcpJsonRepresentation findById(
				CcpJsonFieldName origin,
				CcpBusiness whenFlowError,
				CcpBusiness whenFlowSuccess,
				Consumer<String[]> functionToDeleteKeysInTheCache,
				CcpJsonRepresentation... specifications) {
 
			List<CcpEntity> keySet = Arrays.asList(specifications).stream()
					.filter(x -> x.containsAllFields(JsonFieldNames.entity))
					.map(x -> (CcpEntity) x.get(JsonFieldNames.entity))
					.collect(Collectors.toList());

			LinkedHashSet<CcpEntity> set = new LinkedHashSet<>(keySet);
			CcpEntity[] entities = set.toArray(new CcpEntity[set.size()]);

			CcpCrud crud = CcpDependencyInjection.getDependency(CcpCrud.class);

			CcpJsonRepresentation[] jsons = this.parametersToSearch.toArray(new CcpJsonRepresentation[this.parametersToSearch.size()]);

			CcpJsonRepresentation json = CcpOtherConstants.EMPTY_JSON;
			for (CcpJsonRepresentation jsn : jsons) {
				json = json.mergeWithAnotherJson(jsn);
			}

			CcpSelectUnionAll unionAll = crud.unionAll(jsons, functionToDeleteKeysInTheCache, entities);

			for (CcpJsonRepresentation specification : specifications) {

				boolean executeFreeAction = false == specification.containsField(JsonFieldNames.entity);

				if (executeFreeAction) {
					CcpBusiness action = specification.getAsObject(JsonFieldNames.action);
					json = action.apply(json);
					continue;
				}

				boolean shouldHaveBeenFound = specification.getAsBoolean(JsonFieldNames.found);
				CcpEntity entity = specification.getAsObject(JsonFieldNames.entity);

				boolean wasActuallyFound;
				try {
					wasActuallyFound = entity.isPresentInThisUnionAll(unionAll, json);
				} catch (Exception e) {
					throw new RuntimeException(e);
				}

				boolean itWasNotForeseen = wasActuallyFound != shouldHaveBeenFound;
				CcpEntityMetaData entityMetaData = entity.getEntityMetaData();

				if (itWasNotForeseen) {
					if (false == wasActuallyFound) {
						continue;
					}
					try {
						Supplier<CcpJsonRepresentation> jsonSupplier = json.getJsonSupplier();
						CcpJsonRepresentation dataBaseRow = entity.getRecordFromUnionAll(unionAll, jsonSupplier);
						json = json.addToItem(CcpEntity.JsonFieldNames._entities, entity, dataBaseRow);
						continue;
					} catch (Exception e) {
						entity.isPresentInThisUnionAll(unionAll, json);
						throw new RuntimeException(e);
					}
				}

				boolean willNotExecuteAction = false == specification.containsField(JsonFieldNames.action);

				if (willNotExecuteAction) {
					boolean willNotThrowException = false == specification.containsField(JsonFieldNames.status);
					if (willNotThrowException) {
						continue;
					}
					CcpProcessStatus status = specification.getAsObject(JsonFieldNames.status);
					String message = specification.getOrDefault(JsonFieldNames.message, () -> status.name());
					CcpJsonRepresentation put = json.addToItem(JsonFieldNames.errorDetails, JsonFieldNames.message, message)
							.addToItem(JsonFieldNames.errorDetails, JsonFieldNames.status, status);
					CcpJsonRepresentation apply = whenFlowError.apply(put);
					List<CcpJsonRepresentation> asList = Arrays.asList(specifications).stream()
							.map(j -> j.whenAnyFieldsAreFound(FunctionPutEntity.INSTANCE, JsonFieldNames.entity))
							.map(j -> j.whenAnyFieldsAreFound(FunctionPutStatus.INSTANCE, JsonFieldNames.status))
							.collect(Collectors.toList());
					CcpJsonRepresentation result = apply.put(JsonFieldNames.flow, asList);

					String reason = "Context: " + origin + ". Entity: " + entityMetaData.entityName
							+ ". status: " + status
							+ ". shouldHaveBeenFound: " + shouldHaveBeenFound + ". wasActuallyFound: " + wasActuallyFound;

					throw new CcpErrorFlowDisturb(result, status, reason, this.fields);
				}

				CcpBusiness action = specification.getAsObject(JsonFieldNames.action);

				if (false == shouldHaveBeenFound) {
					json = action.apply(json);
					continue;
				}
				Supplier<CcpJsonRepresentation> jsonSupplier = json.getJsonSupplier();
				CcpJsonRepresentation dataBaseRow = entity.getRecordFromUnionAll(unionAll, jsonSupplier);
				CcpJsonRepresentation context = json.addToItem(CcpEntity.JsonFieldNames._entities, entity, dataBaseRow);
				json = action.apply(context);
			}

			boolean zeroFields = this.fields.length <= 0;
			if (zeroFields) {
				throw new CcpErrorFlowFieldsToReturnNotMentioned(origin);
			}
			CcpJsonRepresentation apply = whenFlowSuccess.apply(json);
			CcpJsonRepresentation subMap = apply.getJsonPiece(this.fields).put(JsonFieldNames.origin, origin);
			return subMap;
		}
	}

	// ─── Exceptions ───────────────────────────────────────────────────────────

	/**
	 * Exceção lançada quando uma operação de multi-get no banco de dados retorna um erro explícito.
	 */
	@SuppressWarnings("serial")
	public static class CcpErrorCrudMultiGetSearchFailed extends RuntimeException {

		enum JsonFieldNames implements CcpJsonFieldName {
			type, reason
		}

		public CcpErrorCrudMultiGetSearchFailed(CcpJsonRepresentation error) {
			super(error.getAsString(JsonFieldNames.type) + ". Reason: " + error.getAsString(JsonFieldNames.reason));
		}
	}

	/**
	 * Exceção lançada quando nenhum dos itens da coleção de JSONs fornecida conseguiu produzir um id
	 * válido para as entidades informadas, tornando a busca {@code multiGet} inviável.
	 */
	@SuppressWarnings("serial")
	public static class CcpErrorCrudMultiGetSearchUnfeasible extends RuntimeException {

		public CcpErrorCrudMultiGetSearchUnfeasible(Collection<CcpJsonRepresentation> jsons, CcpEntity... entities) {
			super(getMessage(jsons, entities));
		}

		private static String getMessage(Collection<CcpJsonRepresentation> jsons, CcpEntity... entities) {
			var entitiesDetails = Arrays.asList(entities).stream().map(entity -> entity.getEntityMetaData()).collect(Collectors.toList());
			return "No item in the following list '" + entitiesDetails + "' was able to produce a "
					+ "valid id to searching in the database. The list of items used to form ids to searching: " + jsons + " and ";
		}
	}

	/**
	 * Exceção lançada no encerramento de um fluxo de busca ({@link CcpSelectFinally}) quando nenhum
	 * campo de retorno foi especificado.
	 */
	@SuppressWarnings("serial")
	public static class CcpErrorFlowFieldsToReturnNotMentioned extends RuntimeException {

		public CcpErrorFlowFieldsToReturnNotMentioned(CcpJsonFieldName origin) {
			super("at least one field must be mentioned. Origin: " + origin.name());
		}
	}
	
	
}

/**
 * Business interno (package-private) que substitui o campo {@code entity} de um JSON — que carrega um objeto
 * {@code CcpEntity} — pelo nome textual da entidade ({@code entityName}). Usado como passo de preparação antes
 * de operações de persistência.
 */
class FunctionPutEntity implements CcpBusiness{
	enum JsonFieldNames implements CcpJsonFieldName{
		entity
	}

	public static final FunctionPutEntity INSTANCE = new FunctionPutEntity();

	private FunctionPutEntity() {}

	/**
	 * Extrai o objeto {@code CcpEntity} do campo {@code entity}, obtém seu nome via {@code getEntityMetaData()}
	 * e substitui o campo pelo nome textual.
	 */
	public CcpJsonRepresentation apply(CcpJsonRepresentation j) {

		CcpEntity ent = j.getAsObject(JsonFieldNames.entity);
		CcpEntityMetaData entityDetails = ent.getEntityMetaData();
		String entityName = entityDetails.entityName;
		CcpJsonRepresentation put2 = j.put(JsonFieldNames.entity, entityName);
		return put2;
	}
}
/**
 * Business interno (package-private) que converte o campo {@code status} — que contém um {@code CcpProcessStatus}
 * — em dois campos textuais: {@code statusName} (nome do enum) e {@code statusNumber} (valor numérico).
 * Remove o campo original {@code status} do resultado.
 */
class FunctionPutStatus implements CcpBusiness {
	enum JsonFieldNames implements CcpJsonFieldName{
		status, statusName, statusNumber
	}

	public static final FunctionPutStatus INSTANCE = new FunctionPutStatus();

	private FunctionPutStatus() {}

	/**
	 * Lê o {@code CcpProcessStatus} do campo {@code status}, adiciona {@code statusName} e {@code statusNumber}
	 * ao JSON e remove o campo {@code status} original.
	 */
	public CcpJsonRepresentation apply(CcpJsonRepresentation j) {
		CcpProcessStatus stats = j.getAsObject(JsonFieldNames.status);
		CcpJsonRepresentation put3 = j.put(JsonFieldNames.statusName, stats.name())
				.put(JsonFieldNames.statusNumber, stats.asNumber());
		CcpJsonRepresentation removeField = put3.removeFields(JsonFieldNames.status);
		return removeField;

	}

}

