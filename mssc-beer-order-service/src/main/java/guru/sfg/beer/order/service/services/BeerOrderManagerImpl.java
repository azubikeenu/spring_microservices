package guru.sfg.beer.order.service.services;

import guru.sfg.beer.order.service.domain.BeerOrder;
import guru.sfg.beer.order.service.domain.BeerOrderEvent;
import guru.sfg.beer.order.service.domain.BeerOrderStatusEnum;
import guru.sfg.beer.order.service.repositories.BeerOrderRepository;
import guru.sfg.beer.order.service.state_machine.interceptors.BeerStateChangedInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
@Slf4j
@RequiredArgsConstructor
public class BeerOrderManagerImpl implements BeerOrderManager {

    public static final String BEER_ORDER_ID= "beer_order_id";
    private final BeerOrderRepository beerOrderRepository;
    private final StateMachineFactory<BeerOrderStatusEnum, BeerOrderEvent> factory;

    private final BeerStateChangedInterceptor beerStateChangedInterceptor;

    @Override
    public BeerOrder newBeerOrder(final BeerOrder beerOrder) {
        beerOrder.setOrderStatus(BeerOrderStatusEnum.NEW);
        beerOrder.setId(null);
        var savedBeer = beerOrderRepository.save(beerOrder);
         sendEvent(beerOrder , BeerOrderEvent.VALIDATE_ORDER);
        return savedBeer;
    }

    public void processValidationResult(UUID beerOrderId, Boolean isValid) {
         BeerOrder beerOrder = beerOrderRepository.findOneById(beerOrderId);
        if (isValid) {
            sendEvent(beerOrder, BeerOrderEvent.VALIDATION_PASSED);
            BeerOrder validatedOrder = beerOrderRepository.findOneById(beerOrderId);
            sendEvent(validatedOrder , BeerOrderEvent.ALLOCATE_ORDER);
        } else {
            sendEvent(beerOrder, BeerOrderEvent.VALIDATION_FAILED);
        }
    }



        private void sendEvent(BeerOrder beerOrder, BeerOrderEvent beerOrderEvent) {
        final StateMachine<BeerOrderStatusEnum, BeerOrderEvent> sm = build(beerOrder);
        final Message<BeerOrderEvent> message = MessageBuilder.withPayload(beerOrderEvent)
                .setHeader(BEER_ORDER_ID, beerOrder.getId().toString()).build();
        sm.sendEvent(message);
    }

    private StateMachine<BeerOrderStatusEnum, BeerOrderEvent> build(BeerOrder beerOrder) {
        var sm = factory.getStateMachine(beerOrder.getId());
        sm.stop();
        sm.getStateMachineAccessor()
                .doWithAllRegions(sma ->{
                    sma.addStateMachineInterceptor(beerStateChangedInterceptor);
                    sma.resetStateMachine(new DefaultStateMachineContext<>(beerOrder.getOrderStatus()
                            , null, null, null));
                });
        sm.start();
        return sm;
    }
}
