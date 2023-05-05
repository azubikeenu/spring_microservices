package guru.sfg.beer.order.service.sm.actions;

import common.events.ValidateOrderRequest;
import guru.sfg.beer.order.service.config.JmsConfig;
import guru.sfg.beer.order.service.domain.BeerOrder;
import guru.sfg.beer.order.service.domain.BeerOrderEvent;
import guru.sfg.beer.order.service.domain.BeerOrderStatusEnum;
import guru.sfg.beer.order.service.repositories.BeerOrderRepository;
import guru.sfg.beer.order.service.services.BeerOrderManagerImpl;
import guru.sfg.beer.order.service.web.mappers.BeerOrderMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Slf4j
@RequiredArgsConstructor
@Service
public class ValidateOrderAction implements Action<BeerOrderStatusEnum, BeerOrderEvent> {
    private final JmsTemplate jmsTemplate;
    private final BeerOrderRepository beerOrderRepository;
    private final BeerOrderMapper beerOrderMapper;

    @Override
    public void execute(final StateContext<BeerOrderStatusEnum, BeerOrderEvent> stateContext) {
        log.debug("Firing validate order action ");
        var orderId = String.valueOf(stateContext.getMessageHeader(BeerOrderManagerImpl.BEER_ORDER_ID));
        final Optional<BeerOrder> beerOrderOptional = beerOrderRepository.findById(UUID.fromString(orderId));
        beerOrderOptional.ifPresentOrElse(beerOrder -> {
            jmsTemplate.convertAndSend(JmsConfig.VALIDATE_ORDER_QUEUE,
                    ValidateOrderRequest.builder().beerOrderDto(beerOrderMapper.beerOrderToDto(beerOrder)).build());
            log.debug("Sent validate order request for order with id : {} ", orderId);
        }, () -> log.error("beerOrder with id {} not found", orderId));
    }
}
