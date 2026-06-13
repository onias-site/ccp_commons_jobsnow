package com.ccp.especifications.db.utils.entity.decorators.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marca uma entidade como somente leitura. Quando presente, o decorator {@code DecoratorReadOnlyEntity}
 * é aplicado, impedindo qualquer operação de escrita ({@code save}, {@code delete}, {@code transferDataTo})
 * e lançando exceção ao tentar executá-las.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target({ ElementType.TYPE })
public @interface CcpEntityOlyReadable{}
