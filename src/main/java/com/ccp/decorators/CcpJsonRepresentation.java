package com.ccp.decorators;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.TreeMap;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;
import java.util.stream.Collectors;

import com.ccp.aop.CcpAllowNullParameter;
import com.ccp.business.CcpBusiness;
import com.ccp.constants.CcpOtherConstants;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.json.CcpJsonHandler;
import com.ccp.hash.CcpHashAlgorithm;
import java.util.stream.Stream; 

/**
 * Tipo central do framework jobsnow. Representa um documento JSON como um mapa imutável {@code Map<String, Object>}
 * e é o único tipo de dado que flui entre todos os componentes de negócio. Oferece uma API fluente abrangente para
 * leitura, escrita, transformação, comparação, navegação em profundidade e validação condicional de campos.
 */
public class CcpJsonRepresentation  {
	/**
	 * Campos padrão usados para serializar detalhes de exceções: causa, mensagem, stack trace, tipo, hash do stack trace e stack trace completo.
	 */
	public static enum Fields implements CcpJsonFieldName{
		cause, message, stackTrace, type, stackTraceHash, completeStackTrace
	}
	/**
	 * Contrato de identificadores de campo. Implementado por enums, lambdas ou classes; garante que nomes de campo
	 * sejam referenciados de forma tipada (sem strings literais espalhadas pelo código).
	 * O método padrão {@code getValue()} chama {@code name()}, suportando enums diretamente.
	 */

	
	/**
	 * Cria uma nova instância a partir do conteúdo de outro JSON (cópia de conteúdo).
	 * @param json o JSON de origem
	 */
	public CcpJsonRepresentation redoJson(CcpJsonRepresentation json) {
		CcpJsonRepresentation redo = new CcpJsonRepresentation(json.content);
		return redo;
	}
	
	public final Map<String, Object> content;
	
	protected CcpJsonRepresentation() {
		this.content = new HashMap<>();
	}

	/**
	 * Fábrica pública para JSON vazio.
	 */
	public static CcpJsonRepresentation getEmptyJson() {
		CcpJsonRepresentation ccpJsonRepresentation = new CcpJsonRepresentation();
		return ccpJsonRepresentation;
	}

	/**
	 * Lê o stream e constrói o JSON; aceita conteúdo JSON ou formato {@code Properties}.
	 * @param is o stream de entrada
	 */
	public CcpJsonRepresentation(InputStream is) {

		this.content = new HashMap<>();
		String result = this.extractJson(is);
		CcpJsonHandler handler = CcpDependencyInjection.getDependency(CcpJsonHandler.class);

		boolean validJson = handler.isValidJson(result);
		
		if(validJson) {
			CcpJsonHandler json = CcpDependencyInjection.getDependency(CcpJsonHandler.class);
			Map<String, Object> map = json.fromJson(result);
			this.content.putAll(map);
			return;
		}

		Properties props = new Properties();
		
		byte[] bytes = result.getBytes();
		ByteArrayInputStream inStream = new ByteArrayInputStream(bytes);
		try {
			props.load(inStream);
		} catch (IOException e) {
			CcpErrorJsonPropertiesUnreadable ccpErrorJsonPropertiesUnreadable = new CcpErrorJsonPropertiesUnreadable(result, e);
			throw ccpErrorJsonPropertiesUnreadable;
		}
		
		Set<Object> keySet = props.keySet();
		for (Object key : keySet) {
			Object value = props.get(key);
			String valorMais = "" + key;
			this.content.put(valorMais, value);
		}
	}

	private String extractJson(InputStream is) {
		InputStreamReader in = new InputStreamReader(is);
		BufferedReader bufferedReader = new BufferedReader(in);
		Stream<String> lines = bufferedReader.lines();
		var joining = Collectors.joining("\n");
		String result = lines.collect(joining);
		return result;
	}
	
	/**
	 * Serializa os detalhes de uma exceção (mensagem, stack trace, causa) em JSON.
	 * @param e a exceção a serializar
	 */
	@CcpAllowNullParameter
	public CcpJsonRepresentation(Throwable e) {
		this(getErrorDetails(e).content);
	}

	/**
	 * Desserializa uma string JSON; lança {@code CcpErrorJsonInvalid} se inválida.
	 * @param json a string JSON a desserializar
	 */
	public CcpJsonRepresentation(String json) {
		this(getMap(json));
	}
 
	private static Map<String, Object> getMap(String json) {
		CcpJsonHandler handler = CcpDependencyInjection.getDependency(CcpJsonHandler.class);
		boolean validJson2 = handler.isValidJson(json);
		boolean invalidJson = false ==  validJson2;
		if(invalidJson) {
			CcpErrorJsonInvalid ccpErrorJsonInvalid = new CcpErrorJsonInvalid(json);
			throw ccpErrorJsonInvalid;
		}
		Map<String, Object> fromJson = handler.fromJson(json);
		return fromJson;

	}
	
	/**
	 * Cria a partir de um mapa existente;.
	 * @param content o mapa de campos e valores
	 */
	public CcpJsonRepresentation(Map<String, Object> content) {
		
		LinkedHashMap<String, Object> linkedHashMap = new LinkedHashMap<String, Object>();
		Set<String> keySet = content.keySet();
		for (String key : keySet) {
			Object value = content.get(key);
			if(value instanceof Class clazz) {
				value = clazz.toString();
			}
			linkedHashMap.put(key, value);
		}
		
		this.content = Collections.unmodifiableMap(linkedHashMap);
	}

	@CcpAllowNullParameter
	private static CcpJsonRepresentation getErrorDetails(Throwable e) {

		CcpJsonRepresentation jr = CcpOtherConstants.EMPTY_JSON;
		boolean eIgual = e == null;

		if(eIgual) {
			return jr; 
		}
		
		Throwable cause = e.getCause();
		String message = e.getMessage();
		boolean messageIgual = message == null;
		if(messageIgual) {
			message = "";
		}
		StackTraceElement[] st = e.getStackTrace();
		List<String> stackTrace = new ArrayList<>();
		for (StackTraceElement ste : st) {
			String stackTraceLine = getStackTraceLine(ste);
			stackTrace.add(stackTraceLine); 
		}
		Object causeDetails = getCauseDetails(cause, st);
		List<StackTraceElement> completeStackTrace2 = getCompleteStackTrace(e);
		Stream<StackTraceElement> stream2 = completeStackTrace2.stream();
		var stream2Map = stream2.map(x -> x.toString());
		var completeStackTrace = stream2Map.collect(Collectors.toList());
		var put2 = jr.put(Fields.completeStackTrace, completeStackTrace);
		var eClass = e.getClass();
		String eClassName = eClass.getName();
		var put3 = put2.put(Fields.type, eClassName);
		var put4 = put3.put(Fields.stackTrace, stackTrace);
		var put5 = put4.put(Fields.message, message);
		jr = put5.put(Fields.cause, causeDetails);
		return jr;
	}

	@CcpAllowNullParameter
	private static List<StackTraceElement> getCompleteStackTrace(Throwable e){
		boolean eIgual2 = e == null;
		if(eIgual2) {
			ArrayList<StackTraceElement> arrayList2 = new ArrayList<StackTraceElement>();
			return arrayList2;
		}
		StackTraceElement[] stackTrace = e.getStackTrace();
		List<StackTraceElement> asList = Arrays.asList(stackTrace);
		Throwable cause = e.getCause();
		List<StackTraceElement> stackTraceList = getCompleteStackTrace(cause);
		var arrayList = new ArrayList<>(stackTraceList);
		arrayList.addAll(asList);
		return arrayList;
		
	}
	
	@CcpAllowNullParameter
	private static Object getCauseDetails(Throwable cause, StackTraceElement[] st) {
		
		boolean hasCause = cause != null;
		
		if(hasCause) {
			CcpJsonRepresentation errorDetails = getErrorDetails(cause);
			return errorDetails;
		}
		return ""; 
	}

	private static String getStackTraceLine(StackTraceElement ste) {
		int lineNumber = ste.getLineNumber();
		String methodName = ste.getMethodName();
		String fileName = ste.getFileName();
		boolean fileNameIgual = fileName == null;
		if(fileNameIgual) {
			return "-";
		}
		String fileNameReplace = fileName.replace(".java", "");
		String fileNameReplaceMais = fileNameReplace + ".";
		String fileNameReplaceMaisMais = fileNameReplaceMais + methodName;
		String fileNameReplaceMaisMaisMais = fileNameReplaceMaisMais + ":";
		String key = fileNameReplaceMaisMaisMais + lineNumber;
		return key;
	}

	@SuppressWarnings("unchecked")
	public <T> T getAsEnum(CcpJsonFieldName field, Class<T> clazz){
		try {
			
			String asString = this.getAsString(field);
			Method met = clazz.getDeclaredMethod("valueOf", String.class);
			Object invoke = met.invoke(null, asString);
			T t = (T)invoke;
			return t;
		} catch (Exception e) {
			String value = field.getValue();
			Object object = this.content.get(value);
			CcpErrorJsonInvalidFieldFormat ccpErrorJsonInvalidFieldFormat = new CcpErrorJsonInvalidFieldFormat(object, value, "enum", this);
			throw ccpErrorJsonInvalidFieldFormat;
		}
	}
	
	
	public <T> T getAsEnum(CcpJsonFieldName field, Class<T> clazz, T defaultValue){
		String asString = this.getAsString(field);
		String asStringTrim = asString.trim();

		boolean hasNoEnum = asStringTrim.isEmpty();
		
		if(hasNoEnum) {
			return defaultValue;
		}
		T asEnum = this.getAsEnum(field, clazz);
		return asEnum;
	}
	
	public Long getAsLongNumber(CcpJsonFieldName field) {
		String fieldValue = field.getValue();
		Long asLongNumber = this.getAsLongNumber(fieldValue);
		return asLongNumber;
	}
	
	private Long getAsLongNumber(String field) {
		
		Object object = this.content.get(field);
		boolean objectIgual = object == null;

		if(objectIgual) {
			CcpErrorJsonInvalidFieldFormat ccpErrorJsonInvalidFieldFormat2 = new CcpErrorJsonInvalidFieldFormat("", field, "long", this);
			throw ccpErrorJsonInvalidFieldFormat2;
		}
		try {
			String valorMais2 = "" + object;
			Double valueOf = Double.valueOf(valorMais2);
			long longValue = valueOf.longValue();
			return longValue;
		} catch (Exception e) {
			CcpErrorJsonInvalidFieldFormat ccpErrorJsonInvalidFieldFormat3 = new CcpErrorJsonInvalidFieldFormat(object, field, "long", this);
			throw ccpErrorJsonInvalidFieldFormat3;
		}
	}

	public Integer getAsIntegerNumber(CcpJsonFieldName field) {
		String fieldValue2 = field.getValue();
		Integer asIntegerNumber = this.getAsIntegerNumber(fieldValue2);
		return asIntegerNumber;
	}
	
	private Integer getAsIntegerNumber(String field) {
		Object object = this.content.get(field);
		boolean objectIgual2 = object == null;
		if(objectIgual2) {
			CcpErrorJsonInvalidFieldFormat ccpErrorJsonInvalidFieldFormat4 = new CcpErrorJsonInvalidFieldFormat("", field, "integer", this);
			throw ccpErrorJsonInvalidFieldFormat4;
		}
		try {
			String valorMais3 = "" + object;
			Double valueOf2 = Double.valueOf(valorMais3);
			int intValue = valueOf2.intValue();
			return intValue;
		} catch (Exception e) {
			CcpErrorJsonInvalidFieldFormat ccpErrorJsonInvalidFieldFormat5 = new CcpErrorJsonInvalidFieldFormat(object, field, "integer", this);
			throw ccpErrorJsonInvalidFieldFormat5;
		}
	}
	
	public Supplier<CcpJsonRepresentation> getJsonSupplier(){
		Supplier<CcpJsonRepresentation> supplier = () -> this;
		return supplier;
	}
	
	public boolean getAsBoolean(CcpJsonFieldName field) {
		String fieldValue3 = field.getValue();
		boolean asBoolean = this.getAsBoolean(fieldValue3);
		return asBoolean;
	}
	
	private boolean getAsBoolean(String field) {
		String asString = this.getAsString(field);
		String toLowerCase = asString.toLowerCase();
		Boolean valueOf3 = Boolean.valueOf(toLowerCase);
		return valueOf3;
	}

	public Double getAsDoubleNumber(CcpJsonFieldName field) {
		String fieldValue4 = field.getValue();
		Double asDoubleNumber = this.getAsDoubleNumber(fieldValue4);
		return asDoubleNumber;
	}
	
	private Double getAsDoubleNumber(String field) {
		Object object = this.content.get(field);
		boolean objectIgual3 = object == null;
		if(objectIgual3) {
			CcpErrorJsonInvalidFieldFormat ccpErrorJsonInvalidFieldFormat6 = new CcpErrorJsonInvalidFieldFormat("", field, "double", this);
			throw ccpErrorJsonInvalidFieldFormat6;
		}
		try {
			String valorMais4 = "" + object;
			Double valueOf4 = Double.valueOf(valorMais4);
			return valueOf4;
		} catch (Exception e) {
			CcpErrorJsonInvalidFieldFormat ccpErrorJsonInvalidFieldFormat7 = new CcpErrorJsonInvalidFieldFormat(object, field, "double", this);
			throw ccpErrorJsonInvalidFieldFormat7;
		}
	}
	
	public CcpTextDecorator getAsTextDecorator(CcpJsonFieldName field) {
		String fieldValue5 = field.getValue();
		CcpTextDecorator asTextDecorator = this.getAsTextDecorator(fieldValue5);
		return asTextDecorator;
	}
	
	private CcpTextDecorator getAsTextDecorator(String field) {
		String asString = this.getAsString(field);
		CcpStringDecorator ccpStringDecorator = new CcpStringDecorator(asString);
		CcpTextDecorator text = ccpStringDecorator.text();
		return text;
	}

	public CcpStringDecorator getAsStringDecorator(CcpJsonFieldName field) {
		String fieldValue6 = field.getValue();
		CcpStringDecorator asStringDecorator = this.getAsStringDecorator(fieldValue6);
		return asStringDecorator;
	}
	
	private CcpStringDecorator getAsStringDecorator(String field) {
		String asString = this.getAsString(field);
		CcpStringDecorator decorator = new CcpStringDecorator(asString);
		return decorator;
	}
	
	/**
	 * Executa {@code business} somente se NENHUM dos campos especificados estiver presente.
	 * @param business a lógica a executar
	 * @param fields os campos a verificar
	 */
	public CcpJsonRepresentation whenFieldsAreNotFound(CcpBusiness business, CcpJsonFieldName... fields) {
		boolean anyFieldIsPresent = this.containsAnyFields(fields);
		if(anyFieldIsPresent) {
			return this;
		}
		
		CcpJsonRepresentation apply = business.execute(this);
		return apply;
	
	}

	/**
	 * Executa {@code business} se PELO MENOS UM dos campos estiver presente.
	 * @param business a lógica a executar
	 * @param fields os campos a verificar
	 */
	public CcpJsonRepresentation whenAnyFieldsAreFound(CcpBusiness business, CcpJsonFieldName... fields) {
		boolean containsAnyFields2 = this.containsAnyFields(fields);
	
		boolean anyFieldIsNotPresent = false == containsAnyFields2;
		
		if(anyFieldIsNotPresent) {
			return this;
		}
		
		CcpJsonRepresentation apply = business.execute(this);
		return apply;
	}

	/**
	 * Executa {@code business} somente se TODOS os campos estiverem presentes.
	 * @param business a lógica a executar
	 * @param fields os campos a verificar
	 */
	public CcpJsonRepresentation whenAllFieldsAreFound(CcpBusiness business, CcpJsonFieldName... fields) {
		boolean containsAllFields2 = this.containsAllFields(fields);
		boolean allFieldIsNotPresent = false == containsAllFields2;
		
		if(allFieldIsNotPresent) {
			return this;
		}
		
		CcpJsonRepresentation apply = business.execute(this);
		return apply;
	}
	
	/**
	 * Retorna o valor do campo como string. Retorna {@code ""} se ausente ou nulo; serializa Maps e Collections corretamente.
	 * @param field o campo a ler
	 */
	public String getAsString(CcpJsonFieldName field) {
		String fieldValue7 = field.getValue();
		String asString = this.getAsString(fieldValue7);
		return asString;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private String getAsString(String field) {
 
		Object object = this.content.get(field);
		boolean containsKey2 = this.content.containsKey(field);

		boolean thisKeyIsNotPresent = false == containsKey2;
		if(thisKeyIsNotPresent) {
			return ""; 
		}
		boolean objectIgual4 = object == null;

		if(objectIgual4) {
			return "";
		}
		
		if(object instanceof Map map) {
			CcpJsonRepresentation json = new CcpJsonRepresentation(map);
			String toString = json.toString();
			return toString;
		}

		if(object instanceof CcpJsonRepresentation json) {
			String toString2 = json.toString();
			return toString2;
		}
		
		if(object instanceof Collection<?> col) {
			boolean colEmpty = col.isEmpty();
		
			if(colEmpty) {
				String toString3 = col.toString();
				return toString3;
			}
			var stream3 = col.stream();
			var stream3Map = stream3.map(x -> x instanceof Map ? new CcpJsonRepresentation((Map)x) : x);

			List<Object> collect = stream3Map.collect(Collectors.toList());
			var get = collect.get(0);
			boolean isCcpJsonRepresentation = get instanceof CcpJsonRepresentation;

			
			if(isCcpJsonRepresentation) {
				String toString4 = collect.toString();
				return toString4;
			}
			CcpJsonHandler dependency = CcpDependencyInjection.getDependency(CcpJsonHandler.class);
			String json = dependency.toJson(col);
			return json;
		}
		String valorMais5 = "" + object;

		return (valorMais5);
	}
	public <T> T getOrDefault(CcpJsonFieldName field, Supplier<T> supplier) {
		T defaultValue = supplier.get();
		String fieldValue8 = field.getValue();
		T orDefault = this.getOrDefault(fieldValue8, defaultValue);
		return orDefault;
	}

	
	@SuppressWarnings("unchecked")
	private <T> T getOrDefault(String field, T defaultValue) {
		Object object = this.content.get(field);
		boolean valorIgual = null == object;

		if(valorIgual) {
			return defaultValue;
		}
		T t2 = (T)object;

		return t2;
	}
	
	public CcpJsonRepresentation getJsonPiece(Collection<String> fields) {
		int size = fields.size();
		String[] array = fields.toArray(new String[size]);
		CcpJsonRepresentation jsonPiece = this.getJsonPiece(array);
		return jsonPiece;
	}	

	public CcpJsonRepresentation getJsonPiece(CcpJsonFieldName... fields) {
		String[] fields2 = this.getFields(fields);
		CcpJsonRepresentation jsonPiece = this.getJsonPiece(fields2);
		return jsonPiece;
	}
	
	private CcpJsonRepresentation getJsonPiece(String... fields) {
		Map<String, Object> subMap = new LinkedHashMap<>();
		
		for (String field : fields) {
			Object value = this.content.get(field);
			boolean valueIgual = value == null;
			if(valueIgual) {
				continue;
			}
			subMap.put(field, value);
		}
		CcpJsonRepresentation ccpJsonRepresentation2 = new CcpJsonRepresentation(subMap);

		return ccpJsonRepresentation2;
	}

	/**
	 * Serializa o JSON em formato compacto (sem formatação).
	 */
	public String asUgglyJson() {
		
		CcpJsonHandler json = CcpDependencyInjection.getDependency(CcpJsonHandler.class);
		TreeMap<String, Object> md = new TreeMap<>(this.content);
		String json2 = json.toJson(md);
		return json2;
		
	}

	/**
	 * Serializa com indentação e quebras de linha.
	 */
	public String asPrettyJson() {
		CcpJsonHandler json = CcpDependencyInjection.getDependency(CcpJsonHandler.class);
		String asPrettyJson = json.asPrettyJson(this.content);
		return asPrettyJson;
	}
	
	
	public String toString() {
		try {
			CcpJsonHandler json = CcpDependencyInjection.getDependency(CcpJsonHandler.class);
			String _json = json.asPrettyJson(new TreeMap<>(this.content));
			return _json;
			
		} catch (Exception e) {
			String toString5 = this.content.toString();
			return toString5;
		}
	}
	

	/**
	 * Retorna o conjunto de nomes de campos presentes.
	 */
	public Set<String> fieldSet(){
		Set<String> keySet = this.content.keySet();
		return keySet;
	}
	
	public CcpJsonRepresentation put(CcpJsonFieldName field, CcpDecorator<?> map) {
		String fieldValue9 = field.getValue();
		CcpJsonRepresentation put = this.put(fieldValue9, map);
		return put;
	}
	
	private CcpJsonRepresentation put(String field, CcpDecorator<?> map) {
		Object internalContent = map.getContent();
		CcpJsonRepresentation put = this.put(field, internalContent);
		return put;
	}
	
	public CcpJsonRepresentation put(CcpJsonFieldName field, Collection<CcpJsonRepresentation> list) {
		String fieldValue10 = field.getValue();
		CcpJsonRepresentation put = this.put(fieldValue10, list);
		return put;
	}
	
	private CcpJsonRepresentation put(String field, Collection<CcpJsonRepresentation> list) {
		Stream<CcpJsonRepresentation> stream4 = list.stream();
		var stream4Map = stream4.map(x -> x.content);
		List<Map<String, Object>> collect = stream4Map.collect(Collectors.toList());
		CcpJsonRepresentation put = this.put(field, collect);
		return put;
	}
	
	/**
	 * Aplica uma função extratora sobre este JSON e retorna o resultado.
	 * @param extractor a função extratora
	 */
	public <T> T extractInformationFromJson(Function<CcpJsonRepresentation, T> extractor) {
		T information = extractor.apply(this);
		return information;
	}
	
	/**
	 * Aplica uma sequência de transformadores em cadeia, passando o resultado de um para o próximo.
	 * @param transformers os transformadores a aplicar em sequência
	 */
	@SafeVarargs
	public final CcpJsonRepresentation getTransformedJson(CcpBusiness... transformers) {
		CcpJsonRepresentation transformedJson = this;
		for (CcpBusiness transformer : transformers) {
			transformedJson = transformer.execute(transformedJson);
		}
		return transformedJson;
	}

	
	/**
	 * Associa um {@code CcpBusiness} como valor de um campo (armazenamento de transformadores para uso posterior).
	 * @param field o campo onde o transformador será armazenado
	 * @param process o transformador a armazenar
	 */
	public CcpJsonRepresentation addJsonTransformer(CcpJsonFieldName field, CcpBusiness process) {
		String fieldValue11 = field.getValue();
		CcpJsonRepresentation addJsonTransformer = this.addJsonTransformer(fieldValue11, process);
		return addJsonTransformer;
	}
	
	/**
	 * Associa um {@code CcpBusiness} como valor de um campo inteiro (índice).
	 * @param field o índice do campo
	 * @param process o transformador a armazenar
	 */
	public CcpJsonRepresentation addJsonTransformer(Integer field, CcpBusiness process) {
		String valorMais6 = "" + field;
		CcpJsonRepresentation addJsonTransformer = this.addJsonTransformer(valorMais6, process);
		return addJsonTransformer;
	}
	private CcpJsonRepresentation addJsonTransformer(String field, CcpBusiness process) {
		CcpJsonRepresentation put = this.put(field, process);
		return put;
	}
	
	/**
	 * Define o mesmo valor em múltiplos campos de uma vez.
	 * @param value o valor a definir
	 * @param fields os campos a preencher
	 */
	public CcpJsonRepresentation putSameValueInManyFields(Object value, CcpJsonFieldName... fields) {
		String[] fields2 = this.getFields(fields);
		CcpJsonRepresentation putSameValueInManyFields = this.putSameValueInManyFields(value, fields2);
		return putSameValueInManyFields;
	}
	
	private CcpJsonRepresentation putSameValueInManyFields(Object value, String... fields) {
		CcpJsonRepresentation json = this;
		
		for (String field : fields) {
			json = json.put(field, value);
		}
		
		return json;
	}
	
	/**
	 * Retorna uma nova instância com o campo adicionado/substituído (imutável).
	 * @param field o campo a adicionar ou substituir
	 * @param value o valor do campo
	 */
	public CcpJsonRepresentation put(CcpJsonFieldName field, Object value) {
		String fieldValue12 = field.getValue();
		CcpJsonRepresentation put = this.put(fieldValue12, value);
		return put;
	}

	/**
	 * Adiciona um JSON aninhado como valor do campo.
	 * @param field o campo onde o JSON aninhado será armazenado
	 * @param value o JSON aninhado
	 */
	public CcpJsonRepresentation put(CcpJsonFieldName field, CcpJsonRepresentation value) {
		String fieldValue13 = field.getValue();
		CcpJsonRepresentation put = this.put(fieldValue13, value.content);
		return put;
	}
	
	/**
	 * Usa o {@code getValue()} do próprio enum como valor do campo (conveniente para campos auto-descritivos).
	 * @param field o campo cujo valor será o seu próprio nome
	 */
	public CcpJsonRepresentation put(CcpJsonFieldName field) {
		CcpJsonRepresentation put = this.put(field, field);
		return put;
	}
	
	private CcpJsonRepresentation put(String field, Object value) {
		Map<String, Object> content = new LinkedHashMap<>();
		content.putAll(this.content);
		content.put(field, value);
		CcpJsonRepresentation json = new CcpJsonRepresentation(content);
		return json;
	}  

	/**
	 * Copia o valor de um campo para um ou mais outros campos.
	 * @param fieldToCopy o campo de origem
	 * @param fieldsToPaste os campos de destino
	 */
	public CcpJsonRepresentation duplicateValueFromField(CcpJsonFieldName fieldToCopy, CcpJsonFieldName... fieldsToPaste) {
		String[] fields = this.getFields(fieldsToPaste);
		String fieldToCopyValue = fieldToCopy.getValue();
		CcpJsonRepresentation response = this.duplicateValueFromField(fieldToCopyValue, fields);
		return response;
	}	

	private CcpJsonRepresentation duplicateValueFromField(String fieldToCopy, String... fieldsToPaste) {
		boolean containsAllFields3 = this.containsAllFields(fieldToCopy);
		boolean inexistentField = false == containsAllFields3;

		if (inexistentField) {
			return this;
		}
		
		CcpJsonRepresentation newMap = this;
		
		for (String fieldToPaste : fieldsToPaste) {
			Object value = this.get(fieldToCopy);
			newMap = newMap.put(fieldToPaste, value);
		}
		
		return newMap;
	}
	
	/**
	 * Renomeia um campo (move o valor de {@code oldField} para {@code newField}).
	 * @param oldField o campo original
	 * @param newField o novo nome do campo
	 */
	public CcpJsonRepresentation renameField(CcpJsonFieldName oldField, CcpJsonFieldName newField) {
		String oldFieldValue = oldField.getValue();
		String newFieldValue = newField.getValue();
		CcpJsonRepresentation renameField = this.renameField(oldFieldValue, newFieldValue);
		return renameField;
	}
	
	private CcpJsonRepresentation renameField(String oldField, String newField) {
		Map<String, Object> content = new HashMap<>();
		content.putAll(this.content);
		Object value = content.remove(oldField);
		boolean valueIgual2 = value == null;
		if(valueIgual2) {
			CcpJsonRepresentation json = new CcpJsonRepresentation(content);
			return json;
		}
		
		content.put(newField, value);
		CcpJsonRepresentation json = new CcpJsonRepresentation(content);
		return json;
	}
	
	private CcpJsonRepresentation removeField(String field) {
		Map<String, Object> content = this.getContent();
		Map<String, Object> copy = new HashMap<>(content);
		copy.remove(field);
		CcpJsonRepresentation json = new CcpJsonRepresentation(copy);
		return json;
	}
	
	/**
	 * Retorna nova instância sem os campos especificados.
	 * @param fields os campos a remover
	 */
	public CcpJsonRepresentation removeFields(CcpJsonFieldName... fields) {
		String[] fields2 = this.getFields(fields);
		CcpJsonRepresentation removeFields = this.removeFields(fields2);
		return removeFields;
	}
	
	private CcpJsonRepresentation removeFields(String... fields) {
		CcpJsonRepresentation json = this;
		for (String field : fields) {
			json = json.removeField(field);
		}
		return json;
	}

	/**
	 * Retorna o mapa interno imutável.
	 */
	public Map<String, Object> getContent() {
		return this.content;
	}

	/**
	 * Cria uma cópia independente do JSON atual.
	 */
	public CcpJsonRepresentation copy() {
		var content3 = this.getContent();
		CcpJsonRepresentation json = new CcpJsonRepresentation(content3);
		return json;
	}
	
	/**
	 * Navega por um caminho de múltiplos campos aninhados e retorna o JSON encontrado.
	 * @param paths o caminho de campos aninhados
	 */
	public CcpJsonRepresentation getInnerJsonFromPath(CcpJsonFieldName...paths) {
		String[] fields = this.getFields(paths);
		CcpJsonRepresentation innerJsonFromPath = this.getInnerJsonFromPath(fields);
		return innerJsonFromPath;
	}
	
	private CcpJsonRepresentation getInnerJsonFromPath(String...paths) {
		try {
			Map<String, Object> map =  this.getValueFromPath(new HashMap<>(), paths);
			CcpJsonRepresentation json = new CcpJsonRepresentation(map);
			return json; 
		} catch (ClassCastException e) {
			CcpJsonRepresentation map =  this.getValueFromPath(CcpOtherConstants.EMPTY_JSON, paths);
			return map;
		}
	}

	/**
	 * Navega pelo caminho e retorna o valor tipado; retorna {@code defaultValue} se qualquer campo do caminho estiver ausente.
	 * @param defaultValue o valor padrão caso o caminho não exista
	 * @param paths o caminho de campos aninhados
	 */
	public <T>T getValueFromPath(T defaultValue, CcpJsonFieldName... paths){
		String[] fields = this.getFields(paths);
		T valueFromPath = this.getValueFromPath(defaultValue, fields);
		return valueFromPath;	
	}
	
	@SuppressWarnings("unchecked")
	public CcpJsonRepresentation getTransformedJsonExecutingIfAndElse(Predicate<CcpJsonRepresentation> condition, CcpBusiness conditionsMet, CcpBusiness conditionsDoNotMet) {
		CcpJsonRepresentation transformedJsonWhenAllConditionsMatch = getTransformedJsonWhenAllConditionsMatch(conditionsMet, conditionsDoNotMet, condition);
		return transformedJsonWhenAllConditionsMatch;
	}
	
	@SuppressWarnings("unchecked")
	public CcpJsonRepresentation getTransformedJsonWhenAllConditionsMatch(CcpBusiness conditionsMet, CcpBusiness conditionsDoNotMet, Predicate<CcpJsonRepresentation>... conditions) {
		for (Predicate<CcpJsonRepresentation> condition : conditions) {
			boolean contionMets = condition.test(this);
			if(contionMets) {
				continue;
			}
			CcpJsonRepresentation apply = conditionsDoNotMet.execute(this);
			return apply;
		}
		
		CcpJsonRepresentation apply = conditionsMet.execute(this);
		return apply;
	}
	
	public CcpJsonRepresentation getTransformedJsonConsideringIfAnyOfTheConditionsIsMet(CcpBusiness conditionsMet, CcpBusiness conditionsDoNotMet, @SuppressWarnings("unchecked") Predicate<CcpJsonRepresentation>... conditions) {
		for (Predicate<CcpJsonRepresentation> condition : conditions) {
			boolean test = condition.test(this);
			boolean contionDoesNotMet = false == test;
			if(contionDoesNotMet) {
				continue;
			}
			CcpJsonRepresentation apply = conditionsMet.execute(this);
			return apply;
		}
		
		CcpJsonRepresentation apply = conditionsDoNotMet.execute(this);
		return apply;
	}
	
	@SuppressWarnings("unchecked")
	private <T>T getValueFromPath(T defaultValue, String... paths){
		
		boolean pathIsMissing = paths.length == 0;
		
		if(pathIsMissing) {
			CcpErrorJsonPathIsMissing ccpErrorJsonPathIsMissing = new CcpErrorJsonPathIsMissing(this);
			throw ccpErrorJsonPathIsMissing;
		}
		
		CcpJsonRepresentation initial = this;
		
		int lastIndex = paths.length - 1;
		boolean lastFieldIsJson = false;
		for(int k = 0; k < paths.length; k++) {
			String path = paths[k];
			boolean containsAllFields4 = initial.containsAllFields(path);

			boolean notContainsAllFields = false == containsAllFields4;
			
			if(notContainsAllFields) {
				return defaultValue;
			}
			
			CcpTextDecorator asTextDecorator = initial.getAsTextDecorator(path);
			boolean validSingleJson = asTextDecorator.isValidSingleJson();
			lastFieldIsJson = validSingleJson;
			
			if(validSingleJson) {
				initial = initial.getInnerJson(path);
				continue;
			}
		}
		
		if(lastFieldIsJson) {
			T t3 = (T)initial;
			return t3;
		}
		
		String path = paths[lastIndex];
		T asObject = initial.getAsObject(path);
		return asObject;
	}


	public List<CcpJsonRepresentation> getInnerJsonListFromPath(CcpJsonFieldName...paths) {
		String[] fields = this.getFields(paths);
		List<CcpJsonRepresentation> innerJsonListFromPath = this.getInnerJsonListFromPath(fields);
		return innerJsonListFromPath;
	}
	
	@SuppressWarnings("unchecked")
	private List<CcpJsonRepresentation> getInnerJsonListFromPath(String...paths) {
		
		List<Object> valueFromPath = this.getValueFromPath(new ArrayList<>(), paths);
		
		boolean empty = valueFromPath.isEmpty();
		
		if(empty) {
			return new ArrayList<>(); 
		}
		
		List<CcpJsonRepresentation> response = new ArrayList<>();
		
		for (Object value : valueFromPath) {
			
			if(value instanceof Map map) {
				CcpJsonRepresentation json = new CcpJsonRepresentation(map);
				response.add(json);
				continue;
			}

			if(value instanceof CcpJsonRepresentation json) {
				response.add(json);
				continue;
			}
			
			if(value instanceof String string) {
				CcpJsonRepresentation json = new CcpJsonRepresentation(string);
				response.add(json);
				continue;
			}
			
			Class<? extends Object> class1 = value.getClass();
			CCpErrorJsonFieldIsNotValidJsonList cCpErrorJsonFieldIsNotValidJsonList = new CCpErrorJsonFieldIsNotValidJsonList(this, class1, paths);
			throw cCpErrorJsonFieldIsNotValidJsonList;
		}
		
		return response;
	}	

	public CcpJsonRepresentation getInnerJson(CcpJsonFieldName field) {
		String fieldValue14 = field.getValue();
		CcpJsonRepresentation innerJson = this.getInnerJson(fieldValue14);
		return innerJson;
	}
	
	@SuppressWarnings("unchecked")
	private CcpJsonRepresentation getInnerJson(String field) {

		Object object = this.content.get(field);
		
		if(object instanceof CcpJsonRepresentation json) {
			return json;
		}
		boolean isString = object instanceof String;

		if(isString) {
			String valorMais7 = "" + object;
			CcpJsonRepresentation json = new CcpJsonRepresentation(valorMais7);
			return json;
		}
		

		if(object instanceof Map map) {
			CcpJsonRepresentation json = new CcpJsonRepresentation(map);
			return json;
		}

		return CcpOtherConstants.EMPTY_JSON;
	}

	
	public List<CcpJsonRepresentation> getAsJsonList(CcpJsonFieldName field) {
		String fieldValue15 = field.getValue();
		List<CcpJsonRepresentation> asJsonList = this.getAsJsonList(fieldValue15);
		return asJsonList;
	}
	
	@SuppressWarnings("unchecked")
	private List<CcpJsonRepresentation> getAsJsonList(String field) {
		
		Object object = this.content.get(field);
		boolean objectIgual5 = object == null;
 	
		if(objectIgual5) {
			return new ArrayList<>();
		}   
		boolean isString2 = object instanceof String;

		if(isString2) {
			CcpJsonHandler jsonHandler = CcpDependencyInjection.getDependency(CcpJsonHandler.class);
			try {
				String toString6 = object.toString();
			
				boolean validJsonList = jsonHandler.isValidJsonList(toString6);
				
				List<Map<String, Object>> fromJson = new ArrayList<>();
				if(validJsonList) {
					String toString7 = object.toString();
					fromJson = jsonHandler.fromJson(toString7);
				}
				var stream5 = fromJson.stream();
				var stream5Map = stream5.map(json -> new CcpJsonRepresentation(json));
				List<CcpJsonRepresentation> collect = stream5Map.collect(Collectors.toList());
				return collect;
			} catch (ClassCastException e) {
				return new ArrayList<>(); 
			}
		}
		boolean isCollection = object instanceof Collection;
		boolean valorIgual2 = false == isCollection;

		if(valorIgual2) {
			return new ArrayList<>();
		}

		
		Collection<Object> list = (Collection<Object>) object;
		var stream6 = list.stream();
		var stream6Map = stream6.map(obj -> {
			
			if(obj instanceof CcpJsonRepresentation jsn) {
				return jsn;
			}
			Map<String, Object> mapStringObject = (Map<String, Object>) obj;

			CcpJsonRepresentation json = new CcpJsonRepresentation(mapStringObject);
			return json;
			});

			List<CcpJsonRepresentation> collect = stream6Map
				.collect(Collectors.toList());
		
		return collect;
	}

	public CcpCollectionDecorator getAsCollectionDecorator(String field){
		List<String> asStringList = this.getAsStringList(field);
		int asStringListSize = asStringList.size();
		Object[] array = asStringList.toArray(new String[asStringListSize]);
		CcpCollectionDecorator ccpCollectionDecorator = new CcpCollectionDecorator(array);
		return ccpCollectionDecorator;
	}
	
	public List<String> getAsStringList(CcpJsonFieldName... fields){
		String[] fields2 = this.getFields(fields);
		List<String> asStringList = this.getAsStringList(fields2);
		return asStringList;
	}
	
	public String[] getAsStringArray(CcpJsonFieldName... fields) {
		List<String> asStringList = this.getAsStringList(fields);
		int asStringListSize2 = asStringList.size();
		String[] array = asStringList.toArray(new String[asStringListSize2]);
		return array;
	}
	
	private List<String> getAsStringList(String... fields){
		for (String field : fields) {
			var asObjectList2 = this.getAsObjectList(field);
			var stream7 = asObjectList2.stream();
			var filter = stream7
					.filter(x -> x != null);
					var filterMap = filter
					.map(x -> x.toString());
					List<String> collect = filterMap.collect(Collectors.toList());
					boolean collectEmpty = collect.isEmpty();
					if(collectEmpty) {
				continue;
			}
			return collect;
		}
		return new ArrayList<>();
	}
	
	public List<Object> getAsObjectList(CcpJsonFieldName field) {
		String fieldValue16 = field.getValue();
		List<Object> asObjectList = this.getAsObjectList(fieldValue16);
		return asObjectList;
	}
	
	private List<Object> getAsObjectList(String field) {
		boolean containsAllFields5 = this.containsAllFields(field);
	
		boolean isNotPresent = false == containsAllFields5;
		
		if(isNotPresent) {
			return new ArrayList<>();
		}
		
		Object object = this.content.get(field);
		boolean isObject = object instanceof Object[];

		if(isObject) {
			Object[] array = this.getAsObject(field);
			List<Object> asList = Arrays.asList(array);
			return asList;
		}
		
		if(object instanceof Collection<?> list) {
			ArrayList<Object> arrayList3 = new ArrayList<Object>(list);
			return arrayList3;
		}
		String toString8 = object.toString();
		String toString8Trim = toString8.trim();

		boolean empty = toString8Trim.isEmpty();
		
		if(empty) {
			return new ArrayList<>();
		}
		
		CcpJsonHandler jsonHandler = CcpDependencyInjection.getDependency(CcpJsonHandler.class);
		
		try {
			String toString9 = object.toString();
			List<Object> fromJson = jsonHandler.fromJson(toString9);
			return fromJson;
		} catch (Exception e) {
			String toString10 = object.toString();
			return Arrays.asList(toString10);
		}
	}
	
	public CcpJsonRepresentation mergeWithAnotherJson(Map<String, Object> map) {
		Map<String, Object> content2 = this.getContent();
		Map<String, Object> content = new LinkedHashMap<>(content2);
		content.putAll(map);
		CcpJsonRepresentation mapDecorator = new CcpJsonRepresentation(content);
		return mapDecorator;
	}

	public CcpJsonRepresentation mergeWithAnotherJson(CcpJsonRepresentation json) {
		CcpJsonRepresentation mapDecorator = this.mergeWithAnotherJson(json.content);
		return mapDecorator;
	}
	
	
	public boolean containsField(CcpJsonFieldName field) {
		String fieldValue17 = field.getValue();
		boolean containsField = this.containsField(fieldValue17);
		return containsField;
	}

	private boolean containsField(String field) {

		Object object = this.content.get(field);
		
		boolean containsKey = object != null;
		return containsKey;
	}

	public boolean containsAllFields(Collection<String> fields) {
		String[] array = this.toArray(fields);
		boolean containsAllFields = this.containsAllFields(array);
		return containsAllFields;
	}

	public boolean containsAnyFields(Collection<String> fields) {
		String[] array = this.toArray(fields);
		boolean containsAnyFields = this.containsAnyFields(array);
		return containsAnyFields;
	}

	private String[] toArray(Collection<String> fields) {
		int size = fields.size();
		String[] a = new String[size];
		String[] array = fields.toArray(a);
		return array;
	}	

	public boolean containsAllFields(CcpJsonFieldName... fields) {
		String[] fields2 = this.getFields(fields);
		boolean containsAllFields = this.containsAllFields(fields2);
		return containsAllFields;
	}
	
	private boolean containsAllFields(String... fields) {
		boolean containsFields = this.containsFields(false, fields);
		return containsFields; 
	}
	
	public boolean containsAnyFields(CcpJsonFieldName... fields) {
		String[] fields2 = this.getFields(fields);
		boolean containsAnyFields = this.containsAnyFields(fields2);
		return containsAnyFields;
	}
	
	private boolean containsAnyFields(String... fields) {
		boolean containsFields = this.containsFields(true, fields);
		return containsFields;
	}

	private boolean containsFields(boolean assertion, String... fields) {
		for (String field : fields) {
			boolean containsField = this.containsField(field);
			boolean containsFieldIgual = containsField == assertion;
			if(containsFieldIgual) {
				return assertion;
			}
		}
		boolean valorIgual3 = false == assertion;
		if(valorIgual3) {
			return true;
		}
		return false;
	}
	
	public Object get(CcpJsonFieldName field) {
		String fieldValue18 = field.getValue();
		Object object = this.get(fieldValue18);
		return object;
	}
	
	private Object get(String field) {
		Object object = this.content.get(field);
		boolean valueIsAbsent = object == null;
		if(valueIsAbsent) {
			CcpErrorJsonFieldNotFound ccpErrorJsonFieldNotFound = new CcpErrorJsonFieldNotFound(field, this);
			throw ccpErrorJsonFieldNotFound;
		}
		return object;
	}

	public <T> T getAsObject(CcpJsonFieldName... fields) {
		String[] fields2 = this.getFields(fields);
		T asObject = this.getAsObject(fields2);
		return asObject;
	}
	
	@SuppressWarnings("unchecked")
	private <T> T getAsObject(String... fields) {
		for (String field : fields) {
			Object object = this.content.get(field);
			boolean objectIgual6 = object == null;
			if(objectIgual6) {
				continue;
			}
			T t4 = (T) object;
			return t4;
		}
		String toString11 = Arrays.asList(fields).toString();
		CcpErrorJsonFieldNotFound ccpErrorJsonFieldNotFound2 = new CcpErrorJsonFieldNotFound(toString11, this);
		throw ccpErrorJsonFieldNotFound2;
	}
	
	public boolean isEmpty() {
		boolean empty = this.content.isEmpty();
		return empty;
	}

	public CcpJsonRepresentation addToList(CcpJsonFieldName field, Object... values) {
		String fieldValue19 = field.getValue();
		CcpJsonRepresentation addToList = this.addToList(fieldValue19, values);
		return addToList;
	}

	private CcpJsonRepresentation addToList(String field, Object... values) {
		CcpJsonRepresentation result = this;
		for (Object value : values) {
			result = result.addToList(field, value);
		}
		return result;
	}
	
	private CcpJsonRepresentation addToList(String field, Object value) {
		List<Object> list = this.getAsObjectList(field);
		list = new ArrayList<>(list);
		list.add(value);
		CcpJsonRepresentation put = this.put(field, list);
		return put;
	}

	public CcpJsonRepresentation addToList(CcpJsonFieldName field, CcpJsonRepresentation value) {
		String fieldValue20 = field.getValue();
		CcpJsonRepresentation addToList = this.addToList(fieldValue20, value);
		return addToList;
	}
	
	private CcpJsonRepresentation addToList(String field, CcpJsonRepresentation value) {
		List<Object> list = this.getAsObjectList(field);
		list = new ArrayList<>(list);
		list.add(value.content);
		CcpJsonRepresentation put = this.put(field, list);
		return put;
	}
	
	public CcpJsonRepresentation addToItem(CcpJsonFieldName field, CcpJsonFieldName subField, Object value) {
		String fieldValue21 = field.getValue();
		String subFieldValue = subField.getValue();
		CcpJsonRepresentation addToItem = this.addToItem(fieldValue21, subFieldValue, value);
		return addToItem;
	}
	
	private CcpJsonRepresentation addToItem(String field, String subField, Object value) {
		CcpJsonRepresentation itemAsMap = this.getInnerJson(field);
		itemAsMap = itemAsMap.put(subField, value);
		
		CcpJsonRepresentation put = this.put(field, itemAsMap.content);
		return put;
	}

	public CcpJsonRepresentation addToItem(CcpJsonFieldName field, CcpJsonFieldName subField, CcpJsonRepresentation value) {
		String fieldValue22 = field.getValue();
		String subFieldValue2 = subField.getValue();
		CcpJsonRepresentation addToItem = this.addToItem(fieldValue22, subFieldValue2, value);
		return addToItem;
	}
	
	private CcpJsonRepresentation addToItem(String field, String subField, CcpJsonRepresentation value) {
		CcpJsonRepresentation itemAsMap = this.getInnerJson(field);
		itemAsMap = itemAsMap.put(subField, value.content);
		
		CcpJsonRepresentation put = this.put(field, itemAsMap.content);
		return put;
	}

	public CcpJsonRepresentation copyIfNotContains(CcpJsonFieldName fieldToCopy, CcpJsonFieldName fieldToPaste) {
		String fieldToCopyValue2 = fieldToCopy.getValue();
		String fieldToPasteValue = fieldToPaste.getValue();
		CcpJsonRepresentation copyIfNotContains = this.copyIfNotContains(fieldToCopyValue2, fieldToPasteValue);
		return copyIfNotContains;
	}
	
	private CcpJsonRepresentation copyIfNotContains(String fieldToCopy, String fieldToPaste) {

		boolean containsAllFields = this.containsAllFields(fieldToPaste);
		
		if(containsAllFields) {
			return this;
		}
	
		CcpJsonRepresentation duplicateValueFromField = this.duplicateValueFromField(fieldToCopy, fieldToPaste);
		
		return duplicateValueFromField;
	}		
	
	public CcpJsonRepresentation putIfNotContains(CcpJsonFieldName field, Object value) {
		String fieldValue23 = field.getValue();

		CcpJsonRepresentation putIfNotContains = this.putIfNotContains(fieldValue23, value);
		return putIfNotContains;
	}
	
	private CcpJsonRepresentation putIfNotContains(String field, Object value) {
		boolean containsAllFields = this.containsAllFields(field);
		
		if(containsAllFields) {
			return this;
		}
		
		CcpJsonRepresentation put = this.put(field, value);
		return put;
	}
	
	public CcpCollectionDecorator getAsArrayMetadata(CcpJsonFieldName field) {
		String fieldValue24 = field.getValue();
		CcpCollectionDecorator asArrayMetadata = this.getAsArrayMetadata(fieldValue24);
		return asArrayMetadata;
	}
	
	private CcpCollectionDecorator getAsArrayMetadata(String field) {
		CcpCollectionDecorator cccpCollectionDecorator = new CcpCollectionDecorator(this, field);
		return cccpCollectionDecorator;
	}
	
	public InputStream toInputStream() {
		String asUgglyJson = this.asUgglyJson();
		byte[] bytes = asUgglyJson.getBytes(StandardCharsets.UTF_8);
		InputStream stream = new ByteArrayInputStream(bytes);
		return stream;
	}
	
	public String getSha1Hash(CcpHashAlgorithm algorithm) {
		String asUgglyJson = this.asUgglyJson();
		CcpStringDecorator ccpStringDecorator2 = new CcpStringDecorator(asUgglyJson);
		CcpHashDecorator ccpStringDecorator2Hash = ccpStringDecorator2.hash();
		String hash = ccpStringDecorator2Hash.asString(algorithm);
		return hash;
	}

	public int hashCode() {
		String hash2 = this.getSha1Hash(CcpHashAlgorithm.SHA1);
		int hashCode = hash2.hashCode();
		return hashCode;
	}
	
	public boolean equals(Object obj) {
		
		if(obj instanceof CcpJsonRepresentation other) {
			String hash = other.getSha1Hash(CcpHashAlgorithm.SHA1);
			String hash2 = this.getSha1Hash(CcpHashAlgorithm.SHA1);
			boolean equals = hash.equals(hash2);
			return equals;
		}
		
		return false;
	}
	
	public CcpJsonRepresentation getTransformedJson(
			List<CcpBusiness> jsonTransformers) {
				int jsonTransformersSize = jsonTransformers.size();

				CcpBusiness[] array = jsonTransformers.toArray(new CcpBusiness[jsonTransformersSize]);
		CcpJsonRepresentation transformedJson = this.getTransformedJson(array);
		return transformedJson;
	}

	public boolean isInnerJson(CcpJsonFieldName fieldName) {
		String fieldNameValue = fieldName.getValue();
		boolean innerJson = this.isInnerJson(fieldNameValue);
		return innerJson;
	}
	
	private boolean isInnerJson(String fieldName) {
		CcpJsonHandler handler = CcpDependencyInjection.getDependency(CcpJsonHandler.class);
		String asString = this.getAsString(fieldName);
		String asStringTrim2 = asString.trim();
		boolean asStringTrim2Empty = asStringTrim2.isEmpty();
		if(asStringTrim2Empty) {
			return false;
		}
		boolean validJson = handler.isValidJson(asString);
		return validJson;
	}
	
	private String[] getFields(CcpJsonFieldName... enumItems) {
		int k = 0;
		String[] array = new String[enumItems.length];
		for (CcpJsonFieldName enum1 : enumItems) {
			array[k++] = enum1.getValue();
		}
		return array;
	}

	public CcpJsonRepresentation getInnerJsonFromPath(CcpJsonFieldName fieldName, String value) {
		String fieldNameValue2 = fieldName.getValue();
		CcpJsonRepresentation innerJsonFromPath = this.getInnerJsonFromPath(fieldNameValue2, value);
		return innerJsonFromPath;
	}
	
	public CcpJsonRepresentation removeEmptyValues() {
		Set<String> fieldSet2 = this.fieldSet();
		Stream<String> stream8 = fieldSet2
				.stream();
				var filter2 = stream8
				.filter(x -> false == this.getAsString(x).trim().isEmpty());
				Set<String> fieldSet = filter2
				.collect(Collectors.toSet())
				;

		CcpJsonRepresentation removeEmptyValues = this.getJsonPiece(fieldSet);
		
		return removeEmptyValues;
	}





	/**
	 * Exceção lançada quando o valor de um campo existe no JSON, mas não pode ser convertido para o tipo esperado
	 * (por exemplo, tentar ler {@code "abc"} como {@code long}).
	 */
	@SuppressWarnings("serial")
	public static class CcpErrorJsonInvalidFieldFormat extends RuntimeException {
		/**
		 * Monta a mensagem indicando o valor encontrado, o nome do campo, o tipo esperado e o JSON completo.
		 * @param value o valor encontrado no campo
		 * @param fieldName o nome do campo
		 * @param fieldType o tipo esperado
		 * @param json o JSON no momento do erro
		 */
		private CcpErrorJsonInvalidFieldFormat(Object value, String fieldName, String fieldType, CcpJsonRepresentation json) {
			super("The value '" + value + "' from the field '" + fieldName + " is not a '" + fieldType + "' in the following json: " + json);
		}
	}

	/**
	 * Exceção lançada quando o conteúdo lido de um {@code InputStream} não é um JSON válido e também não pode ser
	 * interpretado como um arquivo de {@code Properties}.
	 */
	@SuppressWarnings("serial")
	public static class CcpErrorJsonPropertiesUnreadable extends RuntimeException {
		/**
		 * Monta a mensagem com o conteúdo que falhou na leitura e encadeia a exceção original como causa.
		 * @param content o conteúdo lido do stream
		 * @param cause a exceção original de leitura
		 */
		private CcpErrorJsonPropertiesUnreadable(String content, Throwable cause) {
			super("The following content is neither a valid json nor a valid properties file: " + content, cause);
		}
	}

	
}
