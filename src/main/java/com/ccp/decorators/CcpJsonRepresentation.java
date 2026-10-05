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
 * Central type of the jobsnow framework. Represents a JSON document as a {@code Map<String, Object>} and is the data
 * type that flows between all business components. Offers a fluent API for reading, writing, transforming, comparing,
 * deep navigation and conditional execution.
 * <p>
 * Instances are immutable: every "write" method returns a new instance and the map given to the constructor is copied
 * into an unmodifiable, insertion-ordered map. The only exception is the empty JSON built by
 * {@link #getEmptyJson()}, whose internal map is a mutable {@code HashMap}.
 * <p>
 * A field counts as present only when its value is not {@code null}.
 */
public class CcpJsonRepresentation  {
	/**
	 * Default fields used to serialize exception details: cause, message, stack trace, type, stack trace hash and complete stack trace.
	 */
	public static enum CcpStackTraceFields implements CcpJsonFieldName{
		/** Error details of the cause, or an empty text when there is none. */
		cause,
		/** Message of the exception ({@code ""} when null). */
		message,
		/** Own frames of the exception, as {@code File.method:line}. */
		stackTrace,
		/** Class name of the exception. */
		type,
		/** Hash of the stack trace; never filled by this class (the REST exception handler computes its own). */
		stackTraceHash,
		/** Frames of the whole cause chain, deepest cause first. */
		completeStackTrace
	}
	/**
	 * Creates a new instance from the content of another JSON (content copy).
	 * @param json the source JSON
	 */
	public CcpJsonRepresentation redoJson(CcpJsonRepresentation json) {
		CcpJsonRepresentation redo = new CcpJsonRepresentation(json.content);
		return redo;
	}
	
	/** The fields and values of the JSON. Unmodifiable, except for the empty JSON of {@link #getEmptyJson()}. */
	public final Map<String, Object> content;
	
	/** Creates an empty JSON backed by a mutable {@code HashMap}. */
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

	/**
	 * Reads the whole stream as text (platform default charset), joining the lines with {@code \n}.
	 * @param inputStream the stream to read
	 * @return the text read
	 */
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
 
	/**
	 * Parses a JSON object text with the registered {@code CcpJsonHandler}.
	 * @param json the text to parse
	 * @return the parsed map
	 * @throws CcpErrorJsonInvalid when the text is not a valid JSON object
	 */
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

	/**
	 * Builds the error details of an exception: {@code type} (class name), {@code message} ({@code ""} when null),
	 * {@code stackTrace} (one {@code File.method:line} entry per frame), {@code completeStackTrace} (the frames of the
	 * causes, deepest first, followed by the own frames) and {@code cause} (the details of the cause, or {@code ""}).
	 * @param e the exception; {@code null} produces an empty JSON
	 * @return the error details
	 */
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
		errorDetails = errorDetails.put(CcpStackTraceFields.completeStackTrace, completeStackTrace).put(CcpStackTraceFields.type, e.getClass().getName()).put(CcpStackTraceFields.stackTrace, stackTrace).put(CcpStackTraceFields.message, message).put(CcpStackTraceFields.cause, causeDetails);
		return errorDetails;
	}

	/**
	 * Returns the frames of the cause chain, deepest cause first, followed by the frames of the exception itself.
	 * @param e the exception; {@code null} produces an empty list
	 * @return the complete list of frames
	 */
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
	
	/**
	 * Returns the error details of the cause, or {@code ""} when there is no cause.
	 * @param cause the cause, possibly {@code null}
	 * @param stackTraceElements frames of the exception (unused)
	 * @return the details of the cause or an empty text
	 */
	@CcpAllowNullParameter
	private static Object getCauseDetails(Throwable cause, StackTraceElement[] stackTraceElements) {
		
		boolean hasCause = cause != null;
		
		if(hasCause) {
			CcpJsonRepresentation errorDetails = getErrorDetails(cause);
			return errorDetails;
		}
		return ""; 
	}

	/**
	 * Formats a frame as {@code FileWithoutExtension.method:line}, or {@code "-"} when the file name is unknown.
	 * @param stackTraceElement the frame
	 * @return the formatted frame
	 */
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

	/**
	 * Reads the field as a constant of the enum, through the enum's {@code valueOf}.
	 * @param <T> the enum type
	 * @param field the field holding the constant name
	 * @param clazz the enum class
	 * @return the enum constant
	 * @throws CcpErrorJsonInvalidFieldFormat when the value is absent or is not a constant of the enum
	 */
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
	
	
	/**
	 * Reads the field as a constant of the enum, returning the default value when the field is absent or blank.
	 * @param <T> the enum type
	 * @param field the field holding the constant name
	 * @param clazz the enum class
	 * @param defaultValue the value returned when the field is absent or blank
	 * @return the enum constant or the default value
	 * @throws CcpErrorJsonInvalidFieldFormat when the value is not a constant of the enum
	 */
	public <T> T getAsEnum(CcpJsonFieldName field, Class<T> clazz, T defaultValue){
		String asString = this.getAsString(field);
		
		boolean hasNoEnum = asString.trim().isEmpty();
		
		if(hasNoEnum) {
			return defaultValue;
		}
		T asEnum = this.getAsEnum(field, clazz);
		return asEnum;
	}
	
	/**
	 * Reads the field as {@code Long}; any numeric text is accepted and decimals are truncated ({@code "3.9"} gives 3).
	 * @param field the field to read
	 * @return the number
	 * @throws CcpErrorJsonInvalidFieldFormat when the field is absent or not numeric
	 */
	public Long getAsLongNumber(CcpJsonFieldName field) {
		Long asLongNumber = this.getAsLongNumber(field.getValue());
		return asLongNumber;
	}
	
	/**
	 * String-keyed variant of {@link #getAsLongNumber(CcpJsonFieldName)}.
	 * @param field the field name
	 * @return the number
	 */
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

	/**
	 * Reads the field as {@code Integer}; any numeric text is accepted and decimals are truncated.
	 * @param field the field to read
	 * @return the number
	 * @throws CcpErrorJsonInvalidFieldFormat when the field is absent or not numeric
	 */
	public Integer getAsIntegerNumber(CcpJsonFieldName field) {
		Integer asIntegerNumber = this.getAsIntegerNumber(field.getValue());
		return asIntegerNumber;
	}
	
	/**
	 * String-keyed variant of {@link #getAsIntegerNumber(CcpJsonFieldName)}.
	 * @param field the field name
	 * @return the number
	 */
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
	
	/**
	 * Returns a supplier that always supplies this JSON.
	 * @return the supplier
	 */
	public Supplier<CcpJsonRepresentation> getJsonSupplier(){
		Supplier<CcpJsonRepresentation> supplier = () -> this;
		return supplier;
	}
	
	/**
	 * Reads the field as boolean: {@code true} only when its text is {@code "true"} (case insensitive); an absent field
	 * gives {@code false}.
	 * @param field the field to read
	 * @return the boolean value
	 */
	public boolean getAsBoolean(CcpJsonFieldName field) {
		boolean asBoolean = this.getAsBoolean(field.getValue());
		return asBoolean;
	}
	
	/**
	 * String-keyed variant of {@link #getAsBoolean(CcpJsonFieldName)}.
	 * @param field the field name
	 * @return the boolean value
	 */
	private boolean getAsBoolean(String field) {
		String asString = this.getAsString(field);
		return Boolean.valueOf(asString.toLowerCase());
	}

	/**
	 * Reads the field as {@code Double}.
	 * @param field the field to read
	 * @return the number
	 * @throws CcpErrorJsonInvalidFieldFormat when the field is absent or not numeric
	 */
	public Double getAsDoubleNumber(CcpJsonFieldName field) {
		Double asDoubleNumber = this.getAsDoubleNumber(field.getValue());
		return asDoubleNumber;
	}
	
	/**
	 * String-keyed variant of {@link #getAsDoubleNumber(CcpJsonFieldName)}.
	 * @param field the field name
	 * @return the number
	 */
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
	
	/**
	 * Reads the field as text (see {@link #getAsString(CcpJsonFieldName)}) wrapped in a {@code CcpTextDecorator}.
	 * @param field the field to read
	 * @return the text decorator
	 */
	public CcpTextDecorator getAsTextDecorator(CcpJsonFieldName field) {
		CcpTextDecorator asTextDecorator = this.getAsTextDecorator(field.getValue());
		return asTextDecorator;
	}
	
	/**
	 * String-keyed variant of {@link #getAsTextDecorator(CcpJsonFieldName)}.
	 * @param field the field name
	 * @return the text decorator
	 */
	private CcpTextDecorator getAsTextDecorator(String field) {
		String asString = this.getAsString(field);
		CcpStringDecorator ccpStringDecorator = new CcpStringDecorator(asString);
		CcpTextDecorator text = ccpStringDecorator.text();
		return text;
	}

	/**
	 * Reads the field as text (see {@link #getAsString(CcpJsonFieldName)}) wrapped in a {@code CcpStringDecorator}.
	 * @param field the field to read
	 * @return the string decorator
	 */
	public CcpStringDecorator getAsStringDecorator(CcpJsonFieldName field) {
		CcpStringDecorator asStringDecorator = this.getAsStringDecorator(field.getValue());
		return asStringDecorator;
	}
	
	/**
	 * String-keyed variant of {@link #getAsStringDecorator(CcpJsonFieldName)}.
	 * @param field the field name
	 * @return the string decorator
	 */
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
	
	/**
	 * String-keyed variant of {@link #getAsString(CcpJsonFieldName)}: maps and JSONs become pretty JSON text; a list
	 * whose first item is a map or JSON becomes the {@code toString} of the list of JSONs; any other non-empty list becomes
	 * compact JSON; an empty list becomes {@code "[]"}; other values become {@code "" + value}.
	 * @param field the field name
	 * @return the text, or {@code ""} when absent or {@code null}
	 */
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
	/**
	 * Returns the field value, or the supplied default when the field is absent or {@code null}. The supplier is always
	 * called, even when the field is present.
	 * @param <T> the expected type
	 * @param field the field to read
	 * @param supplier supplier of the default value
	 * @return the value or the default
	 */
	public <T> T getOrDefault(CcpJsonFieldName field, Supplier<T> supplier) {
		T defaultValue = supplier.get();
		T orDefault = this.getOrDefault(field.getValue(), defaultValue);
		return orDefault;
	}

	
	/**
	 * String-keyed variant of {@link #getOrDefault(CcpJsonFieldName, Supplier)}.
	 * @param <T> the expected type
	 * @param field the field name
	 * @param defaultValue the default value
	 * @return the value or the default
	 */
	@SuppressWarnings("unchecked")
	private <T> T getOrDefault(String field, T defaultValue) {
		Object object = this.content.get(field);
		
		if(null == object) {
			return defaultValue;
		}
		
		return (T)object;
	}
	
	/**
	 * Returns a new JSON with only the given fields that are present, in the given order.
	 * @param fields the names of the fields to keep
	 * @return the partial JSON
	 */
	public CcpJsonRepresentation getJsonPiece(Collection<String> fields) {
		int size = fields.size();
		String[] array = fields.toArray(new String[size]);
		CcpJsonRepresentation jsonPiece = this.getJsonPiece(array);
		return jsonPiece;
	}	

	/**
	 * Returns a new JSON with only the given fields that are present, in the given order.
	 * @param fields the fields to keep
	 * @return the partial JSON
	 */
	public CcpJsonRepresentation getJsonPiece(CcpJsonFieldName... fields) {
		String[] fieldNames = this.getFields(fields);
		CcpJsonRepresentation jsonPiece = this.getJsonPiece(fieldNames);
		return jsonPiece;
	}
	
	/**
	 * String-keyed variant of {@link #getJsonPiece(CcpJsonFieldName...)}.
	 * @param fields the names of the fields to keep
	 * @return the partial JSON
	 */
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
	 * Serializes the JSON in compact format, with the top-level keys sorted alphabetically.
	 * @return the compact JSON text
	 */
	public String asUgglyJson() {
		
		CcpJsonHandler json = CcpDependencyInjection.getDependency(CcpJsonHandler.class);
		TreeMap<String, Object> sortedContent = new TreeMap<>(this.content);
		String uglyJson = json.toJson(sortedContent);
		return uglyJson;
		
	}

	/**
	 * Serializes the JSON with indentation and line breaks, keeping the field order.
	 * @return the pretty JSON text
	 */
	public String asPrettyJson() {
		CcpJsonHandler json = CcpDependencyInjection.getDependency(CcpJsonHandler.class);
		String asPrettyJson = json.asPrettyJson(this.content);
		return asPrettyJson;
	}
	
	
	/**
	 * Returns the pretty JSON with the top-level keys sorted, or {@code Map.toString()} when the JSON handler is not
	 * available.
	 * @return the textual representation
	 */
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
	 * Returns the names of the fields of the map (including fields whose value is {@code null}).
	 * @return the field names
	 */
	public Set<String> fieldSet(){
		Set<String> keySet = this.content.keySet();
		return keySet;
	}
	
	/**
	 * Returns a new JSON with the field set to the content wrapped by the decorator.
	 * @param field the field to set
	 * @param map the decorator whose content is stored
	 * @return the new JSON
	 */
	public CcpJsonRepresentation put(CcpJsonFieldName field, CcpDecorator<?> map) {
		CcpJsonRepresentation updatedJson = this.put(field.getValue(), map);
		return updatedJson;
	}
	
	/**
	 * String-keyed variant of {@link #put(CcpJsonFieldName, CcpDecorator)}.
	 * @param field the field name
	 * @param map the decorator whose content is stored
	 * @return the new JSON
	 */
	private CcpJsonRepresentation put(String field, CcpDecorator<?> map) {
		Object internalContent = map.getContent();
		CcpJsonRepresentation updatedJson = this.put(field, internalContent);
		return updatedJson;
	}
	
	/**
	 * Returns a new JSON with the field set to the list of the maps of the given JSONs.
	 * @param field the field to set
	 * @param list the JSONs to store
	 * @return the new JSON
	 */
	public CcpJsonRepresentation put(CcpJsonFieldName field, Collection<CcpJsonRepresentation> list) {
		CcpJsonRepresentation updatedJson = this.put(field.getValue(), list);
		return updatedJson;
	}
	
	/**
	 * String-keyed variant of {@link #put(CcpJsonFieldName, Collection)}.
	 * @param field the field name
	 * @param list the JSONs to store
	 * @return the new JSON
	 */
	private CcpJsonRepresentation put(String field, Collection<CcpJsonRepresentation> list) {
		List<Map<String, Object>> contents = list.stream().map(x -> x.content).collect(Collectors.toList());
		CcpJsonRepresentation updatedJson = this.put(field, contents);
		return updatedJson;
	}
	
	/**
	 * Applies an extractor function to this JSON and returns its result.
	 * @param <T> the type of the extracted information
	 * @param extractor the extractor function
	 * @return the extracted information
	 */
	public <T> T extractInformationFromJson(Function<CcpJsonRepresentation, T> extractor) {
		T information = extractor.apply(this);
		return information;
	}
	
	/**
	 * Runs the transformers in sequence through {@code execute} (input validation included), passing the result of one to
	 * the next.
	 * @param transformers the transformers to apply in order
	 * @return the result of the last transformer, or this JSON when there is none
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
	 * Returns a new JSON storing a {@code CcpBusiness} as the value of the field, for later use.
	 * @param field the field where the transformer is stored
	 * @param process the transformer to store
	 * @return the new JSON
	 */
	public CcpJsonRepresentation addJsonTransformer(CcpJsonFieldName field, CcpBusiness process) {
		CcpJsonRepresentation addJsonTransformer = this.addJsonTransformer(field.getValue(), process);
		return addJsonTransformer;
	}
	
	/**
	 * Returns a new JSON storing a {@code CcpBusiness} under the field named by the number.
	 * @param field the number used as field name
	 * @param process the transformer to store
	 * @return the new JSON
	 */
	public CcpJsonRepresentation addJsonTransformer(Integer field, CcpBusiness process) {
		CcpJsonRepresentation addJsonTransformer = this.addJsonTransformer("" + field, process);
		return addJsonTransformer;
	}
	/**
	 * String-keyed variant of {@link #addJsonTransformer(CcpJsonFieldName, CcpBusiness)}.
	 * @param field the field name
	 * @param process the transformer to store
	 * @return the new JSON
	 */
	private CcpJsonRepresentation addJsonTransformer(String field, CcpBusiness process) {
		CcpJsonRepresentation updatedJson = this.put(field, process);
		return updatedJson;
	}
	
	/**
	 * Returns a new JSON with the same value set in every given field.
	 * @param value the value to set
	 * @param fields the fields to fill
	 * @return the new JSON
	 */
	public CcpJsonRepresentation putSameValueInManyFields(Object value, CcpJsonFieldName... fields) {
		String[] fieldNames = this.getFields(fields);
		CcpJsonRepresentation putSameValueInManyFields = this.putSameValueInManyFields(value, fieldNames);
		return putSameValueInManyFields;
	}
	
	/**
	 * String-keyed variant of {@link #putSameValueInManyFields(Object, CcpJsonFieldName...)}.
	 * @param value the value to set
	 * @param fields the names of the fields to fill
	 * @return the new JSON
	 */
	private CcpJsonRepresentation putSameValueInManyFields(Object value, String... fields) {
		CcpJsonRepresentation json = this;
		
		for (String field : fields) {
			json = json.put(field, value);
		}
		
		return json;
	}
	
	/**
	 * Returns a new JSON with the field added or replaced, keeping the position of an existing field.
	 * @param field the field to add or replace
	 * @param value the value of the field
	 * @return the new JSON
	 */
	public CcpJsonRepresentation put(CcpJsonFieldName field, Object value) {
		CcpJsonRepresentation updatedJson = this.put(field.getValue(), value);
		return updatedJson;
	}

	/**
	 * Returns a new JSON with the map of the nested JSON as the value of the field.
	 * @param field the field where the nested JSON is stored
	 * @param value the nested JSON
	 * @return the new JSON
	 */
	public CcpJsonRepresentation put(CcpJsonFieldName field, CcpJsonRepresentation value) {
		CcpJsonRepresentation updatedJson = this.put(field.getValue(), value.content);
		return updatedJson;
	}
	
	/**
	 * Returns a new JSON whose field holds the field itself as value (convenient for self-describing fields).
	 * @param field the field whose value is itself
	 * @return the new JSON
	 */
	public CcpJsonRepresentation put(CcpJsonFieldName field) {
		CcpJsonRepresentation updatedJson = this.put(field, field);
		return updatedJson;
	}
	
	/**
	 * String-keyed variant of {@link #put(CcpJsonFieldName, Object)}.
	 * @param field the field name
	 * @param value the value of the field
	 * @return the new JSON
	 */
	private CcpJsonRepresentation put(String field, Object value) {
		Map<String, Object> content = new LinkedHashMap<>();
		content.putAll(this.content);
		content.put(field, value);
		CcpJsonRepresentation json = new CcpJsonRepresentation(content);
		return json;
	}  

	/**
	 * Returns a new JSON with the value of the source field copied into each target field; when the source field is
	 * absent, this JSON is returned unchanged.
	 * @param fieldToCopy the source field
	 * @param fieldsToPaste the target fields
	 * @return the new JSON
	 */
	public CcpJsonRepresentation duplicateValueFromField(CcpJsonFieldName fieldToCopy, CcpJsonFieldName... fieldsToPaste) {
		String[] fields = this.getFields(fieldsToPaste);
		CcpJsonRepresentation response = this.duplicateValueFromField(fieldToCopy.getValue(), fields);
		return response;
	}	

	/**
	 * String-keyed variant of {@link #duplicateValueFromField(CcpJsonFieldName, CcpJsonFieldName...)}.
	 * @param fieldToCopy the name of the source field
	 * @param fieldsToPaste the names of the target fields
	 * @return the new JSON
	 */
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
	 * Returns a new JSON with the value moved from {@code oldField} to {@code newField} (replacing any value of
	 * {@code newField}). When {@code oldField} is absent, the content is unchanged. The field order is not preserved.
	 * @param oldField the original field
	 * @param newField the new field
	 * @return the new JSON
	 */
	public CcpJsonRepresentation renameField(CcpJsonFieldName oldField, CcpJsonFieldName newField) {
		CcpJsonRepresentation renameField = this.renameField(oldField.getValue(), newField.getValue());
		return renameField;
	}
	
	/**
	 * String-keyed variant of {@link #renameField(CcpJsonFieldName, CcpJsonFieldName)}.
	 * @param oldField the name of the original field
	 * @param newField the name of the new field
	 * @return the new JSON
	 */
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
	
	/**
	 * Returns a new JSON without the field; the field order is not preserved.
	 * @param field the name of the field to remove
	 * @return the new JSON
	 */
	private CcpJsonRepresentation removeField(String field) {
		Map<String, Object> content = this.getContent();
		Map<String, Object> copy = new HashMap<>(content);
		copy.remove(field);
		CcpJsonRepresentation json = new CcpJsonRepresentation(copy);
		return json;
	}
	
	/**
	 * Returns a new JSON without the given fields; the field order is not preserved.
	 * @param fields the fields to remove
	 * @return the new JSON
	 */
	public CcpJsonRepresentation removeFields(CcpJsonFieldName... fields) {
		String[] fieldNames = this.getFields(fields);
		CcpJsonRepresentation removeFields = this.removeFields(fieldNames);
		return removeFields;
	}
	
	/**
	 * String-keyed variant of {@link #removeFields(CcpJsonFieldName...)}.
	 * @param fields the names of the fields to remove
	 * @return the new JSON
	 */
	private CcpJsonRepresentation removeFields(String... fields) {
		CcpJsonRepresentation json = this;
		for (String field : fields) {
			json = json.removeField(field);
		}
		return json;
	}

	/**
	 * Returns the internal map (unmodifiable, except for the empty JSON).
	 * @return the internal map
	 */
	public Map<String, Object> getContent() {
		return this.content;
	}

	/**
	 * Returns a new JSON with the same fields (a shallow copy).
	 * @return the copy
	 */
	public CcpJsonRepresentation copy() {
		CcpJsonRepresentation json = new CcpJsonRepresentation(this.getContent());
		return json;
	}
	
	/**
	 * Navigates a path of nested fields and returns the JSON found at its end, or an empty JSON when the path does not
	 * exist.
	 * @param paths the path of nested fields
	 * @return the nested JSON
	 */
	public CcpJsonRepresentation getInnerJsonFromPath(CcpJsonFieldName...paths) {
		String[] fields = this.getFields(paths);
		CcpJsonRepresentation innerJsonFromPath = this.getInnerJsonFromPath(fields);
		return innerJsonFromPath;
	}
	
	/**
	 * String-keyed variant of {@link #getInnerJsonFromPath(CcpJsonFieldName...)}.
	 * @param paths the names of the nested fields
	 * @return the nested JSON
	 */
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
	 * Navigates the path and returns the value at its end; returns {@code defaultValue} when any field of the path is
	 * absent. When the last value is a JSON (map or JSON text), it is returned as {@code CcpJsonRepresentation}.
	 * @param <T> the expected type
	 * @param defaultValue the value returned when the path does not exist
	 * @param paths the path of nested fields
	 * @return the value found or the default
	 * @throws CcpErrorJsonPathIsMissing when no path is given
	 */
	public <T>T getValueFromPath(T defaultValue, CcpJsonFieldName... paths){
		String[] fields = this.getFields(paths);
		T valueFromPath = this.getValueFromPath(defaultValue, fields);
		return valueFromPath;	
	}
	
	/**
	 * Runs {@code conditionsMet} when the condition holds for this JSON, otherwise {@code conditionsDoNotMet}.
	 * @param condition the condition
	 * @param conditionsMet business run when the condition holds
	 * @param conditionsDoNotMet business run otherwise
	 * @return the result of the business that ran
	 */
	@SuppressWarnings("unchecked")
	public CcpJsonRepresentation getTransformedJsonExecutingIfAndElse(Predicate<CcpJsonRepresentation> condition, CcpBusiness conditionsMet, CcpBusiness conditionsDoNotMet) {
		CcpJsonRepresentation transformedJsonWhenAllConditionsMatch = getTransformedJsonWhenAllConditionsMatch(conditionsMet, conditionsDoNotMet, condition);
		return transformedJsonWhenAllConditionsMatch;
	}
	
	/**
	 * Runs {@code conditionsMet} when every condition holds for this JSON (or there is none), otherwise
	 * {@code conditionsDoNotMet}. Evaluation stops at the first failed condition.
	 * @param conditionsMet business run when all conditions hold
	 * @param conditionsDoNotMet business run otherwise
	 * @param conditions the conditions
	 * @return the result of the business that ran
	 */
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
	
	/**
	 * Runs {@code conditionsMet} when at least one condition holds for this JSON, otherwise {@code conditionsDoNotMet}
	 * (also when there is no condition). Evaluation stops at the first condition that holds.
	 * @param conditionsMet business run when any condition holds
	 * @param conditionsDoNotMet business run otherwise
	 * @param conditions the conditions
	 * @return the result of the business that ran
	 */
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
	
	/**
	 * String-keyed variant of {@link #getValueFromPath(Object, CcpJsonFieldName...)}. Each step descends into the field
	 * when its value is a JSON; when a value in the middle of the path is not a JSON, the next field is looked up in the
	 * same level.
	 * @param <T> the expected type
	 * @param defaultValue the value returned when the path does not exist
	 * @param paths the names of the nested fields
	 * @return the value found or the default
	 */
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


	/**
	 * Navigates the path and returns the list found at its end as JSONs; items may be maps, JSONs or JSON texts. An
	 * absent path gives an empty list.
	 * @param paths the path of nested fields
	 * @return the list of JSONs
	 * @throws CCpErrorJsonFieldIsNotValidJsonList when an item is not a JSON
	 */
	public List<CcpJsonRepresentation> getInnerJsonListFromPath(CcpJsonFieldName...paths) {
		String[] fields = this.getFields(paths);
		List<CcpJsonRepresentation> innerJsonListFromPath = this.getInnerJsonListFromPath(fields);
		return innerJsonListFromPath;
	}
	
	/**
	 * String-keyed variant of {@link #getInnerJsonListFromPath(CcpJsonFieldName...)}.
	 * @param paths the names of the nested fields
	 * @return the list of JSONs
	 */
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

	/**
	 * Returns the nested JSON held by the field (a map, a JSON or a JSON text); an absent field or a value of another type
	 * gives an empty JSON.
	 * @param field the field holding the nested JSON
	 * @return the nested JSON
	 * @throws CcpErrorJsonInvalid when the value is a text that is not a JSON object
	 */
	public CcpJsonRepresentation getInnerJson(CcpJsonFieldName field) {
		CcpJsonRepresentation innerJson = this.getInnerJson(field.getValue());
		return innerJson;
	}
	
	/**
	 * String-keyed variant of {@link #getInnerJson(CcpJsonFieldName)}.
	 * @param field the field name
	 * @return the nested JSON
	 */
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

	
	/**
	 * Returns the list held by the field as JSONs. The value may be a collection of maps or JSONs, or a text holding a
	 * JSON list; an absent field, a text that is not a JSON list, or a value of another type gives an empty list.
	 * @param field the field holding the list
	 * @return the list of JSONs
	 */
	public List<CcpJsonRepresentation> getAsJsonList(CcpJsonFieldName field) {
		List<CcpJsonRepresentation> asJsonList = this.getAsJsonList(field.getValue());
		return asJsonList;
	}
	
	/**
	 * String-keyed variant of {@link #getAsJsonList(CcpJsonFieldName)}.
	 * @param field the field name
	 * @return the list of JSONs
	 */
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

	/**
	 * Returns the items of the field, as texts, wrapped in a {@code CcpCollectionDecorator}.
	 * @param field the field name
	 * @return the collection decorator
	 */
	public CcpCollectionDecorator getAsCollectionDecorator(String field){
		List<String> asStringList = this.getAsStringList(field);
		Object[] array = asStringList.toArray(new String[asStringList.size()]);
		CcpCollectionDecorator collectionDecorator = new CcpCollectionDecorator(array);
		return collectionDecorator;
	}
	
	/**
	 * Returns the items of the first given field that holds a non-empty list, as texts ({@code null} items are skipped);
	 * see {@link #getAsObjectList(CcpJsonFieldName)} for the accepted formats.
	 * @param fields the fields tried in order
	 * @return the texts, or an empty list when none of the fields has items
	 */
	public List<String> getAsStringList(CcpJsonFieldName... fields){
		String[] fieldNames = this.getFields(fields);
		List<String> asStringList = this.getAsStringList(fieldNames);
		return asStringList;
	}
	
	/**
	 * Array variant of {@link #getAsStringList(CcpJsonFieldName...)}.
	 * @param fields the fields tried in order
	 * @return the texts
	 */
	public String[] getAsStringArray(CcpJsonFieldName... fields) {
		List<String> asStringList = this.getAsStringList(fields);
		String[] array = asStringList.toArray(new String[asStringList.size()]);
		return array;
	}
	
	/**
	 * String-keyed variant of {@link #getAsStringList(CcpJsonFieldName...)}.
	 * @param fields the names of the fields tried in order
	 * @return the texts
	 */
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
	
	/**
	 * Returns the items held by the field: an array or a collection is copied; a text holding a JSON list is parsed; any
	 * other non-blank value becomes a single-item list with its text; an absent or blank field gives an empty list.
	 * @param field the field to read
	 * @return a new list with the items
	 */
	public List<Object> getAsObjectList(CcpJsonFieldName field) {
		List<Object> asObjectList = this.getAsObjectList(field.getValue());
		return asObjectList;
	}
	
	/**
	 * String-keyed variant of {@link #getAsObjectList(CcpJsonFieldName)}.
	 * @param field the field name
	 * @return a new list with the items
	 */
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
	
	/**
	 * Returns a new JSON with the fields of this JSON plus the fields of the map; on conflicting keys the map wins.
	 * @param map the fields to add
	 * @return the merged JSON
	 */
	public CcpJsonRepresentation mergeWithAnotherJson(Map<String, Object> map) {
		Map<String, Object> currentContent = this.getContent();
		Map<String, Object> content = new LinkedHashMap<>(currentContent);
		content.putAll(map);
		CcpJsonRepresentation mergedJson = new CcpJsonRepresentation(content);
		return mergedJson;
	}

	/**
	 * Returns a new JSON with the fields of this JSON plus the fields of the other; on conflicting keys the other wins.
	 * @param json the JSON whose fields are added
	 * @return the merged JSON
	 */
	public CcpJsonRepresentation mergeWithAnotherJson(CcpJsonRepresentation json) {
		CcpJsonRepresentation mergedJson = this.mergeWithAnotherJson(json.content);
		return mergedJson;
	}
	
	
	/**
	 * Tells whether the field is present with a non-null value.
	 * @param field the field
	 * @return {@code true} when the field has a value
	 */
	public boolean containsField(CcpJsonFieldName field) {
		boolean containsField = this.containsField(field.getValue());
		return containsField;
	}

	/**
	 * String-keyed variant of {@link #containsField(CcpJsonFieldName)}.
	 * @param field the field name
	 * @return {@code true} when the field has a value
	 */
	private boolean containsField(String field) {

		Object object = this.content.get(field);
		
		boolean containsKey = object != null;
		return containsKey;
	}

	/**
	 * Tells whether every given field has a non-null value ({@code true} for an empty collection).
	 * @param fields the field names
	 * @return {@code true} when all fields are present
	 */
	public boolean containsAllFields(Collection<String> fields) {
		String[] array = this.toArray(fields);
		boolean containsAllFields = this.containsAllFields(array);
		return containsAllFields;
	}

	/**
	 * Tells whether at least one of the given fields has a non-null value ({@code false} for an empty collection).
	 * @param fields the field names
	 * @return {@code true} when any field is present
	 */
	public boolean containsAnyFields(Collection<String> fields) {
		String[] array = this.toArray(fields);
		boolean containsAnyFields = this.containsAnyFields(array);
		return containsAnyFields;
	}

	/**
	 * Converts the collection of field names into an array.
	 * @param fields the field names
	 * @return the array
	 */
	private String[] toArray(Collection<String> fields) {
		int size = fields.size();
		String[] emptyArray = new String[size];
		String[] array = fields.toArray(emptyArray);
		return array;
	}	

	/**
	 * Tells whether every given field has a non-null value ({@code true} when no field is given).
	 * @param fields the fields
	 * @return {@code true} when all fields are present
	 */
	public boolean containsAllFields(CcpJsonFieldName... fields) {
		String[] fieldNames = this.getFields(fields);
		boolean containsAllFields = this.containsAllFields(fieldNames);
		return containsAllFields;
	}
	
	/**
	 * String-keyed variant of {@link #containsAllFields(CcpJsonFieldName...)}.
	 * @param fields the field names
	 * @return {@code true} when all fields are present
	 */
	private boolean containsAllFields(String... fields) {
		boolean containsFields = this.containsFields(false, fields);
		return containsFields; 
	}
	
	/**
	 * Tells whether at least one of the given fields has a non-null value ({@code false} when no field is given).
	 * @param fields the fields
	 * @return {@code true} when any field is present
	 */
	public boolean containsAnyFields(CcpJsonFieldName... fields) {
		String[] fieldNames = this.getFields(fields);
		boolean containsAnyFields = this.containsAnyFields(fieldNames);
		return containsAnyFields;
	}
	
	/**
	 * String-keyed variant of {@link #containsAnyFields(CcpJsonFieldName...)}.
	 * @param fields the field names
	 * @return {@code true} when any field is present
	 */
	private boolean containsAnyFields(String... fields) {
		boolean containsFields = this.containsFields(true, fields);
		return containsFields;
	}

	/**
	 * Shared implementation of "all" ({@code assertion == false}) and "any" ({@code assertion == true}): returns
	 * {@code assertion} at the first field whose presence equals it, otherwise its negation.
	 * @param assertion {@code true} for "any", {@code false} for "all"
	 * @param fields the field names
	 * @return the result of the check
	 */
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
	
	/**
	 * Returns the value of a mandatory field.
	 * @param field the field
	 * @return the value
	 * @throws CcpErrorJsonFieldNotFound when the field is absent or {@code null}
	 */
	public Object get(CcpJsonFieldName field) {
		Object object = this.get(field.getValue());
		return object;
	}
	
	/**
	 * String-keyed variant of {@link #get(CcpJsonFieldName)}.
	 * @param field the field name
	 * @return the value
	 */
	private Object get(String field) {
		Object object = this.content.get(field);
		boolean valueIsAbsent = object == null;
		if(valueIsAbsent) {
			throw new CcpErrorJsonFieldNotFound(field, this);
		}
		return object;
	}

	/**
	 * Returns the value of the first given field that is present, cast to the expected type.
	 * @param <T> the expected type
	 * @param fields the fields tried in order
	 * @return the first value found
	 * @throws CcpErrorJsonFieldNotFound when none of the fields is present
	 */
	public <T> T getAsObject(CcpJsonFieldName... fields) {
		String[] fieldNames = this.getFields(fields);
		T asObject = this.getAsObject(fieldNames);
		return asObject;
	}
	
	/**
	 * String-keyed variant of {@link #getAsObject(CcpJsonFieldName...)}.
	 * @param <T> the expected type
	 * @param fields the field names tried in order
	 * @return the first value found
	 */
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
	
	/**
	 * Tells whether the JSON has no field.
	 * @return {@code true} when the map is empty
	 */
	public boolean isEmpty() {
		boolean empty = this.content.isEmpty();
		return empty;
	}

	/**
	 * Returns a new JSON with the values appended to the list held by the field (see
	 * {@link #getAsObjectList(CcpJsonFieldName)}); an absent field starts an empty list.
	 * @param field the field holding the list
	 * @param values the values to append
	 * @return the new JSON
	 */
	public CcpJsonRepresentation addToList(CcpJsonFieldName field, Object... values) {
		CcpJsonRepresentation addToList = this.addToList(field.getValue(), values);
		return addToList;
	}

	/**
	 * String-keyed variant of {@link #addToList(CcpJsonFieldName, Object...)}.
	 * @param field the field name
	 * @param values the values to append
	 * @return the new JSON
	 */
	private CcpJsonRepresentation addToList(String field, Object... values) {
		CcpJsonRepresentation result = this;
		for (Object value : values) {
			result = result.addToList(field, value);
		}
		return result;
	}
	
	/**
	 * Returns a new JSON with one value appended to the list held by the field.
	 * @param field the field name
	 * @param value the value to append
	 * @return the new JSON
	 */
	private CcpJsonRepresentation addToList(String field, Object value) {
		List<Object> list = this.getAsObjectList(field);
		list = new ArrayList<>(list);
		list.add(value);
		CcpJsonRepresentation updatedJson = this.put(field, list);
		return updatedJson;
	}

	/**
	 * Returns a new JSON with the map of the given JSON appended to the list held by the field.
	 * @param field the field holding the list
	 * @param value the JSON to append
	 * @return the new JSON
	 */
	public CcpJsonRepresentation addToList(CcpJsonFieldName field, CcpJsonRepresentation value) {
		CcpJsonRepresentation addToList = this.addToList(field.getValue(), value);
		return addToList;
	}
	
	/**
	 * String-keyed variant of {@link #addToList(CcpJsonFieldName, CcpJsonRepresentation)}.
	 * @param field the field name
	 * @param value the JSON to append
	 * @return the new JSON
	 */
	private CcpJsonRepresentation addToList(String field, CcpJsonRepresentation value) {
		List<Object> list = this.getAsObjectList(field);
		list = new ArrayList<>(list);
		list.add(value.content);
		CcpJsonRepresentation updatedJson = this.put(field, list);
		return updatedJson;
	}
	
	/**
	 * Returns a new JSON whose nested JSON in {@code field} has {@code subField} set to the value (an absent nested JSON
	 * starts empty).
	 * @param field the field holding the nested JSON
	 * @param subField the field set inside the nested JSON
	 * @param value the value to set
	 * @return the new JSON
	 */
	public CcpJsonRepresentation addToItem(CcpJsonFieldName field, CcpJsonFieldName subField, Object value) {
		CcpJsonRepresentation addToItem = this.addToItem(field.getValue(), subField.getValue(), value);
		return addToItem;
	}
	
	/**
	 * String-keyed variant of {@link #addToItem(CcpJsonFieldName, CcpJsonFieldName, Object)}.
	 * @param field the field name
	 * @param subField the nested field name
	 * @param value the value to set
	 * @return the new JSON
	 */
	private CcpJsonRepresentation addToItem(String field, String subField, Object value) {
		CcpJsonRepresentation itemAsMap = this.getInnerJson(field);
		itemAsMap = itemAsMap.put(subField, value);
		
		CcpJsonRepresentation updatedJson = this.put(field, itemAsMap.content);
		return updatedJson;
	}

	/**
	 * Returns a new JSON whose nested JSON in {@code field} has {@code subField} set to the map of the given JSON.
	 * @param field the field holding the nested JSON
	 * @param subField the field set inside the nested JSON
	 * @param value the JSON to set
	 * @return the new JSON
	 */
	public CcpJsonRepresentation addToItem(CcpJsonFieldName field, CcpJsonFieldName subField, CcpJsonRepresentation value) {
		CcpJsonRepresentation addToItem = this.addToItem(field.getValue(), subField.getValue(), value);
		return addToItem;
	}
	
	/**
	 * String-keyed variant of {@link #addToItem(CcpJsonFieldName, CcpJsonFieldName, CcpJsonRepresentation)}.
	 * @param field the field name
	 * @param subField the nested field name
	 * @param value the JSON to set
	 * @return the new JSON
	 */
	private CcpJsonRepresentation addToItem(String field, String subField, CcpJsonRepresentation value) {
		CcpJsonRepresentation itemAsMap = this.getInnerJson(field);
		itemAsMap = itemAsMap.put(subField, value.content);
		
		CcpJsonRepresentation updatedJson = this.put(field, itemAsMap.content);
		return updatedJson;
	}

	/**
	 * Copies the value of {@code fieldToCopy} into {@code fieldToPaste} only when the latter is absent.
	 * @param fieldToCopy the source field
	 * @param fieldToPaste the target field
	 * @return the new JSON, or this JSON when the target is already present or the source is absent
	 */
	public CcpJsonRepresentation copyIfNotContains(CcpJsonFieldName fieldToCopy, CcpJsonFieldName fieldToPaste) {
		CcpJsonRepresentation copyIfNotContains = this.copyIfNotContains(fieldToCopy.getValue(), fieldToPaste.getValue());
		return copyIfNotContains;
	}
	
	/**
	 * String-keyed variant of {@link #copyIfNotContains(CcpJsonFieldName, CcpJsonFieldName)}.
	 * @param fieldToCopy the name of the source field
	 * @param fieldToPaste the name of the target field
	 * @return the new JSON
	 */
	private CcpJsonRepresentation copyIfNotContains(String fieldToCopy, String fieldToPaste) {

		boolean containsAllFields = this.containsAllFields(fieldToPaste);
		
		if(containsAllFields) {
			return this;
		}
	
		CcpJsonRepresentation duplicateValueFromField = this.duplicateValueFromField(fieldToCopy, fieldToPaste);
		
		return duplicateValueFromField;
	}		
	
	/**
	 * Sets the field only when it is absent.
	 * @param field the field
	 * @param value the value
	 * @return the new JSON, or this JSON when the field is already present
	 */
	public CcpJsonRepresentation putIfNotContains(CcpJsonFieldName field, Object value) {
	
		CcpJsonRepresentation putIfNotContains = this.putIfNotContains(field.getValue(), value);
		return putIfNotContains;
	}
	
	/**
	 * String-keyed variant of {@link #putIfNotContains(CcpJsonFieldName, Object)}.
	 * @param field the field name
	 * @param value the value
	 * @return the new JSON
	 */
	private CcpJsonRepresentation putIfNotContains(String field, Object value) {
		boolean containsAllFields = this.containsAllFields(field);
		
		if(containsAllFields) {
			return this;
		}
		
		CcpJsonRepresentation updatedJson = this.put(field, value);
		return updatedJson;
	}
	
	/**
	 * Wraps the list held by the field in a {@code CcpCollectionDecorator}, to inspect its items.
	 * @param field the field holding the list
	 * @return the collection decorator
	 */
	public CcpCollectionDecorator getAsArrayMetadata(CcpJsonFieldName field) {
		CcpCollectionDecorator asArrayMetadata = this.getAsArrayMetadata(field.getValue());
		return asArrayMetadata;
	}
	
	/**
	 * String-keyed variant of {@link #getAsArrayMetadata(CcpJsonFieldName)}.
	 * @param field the field name
	 * @return the collection decorator
	 */
	private CcpCollectionDecorator getAsArrayMetadata(String field) {
		CcpCollectionDecorator collectionDecorator = new CcpCollectionDecorator(this, field);
		return collectionDecorator;
	}
	
	/**
	 * Returns a UTF-8 stream over the compact JSON ({@link #asUgglyJson()}).
	 * @return the stream
	 */
	public InputStream toInputStream() {
		String asUgglyJson = this.asUgglyJson();
		byte[] bytes = asUgglyJson.getBytes(StandardCharsets.UTF_8);
		InputStream stream = new ByteArrayInputStream(bytes);
		return stream;
	}
	
	/**
	 * Hashes the compact JSON with sorted top-level keys ({@link #asUgglyJson()}) with the given algorithm, despite the
	 * name.
	 * @param algorithm the hash algorithm
	 * @return the hexadecimal hash
	 */
	public String getSha1Hash(CcpHashAlgorithm algorithm) {
		String asUgglyJson = this.asUgglyJson();
		String hash = new CcpStringDecorator(asUgglyJson).hash().asString(algorithm);
		return hash;
	}

	/**
	 * Hash code consistent with {@link #equals(Object)}: derived from the SHA-1 of the compact JSON.
	 * @return the hash code
	 */
	public int hashCode() {
		String hash = this.getSha1Hash(CcpHashAlgorithm.SHA1);
		int hashCode = hash.hashCode();
		return hashCode;
	}
	
	/**
	 * Two JSONs are equal when their compact JSON texts with sorted top-level keys have the same SHA-1, i.e. same fields
	 * and values regardless of the top-level order.
	 * @param obj the other object
	 * @return {@code true} when both JSONs have the same content
	 */
	public boolean equals(Object obj) {
		
		if(obj instanceof CcpJsonRepresentation other) {
			String otherHash = other.getSha1Hash(CcpHashAlgorithm.SHA1);
			String thisHash = this.getSha1Hash(CcpHashAlgorithm.SHA1);
			boolean equals = otherHash.equals(thisHash);
			return equals;
		}
		
		return false;
	}
	
	/**
	 * List variant of {@link #getTransformedJson(CcpBusiness...)}.
	 * @param jsonTransformers the transformers to apply in order
	 * @return the result of the last transformer
	 */
	public CcpJsonRepresentation getTransformedJson(
			List<CcpBusiness> jsonTransformers) {
		
		CcpBusiness[] array = jsonTransformers.toArray(new CcpBusiness[jsonTransformers.size()]);
		CcpJsonRepresentation transformedJson = this.getTransformedJson(array);
		return transformedJson;
	}

	/**
	 * Tells whether the text of the field is a valid JSON object; an absent or blank field gives {@code false}.
	 * @param fieldName the field
	 * @return {@code true} when the field holds a JSON object
	 */
	public boolean isInnerJson(CcpJsonFieldName fieldName) {
		boolean innerJson = this.isInnerJson(fieldName.getValue());
		return innerJson;
	}
	
	/**
	 * String-keyed variant of {@link #isInnerJson(CcpJsonFieldName)}.
	 * @param fieldName the field name
	 * @return {@code true} when the field holds a JSON object
	 */
	private boolean isInnerJson(String fieldName) {
		CcpJsonHandler handler = CcpDependencyInjection.getDependency(CcpJsonHandler.class);
		String asString = this.getAsString(fieldName);
		if(asString.trim().isEmpty()) {
			return false;
		}
		boolean validJson = handler.isValidJson(asString);
		return validJson;
	}
	
	/**
	 * Converts field names into the keys used in the map.
	 * @param enumItems the field names
	 * @return the keys
	 */
	private String[] getFields(CcpJsonFieldName... enumItems) {
		int k = 0;
		String[] array = new String[enumItems.length];
		for (CcpJsonFieldName enumItem : enumItems) {
			array[k++] = enumItem.getValue();
		}
		return array;
	}

	/**
	 * Navigates two levels: the field and, inside it, the nested field whose name is {@code value}.
	 * @param fieldName the first field of the path
	 * @param value the name of the nested field
	 * @return the nested JSON, or an empty JSON when the path does not exist
	 */
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



	/**
	 * Reads the items of the field (see {@link #getAsStringList(CcpJsonFieldName...)}) as constants of the enum.
	 * @param <T> the enum type
	 * @param field the field holding the constant names
	 * @param enumClass the enum class
	 * @return the enum constants
	 * @throws RuntimeException wrapping the reflection error when an item is not a constant of the enum
	 */
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