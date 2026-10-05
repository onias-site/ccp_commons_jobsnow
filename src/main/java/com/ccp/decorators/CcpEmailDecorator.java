package com.ccp.decorators;

import java.text.Normalizer;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Decorator specialized in e-mail addresses. Offers robust validation (with business rules specific
 * to the jobsnow domain), accent normalization, extraction of e-mails from free text and hash calculation of the address.
 */
public class CcpEmailDecorator implements  CcpDecorator<String>{
	/** Basic syntax of an e-mail address: local part, {@code @}, domain and a top-level domain of at least two letters. */
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

	
	/** The e-mail address. */
	public final String content;

	/**
	 * Wraps the string as an e-mail.
	 * @param content the e-mail address
	 */
	protected CcpEmailDecorator(String content) {
		this.content = content;
	}

	/**
	 * Returns the address.
	 * @return the address
	 */
	public String toString() {
		return this.content;
	}

	/**
	 * Removes accents from the address. If it is already a valid e-mail, handles the local and domain parts separately
	 * to preserve the {@code @}; otherwise applies a generic NFD normalization.
	 */
	public CcpEmailDecorator stripAccents() {
		boolean valid = this.isValid();
		if(valid) {
			String[] split = this.content.split("@");
			String localPart = split[0];
			String domainPart = split[1];
			CcpTextDecorator localPartText = new CcpTextDecorator(localPart);
			CcpTextDecorator strippedLocalPart = localPartText.stripAccents();
			String localPartWithoutAccents = strippedLocalPart.content;
			CcpTextDecorator domainPartText = new CcpTextDecorator(domainPart);
			CcpTextDecorator strippedDomainPart = domainPartText.stripAccents();
			String domainPartWithoutAccents = strippedDomainPart.content;
			String localPartWithAt = localPartWithoutAccents + "@";
			String emailWithoutAccents = localPartWithAt + domainPartWithoutAccents;
			CcpEmailDecorator ccpEmailDecorator = new CcpEmailDecorator(emailWithoutAccents);
			return ccpEmailDecorator;
		}
		
		String normalizedContent = Normalizer.normalize(this.content, Normalizer.Form.NFD);
		String contentWithoutAccents = normalizedContent.replaceAll("[\\p{InCombiningDiacriticalMarks}]", "");
		CcpEmailDecorator ccpEmailDecorator = new CcpEmailDecorator(contentWithoutAccents);
		return ccpEmailDecorator;
	}
	
	/**
	 * Tells whether the text is a valid e-mail address, applying, in this order:
	 * <ol>
	 * <li>exactly one {@code @} and a non-blank local part, otherwise invalid;</li>
	 * <li>addresses ending with {@code .digital}, {@code @wayon.global} or {@code @corp.inovation.com.br} (case
	 * insensitive) are always valid;</li>
	 * <li>addresses ending with {@code .docx}, {@code .digi}, {@code .onli}, {@code .glob}, {@code .soci}, {@code .bren} or
	 * containing {@code .coom} are invalid (truncated or misspelled domains found in imported data);</li>
	 * <li>the address must match {@link #EMAIL_REGEX};</li>
	 * <li>a top-level domain starting with {@code com} or {@code br} must be exactly {@code com} or {@code br}.</li>
	 * </ol>
	 * @return {@code true} if the address is valid
	 */
	public boolean isValid() {
		String[] split = this.content.split("@");
		boolean hasNotExactlyOneAt = split.length != 2;

		if(hasNotExactlyOneAt) {
			return false;
		}
		String localPartTrimmed = split[0].trim();
		boolean localPartIsEmpty = localPartTrimmed.isEmpty();
		if(localPartIsEmpty) {
			return false;
		}
		String trimmedForDigitalCheck = this.content.trim();
		String lowerForDigitalCheck = trimmedForDigitalCheck.toLowerCase();
		boolean isDigitalDomain = lowerForDigitalCheck.endsWith(".digital");
		if(isDigitalDomain) {
			return true;
		}
		String trimmedForWayonCheck = this.content.trim();
		String lowerForWayonCheck = trimmedForWayonCheck.toLowerCase();
		boolean isWayonDomain = lowerForWayonCheck.endsWith("@wayon.global");
		if(isWayonDomain) {
			return true;
		}
		String trimmedForInovationCheck = this.content.trim();
		String lowerForInovationCheck = trimmedForInovationCheck.toLowerCase();
		boolean isInovationDomain = lowerForInovationCheck.endsWith("@corp.inovation.com.br");
		if(isInovationDomain) { 
			return true;
		}
		String lowerForDocxCheck = this.content.toLowerCase();
		boolean endsWithDocx = lowerForDocxCheck.endsWith(".docx");
		if(endsWithDocx) {
			return false;
		}
		String lowerForDigiCheck = this.content.toLowerCase();
		boolean endsWithDigi = lowerForDigiCheck.endsWith(".digi");
		if(endsWithDigi) {
			return false;
		}
		String lowerForOnliCheck = this.content.toLowerCase();
		boolean endsWithOnli = lowerForOnliCheck.endsWith(".onli");
		if(endsWithOnli) {
			return false;
		}
		String lowerForGlobCheck = this.content.toLowerCase();
		boolean endsWithGlob = lowerForGlobCheck.endsWith(".glob");
		if(endsWithGlob) {
			return false;
		}
		String lowerForSociCheck = this.content.toLowerCase();
		boolean endsWithSoci = lowerForSociCheck.endsWith(".soci");
		if(endsWithSoci) {
			return false;
		}
		String lowerForBrenCheck = this.content.toLowerCase();
		boolean endsWithBren = lowerForBrenCheck.endsWith(".bren");
		if(endsWithBren) {
			return false;
		}
		String lowerForCoomCheck = this.content.toLowerCase();
		boolean containsCoom = lowerForCoomCheck.contains(".coom");

		if(containsCoom) {
			return false;
		}

		Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(this.content);
		boolean matchesRegex = matcher.find();
		boolean doesNotMatchRegex = false == matchesRegex;

		if(doesNotMatchRegex) {
			return false;
		}
		
		String domain = split[1];
		String[] domainParts = domain.split("\\.");
		int lastPartIndex = domainParts.length - 1;
		String topLevelDomain = domainParts[lastPartIndex];
		String lowerTopLevelDomain = topLevelDomain.toLowerCase();
		boolean startsWithCom = lowerTopLevelDomain.startsWith("com");
		boolean isMisspelledCom = startsWithCom && false == topLevelDomain.toLowerCase().equalsIgnoreCase("com");
		if(isMisspelledCom) {
			return false;
		}
		String lowerTopLevelDomainForBrCheck = topLevelDomain.toLowerCase();
		boolean startsWithBr = lowerTopLevelDomainForBrCheck.startsWith("br");
		boolean isMisspelledBr = startsWithBr && false == topLevelDomain.toLowerCase().equalsIgnoreCase("br");
		if(isMisspelledBr) {
			return false;
		}
		
		return true;
	}
	/** {@link #EMAIL_REGEX} compiled case insensitive. */
	private static final Pattern VALID_EMAIL_ADDRESS_REGEX = 
		    Pattern.compile(EMAIL_REGEX, Pattern.CASE_INSENSITIVE);

	/**
	 * Looks for the first valid e-mail address in a free text. The lowercase text is split by the delimiters and, for
	 * each word: if it contains {@code +}, the piece after the last {@code +} is tried; then a trailing dot is removed,
	 * the accents of each part are stripped and the result is tried. The first valid candidate is returned lowercase,
	 * trimmed and without accents.
	 * @param delimiters regular expression of the delimiters used to split the text
	 * @return the first address found, or a decorator over {@code ""} when there is none
	 */
	public CcpEmailDecorator findFirst(String delimiters) {
		String lowerContent = this.content.toLowerCase();
	
		String[] words = lowerContent.split(delimiters);
		for (String word : words) {
			boolean containsPlus = word.contains("+");
			if(containsPlus) {
				String wordWithSpaces = word.replace("+", " ");
				String[] split = wordWithSpaces.split(" ");
				int lastPieceIndex = split.length - 1;
				String email = split[lastPieceIndex];
				CcpEmailDecorator ccpEmailDecorator = new CcpEmailDecorator(email);
				boolean isValidEmail = ccpEmailDecorator.isValid();
				if(isValidEmail) {
					return ccpEmailDecorator;
				}
			}
			boolean endsWithDot = word.endsWith(".");

			if(endsWithDot) {
				int wordLength = word.length();
				int lengthWithoutDot = wordLength - 1;
				word = word.substring(0, lengthWithoutDot);
			}
			String[] split = word.split("@");
			
			String rebuiltEmail = "";
			
			for (String piece : split) {
				CcpTextDecorator pieceText = new CcpTextDecorator(piece);
				CcpTextDecorator strippedPiece = pieceText.stripAccents();
				String pieceWithoutAccents = strippedPiece.getContent();
				String pieceWithAt = pieceWithoutAccents + "@";
				rebuiltEmail += (pieceWithAt);
			}
			int rebuiltEmailLength = rebuiltEmail.length();
			int lengthWithoutLastAt = rebuiltEmailLength - 1;
			String substring = rebuiltEmail.substring(0, lengthWithoutLastAt);
			CcpEmailDecorator candidateEmail = new CcpEmailDecorator(substring);
			boolean candidateIsValid = candidateEmail.isValid();
			if(candidateIsValid) {
				CcpEmailDecorator candidateWithoutAccents = candidateEmail.stripAccents();
				String lowerCandidate = candidateWithoutAccents.content.toLowerCase();
				String normalizedEmail = lowerCandidate.trim();
				CcpEmailDecorator ccpEmailDecorator = new CcpEmailDecorator(normalizedEmail);
				return ccpEmailDecorator;
			}
		}
		CcpEmailDecorator ccpEmailDecorator = new CcpEmailDecorator("");
		return ccpEmailDecorator;
	}

	/**
	 * Splits the text by the delimiter and collects, sorted and lowercase, every trimmed piece that is a valid e-mail.
	 * @param delimiter regular expression of the delimiter used to split the text
	 * @return the sorted set of valid e-mails found in the text
	 */
	public Set<String> extractFromText(String delimiter) {
		String[] split = this.content.split(delimiter);
		Set<String> emails = new TreeSet<>();

		for (String piece : split) {
			String trim = piece.trim();
			CcpEmailDecorator decorator = new CcpEmailDecorator(trim);
			boolean isValidEmail = decorator.isValid();
			boolean invalid = false == isValidEmail;
			if (invalid) {
				continue;
			}
			String lowerCase = trim.toLowerCase();
			emails.add(lowerCase);
		}

		return emails;
	}

	
	/**
	 * Returns the domain part (after {@code @}).
	 * @return the domain, or an empty text when the address does not have exactly one {@code @}
	 */
	public String getDomain() {
		String[] split = this.content.split("@");
		boolean hasNotExactlyOneAt = split.length != 2;

		if (hasNotExactlyOneAt) {
			return "";
		}

		String domain = split[1];
		return domain;
	}

	/**
	 * Returns the address.
	 * @return the address
	 */
	public String getContent() {
		return this.content;
	}
	/**
	 * Creates a {@code CcpHashDecorator} over the address, to compute its hash.
	 * @return the hash decorator
	 */
	public CcpHashDecorator hash() {
		CcpHashDecorator ccpHashDecorator = new CcpHashDecorator(this.content);
		return ccpHashDecorator;
	}

}
