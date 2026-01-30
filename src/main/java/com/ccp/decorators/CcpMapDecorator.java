package com.ccp.decorators;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.ccp.business.CcpBusiness;
import com.ccp.validations.CcpItIsTrueThatTheFollowingFields;

public interface CcpMapDecorator<Type> extends CcpDecorator<Map<String, Object>>{

	Long getAsLongNumber(Type field);

	Integer getAsIntegerNumber(Type field);

	boolean getAsBoolean(Type field);

	Double getAsDoubleNumber(Type field);

	CcpJsonRepresentation putFilledTemplate(Type fieldToSearch, Type fieldToPut);

	CcpTextDecorator getAsTextDecorator(Type field);

	CcpStringDecorator getAsStringDecorator(Type field);

	@SuppressWarnings("unchecked")
	CcpJsonRepresentation whenFieldsAreNotFound(CcpBusiness business, Type... fields);

	String getAsString(Type field);

	<T> T getOrDefault(Type field, T defaultValue);

	CcpJsonRepresentation getJsonPiece(Collection<String> fields);

	@SuppressWarnings("unchecked")
	CcpJsonRepresentation getJsonPiece(Type... fields);

	String toString();

	CcpJsonRepresentation put(Type field, CcpDecorator<?> map);

	CcpJsonRepresentation put(Type field, Collection<CcpJsonRepresentation> list);

	CcpJsonRepresentation getTransformedJsonIfFoundTheField(Type field, CcpBusiness... transformers);

	CcpJsonRepresentation addJsonTransformer(Type field, CcpBusiness process);

	@SuppressWarnings("unchecked")
	CcpJsonRepresentation putSameValueInManyFields(Object value, Type... fields);

	CcpJsonRepresentation put(Type field, Object value);

	@SuppressWarnings("unchecked")
	CcpJsonRepresentation duplicateValueFromField(Type fieldToCopy, Type... fieldsToPaste);

	CcpJsonRepresentation renameField(Type oldField, Type newField);

	CcpJsonRepresentation removeField(Type field);

	@SuppressWarnings("unchecked")
	CcpJsonRepresentation removeFields(Type... fields);

	@SuppressWarnings("unchecked")
	CcpJsonRepresentation getInnerJsonFromPath(Type... paths);

	@SuppressWarnings("unchecked")
	<T> T getValueFromPath(T defaultValue, Type... paths);

	@SuppressWarnings("unchecked")
	List<CcpJsonRepresentation> getInnerJsonListFromPath(Type... paths);

	CcpJsonRepresentation getInnerJson(Type field);

	List<CcpJsonRepresentation> getAsJsonList(Type field);

	CcpCollectionDecorator getAsCollectionDecorator(String field);

	@SuppressWarnings("unchecked")
	List<String> getAsStringList(Type... fields);

	List<Object> getAsObjectList(Type field);

	boolean containsField(Type field);

	boolean containsAllFields(Collection<String> fields);

	@SuppressWarnings("unchecked")
	boolean containsAllFields(Type... fields);

	@SuppressWarnings("unchecked")
	boolean containsAnyFields(Type... fields);

	Object get(Type field);

	@SuppressWarnings("unchecked")
	<T> T getAsObject(Type... fields);

	CcpJsonRepresentation addToList(Type field, Object... values);

	CcpJsonRepresentation addToList(Type field, CcpJsonRepresentation value);

	CcpJsonRepresentation addToItem(Type field, Type subField, Object value);

	CcpJsonRepresentation addToItem(Type field, Type subField, CcpJsonRepresentation value);

	CcpJsonRepresentation copyIfNotContains(Type fieldToCopy, Type fieldToPaste);

	CcpJsonRepresentation putIfNotContains(Type field, Object value);

	CcpCollectionDecorator getAsArrayMetadata(Type field);

	@SuppressWarnings("unchecked")
	CcpItIsTrueThatTheFollowingFields itIsTrueThatTheFollowingFields(Type... fields);

	Set<String> getMissingFields(Collection<String> fields);

	int hashCode();

	boolean equals(Object obj);

	boolean isInnerJson(Type fieldName);

	CcpJsonRepresentation addToItem(Type field, String subField, CcpJsonRepresentation innerJson);

	CcpJsonRepresentation addToItem(Type statis, String field, Object avg);

	CcpJsonRepresentation getInnerJsonFromPath(Type fieldName, String value);
}