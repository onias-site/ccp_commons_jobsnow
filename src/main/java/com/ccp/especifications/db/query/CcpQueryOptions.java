package com.ccp.especifications.db.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.ccp.constants.CcpOtherConstants;
import com.ccp.decorators.CcpFieldName;
import com.ccp.decorators.CcpJsonFieldName;
import com.ccp.especifications.db.utils.entity.CcpEntity;
import com.ccp.especifications.db.utils.entity.decorators.engine.CcpEntityMetaData;
import com.ccp.decorators.CcpJsonRepresentation;

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
		CcpQuery ccpQuery = new CcpQuery(this);
		return ccpQuery;
	}

	/**
	 * Inicia uma query simplificada sem o wrapper bool/filter.
	 */
	public CcpQuerySimplifiedQuery startSimplifiedQuery() {
		CcpQuerySimplifiedQuery ccpQuerySimplifiedQuery = new CcpQuerySimplifiedQuery(this);
		return ccpQuerySimplifiedQuery;
	}

	/**
	 * Inicia o bloco de agregações da requisição.
	 */
	public CcpQueryAggregations startAggregations() {
		CcpQueryAggregations ccpQueryAggregations = new CcpQueryAggregations(this);
		return ccpQueryAggregations;
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
		CcpFieldName ccpFieldName = new CcpFieldName(fieldName);
		CcpJsonRepresentation put = CcpOtherConstants.EMPTY_JSON.put(ccpFieldName, sortType);
		Map<String, Object> content = put.getContent();
		List<Object> asList = Arrays.asList(content);
		boolean containsAllFields = copy.json.containsAllFields(JsonFieldNames.sort);
		if (containsAllFields) {
			List<Object> sort = copy.json.getAsObjectList(JsonFieldNames.sort);
			asList = new ArrayList<>(sort);
			asList.add(content);
		}
		copy.json = copy.json.put(JsonFieldNames.sort, asList);
		return copy;
	}

	@SuppressWarnings("unchecked")
	protected <T extends CcpQueryComponent> T getInstanceCopy() {
		CcpQueryOptions ccpQueryOptions = new CcpQueryOptions();
		T t = (T) ccpQueryOptions;
		return t;
	}

	/**
	 * Associa esta query a índices pelo nome (string) e retorna um executor pronto para uso.
	 */
	public CcpQueryExecutorDecorator selectFrom(String... resourcesNames) {
		CcpQueryExecutorDecorator ccpQueryExecutorDecorator = new CcpQueryExecutorDecorator(this, resourcesNames);
		return ccpQueryExecutorDecorator;
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
		CcpQueryExecutorDecorator ccpQueryExecutorDecorator2 = new CcpQueryExecutorDecorator(this, resourcesNames);
		return ccpQueryExecutorDecorator2;
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
		CcpJsonRepresentation put2 = CcpOtherConstants.EMPTY_JSON.put(JsonFieldNames.match_all, CcpOtherConstants.EMPTY_JSON.content);
		CcpQueryOptions clone = super.putProperty("query", put2.content);
		return clone;
	}

	/**
	 * Base abstrata para todos os operadores booleanos de query do Elasticsearch (must, should, filter, must_not, should_not).
	 * Gerencia a coleção de condições e fornece métodos genéricos para adicionar diferentes tipos de filtro.
	 */


	/**
	 * Representa o nó query dentro do builder fluent de queries do Elasticsearch.
	 * Serve como ponto de entrada para construir o bloco de consulta principal de uma requisição.
	 */


	/**
	 * Representa o nó bool dentro de uma query booleana do Elasticsearch.
	 * É o ponto central de composição de filtros booleanos, permitindo criar cláusulas filter, must, should, must_not e should_not.
	 */


	/**
	 * Representa o nó filter dentro de uma query booleana do Elasticsearch.
	 * Diferentemente de must, as condições de filtro não afetam a pontuação de relevância dos documentos.
	 */


	/**
	 * Representa o nó must dentro de uma query booleana do Elasticsearch.
	 * As condições adicionadas aqui são obrigatórias e impactam a pontuação de relevância dos documentos retornados.
	 */


	/**
	 * Representa o nó must_not dentro de uma query booleana do Elasticsearch.
	 * As condições aqui presentes excluem documentos que as satisfaçam.
	 */


	/**
	 * Representa o nó should dentro de uma query booleana do Elasticsearch.
	 * As condições adicionadas aqui são opcionais e incrementam a pontuação de relevância dos documentos que as satisfaçam.
	 * Suporta o parâmetro minimum_should_match para exigir que pelo menos N condições sejam verdadeiras.
	 */


	/**
	 * Representa o nó should_not dentro de uma query booleana.
	 * As condições aqui são opcionais e penalizam a pontuação de documentos que as satisfaçam (semântica negativa opcional).
	 */


	/**
	 * Componente de query simplificada que estende {@code CcpQueryBooleanOperator}. Permite construir consultas
	 * com cláusulas {@code term}, {@code terms}, {@code match}, {@code matchPhrase}, {@code prefix} e {@code exists}
	 * de forma fluente, retornando ao {@code CcpQueryOptions} pai ao chamar {@code endSimplifiedQueryAndBackToRequest()}.
	 */


	/**
	 * Representa o nó aggs (agregações) no builder fluent de queries do Elasticsearch.
	 * Permite adicionar agregações métricas (min, max, avg, sum) e iniciar buckets (agrupamentos).
	 */


	/**
	 * Representa um bucket de agregação do Elasticsearch (terms ou histogram) dentro do builder fluent de queries.
	 * Permite configurar um agrupamento por campo e tamanho, e encerrar voltando ao nó pai de agregações.
	 */


	/**
	 * Representa o nó range no builder de queries do Elasticsearch.
	 * Serve como contêiner para definições de intervalo de um ou mais campos, permitindo retornar ao contexto pai correto após a definição.
	 */


	/**
	 * Representa as condições de intervalo para um campo específico dentro de um bloco range do Elasticsearch.
	 * Permite encadear operadores de comparação (lt, lte, gt, gte) de forma fluent.
	 */


	/**
	 * Decorator sobre CcpQueryExecutor que captura a query e os nomes de índices no construtor, simplificando as chamadas ao executor real.
	 * Cada método delega ao CcpQueryExecutor injetado via DI sem precisar que o chamador repasse esses parâmetros repetidamente.
	 */

}
