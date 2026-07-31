package com.ccp.aop;

/**
 * Intercepta todos os métodos de todos os projetos dependentes e lança exceção
 * se qualquer parâmetro recebido for nulo.
 */
public aspect CcpNullParameterAspect {

    pointcut anyMethod():
        execution(* com.ccp..*(..))
        || execution(* com.jn..*(..))
        || execution(* com.jb..*(..))
        || execution(* com.vis..*(..));

    pointcut excludeAopPackage():
        within(com.ccp.aop..*);

    pointcut excludeAnnotatedMethod():
        execution(@CcpAllowNullParameter * *(..));

    pointcut monitoredMethod():
        anyMethod()
        && !excludeAopPackage()
        && !excludeAnnotatedMethod()
        && !execution(* *..*lambda$*(..));

    before(): monitoredMethod() {
        Object[] args = thisJoinPoint.getArgs();
        if (args == null || args.length == 0) {
            return;
        }
        String signature = thisJoinPoint.getSignature().toLongString();
        for (int i = 0; i < args.length; i++) {
            if (args[i] == null) {
                throw new CcpNullParameterException(signature, i);
            }
        }
    }
}
