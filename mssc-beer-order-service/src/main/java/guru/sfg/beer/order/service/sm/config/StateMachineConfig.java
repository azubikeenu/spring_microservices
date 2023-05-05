package guru.sfg.beer.order.service.sm.config;

import guru.sfg.beer.order.service.domain.BeerOrderEvent;
import guru.sfg.beer.order.service.domain.BeerOrderStatusEnum;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.action.Action;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineConfigurationConfigurer;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;
import org.springframework.statemachine.listener.StateMachineListenerAdapter;
import org.springframework.statemachine.state.State;

import java.util.EnumSet;

@Configuration
@Slf4j
@EnableStateMachineFactory
@RequiredArgsConstructor
public class StateMachineConfig extends StateMachineConfigurerAdapter<BeerOrderStatusEnum , BeerOrderEvent> {
    private final Action<BeerOrderStatusEnum, BeerOrderEvent> validateOrderAction;
    private final Action<BeerOrderStatusEnum, BeerOrderEvent>  allocateOrderAction;
    @Override
    public void configure(final StateMachineStateConfigurer<BeerOrderStatusEnum, BeerOrderEvent> states) throws Exception {
        states.withStates()
                .initial(BeerOrderStatusEnum.NEW)
                .states(EnumSet.allOf(BeerOrderStatusEnum.class))
                .end(BeerOrderStatusEnum.PICKED_UP)
                .end(BeerOrderStatusEnum.DELIVERED)
                .end(BeerOrderStatusEnum.DELIVERY_EXCEPTION)
                .end(BeerOrderStatusEnum.VALIDATION_EXCEPTION)
                .end(BeerOrderStatusEnum.ALLOCATION_EXCEPTION);
    }

    @Override
    public void configure( StateMachineTransitionConfigurer<BeerOrderStatusEnum, BeerOrderEvent> transitions) throws Exception {
        transitions.withExternal()
                .source(BeerOrderStatusEnum.NEW).target(BeerOrderStatusEnum.VALIDATION_PENDING)
                .event(BeerOrderEvent.VALIDATE_ORDER).action(validateOrderAction)
                .and().withExternal()
                .source(BeerOrderStatusEnum.VALIDATION_PENDING).target(BeerOrderStatusEnum.VALIDATED)
                .event(BeerOrderEvent.VALIDATION_PASSED)
                .and().withExternal()
                .source(BeerOrderStatusEnum.VALIDATION_PENDING).target(BeerOrderStatusEnum.VALIDATION_EXCEPTION)
                .event(BeerOrderEvent.VALIDATION_FAILED)
                .and().withExternal()
                .source(BeerOrderStatusEnum.VALIDATED).target(BeerOrderStatusEnum.ALLOCATION_PENDING)
                .event(BeerOrderEvent.ALLOCATE_ORDER).action(allocateOrderAction)
                .and().withExternal()
                .source(BeerOrderStatusEnum.ALLOCATION_PENDING).target(BeerOrderStatusEnum.ALLOCATED)
                .event(BeerOrderEvent.ALLOCATION_SUCCESS)
                .and().withExternal()
                .source(BeerOrderStatusEnum.ALLOCATION_PENDING).target(BeerOrderStatusEnum.ALLOCATION_EXCEPTION)
                .event(BeerOrderEvent.ALLOCATION_FAILED)
                .and().withExternal()
                .source(BeerOrderStatusEnum.ALLOCATION_PENDING).target(BeerOrderStatusEnum.PENDING_INVENTORY)
                .event(BeerOrderEvent.ALLOCATION_NO_INVENTORY);




    }

    @Override
    public void configure(final StateMachineConfigurationConfigurer<BeerOrderStatusEnum, BeerOrderEvent> config) throws Exception {
        StateMachineListenerAdapter<BeerOrderStatusEnum , BeerOrderEvent> adapter = new StateMachineListenerAdapter<>(){
            @Override
            public void stateChanged(final State<BeerOrderStatusEnum, BeerOrderEvent> from, final State<BeerOrderStatusEnum, BeerOrderEvent> to) {
                log.info("State changed from :{} to {}", from , to );
            }
        };
        config.withConfiguration().listener(adapter);
    }
}
