package com.ccp.especifications.db.utils.entity.decorators.engine;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.ccp.business.CcpBusiness;
import com.ccp.constants.CcpOtherConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.db.bulk.CcpBulkEntityOperationType;
import com.ccp.especifications.db.bulk.CcpBulkItem;
import com.ccp.especifications.db.bulk.CcpErrorBulkEntityRecordNotFound;
import com.ccp.especifications.db.crud.CcpCrud;
import com.ccp.especifications.db.crud.CcpSelectUnionAll;
import com.ccp.especifications.db.crud.CcpUnionAllExecutor;
import com.ccp.especifications.db.utils.entity.CcpEntity;
import com.ccp.especifications.db.utils.entity.CcpEntityOperationType;
import com.ccp.especifications.db.utils.entity.decorators.annotations.CcpEntityTwin;
import com.ccp.especifications.db.utils.entity.fields.CcpEntityField;
import java.util.stream.Stream;

/**
 * Contém todos os metadados de uma entidade: nome do índice, classe configuradora, campos, chave
 * primária e campos atualizáveis. Fornece operações utilitárias para cálculo de ID, extração de
 * campos, busca e montagem de itens bulk. Instâncias são criadas e usadas internamente por
 * {@code CcpEntityFactory} e {@code DefaultImplementationEntity}.
 */
public final class CcpEntityMetaData { 

	public final List<String> onlyUpdatableFields;
	public final Class<?>  configurationClass; 
	public final List<String> primaryKeyNames;
	public final CcpEntityField[] allFields;
	public final CcpEntity entity;
	public final String entityName;
	
	CcpEntityMetaData(Class<?> configurationClass, Function<Class<?>, String> entityNameProducer){
		
		this.configurationClass = configurationClass;
				
		this.allFields = CcpEntityFactory.getFields(configurationClass);
		
		this.entityName = entityNameProducer.apply(configurationClass);
		Stream<CcpEntityField> stream = Arrays.asList(this.allFields).stream();
		var filter = stream.filter(field -> field.primaryKey);
		var filterMap = filter.map(field -> field.name());

		this.primaryKeyNames = filterMap.collect(Collectors.toList());
		Stream<CcpEntityField> stream2 = Arrays.asList(this.allFields).stream();
		var filter2 = stream2
				.filter(field -> false == field.primaryKey);
				var filter3 = filter2
				.filter(field -> field.updatable);
				var filter3Map = filter3
				
				.map(field -> field.name());

				this.onlyUpdatableFields = filter3Map
				.collect(Collectors.toList());
		
		this.entity = null;
	}

	CcpEntityMetaData(Class<?> configurationClass, List<String> primaryKeyNames, List<String> onlyUpdatableFields, CcpEntityField[] allFields,	String entityName, CcpEntity entity) {
		this.onlyUpdatableFields = onlyUpdatableFields;
		this.configurationClass = configurationClass;
		this.primaryKeyNames = primaryKeyNames;
		this.entityName = entityName;
		this.allFields = allFields;
		this.entity = entity;
	}

	boolean isTwinEntity() {
		
		CcpEntityTwin annotation = this.configurationClass.getAnnotation(CcpEntityTwin.class);
		boolean annotationIgual = annotation == null;

		if(annotationIgual) {
			return false;
		}
		
		String twinEntityName = annotation.twinEntityName();
		boolean equals = this.entityName.equals(twinEntityName);
		return equals;
	}
	
	
	CcpEntityMetaData associateEntity() {
		boolean twinEntity = this.isTwinEntity();
		if(twinEntity) {
			CcpEntity twin = CcpEntityFactory.getEntity(this.configurationClass, x -> x.getAnnotation(CcpEntityTwin.class).twinEntityName());
			CcpEntityMetaData ccpEntityMetaData = new CcpEntityMetaData(this.configurationClass, this.primaryKeyNames, this.onlyUpdatableFields, this.allFields, this.entityName, twin);
			return ccpEntityMetaData;
		}
		
		Field field = this.configurationClass.getDeclaredField("ENTITY");
		Object object = field.get(null);
		CcpEntity entity = (CcpEntity) object;
		CcpEntityMetaData ccpEntityMetaData2 = new CcpEntityMetaData(this.configurationClass, this.primaryKeyNames, this.onlyUpdatableFields, this.allFields, this.entityName, entity);
		return ccpEntityMetaData2;
		
	}

	/** Retorna um {@code CcpBusiness} que executa a operação informada sobre a entidade destes metadados. */
	public CcpBusiness getOperationCallback(CcpEntityOperationType operation){
		return json -> operation.execute(this.entity, json);
	}

	/** Retorna um JSON contendo apenas os campos atualizáveis (não chave primária) presentes em {@code json}. */
	public CcpJsonRepresentation getOnlyUpdatableFields(CcpJsonRepresentation json) {
		CcpJsonRepresentation jsonPiece = json.getJsonPiece(this.onlyUpdatableFields);
		return jsonPiece;
	}

	private CcpJsonRepresentation getPrimaryKeyValues(CcpJsonRepresentation json) {
		boolean containsAllFields = json.containsAllFields(this.primaryKeyNames);
	
		boolean primaryKeyMissing = false == containsAllFields;
		
		if(primaryKeyMissing) {
			CcpErrorEntityPrimaryKeyIsMissing ccpErrorEntityPrimaryKeyIsMissing = new CcpErrorEntityPrimaryKeyIsMissing(this.entity, json);
			throw ccpErrorEntityPrimaryKeyIsMissing;
		}
		
		CcpJsonRepresentation jsonPiece = json.getJsonPiece(this.primaryKeyNames);
		return jsonPiece;
	}

	/** Retorna os valores dos campos de chave primária ordenados alfabeticamente pelo nome do campo. */
	public ArrayList<Object> getSortedPrimaryKeyValues(CcpJsonRepresentation json) {

		CcpJsonRepresentation primaryKeyValues = this.getPrimaryKeyValues(json);
		
		TreeMap<String, Object> treeMap = new TreeMap<>(primaryKeyValues.content);
		Collection<Object> values2 = treeMap.values();
		ArrayList<Object> onlyPrimaryKeys = new ArrayList<>(values2);
		return onlyPrimaryKeys;
	}

	/** Retorna um JSON contendo apenas os campos declarados nos metadados desta entidade. */
	public CcpJsonRepresentation getOnlyExistingFields(CcpJsonRepresentation json) {
		CcpJsonRepresentation subMap = json.getJsonPiece(this.allFields);
		return subMap;
	}
	
	/** Retorna os nomes dos índices de todas as entidades associadas (incluindo twin). */
	public String[] getEntitiesToSelect() {
		List<CcpEntity> associatedEntities = this.entity.getAssociatedEntities();
		Stream<CcpEntity> stream3 = associatedEntities.stream();
		var stream3Map = stream3.map(x -> x.getEntityMetaData().entityName);
		List<String> collect = stream3Map.collect(Collectors.toList());
		int collectSize = collect.size();
		String[] array = collect.toArray(new String[collectSize]);
		return array;
	}
	
	/**
	 * Busca o documento pelo ID calculado; se não encontrado, executa {@code ifNotFound} e retorna
	 * seu resultado.
	 */
	public CcpJsonRepresentation getOneByIdOrHandleItIfThisIdWasNotFound(CcpJsonRepresentation json, CcpBusiness ifNotFound) {
		try {
			CcpCrud crud = CcpDependencyInjection.getDependency(CcpCrud.class);
			String calculateId = this.entity.calculateId(json);
			CcpJsonRepresentation oneById = crud.getOneById(this.entityName, calculateId);
			return oneById;
			
		} catch (CcpErrorBulkEntityRecordNotFound e) {
			CcpJsonRepresentation execute = ifNotFound.execute(json);
			return execute;
		}
	}

	/** Retorna {@code true} se a entidade não possui campos atualizáveis (somente leitura efetiva). */
	public boolean isNotAnUpdatableEntity() {
		boolean empty = this.onlyUpdatableFields.isEmpty();
		return empty;
	}

	/** Busca múltiplos documentos por seus IDs e retorna os resultados agrupados por esta entidade. */
	public CcpJsonRepresentation getMultipleByIds(Collection<CcpJsonRepresentation> asList) {
		boolean hasNoIdsToSearch = asList.isEmpty();

		if (hasNoIdsToSearch) {
			return CcpOtherConstants.EMPTY_JSON; 
		}
 
		CcpCrud crud = CcpDependencyInjection.getDependency(CcpCrud.class);

		CcpUnionAllExecutor unionAllExecutor = crud.getUnionAllExecutor();

		CcpSelectUnionAll unionAll = unionAllExecutor.unionAll(asList, this.entity);
		CcpJsonRepresentation innerJson = unionAll.condensed.getInnerJson(this.entity);
		return innerJson;
	}
	
	public CcpJsonRepresentation getPrimaryKeyValues(Supplier<CcpJsonRepresentation> supplier) {
		
		CcpJsonRepresentation json = supplier.get();
		
		CcpJsonRepresentation primaryKeyValues = this.getPrimaryKeyValues(json);
		
		return primaryKeyValues;
	}

	public String name() {
		return this.entityName;
	}
	
	public String toString() {
		return this.entityName;
	}


	public CcpBulkItem toCreateBulkItem(CcpJsonRepresentation json) {
		String id = this.entity.calculateId(json);
		CcpBulkItem response = new CcpBulkItem(json, CcpBulkEntityOperationType.create, this.entity, id);
		return response;
	}
	public CcpBulkItem toUpdateBulkItem(CcpJsonRepresentation json) {
		String id = this.entity.calculateId(json);
		CcpBulkItem response = new CcpBulkItem(json, CcpBulkEntityOperationType.update, this.entity, id);
		return response;
	}
	
	
	public CcpBulkItem toDeleteBulkItem(CcpJsonRepresentation json) {
		String id = this.entity.calculateId(json);
		CcpBulkItem response = new CcpBulkItem(json, CcpBulkEntityOperationType.delete, this.entity, id);
		return response;
	}

	
}
