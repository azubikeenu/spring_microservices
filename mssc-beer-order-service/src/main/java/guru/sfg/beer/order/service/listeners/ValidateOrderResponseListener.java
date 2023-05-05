package guru.sfg.beer.order.service.listeners;

import common.events.ValidateOrderResponse;
import guru.sfg.beer.order.service.config.JmsConfig;
import guru.sfg.beer.order.service.services.BeerOrderManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@RequiredArgsConstructor
public class ValidateOrderResponseListener {
    private final BeerOrderManager beerOrderManager;

    @JmsListener(destination = JmsConfig.VALIDATE_ORDER_RESPONSE)
    public void listen(ValidateOrderResponse validateOrderResponse) {
        var beerId = validateOrderResponse.getBeerId();
        log.debug("processing validation result for beerOrder with id : {} and isValid  {} " , beerId , validateOrderResponse.isValid());
       beerOrderManager.processValidationResult(beerId,validateOrderResponse.isValid());
    }

}
