package com.azubike.msscbeerservice.converters;

import common.model.BeerStyle;
import org.springframework.core.convert.converter.Converter;

public class StringToEnumConverter implements Converter<String, BeerStyle> {

  @Override
  public BeerStyle convert(final String source) {
    try {
      return BeerStyle.valueOf(source.toUpperCase());
    } catch (IllegalArgumentException ex) {
      return BeerStyle.NONE; // added this as a rollback value to facilitate query by beerStyle
    }
  }
}
