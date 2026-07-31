package com.ccp.aop;

/**
 * Intercepta todos os métodos não-void de todos os projetos dependentes e lança
 * exceção se o retorno for nulo.
 */
public aspect CcpNullReturnAspect {

    pointcut anyNonVoidMethod():
        (
            execution(!void com.ccp..*(..))
            || execution(!void com.jn..*(..))
            || execution(!void com.jb..*(..))
            || execution(!void com.vis..*(..))
        )
        && !within(com.ccp.aop..*)
        && !execution(@CcpAllowNullReturn * *(..))
        && !execution(!void *..*lambda$*(..));

    after() returning(Object result): anyNonVoidMethod() {
        if (result == null) {
            String signature = thisJoinPoint.getSignature().toLongString();
            throw new CcpNullReturnException(signature);
        }
    }
}
