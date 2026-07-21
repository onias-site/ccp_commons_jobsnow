package com.ccp.especifications.db.crud;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.function.Consumer;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.ccp.business.CcpBusiness;
import com.ccp.constants.CcpOtherConstants;
import com.ccp.decorators.CcpFieldName;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonRepresentation.CcpJsonFieldName;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.db.utils.entity.CcpEntity;
import com.ccp.especifications.db.utils.entity.decorators.engine.CcpEntityMetaData;
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

			CcpSelectUnionAll unionAll = crud.unionAll(jsons, functionToDeleteKeysInTheCache, entities);
			
			CcpJsonRepresentation json = jsons[0];
 			
			for (CcpJsonRepresentation specification : specifications) {

				boolean executeFreeAction = false == specification.containsField(JsonFieldNames.entity);

				if (executeFreeAction) {
					CcpBusiness action = specification.getAsObject(JsonFieldNames.action);
					json = action.apply(json);
					continue;
				}

				boolean shouldHaveBeenFound = specification.getAsBoolean(JsonFieldNames.found);
				CcpEntity entity = specification.getAsObject(JsonFieldNames.entity);

				boolean wasActuallyFound = this.isPresentInUnionAll(unionAll, entity);

				boolean itWasNotForeseen = wasActuallyFound != shouldHaveBeenFound;
				CcpEntityMetaData entityMetaData = entity.getEntityMetaData();

				if (itWasNotForeseen) {
					if (false == wasActuallyFound) {
						continue;
					}

					CcpJsonRepresentation dataBaseRow = this.getRecordFromUnionAll(unionAll, entity);
					json = json.addToItem(CcpEntity.JsonFieldNames._entities, entity, dataBaseRow);
					continue;
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
					CcpJsonRepresentation dataBaseRow = this.getRecordFromUnionAll(unionAll, entity);
					CcpJsonRepresentation context = put.addToItem(CcpEntity.JsonFieldNames._entities, entity, dataBaseRow);
					CcpJsonRepresentation apply = whenFlowError.apply(context);
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
				CcpJsonRepresentation dataBaseRow = this.getRecordFromUnionAll(unionAll, entity);
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
		
		private boolean isPresentInUnionAll(CcpSelectUnionAll unionAll, CcpEntity entity) {
			
			for (CcpJsonRepresentation parameterToSearch : this.parametersToSearch) {
				CcpEntityMetaData metaData = entity.getEntityMetaData();
				boolean isNotParameterToSearch = false == parameterToSearch.containsAllFields(metaData.primaryKeyNames);
				if(isNotParameterToSearch) {
					continue;
				}
				boolean presentInThisUnionAll = entity.isPresentInThisUnionAll(unionAll, parameterToSearch);
				if(presentInThisUnionAll) {
					return true;
				}
			}
			return false;
		}
		
		private CcpJsonRepresentation getRecordFromUnionAll(CcpSelectUnionAll unionAll, CcpEntity entity) {
			
			for (CcpJsonRepresentation parameterToSearch : this.parametersToSearch) {
				CcpEntityMetaData metaData = entity.getEntityMetaData();
				boolean isNotParameterToSearch = false == parameterToSearch.containsAllFields(metaData.primaryKeyNames);
				if(isNotParameterToSearch) {
					continue;
				}
				
				boolean isNotPresentInThisUnionAll = false == entity.isPresentInThisUnionAll(unionAll, parameterToSearch);
				
				if(isNotPresentInThisUnionAll) {
					continue;
				}
				
				Supplier<CcpJsonRepresentation> jsonSupplier = parameterToSearch.getJsonSupplier();
				CcpJsonRepresentation recordFromUnionAll = entity.getRecordFromUnionAll(unionAll, jsonSupplier);
				return recordFromUnionAll;
			}
			return CcpOtherConstants.EMPTY_JSON;
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

