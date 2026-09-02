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
		int contentLength = this.content.length();
		int lengthMenos = length - contentLength;
		boolean lengthMenosMenorOuIgual = (lengthMenos )<=0;
		if(lengthMenosMenorOuIgual) {
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
		String contentReplace = this.content.replace("#", charp);
		String s = Normalizer.normalize(contentReplace, Normalizer.Form.NFD);
		String replaceAll = s.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
		s = replaceAll.replaceAll("[^\\w\\s.,+-]", "");
		String replace = s.replace(charp, "#");
		CcpTextDecorator ccpTextDecorator2 = new CcpTextDecorator(replace);
		return ccpTextDecorator2;
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
			boolean beginIndexMenor = beginIndex < 0;
			if(beginIndexMenor) {
				return list;
			}
			int indexOf = str.indexOf(endDelimiter );
			int endDelimiterLength = endDelimiter.length();
			endIndex = indexOf+ endDelimiterLength;
			boolean endIndexMenor = endIndex < 0;

			if(endIndexMenor) {
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
		Stream<String> stream = asList.stream();
		var filter = stream.filter(predicate);
		List<String> collect = filter.collect(Collectors.toList());
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
		String toString = sb.toString();

		CcpTextDecorator ccpTextDecorator = new CcpTextDecorator(toString);
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
		boolean lengthMaior = split.length > 1;

		if (lengthMaior) {
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



	/**
	 * Substitui os placeholders {@code {nomeDoCampo}} pelos valores correspondentes do JSON de parâmetros.
	 * Suporta também a função dinâmica {@code {currentTimeMillis()}}.
	 */
	public CcpTextDecorator resolveTemplate(CcpJsonRepresentation parameters) {
		Map<String, Object> content = parameters.getContent();
		Set<String> keySet = content.keySet();
		String message = new String(this.content);
		for (String key : keySet) {
			CcpFieldName ccpFieldName = new CcpFieldName(key);
			String value = parameters.getAsString(ccpFieldName);
			String valorMais = "{" + key;
			String valorMaisMais = valorMais + "}";
			message = message.replace(valorMaisMais, value);
		}

		CcpTemplateFunctions[] templateExpressions = CcpTemplateFunctions.values();

		for (CcpTemplateFunctions templateExpression : templateExpressions) {
			String value = templateExpression.get();
			String valorMais2 = "{" + templateExpression;
			String valorMais2Mais = valorMais2 + "()}";
			message = message.replace(valorMais2Mais, value);
		}
		CcpTextDecorator ccpTextDecorator3 = new CcpTextDecorator(message);

		return ccpTextDecorator3;
	}

	/**
	 * Remove recursivamente todos os caracteres {@code c} do início da string.
	 */
	public CcpTextDecorator removeStartingCharacters( char c) {
		String valorMais3 = "" + c;
		boolean startsWith = this.content.startsWith(valorMais3);
		boolean valorIgual = false == startsWith;

		if(valorIgual) {
			return this;
		}

		String substring = this.content.substring(1);
		CcpTextDecorator ccpTextDecorator4 = new CcpTextDecorator(substring);
		CcpTextDecorator removeStartingCharacters = ccpTextDecorator4.removeStartingCharacters(c);
		return removeStartingCharacters;
	}

	/**
	 * Remove recursivamente todos os caracteres {@code c} do final da string.
	 */
	public CcpTextDecorator removeEndingCharacters(char c) {
		String valorMais4 = "" + c;
		boolean endsWith = this.content.endsWith(valorMais4);
		boolean valorIgual2 = false == endsWith;

		if(valorIgual2) {
			return this;
		}
		int contentLength2 = this.content.length();
		int contentLength2Menos = contentLength2 - 1;

		String substring = this.content.substring(0, contentLength2Menos);
		CcpTextDecorator ccpTextDecorator5 = new CcpTextDecorator(substring);
		CcpTextDecorator removed = ccpTextDecorator5.removeEndingCharacters(c);
		return removed;
	}

	/**
	 * Verifica se o texto é um JSON de objeto válido.
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
			CcpStringDecorator ccpStringDecorator = new CcpStringDecorator(string);
			CcpTextDecorator ccpStringDecoratorText = ccpStringDecorator.text();
			CcpTextDecorator capitalize2 = ccpStringDecoratorText.capitalize();
			String capitalize = capitalize2.content;
			sb.append(capitalize);
		}
		String toString2 = sb.toString();
		CcpTextDecorator ccpTextDecorator = new CcpTextDecorator( toString2);
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
			boolean kIgual = k == 0;
			if(kIgual) {
				k++;
				continue;
			}
			boolean cMenor = c < 'A';

			if(cMenor) {
				k++;
				continue;
			}
			boolean cMaior = c > 'Z';
			if(cMaior) {
				k++;
				continue;
			}
			int kMais = k++ + m++;
			sb.insert(kMais, "_");
		}
		String toString3 = sb.toString();
		String toLowerCase = toString3.toLowerCase();
		CcpTextDecorator ccpTextDecorator = new CcpTextDecorator(toLowerCase);
		return ccpTextDecorator;
	}

	/**
	 * Coloca a primeira letra em maiúscula e o restante em minúsculas.
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
	 * Retorna o tamanho do texto encapsulado em {@code CcpNumberDecorator}.
	 */
	public CcpNumberDecorator lenght() {
		int contentLength3 = content.length();
		String valorMais5 = "" + contentLength3;
		CcpNumberDecorator ccpNumberDecorator = new CcpNumberDecorator(valorMais5);
		return ccpNumberDecorator;
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
		String toUpperCase = s1.content.toUpperCase();
		String toUpperCase2 = s2.content.toUpperCase();
		boolean contains2 = toUpperCase.contains(toUpperCase2);
		boolean notContained = false == contains2;

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
		CcpTextDecorator sanitize = this.sanitize(CcpOtherConstants.DELIMITERS_ARRAY);
		return sanitize;
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
		CcpStringDecorator ccpStringDecorator2 = new CcpStringDecorator(upperCase);
		CcpTextDecorator ccpStringDecorator2Text = ccpStringDecorator2.text();
		CcpTextDecorator ctd = ccpStringDecorator2Text.stripAccents();
		return ctd;
	}

	/**
	 * Retorna o texto interno.
	 */
	public String getContent() {
		return this.content;
	}

}
