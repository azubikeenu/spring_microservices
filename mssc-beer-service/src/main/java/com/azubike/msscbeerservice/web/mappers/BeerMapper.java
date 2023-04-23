package com.azubike.msscbeerservice.web.mappers;

import com.azubike.msscbeerservice.domain.Beer;
import com.azubike.msscbeerservice.services.inventory.BeerInventoryService;
import com.azubike.msscbeerservice.web.model.BeerDto;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.springframework.beans.factory.annotation.Autowired;

@Mapper(
    uses = {DateMapper.class},
    componentModel = "spring")
public abstract class BeerMapper {
  @Autowired protected BeerInventoryService beerInventoryService;

  @Mapping(target = "quantityOnHand", source = "beer.minOnHand")
  public abstract BeerDto beerToDto(Beer beer);


  @Mapping(target = "minOnHand" , source = "beerDto.quantityOnHand")
  public abstract Beer dtoToBeer(BeerDto beerDto);

  @Mapping(
      target = "quantityOnHand",
      expression = "java(beerInventoryService.getQuantityOnHandInventory(beer.getId()))")
  public abstract BeerDto beerToBeerDtoWithInventory(Beer beer);
}
