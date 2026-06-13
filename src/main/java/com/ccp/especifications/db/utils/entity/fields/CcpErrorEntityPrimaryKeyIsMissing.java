package com.ccp.especifications.db.utils.entity.fields;

import java.util.List;
import java.util.Set;

import com.ccp.decorators.CcpCollectionDecorator;
import com.ccp.decorators.CcpJsonRepresentation;
import com.ccp.especifications.db.utils.entity.CcpEntity;
import com.ccp.especifications.db.utils.entity.decorators.engine.CcpEntityMetaData;

/**
 * Exceção lançada quando o JSON fornecido não contém todos os campos de chave primária exigidos
 * pela entidade, impedindo o cálculo do ID do documento.
 */
@SuppressWarnings("serial")
public class CcpErrorEntityPrimaryKeyIsMissing extends RuntimeException{
	public final CcpEntityMetaData entityMetadata;
	/** Gera a mensagem listando quais campos de chave primária estão ausentes no JSON. */
	public CcpErrorEntityPrimaryKeyIsMissing(CcpEntity entity, CcpJsonRepresentation json) {
		super(getMessage(entity, json));
		this.entityMetadata = entity.getEntityMetaData();
	}
	
	private static String getMessage(CcpEntity entity, CcpJsonRepresentation json) {
		CcpEntityMetaData entityDetails = entity.getEntityMetaData();
		List<String> onlyPrimaryKey = entityDetails.primaryKeyNames;

		Set<String> fieldSet = json.fieldSet();
		CcpCollectionDecorator ccd = new CcpCollectionDecorator(onlyPrimaryKey);
		List<String> primaryKeyMissing = ccd.getExclusiveList(fieldSet);
		String entityName = entityDetails.entityName;
		
		String message = String.format("The json %s does not provide the required keys '%s' the entity '%s'", json, primaryKeyMissing, entityName);
		
		return message;

	}
}
