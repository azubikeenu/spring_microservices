package com.azubike.msscbeerservice.services.inventory;

import java.util.UUID;

public interface BeerInventoryService {
  Integer getQuantityOnHandInventory(UUID beerId);
}
