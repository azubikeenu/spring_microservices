package com.azubike.msscbeerservice.services;

import brewery.model.BeerDto;
import brewery.model.BeerPageList;
import brewery.model.BeerStyle;
import java.util.UUID;
import org.springframework.data.domain.PageRequest;

public interface BeerService {
  BeerDto getBeerById(UUID beerId, final boolean showInventoryOnHand);

  BeerDto saveNewBeer(BeerDto beerDto);

  BeerDto updateBeer(UUID beerId, BeerDto beerDto);

  void deleteBeer(UUID beerId);

  BeerPageList listBeers(
      String beerName, BeerStyle beerStyle, PageRequest pageable, boolean showInventoryOnHand);

  BeerDto findByUpc(String upc);
}
