package com.ccp.decorators;

import java.io.InputStream;
import java.util.Collection;
import java.util.function.Consumer;

import com.ccp.dependency.injection.CcpDependencyInjection;
import com.ccp.especifications.json.CcpJsonHandler;

/**
 * Central decorator over {@code String} that works as a conversion hub to all the other types of the
 * framework. Starting from a string, it offers fluent access to specialized decorators (e-mail, file,
 * hash, JSON, URL, reflection, etc.) and type checks.
 */
public class CcpStringDecorator implements CcpDecorator<String> {

	public final String content;

	/**
	 * Extracts the value of a JSON field as a string.
	 */
	public CcpStringDecorator(CcpJsonRepresentation json, String key) {
		CcpFieldName ccpFieldName = new CcpFieldName(key);
		this.content = json.getAsString(ccpFieldName);
	}

	/**
	 * Wraps the given string.
	 */
	public CcpStringDecorator(String content) {
		this.content = content;
	}

	/**
	 * Reads all the bytes of the {@code InputStream} and converts them into a string.
	 */
	public CcpStringDecorator(InputStream inputStream) {
		this(readAllBytes(inputStream));
	}

	/**
	 * Converts the primitive byte array into a string.
	 */
	public CcpStringDecorator(byte[] content) {
		this(new String(content));
	}

	/**
	 * Converts the wrapper byte array into a string.
	 */
	public CcpStringDecorator(Byte[] content) {
		this(readAllBytes(content));
	}

	private static byte[] readAllBytes(InputStream inputStream){
		byte[] bytes = inputStream.readAllBytes();
		return bytes;

	}

	/**
	 * Interprets the string as an e-mail address.
	 */
	public CcpEmailDecorator email() {
		CcpEmailDecorator ccpEmailDecorator = new CcpEmailDecorator(this.content);
		return ccpEmailDecorator;
	}

	/**
	 * Interprets the string as a file path.
	 */
	public CcpFileDecorator file() {
		CcpFileDecorator ccpFileDecorator = new CcpFileDecorator(this.content);
		return ccpFileDecorator;
	}

	/**
	 * Interprets the string as a directory path.
	 */
	public CcpFolderDecorator folder() {
		CcpFolderDecorator ccpFolderDecorator = new CcpFolderDecorator(this.content);
		return ccpFolderDecorator;
	}

	/**
	 * Prepares the string for hash calculation.
	 */
	public CcpHashDecorator hash() {
		CcpHashDecorator ccpHashDecorator = new CcpHashDecorator(this.content);
		return ccpHashDecorator;
	}

	/**
	 * Converts the string to a number.
	 */
	public CcpNumberDecorator number() {
		CcpNumberDecorator ccpNumberDecorator = new CcpNumberDecorator(this.content);
		return ccpNumberDecorator;
	}

	/**
	 * Creates a {@code CcpJsonFieldName} whose value is this string.
	 */
	public CcpJsonFieldName jsonFieldName() {
		CcpJsonFieldName ccpJsonFieldName = new CcpFieldName(this.content);
		return ccpJsonFieldName;
	}

	/**
	 * Gives access to text manipulation operations.
	 */
	public CcpTextDecorator text() {
		CcpTextDecorator ccpTextDecorator = new CcpTextDecorator(this.content);
		return ccpTextDecorator;
	}

	/**
	 * Interprets the string as a URL for encode/decode.
	 */
	public CcpUrlDecorator url() {
		CcpUrlDecorator ccpUrlDecorator = new CcpUrlDecorator(this.content);
		return ccpUrlDecorator;
	}

	/**
	 * Deserializes the string as JSON.
	 */
	public CcpJsonRepresentation json() {
		CcpJsonRepresentation ccpJsonRepresentation = new CcpJsonRepresentation(this.content);
		return ccpJsonRepresentation;
	}

	/**
	 * Interprets the string as a password.
	 */
	public CcpPasswordDecorator password() {
		CcpPasswordDecorator ccpPasswordDecorator = new CcpPasswordDecorator(this.content);
		return ccpPasswordDecorator;
	}

	/**
	 * Prepares the string for opening a stream.
	 */
	public CcpInputStreamDecorator inputStreamFrom() {
		CcpInputStreamDecorator ccpInputStreamDecorator = new CcpInputStreamDecorator(this.content);
		return ccpInputStreamDecorator;
	}

	/**
	 * Prepares the string for reading settings.
	 */
	public CcpPropertiesDecorator propertiesFrom() {
		CcpPropertiesDecorator ccpPropertiesDecorator = new CcpPropertiesDecorator(this.content);
		return ccpPropertiesDecorator;
	}

	/**
	 * Prepares the string (class name) for reflection.
	 */
	public CcpReflectionConstructorDecorator reflection() {
		CcpReflectionConstructorDecorator decorator = new CcpReflectionConstructorDecorator(this.content);
		return decorator;
	}

	/**
	 * Checks whether the string represents a valid JSON object.
	 */
	public boolean isInnerJson() {
		CcpJsonHandler json = CcpDependencyInjection.getDependency(CcpJsonHandler.class);
		boolean validJson = json.isValidJson(this.content);
		return validJson;
	}

	/**
	 * Returns the internal string.
	 */
	public String toString() {
		return this.content;
	}

	/**
	 * Checks whether the string represents a JSON list.
	 */
	public boolean isList() {
		boolean valid = this.isValid(x ->  {
			Collection<?> fromJson = CcpDependencyInjection.getDependency(CcpJsonHandler.class).fromJson(x);
			fromJson.toString();
		});
		return valid;

	}

	/**
	 * Checks whether the string can be converted to {@code long}.
	 */
	@SuppressWarnings("unused")
	public boolean isLongNumber() {
		boolean valid = this.isValid(x -> {
			boolean endsWithDecimalZero = x.endsWith(".0");
			Object parsedNumber = endsWithDecimalZero ? Double.valueOf(x) : Long.valueOf(x);
		});
		return valid;
	}

	/**
	 * Checks whether the string can be converted to {@code double}.
	 */
	public boolean isDoubleNumber() {
		boolean valid = this.isValid(x -> Double.valueOf(x));
		return valid;
	}

	/**
	 * Checks whether the string is {@code "true"} or {@code "false"} (case insensitive).
	 */
	public boolean isBoolean() {
		boolean valid = this.isValid(x -> {
			boolean isTrue = "true".equalsIgnoreCase(x);
			if (isTrue) {
				return;
			}
			boolean isFalse = "false".equalsIgnoreCase(x);
			if (isFalse) {
				return;
			}
			CcpErrorStringIsNotBoolean notBooleanError = new CcpErrorStringIsNotBoolean();
			throw notBooleanError;
		});
		return valid;
	}

	@SuppressWarnings("serial")
	private static class CcpErrorStringIsNotBoolean extends RuntimeException {
	}

	private boolean isValid(Consumer<String>  consumer) {
		try {
			consumer.accept(this.content);;
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	/**
	 * Implementation of {@code CcpDecorator}; returns the internal string.
	 */
	public String getContent() {
		return this.content;
	}

	/**
	 * Returns the byte array of the string (wrapper type {@code Byte[]}).
	 */
	public Byte[] getBytes() {
		byte[] bytes = this.content.getBytes();
		Byte[] result = new Byte[bytes.length];
		int k = 0;

		for (Byte byteValue : bytes) {
			result[k++] = byteValue;
		}

		return result;
	}

	private static byte[] readAllBytes(Byte[] bytes) {
		byte[] result = new byte[bytes.length];
		int k = 0;

		for (Byte byteValue : bytes) {
			result[k++] = byteValue;
		}

		return result;

	}
}
