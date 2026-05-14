package com.ccp.decorators;

import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

import com.ccp.business.CcpBusiness;
import com.ccp.validations.CcpItIsTrueThatTheFollowingFields;

public interface CcpMapDecorator<Type> extends CcpDecorator<Map<String, Object>>{

	Long getAsLongNumber(Type field);

	Integer getAsIntegerNumber(Type field);

	boolean getAsBoolean(Type field);

	Double getAsDoubleNumber(Type field);

	CcpTextDecorator getAsTextDecorator(Type field);
	
	CcpJsonRepresentation putFilledTemplate(Type fieldToSearch, Type fieldToPut);

	CcpStringDecorator getAsStringDecorator(Type field);

	@SuppressWarnings("unchecked")
	CcpJsonRepresentation whenFieldsAreNotFound(CcpBusiness business, Type... fields);

	@SuppressWarnings("unchecked")
	CcpJsonRepresentation whenAnyFieldsAreFound(CcpBusiness business, Type... fields);

	@SuppressWarnings("unchecked")
	CcpJsonRepresentation whenAllFieldsAreFound(CcpBusiness business, Type... fields);

	String getAsString(Type field);

	CcpJsonRepresentation getJsonPiece(Collection<String> fields);

	@SuppressWarnings("unchecked")
	CcpJsonRepresentation getJsonPiece(Type... fields);

	CcpJsonRepresentation put(Type field, CcpDecorator<?> map);

	CcpJsonRepresentation put(Type field, Collection<CcpJsonRepresentation> list);

	CcpJsonRepresentation addJsonTransformer(Type field, CcpBusiness process);

	@SuppressWarnings("unchecked")
	CcpJsonRepresentation putSameValueInManyFields(Object value, Type... fields);

	CcpJsonRepresentation put(Type field, Object value);

	@SuppressWarnings("unchecked")
	CcpJsonRepresentation duplicateValueFromField(Type fieldToCopy, Type... fieldsToPaste);

	CcpJsonRepresentation renameField(Type oldField, Type newField);

	@SuppressWarnings("unchecked")
	CcpJsonRepresentation removeFields(Type... fields);

	@SuppressWarnings("unchecked")
	CcpJsonRepresentation getInnerJsonFromPath(Type... paths);

	@SuppressWarnings("unchecked")
	<T> T getValueFromPath(T defaultValue, Type... paths);

	@SuppressWarnings("unchecked")
	List<CcpJsonRepresentation> getInnerJsonListFromPath(Type... paths);

	CcpJsonRepresentation getInnerJson(Type field);//TODO REMOVER

	List<CcpJsonRepresentation> getAsJsonList(Type field);

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

	boolean isInnerJson(Type fieldName);

	CcpJsonRepresentation getInnerJsonFromPath(Type fieldName, String value);
	
	<T> T getOrDefault(Type field, Supplier<T> supplier);
}