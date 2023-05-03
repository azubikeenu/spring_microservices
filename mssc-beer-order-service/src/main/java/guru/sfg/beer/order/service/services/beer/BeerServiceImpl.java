package guru.sfg.beer.order.service.services.beer;

import common.model.BeerDto;
import java.util.Optional;
import java.util.UUID;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.web.client.RestTemplateBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@ConfigurationProperties(prefix = "sfg.brewery", ignoreUnknownFields = false)
@Service
public class BeerServiceImpl implements BeerService {

  public static final String BEER_PATH_V1 = "/api/v1/beer/";
  public static final String BEER_UPC_PATH_V1 = "/api/v1/beer/{upc}/beerUpc";
  private final RestTemplate restTemplate;

  private String beerServiceHost;

  public BeerServiceImpl(RestTemplateBuilder restTemplateBuilder) {
    this.restTemplate = restTemplateBuilder.build();
  }

  @Override
  public Optional<BeerDto> getBeerById(final UUID uuid) {
    return Optional.ofNullable(
        restTemplate.getForObject(beerServiceHost + BEER_PATH_V1 + uuid.toString(), BeerDto.class));
  }

  @Override
  public Optional<BeerDto> getBeerByUpc(final String upc) {
    ParameterizedTypeReference<BeerDto> beerDto = new ParameterizedTypeReference<>() {};
    String path = beerServiceHost + BEER_UPC_PATH_V1;

    final ResponseEntity<BeerDto> responseEntity =
        restTemplate.exchange(path, HttpMethod.GET, null, beerDto, upc);
    return Optional.ofNullable(responseEntity.getBody());
  }

  public void setBeerServiceHost(final String beerServiceHost) {
    this.beerServiceHost = beerServiceHost;
  }
}
