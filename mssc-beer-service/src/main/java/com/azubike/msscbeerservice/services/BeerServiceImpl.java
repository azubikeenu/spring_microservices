package com.azubike.msscbeerservice.services;

import com.azubike.msscbeerservice.domain.Beer;
import com.azubike.msscbeerservice.repositories.BeerRepository;
import com.azubike.msscbeerservice.web.controllers.exception.NotFoundException;
import com.azubike.msscbeerservice.web.mappers.BeerMapper;
import common.model.BeerDto;
import common.model.BeerPageList;
import common.model.BeerStyle;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.CachePut;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

@Service
@RequiredArgsConstructor
@Slf4j
public class BeerServiceImpl implements BeerService {
  private final BeerRepository beerRepository;
  private final BeerMapper beerMapper;

  @Override
  @Cacheable(
      cacheNames = "beerCache",
      key = "#beerId",
      condition = "#showInventoryOnHand == false ")
  public BeerDto getBeerById(UUID beerId, final boolean showInventoryOnHand) {
    return !showInventoryOnHand
        ? beerMapper.beerToDto(getBeer(beerId))
        : beerMapper.beerToBeerDtoWithInventory(getBeer(beerId));
  }

  @Override
  public BeerDto saveNewBeer(BeerDto beerDto) {
    return beerMapper.beerToDto(beerRepository.save(beerMapper.dtoToBeer(beerDto)));
  }

  @Override
  @CachePut(cacheNames = "beerCache", key = "#beerId")
  public BeerDto updateBeer(UUID beerId, BeerDto beerDto) {
    Beer beer = getBeer(beerId);
    beer.setBeerName(beerDto.getBeerName());
    beer.setBeerStyle(beerDto.getBeerStyle());
    beer.setPrice(beerDto.getPrice());
    beer.setUpc(beerDto.getUpc());
    return beerMapper.beerToDto(beerRepository.save(beer));
  }

  @Override
  @CacheEvict(cacheNames = "beerCache", key = "#beerId")
  public void deleteBeer(UUID beerId) {
    log.info("deleting beer with id {}", beerId);
    beerRepository.delete(getBeer(beerId));
  }

  @Override
  @Cacheable(cacheNames = "beerListCache", condition = "#showInventoryOnHand == false ")
  public BeerPageList listBeers(
      final String beerName,
      final BeerStyle beerStyle,
      final PageRequest pageRequest,
      boolean showInventoryOnHand) {
    BeerPageList beerPageList;
    Page<Beer> beerPage;
    if (!StringUtils.isEmpty(beerName) && !StringUtils.isEmpty(beerStyle)) {
      // search by beerName and beerStyle
      beerPage = beerRepository.findAllByBeerNameAndBeerStyle(beerName, beerStyle, pageRequest);
    } else if (!StringUtils.isEmpty(beerName) && StringUtils.isEmpty(beerStyle)) {
      // search by beerName
      beerPage = beerRepository.findAllByBeerName(beerName, pageRequest);
    } else if (StringUtils.isEmpty(beerName) && !StringUtils.isEmpty(beerStyle)) {
      // search by beerStyle
      beerPage = beerRepository.findAllByBeerStyle(beerStyle, pageRequest);
    } else {
      // return a list of all beers
      beerPage = beerRepository.findAll(pageRequest);
    }
    // create the beer pageList
    beerPageList =
        new BeerPageList(
            beerPage.getContent().stream()
                .map(
                    beer ->
                        showInventoryOnHand
                            ? beerMapper.beerToBeerDtoWithInventory(beer)
                            : beerMapper.beerToDto(beer))
                .collect(Collectors.toList()),
            PageRequest.of(
                beerPage.getPageable().getPageNumber(), beerPage.getPageable().getPageSize()),
            beerPage.getTotalElements());
    return beerPageList;
  }

  @Override
  @Cacheable(cacheNames = "beerCache" ,key = "#upc")
  public BeerDto findByUpc(final String upc) {
    final Beer beer =
        beerRepository
            .findByUpc(upc)
            .orElseThrow(() -> new NotFoundException("Beer with upc " + upc + "not found"));
    return beerMapper.beerToDto(beer);
  }

  private Beer getBeer(UUID beerId) {
    return beerRepository
        .findById(beerId)
        .orElseThrow(() -> new NotFoundException(beerId + ", Not Found."));
  }
}
