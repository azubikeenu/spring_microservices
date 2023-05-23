package com.azubike.msscbeerservice.clients;

import com.azubike.msscbeerservice.services.inventory.BeerInventoryDto;
import com.azubike.msscbeerservice.services.inventory.BeerInventoryServiceRestTemplateImpl;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import java.util.List;
import java.util.UUID;

@FeignClient(name = "beer-inventory-service")
public interface InventoryServiceFeignClient {
    @RequestMapping(method = RequestMethod.GET, value = BeerInventoryServiceRestTemplateImpl.INVENTORY_PATH)
    ResponseEntity<List<BeerInventoryDto>> getOnhandInventory(@PathVariable UUID beerId);
}
