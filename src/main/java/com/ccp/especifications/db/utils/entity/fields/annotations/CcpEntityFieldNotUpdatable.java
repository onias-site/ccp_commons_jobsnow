package com.ccp.especifications.db.utils.entity.fields.annotations;

import static java.lang.annotation.RetentionPolicy.RUNTIME;

import java.lang.annotation.Retention;

/**
 * Marca um campo de entidade como não atualizável. Campos com esta anotação são excluídos da lista
 * de campos atualizáveis em {@code CcpEntityMetaData}, impedindo sua modificação em operações de
 * update sem afetar a chave primária.
 */
@Retention(RUNTIME)
public @interface CcpEntityFieldNotUpdatable {
}
