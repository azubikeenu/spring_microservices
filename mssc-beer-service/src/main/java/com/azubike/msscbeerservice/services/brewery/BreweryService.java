package com.azubike.msscbeerservice.services.brewery;

import com.azubike.msscbeerservice.config.JmsConfig;
import com.azubike.msscbeerservice.domain.Beer;
import com.azubike.msscbeerservice.repositories.BeerRepository;
import com.azubike.msscbeerservice.services.inventory.BeerInventoryService;
import com.azubike.msscbeerservice.web.mappers.BeerMapper;
import common.events.BrewBeerEvent;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Service
public class BreweryService {
    private final BeerRepository beerRepository;
    private final JmsTemplate jmsTemplate;
    private final BeerInventoryService beerInventoryService;
    private final BeerMapper beerMapper;


    @Scheduled(fixedRate = 5000)
    public void checkForLowInventory(){
        final List<Beer> beers = beerRepository.findAll();
        beers.forEach(beer -> {
            var minOnHand = beer.getMinOnHand();
            var inventoryOnHand = beerInventoryService.getQuantityOnHandInventory(beer.getId());
            log.debug("Beer name : {} minOnHand : {} inventoryOnHand : {}" , beer.getBeerName() , minOnHand,inventoryOnHand);

            if(minOnHand != null && inventoryOnHand <= minOnHand){
                log.debug("Request to brew beer {}" ,beer.getId());
                jmsTemplate.convertAndSend(JmsConfig.BREWING_REQUEST_QUEUE, new BrewBeerEvent(beerMapper.beerToDto(beer)));
            }
        });
    }
}

