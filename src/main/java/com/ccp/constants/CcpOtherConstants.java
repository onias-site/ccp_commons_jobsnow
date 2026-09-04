package com.ccp.constants;

import java.util.List;
import java.util.function.Function;

import com.ccp.business.CcpBusiness;
import com.ccp.decorators.CcpFieldName;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.decorators.CcpStringDecorator;
import com.ccp.especifications.db.bulk.CcpBulkItem;
import com.ccp.especifications.db.utils.entity.CcpEntity.JsonFieldNames;
import com.ccp.especifications.db.utils.entity.decorators.engine.CcpEntityMetaData;
import com.ccp.flow.CcpErrorFlowDisturb;
import com.ccp.process.CcpProcessStatusDefault;

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

	CcpStringDecorator LETTERS_AND_NUMBERS = new CcpStringDecorator("ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789");

	Function<CcpBulkItem, List<CcpBulkItem>> whenRecordWasNotFoundInTheEntityToSearch = jsn -> {
		CcpEntityMetaData entityDetails = jsn.entity.getEntityMetaData();
		CcpJsonRepresentation put = CcpOtherConstants.EMPTY_JSON.put(JsonFieldNames.entity, entityDetails.entityName);
		CcpErrorFlowDisturb ccpErrorFlowDisturb = new CcpErrorFlowDisturb(put, CcpProcessStatusDefault.NOT_FOUND);
		throw ccpErrorFlowDisturb;
	};

}
