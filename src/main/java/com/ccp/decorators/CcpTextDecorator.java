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

import com.ccp.constantes.CcpOtherConstants;

public class CcpTextDecorator implements CcpDecorator<String> {
	public final String content;

	protected CcpTextDecorator(String content) {
		this.content = content;
	}

	public CcpTextDecorator completeLeft(char complement, int length) {
		if((length - this.content.length() )<=0) {
			return this;
		}
		String x = "";
		for(int k = this.content.length(); k < length; k++) {
			x += complement;
		}
		String complete = x + this.content;
		CcpTextDecorator ccpTextDecorator = new CcpTextDecorator(complete);
		return ccpTextDecorator;
	}
	
	public CcpTextDecorator stripAccents() {
		
		String s = Normalizer.normalize(this.content, Normalizer.Form.NFD);
		s = s.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "").replaceAll("[^\\w\\s.,+-]", "");
		return new CcpTextDecorator(s);
	}
	
	public List<String> getPieces(String beginDelimiter, String endDelimiter) {
		int beginIndex = 0;
		int endIndex = 0;
		String str = this.content;
		List<String> list = new ArrayList<>();
		while(true) {
			beginIndex = str.indexOf(beginDelimiter);
			if(beginIndex < 0) {
				return list;
			}
			endIndex = str.indexOf(endDelimiter )+ endDelimiter.length();

			if(endIndex < 0) {
				return list;
			}
			String substring = str.substring(beginIndex, endIndex);
			list.add(substring);
			str = str.substring(endIndex);
		}
	}
	
	public List<String> getPieces(Predicate<String> predicate, String delimiter){
		String[] split = this.content.split(delimiter);
		List<String> asList = Arrays.asList(split);
		List<String> collect = asList.stream().filter(predicate).collect(Collectors.toList());
		return collect;
	}
	
	public CcpTextDecorator removePieces(Predicate<String> predicate, String delimiter) {
		List<String> pieces = this.getPieces(predicate, delimiter);
		CcpTextDecorator ccpTextDecorator = this.removePieces(pieces);
		return ccpTextDecorator;
	}
	
	public CcpTextDecorator replace(String oldText, String newText) {
		String replace = this.content.replace(oldText, newText);
		CcpTextDecorator ccpTextDecorator = new CcpTextDecorator(replace);
		return ccpTextDecorator;
	}
	
	public CcpTextDecorator removePieces(String beginDelimiter, String endDelimiter) {
		List<String> pieces = this.getPieces(beginDelimiter, endDelimiter);
		CcpTextDecorator ccpTextDecorator = this.removePieces(pieces);
		return ccpTextDecorator;
	}

	public CcpTextDecorator removePieces(List<String> pieces) {
		String str = this.content;	
		for (String piece : pieces) {
			str = str.replace(piece, " ");
		}
		CcpTextDecorator ccpTextDecorator = new CcpTextDecorator(str);
		return ccpTextDecorator;
	}
	
	public CcpTextDecorator generateToken(long charactersSize) {

		Random random = new Random();
		char[] charArray = this.content.toCharArray();
		StringBuilder sb = new StringBuilder();

		for (int k = 0; k < charactersSize; k++) {

			int indiceAleatorio = random.nextInt(charArray.length);

			char caractereAleatorio = charArray[indiceAleatorio];
			sb.append(caractereAleatorio);
		}

		CcpTextDecorator ccpTextDecorator = new CcpTextDecorator(sb.toString());
		return ccpTextDecorator;
	}
	
	public InputStream getByteArrayInputStream() {
		byte[] byteArrayFromBase64String = this.getByteArrayFromBase64String();
		ByteArrayInputStream is = new ByteArrayInputStream(byteArrayFromBase64String);
		return is;
	}
	
	public byte[] getByteArrayFromBase64String() {
		String[] split = this.content.split(",");
		String str = split[0];

		if (split.length > 1) {
			str = split[1];
		}

		String base64 = str;

		Decoder decoder = Base64.getDecoder();

		byte[] byteArray = decoder.decode(base64);
		return byteArray;
	}
	
	public  ByteArrayInputStream getParameterAsByteArrayInputStream() {

		byte[] byteArray = this.getByteArrayFromBase64String();

		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray);

		return byteArrayInputStream;

	}
	public CcpTextDecorator getMessage(CcpJsonRepresentation parameters) {

		Map<String, Object> content = parameters.getContent();
		Set<String> keySet = content.keySet();
		String message = new String(this.content);
		for (String key : keySet) {
			String value = parameters.getDynamicVersion().getAsString(key);
			message = message.replace("{" + key + "}", value);
		}
		return new CcpTextDecorator(message);
	}
	//StringDecorator removeStartingCharacters()
	public CcpTextDecorator removeStartingCharacters( char c) {
		
		
		if(false == this.content.startsWith("" + c)) {
			return this;
		}
		
		String substring = this.content.substring(1);
		CcpTextDecorator removeStartingCharacters = new CcpTextDecorator(substring).removeStartingCharacters(c);
		return removeStartingCharacters;
	}

	public CcpTextDecorator removeEndingCharacters(char c) {

		if(false == this.content.endsWith("" + c)) {
			return this;
		}

		String substring = this.content.substring(0, this.content.length() - 1);
		CcpTextDecorator removed = new CcpTextDecorator(substring).removeEndingCharacters(c);
		return removed;
	}

	
	public boolean isValidSingleJson() {
		try {
			new CcpJsonRepresentation(this.content);
			return true;
		} catch (CcpErrorJsonInvalid e) {
			return false;
		}
	}
	
	public String toString() {
		return this.content;
	}
	
	public CcpTextDecorator asBase64() {
		byte[] bytes = this.content.getBytes();
		Encoder encoder = Base64.getEncoder();
		String encodeToString = encoder.encodeToString(bytes);
		CcpTextDecorator ccpTextDecorator = new CcpTextDecorator(encodeToString);
		return ccpTextDecorator;
	}
	
	public CcpTextDecorator toCamelCase() {
		String[] split = this.content.split("_");
		List<String> asList = Arrays.asList(split);
		StringBuilder sb = new StringBuilder();
		for (String string : asList) {
			String capitalize = new CcpStringDecorator(string).text().capitalize().content;
			sb.append(capitalize);
		}
		CcpTextDecorator ccpTextDecorator = new CcpTextDecorator( sb.toString());
		return ccpTextDecorator;
	}

	public CcpTextDecorator toSnakeCase() {
		char[] charArray = this.content.toCharArray();
		StringBuilder sb = new StringBuilder(this.content);
		int k = 0;
		int m = 0;
		for (char c : charArray) {
			if(k == 0) {
				k++;
				continue;
			}
			
			if(c < 'A') {
				k++;
				continue;
			}
			if(c > 'Z') {
				k++;
				continue;
			}
			sb.insert(k++ + m++, "_");
		}
		CcpTextDecorator ccpTextDecorator = new CcpTextDecorator(sb.toString().toLowerCase());
		return ccpTextDecorator;
	}
	
	public CcpTextDecorator capitalize() {
		
		if(this.content.trim().isEmpty()) {
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
	
	public CcpNumberDecorator lenght() {
		return new CcpNumberDecorator("" + content.length());
	}
	
	public boolean regexMatches(String regex) {
		Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(this.content);
		boolean find = m.find();
		return find;
	}
	
	public boolean contains(String phrase) {
		boolean contains = this.contains(CcpOtherConstants.DELIMITERS, phrase);
		return contains;
	}
	
	public boolean contains(String[] delimiters, String phrase) {
		CcpTextDecorator s1 = this.sanitize(delimiters);
		CcpTextDecorator ctd = new CcpTextDecorator(phrase);
		CcpTextDecorator s2 = ctd.sanitize(delimiters);
		boolean notContained = false == s1.content.toUpperCase().contains(s2.content.toUpperCase());
		
		if(notContained) {
			return false;
		}
		
		List<String> split1 = s1.split();
		List<String> split2 = s2.split();
		
		boolean containsAll = split1.containsAll(split2);
		return containsAll;
	}
	
	private List<String> split(){
		String[] split = this.content.split(" ");
		List<String> asList = Arrays.asList(split);
		return asList;
	}
	
	public CcpTextDecorator sanitize() {
		return this.sanitize(CcpOtherConstants.DELIMITERS);
	}
	
	public CcpTextDecorator sanitize(String[] delimiters) {
		String text = this.content;
		for (String delimiter : delimiters) {
			text = text.replace(delimiter, " ");
		}
		String upperCase = text.toUpperCase();
		CcpTextDecorator ctd = new CcpStringDecorator(upperCase).text().stripAccents();
		return ctd;
	}

	public String getContent() {
		return this.content;
	}

}
