package com.ccp.especifications.db.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;

import com.ccp.constants.CcpOtherConstants;
import com.ccp.decorators.CcpFieldName;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpJsonRepresentation.CcpJsonFieldName;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.db.utils.entity.CcpEntity;
import com.ccp.especifications.db.utils.entity.decorators.engine.CcpEntityMetaData;
import com.ccp.especifications.db.utils.entity.fields.CcpEntityField;
import com.ccp.especifications.http.CcpHttpMethods;

/**
 * Nó raiz do builder fluent de queries do Elasticsearch.
 * Ponto de entrada único para iniciar a construção de uma query, configurar paginação, ordenação e executar a consulta nos índices desejados.
 * Possui uma instância singleton INSTANCE para uso como ponto de partida.
 */
public class CcpQueryOptions extends CcpQueryComponent {
	enum JsonFieldNames implements CcpJsonFieldName {
		sort, match_all
	}

	public static final CcpQueryOptions INSTANCE = new CcpQueryOptions();

	private CcpQueryOptions() {
		super(null, "");
	}

	/**
	 * Inicia o bloco query principal da requisição.
	 */
	public CcpQuery startQuery() {
		return new CcpQuery(this);
	}

	/**
	 * Inicia uma query simplificada sem o wrapper bool/filter.
	 */
	public CcpQuerySimplifiedQuery startSimplifiedQuery() {
		return new CcpQuerySimplifiedQuery(this);
	}

	/**
	 * Inicia o bloco de agregações da requisição.
	 */
	public CcpQueryAggregations startAggregations() {
		return new CcpQueryAggregations(this);
	}

	/**
	 * Adiciona ordenação ascendente pelo(s) campo(s) informado(s).
	 */
	public CcpQueryOptions addAscSorting(String fields) {
		CcpQueryOptions sort = this.addSorting("asc", fields);
		return sort;
	}

	/**
	 * Adiciona ordenação descendente pelo(s) campo(s) informado(s).
	 */
	public CcpQueryOptions addDescSorting(String... fields) {
		CcpQueryOptions sort = this.addSorting("desc", fields);
		return sort;
	}

	/**
	 * Adiciona ordenação com tipo customizado (asc/desc) para múltiplos campos.
	 */
	public CcpQueryOptions addSorting(String sortType, String... fields) {
		CcpQueryOptions sort = this;
		for (String field : fields) {
			sort = sort.sort(field, sortType);
		}
		return sort;
	}

	private CcpQueryOptions sort(String fieldName, String sortType) {
		CcpQueryOptions copy = this.copy();
		Map<String, Object> content = CcpOtherConstants.EMPTY_JSON.put(new CcpFieldName(fieldName), sortType).getContent();
		List<Object> asList = Arrays.asList(content);
		if (copy.json.containsAllFields(JsonFieldNames.sort)) {
			List<Object> sort = copy.json.getAsObjectList(JsonFieldNames.sort);
			asList = new ArrayList<>(sort);
			asList.add(content);
		}
		copy.json = copy.json.put(JsonFieldNames.sort, asList);
		return copy;
	}

	@SuppressWarnings("unchecked")
	protected <T extends CcpQueryComponent> T getInstanceCopy() {
		return (T) new CcpQueryOptions();
	}

	/**
	 * Associa esta query a índices pelo nome (string) e retorna um executor pronto para uso.
	 */
	public CcpQueryExecutorDecorator selectFrom(String... resourcesNames) {
		return new CcpQueryExecutorDecorator(this, resourcesNames);
	}

	/**
	 * Associa esta query a índices extraídos dos metadados das entidades informadas.
	 */
	public CcpQueryExecutorDecorator selectFrom(CcpEntity... entities) {
		String[] resourcesNames = new String[entities.length];
		int k = 0;
		for (CcpEntity entity : entities) {
			CcpEntityMetaData entityDetails = entity.getEntityMetaData();
			resourcesNames[k++] = entityDetails.entityName;
		}
		return new CcpQueryExecutorDecorator(this, resourcesNames);
	}

	/**
	 * Define o ID de scroll para continuar uma iteração paginada.
	 */
	public CcpQueryOptions setScrollId(String scrollId) {
		CcpQueryOptions clone = super.putProperty("scroll_id", scrollId);
		return clone;
	}

	/**
	 * Define o número máximo de documentos retornados.
	 */
	public CcpQueryOptions setSize(int size) {
		CcpQueryOptions clone = super.putProperty("size", size);
		return clone;
	}

	/**
	 * Define o tamanho máximo como 10.000 documentos.
	 */
	public CcpQueryOptions maxResults() {
		CcpQueryOptions clone = super.putProperty("size", 10000);
		return clone;
	}

	/**
	 * Define o tamanho como 0 (útil para consultas que retornam apenas metadados ou agregações).
	 */
	public CcpQueryOptions zeroResults() {
		CcpQueryOptions clone = super.putProperty("size", 0);
		return clone;
	}

	/**
	 * Define o offset (paginação por deslocamento) dos resultados.
	 */
	public CcpQueryOptions setFrom(int from) {
		CcpQueryOptions clone = super.putProperty("from", from);
		return clone;
	}

	/**
	 * Define o tempo de expiração do contexto de scroll (ex: "1m", "5m").
	 */
	public CcpQueryOptions setScrollTime(String scrollTime) {
		CcpQueryOptions clone = super.putProperty("scroll", scrollTime);
		return clone;
	}

	/**
	 * Adiciona uma cláusula match_all que retorna todos os documentos sem filtro.
	 */
	public CcpQueryOptions matchAll() {
		CcpQueryOptions clone = super.putProperty("query", CcpOtherConstants.EMPTY_JSON.put(JsonFieldNames.match_all, CcpOtherConstants.EMPTY_JSON.content).content);
		return clone;
	}

	/**
	 * Base abstrata para todos os operadores booleanos de query do Elasticsearch (must, should, filter, must_not, should_not).
	 * Gerencia a coleção de condições e fornece métodos genéricos para adicionar diferentes tipos de filtro.
	 */
	public static abstract class CcpQueryBooleanOperator extends CcpQueryComponent {
		enum JsonFieldNames implements CcpJsonFieldName {
			operator, boost, query
		}

		protected Set<Object> items = new LinkedHashSet<>();

		CcpQueryBooleanOperator(CcpQueryComponent parent, String name) {
			super(parent, name);
		}

		public <T extends CcpQueryBooleanOperator> T term(CcpJsonFieldName field, Object value) {
			T addCondition = this.addCondition(field.name(), value, "term");
			return addCondition;
		}

		public <T extends CcpQueryBooleanOperator> T terms(CcpJsonFieldName field, Object value) {
			T addCondition = this.addCondition(field.name(), value, "terms");
			return addCondition;
		}

		public <T extends CcpQueryBooleanOperator> T prefix(CcpEntityField field, Object value) {
			T addCondition = this.addCondition(field.name(), value, "prefix");
			return addCondition;
		}

		public <T extends CcpQueryBooleanOperator> T match(CcpJsonFieldName field, Object value) {
			T addCondition = this.addCondition(field.name(), value, "match");
			return addCondition;
		}

		public <T extends CcpQueryBooleanOperator> T matchPhrase(CcpEntityField field, Object value) {
			T addCondition = this.addCondition(field.name(), value, "match_phrase");
			return addCondition;
		}

		public <T extends CcpQueryBooleanOperator> T match(CcpEntityField field, Object value, double boost, String operator) {
			T addCondition = this.addCondition(field.name(), value, "match", boost, operator);
			return addCondition;
		}

		public <T extends CcpQueryBooleanOperator> T matchPhrase(CcpEntityField field, Object value, double boost) {
			T addCondition = this.addCondition(field.name(), value, "match_phrase", boost, "");
			return addCondition;
		}

		public <T extends CcpQueryBooleanOperator> T exists(String field) {
			T addCondition = this.addCondition("field", field, "exists");
			return addCondition;
		}

		@SuppressWarnings("unchecked")
		protected <T extends CcpQueryBooleanOperator> T addCondition(String field, Object value, String key) {
			CcpQueryBooleanOperator clone = this.copy();
			if (value == null) {
				return (T) clone;
			}
			Map<String, Object> map = CcpOtherConstants.EMPTY_JSON.put(new CcpFieldName(field), value).getContent();
			Map<String, Object> outerMap = CcpOtherConstants.EMPTY_JSON.put(new CcpFieldName(key), map).getContent();
			clone.items.addAll(this.items);
			clone.items.add(outerMap);
			return (T) clone;
		}

		@SuppressWarnings("unchecked")
		protected <T extends CcpQueryBooleanOperator> T addCondition(String field, Object value, String key, double boost, String operator) {
			CcpQueryBooleanOperator clone = this.copy();
			if (value == null) {
				return (T) clone;
			}
			CcpJsonRepresentation put = CcpOtherConstants.EMPTY_JSON.put(JsonFieldNames.query, value).put(JsonFieldNames.boost, boost);
			if (operator != null && false == operator.trim().isEmpty()) {
				put = put.put(JsonFieldNames.operator, operator);
			}
			Map<String, Object> map = put.getContent();
			Map<String, Object> mapField = CcpOtherConstants.EMPTY_JSON.put(new CcpFieldName(field), map).getContent();
			CcpOtherConstants.EMPTY_JSON.put(new CcpFieldName(key), mapField).getContent();
			Map<String, Object> outerMap = CcpOtherConstants.EMPTY_JSON.put(new CcpFieldName(key), mapField).getContent();
			clone.items.addAll(this.items);
			clone.items.add(outerMap);
			return (T) clone;
		}

		Object getValue() {
			return new ArrayList<>(this.items);
		}

		@SuppressWarnings("unchecked")
		<T extends CcpQueryComponent> T addChild(CcpQueryComponent child) {
			CcpQueryBooleanOperator copy = this.copy();
			copy.items.addAll(this.items);
			Object childValue = child.getValue();
			Map<String, Object> childContent = CcpOtherConstants.EMPTY_JSON.put(new CcpFieldName(child.name), childValue).getContent();
			copy.items.add(childContent);
			return (T) copy;
		}

		@SuppressWarnings("unchecked")
		protected <T extends CcpQueryComponent> T copy() {
			CcpQueryBooleanOperator instanceCopy = this.getInstanceCopy();
			instanceCopy.name = this.name;
			instanceCopy.parent = this.parent.copy();
			instanceCopy.items.addAll(this.items);
			return (T) instanceCopy;
		}

		public CcpQueryRange startRange() {
			return new CcpQueryRange(this);
		}

		public boolean hasChildreen() {
			return false == this.items.isEmpty();
		}
	}

	/**
	 * Representa o nó query dentro do builder fluent de queries do Elasticsearch.
	 * Serve como ponto de entrada para construir o bloco de consulta principal de uma requisição.
	 */
	public static final class CcpQuery extends CcpQueryComponent {
		CcpQuery(CcpQueryComponent parent) {
			super(parent, "query");
		}

		public CcpQueryBool startBool() {
			return new CcpQueryBool(this);
		}

		public CcpQueryOptions endQueryAndBackToRequest() {
			return this.parent.addChild(this);
		}

		@SuppressWarnings("unchecked")
		protected <T extends CcpQueryComponent> T getInstanceCopy() {
			return (T) new CcpQuery(this.parent);
		}
	}

	/**
	 * Representa o nó bool dentro de uma query booleana do Elasticsearch.
	 * É o ponto central de composição de filtros booleanos, permitindo criar cláusulas filter, must, should, must_not e should_not.
	 */
	public static final class CcpQueryBool extends CcpQueryComponent {
		CcpQueryBool(CcpQueryComponent parent) {
			super(parent, "bool");
		}

		public CcpQueryFilter startFilter() {
			return new CcpQueryFilter(this);
		}

		public CcpQueryMust startMust() {
			return new CcpQueryMust(this);
		}

		public CcpQueryShould startShould(int minimumShouldMatch) {
			return new CcpQueryShould(this).setMinimumShouldMatch(minimumShouldMatch);
		}

		public CcpQueryMustNot startMustNot() {
			return new CcpQueryMustNot(this);
		}

		public CcpQueryShouldNot startShouldNot() {
			return new CcpQueryShouldNot(this);
		}

		public CcpQueryShould endBoolAndBackToShould() {
			return this.parent.addChild(this);
		}

		public CcpQueryMust endBoolAndBackToMust() {
			return this.parent.addChild(this);
		}

		public CcpQueryShouldNot endBoolAndBackToShouldNot() {
			return this.parent.addChild(this);
		}

		public CcpQueryMustNot endBoolAndBackToMustNot() {
			return this.parent.addChild(this);
		}

		public CcpQueryFilter endBoolAndBackToFilter() {
			return this.parent.addChild(this);
		}

		public CcpQuery endBoolAndBackToQuery() {
			return this.parent.addChild(this);
		}

		@SuppressWarnings("unchecked")
		protected <T extends CcpQueryComponent> T getInstanceCopy() {
			return (T) new CcpQueryBool(this.parent);
		}
	}

	/**
	 * Representa o nó filter dentro de uma query booleana do Elasticsearch.
	 * Diferentemente de must, as condições de filtro não afetam a pontuação de relevância dos documentos.
	 */
	public static final class CcpQueryFilter extends CcpQueryComponent {
		CcpQueryFilter(CcpQueryComponent parent) {
			super(parent, "filter");
		}

		public CcpQueryBool startBool() {
			return new CcpQueryBool(this);
		}

		public CcpQueryBool endFilterAndBackToBool() {
			return this.parent.addChild(this);
		}

		@SuppressWarnings("unchecked")
		protected <T extends CcpQueryComponent> T getInstanceCopy() {
			return (T) new CcpQueryFilter(this.parent);
		}
	}

	/**
	 * Representa o nó must dentro de uma query booleana do Elasticsearch.
	 * As condições adicionadas aqui são obrigatórias e impactam a pontuação de relevância dos documentos retornados.
	 */
	public static final class CcpQueryMust extends CcpQueryBooleanOperator {
		CcpQueryMust(CcpQueryComponent parent) {
			super(parent, "must");
		}

		public CcpQueryBool endMustAndBackToBool() {
			return this.parent.addChild(this);
		}

		@SuppressWarnings("unchecked")
		public CcpQueryMust matchPhrase(CcpEntityField field, Object value) {
			return super.matchPhrase(field, value);
		}

		@SuppressWarnings("unchecked")
		public CcpQueryMust prefix(CcpEntityField field, Object value) {
			return super.prefix(field, value);
		}

		@SuppressWarnings("unchecked")
		public CcpQueryMust term(CcpJsonFieldName field, Object value) {
			return super.term(field, value);
		}

		@SuppressWarnings("unchecked")
		public CcpQueryMust terms(CcpJsonFieldName field, Object value) {
			return super.terms(field, value);
		}

		@SuppressWarnings("unchecked")
		protected <T extends CcpQueryComponent> T getInstanceCopy() {
			return (T) new CcpQueryMust(this.parent);
		}

		@SuppressWarnings("unchecked")
		public CcpQueryMust exists(String field) {
			return super.exists(field);
		}

		public CcpQueryBool startBool() {
			return new CcpQueryBool(this);
		}

		public CcpQueryMust match(CcpEntityField field, Object value) {
			return super.match(field, value);
		}
	}

	/**
	 * Representa o nó must_not dentro de uma query booleana do Elasticsearch.
	 * As condições aqui presentes excluem documentos que as satisfaçam.
	 */
	public static final class CcpQueryMustNot extends CcpQueryBooleanOperator {
		CcpQueryMustNot(CcpQueryComponent parent) {
			super(parent, "must_not");
		}

		public CcpQueryBool endMustNotAndBackToBool() {
			return this.parent.addChild(this);
		}

		@SuppressWarnings("unchecked")
		public CcpQueryMustNot prefix(CcpEntityField field, Object value) {
			return super.prefix(field, value);
		}

		@SuppressWarnings("unchecked")
		public CcpQueryMustNot matchPhrase(CcpEntityField field, Object value) {
			return super.matchPhrase(field, value);
		}

		public CcpQueryMustNot term(CcpEntityField field, Object value) {
			return super.term(field, value);
		}

		@SuppressWarnings("unchecked")
		protected <T extends CcpQueryComponent> T getInstanceCopy() {
			return (T) new CcpQueryMustNot(this.parent);
		}

		@SuppressWarnings("unchecked")
		public CcpQueryMustNot exists(String field) {
			return super.exists(field);
		}

		public CcpQueryBool startBool() {
			return new CcpQueryBool(this);
		}
	}

	/**
	 * Representa o nó should dentro de uma query booleana do Elasticsearch.
	 * As condições adicionadas aqui são opcionais e incrementam a pontuação de relevância dos documentos que as satisfaçam.
	 * Suporta o parâmetro minimum_should_match para exigir que pelo menos N condições sejam verdadeiras.
	 */
	public static final class CcpQueryShould extends CcpQueryBooleanOperator {
		enum JsonFieldNames implements CcpJsonFieldName {
			minimum_should_match
		}

		CcpQueryShould(CcpQueryComponent parent) {
			super(parent, "should");
		}

		@SuppressWarnings("unchecked")
		public CcpQueryShould prefix(CcpEntityField field, Object value) {
			return super.prefix(field, value);
		}

		CcpQueryShould setMinimumShouldMatch(int minimumShouldMatch) {
			CcpQueryShould copy = this.copy();
			copy.parent.json = copy.parent.json.put(JsonFieldNames.minimum_should_match, minimumShouldMatch);
			return copy;
		}

		public CcpQueryBool endShouldAndBackToBool() {
			CcpQueryComponent copy = this.parent.copy();
			CcpQueryBool addChild = copy.addChild(this);
			return addChild;
		}

		public CcpQueryShould matchPhrase2(CcpEntityField field, Object value) {
			return super.matchPhrase(field, value);
		}

		public CcpQueryShould match(CcpEntityField field, Object value) {
			return super.match(field, value);
		}

		public CcpQueryShould matchPhrase(String field, Object value, double boost) {
			CcpQueryShould addCondition = this.addCondition(field, value, "match_phrase", boost, "");
			return addCondition;
		}

		public CcpQueryShould match(String field, Object value, double boost, String operator) {
			CcpQueryShould addCondition = this.addCondition(field, value, "match", boost, operator);
			return addCondition;
		}

		public CcpQueryShould term(CcpEntityField field, Object value) {
			return super.term(field, value);
		}

		@SuppressWarnings("unchecked")
		protected <T extends CcpQueryComponent> T getInstanceCopy() {
			return (T) new CcpQueryShould(this.parent);
		}

		@SuppressWarnings("unchecked")
		public CcpQueryShould exists(String field) {
			return super.exists(field);
		}

		public CcpQueryBool startBool() {
			return new CcpQueryBool(this);
		}
	}

	/**
	 * Representa o nó should_not dentro de uma query booleana.
	 * As condições aqui são opcionais e penalizam a pontuação de documentos que as satisfaçam (semântica negativa opcional).
	 */
	public static final class CcpQueryShouldNot extends CcpQueryBooleanOperator {
		CcpQueryShouldNot(CcpQueryComponent parent) {
			super(parent, "should_not");
		}

		@SuppressWarnings("unchecked")
		public CcpQueryShouldNot prefix(CcpEntityField field, Object value) {
			return super.prefix(field, value);
		}

		public CcpQueryBool endShouldNotAndBackToBool() {
			CcpQueryComponent copy = this.parent.copy();
			CcpQueryBool addChild = copy.addChild(this);
			return addChild;
		}

		@SuppressWarnings("unchecked")
		protected <T extends CcpQueryComponent> T getInstanceCopy() {
			return (T) new CcpQueryShouldNot(this.parent);
		}

		@SuppressWarnings("unchecked")
		public CcpQueryShouldNot matchPhrase(CcpEntityField field, Object value) {
			return super.matchPhrase(field, value);
		}

		public CcpQueryShouldNot term(CcpEntityField field, Object value) {
			return super.term(field, value);
		}

		@SuppressWarnings("unchecked")
		public CcpQueryShouldNot exists(String field) {
			return super.exists(field);
		}

		public CcpQueryBool startBool() {
			return new CcpQueryBool(this);
		}
	}

	/**
	 * Componente de query simplificada que estende {@code CcpQueryBooleanOperator}. Permite construir consultas
	 * com cláusulas {@code term}, {@code terms}, {@code match}, {@code matchPhrase}, {@code prefix} e {@code exists}
	 * de forma fluente, retornando ao {@code CcpQueryOptions} pai ao chamar {@code endSimplifiedQueryAndBackToRequest()}.
	 */
	public static final class CcpQuerySimplifiedQuery extends CcpQueryBooleanOperator {
		CcpQuerySimplifiedQuery(CcpQueryComponent parent) {
			super(parent, "query");
		}

		public CcpQuerySimplifiedQuery terms(CcpEntityField field, Object value) {
			return super.terms(field, value);
		}

		@SuppressWarnings("unchecked")
		public CcpQuerySimplifiedQuery prefix(CcpEntityField field, Object value) {
			return super.prefix(field, value);
		}

		public CcpQueryOptions endSimplifiedQueryAndBackToRequest() {
			return this.parent.addChild(this);
		}

		@SuppressWarnings("unchecked")
		protected <T extends CcpQueryComponent> T getInstanceCopy() {
			return (T) new CcpQuerySimplifiedQuery(this.parent);
		}

		Object getValue() {
			return this.json.content;
		}

		@SuppressWarnings("unchecked")
		protected CcpQuerySimplifiedQuery copy() {
			CcpQuerySimplifiedQuery instanceCopy = this.getInstanceCopy();
			instanceCopy.name = this.name;
			instanceCopy.parent = this.parent.copy();
			instanceCopy.json = this.json.copy();
			return instanceCopy;
		}

		@SuppressWarnings("unchecked")
		CcpQuerySimplifiedQuery addChild(CcpQueryComponent child) {
			CcpQuerySimplifiedQuery instanceCopy = this.copy();
			Object value = child.getValue();
			instanceCopy.json = instanceCopy.json.put(new CcpFieldName(child.name), value);
			return instanceCopy;
		}

		@SuppressWarnings("unchecked")
		public CcpQuerySimplifiedQuery matchPhrase(CcpEntityField field, Object value) {
			return super.matchPhrase(field, value);
		}

		public CcpQuerySimplifiedQuery term(CcpEntityField field, Object value) {
			return super.term(field, value);
		}

		@SuppressWarnings("unchecked")
		public CcpQuerySimplifiedQuery match(CcpJsonFieldName field, Object value) {
			return super.match(field, value);
		}

		@SuppressWarnings("unchecked")
		public CcpQuerySimplifiedQuery exists(String field) {
			return super.exists(field);
		}

		@SuppressWarnings("unchecked")
		protected CcpQuerySimplifiedQuery addCondition(String field, Object value, String key) {
			Map<String, Object> map = CcpOtherConstants.EMPTY_JSON.put(new CcpFieldName(field), value).getContent();
			Map<String, Object> outerMap = CcpOtherConstants.EMPTY_JSON.put(new CcpFieldName(key), map).getContent();
			CcpQuerySimplifiedQuery clone = this.copy();
			clone.json = new CcpJsonRepresentation(outerMap);
			return clone;
		}

		public boolean hasChildreen() {
			return false == this.json.content.isEmpty();
		}
	}

	/**
	 * Representa o nó aggs (agregações) no builder fluent de queries do Elasticsearch.
	 * Permite adicionar agregações métricas (min, max, avg, sum) e iniciar buckets (agrupamentos).
	 */
	public static final class CcpQueryAggregations extends CcpQueryComponent {
		enum JsonFieldNames implements CcpJsonFieldName {
			field
		}

		CcpQueryAggregations(CcpQueryComponent parent) {
			super(parent, "aggs");
		}

		public CcpQueryOptions endAggregationsAndBackToRequest() {
			return this.parent.addChild(this);
		}

		public BucketAggregation endAggregationsAndBackToBucket() {
			return this.parent.addChild(this);
		}

		public CcpQueryAggregations addMinAggregation(String aggregationName, CcpEntityField fieldName) {
			CcpQueryAggregations copy = this.createAggregation(aggregationName, fieldName, "min");
			return copy;
		}

		private CcpQueryAggregations createAggregation(String aggregationName, CcpEntityField fieldName, String key) {
			CcpQueryAggregations copy = this.copy();
			Map<String, Object> c1 = CcpOtherConstants.EMPTY_JSON.put(JsonFieldNames.field, fieldName).getContent();
			Map<String, Object> c2 = CcpOtherConstants.EMPTY_JSON.put(new CcpFieldName(key), c1).getContent();
			copy.json = copy.json.put(new CcpFieldName(aggregationName), c2);
			return copy;
		}

		public CcpQueryAggregations addMaxAggregation(String aggregationName, CcpEntityField fieldName) {
			CcpQueryAggregations copy = this.createAggregation(aggregationName, fieldName, "max");
			return copy;
		}

		public CcpQueryAggregations addAvgAggregation(String aggregationName, CcpEntityField fieldName) {
			CcpQueryAggregations copy = this.createAggregation(aggregationName, fieldName, "avg");
			return copy;
		}

		public BucketAggregation startBucket(String bucketName, CcpEntityField fieldName, long size) {
			BucketAggregation bucketAggregation = new BucketAggregation(this, bucketName, fieldName, size);
			return bucketAggregation;
		}

		@SuppressWarnings("unchecked")
		protected <T extends CcpQueryComponent> T getInstanceCopy() {
			return (T) new CcpQueryAggregations(this.parent);
		}

		public CcpQueryAggregations addSumAggregation(String aggregationName, CcpEntityField fieldName) {
			CcpQueryAggregations copy = this.createAggregation(aggregationName, fieldName, "sum");
			return copy;
		}
	}

	/**
	 * Representa um bucket de agregação do Elasticsearch (terms ou histogram) dentro do builder fluent de queries.
	 * Permite configurar um agrupamento por campo e tamanho, e encerrar voltando ao nó pai de agregações.
	 */
	public static final class BucketAggregation extends CcpQueryComponent {
		enum JsonFieldNames implements CcpJsonFieldName {
			field
		}

		private final CcpEntityField fieldName;
		private final long size;

		BucketAggregation(CcpQueryComponent parent, String name, CcpEntityField fieldName, long size) {
			super(parent, name);
			this.fieldName = fieldName;
			this.size = size;
		}

		public CcpQueryAggregations endTermsBuckedAndBackToAggregations() {
			CcpQueryAggregations addChild = this.getStatisRequest("size", "terms");
			return addChild;
		}

		public CcpQueryAggregations endHistogramBuckedAndBackToAggregations() {
			CcpQueryAggregations addChild = this.getStatisRequest("interval", "histogram");
			return addChild;
		}

		private CcpQueryAggregations getStatisRequest(String p1, String p2) {
			CcpQueryComponent copy = this.copy();
			Map<String, Object> content = CcpOtherConstants.EMPTY_JSON.put(JsonFieldNames.field, this.fieldName)
					.put(new CcpFieldName(p1), this.size).getContent();
			copy.json = copy.json.put(new CcpFieldName(p2), content);
			CcpQueryAggregations addChild = this.parent.addChild(copy);
			return addChild;
		}

		public CcpQueryAggregations startAggregations() {
			return new CcpQueryAggregations(this);
		}

		@SuppressWarnings("unchecked")
		protected <T extends CcpQueryComponent> T getInstanceCopy() {
			return (T) new BucketAggregation(this.parent, this.name, this.fieldName, this.size);
		}
	}

	/**
	 * Representa o nó range no builder de queries do Elasticsearch.
	 * Serve como contêiner para definições de intervalo de um ou mais campos, permitindo retornar ao contexto pai correto após a definição.
	 */
	public static class CcpQueryRange extends CcpQueryComponent {
		CcpQueryRange(CcpQueryComponent parent) {
			super(parent, "range");
		}

		@SuppressWarnings("unchecked")
		protected <T extends CcpQueryComponent> T getInstanceCopy() {
			return (T) new CcpQueryRange(this.parent);
		}

		public CcpQueryFieldRange startFieldRange(String fieldName) {
			return new CcpQueryFieldRange(this, fieldName);
		}

		public CcpQuerySimplifiedQuery endRangeAndBackToSimplifiedQuery() {
			return this.parent.addChild(this);
		}

		public CcpQueryShould endRangeAndBackToShould() {
			return this.parent.addChild(this);
		}

		public CcpQueryMust endRangeAndBackToMust() {
			return this.parent.addChild(this);
		}

		public CcpQueryShouldNot endRangeAndBackToShouldNot() {
			return this.parent.addChild(this);
		}

		public CcpQueryMustNot endRangeAndBackToMustNot() {
			return this.parent.addChild(this);
		}
	}

	/**
	 * Representa as condições de intervalo para um campo específico dentro de um bloco range do Elasticsearch.
	 * Permite encadear operadores de comparação (lt, lte, gt, gte) de forma fluent.
	 */
	public static class CcpQueryFieldRange extends CcpQueryComponent {
		CcpQueryFieldRange(CcpQueryComponent parent, String name) {
			super(parent, name);
		}

		@SuppressWarnings("unchecked")
		protected CcpQueryFieldRange getInstanceCopy() {
			return new CcpQueryFieldRange(this.parent, this.name);
		}

		private CcpQueryFieldRange putOperator(String operatorName, Object value) {
			CcpQueryFieldRange copy = this.copy();
			copy.json = copy.json.put(new CcpFieldName(operatorName), value);
			return copy;
		}

		public CcpQueryFieldRange lessThan(Object value) {
			return this.putOperator("lt", value);
		}

		public CcpQueryFieldRange lessThanEquals(Object value) {
			return this.putOperator("lte", value);
		}

		public CcpQueryFieldRange greaterThan(Object value) {
			return this.putOperator("gt", value);
		}

		public CcpQueryFieldRange greaterThanEquals(Object value) {
			return this.putOperator("gte", value);
		}

		public CcpQueryRange endFieldRangeAndBackToRange() {
			return this.parent.addChild(this);
		}
	}

	/**
	 * Decorator sobre CcpQueryExecutor que captura a query e os nomes de índices no construtor, simplificando as chamadas ao executor real.
	 * Cada método delega ao CcpQueryExecutor injetado via DI sem precisar que o chamador repasse esses parâmetros repetidamente.
	 */
	public static class CcpQueryExecutorDecorator {
		private final CcpQueryExecutor requestExecutor = CcpDependencyInjection.getDependency(CcpQueryExecutor.class);
		private final String[] resourcesNames;
		private final CcpQueryOptions elasticQuery;

		protected CcpQueryExecutorDecorator(CcpQueryOptions elasticQuery, String... resourcesNames) {
			this.resourcesNames = resourcesNames;
			this.elasticQuery = elasticQuery;
		}

		public CcpJsonRepresentation getResultAsPackage(String url, CcpHttpMethods method, int expectedStatus, String... array) {
			return this.requestExecutor.getResultAsPackage(url, method, expectedStatus, this.elasticQuery, this.resourcesNames, array);
		}

		public CcpJsonRepresentation getTermsStatis(String fieldName) {
			return this.requestExecutor.getTermsStatis(this.elasticQuery, this.resourcesNames, fieldName);
		}

		public CcpJsonRepresentation delete() {
			return this.requestExecutor.delete(this.elasticQuery, this.resourcesNames);
		}

		public CcpJsonRepresentation update(CcpJsonRepresentation newValues) {
			return this.requestExecutor.update(this.elasticQuery, this.resourcesNames, newValues);
		}

		public CcpQueryExecutorDecorator consumeQueryResult(String scrollTime, int size,
				Consumer<CcpJsonRepresentation> consumer, String... fields) {
			this.requestExecutor.consumeQueryResult(this.elasticQuery, this.resourcesNames, scrollTime, size, consumer, fields);
			return this;
		}

		public long total() {
			return this.requestExecutor.total(this.elasticQuery, this.resourcesNames);
		}

		public List<CcpJsonRepresentation> getResultAsList(String... fieldsToSearch) {
			return this.requestExecutor.getResultAsList(this.elasticQuery, this.resourcesNames, fieldsToSearch);
		}

		public CcpJsonRepresentation getResultAsMap(String field) {
			return this.requestExecutor.getResultAsMap(this.elasticQuery, this.resourcesNames, field);
		}

		public CcpJsonRepresentation getMap(String field) {
			return this.requestExecutor.getMap(this.elasticQuery, this.resourcesNames, field);
		}

		public CcpJsonRepresentation getAggregations() {
			return requestExecutor.getAggregations(this.elasticQuery, this.resourcesNames);
		}
	}
}
