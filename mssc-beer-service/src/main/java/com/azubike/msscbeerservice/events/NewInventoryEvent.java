package com.azubike.msscbeerservice.events;

import com.azubike.msscbeerservice.web.model.BeerDto;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class NewInventoryEvent  extends  BeerEvent{
    @Builder
    public NewInventoryEvent(final BeerDto beerDto) {
        super(beerDto);
    }
}
