package com.azubike.msscbeerservice.events;

import com.azubike.msscbeerservice.web.model.BeerDto;
import lombok.*;
@NoArgsConstructor
public class NewInventoryEvent  extends BeerEvent{
    public NewInventoryEvent(final BeerDto beerDto) {
        super(beerDto);
    }
}
