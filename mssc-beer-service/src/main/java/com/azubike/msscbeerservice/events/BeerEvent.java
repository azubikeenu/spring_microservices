package com.azubike.msscbeerservice.events;

import com.azubike.msscbeerservice.web.model.BeerDto;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;


@AllArgsConstructor
@NoArgsConstructor
@Data
@Builder
public class BeerEvent implements Serializable {
    private BeerDto beerDto;
}