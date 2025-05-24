package com.ccp.decorators;

import java.text.Normalizer;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.ccp.constantes.CcpStringConstants;

public class CcpEmailDecorator implements  CcpDecorator<String>{

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

	protected CcpEmailDecorator(String content) {
		this.content = content;
	}

	public String toString() {
		return this.content;
	}

	public CcpEmailDecorator stripAccents() {
		if(this.isValid()) {
			String[] split = this.content.split("@");
			String s1 = split[0];
			String s2 = split[1];
			String p1 = new CcpTextDecorator(s1).stripAccents().content;
			String p2 = new CcpTextDecorator(s2).stripAccents().content;
			return new CcpEmailDecorator(p1 + "@" + p2);
		}
		
		String s = Normalizer.normalize(this.content, Normalizer.Form.NFD);
		String replaceAll = s.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
		CcpEmailDecorator ccpEmailDecorator = new CcpEmailDecorator(replaceAll);
		return ccpEmailDecorator;
	}
	
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
		
		if(find == false) {
			return false;
		}
		
		String domain = split[1];
		String[] split2 = domain.split("\\.");
		String last = split2[split2.length - 1];
		if(last.toLowerCase().startsWith("com") && last.toLowerCase().equalsIgnoreCase("com") == false) {
			return false;
		}
		if(last.toLowerCase().startsWith("br") && last.toLowerCase().equalsIgnoreCase("br") == false) {
			return false;
		}
		
		return true;
	}
	private static final Pattern VALID_EMAIL_ADDRESS_REGEX = 
		    Pattern.compile(CcpStringConstants.EMAIL_REGEX.value, Pattern.CASE_INSENSITIVE);

	public CcpEmailDecorator findFirst(String delimitadores) {

		String[] palavras = this.content.toLowerCase().split(delimitadores);
		for (String palavra : palavras) {
			if(palavra.contains("+")) {
				String[] split = palavra.replace("+", " ").split(" ");
				if(split.length == 0) {
					continue;
				}
				String email = split[split.length - 1];
				CcpEmailDecorator ccpEmailDecorator = new CcpEmailDecorator(email);
				if(ccpEmailDecorator.isValid()) {
					return ccpEmailDecorator;
				}
			}

			if(palavra.endsWith(".")) {
				palavra = palavra.substring(0, palavra.length() - 1);
			}
			
			CcpEmailDecorator ced = new CcpEmailDecorator(palavra);
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

	public Set<String> extractFromText(String delimiter) {
		String[] split = this.content.split(delimiter);
		Set<String> emails = new TreeSet<>();

		for (String piece : split) {
			if (new CcpEmailDecorator(piece).isValid()) {
				emails.add(piece.trim().toLowerCase());
			}
		}

		return emails;
	}

	
	public String getDomain() {
		String[] split = this.content.split("@");

		if (split.length != 2) {
			return "";
		}

		String domain = split[1];
		return domain;
	}

	public String getContent() {
		return this.content;
	}
	public CcpHashDecorator hash() {
		return new CcpHashDecorator(this.content);
	}

}
