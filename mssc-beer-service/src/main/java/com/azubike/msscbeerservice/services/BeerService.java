package com.azubike.msscbeerservice.services;

import com.azubike.msscbeerservice.web.model.BeerDto;
import com.azubike.msscbeerservice.web.model.BeerPageList;
import com.azubike.msscbeerservice.web.model.BeerStyle;
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
