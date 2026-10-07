package com.ccp.especifications.http;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.ccp.constants.CcpOtherConstants;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpStringDecorator;
import com.ccp.decorators.CcpTextDecorator;
import com.ccp.decorators.CcpJsonFieldName;
import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.json.CcpJsonHandler;
/**
 * Immutable HTTP response: body, status and the equivalent curl command, with readers of the body in several formats
 * and checks of the status range.
 */
public class CcpHttpResponse {
	/** Fields of the textual form of the response. */
	enum JsonFieldNames implements CcpJsonFieldName{
		/** The body. */
		httpResponse,
		/** The status. */
		httpStatus
	}

	/** The response body. */
	public final String httpResponse;
	/** The HTTP status. */
	public final int httpStatus;
	/** The equivalent curl command, for debugging. */
	public final String curl;
	
	
	/**
	 * Reads the body from the stream (platform default charset).
	 * @param httpResponse the body stream
	 * @param httpStatus the HTTP status
	 * @param curl the equivalent curl command
	 */
	public CcpHttpResponse(InputStream httpResponse, int httpStatus, String curl) {
		this(new CcpStringDecorator(httpResponse).content, httpStatus, curl);
	}

	/**
	 * Builds the response.
	 * @param httpResponse the body
	 * @param httpStatus the HTTP status
	 * @param curl the equivalent curl command
	 */
	public CcpHttpResponse(String httpResponse, int httpStatus, String curl) {
		this.httpResponse = httpResponse;
		this.httpStatus = httpStatus;
		this.curl = curl;
	}
	
	/**
	 * Tells whether the body is a valid JSON object; an empty body counts as valid.
	 * @return {@code true} for a JSON object or an empty body
	 */
	public boolean isValidSingleJson() {
		String httpResponseTrim = this.httpResponse.trim();
		boolean httpResponseTrimEmpty = httpResponseTrim.isEmpty();
		if(httpResponseTrimEmpty) {
			return true;
		}
		CcpStringDecorator ccpStringDecorator = new CcpStringDecorator(this.httpResponse);
		CcpTextDecorator text = ccpStringDecorator.text();
		boolean validSingleJson = text.isValidSingleJson();
		return validSingleJson;
	}
	
	/**
	 * Parses the body as a JSON object.
	 * @return the JSON, or an empty JSON when the body is not a JSON object
	 */
	public CcpJsonRepresentation asSingleJson() {
		try {
			CcpStringDecorator ccpStringDecorator2 = new CcpStringDecorator(this.httpResponse);
			CcpJsonRepresentation json2 = ccpStringDecorator2.json();
			return json2;
		} catch (Exception e) {
			return CcpOtherConstants.EMPTY_JSON;
		}
	}
	
	/**
	 * Parses the body as a JSON list of objects, each one turned into a {@code CcpJsonRepresentation}; a blank body is an
	 * empty list. Until 2026-10-07 the items were the maps produced by the JSON handler, despite the declared type, and
	 * the first read of an item raised a {@code ClassCastException} far from here.
	 * @return the records of the list
	 * @throws CcpErrorHttpResponseIsNotListOfRecords when the body is not a JSON list or has an item that is not an object
	 */
	public List<CcpJsonRepresentation> asListRecord(){
		String httpResponseTrim = this.httpResponse.trim();
		boolean httpResponseTrimEmpty = httpResponseTrim.isEmpty();
		if(httpResponseTrimEmpty) {
			return new ArrayList<>();
		}
		CcpJsonHandler json = CcpDependencyInjection.getDependency(CcpJsonHandler.class);
		boolean validJsonList = json.isValidJsonList(this.httpResponse);
		boolean isNotJsonList = false == validJsonList;
		if(isNotJsonList) {
			throw new CcpErrorHttpResponseIsNotListOfRecords(this.httpResponse);
		}
		List<Object> items = json.fromJson(this.httpResponse);
		List<CcpJsonRepresentation> records = new ArrayList<>();
		for (Object item : items) {
			boolean isNotObject = false == item instanceof Map;
			if(isNotObject) {
				throw new CcpErrorHttpResponseIsNotListOfRecords(this.httpResponse);
			}
			@SuppressWarnings("unchecked")
			Map<String, Object> itemAsMap = (Map<String, Object>) item;
			CcpJsonRepresentation record = new CcpJsonRepresentation(itemAsMap);
			records.add(record);
		}
		return records;
	}

	/**
	 * Parses the body as a JSON list.
	 * @return the items of the list
	 */
	public List<Object> asListObject(){
		CcpJsonHandler json = CcpDependencyInjection.getDependency(CcpJsonHandler.class);
		List<Object> fromJson = json.fromJson(this.httpResponse);
		return fromJson; 
	}

	/**
	 * Encodes the body in Base64.
	 * @return the Base64 text
	 */
	public String asBase64() {
		byte[] bytes = this.httpResponse.getBytes();
		CcpStringDecorator ccpStringDecorator3 = new CcpStringDecorator(bytes);
		CcpTextDecorator ccpStringDecorator3Text = ccpStringDecorator3.text();
		CcpTextDecorator asBase64 = ccpStringDecorator3Text.asBase64();
		String encodeToString = asBase64.content;
		return encodeToString;
	}
	
	
	/**
	 * Returns the status and the body as JSON.
	 * @return the JSON text
	 */
	public String toString() {
		CcpJsonRepresentation put = CcpOtherConstants.EMPTY_JSON
				.put(JsonFieldNames.httpStatus, this.httpStatus);
				CcpJsonRepresentation put2 = put
				.put(JsonFieldNames.httpResponse, this.httpResponse);
				String toString = put2
				.toString();
				return toString;
	}
	

	/**
	 * Tells whether the status is between {@code range} and {@code range + 99}.
	 * @param range the start of the range
	 * @return {@code true} when the status is in the range
	 */
	private boolean isInRange(int range) {
		boolean statusBelowRange = this.httpStatus < range;
		if(statusBelowRange) {
			return false;
		}
		int rangeEnd = range + 99;
		boolean statusAboveRange = this.httpStatus > (rangeEnd);
		if(statusAboveRange) {
			return false;
		}
		return true;
		
	}
	
	/**
	 * Tells whether the status is in the 400-499 range.
	 * @return {@code true} for a client error
	 */
	public boolean isClientError() {
		boolean inRange = this.isInRange(400);
		return inRange;
	}
	
	/**
	 * Tells whether the status is in the 500-599 range.
	 * @return {@code true} for a server error
	 */
	public boolean isServerError() {
		boolean inRange2 = this.isInRange(500);
		return inRange2;
	}

	/**
	 * Tells whether the status is in the 200-299 range.
	 * @return {@code true} for a success
	 */
	public boolean isSuccess() {
		boolean inRange3 = this.isInRange(200);
		return inRange3;
	}

	/** Raised by {@link CcpHttpResponse#asListRecord()} when the body is not a JSON list of objects. */
	@SuppressWarnings("serial")
	public static class CcpErrorHttpResponseIsNotListOfRecords extends RuntimeException {
		/**
		 * Builds the message with the body received.
		 * @param httpResponse the body
		 */
		private CcpErrorHttpResponseIsNotListOfRecords(String httpResponse) {
			super("The http response is not a json list of objects: " + httpResponse);
		}
	}
}
