package com.azubike.msscbeerservice.events;

import com.azubike.msscbeerservice.web.model.BeerDto;
import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Data
@EqualsAndHashCode(callSuper = true)
public class BrewBeerEvent extends BeerEvent{

    @Builder
    public BrewBeerEvent(final BeerDto beerDto) {
        super(beerDto);
    }
}
