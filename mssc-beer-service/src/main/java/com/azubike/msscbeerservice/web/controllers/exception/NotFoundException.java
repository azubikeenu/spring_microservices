package com.azubike.msscbeerservice.web.controllers.exception;

public class NotFoundException extends RuntimeException {
  public NotFoundException(String err) {
    super(err);
  }
}
