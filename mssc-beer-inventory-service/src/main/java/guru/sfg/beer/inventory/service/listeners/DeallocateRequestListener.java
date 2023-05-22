package guru.sfg.beer.inventory.service.listeners;


import common.events.DeallocateOrderRequest;
import guru.sfg.beer.inventory.service.config.JmsConfig;
import guru.sfg.beer.inventory.service.services.AllocationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class DeallocateRequestListener {
    private final AllocationService allocationService;

    @JmsListener(destination = JmsConfig.DEALLOCATE_ORDER_QUEUE)
    public void listen(DeallocateOrderRequest deallocateOrderRequest) {
        allocationService.deallocateOrder(deallocateOrderRequest.getBeerOrderDto());
        log.debug("deallocated order with id {}", deallocateOrderRequest.getBeerOrderDto().getId());
    }
}
