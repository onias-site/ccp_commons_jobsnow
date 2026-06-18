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
import java.util.function.Supplier;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import com.ccp.constants.CcpOtherConstants;

/**
 * Decorator especializado em operações de manipulação e análise de texto: remoção de acentos, geração de
 * tokens aleatórios, preenchimento, capitalização, conversão de case, validação de regex, sanitização para
 * busca, encode/decode Base64 e resolução de templates com variáveis.
 */
public class CcpTextDecorator implements CcpDecorator<String> {
	public final String content;

	/**
	 * Encapsula o texto.
	 */
	protected CcpTextDecorator(String content) {
		this.content = content;
	}

	/**
	 * Preenche o texto à esquerda com o caractere {@code complement} até atingir o tamanho {@code length}.
	 */
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

	/**
	 * Remove acentos e diacríticos preservando {@code #} e caracteres alfanuméricos básicos.
	 */
	public CcpTextDecorator stripAccents() {

		String charp = "__charp__";
		String s = Normalizer.normalize(this.content.replace("#", charp), Normalizer.Form.NFD);
		s = s.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "").replaceAll("[^\\w\\s.,+-]", "");
		String replace = s.replace(charp, "#");
		return new CcpTextDecorator(replace);
	}

	/**
	 * Extrai todas as substrings delimitadas por {@code beginDelimiter} e {@code endDelimiter}.
	 */
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

	/**
	 * Divide o texto pelo delimitador e filtra as partes pelo predicado.
	 */
	public List<String> getPieces(Predicate<String> predicate, String delimiter){
		String[] split = this.content.split(delimiter);
		List<String> asList = Arrays.asList(split);
		List<String> collect = asList.stream().filter(predicate).collect(Collectors.toList());
		return collect;
	}

	/**
	 * Remove as partes que atendem ao predicado, substituindo-as por espaço.
	 */
	public CcpTextDecorator removePieces(Predicate<String> predicate, String delimiter) {
		List<String> pieces = this.getPieces(predicate, delimiter);
		CcpTextDecorator ccpTextDecorator = this.removePieces(pieces);
		return ccpTextDecorator;
	}

	/**
	 * Substitui todas as ocorrências de {@code oldText} por {@code newText}.
	 */
	public CcpTextDecorator replace(String oldText, String newText) {
		String replace = this.content.replace(oldText, newText);
		CcpTextDecorator ccpTextDecorator = new CcpTextDecorator(replace);
		return ccpTextDecorator;
	}

	/**
	 * Remove todas as substrings delimitadas por {@code beginDelimiter} e {@code endDelimiter}.
	 */
	public CcpTextDecorator removePieces(String beginDelimiter, String endDelimiter) {
		List<String> pieces = this.getPieces(beginDelimiter, endDelimiter);
		CcpTextDecorator ccpTextDecorator = this.removePieces(pieces);
		return ccpTextDecorator;
	}

	/**
	 * Remove da string cada substring da lista fornecida.
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
	 * Gera um token aleatório de tamanho {@code charactersSize} selecionando caracteres do conteúdo atual (útil como alfabeto de tokens).
	 */
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

	/**
	 * Decodifica a string Base64 e retorna como {@code ByteArrayInputStream}.
	 */
	public InputStream getByteArrayInputStream() {
		byte[] byteArrayFromBase64String = this.getByteArrayFromBase64String();
		ByteArrayInputStream is = new ByteArrayInputStream(byteArrayFromBase64String);
		return is;
	}

	/**
	 * Decodifica a string Base64 (suportando prefixo {@code data:xxx,base64}) e retorna o array de bytes.
	 */
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

	/**
	 * Alias de {@code getByteArrayInputStream}.
	 */
	public  ByteArrayInputStream getParameterAsByteArrayInputStream() {

		byte[] byteArray = this.getByteArrayFromBase64String();

		ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArray);

		return byteArrayInputStream;

	}

	public static enum CcpTemplateFunctions implements Supplier<String>{
		currentTimeMillis {
			public String get() {
				return "" + System.currentTimeMillis();
			}

		};
	}

	/**
	 * Substitui os placeholders {@code {nomeDoCampo}} pelos valores correspondentes do JSON de parâmetros.
	 * Suporta também a função dinâmica {@code {currentTimeMillis()}}.
	 */
	public CcpTextDecorator resolveTemplate(CcpJsonRepresentation parameters) {
		Map<String, Object> content = parameters.getContent();
		Set<String> keySet = content.keySet();
		String message = new String(this.content);
		for (String key : keySet) {
			String value = parameters.getAsString(new CcpFieldName(key));
			message = message.replace("{" + key + "}", value);
		}

		CcpTemplateFunctions[] templateExpressions = CcpTemplateFunctions.values();

		for (CcpTemplateFunctions templateExpression : templateExpressions) {
			String value = templateExpression.get();
			message = message.replace("{" + templateExpression + "()}", value);
		}

		return new CcpTextDecorator(message);
	}

	/**
	 * Remove recursivamente todos os caracteres {@code c} do início da string.
	 */
	public CcpTextDecorator removeStartingCharacters( char c) {

		if(false == this.content.startsWith("" + c)) {
			return this;
		}

		String substring = this.content.substring(1);
		CcpTextDecorator removeStartingCharacters = new CcpTextDecorator(substring).removeStartingCharacters(c);
		return removeStartingCharacters;
	}

	/**
	 * Remove recursivamente todos os caracteres {@code c} do final da string.
	 */
	public CcpTextDecorator removeEndingCharacters(char c) {

		if(false == this.content.endsWith("" + c)) {
			return this;
		}

		String substring = this.content.substring(0, this.content.length() - 1);
		CcpTextDecorator removed = new CcpTextDecorator(substring).removeEndingCharacters(c);
		return removed;
	}

	/**
	 * Verifica se o texto é um JSON de objeto válido.
	 */
	public boolean isValidSingleJson() {
		try {
			new CcpJsonRepresentation(this.content);
			return true;
		} catch (CcpJsonRepresentation.CcpErrorJsonInvalid e) {
			return false;
		}
	}

	/**
	 * Retorna o texto interno.
	 */
	public String toString() {
		return this.content;
	}

	/**
	 * Codifica o texto em Base64.
	 */
	public CcpTextDecorator asBase64() {
		byte[] bytes = this.content.getBytes();
		Encoder encoder = Base64.getEncoder();
		String encodeToString = encoder.encodeToString(bytes);
		CcpTextDecorator ccpTextDecorator = new CcpTextDecorator(encodeToString);
		return ccpTextDecorator;
	}

	/**
	 * Converte texto em {@code snake_case} para {@code CamelCase}.
	 */
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

	/**
	 * Converte texto em {@code CamelCase} para {@code snake_case}.
	 */
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

	/**
	 * Coloca a primeira letra em maiúscula e o restante em minúsculas.
	 */
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

	/**
	 * Retorna o tamanho do texto encapsulado em {@code CcpNumberDecorator}.
	 */
	public CcpNumberDecorator lenght() {
		return new CcpNumberDecorator("" + content.length());
	}

	/**
	 * Verifica se o texto corresponde à expressão regular (case insensitive).
	 */
	public boolean regexMatches(String regex) {
		Pattern p = Pattern.compile(regex, Pattern.CASE_INSENSITIVE);
		Matcher m = p.matcher(this.content);
		boolean find = m.find();
		return find;
	}

	/**
	 * Verifica se o texto contém a frase usando sanitização por delimitadores padrão e comparação de palavras.
	 */
	public boolean contains(String phrase) {
		boolean contains = this.contains(CcpOtherConstants.DELIMITERS_ARRAY, phrase);
		return contains;
	}

	/**
	 * Verifica se o texto contém a frase com delimitadores personalizados.
	 */
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

	/**
	 * Substitui os delimitadores padrão por espaço e converte para maiúsculas sem acentos.
	 */
	public CcpTextDecorator sanitize() {
		return this.sanitize(CcpOtherConstants.DELIMITERS_ARRAY);
	}

	/**
	 * Substitui os delimitadores personalizados por espaço e converte para maiúsculas sem acentos.
	 */
	public CcpTextDecorator sanitize(String[] delimiters) {
		String text = this.content;
		for (String delimiter : delimiters) {
			text = text.replace(delimiter, " ");
		}
		String upperCase = text.toUpperCase();
		CcpTextDecorator ctd = new CcpStringDecorator(upperCase).text().stripAccents();
		return ctd;
	}

	/**
	 * Retorna o texto interno.
	 */
	public String getContent() {
		return this.content;
	}

}
