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
        final AllocateOrderResponse.AllocateOrderResponseBuilder builder = AllocateOrderResponse.builder();
        builder.beerOrderDto(allocateOrderRequest.getBeerOrderDto());

        try {
            Boolean allocationResult = allocationService.allocateOrder(allocateOrderRequest.getBeerOrderDto());
            // perform allocation
            builder.pendingInventory(!allocationResult);
             // set the error to false
            builder.allocationError(false);
        } catch (Exception e) {
            log.error("Allocation failed for Order Id:" + allocateOrderRequest.getBeerOrderDto().getId());
            builder.allocationError(true);
        }

        jmsTemplate.convertAndSend(JmsConfig.ALLOCATE_ORDER_RESPONSE_QUEUE,
                builder.build());

    }
}

