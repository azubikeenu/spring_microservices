package guru.sfg.beer.order.service.sm.interceptors;


import guru.sfg.beer.order.service.domain.BeerOrder;
import guru.sfg.beer.order.service.domain.BeerOrderEvent;
import guru.sfg.beer.order.service.domain.BeerOrderStatusEnum;
import guru.sfg.beer.order.service.repositories.BeerOrderRepository;
import guru.sfg.beer.order.service.services.BeerOrderManagerImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.state.State;
import org.springframework.statemachine.support.StateMachineInterceptorAdapter;
import org.springframework.statemachine.transition.Transition;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;


@Slf4j
@Service
@RequiredArgsConstructor
public class BeerStateChangedInterceptor extends StateMachineInterceptorAdapter<BeerOrderStatusEnum, BeerOrderEvent> {

    private final BeerOrderRepository beerOrderRepository;

    @Override
    public void preStateChange(State<BeerOrderStatusEnum, BeerOrderEvent> state, Message<BeerOrderEvent> message,
                               Transition<BeerOrderStatusEnum, BeerOrderEvent> transition, StateMachine<BeerOrderStatusEnum,
            BeerOrderEvent> stateMachine, StateMachine<BeerOrderStatusEnum, BeerOrderEvent> rootStateMachine) {
        log.info("Triggering preState change for beerOrder ");
        Optional.ofNullable(message)
                .flatMap(msg -> Optional.ofNullable((String) msg.getHeaders().getOrDefault(BeerOrderManagerImpl.BEER_ORDER_ID, " ")))
                .ifPresent(orderId -> {
                    log.debug("Saving state for order id: " + orderId + " Status: " + state.getId());
                    final Optional<BeerOrder> optionalBeerOrder = beerOrderRepository.findById(UUID.fromString(orderId));
                    optionalBeerOrder.ifPresentOrElse(beerOrder -> {
                        // this sets the status of the beer to the transition status triggered by the stateMachineEvent
                        beerOrder.setOrderStatus(state.getId());
                        beerOrderRepository.save(beerOrder);
                    }, () -> log.error("BeerOrder not found"));
                });
    }

}