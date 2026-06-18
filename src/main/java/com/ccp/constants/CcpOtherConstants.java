package com.ccp.constants;

import com.ccp.business.CcpBusiness;
import com.ccp.decorators.CcpFieldName;
import com.ccp.decorators.CcpJsonRepresentation;

/**
 * Repositório de constantes globais compartilhadas em todo o sistema.
 * Define instâncias reusáveis de negócios triviais, um JSON vazio canônico, delimitadores de texto e um campo de nome vazio.
 */
public interface CcpOtherConstants {

	/** {@code CcpBusiness} que ignora o JSON de entrada e sempre retorna {@code EMPTY_JSON}. Útil como valor padrão ou no-op que zera o resultado. */
	CcpBusiness RETURNS_EMPTY_JSON = x -> CcpOtherConstants.EMPTY_JSON;
	/** {@code CcpBusiness} que devolve o próprio JSON de entrada sem modificação. Padrão pass-through. */
	CcpBusiness DO_NOTHING = json -> json;
	/** Instância canônica de {@code CcpJsonRepresentation} vazio. Evita criação repetida de mapas vazios. */
	CcpJsonRepresentation EMPTY_JSON = CcpJsonRepresentation.getEmptyJson();
	/** Array de strings com os delimitadores textuais mais comuns (barra, ponto, vírgula, etc.), usado para tokenizar e sanitizar texto. */
	String[] DELIMITERS_ARRAY = new String[] {"/", "\\", ".","\t", "\n", ":", "," , ";", "!", "?", "[", "]", "{", "}", "<", ">", "=", "(", ")", "'", "`",  "\""};
	/** Expressão regular equivalente ao array de delimitadores, pronta para uso com {@code String.split()} ou {@code Pattern}. */
	String DELIMITERS = "\r|\t|\n|\\s|\\:|\\,|\\-|\\;|\\!|\\?|\\[|\\]|\\{|\\}|\\<|\\>|\\=|\\(|\\)\\ |\\'|\\\"|\\`|\\.";
	/** Implementação de {@code CcpJsonFieldName} cujo valor é a string vazia. Representa ausência de nome de campo. */
	CcpFieldName EMPTY_STRING = new CcpFieldName("");
}
