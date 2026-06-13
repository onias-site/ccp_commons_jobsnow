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
			String p1 = new CcpTextDecorator(s1).stripAccents().content;
			String p2 = new CcpTextDecorator(s2).stripAccents().content;
			CcpEmailDecorator ccpEmailDecorator = new CcpEmailDecorator(p1 + "@" + p2);
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

		if(split.length != 2) {
			return false;
		}
		if(split[0].trim().isEmpty()) {
			return false;
		}
		if(this.content.trim().toLowerCase().endsWith(".digital")) {
			return true;
		}
		if(this.content.trim().toLowerCase().endsWith("@wayon.global")) {
			return true;
		}
		if(this.content.trim().toLowerCase().endsWith("@corp.inovation.com.br")) { 
			return true;
		}
		if(this.content.toLowerCase().endsWith(".docx")) {
			return false;
		}
		if(this.content.toLowerCase().endsWith(".digi")) {
			return false;
		}
		if(this.content.toLowerCase().endsWith(".onli")) {
			return false;
		}
		if(this.content.toLowerCase().endsWith(".glob")) {
			return false;
		}
		if(this.content.toLowerCase().endsWith(".soci")) {
			return false;
		}
		if(this.content.toLowerCase().endsWith(".bren")) {
			return false;
		}

		if(this.content.toLowerCase().contains(".coom")) {
			return false;
		}

		Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(this.content);
		boolean find = matcher.find();
		
		if(false == find) {
			return false;
		}
		
		String domain = split[1];
		String[] split2 = domain.split("\\.");
		String last = split2[split2.length - 1];
		if(last.toLowerCase().startsWith("com") && false == last.toLowerCase().equalsIgnoreCase("com")) {
			return false;
		}
		if(last.toLowerCase().startsWith("br") && false == last.toLowerCase().equalsIgnoreCase("br")) {
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
		
		String[] palavras = this.content.toLowerCase().split(delimitadores);
		for (String palavra : palavras) {
			if(palavra.contains("+")) {
				String[] split = palavra.replace("+", " ").split(" ");
				String email = split[split.length - 1];
				CcpEmailDecorator ccpEmailDecorator = new CcpEmailDecorator(email);
				if(ccpEmailDecorator.isValid()) {
					return ccpEmailDecorator;
				}
			}

			if(palavra.endsWith(".")) {
				palavra = palavra.substring(0, palavra.length() - 1);
			}
			String[] split = palavra.split("@");
			
			String str = "";
			
			for (String string : split) {
				String content = new CcpTextDecorator(string).stripAccents().getContent();
				str += (content + "@");
			}
			String substring = str.substring(0, str.length() - 1);
			CcpEmailDecorator ced = new CcpEmailDecorator(substring);
			if(ced.isValid()) {
				CcpEmailDecorator stripAccents = ced.stripAccents();
				String retorno = stripAccents.content.toLowerCase().trim();
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
			boolean invalid = false == decorator.isValid();
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

		if (split.length != 2) {
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
		return new CcpHashDecorator(this.content);
	}

}
