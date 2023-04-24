package guru.sfg.beer.inventory.service.listeners;

import common.events.NewInventoryEvent;
import common.model.BeerDto;
import guru.sfg.beer.inventory.service.config.JmsConfig;
import guru.sfg.beer.inventory.service.domain.BeerInventory;
import guru.sfg.beer.inventory.service.repositories.BeerInventoryRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jms.annotation.JmsListener;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class NewInventoryListener {
    private final BeerInventoryRepository beerInventoryRepository;

    @JmsListener(destination = JmsConfig.NEW_INVENTORY_QUEUE)
    public void listen(NewInventoryEvent newInventoryEvent) {
        BeerDto beerDto = newInventoryEvent.getBeerDto();

        log.debug("creating new inventory for beer {}", beerDto.getId());
        beerInventoryRepository.save(BeerInventory.builder().beerId(beerDto.getId())
                .upc(beerDto.getUpc()).quantityOnHand(beerDto.getQuantityOnHand()).build());

    }

}
