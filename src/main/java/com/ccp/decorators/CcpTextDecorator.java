package com.ccp.decorators;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.Normalizer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Base64.Decoder;
import java.util.Base64.Encoder;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.function.Predicate;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.ccp.constants.CcpOtherConstants;
import java.util.stream.Stream;

/**
 * Decorator specialized in text manipulation and analysis: accent removal, random
 * token generation, padding, capitalization, case conversion, regex validation, sanitization for
 * search, Base64 encode/decode and resolution of templates with variables.
 */
public class CcpTextDecorator implements CcpDecorator<String> {
	public final String content;

	/**
	 * Wraps the text.
	 */
	protected CcpTextDecorator(String content) {
		this.content = content;
	}

	/**
	 * Pads the text on the left with the character {@code complement} until it reaches the length {@code length}.
	 */
	public CcpTextDecorator completeLeft(char complement, int length) {
		int contentLength = this.content.length();
		int missingCharacters = length - contentLength;
		boolean alreadyLongEnough = (missingCharacters )<=0;
		if(alreadyLongEnough) {
			return this;
		}
		String padding = "";
		for(int k = this.content.length(); k < length; k++) {
			padding += complement;
		}
		String complete = padding + this.content;
		CcpTextDecorator ccpTextDecorator = new CcpTextDecorator(complete);
		return ccpTextDecorator;
	}

	/**
	 * Removes accents and diacritics, preserving {@code #} and basic alphanumeric characters.
	 */
	public CcpTextDecorator stripAccents() {

		String hashPlaceholder = "__charp__";
		String contentReplace = this.content.replace("#", hashPlaceholder);
		String normalizedText = Normalizer.normalize(contentReplace, Normalizer.Form.NFD);
		String withoutDiacritics = normalizedText.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
		normalizedText = withoutDiacritics.replaceAll("[^\\w\\s.,+-]", "");
		String restoredText = normalizedText.replace(hashPlaceholder, "#");
		CcpTextDecorator strippedText = new CcpTextDecorator(restoredText);
		return strippedText;
	}

	/**
	 * Extracts all substrings delimited by {@code beginDelimiter} and {@code endDelimiter}.
	 */
	public List<String> getPieces(String beginDelimiter, String endDelimiter) {
		int beginIndex = 0;
		int endIndex = 0;
		String str = this.content;
		List<String> list = new ArrayList<>();
		while(true) {
			beginIndex = str.indexOf(beginDelimiter);
			boolean beginDelimiterNotFound = beginIndex < 0;
			if(beginDelimiterNotFound) {
				return list;
			}
			int endDelimiterIndex = str.indexOf(endDelimiter );
			int endDelimiterLength = endDelimiter.length();
			endIndex = endDelimiterIndex+ endDelimiterLength;
			boolean endDelimiterNotFound = endIndex < 0;

			if(endDelimiterNotFound) {
				return list;
			}
			String substring = str.substring(beginIndex, endIndex);
			list.add(substring);
			str = str.substring(endIndex);
		}
	}

	/**
	 * Splits the text by the delimiter and filters the pieces by the predicate.
	 */
	public List<String> getPieces(Predicate<String> predicate, String delimiter){
		String[] split = this.content.split(delimiter);
		List<String> asList = Arrays.asList(split);
		Stream<String> stream = asList.stream();
		var filteredPieces = stream.filter(predicate);
		List<String> matchingPieces = filteredPieces.collect(Collectors.toList());
		return matchingPieces;
	}

	/**
	 * Removes the pieces that satisfy the predicate, replacing them with a space.
	 */
	public CcpTextDecorator removePieces(Predicate<String> predicate, String delimiter) {
		List<String> pieces = this.getPieces(predicate, delimiter);
		CcpTextDecorator ccpTextDecorator = this.removePieces(pieces);
		return ccpTextDecorator;
	}

	/**
	 * Replaces all occurrences of {@code oldText} with {@code newText}.
	 */
	public CcpTextDecorator replace(String oldText, String newText) {
		String replacedText = this.content.replace(oldText, newText);
		CcpTextDecorator ccpTextDecorator = new CcpTextDecorator(replacedText);
		return ccpTextDecorator;
	}

	/**
	 * Removes all substrings delimited by {@code beginDelimiter} and {@code endDelimiter}.
	 */
	public CcpTextDecorator removePieces(String beginDelimiter, String endDelimiter) {
		List<String> pieces = this.getPieces(beginDelimiter, endDelimiter);
		CcpTextDecorator ccpTextDecorator = this.removePieces(pieces);
		return ccpTextDecorator;
	}

	/**
	 * Removes from the string each substring of the given list.
	 */
	public CcpTextDecorator removePieces(List<String> pieces) {
		String str = this.content;
		for (String piece : pieces) {
			str = str.replace(piece, " ");
		}
		CcpTextDecorator ccpTextDecorator = new CcpTextDecorator(str);
		return ccpTextDecorator;
	}

	/**
	 * Generates a random token of size {@code charactersSize} by picking characters from the current content (useful as a token alphabet).
	 */
	public CcpTextDecorator generateToken(long charactersSize) {

		Random random = new Random();
		char[] charArray = this.content.toCharArray();
		StringBuilder sb = new StringBuilder();

		for (int k = 0; k < charactersSize; k++) {

			int randomIndex = random.nextInt(charArray.length);

			char randomCharacter = charArray[randomIndex];
			sb.append(randomCharacter);
		}
		String token = sb.toString();

		CcpTextDecorator ccpTextDecorator = new CcpTextDecorator(token);
		return ccpTextDecorator;
	}

	/**
	 * Decodes the Base64 string and returns it as a {@code ByteArrayInputStream}.
	 */
	public InputStream getByteArrayInputStream() {
		byte[] byteArrayFromBase64String = this.getByteArrayFromBase64String();
		ByteArrayInputStream inputStream = new ByteArrayInputStream(byteArrayFromBase64String);
		return inputStream;
	}

	/**
	 * Decodes the Base64 string (supporting the {@code data:xxx,base64} prefix) and returns the byte array.
	 */
	public byte[] getByteArrayFromBase64String() {
		String[] split = this.content.split(",");
		String str = split[0];
		boolean hasDataPrefix = split.length > 1;

		if (hasDataPrefix) {
			str = split[1];
		}

		String base64 = str;

		Decoder decoder = Base64.getDecoder();

		byte[] byteArray = decoder.decode(base64);
		return byteArray;
	}

	/**
	 * Alias of {@code getByteArrayInputStream}.
	 */
	public  ByteArrayInputStream getParameterAsByteArrayInputStream() {

		byte[] byteArray = this.getByteArrayFromBase64String();

		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray);

		return byteArrayInputStream;

	}



	/**
	 * Replaces the {@code {fieldName}} placeholders with the corresponding values of the parameters JSON.
	 * Also supports the dynamic function {@code {currentTimeMillis()}}.
	 */
	public CcpTextDecorator resolveTemplate(CcpJsonRepresentation parameters) {
		Map<String, Object> content = parameters.getContent();
		Set<String> keySet = content.keySet();
		String message = new String(this.content);
		for (String key : keySet) {
			CcpFieldName ccpFieldName = new CcpFieldName(key);
			String value = parameters.getAsString(ccpFieldName);
			String placeholderStart = "{" + key;
			String placeholder = placeholderStart + "}";
			message = message.replace(placeholder, value);
		}

		CcpTemplateFunctions[] templateExpressions = CcpTemplateFunctions.values();

		for (CcpTemplateFunctions templateExpression : templateExpressions) {
			String value = templateExpression.get();
			String functionPlaceholderStart = "{" + templateExpression;
			String functionPlaceholder = functionPlaceholderStart + "()}";
			message = message.replace(functionPlaceholder, value);
		}
		CcpTextDecorator resolvedTemplate = new CcpTextDecorator(message);

		return resolvedTemplate;
	}

	/**
	 * Recursively removes every {@code c} character from the start of the string.
	 */
	public CcpTextDecorator removeStartingCharacters( char c) {
		String characterAsString = "" + c;
		boolean startsWith = this.content.startsWith(characterAsString);
		boolean doesNotStartWithCharacter = false == startsWith;

		if(doesNotStartWithCharacter) {
			return this;
		}

		String substring = this.content.substring(1);
		CcpTextDecorator textWithoutFirstCharacter = new CcpTextDecorator(substring);
		CcpTextDecorator removeStartingCharacters = textWithoutFirstCharacter.removeStartingCharacters(c);
		return removeStartingCharacters;
	}

	/**
	 * Recursively removes every {@code c} character from the end of the string.
	 */
	public CcpTextDecorator removeEndingCharacters(char c) {
		String characterAsString = "" + c;
		boolean endsWith = this.content.endsWith(characterAsString);
		boolean doesNotEndWithCharacter = false == endsWith;

		if(doesNotEndWithCharacter) {
			return this;
		}
		int contentLength = this.content.length();
		int lastCharacterIndex = contentLength - 1;

		String substring = this.content.substring(0, lastCharacterIndex);
		CcpTextDecorator textWithoutLastCharacter = new CcpTextDecorator(substring);
		CcpTextDecorator removed = textWithoutLastCharacter.removeEndingCharacters(c);
		return removed;
	}

	/**
	 * Checks whether the text is a valid JSON object.
	 */
	public boolean isValidSingleJson() {
		try {
			new CcpJsonRepresentation(this.content);
			return true;
		} catch (CcpErrorJsonInvalid e) {
			return false;
		}
	}

	/**
	 * Returns the internal text.
	 */
	public String toString() {
		return this.content;
	}

	/**
	 * Encodes the text in Base64.
	 */
	public CcpTextDecorator asBase64() {
		byte[] bytes = this.content.getBytes();
		Encoder encoder = Base64.getEncoder();
		String encodeToString = encoder.encodeToString(bytes);
		CcpTextDecorator ccpTextDecorator = new CcpTextDecorator(encodeToString);
		return ccpTextDecorator;
	}

	/**
	 * Converts {@code snake_case} text to {@code CamelCase}.
	 */
	public CcpTextDecorator toCamelCase() {
		String[] split = this.content.split("_");
		List<String> asList = Arrays.asList(split);
		StringBuilder sb = new StringBuilder();
		for (String string : asList) {
			CcpStringDecorator ccpStringDecorator = new CcpStringDecorator(string);
			CcpTextDecorator ccpStringDecoratorText = ccpStringDecorator.text();
			CcpTextDecorator capitalizedText = ccpStringDecoratorText.capitalize();
			String capitalizedWord = capitalizedText.content;
			sb.append(capitalizedWord);
		}
		String camelCaseText = sb.toString();
		CcpTextDecorator ccpTextDecorator = new CcpTextDecorator( camelCaseText);
		return ccpTextDecorator;
	}

	/**
	 * Converts {@code CamelCase} text to {@code snake_case}.
	 */
	public CcpTextDecorator toSnakeCase() {
		char[] charArray = this.content.toCharArray();
		StringBuilder sb = new StringBuilder(this.content);
		int k = 0;
		int insertedUnderscores = 0;
		for (char c : charArray) {
			boolean isFirstCharacter = k == 0;
			if(isFirstCharacter) {
				k++;
				continue;
			}
			boolean isBeforeUpperCase = c < 'A';

			if(isBeforeUpperCase) {
				k++;
				continue;
			}
			boolean isAfterUpperCase = c > 'Z';
			if(isAfterUpperCase) {
				k++;
				continue;
			}
			int insertPosition = k++ + insertedUnderscores++;
			sb.insert(insertPosition, "_");
		}
		String snakeCaseText = sb.toString();
		String toLowerCase = snakeCaseText.toLowerCase();
		CcpTextDecorator ccpTextDecorator = new CcpTextDecorator(toLowerCase);
		return ccpTextDecorator;
	}

	/**
	 * Puts the first letter in upper case and the rest in lower case.
	 */
	public CcpTextDecorator capitalize() {
		String contentTrim = this.content.trim();
		boolean contentTrimEmpty = contentTrim.isEmpty();

		if(contentTrimEmpty) {
			CcpTextDecorator ccpTextDecorator = new CcpTextDecorator("");
			return ccpTextDecorator;
		}
		String firstLetter = this.content.substring(0, 1);
		String substring = this.content.substring(1);
		String upperCase = firstLetter.toUpperCase();
		String lowerCase = substring.toLowerCase();
		String complete = upperCase + lowerCase;
		CcpTextDecorator ccpTextDecorator = new CcpTextDecorator(complete);
		return ccpTextDecorator;
	}

	/**
	 * Returns the text length wrapped in a {@code CcpNumberDecorator}.
	 */
	public CcpNumberDecorator lenght() {
		int contentLength = content.length();
		String lengthAsString = "" + contentLength;
		CcpNumberDecorator ccpNumberDecorator = new CcpNumberDecorator(lengthAsString);
		return ccpNumberDecorator;
	}

	/**
	 * Checks whether the text matches the regular expression (case insensitive).
	 */
	public boolean regexMatches(String regex) {
		Pattern pattern = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		Matcher matcher = pattern.matcher(this.content);
		boolean found = matcher.find();
		return found;
	}

	/**
	 * Checks whether the text contains the phrase, using sanitization by the default delimiters and word comparison.
	 */
	public boolean contains(String phrase) {
		boolean contains = this.contains(CcpOtherConstants.DELIMITERS_ARRAY, phrase);
		return contains;
	}

	/**
	 * Checks whether the text contains the phrase, using custom delimiters.
	 */
	public boolean contains(String[] delimiters, String phrase) {
		CcpTextDecorator sanitizedText = this.sanitize(delimiters);
		CcpTextDecorator phraseDecorator = new CcpTextDecorator(phrase);
		CcpTextDecorator sanitizedPhrase = phraseDecorator.sanitize(delimiters);
		String upperCaseText = sanitizedText.content.toUpperCase();
		String upperCasePhrase = sanitizedPhrase.content.toUpperCase();
		boolean containsPhrase = upperCaseText.contains(upperCasePhrase);
		boolean notContained = false == containsPhrase;

		if(notContained) {
			return false;
		}

		List<String> textWords = sanitizedText.split();
		List<String> phraseWords = sanitizedPhrase.split();

		boolean containsAll = textWords.containsAll(phraseWords);
		return containsAll;
	}

	private List<String> split(){
		String[] split = this.content.split(" ");
		List<String> asList = Arrays.asList(split);
		return asList;
	}

	/**
	 * Replaces the default delimiters with a space and converts to upper case without accents.
	 */
	public CcpTextDecorator sanitize() {
		CcpTextDecorator sanitize = this.sanitize(CcpOtherConstants.DELIMITERS_ARRAY);
		return sanitize;
	}

	/**
	 * Replaces the custom delimiters with a space and converts to upper case without accents.
	 */
	public CcpTextDecorator sanitize(String[] delimiters) {
		String text = this.content;
		for (String delimiter : delimiters) {
			text = text.replace(delimiter, " ");
		}
		String upperCase = text.toUpperCase();
		CcpStringDecorator upperCaseDecorator = new CcpStringDecorator(upperCase);
		CcpTextDecorator upperCaseText = upperCaseDecorator.text();
		CcpTextDecorator sanitizedText = upperCaseText.stripAccents();
		return sanitizedText;
	}

	/**
	 * Returns the internal text.
	 */
	public String getContent() {
		return this.content;
	}

}
