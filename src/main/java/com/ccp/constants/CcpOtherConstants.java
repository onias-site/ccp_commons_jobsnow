package com.ccp.constants;

import java.util.Arrays;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.ccp.business.CcpBusiness;
import com.ccp.decorators.CcpFieldName;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpStringDecorator;

/**
 * Repository of global constants shared by the whole system: trivial reusable business rules, the canonical empty
 * JSON, text delimiters and an empty field name.
 */
public interface CcpOtherConstants {

	/** {@code CcpBusiness} that ignores the input JSON and always returns {@link #EMPTY_JSON}. Useful as a default value or as a no-op that clears the result. */
	CcpBusiness RETURNS_EMPTY_JSON = x -> CcpOtherConstants.EMPTY_JSON;
	/** {@code CcpBusiness} that returns the input JSON untouched (pass-through). */
	CcpBusiness DO_NOTHING = json -> json;
	/** Canonical empty {@code CcpJsonRepresentation}; avoids creating empty maps repeatedly. */
	CcpJsonRepresentation EMPTY_JSON = CcpJsonRepresentation.getEmptyJson();
	/**
	 * The textual delimiters (white space, dot, hyphen, punctuation, brackets, quotes), used to tokenize and sanitize
	 * text. It is the single source of the set: {@link #DELIMITERS} is built from it. The slash and the backslash are not
	 * delimiters, so short skills such as {@code CI/CD} and {@code UI/UX} stay whole words.
	 */
	String[] DELIMITERS_ARRAY = new String[] {" ", "\t", "\n", "\u000B", "\f", "\r", ".", "-", ":", ",", ";", "!", "?", "[", "]", "{", "}", "<", ">", "=", "(", ")", "'", "`", "\""};
	/**
	 * Regular expression of text delimiters, ready to be used with {@code String.split()} or {@code Pattern}: each item
	 * of {@link #DELIMITERS_ARRAY}, quoted, as an alternative. Until 2026-10-22 it was written by hand and did not match
	 * the array (it split on {@code -} and white space, the array on {@code /} and {@code \}).
	 */
	String DELIMITERS = Arrays.stream(DELIMITERS_ARRAY).map(Pattern::quote).collect(Collectors.joining("|"));
	/** {@code CcpJsonFieldName} whose value is the empty string; represents the absence of a field name. */
	CcpFieldName EMPTY_STRING = new CcpFieldName("");

	/** Uppercase letters A-Z followed by the digits 0-9: the alphabet used to generate random codes. */
	CcpStringDecorator LETTERS_AND_NUMBERS = new CcpStringDecorator("ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");

}
