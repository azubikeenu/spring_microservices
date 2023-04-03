package com.azubike.msscbeerservice.util;

import com.azubike.msscbeerservice.web.model.BeerDto;
import com.azubike.msscbeerservice.web.model.BeerStyle;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.UUID;

public class TestUtils {

  public static final String BEER_1_UPC = "0631234200036";
  public static final String BEER_2_UPC = "0631234300019";

  public static BeerDto createValidBeerDto() {
    return BeerDto.builder()
        .id(UUID.randomUUID())
        .beerName("PALE_ALE")
        .version(1L)
        .beerStyle(BeerStyle.ALE)
        .price(new BigDecimal("10.5"))
        .upc(BEER_1_UPC)
        .createdDate(OffsetDateTime.now())
        .lastModifiedDate(OffsetDateTime.now())
        .quantityOnHand(10)
        .build();
  }

  public static BeerDto createBeerDto() {
    return BeerDto.builder()
        .beerName("PALE_ALE")
        .beerStyle(BeerStyle.ALE)
        .price(new BigDecimal("10.5"))
        .upc(BEER_2_UPC)
        .quantityOnHand(10)
        .build();
  }
}
