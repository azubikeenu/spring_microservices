package com.azubike.msscbreweryclient.utils;

import com.azubike.msscbreweryclient.web.model.BeerDto;
import com.azubike.msscbreweryclient.web.model.BeerStyle;

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
            .upc("1222333330")
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
            .upc("22233LLL33")
            .quantityOnHand(10)
            .build();
  }

}
