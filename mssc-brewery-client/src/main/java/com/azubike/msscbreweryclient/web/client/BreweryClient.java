package com.azubike.msscbreweryclient.web.client;

import com.azubike.msscbreweryclient.web.model.BeerDto;
import com.azubike.msscbreweryclient.web.model.CustomerDto;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.UUID;

@Component
@ConfigurationProperties(prefix = "sfg.brewery", ignoreInvalidFields = false)
public class BreweryClient {
  private String apihost;

  public final String BEER_PATH_V1 = "/api/v1/beer/";
  public final String CUSTOMER_PATH_V1 = "/api/v1/customer/";

  private final RestTemplate restTemplate;

  public BreweryClient(RestTemplateBuilder restTemplateBuilder) {
    this.restTemplate = restTemplateBuilder.build();
  }

  public BeerDto getById(UUID beerId) {
    return restTemplate.getForObject(apihost + BEER_PATH_V1 + beerId.toString(), BeerDto.class);
  }

  public BeerDto saveNewBeer(BeerDto beerDto) {
    return restTemplate.postForObject(apihost + BEER_PATH_V1, beerDto, BeerDto.class);
  }

  public void updateExistingBeer(UUID beerId, BeerDto beerDto) {
    restTemplate.put(apihost + BEER_PATH_V1 + beerId.toString(), beerDto);
  }

  public void deleteExistingBeer(UUID beerId) {
    restTemplate.delete(apihost + BEER_PATH_V1 + beerId.toString());
  }

  public CustomerDto getCustomerById(UUID customerId) {
    return restTemplate.getForObject(
        apihost + CUSTOMER_PATH_V1 + customerId.toString(), CustomerDto.class);
  }

  public CustomerDto saveNewCustomer(CustomerDto customerDto) {

    return restTemplate.postForObject(apihost + CUSTOMER_PATH_V1, customerDto, CustomerDto.class);
  }

  public void updateNewCustomer(CustomerDto customerDto, UUID customerId) {
    restTemplate.put(
        apihost + CUSTOMER_PATH_V1 + customerId.toString(), customerDto, CustomerDto.class);
  }

  public void deleteExistingCustomer(UUID customerId) {
    restTemplate.delete(apihost + CUSTOMER_PATH_V1 + customerId.toString());
  }

  public void setApihost(String apihost) {
    this.apihost = apihost;
  }
}
