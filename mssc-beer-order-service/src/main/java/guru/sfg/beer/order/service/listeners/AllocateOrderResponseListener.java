package guru.sfg.beer.order.service.listeners;


import common.events.AllocateOrderResponse;
import guru.sfg.beer.order.service.config.JmsConfig;
import guru.sfg.beer.order.service.services.BeerOrderManager;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AllocateOrderResponseListener {
    private final BeerOrderManager beerOrderManager;
    @JmsListener(destination = JmsConfig.ALLOCATE_ORDER_QUEUE)
    public void listen(AllocateOrderResponse response){
        if(!response.getAllocationError() && !response.getPendingInventory()){
            beerOrderManager.beerOrderAllocationPassed(response.getBeerOrderDto());
        }else if(!response.getPendingInventory() && response.getAllocationError()){
            beerOrderManager.beerOrderAllocationFailed(response.getBeerOrderDto());
        }else if(response.getPendingInventory() && !response.getAllocationError()){
            beerOrderManager.beerOrderAllocationPending(response.getBeerOrderDto());
        }
    }
}
