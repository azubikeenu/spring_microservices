package brewery.events;

import brewery.model.BeerDto;
import lombok.*;
@NoArgsConstructor
public class NewInventoryEvent  extends BeerEvent{
    public NewInventoryEvent(final BeerDto beerDto) {
        super(beerDto);
    }
}
