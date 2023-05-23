package com.azubike.msscbeerservice.clients;

import com.azubike.msscbeerservice.services.inventory.BeerInventoryDto;
import com.azubike.msscbeerservice.services.inventory.BeerInventoryServiceRestTemplateImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "beer-inventory-service")
public interface InventoryServiceFeignClient {
    @GetMapping(BeerInventoryServiceRestTemplateImpl.INVENTORY_PATH)
    List<BeerInventoryDto> listBeersById(@PathVariable UUID beerId);
}
