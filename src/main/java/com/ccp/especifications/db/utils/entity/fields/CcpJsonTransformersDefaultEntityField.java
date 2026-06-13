package com.ccp.especifications.db.utils.entity.fields
;

import com.ccp.business.CcpBusiness;

/**
 * Contrato para transformadores padrão de campos de entidade. Estende {@code CcpBusiness} para
 * poder ser usado como função de transformação e acrescenta os métodos de verificação de
 * elegibilidade como chave primária e de retorno do nome do campo.
 */
public interface CcpJsonTransformersDefaultEntityField extends CcpBusiness{
	/** Retorna {@code true} se este transformador pode ser aplicado a campos de chave primária. */
	boolean canBePrimaryKey();
	/** Retorna o nome do campo associado a este transformador. */
	String name();
}
