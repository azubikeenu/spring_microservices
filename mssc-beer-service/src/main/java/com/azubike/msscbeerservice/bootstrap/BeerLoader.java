package com.azubike.msscbeerservice.bootstrap;

import com.azubike.msscbeerservice.domain.Beer;
import com.azubike.msscbeerservice.repositories.BeerRepository;
import com.azubike.msscbeerservice.web.model.BeerStyle;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;import java.util.UUID;

//@Component
@RequiredArgsConstructor
@Slf4j
public class BeerLoader implements CommandLineRunner {

  public static final String BEER_1_UPC = "0631234200036";
  public static final String BEER_2_UPC = "0631234300019";
  public static final String BEER_3_UPC = "0083783375213";

  public static final UUID BEER_1_UUID = UUID.fromString("0a818933-087d-47f2-ad83-2f986ed087eb");
  public static final UUID BEER_2_UUID = UUID.fromString("a712d914-61ea-4623-8bd0-32c0f6545bfd");
  public static final UUID BEER_3_UUID = UUID.fromString("026cc3c8-3a0c-4083-a05b-e908048c1b08");

  private final BeerRepository beerRepository;

  @Override
  public void run(String... args) throws Exception {
    loadBeers();
  }

  private void loadBeers() {
    if (beerRepository.count() == 0) {
      log.info("Creating new Instances of beers.......");
      beerRepository.save(
          Beer.builder()
              .beerName("Mango Bobs")
              .beerStyle(BeerStyle.IPA)
              .quantityToBrew(300)
              .upc(BEER_1_UPC)
              .price(new BigDecimal("12.50"))
              .build());

      beerRepository.save(
          Beer.builder()
              .beerName("Star")
              .beerStyle(BeerStyle.ALE)
              .quantityToBrew(100)
              .upc(BEER_2_UPC)
              .price(new BigDecimal("11.50"))
              .build());

      beerRepository.save(
          Beer.builder()
              .beerName("Galaxy Cat")
              .beerStyle(BeerStyle.LAGER)
              .quantityToBrew(100)
              .upc(BEER_3_UPC)
              .price(new BigDecimal("10.50"))
              .build());

      log.info("Saving a total of {} beers", beerRepository.count());
    }
  }
}
