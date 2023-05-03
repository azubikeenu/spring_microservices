package com.azubike.msscbeerservice.listeners;

import com.azubike.msscbeerservice.domain.Beer;
import com.azubike.msscbeerservice.repositories.BeerRepository;
import common.model.BeerOrderDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.concurrent.atomic.AtomicInteger;

@Service
@Slf4j
@RequiredArgsConstructor
public class ValidateRequest {
    private final BeerRepository beerRepository;

    public boolean performValidation(BeerOrderDto beerOrderDto){
        AtomicInteger beersNotFound = new AtomicInteger();
        beerOrderDto.getBeerOrderLines().forEach(beerOrderLineDto -> {
            final Optional<Beer> optionalBeer = beerRepository.findById(beerOrderDto.getId());
           if(optionalBeer.isEmpty()){
               beersNotFound.incrementAndGet();
           }
        });
        return beersNotFound.get() == 0 ;

    }
}
