package com.azubike.msscbeerservice.web.controllers;

import com.azubike.msscbeerservice.services.BeerService;
import common.model.BeerDto;
import common.model.BeerPageList;
import common.model.BeerStyle;
import java.net.URI;
import java.util.UUID;
import javax.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

@RestController
@RequiredArgsConstructor
@RequestMapping("api/v1/beer")
public class BeerServiceController {
  private final BeerService beerService;
  private final Integer DEFAULT_PAGE_NUMBER = 0;
  private final Integer DEFAULT_PAGE_SIZE = 25;

  @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BeerPageList> getBeers(
      @RequestParam(value = "pageNumber", required = false, defaultValue = "0") Integer pageNumber,
      @RequestParam(value = "pageSize", required = false, defaultValue = "25") Integer pageSize,
      @RequestParam(value = "beerName", required = false) String beerName,
      @RequestParam(value = "beerStyle", required = false) BeerStyle beerStyle,
      @RequestParam(value = "showInventoryOnHand", required = false, defaultValue = "false")
          boolean showInventoryOnHand) {

    if (pageNumber < 0) pageNumber = DEFAULT_PAGE_NUMBER;

    if (pageSize < 1) pageNumber = DEFAULT_PAGE_SIZE;

    BeerPageList beerPageList =
        beerService.listBeers(
            beerName, beerStyle, PageRequest.of(pageNumber, pageSize), showInventoryOnHand);

    return ResponseEntity.ok(beerPageList);
  }

  @GetMapping(value = "/{beerId}", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BeerDto> getBeerById(
      @PathVariable("beerId") UUID beerId,
      @RequestParam(value = "showInventoryOnHand", required = false, defaultValue = "false")
          boolean showInventoryOnHand) {
    BeerDto beerDto = beerService.getBeerById(beerId, showInventoryOnHand);
    return ResponseEntity.ok(beerDto);
  }

  @GetMapping(value = "/{upc}/beerUpc", produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BeerDto> getBeerByUpc(@PathVariable("upc") String upc) {
    final BeerDto beerDto = beerService.findByUpc(upc);
    return ResponseEntity.ok(beerDto);
  }

  @PostMapping(
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BeerDto> saveNewBeer(@Valid @RequestBody BeerDto beerDto) {
    BeerDto savedBeer = beerService.saveNewBeer(beerDto);
    URI location =
        ServletUriComponentsBuilder.fromCurrentRequest()
            .path("/{beerId}")
            .buildAndExpand(savedBeer.getId())
            .toUri();
    return ResponseEntity.created(location).body(savedBeer);
  }

  @PutMapping(
      value = "/{beerId}",
      consumes = MediaType.APPLICATION_JSON_VALUE,
      produces = MediaType.APPLICATION_JSON_VALUE)
  public ResponseEntity<BeerDto> updateBeerById(
      @PathVariable("beerId") UUID beerId, @Valid @RequestBody BeerDto beerDto) {
    BeerDto updateBeer = beerService.updateBeer(beerId, beerDto);
    return ResponseEntity.ok(updateBeer);
  }

  @DeleteMapping(value = "/{beerId}")
  public void deleteBeerById(@PathVariable("beerId") UUID beerId) {
    beerService.deleteBeer(beerId);
  }
}
