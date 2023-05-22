package guru.sfg.beer.order.service.sm.actions;

import common.events.DeallocateOrderRequest;
import guru.sfg.beer.order.service.config.JmsConfig;
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
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

@RequiredArgsConstructor
@Component
@Slf4j
public class DeallocateOrderAction  implements Action<BeerOrderStatusEnum, BeerOrderEvent> {
    private final JmsTemplate jmsTemplate;
    private final BeerOrderRepository beerOrderRepository;
    private final BeerOrderMapper beerOrderMapper;

    @Override
    @Transactional
    public void execute(final StateContext<BeerOrderStatusEnum, BeerOrderEvent> stateContext) {
        log.debug("Firing deallocate order action ");
        String orderId =  (String) stateContext.getMessage().getHeaders().get(BeerOrderManagerImpl.BEER_ORDER_ID);
        beerOrderRepository.findById(UUID.fromString(Objects.requireNonNull(orderId))).ifPresentOrElse(beerOrder -> {
            final DeallocateOrderRequest deallocateOrderRequest =DeallocateOrderRequest.builder()
                    .beerOrderDto(beerOrderMapper.beerOrderToDto(beerOrder)).build();
            jmsTemplate.convertAndSend(JmsConfig.DEALLOCATE_ORDER_QUEUE ,
                    deallocateOrderRequest);
            log.debug("Sent deallocate order request for beer order with id : {} " , orderId);
        },() -> log.error("beerOrder  with id : {} not found" , orderId));

    }
}
