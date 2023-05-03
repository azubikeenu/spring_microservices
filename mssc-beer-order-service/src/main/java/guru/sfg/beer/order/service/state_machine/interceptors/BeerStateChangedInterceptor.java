package guru.sfg.beer.order.service.state_machine.interceptors;


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

@Service
@Slf4j
@RequiredArgsConstructor
public class BeerStateChangedInterceptor extends StateMachineInterceptorAdapter<BeerOrderStatusEnum , BeerOrderEvent> {
    private final BeerOrderRepository beerOrderRepository;

    @Override
    public void preStateChange(final State<BeerOrderStatusEnum, BeerOrderEvent> state,
                               final Message<BeerOrderEvent> message, final Transition<BeerOrderStatusEnum,
            BeerOrderEvent> transition, final StateMachine<BeerOrderStatusEnum, BeerOrderEvent> stateMachine,
                               final StateMachine<BeerOrderStatusEnum, BeerOrderEvent> rootStateMachine) {
        Optional.ofNullable(message)
                .flatMap(msg -> Optional.ofNullable(msg.getHeaders().getOrDefault(BeerOrderManagerImpl.BEER_ORDER_ID, "")))
                .flatMap(beerId -> beerOrderRepository.findById(UUID.fromString(String.valueOf(beerId))))
                .ifPresent(beerOrder -> {
                    // sets the beerOrder State to the current state of the state machine
                    beerOrder.setOrderStatus(state.getId());
                    beerOrderRepository.save(beerOrder);
                });
    }

}