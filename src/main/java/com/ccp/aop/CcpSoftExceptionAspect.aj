package com.ccp.aop;

/**
 * Converte todas as checked exceptions (filhas de java.lang.Exception, exceto
 * RuntimeException) em SoftException (unchecked) em tempo de compilação.
 *
 * Efeito: os projetos dependentes compilados com ajc não precisam mais declarar
 * "throws" nem envolver chamadas em try/catch para checked exceptions nos
 * pacotes cobertos pelo pointcut abaixo.
 *
 * IMPORTANTE: este aspecto só elimina erros de compilação quando o projeto
 * dependente compila via ajc (aspectj-maven-plugin) com este jar no aspectpath.
 * Com LTW (agente JVM) ele converte as exceções em runtime, mas o javac ainda
 * exige os "throws" no código-fonte.
 */
public aspect CcpSoftExceptionAspect {

    declare soft: Exception+:
        (
            execution(* com.ccp..*(..))
            || execution(* com.jn..*(..))
            || execution(* com.jb..*(..))
            || execution(* com.vis..*(..))
            || execution(com.ccp..*.new(..))
            || execution(com.jn..*.new(..))
            || execution(com.jb..*.new(..))
            || execution(com.vis..*.new(..))
        )
        && !within(com.ccp.aop..*);
}
