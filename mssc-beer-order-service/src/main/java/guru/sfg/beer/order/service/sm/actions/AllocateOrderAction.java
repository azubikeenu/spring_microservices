package guru.sfg.beer.order.service.sm.actions;

import common.events.AllocateOrderRequest;
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
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
public class AllocateOrderAction implements Action<BeerOrderStatusEnum , BeerOrderEvent> {
    private final JmsTemplate jmsTemplate;
    private final BeerOrderRepository beerOrderRepository;
    private final BeerOrderMapper beerOrderMapper;

    @Override
    @Transactional
    public void execute(final StateContext<BeerOrderStatusEnum, BeerOrderEvent> stateContext) {
        log.debug("Firing allocate order action ");
        String orderId =  (String) stateContext.getMessage().getHeaders().get(BeerOrderManagerImpl.BEER_ORDER_ID);
        final BeerOrder beerOrder = beerOrderRepository.findById(UUID.fromString(Objects.requireNonNull(orderId)))
                .orElseThrow(() -> new RuntimeException("Beer with id " + orderId + " Not found"));

        final AllocateOrderRequest allocateOrderRequest = AllocateOrderRequest.builder()
                .beerOrderDto(beerOrderMapper.beerOrderToDto(beerOrder)).build();

        jmsTemplate.convertAndSend(JmsConfig.ALLOCATE_ORDER_QUEUE ,
                allocateOrderRequest);
        log.debug("Sent allocate order request for order with id : {} " , orderId);

    }
}
