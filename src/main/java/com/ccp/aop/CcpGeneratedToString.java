package com.ccp.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Marca o {@code toString()} introduzido por {@code CcpToStringAspect}.
 *
 * Depois da tecelagem, o método introduzido é indistinguível de um método escrito à mão
 * (não é sintético nem bridge). Esta anotação é o que permite ao
 * {@code CcpToStringBuilder} saber, ao serializar um atributo, se o {@code toString()}
 * daquele tipo é próprio — e portanto deve ser usado como texto — ou se foi gerado pelo
 * aspecto — e portanto o objeto deve ser expandido como JSON aninhado, e não embutido
 * como string escapada.
 *
 * Não deve ser usada manualmente.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface CcpGeneratedToString {

}
