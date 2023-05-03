package guru.sfg.beer.order.service.state_machine.config;

import guru.sfg.beer.order.service.domain.BeerOrderEvent;
import guru.sfg.beer.order.service.domain.BeerOrderStatusEnum;
import guru.sfg.beer.order.service.state_machine.actions.AllocateOrderAction;
import guru.sfg.beer.order.service.state_machine.actions.ValidateOrderAction;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.springframework.statemachine.config.EnableStateMachineFactory;
import org.springframework.statemachine.config.StateMachineConfigurerAdapter;
import org.springframework.statemachine.config.builders.StateMachineStateConfigurer;
import org.springframework.statemachine.config.builders.StateMachineTransitionConfigurer;

import java.util.EnumSet;

@Configuration
@Slf4j
@EnableStateMachineFactory
@RequiredArgsConstructor
public class StateMachineConfig extends StateMachineConfigurerAdapter<BeerOrderStatusEnum , BeerOrderEvent> {
    private final ValidateOrderAction validateOrderAction;
    private final AllocateOrderAction allocateOrderAction;
    @Override
    public void configure(final StateMachineStateConfigurer<BeerOrderStatusEnum, BeerOrderEvent> states) throws Exception {
        states.withStates().initial(BeerOrderStatusEnum.NEW)
                .states(EnumSet.allOf(BeerOrderStatusEnum.class))
                .end(BeerOrderStatusEnum.PICKED_UP)
                .end(BeerOrderStatusEnum.ALLOCATION_EXCEPTION)
                .end(BeerOrderStatusEnum.DELIVERY_EXCEPTION)
                .end(BeerOrderStatusEnum.DELIVERED);
    }

    @Override
    public void configure(final StateMachineTransitionConfigurer<BeerOrderStatusEnum, BeerOrderEvent> transitions) throws Exception {
        transitions.withExternal().source(BeerOrderStatusEnum.NEW).target(BeerOrderStatusEnum.VALIDATION_PENDING)
                .event(BeerOrderEvent.VALIDATE_ORDER).action(validateOrderAction.validateOrder())
                .and().withExternal().source(BeerOrderStatusEnum.NEW).target(BeerOrderStatusEnum.VALIDATED)
                .event(BeerOrderEvent.VALIDATION_PASSED)
                .and().withExternal().source(BeerOrderStatusEnum.NEW).target(BeerOrderStatusEnum.VALIDATION_EXCEPTION)
                .event(BeerOrderEvent.VALIDATION_FAILED)
                .and().withExternal().source(BeerOrderStatusEnum.VALIDATED).target(BeerOrderStatusEnum.ALLOCATION_PENDING)
                .event(BeerOrderEvent.ALLOCATE_ORDER).action(allocateOrderAction);

    }
}
