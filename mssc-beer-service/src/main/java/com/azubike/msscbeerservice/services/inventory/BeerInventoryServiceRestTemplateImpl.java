package com.azubike.msscbeerservice.services.inventory;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.context.annotation.Profile;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

@Slf4j
@Service
@ConfigurationProperties(prefix = "sfg.brewery", ignoreInvalidFields = true)
@Profile("!local_discovery")
public class BeerInventoryServiceRestTemplateImpl implements BeerInventoryService {
  public final static String INVENTORY_PATH = "/api/v1/inventory/{beerId}/beer";
  private  String beerInventoryServiceHost;

  private final RestTemplate restTemplate;

  public BeerInventoryServiceRestTemplateImpl(final RestTemplateBuilder restTemplateBuilder) {
    this.restTemplate = restTemplateBuilder.build();
  }

  public void setBeerInventoryServiceHost(final String beerInventoryServiceHost) {
    this.beerInventoryServiceHost = beerInventoryServiceHost;
  }

  @Override
  public Integer getQuantityOnHandInventory(final UUID beerId) {

    ParameterizedTypeReference<List<BeerInventoryDto>> beerInventoryDto =
        new ParameterizedTypeReference<>() {};

    String path = beerInventoryServiceHost + INVENTORY_PATH;

    final ResponseEntity<List<BeerInventoryDto>> responseEntity =
        restTemplate.exchange(path, HttpMethod.GET, null, beerInventoryDto, beerId);

    return Objects.requireNonNull(responseEntity.getBody()).stream()
        .mapToInt(BeerInventoryDto::getQuantityOnHand)
        .sum();
  }


}
