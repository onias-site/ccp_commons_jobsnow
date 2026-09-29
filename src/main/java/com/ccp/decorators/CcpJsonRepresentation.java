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
 
/**
 * Central type of the jobsnow framework. Represents a JSON document as an immutable {@code Map<String, Object>}
 * and is the only data type that flows between all business components. Offers a comprehensive fluent API for
 * reading, writing, transforming, comparing, deep navigation and conditional field validation.
 */
public class CcpJsonRepresentation  {
	/**
	 * Default fields used to serialize exception details: cause, message, stack trace, type, stack trace hash and complete stack trace.
	 */
	public static enum Fields implements CcpJsonFieldName{
		cause, message, stackTrace, type, stackTraceHash, completeStackTrace
	}
	/**
	 * Creates a new instance from the content of another JSON (content copy).
	 * @param json the source JSON
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
	 * Public factory for an empty JSON.
	 */
	public static CcpJsonRepresentation getEmptyJson() {
		return new CcpJsonRepresentation();
	}

	/**
	 * Reads the stream and builds the JSON; accepts JSON content or {@code Properties} format.
	 * @param inputStream the input stream
	 */
	public CcpJsonRepresentation(InputStream inputStream) {

		this.content = new HashMap<>();
		String result = this.extractJson(inputStream);
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
			throw new RuntimeException(e);
		}
		
		Set<Object> keySet = props.keySet();
		for (Object key : keySet) {
			Object value = props.get(key);
			this.content.put("" + key, value);
		}
	}

	private String extractJson(InputStream inputStream) {
		InputStreamReader reader = new InputStreamReader(inputStream);
		String result = new BufferedReader(reader).lines().collect(Collectors.joining("\n"));
		return result;
	}
	
	/**
	 * Serializes the details of an exception (message, stack trace, cause) as JSON.
	 * @param e the exception to serialize
	 */
	@CcpAllowNullParameter
	public CcpJsonRepresentation(Throwable e) {
		this(getErrorDetails(e).content);
	}

	/**
	 * Deserializes a JSON string; throws {@code CcpErrorJsonInvalid} if it is invalid.
	 * @param json the JSON string to deserialize
	 */
	public CcpJsonRepresentation(String json) {
		this(getMap(json));
	}
 
	private static Map<String, Object> getMap(String json) {
		CcpJsonHandler handler = CcpDependencyInjection.getDependency(CcpJsonHandler.class);
		boolean invalidJson = false ==  handler.isValidJson(json);
		if(invalidJson) {
			throw new CcpErrorJsonInvalid(json);
		}
		Map<String, Object> fromJson = handler.fromJson(json);
		return fromJson;

	}
	
	/**
	 * Creates from an existing map.
	 * @param content the map of fields and values
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

		CcpJsonRepresentation errorDetails = CcpOtherConstants.EMPTY_JSON;
		
		if(e == null) {
			return errorDetails; 
		}
		
		Throwable cause = e.getCause();
		String message = e.getMessage();
		if(message == null) {
			message = "";
		}
		StackTraceElement[] stackTraceElements = e.getStackTrace();
		List<String> stackTrace = new ArrayList<>();
		for (StackTraceElement stackTraceElement : stackTraceElements) {
			String stackTraceLine = getStackTraceLine(stackTraceElement);
			stackTrace.add(stackTraceLine); 
		}
		Object causeDetails = getCauseDetails(cause, stackTraceElements);
		var completeStackTrace = getCompleteStackTrace(e).stream().map(x -> x.toString()).collect(Collectors.toList());
		errorDetails = errorDetails.put(Fields.completeStackTrace, completeStackTrace).put(Fields.type, e.getClass().getName()).put(Fields.stackTrace, stackTrace).put(Fields.message, message).put(Fields.cause, causeDetails);
		return errorDetails;
	}

	@CcpAllowNullParameter
	private static List<StackTraceElement> getCompleteStackTrace(Throwable e){
		if(e == null) {
			return new ArrayList<StackTraceElement>();
		}
		StackTraceElement[] stackTrace = e.getStackTrace();
		List<StackTraceElement> ownStackTrace = Arrays.asList(stackTrace);
		Throwable cause = e.getCause();
		List<StackTraceElement> causeStackTrace = getCompleteStackTrace(cause);
		var completeStackTrace = new ArrayList<>(causeStackTrace);
		completeStackTrace.addAll(ownStackTrace);
		return completeStackTrace;
		
	}
	
	@CcpAllowNullParameter
	private static Object getCauseDetails(Throwable cause, StackTraceElement[] stackTraceElements) {
		
		boolean hasCause = cause != null;
		
		if(hasCause) {
			CcpJsonRepresentation errorDetails = getErrorDetails(cause);
			return errorDetails;
		}
		return ""; 
	}

	private static String getStackTraceLine(StackTraceElement stackTraceElement) {
		int lineNumber = stackTraceElement.getLineNumber();
		String methodName = stackTraceElement.getMethodName();
		String fileName = stackTraceElement.getFileName();
		if(fileName == null) {
			return "-";
		}
		String stackTraceLine = fileName.replace(".java", "") + "." + methodName + ":" + lineNumber;
		return stackTraceLine;
	}

	@SuppressWarnings("unchecked")
	public <T> T getAsEnum(CcpJsonFieldName field, Class<T> clazz){
		try {
			
			String asString = this.getAsString(field);
			Method valueOfMethod = clazz.getDeclaredMethod("valueOf", String.class);
			Object enumValue = valueOfMethod.invoke(null, asString);
			return (T)enumValue;
		} catch (Exception e) {
			String value = field.getValue();
			Object object = this.content.get(value);
			throw new CcpErrorJsonInvalidFieldFormat(object, value, "enum", this);
		}
	}
	
	
	public <T> T getAsEnum(CcpJsonFieldName field, Class<T> clazz, T defaultValue){
		String asString = this.getAsString(field);
		
		boolean hasNoEnum = asString.trim().isEmpty();
		
		if(hasNoEnum) {
			return defaultValue;
		}
		T asEnum = this.getAsEnum(field, clazz);
		return asEnum;
	}
	
	public Long getAsLongNumber(CcpJsonFieldName field) {
		Long asLongNumber = this.getAsLongNumber(field.getValue());
		return asLongNumber;
	}
	
	private Long getAsLongNumber(String field) {
		
		Object object = this.content.get(field);
		
		if(object == null) {
			throw new CcpErrorJsonInvalidFieldFormat("", field, "long", this);
		}
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
		if(object == null) {
			throw new CcpErrorJsonInvalidFieldFormat("", field, "integer", this);
		}
		try {
			return Double.valueOf("" + object).intValue();
		} catch (Exception e) {
			throw new CcpErrorJsonInvalidFieldFormat(object, field, "integer", this);
		}
	}
	
	public Supplier<CcpJsonRepresentation> getJsonSupplier(){
		Supplier<CcpJsonRepresentation> supplier = () -> this;
		return supplier;
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
		if(object == null) {
			throw new CcpErrorJsonInvalidFieldFormat("", field, "double", this);
		}
		try {
			return Double.valueOf("" + object);
		} catch (Exception e) {
			throw new CcpErrorJsonInvalidFieldFormat(object, field, "double", this);
		}
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
	
	/**
	 * Executes {@code business} only if NONE of the specified fields is present.
	 * @param business the logic to execute
	 * @param fields the fields to check
	 */
	public CcpJsonRepresentation whenFieldsAreNotFound(CcpBusiness business, CcpJsonFieldName... fields) {
		boolean anyFieldIsPresent = this.containsAnyFields(fields);
		if(anyFieldIsPresent) {
			return this;
		}
		
		CcpJsonRepresentation executionResult = business.execute(this);
		return executionResult;
	
	}

	/**
	 * Executes {@code business} if AT LEAST ONE of the fields is present.
	 * @param business the logic to execute
	 * @param fields the fields to check
	 */
	public CcpJsonRepresentation whenAnyFieldsAreFound(CcpBusiness business, CcpJsonFieldName... fields) {
		
		boolean anyFieldIsNotPresent = false == this.containsAnyFields(fields);
		
		if(anyFieldIsNotPresent) {
			return this;
		}
		
		CcpJsonRepresentation executionResult = business.execute(this);
		return executionResult;
	}

	/**
	 * Executes {@code business} only if ALL the fields are present.
	 * @param business the logic to execute
	 * @param fields the fields to check
	 */
	public CcpJsonRepresentation whenAllFieldsAreFound(CcpBusiness business, CcpJsonFieldName... fields) {
		boolean notAllFieldsArePresent = false == this.containsAllFields(fields);
		
		if(notAllFieldsArePresent) {
			return this;
		}
		
		CcpJsonRepresentation executionResult = business.execute(this);
		return executionResult;
	}
	
	/**
	 * Returns the field value as a string. Returns {@code ""} if absent or null; serializes Maps and Collections correctly.
	 * @param field the field to read
	 */
	public String getAsString(CcpJsonFieldName field) {
		String asString = this.getAsString(field.getValue());
		return asString;
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	private String getAsString(String field) {
 
		Object object = this.content.get(field);
		
		boolean thisKeyIsNotPresent = false == this.content.containsKey(field);
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
		
		if(object instanceof Collection<?> col) {
			
			if(col.isEmpty()) {
				return col.toString();
			}

			List<Object> items = col.stream().map(x -> x instanceof Map ? new CcpJsonRepresentation((Map)x) : x).collect(Collectors.toList());

			
			if(items.get(0) instanceof CcpJsonRepresentation) {
				return items.toString();
			}
			CcpJsonHandler jsonHandler = CcpDependencyInjection.getDependency(CcpJsonHandler.class);
			String json = jsonHandler.toJson(col);
			return json;
		}
		
		return ("" + object);
	}
	public <T> T getOrDefault(CcpJsonFieldName field, Supplier<T> supplier) {
		T defaultValue = supplier.get();
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
		String[] fieldNames = this.getFields(fields);
		CcpJsonRepresentation jsonPiece = this.getJsonPiece(fieldNames);
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

	/**
	 * Serializes the JSON in compact format (no formatting).
	 */
	public String asUgglyJson() {
		
		CcpJsonHandler json = CcpDependencyInjection.getDependency(CcpJsonHandler.class);
		TreeMap<String, Object> sortedContent = new TreeMap<>(this.content);
		String uglyJson = json.toJson(sortedContent);
		return uglyJson;
		
	}

	/**
	 * Serializes with indentation and line breaks.
	 */
	public String asPrettyJson() {
		CcpJsonHandler json = CcpDependencyInjection.getDependency(CcpJsonHandler.class);
		String asPrettyJson = json.asPrettyJson(this.content);
		return asPrettyJson;
	}
	
	
	public String toString() {
		try {
			CcpJsonHandler json = CcpDependencyInjection.getDependency(CcpJsonHandler.class);
			String prettyJson = json.asPrettyJson(new TreeMap<>(this.content));
			return prettyJson;
			
		} catch (Exception e) {
			return this.content.toString();
		}
	}
	

	/**
	 * Returns the set of names of the fields present.
	 */
	public Set<String> fieldSet(){
		Set<String> keySet = this.content.keySet();
		return keySet;
	}
	
	public CcpJsonRepresentation put(CcpJsonFieldName field, CcpDecorator<?> map) {
		CcpJsonRepresentation updatedJson = this.put(field.getValue(), map);
		return updatedJson;
	}
	
	private CcpJsonRepresentation put(String field, CcpDecorator<?> map) {
		Object internalContent = map.getContent();
		CcpJsonRepresentation updatedJson = this.put(field, internalContent);
		return updatedJson;
	}
	
	public CcpJsonRepresentation put(CcpJsonFieldName field, Collection<CcpJsonRepresentation> list) {
		CcpJsonRepresentation updatedJson = this.put(field.getValue(), list);
		return updatedJson;
	}
	
	private CcpJsonRepresentation put(String field, Collection<CcpJsonRepresentation> list) {
		List<Map<String, Object>> contents = list.stream().map(x -> x.content).collect(Collectors.toList());
		CcpJsonRepresentation updatedJson = this.put(field, contents);
		return updatedJson;
	}
	
	/**
	 * Applies an extractor function to this JSON and returns the result.
	 * @param extractor the extractor function
	 */
	public <T> T extractInformationFromJson(Function<CcpJsonRepresentation, T> extractor) {
		T information = extractor.apply(this);
		return information;
	}
	
	/**
	 * Applies a sequence of transformers in a chain, passing the result of one to the next.
	 * @param transformers the transformers to apply in sequence
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
	 * Associates a {@code CcpBusiness} as the value of a field (stores transformers for later use).
	 * @param field the field where the transformer will be stored
	 * @param process the transformer to store
	 */
	public CcpJsonRepresentation addJsonTransformer(CcpJsonFieldName field, CcpBusiness process) {
		CcpJsonRepresentation addJsonTransformer = this.addJsonTransformer(field.getValue(), process);
		return addJsonTransformer;
	}
	
	/**
	 * Associates a {@code CcpBusiness} as the value of an integer field (index).
	 * @param field the field index
	 * @param process the transformer to store
	 */
	public CcpJsonRepresentation addJsonTransformer(Integer field, CcpBusiness process) {
		CcpJsonRepresentation addJsonTransformer = this.addJsonTransformer("" + field, process);
		return addJsonTransformer;
	}
	private CcpJsonRepresentation addJsonTransformer(String field, CcpBusiness process) {
		CcpJsonRepresentation updatedJson = this.put(field, process);
		return updatedJson;
	}
	
	/**
	 * Sets the same value in multiple fields at once.
	 * @param value the value to set
	 * @param fields the fields to fill
	 */
	public CcpJsonRepresentation putSameValueInManyFields(Object value, CcpJsonFieldName... fields) {
		String[] fieldNames = this.getFields(fields);
		CcpJsonRepresentation putSameValueInManyFields = this.putSameValueInManyFields(value, fieldNames);
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
	 * Returns a new instance with the field added/replaced (immutable).
	 * @param field the field to add or replace
	 * @param value the field value
	 */
	public CcpJsonRepresentation put(CcpJsonFieldName field, Object value) {
		CcpJsonRepresentation updatedJson = this.put(field.getValue(), value);
		return updatedJson;
	}

	/**
	 * Adds a nested JSON as the field value.
	 * @param field the field where the nested JSON will be stored
	 * @param value the nested JSON
	 */
	public CcpJsonRepresentation put(CcpJsonFieldName field, CcpJsonRepresentation value) {
		CcpJsonRepresentation updatedJson = this.put(field.getValue(), value.content);
		return updatedJson;
	}
	
	/**
	 * Uses the enum's own {@code getValue()} as the field value (convenient for self-describing fields).
	 * @param field the field whose value will be its own name
	 */
	public CcpJsonRepresentation put(CcpJsonFieldName field) {
		CcpJsonRepresentation updatedJson = this.put(field, field);
		return updatedJson;
	}
	
	private CcpJsonRepresentation put(String field, Object value) {
		Map<String, Object> content = new LinkedHashMap<>();
		content.putAll(this.content);
		content.put(field, value);
		CcpJsonRepresentation json = new CcpJsonRepresentation(content);
		return json;
	}  

	/**
	 * Copies the value of a field to one or more other fields.
	 * @param fieldToCopy the source field
	 * @param fieldsToPaste the target fields
	 */
	public CcpJsonRepresentation duplicateValueFromField(CcpJsonFieldName fieldToCopy, CcpJsonFieldName... fieldsToPaste) {
		String[] fields = this.getFields(fieldsToPaste);
		CcpJsonRepresentation response = this.duplicateValueFromField(fieldToCopy.getValue(), fields);
		return response;
	}	

	private CcpJsonRepresentation duplicateValueFromField(String fieldToCopy, String... fieldsToPaste) {
		boolean inexistentField = false == this.containsAllFields(fieldToCopy);

		if (inexistentField) {
			return this;
		}
		
		CcpJsonRepresentation jsonWithCopies = this;
		
		for (String fieldToPaste : fieldsToPaste) {
			Object value = this.get(fieldToCopy);
			jsonWithCopies = jsonWithCopies.put(fieldToPaste, value);
		}
		
		return jsonWithCopies;
	}
	
	/**
	 * Renames a field (moves the value from {@code oldField} to {@code newField}).
	 * @param oldField the original field
	 * @param newField the new field name
	 */
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
	
	private CcpJsonRepresentation removeField(String field) {
		Map<String, Object> content = this.getContent();
		Map<String, Object> copy = new HashMap<>(content);
		copy.remove(field);
		CcpJsonRepresentation json = new CcpJsonRepresentation(copy);
		return json;
	}
	
	/**
	 * Returns a new instance without the specified fields.
	 * @param fields the fields to remove
	 */
	public CcpJsonRepresentation removeFields(CcpJsonFieldName... fields) {
		String[] fieldNames = this.getFields(fields);
		CcpJsonRepresentation removeFields = this.removeFields(fieldNames);
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
	 * Returns the immutable internal map.
	 */
	public Map<String, Object> getContent() {
		return this.content;
	}

	/**
	 * Creates an independent copy of the current JSON.
	 */
	public CcpJsonRepresentation copy() {
		CcpJsonRepresentation json = new CcpJsonRepresentation(this.getContent());
		return json;
	}
	
	/**
	 * Navigates a path of multiple nested fields and returns the JSON found.
	 * @param paths the path of nested fields
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
			CcpJsonRepresentation innerJson =  this.getValueFromPath(CcpOtherConstants.EMPTY_JSON, paths);
			return innerJson;
		}
	}

	/**
	 * Navigates the path and returns the typed value; returns {@code defaultValue} if any field of the path is absent.
	 * @param defaultValue the default value when the path does not exist
	 * @param paths the path of nested fields
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
			boolean conditionIsMet = condition.test(this);
			if(conditionIsMet) {
				continue;
			}
			CcpJsonRepresentation executionResult = conditionsDoNotMet.execute(this);
			return executionResult;
		}
		
		CcpJsonRepresentation executionResult = conditionsMet.execute(this);
		return executionResult;
	}
	
	public CcpJsonRepresentation getTransformedJsonConsideringIfAnyOfTheConditionsIsMet(CcpBusiness conditionsMet, CcpBusiness conditionsDoNotMet, @SuppressWarnings("unchecked") Predicate<CcpJsonRepresentation>... conditions) {
		for (Predicate<CcpJsonRepresentation> condition : conditions) {
			boolean conditionIsNotMet = false == condition.test(this);
			if(conditionIsNotMet) {
				continue;
			}
			CcpJsonRepresentation executionResult = conditionsMet.execute(this);
			return executionResult;
		}
		
		CcpJsonRepresentation executionResult = conditionsDoNotMet.execute(this);
		return executionResult;
	}
	
	@SuppressWarnings("unchecked")
	private <T>T getValueFromPath(T defaultValue, String... paths){
		
		boolean pathIsMissing = paths.length == 0;
		
		if(pathIsMissing) {
			throw new CcpErrorJsonPathIsMissing(this);
		}
		
		CcpJsonRepresentation currentJson = this;
		
		int lastIndex = paths.length - 1;
		boolean lastFieldIsJson = false;
		for(int k = 0; k < paths.length; k++) {
			String path = paths[k];
			
			boolean notContainsAllFields = false == currentJson.containsAllFields(path);
			
			if(notContainsAllFields) {
				return defaultValue;
			}
			
			CcpTextDecorator asTextDecorator = currentJson.getAsTextDecorator(path);
			boolean validSingleJson = asTextDecorator.isValidSingleJson();
			lastFieldIsJson = validSingleJson;
			
			if(validSingleJson) {
				currentJson = currentJson.getInnerJson(path);
				continue;
			}
		}
		
		if(lastFieldIsJson) {
			return (T)currentJson;
		}
		
		String path = paths[lastIndex];
		T asObject = currentJson.getAsObject(path);
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
			
			Class<? extends Object> valueClass = value.getClass();
			throw new CCpErrorJsonFieldIsNotValidJsonList(this, valueClass, paths);
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
				
				boolean validJsonList = jsonHandler.isValidJsonList(object.toString());
				
				List<Map<String, Object>> fromJson = new ArrayList<>();
				if(validJsonList) {
					fromJson = jsonHandler.fromJson(object.toString());
				}
				List<CcpJsonRepresentation> jsonList = fromJson.stream().map(json -> new CcpJsonRepresentation(json)).collect(Collectors.toList());
				return jsonList;
			} catch (ClassCastException e) {
				return new ArrayList<>(); 
			}
		}

		if(false == object instanceof Collection) {
			return new ArrayList<>();
		}

		
		Collection<Object> list = (Collection<Object>) object;
		
		List<CcpJsonRepresentation> jsonList = list.stream().map(obj -> {
			
			if(obj instanceof CcpJsonRepresentation jsonItem) {
				return jsonItem;
			}
			
			CcpJsonRepresentation json = new CcpJsonRepresentation((Map<String, Object>) obj);
			return json;
		})
				.collect(Collectors.toList());
		
		return jsonList;
	}

	public CcpCollectionDecorator getAsCollectionDecorator(String field){
		List<String> asStringList = this.getAsStringList(field);
		Object[] array = asStringList.toArray(new String[asStringList.size()]);
		CcpCollectionDecorator collectionDecorator = new CcpCollectionDecorator(array);
		return collectionDecorator;
	}
	
	public List<String> getAsStringList(CcpJsonFieldName... fields){
		String[] fieldNames = this.getFields(fields);
		List<String> asStringList = this.getAsStringList(fieldNames);
		return asStringList;
	}
	
	public String[] getAsStringArray(CcpJsonFieldName... fields) {
		List<String> asStringList = this.getAsStringList(fields);
		String[] array = asStringList.toArray(new String[asStringList.size()]);
		return array;
	}
	
	private List<String> getAsStringList(String... fields){
		for (String field : fields) {
			List<String> stringList = this.getAsObjectList(field).stream()
					.filter(x -> x != null)
					.map(x -> x.toString()).collect(Collectors.toList());
			if(stringList.isEmpty()) {
				continue;
			}
			return stringList;
		}
		return new ArrayList<>();
	}
	
	public List<Object> getAsObjectList(CcpJsonFieldName field) {
		List<Object> asObjectList = this.getAsObjectList(field.getValue());
		return asObjectList;
	}
	
	private List<Object> getAsObjectList(String field) {
		
		boolean isNotPresent = false == this.containsAllFields(field);
		
		if(isNotPresent) {
			return new ArrayList<>();
		}
		
		Object object = this.content.get(field);
		
		if(object instanceof Object[]) {
			Object[] array = this.getAsObject(field);
			List<Object> arrayAsList = Arrays.asList(array);
			return arrayAsList;
		}
		
		if(object instanceof Collection<?> list) {
			return new ArrayList<Object>(list);
		}
		
		boolean empty = object.toString().trim().isEmpty();
		
		if(empty) {
			return new ArrayList<>();
		}
		
		CcpJsonHandler jsonHandler = CcpDependencyInjection.getDependency(CcpJsonHandler.class);
		
		try {
			List<Object> fromJson = jsonHandler.fromJson(object.toString());
			return fromJson;
		} catch (Exception e) {
			return Arrays.asList(object.toString());
		}
	}
	
	public CcpJsonRepresentation mergeWithAnotherJson(Map<String, Object> map) {
		Map<String, Object> currentContent = this.getContent();
		Map<String, Object> content = new LinkedHashMap<>(currentContent);
		content.putAll(map);
		CcpJsonRepresentation mergedJson = new CcpJsonRepresentation(content);
		return mergedJson;
	}

	public CcpJsonRepresentation mergeWithAnotherJson(CcpJsonRepresentation json) {
		CcpJsonRepresentation mergedJson = this.mergeWithAnotherJson(json.content);
		return mergedJson;
	}
	
	
	public boolean containsField(CcpJsonFieldName field) {
		boolean containsField = this.containsField(field.getValue());
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
		String[] emptyArray = new String[size];
		String[] array = fields.toArray(emptyArray);
		return array;
	}	

	public boolean containsAllFields(CcpJsonFieldName... fields) {
		String[] fieldNames = this.getFields(fields);
		boolean containsAllFields = this.containsAllFields(fieldNames);
		return containsAllFields;
	}
	
	private boolean containsAllFields(String... fields) {
		boolean containsFields = this.containsFields(false, fields);
		return containsFields; 
	}
	
	public boolean containsAnyFields(CcpJsonFieldName... fields) {
		String[] fieldNames = this.getFields(fields);
		boolean containsAnyFields = this.containsAnyFields(fieldNames);
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
		if(false == assertion) {
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
		String[] fieldNames = this.getFields(fields);
		T asObject = this.getAsObject(fieldNames);
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
	
	private CcpJsonRepresentation addToList(String field, Object value) {
		List<Object> list = this.getAsObjectList(field);
		list = new ArrayList<>(list);
		list.add(value);
		CcpJsonRepresentation updatedJson = this.put(field, list);
		return updatedJson;
	}

	public CcpJsonRepresentation addToList(CcpJsonFieldName field, CcpJsonRepresentation value) {
		CcpJsonRepresentation addToList = this.addToList(field.getValue(), value);
		return addToList;
	}
	
	private CcpJsonRepresentation addToList(String field, CcpJsonRepresentation value) {
		List<Object> list = this.getAsObjectList(field);
		list = new ArrayList<>(list);
		list.add(value.content);
		CcpJsonRepresentation updatedJson = this.put(field, list);
		return updatedJson;
	}
	
	public CcpJsonRepresentation addToItem(CcpJsonFieldName field, CcpJsonFieldName subField, Object value) {
		CcpJsonRepresentation addToItem = this.addToItem(field.getValue(), subField.getValue(), value);
		return addToItem;
	}
	
	private CcpJsonRepresentation addToItem(String field, String subField, Object value) {
		CcpJsonRepresentation itemAsMap = this.getInnerJson(field);
		itemAsMap = itemAsMap.put(subField, value);
		
		CcpJsonRepresentation updatedJson = this.put(field, itemAsMap.content);
		return updatedJson;
	}

	public CcpJsonRepresentation addToItem(CcpJsonFieldName field, CcpJsonFieldName subField, CcpJsonRepresentation value) {
		CcpJsonRepresentation addToItem = this.addToItem(field.getValue(), subField.getValue(), value);
		return addToItem;
	}
	
	private CcpJsonRepresentation addToItem(String field, String subField, CcpJsonRepresentation value) {
		CcpJsonRepresentation itemAsMap = this.getInnerJson(field);
		itemAsMap = itemAsMap.put(subField, value.content);
		
		CcpJsonRepresentation updatedJson = this.put(field, itemAsMap.content);
		return updatedJson;
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
		
		CcpJsonRepresentation updatedJson = this.put(field, value);
		return updatedJson;
	}
	
	public CcpCollectionDecorator getAsArrayMetadata(CcpJsonFieldName field) {
		CcpCollectionDecorator asArrayMetadata = this.getAsArrayMetadata(field.getValue());
		return asArrayMetadata;
	}
	
	private CcpCollectionDecorator getAsArrayMetadata(String field) {
		CcpCollectionDecorator collectionDecorator = new CcpCollectionDecorator(this, field);
		return collectionDecorator;
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
		String hash = this.getSha1Hash(CcpHashAlgorithm.SHA1);
		int hashCode = hash.hashCode();
		return hashCode;
	}
	
	public boolean equals(Object obj) {
		
		if(obj instanceof CcpJsonRepresentation other) {
			String otherHash = other.getSha1Hash(CcpHashAlgorithm.SHA1);
			String thisHash = this.getSha1Hash(CcpHashAlgorithm.SHA1);
			boolean equals = otherHash.equals(thisHash);
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
		for (CcpJsonFieldName enumItem : enumItems) {
			array[k++] = enumItem.getValue();
		}
		return array;
	}

	public CcpJsonRepresentation getInnerJsonFromPath(CcpJsonFieldName fieldName, String value) {
		CcpJsonRepresentation innerJsonFromPath = this.getInnerJsonFromPath(fieldName.getValue(), value);
		return innerJsonFromPath;
	}



	/**
	 * Exception thrown when the value of a field exists in the JSON but cannot be converted to the expected type
	 * (for example, trying to read {@code "abc"} as a {@code long}).
	 */
	@SuppressWarnings("serial")
	public static class CcpErrorJsonInvalidFieldFormat extends RuntimeException {
		/**
		 * Builds the message stating the value found, the field name, the expected type and the complete JSON.
		 * @param value the value found in the field
		 * @param fieldName the field name
		 * @param fieldType the expected type
		 * @param json the JSON at the moment of the error
		 */
		private CcpErrorJsonInvalidFieldFormat(Object value, String fieldName, String fieldType, CcpJsonRepresentation json) {
			super("The value '" + value + "' from the field '" + fieldName + " is not a '" + fieldType + "' in the following json: " + json);
		}
	}



	@SuppressWarnings("unchecked")
	public <T> List<T> getAsEnumList(CcpJsonFieldName field, Class<T> enumClass) {
		try {
			Method method = enumClass.getDeclaredMethod("valueOf", String.class);
			List<String> asStringList = this.getAsStringList(field);
			List<T> list = new ArrayList<>();
			for (String string : asStringList) {
				T enumValue = (T)method.invoke(null, string);
				list.add(enumValue);
			}
			return list;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

}