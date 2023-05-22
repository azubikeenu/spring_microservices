package guru.sfg.beer.order.service.sm.actions;

import guru.sfg.beer.order.service.domain.BeerOrderEvent;
import guru.sfg.beer.order.service.domain.BeerOrderStatusEnum;
import guru.sfg.beer.order.service.services.BeerOrderManagerImpl;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.statemachine.StateContext;
import org.springframework.statemachine.action.Action;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class ValidationFailureAction implements Action<BeerOrderStatusEnum, BeerOrderEvent> {
    @Override
    public void execute(final StateContext<BeerOrderStatusEnum, BeerOrderEvent> stateContext) {
        var orderId = String.valueOf(stateContext.getMessageHeader(BeerOrderManagerImpl.BEER_ORDER_ID));
        log.error("Validation failed for beerOrder with id {}" , orderId);
    }
}
