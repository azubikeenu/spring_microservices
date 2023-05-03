package guru.sfg.beer.order.service.services.beer;

import common.model.BeerDto;
import java.util.Optional;
import java.util.UUID;

public interface BeerService {
  Optional<BeerDto> getBeerById(UUID uuid);

  Optional<BeerDto> getBeerByUpc(String upc);
}
