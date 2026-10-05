package com.ccp.aop;

/**
 * Turns every checked exception (subclasses of {@code java.lang.Exception}, except {@code RuntimeException}) into an
 * unchecked {@code SoftException} at compile time.
 * <p>
 * Effect: projects compiled with ajc do not need to declare {@code throws} nor wrap calls in try/catch for checked
 * exceptions in the packages covered by the pointcut below.
 * <p>
 * IMPORTANT: this aspect only removes compile errors when the dependent project compiles through ajc
 * (aspectj-maven-plugin) with this jar in the aspectpath. With LTW (JVM agent) it converts the exceptions at runtime,
 * but javac still requires the {@code throws} clauses in the source code.
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
        && 
        !within(com.ccp.aop..*);
}
