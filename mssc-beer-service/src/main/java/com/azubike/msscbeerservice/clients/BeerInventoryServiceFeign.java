package com.azubike.msscbeerservice.clients;

import com.azubike.msscbeerservice.services.inventory.BeerInventoryDto;
import com.azubike.msscbeerservice.services.inventory.BeerInventoryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
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
        final List<BeerInventoryDto> list = inventoryServiceFeignClient.listBeersById(beerId);
        return Objects.requireNonNull(list).stream()
                .mapToInt(BeerInventoryDto::getQuantityOnHand)
                .sum();
    }
}
