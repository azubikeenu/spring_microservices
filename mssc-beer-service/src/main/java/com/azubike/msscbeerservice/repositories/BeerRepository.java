package com.azubike.msscbeerservice.repositories;

import com.azubike.msscbeerservice.domain.Beer;
import brewery.model.BeerStyle;
import java.util.Optional;
import java.util.UUID;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BeerRepository extends JpaRepository<Beer, UUID> {
  Page<Beer> findAllByBeerNameAndBeerStyle(
      String beerName, BeerStyle beerStyle, PageRequest pageRequest);

  Page<Beer> findAllByBeerName(String beerName, PageRequest pageRequest);

  Page<Beer> findAllByBeerStyle(BeerStyle beerStyle, PageRequest pageRequest);

  Optional<Beer> findByUpc(String upc);
}
