package com.azubike.msscbeerservice.events;

import com.azubike.msscbeerservice.web.model.BeerDto;
import lombok.*;


@NoArgsConstructor
public class BrewBeerEvent extends BeerEvent{
    public BrewBeerEvent(final BeerDto beerDto) {
        super(beerDto);
    }
}
