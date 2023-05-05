package com.azubike.msscbeerservice.listeners;

import com.azubike.msscbeerservice.config.JmsConfig;
import common.events.ValidateOrderRequest;
import common.events.ValidateOrderResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ValidateRequestListener {
    private final ValidateRequest validateRequest;
    private final JmsTemplate jmsTemplate;

    @JmsListener(destination = JmsConfig.VALIDATE_ORDER_QUEUE)
    public void listen(ValidateOrderRequest validateOrderRequest){
        final boolean isValid = validateRequest.performValidation(validateOrderRequest.getBeerOrderDto());
        log.debug("Sending a validation response for beerOrder with id {} " , validateOrderRequest.getBeerOrderDto().getId() );
        final ValidateOrderResponse validateOrderResponse = ValidateOrderResponse.builder()
                .isValid(isValid).beerId(validateOrderRequest.getBeerOrderDto().getId()).build();

        jmsTemplate.convertAndSend(JmsConfig.VALIDATE_ORDER_RESPONSE ,
                validateOrderResponse);
    }
}
