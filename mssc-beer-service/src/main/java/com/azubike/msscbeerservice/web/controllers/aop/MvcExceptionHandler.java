package com.azubike.msscbeerservice.web.controllers.aop;

import com.azubike.msscbeerservice.web.controllers.exception.NotFoundException;
import com.azubike.msscbeerservice.web.controllers.exception.response.ErrorMessage;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;

import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class MvcExceptionHandler {
  @ExceptionHandler({MethodArgumentNotValidException.class})
  public ResponseEntity<Object> handleMethodArgumentNotValidException(
      MethodArgumentNotValidException ex, WebRequest req) {
    String path = ((ServletWebRequest) req).getRequest().getRequestURI();

    final List<String> errorMessages =
        ex.getBindingResult().getFieldErrors().stream()
            .map(e -> String.format("%s - %s", e.getField(), e.getDefaultMessage()))
            .collect(Collectors.toList());

    ErrorMessage errorMessage =
        new ErrorMessage(
            new Date(), errorMessages.toString(), HttpStatus.BAD_REQUEST.value(), path);
    return new ResponseEntity<>(errorMessage, new HttpHeaders(), HttpStatus.BAD_REQUEST);
  }

  @ExceptionHandler(value = {Exception.class})
  public ResponseEntity<Object> handleException(Exception ex, WebRequest req) {
    String path = ((ServletWebRequest) req).getRequest().getRequestURI();

    ErrorMessage errorMessage =
        new ErrorMessage(
            new Date(), ex.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR.value(), path);
    return new ResponseEntity<>(errorMessage, new HttpHeaders(), HttpStatus.INTERNAL_SERVER_ERROR);
  }

  @ExceptionHandler(NotFoundException.class)
  public ResponseEntity<Object> validationErrorHandler(NotFoundException ex, WebRequest req) {
    String path = ((ServletWebRequest) req).getRequest().getRequestURI();
    ErrorMessage errorMessage =
        new ErrorMessage(new Date(), ex.getMessage(), HttpStatus.NOT_FOUND.value(), path);
    return new ResponseEntity<>(errorMessage, new HttpHeaders(), HttpStatus.NOT_FOUND);
  }

  @ExceptionHandler(ConversionFailedException.class)
  public ResponseEntity<Object> handleConflict(ConversionFailedException ex, WebRequest req) {
    String path = ((ServletWebRequest) req).getRequest().getRequestURI();
    ErrorMessage errorMessage =
        new ErrorMessage(new Date(), ex.getMessage(), HttpStatus.NOT_FOUND.value(), path);
    return new ResponseEntity<>(errorMessage, new HttpHeaders(), HttpStatus.NOT_FOUND);
  }
}
