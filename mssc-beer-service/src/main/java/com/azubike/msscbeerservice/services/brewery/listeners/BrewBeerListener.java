package com.azubike.msscbeerservice.services.brewery.listeners;

import com.azubike.msscbeerservice.config.JmsConfig;
import common.events.BrewBeerEvent;
import common.events.NewInventoryEvent;
import com.azubike.msscbeerservice.repositories.BeerRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class BrewBeerListener {
    private final BeerRepository beerRepository;
    private final JmsTemplate jmsTemplate;

    @JmsListener(destination = JmsConfig.BREWING_REQUEST_QUEUE)
    public void listen(BrewBeerEvent brewBeerEvent) {
        var beerDto = brewBeerEvent.getBeerDto();
        // get the quantity to brew from the BeerEntity
        var beer = beerRepository.findById(beerDto.getId()).orElseThrow(() ->
                new RuntimeException(String.format("Beer with id %s not found", beerDto.getId().toString())));

        // sets the quantity at hand to quantity to brew which is a property on the beer object
        beerDto.setQuantityOnHand(beer.getQuantityToBrew());
        log.debug("brewing beer :{} and updating quantityOnHand To {}" , beer.getId() , beer.getQuantityToBrew());
        NewInventoryEvent newInventoryEvent = new NewInventoryEvent(beerDto);
        // this sends a request to the inventory service to update the inventory
        jmsTemplate.convertAndSend(JmsConfig.NEW_INVENTORY_QUEUE , newInventoryEvent);
    }
}
