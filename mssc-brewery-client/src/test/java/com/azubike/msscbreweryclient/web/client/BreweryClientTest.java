package com.azubike.msscbreweryclient.web.client;


import com.azubike.msscbreweryclient.web.model.BeerDto;
import com.azubike.msscbreweryclient.web.model.BeerStyle;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;
import java.util.concurrent.ThreadLocalRandom;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;

@SpringBootTest
class BreweryClientTest {
  @Autowired BreweryClient breweryClient;

  @Test
  void getById() {
    BeerDto request = BeerDto.builder()
            .beerStyle(BeerStyle.ALE)
            .beerName("Gulder")
            .price(new BigDecimal("1.23"))
            .upc(String.valueOf(ThreadLocalRandom.current().nextLong(10000, 200000)))
            .build();
    BeerDto response = breweryClient.saveNewBeer(request);
    final BeerDto beerDto = breweryClient.getById(response.getId());
    assertThat(beerDto).isNotNull();
  }

  @Test
  void saveNewBeer() {
    BeerDto request = BeerDto.builder()
            .beerStyle(BeerStyle.ALE)
            .beerName("Gulder")
            .price(new BigDecimal("1.23"))
            .upc(String.valueOf(ThreadLocalRandom.current().nextLong(10000, 200000)))
            .build();

    BeerDto response = breweryClient.saveNewBeer(request);
    assertEquals(request.getBeerName(), response.getBeerName());
  }

  @Test
  void updateExistingBeer() {
    String newName = "Pepsi";

    BeerDto newBeer  = BeerDto.builder()
            .beerStyle(BeerStyle.GOSE)
            .beerName("Star")
            .price(new BigDecimal("1.23"))
            .upc(String.valueOf(ThreadLocalRandom.current().nextLong(10000, 200000)))
            .build();
    BeerDto savedBeer  = breweryClient.saveNewBeer(newBeer);
    savedBeer.setBeerName(newName);
    breweryClient.updateExistingBeer(savedBeer.getId(), savedBeer);

    BeerDto responseGetBeer = breweryClient.getById(savedBeer.getId());

    assertEquals(responseGetBeer.getBeerName(), newName);
  }

}
