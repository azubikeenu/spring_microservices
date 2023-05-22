package guru.sfg.beer.order.service.test_components;

import common.events.AllocateOrderRequest;
import common.events.AllocateOrderResponse;
import guru.sfg.beer.order.service.config.JmsConfig;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.messaging.Message;
import org.springframework.stereotype.Component;

@Slf4j
@RequiredArgsConstructor
@Component
public class BeerOrderAllocationListener {

    private final JmsTemplate jmsTemplate;

    @JmsListener(destination = JmsConfig.ALLOCATE_ORDER_QUEUE)
    public void listen(Message msg) {
        AllocateOrderRequest request = (AllocateOrderRequest) msg.getPayload();
        String customerRef = request.getBeerOrderDto().getCustomerRef();
        boolean pendingInventory = false;
        boolean allocationError = false;
        boolean allocate = true;
        if(customerRef != null){
            switch (customerRef) {
                case "allocation_pending":
                    pendingInventory = true;
                    break;
                case "allocation_error":
                    allocationError = true;
                    break;
                case "dont_allocate":
                    allocate = false;
                    break;
            }
        }

        //stub a full allocation
        final var isPartialAllocation = pendingInventory;
        request.getBeerOrderDto().getBeerOrderLines().forEach(beerOrderLineDto -> {
            var quantityToAllocate = isPartialAllocation ?
                    beerOrderLineDto.getOrderQuantity() - 1
                    : beerOrderLineDto.getOrderQuantity();
            beerOrderLineDto.setQuantityAllocated(quantityToAllocate);
        });

        if(allocate){
            jmsTemplate.convertAndSend(JmsConfig.ALLOCATE_ORDER_RESPONSE_QUEUE,
                    AllocateOrderResponse.builder()
                            .beerOrderDto(request.getBeerOrderDto())
                            .pendingInventory(pendingInventory)
                            .allocationError(allocationError)
                            .build());
        }

    }
}