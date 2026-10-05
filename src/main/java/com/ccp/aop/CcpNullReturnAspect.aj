package com.ccp.aop;

/**
 * Intercepts every non-void method of the {@code com.ccp}, {@code com.jn}, {@code com.jb} and {@code com.vis} packages
 * and throws {@link CcpNullReturnException} when it returns {@code null}.
 * <p>
 * Methods annotated with {@link CcpAllowNullReturn}, lambdas and the {@code com.ccp.aop} package itself are not
 * checked.
 */
public aspect CcpNullReturnAspect {

    /** Execution of any checked non-void method of the covered packages. */
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

    /** Throws {@link CcpNullReturnException} when the method returned {@code null}. */
    after() returning(Object result): anyNonVoidMethod() {
        if (result == null) {
            String signature = thisJoinPoint.getSignature().toLongString();
            throw new CcpNullReturnException(signature);
        }
    }
}
