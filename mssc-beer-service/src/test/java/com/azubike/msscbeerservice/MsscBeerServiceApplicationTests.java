package com.azubike.msscbeerservice;

import com.azubike.msscbeerservice.bootstrap.BeerLoader;
import com.azubike.msscbeerservice.services.inventory.BeerInventoryService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;

@Slf4j
@SpringBootTest
class MsscBeerServiceApplicationTests {
  @Autowired BeerInventoryService beerInventoryService;

  @Test
  void contextLoads() {
    log.info("Context loads");
  }

  /// -----Cant run this test on a QA/CI env till deployment ---///
  //  @Test
  //  void testQuantityOnHand() {
  //    final Integer quantityOnHandInventory =
  //        beerInventoryService.getQuantityOnHandInventory(BeerLoader.BEER_1_UUID);
  //    assertThat(quantityOnHandInventory).isEqualTo(50);
  //  }
}
