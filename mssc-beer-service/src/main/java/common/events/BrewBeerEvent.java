package common.events;

import common.model.BeerDto;
import lombok.*;


@NoArgsConstructor
public class BrewBeerEvent extends BeerEvent{
    public BrewBeerEvent(final BeerDto beerDto) {
        super(beerDto);
    }
}
