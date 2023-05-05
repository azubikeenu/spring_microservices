package guru.sfg.beer.order.service.test_components;

import common.events.ValidateOrderRequest;
import common.events.ValidateOrderResponse;
import guru.sfg.beer.order.service.config.JmsConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class BeerOrderValidationListener {
    private final JmsTemplate jmsTemplate;

    @JmsListener(destination = JmsConfig.VALIDATE_ORDER_QUEUE)
    public void listen(Message msg){
        ValidateOrderRequest request = (ValidateOrderRequest) msg.getPayload();


        final ValidateOrderResponse validateOrderResponse = ValidateOrderResponse.builder()
                .isValid(true)
                .beerId(request.getBeerOrderDto().getId())
                .build();

        jmsTemplate.convertAndSend(JmsConfig.VALIDATE_ORDER_RESPONSE,
                validateOrderResponse);
        }
}