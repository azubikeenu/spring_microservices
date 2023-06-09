package guru.sfg.beer.order.service.services;

import common.model.BeerOrderDto;
import guru.sfg.beer.order.service.domain.BeerOrder;
import guru.sfg.beer.order.service.domain.BeerOrderEvent;
import guru.sfg.beer.order.service.domain.BeerOrderStatusEnum;
import guru.sfg.beer.order.service.repositories.BeerOrderRepository;
import guru.sfg.beer.order.service.sm.interceptors.BeerStateChangedInterceptor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.statemachine.StateMachine;
import org.springframework.statemachine.config.StateMachineFactory;
import org.springframework.statemachine.support.DefaultStateMachineContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;


@Service
@Slf4j
@RequiredArgsConstructor
public class BeerOrderManagerImpl implements BeerOrderManager {

    public static final String BEER_ORDER_ID = "beer_order_id";
    private final BeerOrderRepository beerOrderRepository;
    private final StateMachineFactory<BeerOrderStatusEnum, BeerOrderEvent> factory;

    private final BeerStateChangedInterceptor beerStateChangedInterceptor;


    @Override
    @Transactional
    public BeerOrder newBeerOrder(BeerOrder beerOrder) {
        beerOrder.setId(null);
        beerOrder.setOrderStatus(BeerOrderStatusEnum.NEW);
        BeerOrder savedBeerOrder = beerOrderRepository.saveAndFlush(beerOrder);
        sendEvent(savedBeerOrder, BeerOrderEvent.VALIDATE_ORDER);
        return savedBeerOrder;
    }


    @Override
    @Transactional
    public void processValidationResult(UUID beerOrderId, Boolean isValid) {
         var beerOrder = beerOrderRepository.getReferenceById(beerOrderId);
        if(beerOrder!= null){
            if (isValid) {
                sendEvent(beerOrder, BeerOrderEvent.VALIDATION_PASSED);
                // wait for status to be validated
                awaitForStatus(beerOrderId, BeerOrderStatusEnum.VALIDATED);
                // get the validatedOrder with status VALIDATED
                BeerOrder validatedOrder = beerOrderRepository.getReferenceById(beerOrderId);
                  sendEvent(validatedOrder , BeerOrderEvent.ALLOCATE_ORDER);
            } else {
                sendEvent(beerOrder, BeerOrderEvent.VALIDATION_FAILED);
            }
        }else {
            log.error("beer order with id : {} not found", beerOrderId);
        }

    }

@Transactional
    @Override
    public void beerOrderAllocationPassed(BeerOrderDto beerOrderDto) {
        beerOrderRepository.findById(beerOrderDto.getId()).ifPresentOrElse(beerOrder -> {
            awaitForStatus(beerOrder.getId() ,BeerOrderStatusEnum.ALLOCATED);
            updateAllocatedQty(beerOrderDto);
            sendEvent(beerOrder, BeerOrderEvent.ALLOCATION_SUCCESS);
        }, () -> log.info("Order with id {} not found", beerOrderDto.getId()));
    }


    @Override
    @Transactional
    public void beerOrderAllocationFailed(BeerOrderDto beerOrderDto) {
        beerOrderRepository.findById(beerOrderDto.getId()).ifPresentOrElse(beerOrder -> {
            sendEvent(beerOrder, BeerOrderEvent.ALLOCATION_FAILED);
        }, () -> log.info("Order with id {} not found", beerOrderDto.getId()));
    }


    @Override
    @Transactional
    public void beerOrderAllocationPending(BeerOrderDto beerOrderDto) {
        beerOrderRepository.findById(beerOrderDto.getId()).ifPresentOrElse(beerOrder -> {
            sendEvent(beerOrder, BeerOrderEvent.ALLOCATION_NO_INVENTORY);
        }, () -> log.info("Order with id {} not found", beerOrderDto.getId()));
    }

    @Override
    @Transactional
    public void cancelOrder(final UUID id) {
      beerOrderRepository.findById(id).ifPresentOrElse((beerOrder -> {
          sendEvent(beerOrder , BeerOrderEvent.CANCEL_ORDER);
      }) , ()-> log.info("Order with id {} not found" ,id));

    }

    private void updateAllocatedQty (BeerOrderDto beerOrderDto) {
        Optional<BeerOrder> allocatedOrderOptional = Optional.of(beerOrderRepository.getReferenceById(beerOrderDto.getId()));
        allocatedOrderOptional.ifPresentOrElse(allocatedOrder -> {
            allocatedOrder.getBeerOrderLines().forEach(beerOrderLine -> {
                beerOrderDto.getBeerOrderLines().forEach(beerOrderLineDto -> {
                    // make sure you update the right beer
                    if(beerOrderLine.getId().equals(beerOrderLineDto.getId())){
                        beerOrderLine.setQuantityAllocated(beerOrderLineDto.getQuantityAllocated());
                    }
                });
            });
            // save the updated order to the repository
            beerOrderRepository.saveAndFlush(allocatedOrder);
        }, () -> log.error("Order Not Found. Id: " + beerOrderDto.getId()));
    }


    /**
     * This is a utility method that helps handle the async state of event messaging
     * it is best used when we are transitioning from one state to another within a single method
     * @param beerOrderId
     * @param statusEnum
     */

    private void awaitForStatus(UUID beerOrderId, BeerOrderStatusEnum statusEnum) {

        AtomicBoolean found = new AtomicBoolean(false);
        AtomicInteger loopCount = new AtomicInteger(0);

        while (!found.get()) {
            // this creates a base condition and exits the loop after 10 retries
            if (loopCount.incrementAndGet() > 10) {
                log.debug("Loop Retries exceeded");
                break;
            }
            // since sendEvents are not in sync with the database call  we have to wait
            // till the status is change to the intermediary state before proceeding to the final state
            Optional.of(beerOrderRepository.getReferenceById(beerOrderId)).ifPresentOrElse(beerOrder -> {
                if (beerOrder.getOrderStatus().equals(statusEnum)) {
                    found.set(true);
                    log.debug("Order Found");
                } else {
                    log.debug("Order Status Not Equal. Expected: " + statusEnum.name() + " Found: " + beerOrder.getOrderStatus().name());
                }
            }, () -> {
                log.debug("Order Id Not Found");
            });

            if (!found.get()) {
                try {
                    log.debug("Sleeping for retry");
                    Thread.sleep(100);
                } catch (Exception e) {
                    // do nothing
                }
            }
        }
    }


    @Override
    @Transactional
    public void beerOrderPickedUp(UUID id) {
        Optional<BeerOrder> beerOrderOptional = Optional.of(beerOrderRepository.getReferenceById(id));
        beerOrderOptional.ifPresentOrElse(beerOrder -> {
             // Handle process later
            sendEvent(beerOrder, BeerOrderEvent.BEER_ORDER_PICKED_UP);
        }, () -> log.error("Order Not Found. Id: " + id));
    }



    private void sendEvent(BeerOrder beerOrder, BeerOrderEvent beerOrderEvent) {
        final StateMachine<BeerOrderStatusEnum, BeerOrderEvent> sm = build(beerOrder);
        // this augments the message send by the stateMachine with a header property that contains the beerId which is
        // utilized by the BeerOrderStateChangeInterceptor
        final Message<BeerOrderEvent> message = MessageBuilder.withPayload(beerOrderEvent)
                .setHeader(BEER_ORDER_ID, beerOrder.getId().toString()).build();
        final boolean accepted = sm.sendEvent(message);
        log.debug("Event {} sent accepted value is {}" , beerOrderEvent,accepted );
    }

    private StateMachine<BeerOrderStatusEnum, BeerOrderEvent> build(BeerOrder beerOrder) {
        StateMachine<BeerOrderStatusEnum, BeerOrderEvent> sm = factory.getStateMachine(beerOrder.getId());
         sm.stop();
        sm.getStateMachineAccessor()
                .doWithAllRegions(sma -> {
                    sma.addStateMachineInterceptor(beerStateChangedInterceptor);
                    //Rehydrate the status of the state machine with beerStatus from the database which is derived from the interceptor
                    sma.resetStateMachine(new
                            DefaultStateMachineContext<>(beerOrder.getOrderStatus(), null, null, null));
                });
        sm.start();
        return sm;
    }
}
