package com.azubike.msscbeerservice.listeners;

import com.azubike.msscbeerservice.config.JmsConfig;
import brewery.events.BrewBeerEvent;
import brewery.events.NewInventoryEvent;
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

        beerDto.setQuantityOnHand(beer.getQuantityToBrew());
        log.debug("brewing beer :{} and updating quantityOnHand To {}" , beer.getId() , beer.getQuantityToBrew());
        NewInventoryEvent newInventoryEvent = new NewInventoryEvent(beerDto);
        jmsTemplate.convertAndSend(JmsConfig.NEW_INVENTORY_QUEUE , newInventoryEvent);
    }
}
