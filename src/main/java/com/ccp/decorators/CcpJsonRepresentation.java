package com.ccp.decorators;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
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
import java.util.stream.Collectors;

import com.ccp.constantes.CcpOtherConstants;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.json.CcpJsonHandler;
import com.ccp.especifications.mensageria.receiver.CcpBusiness;
import com.ccp.utils.CcpHashAlgorithm;
import com.ccp.validations.CcpItIsTrueThatTheFollowingFields;
 
public final class CcpJsonRepresentation implements CcpDecorator<Map<String, Object>>  {

	public static interface CcpJsonFieldName{
		
		default String getValue() {
			return this.name();
		}
		
		String name();
	}
	
	public final Map<String, Object> content;
	
	protected CcpJsonRepresentation() {
		this.content = new HashMap<>();
	}
	
	public static CcpJsonRepresentation getEmptyJson() {
		return new CcpJsonRepresentation();
	}
	
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
		
		try {
			byte[] bytes = result.getBytes();
			ByteArrayInputStream inStream = new ByteArrayInputStream(bytes);
			props.load(inStream);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
		
		Set<Object> keySet = props.keySet();
		for (Object key : keySet) {
			Object value = props.get(key);
			this.content.put("" + key, value);
		}
	}

	private String extractJson(InputStream is) {
		InputStreamReader in = new InputStreamReader(is);
		String result = new BufferedReader(in).lines().collect(Collectors.joining("\n"));
		return result;
	}
	
	public CcpJsonRepresentation(Throwable e) {
		this(getErrorDetails(e).content);
	}

	public CcpJsonRepresentation(String json) {
		this(getMap(json));
	}
 
	static Map<String, Object> getMap(String json) {
		CcpJsonHandler handler = CcpDependencyInjection.getDependency(CcpJsonHandler.class);
		try {
			Map<String, Object> fromJson = handler.fromJson(json);
			return fromJson;
			
		} catch (Exception e) {
			throw new CcpErrorJsonInvalid(json , e);
		}
	}
	
	public CcpJsonRepresentation(Map<String, Object> content) {
		if(content == null) {
			throw new CcpErrorJsonNull();
		}
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

	private static CcpJsonRepresentation getErrorDetails(Throwable e) {

		CcpJsonRepresentation jr = CcpOtherConstants.EMPTY_JSON;
		
		if(e == null) {
			return jr; 
		}
		
		Throwable cause = e.getCause();
		String message = e.getMessage();
		StackTraceElement[] st = e.getStackTrace();
		List<String> stackTrace = new ArrayList<>();
		for (StackTraceElement ste : st) {
			String stackTraceLine = getStackTraceLine(ste);
			stackTrace.add(stackTraceLine); 
		}
		Object causeDetails = getCauseDetails(cause, st);
		jr = jr.put("type", e.getClass().getName()).put("stackTrace", stackTrace).put("message", message).put("cause", causeDetails);
		return jr;
	}

	private static Object getCauseDetails(Throwable cause, StackTraceElement[] st) {
		
		boolean hasCause = cause != null;
		
		if(hasCause) {
			CcpJsonRepresentation errorDetails = getErrorDetails(cause);
			return errorDetails;
		}
		
		int k = 0;
		List<String> stack = new ArrayList<>();
		for (StackTraceElement stackTraceElement : st) {
			String line = getStackTraceLine(stackTraceElement);
			stack.add(line);
			if(k++ >= 100) {
				break;
			}
		}
		return stack; 
	}

	private static String getStackTraceLine(StackTraceElement ste) {
		int lineNumber = ste.getLineNumber();
		String methodName = ste.getMethodName();
		String fileName = ste.getFileName();
		if(fileName == null) {
			return "";
		}
		String key = fileName.replace(".java", "") + "." + methodName + ":" + lineNumber+ "<BR>";
		return key;
	}

	public Long getAsLongNumber(CcpJsonFieldName field) {
		Long asLongNumber = this.getAsLongNumber(field.getValue());
		return asLongNumber;
	}
	
	private Long getAsLongNumber(String field) {
		
		Object object = this.content.get(field);
		try {
			return Double.valueOf("" + object).longValue();
		} catch (Exception e) {
			throw new CcpErrorJsonInvalidFieldFormat(object, field, "long", this);
		}
	}

	public Integer getAsIntegerNumber(CcpJsonFieldName field) {
		Integer asIntegerNumber = this.getAsIntegerNumber(field.getValue());
		return asIntegerNumber;
	}
	
	private Integer getAsIntegerNumber(String field) {
		Object object = this.content.get(field);
		try {
			return Double.valueOf("" + object).intValue();
		} catch (Exception e) {
			throw new CcpErrorJsonInvalidFieldFormat(object, field, "integer", this);
		}
	}
	
	public boolean getAsBoolean(CcpJsonFieldName field) {
		boolean asBoolean = this.getAsBoolean(field.getValue());
		return asBoolean;
	}
	
	private boolean getAsBoolean(String field) {
		String asString = this.getAsString(field);
		return Boolean.valueOf(asString.toLowerCase());
	}

	public Double getAsDoubleNumber(CcpJsonFieldName field) {
		Double asDoubleNumber = this.getAsDoubleNumber(field.getValue());
		return asDoubleNumber;
	}
	
	private Double getAsDoubleNumber(String field) {
		Object object = this.content.get(field);
		try {
			return Double.valueOf("" + object);
		} catch (Exception e) {
			throw new CcpErrorJsonInvalidFieldFormat(object, field, "double", this);
		}
	}
	
	public CcpJsonRepresentation putFilledTemplate(CcpJsonFieldName fieldToSearch, CcpJsonFieldName fieldToPut) {
		CcpJsonRepresentation putFilledTemplate = this.putFilledTemplate(fieldToSearch.getValue(), fieldToPut.getValue());
		return putFilledTemplate;
	}
	
	private CcpJsonRepresentation putFilledTemplate(String fieldToSearch, String fieldToPut) {
		
		String asString = this.getAsString(fieldToSearch);
		
		CcpTextDecorator ccpTextDecorator = new CcpTextDecorator(asString);
		
		String message = ccpTextDecorator.getMessage(this).content;
		 
		CcpJsonRepresentation put = this.put(fieldToPut, message);
		
		return put;
	}
	
	public CcpTextDecorator getAsTextDecorator(CcpJsonFieldName field) {
		CcpTextDecorator asTextDecorator = this.getAsTextDecorator(field.getValue());
		return asTextDecorator;
	}
	
	private CcpTextDecorator getAsTextDecorator(String field) {
		String asString = this.getAsString(field);
		CcpStringDecorator ccpStringDecorator = new CcpStringDecorator(asString);
		CcpTextDecorator text = ccpStringDecorator.text();
		return text;
	}

	public CcpStringDecorator getAsStringDecorator(CcpJsonFieldName field) {
		CcpStringDecorator asStringDecorator = this.getAsStringDecorator(field.getValue());
		return asStringDecorator;
	}
	
	private CcpStringDecorator getAsStringDecorator(String field) {
		String asString = this.getAsString(field);
		CcpStringDecorator decorator = new CcpStringDecorator(asString);
		return decorator;
	}
	
	public String getAsString(CcpJsonFieldName field) {
		String asString = this.getAsString(field.getValue());
		return asString;
	}
	
	@SuppressWarnings("unchecked")
	private String getAsString(String field) {

		Object object = this.content.get(field);
		
		boolean thisKeyIsNotPresent = this.content.containsKey(field) == false;
		if(thisKeyIsNotPresent) {
			return ""; 
		}

		if(object == null) {
			return "";
		}
		
		if(object instanceof Map map) {
			CcpJsonRepresentation json = new CcpJsonRepresentation(map);
			return json.toString();
		}

		if(object instanceof CcpJsonRepresentation json) {
			return json.toString();
		}
		return ("" + object);
	}

	public <T> T getOrDefault(CcpJsonFieldName field, T defaultValue) {
		T orDefault = this.getOrDefault(field.getValue(), defaultValue);
		return orDefault;
	}
	
	@SuppressWarnings("unchecked")
	private <T> T getOrDefault(String field, T defaultValue) {
		Object object = this.content.get(field);
		
		if(null == object) {
			return defaultValue;
		}
		
		return (T)object;
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
			if(value == null) {
				continue;
			}
			subMap.put(field, value);
		}
		
		return new CcpJsonRepresentation(subMap);
	}

	public String asUgglyJson() {
		CcpJsonHandler json = CcpDependencyInjection.getDependency(CcpJsonHandler.class);
		
		try {
			String json2 = json.toJson(this.content);
			return json2;
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

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
			return this.content.toString();
		}
	}
	

	public Set<String> fieldSet(){
		Set<String> keySet = this.content.keySet();
		return keySet;
	}
	
	public CcpJsonRepresentation put(CcpJsonFieldName field, CcpDecorator<?> map) {
		CcpJsonRepresentation put = this.put(field.getValue(), map);
		return put;
	}
	
	private CcpJsonRepresentation put(String field, CcpDecorator<?> map) {
		Object internalContent = map.getContent();
		CcpJsonRepresentation put = this.put(field, internalContent);
		return put;
	}
	
	public CcpJsonRepresentation put(CcpJsonFieldName field, Collection<CcpJsonRepresentation> list) {
		CcpJsonRepresentation put = this.put(field.getValue(), list);
		return put;
	}
	
	private CcpJsonRepresentation put(String field, Collection<CcpJsonRepresentation> list) {
		List<Map<String, Object>> collect = list.stream().map(x -> x.content).collect(Collectors.toList());
		CcpJsonRepresentation put = this.put(field, collect);
		return put;
	}
	
	public <T> T extractInformationFromJson(Function<CcpJsonRepresentation, T> extractor) {
		T information = extractor.apply(this);
		return information;
	}
	
	@SafeVarargs
	public final CcpJsonRepresentation getTransformedJson(CcpBusiness... transformers) {
		CcpJsonRepresentation transformedJson = this;
		for (CcpBusiness transformer : transformers) {
			transformedJson = transformer.apply(transformedJson);
		}
		return transformedJson;
	}

	public CcpJsonRepresentation getTransformedJsonIfFoundTheField(CcpJsonFieldName field, CcpBusiness... transformers) {
		CcpJsonRepresentation transformedJsonIfFoundTheField = this.getTransformedJsonIfFoundTheField(field.getValue(), transformers);
		return transformedJsonIfFoundTheField;
	}
	
	private CcpJsonRepresentation getTransformedJsonIfFoundTheField(String field, CcpBusiness... transformers) {

		boolean fieldNotFound = this.containsAllFields(field) == false;
		if(fieldNotFound) {
			return this;
		}
		
		CcpJsonRepresentation transformedJson = this;
		
		for (CcpBusiness transformer : transformers) {
			transformedJson = transformer.apply(transformedJson);
		}
		
		return transformedJson;
	}
	
	public CcpJsonRepresentation addJsonTransformer(CcpJsonFieldName field, CcpBusiness process) {
		CcpJsonRepresentation addJsonTransformer = this.addJsonTransformer(field.getValue(), process);
		return addJsonTransformer;
	}
	
	public CcpJsonRepresentation addJsonTransformer(Integer field, CcpBusiness process) {
		CcpJsonRepresentation addJsonTransformer = this.addJsonTransformer("" + field, process);
		return addJsonTransformer;
	}
	private CcpJsonRepresentation addJsonTransformer(String field, CcpBusiness process) {
		CcpJsonRepresentation put = this.put(field, process);
		return put;
	}
	
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
	
	public CcpJsonRepresentation put(CcpJsonFieldName field, Object value) {
		CcpJsonRepresentation put = this.put(field.getValue(), value);
		return put;
	}
	
	private CcpJsonRepresentation put(String field, Object value) {
		Map<String, Object> content = new LinkedHashMap<>();
		content.putAll(this.content);
		content.put(field, value);
		CcpJsonRepresentation json = new CcpJsonRepresentation(content);
		return json;
	}  

	public CcpJsonRepresentation duplicateValueFromField(CcpJsonFieldName fieldToCopy, CcpJsonFieldName... array) {
		String[] fields = this.getFields(array);
		CcpJsonRepresentation response = this.duplicateValueFromField(fieldToCopy.getValue(), fields);
		return response;
	}	

	private CcpJsonRepresentation duplicateValueFromField(String fieldToCopy, String... fieldsToPaste) {
		boolean inexistentField = this.containsAllFields(fieldToCopy) == false;

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
	
	public CcpJsonRepresentation renameField(CcpJsonFieldName oldField, CcpJsonFieldName newField) {
		CcpJsonRepresentation renameField = this.renameField(oldField.getValue(), newField.getValue());
		return renameField;
	}
	
	private CcpJsonRepresentation renameField(String oldField, String newField) {
		Map<String, Object> content = new HashMap<>();
		content.putAll(this.content);
		Object value = content.remove(oldField);
		if(value == null) {
			CcpJsonRepresentation json = new CcpJsonRepresentation(content);
			return json;
		}
		
		content.put(newField, value);
		CcpJsonRepresentation json = new CcpJsonRepresentation(content);
		return json;
	}

	public CcpJsonRepresentation removeField(CcpJsonFieldName field) {
		CcpJsonRepresentation removeField = this.removeField(field.getValue());
		return removeField;
	}
	
	private CcpJsonRepresentation removeField(String field) {
		Map<String, Object> content = this.getContent();
		Map<String, Object> copy = new HashMap<>(content);
		copy.remove(field);
		CcpJsonRepresentation json = new CcpJsonRepresentation(copy);
		return json;
	}
	
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

	public Map<String, Object> getContent() {
		return this.content;
	}
	
	public CcpJsonRepresentation copy() {
		CcpJsonRepresentation json = new CcpJsonRepresentation(this.getContent());
		return json;
	}
	
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

	public <T>T getValueFromPath(T defaultValue, CcpJsonFieldName... paths){
		String[] fields = this.getFields(paths);
		T valueFromPath = this.getValueFromPath(defaultValue, fields);
		return valueFromPath;	
	}
	
	@SuppressWarnings("unchecked")
	private <T>T getValueFromPath(T defaultValue, String... paths){
		
		boolean pathIsMissing = paths.length == 0;
		
		if(pathIsMissing) {
			throw new CcpErrorJsonPathIsMissing(this);
		}
		
		CcpJsonRepresentation initial = this;
		
		int lastIndex = paths.length - 1;
		boolean lastFieldIsJson = false;
		for(int k = 0; k < paths.length; k++) {
			String path = paths[k];
			
			boolean notContainsAllFields = initial.containsAllFields(path) == false;
			
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
			return (T)initial;
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
			throw new CCpErrorJsonFieldIsNotValidJsonList(this, class1, paths);
		}
		
		return response;
	}	

	public CcpJsonRepresentation getInnerJson(CcpJsonFieldName field) {
		CcpJsonRepresentation innerJson = this.getInnerJson(field.getValue());
		return innerJson;
	}
	
	@SuppressWarnings("unchecked")
	private CcpJsonRepresentation getInnerJson(String field) {

		Object object = this.content.get(field);
		
		if(object instanceof CcpJsonRepresentation json) {
			return json;
		}
		
		if(object instanceof String) {
			CcpJsonRepresentation json = new CcpJsonRepresentation("" + object);
			return json;
		}
		

		if(object instanceof Map map) {
			CcpJsonRepresentation json = new CcpJsonRepresentation(map);
			return json;
		}

		return CcpOtherConstants.EMPTY_JSON;
	}

	
	public List<CcpJsonRepresentation> getAsJsonList(CcpJsonFieldName field) {
		List<CcpJsonRepresentation> asJsonList = this.getAsJsonList(field.getValue());
		return asJsonList;
	}
	
	@SuppressWarnings("unchecked")
	private List<CcpJsonRepresentation> getAsJsonList(String field) {
		
		Object object = this.content.get(field);
		 
		if(object == null) {
			return new ArrayList<>();
		}   
		
		if(object instanceof String) {
			CcpJsonHandler jsonHandler = CcpDependencyInjection.getDependency(CcpJsonHandler.class);
			try {
				List<Map<String, Object>> fromJson = jsonHandler.fromJson(object.toString());
				List<CcpJsonRepresentation> collect = fromJson.stream().map(json -> new CcpJsonRepresentation(json)).collect(Collectors.toList());
				return collect;
			} catch (ClassCastException e) {
				return new ArrayList<>();
			}
		}

		if(object instanceof Collection == false) {
			return new ArrayList<>();
		}

		
		Collection<Object> list = (Collection<Object>) object;
		
		List<CcpJsonRepresentation> collect = list.stream().map(obj -> {
			
			if(obj instanceof CcpJsonRepresentation jsn) {
				return jsn;
			}
			
			CcpJsonRepresentation json = new CcpJsonRepresentation((Map<String, Object>) obj);
			return json;
		})
				.collect(Collectors.toList());
		
		return collect;
	}

	public CcpCollectionDecorator getAsCollectionDecorator(String field){
		List<String> asStringList = this.getAsStringList(field);
		Object[] array = asStringList.toArray(new String[asStringList.size()]);
		CcpCollectionDecorator ccpCollectionDecorator = new CcpCollectionDecorator(array);
		return ccpCollectionDecorator;
	}
	
	public List<String> getAsStringList(CcpJsonFieldName... fields){
		String[] fields2 = this.getFields(fields);
		List<String> asStringList = this.getAsStringList(fields2);
		return asStringList;
	}
	
	private List<String> getAsStringList(String... fields){
		for (String field : fields) {
			List<String> collect = this.getAsObjectList(field).stream()
					.filter(x -> x != null)
					.map(x -> x.toString()).collect(Collectors.toList());
			if(collect.isEmpty()) {
				continue;
			}
			return collect;
		}
		return new ArrayList<>();
	}
	
	public List<Object> getAsObjectList(CcpJsonFieldName field) {
		List<Object> asObjectList = this.getAsObjectList(field.getValue());
		return asObjectList;
	}
	
	private List<Object> getAsObjectList(String field) {
		
		boolean isNotPresent = this.containsAllFields(field) == false;
		
		if(isNotPresent) {
			return new ArrayList<>();
		}
		
		Object object = this.content.get(field);
		
		if(object == null) {
			return new ArrayList<>();
		}
		
		if(object instanceof Object[]) {
			Object[] array = this.getAsObject(field);
			List<Object> asList = Arrays.asList(array);
			return asList;
		}
		
		if(object instanceof Collection<?> list) {
			return new ArrayList<Object>(list);
		}
		
		CcpJsonHandler jsonHandler = CcpDependencyInjection.getDependency(CcpJsonHandler.class);
		try {
			List<Object> fromJson = jsonHandler.fromJson(object.toString());
			return fromJson;
		} catch (Exception e) {
			return Arrays.asList(object.toString());
		}
	}
	
	public CcpJsonRepresentation putAll(Map<String, Object> map) {
		Map<String, Object> content2 = this.getContent();
		Map<String, Object> content = new LinkedHashMap<>(content2);
		content.putAll(map);
		CcpJsonRepresentation mapDecorator = new CcpJsonRepresentation(content);
		return mapDecorator;
	}

	public CcpJsonRepresentation putAll(CcpJsonRepresentation json) {
		CcpJsonRepresentation mapDecorator = this.putAll(json.content);
		return mapDecorator;
	}
	
	public boolean containsField(CcpJsonFieldName field) {
		boolean containsField = this.containsField(field.getValue());
		return containsField;
	}

	private boolean containsField(String field) {
		boolean containsKey = this.content.containsKey(field);
		return containsKey;
	}

	public boolean containsAllFields(Collection<String> fields) {
		String[] array = this.toArray(fields);
		boolean containsAllFields = this.containsAllFields(array);
		return containsAllFields;
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
			if(containsField == assertion) {
				return assertion;
			}
		}
		if(assertion == false) {
			return true;
		}
		return false;
	}
	
	public Object get(CcpJsonFieldName field) {
		Object object = this.get(field.getValue());
		return object;
	}
	
	private Object get(String field) {
		Object object = this.content.get(field);
		boolean valueIsAbsent = object == null;
		if(valueIsAbsent) {
			throw new CcpErrorJsonFieldNotFound(field, this);
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
			if(object == null) {
				continue;
			}
			return (T) object;
		}
		throw new CcpErrorJsonFieldNotFound(Arrays.asList(fields).toString(), this);
	}
	
	public boolean isEmpty() {
		boolean empty = this.content.isEmpty();
		return empty;
	}

	public CcpJsonRepresentation addToList(CcpJsonFieldName field, Object... values) {
		CcpJsonRepresentation addToList = this.addToList(field.getValue(), values);
		return addToList;
	}

	private CcpJsonRepresentation addToList(String field, Object... values) {
		CcpJsonRepresentation result = this;
		for (Object value : values) {
			result = result.addToList(field, value);
		}
		return result;
	}
	
	public CcpJsonRepresentation addToList(String field, Object value) {
		List<Object> list = this.getAsObjectList(field);
		list = new ArrayList<>(list);
		list.add(value);
		CcpJsonRepresentation put = this.put(field, list);
		return put;
	}

	public CcpJsonRepresentation addToList(CcpJsonFieldName field, CcpJsonRepresentation value) {
		CcpJsonRepresentation addToList = this.addToList(field.getValue(), value);
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
		CcpJsonRepresentation addToItem = this.addToItem(field.getValue(), subField.getValue(), value);
		return addToItem;
	}
	
	private CcpJsonRepresentation addToItem(String field, String subField, Object value) {
		CcpJsonRepresentation itemAsMap = this.getInnerJson(field);
		itemAsMap = itemAsMap.put(subField, value);
		
		CcpJsonRepresentation put = this.put(field, itemAsMap.content);
		return put;
	}

	public CcpJsonRepresentation addToItem(CcpJsonFieldName field, CcpJsonFieldName subField, CcpJsonRepresentation value) {
		CcpJsonRepresentation addToItem = this.addToItem(field.getValue(), subField.getValue(), value);
		return addToItem;
	}
	
	private CcpJsonRepresentation addToItem(String field, String subField, CcpJsonRepresentation value) {
		CcpJsonRepresentation itemAsMap = this.getInnerJson(field);
		itemAsMap = itemAsMap.put(subField, value.content);
		
		CcpJsonRepresentation put = this.put(field, itemAsMap.content);
		return put;
	}

	public CcpJsonRepresentation copyIfNotContains(CcpJsonFieldName fieldToCopy, CcpJsonFieldName fieldToPaste) {
		CcpJsonRepresentation copyIfNotContains = this.copyIfNotContains(fieldToCopy.getValue(), fieldToPaste.getValue());
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
	
		CcpJsonRepresentation putIfNotContains = this.putIfNotContains(field.getValue(), value);
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
		CcpCollectionDecorator asArrayMetadata = this.getAsArrayMetadata(field.getValue());
		return asArrayMetadata;
	}
	
	private CcpCollectionDecorator getAsArrayMetadata(String field) {
		CcpCollectionDecorator cccpCollectionDecorator = new CcpCollectionDecorator(this, field);
		return cccpCollectionDecorator;
	}
	public CcpItIsTrueThatTheFollowingFields itIsTrueThatTheFollowingFields(CcpJsonFieldName...fields) {
		String[] fields2 = this.getFields(fields);
		CcpItIsTrueThatTheFollowingFields itIsTrueThatTheFollowingFields = this.itIsTrueThatTheFollowingFields(fields2);
		return itIsTrueThatTheFollowingFields;
	}
	
	private CcpItIsTrueThatTheFollowingFields itIsTrueThatTheFollowingFields(String...fields) {
		return new CcpItIsTrueThatTheFollowingFields(this, fields);
	}
	
	public Set<String> getMissingFields(Collection<String> fields){
		Set<String> collect = fields.stream().filter(field -> this.getAsString(field).trim().isEmpty()).collect(Collectors.toSet());
		return collect;
	}
	
	public InputStream toInputStream() {
		String asUgglyJson = this.asUgglyJson();
		byte[] bytes = asUgglyJson.getBytes(StandardCharsets.UTF_8);
		InputStream stream = new ByteArrayInputStream(bytes);
		return stream;
	}
	
	public String getSha1Hash(CcpHashAlgorithm algorithm) {
		String asUgglyJson = this.asUgglyJson();
		String hash = new CcpStringDecorator(asUgglyJson).hash().asString(algorithm);
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
		
		CcpBusiness[] array = jsonTransformers.toArray(new CcpBusiness[jsonTransformers.size()]);
		CcpJsonRepresentation transformedJson = this.getTransformedJson(array);
		return transformedJson;
	}

	public boolean isInnerJson(CcpJsonFieldName fieldName) {
		boolean innerJson = this.isInnerJson(fieldName.getValue());
		return innerJson;
	}
	
	private boolean isInnerJson(String fieldName) {
		CcpJsonHandler handler = CcpDependencyInjection.getDependency(CcpJsonHandler.class);
		String asString = this.getAsString(fieldName);
		if(asString.trim().isEmpty()) {
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

	public CcpJsonRepresentation addToItem(String completeRuleName, CcpJsonFieldName evaluatedfields,
			Object... fields) {
		CcpJsonRepresentation addToItem = this.addToItem(completeRuleName, evaluatedfields.getValue(), fields);
		return addToItem;
	}

	public CcpJsonRepresentation addToItem(CcpJsonFieldName errors, String completeRuleName,
			CcpJsonRepresentation errors2) {
		CcpJsonRepresentation addToItem = this.addToItem(errors.getValue(), completeRuleName, errors2);
		return addToItem;
	}

	
	public CcpDynamicJsonRepresentation getDynamicVersion() {
		return new CcpDynamicJsonRepresentation(this);
	}
	
	
	public static class CcpDynamicJsonRepresentation {
		private CcpJsonRepresentation json;

		private CcpDynamicJsonRepresentation(CcpJsonRepresentation json) {
			this.json = json;
		}

		public String toString() {
			return this.json.toString();
		}

		public CcpJsonRepresentation addJsonTransformer(String field,
				CcpBusiness process) {
			return this.json.addJsonTransformer(field, process);
		}

		public CcpJsonRepresentation addToItem(String field, String subField, CcpJsonRepresentation value) {
			return this.json.addToItem(field, subField, value);
		}

		public CcpJsonRepresentation addToItem(String field, String subField, Object value) {
			return this.json.addToItem(field, subField, value);
		}

		public CcpJsonRepresentation addToList(String field, CcpJsonRepresentation value) {
			return this.json.addToList(field, value);
		}

		public CcpJsonRepresentation addToList(String field, Object... values) {
			return this.json.addToList(field, values);

		}

		public boolean containsAllFields(String... fields) {
			return this.json.containsAllFields(fields);
		}

		public boolean containsAnyFields(String... fields) {
			return this.json.containsAnyFields(fields);
		}

		public boolean containsField(String field) {
			return this.json.containsField(field);
		}

		public boolean containsFields(boolean assertion, String... fields) {
			return this.json.containsFields(assertion, fields);
		}

		public CcpJsonRepresentation copyIfNotContains(String fieldToCopy, String fieldToPaste) {
			return this.json.copyIfNotContains(fieldToCopy, fieldToPaste);
		}

		public CcpJsonRepresentation duplicateValueFromField(String fieldToCopy, String... fieldsToPaste) {
			return this.json.duplicateValueFromField(fieldToCopy, fieldsToPaste);
		}

		public Object get(String field) {
			return this.json.get(field);
		}

		public CcpCollectionDecorator getAsArrayMetadata(String field) {
			return this.json.getAsArrayMetadata(field);
		}

		public boolean getAsBoolean(String field) {
			return this.json.getAsBoolean(field);			
		}

		public Double getAsDoubleNumber(String field) {
			return this.json.getAsDoubleNumber(field);			
		}

		public Integer getAsIntegerNumber(String field) {
			return this.json.getAsIntegerNumber(field);
		}

		public List<CcpJsonRepresentation> getAsJsonList(String field) {
			return this.json.getAsJsonList(field);			
		}

		public Long getAsLongNumber(String field) {
			return this.json.getAsLongNumber(field);
		}

		public <T> T getAsObject(String... fields) {
			return this.json.getAsObject(fields);
		}

		public List<Object> getAsObjectList(String field) {
			return this.json.getAsObjectList(field);
		}

		public String getAsString(String field) {
			return this.json.getAsString(field);
		}

		public CcpStringDecorator getAsStringDecorator(String field) {
			return this.json.getAsStringDecorator(field);			
		}

		public List<String> getAsStringList(String... fields) {
			return this.json.getAsStringList(fields);
		}

		public CcpTextDecorator getAsTextDecorator(String field) {
			return this.json.getAsTextDecorator(field);			
		}

		public CcpJsonRepresentation getInnerJson(String field) {
			return this.json.getInnerJson(field);
		}

		public CcpJsonRepresentation getInnerJsonFromPath(String... paths) {
			return this.json.getInnerJsonFromPath(paths);
		}

		public List<CcpJsonRepresentation> getInnerJsonListFromPath(String... paths) {
			return this.json.getInnerJsonListFromPath(paths);
		}

		public <T> T getOrDefault(String field, T defaultValue) {
			return this.json.getOrDefault(field, defaultValue);
		}

		public CcpJsonRepresentation getTransformedJsonIfFoundTheField(String field,
				 CcpBusiness... transformers) {
			return this.json.getTransformedJsonIfFoundTheField(field, transformers);
		}

		public <T> T getValueFromPath(T defaultValue, String... paths) {
			return this.json.getValueFromPath(defaultValue, paths);
		}

		public boolean isInnerJson(String fieldName) {
			return this.json.isInnerJson(fieldName);			
		}

		public CcpItIsTrueThatTheFollowingFields itIsTrueThatTheFollowingFields(String... fields) {
			return this.json.itIsTrueThatTheFollowingFields(fields);
		}

		public CcpJsonRepresentation put(String field, CcpDecorator<?> map) {
			return this.json.put(field, map);
		}

		public CcpJsonRepresentation put(String field, Collection<CcpJsonRepresentation> list) {
			return this.json.put(field, list);

		}

		public CcpJsonRepresentation put(String field, Object value) {
			return this.json.put(field, value);

		}

		public CcpJsonRepresentation putFilledTemplate(String fieldToSearch, String fieldToPut) {
			return this.json.putFilledTemplate(fieldToSearch, fieldToPut);

		}

		public CcpJsonRepresentation putIfNotContains(String field, Object value) {
			return this.json.putIfNotContains(field, value);
		}

		public CcpJsonRepresentation putSameValueInManyFields(Object value, String... fields) {
			return this.json.putSameValueInManyFields(value, fields);
		}

		public CcpJsonRepresentation removeFields(String... fields) {
			return this.json.removeFields(fields);
		}

		public CcpJsonRepresentation renameField(String oldField, String newField) {
			return this.json.renameField(oldField, newField);
		}

		public CcpJsonRepresentation getJsonPiece(String... fields) {
			return this.json.getJsonPiece(fields);
		}

		public CcpJsonRepresentation removeField(String time) {
			return this.json.removeField(time);
		}
	}

	public CcpJsonRepresentation addToItem(CcpJsonFieldName statis, String field, Object avg) {
		CcpJsonRepresentation addToItem = addToItem(statis.getValue(), field, avg);
		return addToItem;
	}
	public CcpJsonRepresentation getInnerJsonFromPath(CcpJsonFieldName fieldName, String value) {
		CcpJsonRepresentation innerJsonFromPath = this.getInnerJsonFromPath(fieldName.getValue(), value);
		return innerJsonFromPath;
	}
}
