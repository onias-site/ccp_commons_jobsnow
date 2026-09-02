package com.ccp.decorators;

import java.text.Normalizer;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Decorator especializado em endereços de e-mail. Oferece validação robusta (com regras de negócio específicas
 * do domínio jobsnow), normalização de acentos, extração de e-mails a partir de texto livre e cálculo de hash do endereço.
 */
public class CcpEmailDecorator implements  CcpDecorator<String>{
	public static final String	EMAIL_REGEX = "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$";

//	private static Set<String> nonProfessionalDomains = new HashSet<>();
//	
//	static {
//		nonProfessionalDomains.add("globalweb.com.br");
//		nonProfessionalDomains.add("localweb.com.br");
//		nonProfessionalDomains.add("protonmail.com");
//		nonProfessionalDomains.add("locaweb.com.br");
//		nonProfessionalDomains.add("outlook.com.br");
//		nonProfessionalDomains.add("yahoo.com.br");
//		nonProfessionalDomains.add("terra.com.br");
//		nonProfessionalDomains.add("outlook.com");
//		nonProfessionalDomains.add("hotmail.com");
//		nonProfessionalDomains.add("uol.com.br");
//		nonProfessionalDomains.add("bol.com.br");
//		nonProfessionalDomains.add("uolinc.com");
//		nonProfessionalDomains.add("yahoo.com");
//		nonProfessionalDomains.add("gmail.com");
//		nonProfessionalDomains.add("ig.com.br");
//		nonProfessionalDomains.add("live.com");
//		nonProfessionalDomains.add("msn.com");
//	}

	
	public final String content;

	/**
	 * Encapsula a string como e-mail.
	 * @param content o endereço de e-mail
	 */
	protected CcpEmailDecorator(String content) {
		this.content = content;
	}

	public String toString() {
		return this.content;
	}

	/**
	 * Remove acentos do endereço. Se já for um e-mail válido, trata as partes local e domínio separadamente
	 * para preservar o {@code @}; caso contrário, aplica normalização NFD genérica.
	 */
	public CcpEmailDecorator stripAccents() {
		boolean valid = this.isValid();
		if(valid) {
			String[] split = this.content.split("@");
			String s1 = split[0];
			String s2 = split[1];
			CcpTextDecorator ccpTextDecorator = new CcpTextDecorator(s1);
			CcpTextDecorator stripAccents2 = ccpTextDecorator.stripAccents();
			String p1 = stripAccents2.content;
			CcpTextDecorator ccpTextDecorator2 = new CcpTextDecorator(s2);
			CcpTextDecorator stripAccents3 = ccpTextDecorator2.stripAccents();
			String p2 = stripAccents3.content;
			String p1Mais = p1 + "@";
			String p1MaisMais = p1Mais + p2;
			CcpEmailDecorator ccpEmailDecorator = new CcpEmailDecorator(p1MaisMais);
			return ccpEmailDecorator;
		}
		
		String s = Normalizer.normalize(this.content, Normalizer.Form.NFD);
		String replaceAll = s.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
		CcpEmailDecorator ccpEmailDecorator = new CcpEmailDecorator(replaceAll);
		return ccpEmailDecorator;
	}
	
	/**
	 * Verifica se a string é um endereço de e-mail válido segundo a regex padrão e regras de negócio do domínio.
	 * @return {@code true} se o endereço for válido
	 */
	public boolean isValid() {
		String[] split = this.content.split("@");
		boolean lengthDiferente = split.length != 2;

		if(lengthDiferente) {
			return false;
		}
		String splitTrim = split[0].trim();
		boolean splitTrimEmpty = splitTrim.isEmpty();
		if(splitTrimEmpty) {
			return false;
		}
		String contentTrim = this.content.trim();
		String toLowerCase = contentTrim.toLowerCase();
		boolean endsWith = toLowerCase.endsWith(".digital");
		if(endsWith) {
			return true;
		}
		String contentTrim2 = this.content.trim();
		String toLowerCase2 = contentTrim2.toLowerCase();
		boolean endsWith2 = toLowerCase2.endsWith("@wayon.global");
		if(endsWith2) {
			return true;
		}
		String contentTrim3 = this.content.trim();
		String toLowerCase3 = contentTrim3.toLowerCase();
		boolean endsWith3 = toLowerCase3.endsWith("@corp.inovation.com.br");
		if(endsWith3) { 
			return true;
		}
		String toLowerCase4 = this.content.toLowerCase();
		boolean endsWith4 = toLowerCase4.endsWith(".docx");
		if(endsWith4) {
			return false;
		}
		String toLowerCase5 = this.content.toLowerCase();
		boolean endsWith5 = toLowerCase5.endsWith(".digi");
		if(endsWith5) {
			return false;
		}
		String toLowerCase6 = this.content.toLowerCase();
		boolean endsWith6 = toLowerCase6.endsWith(".onli");
		if(endsWith6) {
			return false;
		}
		String toLowerCase7 = this.content.toLowerCase();
		boolean endsWith7 = toLowerCase7.endsWith(".glob");
		if(endsWith7) {
			return false;
		}
		String toLowerCase8 = this.content.toLowerCase();
		boolean endsWith8 = toLowerCase8.endsWith(".soci");
		if(endsWith8) {
			return false;
		}
		String toLowerCase9 = this.content.toLowerCase();
		boolean endsWith9 = toLowerCase9.endsWith(".bren");
		if(endsWith9) {
			return false;
		}
		String toLowerCase10 = this.content.toLowerCase();
		boolean contains = toLowerCase10.contains(".coom");

		if(contains) {
			return false;
		}

		Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(this.content);
		boolean find = matcher.find();
		boolean valorIgual = false == find;

		if(valorIgual) {
			return false;
		}
		
		String domain = split[1];
		String[] split2 = domain.split("\\.");
		int lengthMenos = split2.length - 1;
		String last = split2[lengthMenos];
		String toLowerCase11 = last.toLowerCase();
		boolean startsWith = toLowerCase11.startsWith("com");
		boolean startsWithE = startsWith && false == last.toLowerCase().equalsIgnoreCase("com");
		if(startsWithE) {
			return false;
		}
		String toLowerCase12 = last.toLowerCase();
		boolean startsWith2 = toLowerCase12.startsWith("br");
		boolean startsWith2E = startsWith2 && false == last.toLowerCase().equalsIgnoreCase("br");
		if(startsWith2E) {
			return false;
		}
		
		return true;
	}
	private static final Pattern VALID_EMAIL_ADDRESS_REGEX = 
		    Pattern.compile(EMAIL_REGEX, Pattern.CASE_INSENSITIVE);

	/**
	 * Percorre as "palavras" obtidas ao dividir o conteúdo pelos delimitadores fornecidos e retorna
	 * o primeiro trecho reconhecido como e-mail válido. Retorna {@code CcpEmailDecorator("")} se nenhum for encontrado.
	 * @param delimitadores expressão de delimitadores para divisão do texto
	 */
	public CcpEmailDecorator findFirst(String delimitadores) {
		String toLowerCase13 = this.content.toLowerCase();
	
		String[] palavras = toLowerCase13.split(delimitadores);
		for (String palavra : palavras) {
			boolean contains2 = palavra.contains("+");
			if(contains2) {
				String palavraReplace = palavra.replace("+", " ");
				String[] split = palavraReplace.split(" ");
				int lengthMenos2 = split.length - 1;
				String email = split[lengthMenos2];
				CcpEmailDecorator ccpEmailDecorator = new CcpEmailDecorator(email);
				boolean valid2 = ccpEmailDecorator.isValid();
				if(valid2) {
					return ccpEmailDecorator;
				}
			}
			boolean endsWith10 = palavra.endsWith(".");

			if(endsWith10) {
				int palavraLength = palavra.length();
				int palavraLengthMenos = palavraLength - 1;
				palavra = palavra.substring(0, palavraLengthMenos);
			}
			String[] split = palavra.split("@");
			
			String str = "";
			
			for (String string : split) {
				CcpTextDecorator ccpTextDecorator3 = new CcpTextDecorator(string);
				CcpTextDecorator stripAccents4 = ccpTextDecorator3.stripAccents();
				String content = stripAccents4.getContent();
				String contentMais = content + "@";
				str += (contentMais);
			}
			int strLength = str.length();
			int strLengthMenos = strLength - 1;
			String substring = str.substring(0, strLengthMenos);
			CcpEmailDecorator ced = new CcpEmailDecorator(substring);
			boolean valid3 = ced.isValid();
			if(valid3) {
				CcpEmailDecorator stripAccents = ced.stripAccents();
				String toLowerCase14 = stripAccents.content.toLowerCase();
				String retorno = toLowerCase14.trim();
				CcpEmailDecorator ccpEmailDecorator = new CcpEmailDecorator(retorno);
				return ccpEmailDecorator;
			}
		}
		CcpEmailDecorator ccpEmailDecorator = new CcpEmailDecorator("");
		return ccpEmailDecorator;
	}

	/**
	 * Divide o texto pelo delimitador e coleta em um {@code TreeSet} todos os trechos que são e-mails válidos (em minúsculas).
	 * @param delimiter o delimitador para divisão do texto
	 * @return conjunto de e-mails válidos encontrados no texto
	 */
	public Set<String> extractFromText(String delimiter) {
		String[] split = this.content.split(delimiter);
		Set<String> emails = new TreeSet<>();

		for (String piece : split) {
			String trim = piece.trim();
			CcpEmailDecorator decorator = new CcpEmailDecorator(trim);
			boolean valid4 = decorator.isValid();
			boolean invalid = false == valid4;
			if (invalid) {
				continue;
			}
			String lowerCase = trim.toLowerCase();
			emails.add(lowerCase);
		}

		return emails;
	}

	
	/**
	 * Retorna a parte do domínio (após {@code @}). Retorna string vazia se o endereço não tiver exatamente um {@code @}.
	 */
	public String getDomain() {
		String[] split = this.content.split("@");
		boolean lengthDiferente2 = split.length != 2;

		if (lengthDiferente2) {
			return "";
		}

		String domain = split[1];
		return domain;
	}

	/**
	 * Implementação de {@code CcpDecorator}; devolve o endereço.
	 */
	public String getContent() {
		return this.content;
	}
	/**
	 * Cria um {@code CcpHashDecorator} sobre o endereço para cálculo de hash.
	 */
	public CcpHashDecorator hash() {
		CcpHashDecorator ccpHashDecorator = new CcpHashDecorator(this.content);
		return ccpHashDecorator;
	}

}
