package com.ccp.aop;

/**
 * Intercepts every method and constructor of the {@code com.ccp}, {@code com.jn}, {@code com.jb} and {@code com.vis}
 * packages and throws {@link CcpNullParameterException} when any received argument is {@code null}.
 * <p>
 * Members annotated with {@link CcpAllowNullParameter}, lambdas and the {@code com.ccp.aop} package itself are not
 * checked.
 */
public aspect CcpNullParameterAspect {

    /** Execution of any method of the covered packages. */
    pointcut anyMethod():
        execution(* com.ccp..*(..))
        || execution(* com.jn..*(..))
        || execution(* com.jb..*(..))
        || execution(* com.vis..*(..));

    /** Execution of any constructor of the covered packages. */
    pointcut anyConstructor():
        execution(com.ccp..new(..))
        || execution(com.jn..new(..))
        || execution(com.jb..new(..))
        || execution(com.vis..new(..));

    /** Code of the aspect infrastructure itself, which is never checked. */
    pointcut excludeAopPackage():
        within(com.ccp.aop..*);

    /** Methods and constructors that explicitly accept {@code null} arguments. */
    pointcut excludeAnnotatedMethod():
        execution(@CcpAllowNullParameter * *(..))
        || execution(@CcpAllowNullParameter *.new(..));

    /** Members whose arguments are checked: methods and constructors of the covered packages, minus the exclusions. */
    pointcut monitoredMethod():
        (anyMethod() || anyConstructor())
        && !excludeAopPackage()
        && !excludeAnnotatedMethod()
        && !execution(* *..*lambda$*(..));

    /** Throws {@link CcpNullParameterException} at the first {@code null} argument, naming its index. */
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
