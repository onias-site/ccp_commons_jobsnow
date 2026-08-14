package com.ccp.aop;

/**
 * Interface marcadora aplicada por {@code CcpToStringAspect} (via {@code declare parents})
 * a todas as classes dos pacotes cobertos pelo aspecto.
 *
 * A implementação de {@code toString()} é introduzida nesta interface por ITD
 * (inter-type declaration). O AspectJ só injeta o método nas classes que NÃO possuem
 * {@code toString()} próprio (declarado nelas ou herdado de uma superclasse), portanto
 * nenhuma implementação existente é sobrescrita.
 *
 * Não deve ser implementada manualmente.
 */
public interface CcpToString {

}
