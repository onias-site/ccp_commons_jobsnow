package com.ccp.constants;

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
	/** The most common textual delimiters (slash, backslash, dot, tab, line feed, punctuation, brackets, quotes), used to tokenize and sanitize text. */
	String[] DELIMITERS_ARRAY = new String[] {"/", "\\", ".","\t", "\n", ":", "," , ";", "!", "?", "[", "]", "{", "}", "<", ">", "=", "(", ")", "'", "`",  "\""};
	/**
	 * Regular expression of text delimiters, ready to be used with {@code String.split()} or {@code Pattern}. Until
	 * 2026-10-07 the closing parenthesis was written {@code \)\ } (parenthesis followed by a space), so a {@code )} at the
	 * end of a word ("Spring (Boot)") stayed glued to it.
	 * <p>
	 * It does not match the same set as {@link #DELIMITERS_ARRAY}: this one splits on {@code -}, {@code \r} and white
	 * space, the array on {@code /} and {@code \}. Unifying them changes how skills are recognized in a free text (the
	 * only user of this expression, in vis), so it waits for that decision.
	 */
	String DELIMITERS = "\r|\t|\n|\\s|\\:|\\,|\\-|\\;|\\!|\\?|\\[|\\]|\\{|\\}|\\<|\\>|\\=|\\(|\\)|\\'|\\\"|\\`|\\.";
	/** {@code CcpJsonFieldName} whose value is the empty string; represents the absence of a field name. */
	CcpFieldName EMPTY_STRING = new CcpFieldName("");

	/** Uppercase letters A-Z followed by the digits 0-9: the alphabet used to generate random codes. */
	CcpStringDecorator LETTERS_AND_NUMBERS = new CcpStringDecorator("ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");

}
