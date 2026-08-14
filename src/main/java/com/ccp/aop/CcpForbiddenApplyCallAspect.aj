package com.ccp.aop;

/**
 * Proíbe, em tempo de compilação, a chamada direta a {@code apply(..)} sobre qualquer
 * implementação de {@code CcpBusiness}. O ponto de entrada correto é {@code execute(..)},
 * que valida o JSON de entrada com {@code CcpJsonValidatorEngine} antes de delegar para
 * {@code apply}. Chamar {@code apply} diretamente pula a validação.
 *
 * As únicas exceções são os dois gateways que existem justamente para validar antes de
 * delegar: {@code CcpBusiness.execute(CcpJsonRepresentation)} e
 * {@code CcpService.execute(Map)}.
 *
 * Vale apenas para chamadas ({@code call}), não para as declarações: toda implementação
 * de {@code CcpBusiness} continua obrigada a definir seu próprio {@code apply}.
 */
public aspect CcpForbiddenApplyCallAspect {

    pointcut applyCall():
        call(com.ccp.decorators.CcpJsonRepresentation
             com.ccp.business.CcpBusiness+.apply(com.ccp.decorators.CcpJsonRepresentation));

    pointcut insideValidatingGateway():
        within(com.ccp.business.CcpBusiness)
        || within(com.ccp.service.CcpService);

    declare error:
        applyCall() && !insideValidatingGateway()
        : "Call execute(..) instead of apply(..). execute validates the incoming JSON before delegating to apply; calling apply directly skips validation. Only CcpBusiness.execute and CcpService.execute may call apply.";
}
