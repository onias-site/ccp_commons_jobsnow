package com.ccp.especifications.db.query;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import com.ccp.constantes.CcpOtherConstants;
import com.ccp.decorators.CcpFieldName;
import com.ccp.decorators.CcpJsonRepresentation.CcpJsonFieldName;
import com.ccp.especifications.db.utils.entity.CcpEntity;
import com.ccp.especifications.db.utils.entity.decorators.engine.CcpEntityMetaData;

/**
 * Nó raiz do builder fluent de queries do Elasticsearch.
 * Ponto de entrada único para iniciar a construção de uma query, configurar paginação, ordenação e executar a consulta nos índices desejados.
 * Possui uma instância singleton INSTANCE para uso como ponto de partida.
 */
public class CcpQueryOptions extends CcpQueryComponent{
	enum JsonFieldNames implements CcpJsonFieldName{
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
		if(copy.json.containsAllFields(JsonFieldNames.sort)) {
			List<Object> sort = copy.json.getAsObjectList(JsonFieldNames.sort);
			asList = new ArrayList<>(sort);
			asList.add(content);
		}
		copy.json = copy.json.put(JsonFieldNames.sort, asList);
		return copy;
	}
	@SuppressWarnings("unchecked")
	protected <T extends CcpQueryComponent> T getInstanceCopy() {
		return (T)new CcpQueryOptions();
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

	
}
