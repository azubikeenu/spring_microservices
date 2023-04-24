package common.events;

import common.model.BeerDto;
import lombok.*;
@NoArgsConstructor
public class NewInventoryEvent  extends BeerEvent{
    public NewInventoryEvent(final BeerDto beerDto) {
        super(beerDto);
    }
}
