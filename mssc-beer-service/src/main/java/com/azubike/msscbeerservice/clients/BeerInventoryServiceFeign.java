package com.azubike.msscbeerservice.clients;

import com.azubike.msscbeerservice.services.inventory.BeerInventoryDto;
import com.azubike.msscbeerservice.services.inventory.BeerInventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Service
@Slf4j
@RequiredArgsConstructor
@Profile("local_discovery")
public class BeerInventoryServiceFeign implements BeerInventoryService {
    private  final InventoryServiceFeignClient inventoryServiceFeignClient;
    @Override
    public Integer getQuantityOnHandInventory(final UUID beerId) {
        log.debug("Calling Inventory Service");
        ResponseEntity<List<BeerInventoryDto>> responseEntity = inventoryServiceFeignClient.getOnhandInventory(beerId);
        //sum from inventory list
        Integer onHand = Objects.requireNonNull(responseEntity.getBody())
                .stream()
                .mapToInt(BeerInventoryDto::getQuantityOnHand)
                .sum();

        return onHand;
    }
}
