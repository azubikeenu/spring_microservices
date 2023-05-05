package guru.sfg.beer.inventory.service.listeners;


import common.events.AllocateOrderRequest;
import common.events.AllocateOrderResponse;
import guru.sfg.beer.inventory.service.config.JmsConfig;
import guru.sfg.beer.inventory.service.services.AllocationService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@RequiredArgsConstructor
@Slf4j
@Service
public class AllocateOrderRequestListener {
    private final AllocationService allocationService;
    private final JmsTemplate jmsTemplate;

    @JmsListener(destination = JmsConfig.ALLOCATE_ORDER_QUEUE)
    public void listen(AllocateOrderRequest allocateOrderRequest) {
        var builder = AllocateOrderResponse.builder();
        try {
            final Boolean isAllocated = allocationService.allocateOrder(allocateOrderRequest.getBeerOrderDto());
            builder.pendingInventory(!isAllocated);
        } catch (Exception ex) {
            log.debug("An error occurred while allocating beerOrder with id {}", allocateOrderRequest.getBeerOrderDto().getId());
            builder.allocationError(true);
        }
        builder.beerOrderDto(allocateOrderRequest.getBeerOrderDto());

        jmsTemplate.convertAndSend(JmsConfig.ALLOCATE_ORDER_RESPONSE_QUEUE , builder.build());
    }
}
