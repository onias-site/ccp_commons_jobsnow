package com.ccp.aop;

/**
 * Forbids, at compile time, calling {@code apply(..)} directly on any {@code CcpBusiness} implementation. The right
 * entry point is {@code execute(..)}, which validates the input JSON with {@code CcpJsonValidatorEngine} before
 * delegating to {@code apply}; calling {@code apply} directly skips the validation.
 * <p>
 * The only exceptions are the two gateways that exist precisely to validate before delegating:
 * {@code CcpBusiness.execute(CcpJsonRepresentation)} and {@code CcpService.execute(Map)}.
 * <p>
 * It only applies to calls ({@code call}), not to declarations: every {@code CcpBusiness} implementation still has to
 * define its own {@code apply}.
 */
public aspect CcpForbiddenApplyCallAspect {

    /** Any call to {@code apply(CcpJsonRepresentation)} on a {@code CcpBusiness} subtype. */
    pointcut applyCall():
        call(com.ccp.decorators.CcpJsonRepresentation
             com.ccp.business.CcpBusiness+.apply(com.ccp.decorators.CcpJsonRepresentation));

    /** Code inside the two validating gateways, the only places allowed to call {@code apply}. */
    pointcut insideValidatingGateway():
        within(com.ccp.business.CcpBusiness)
        || within(com.ccp.service.CcpService);

    declare error:
        applyCall() && !insideValidatingGateway()
        : "Call execute(..) instead of apply(..). execute validates the incoming JSON before delegating to apply; calling apply directly skips validation. Only CcpBusiness.execute and CcpService.execute may call apply.";
}
