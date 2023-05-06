package guru.sfg.beer.order.service.services;

import common.model.BeerOrderDto;
import guru.sfg.beer.order.service.domain.BeerOrder;

import java.util.UUID;

public interface BeerOrderManager {
    BeerOrder newBeerOrder (BeerOrder beerOrder);

    void processValidationResult(UUID beerOrderId, Boolean isValid) ;


   void  beerOrderAllocationPassed(BeerOrderDto beerOrderDto);
   void  beerOrderAllocationFailed(BeerOrderDto beerOrderDto);
   void  beerOrderAllocationPending(BeerOrderDto beerOrderDto);

    void beerOrderPickedUp(UUID id);

}
