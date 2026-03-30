package com.mybot.backend.support;

import com.mybot.backend.api.dto.ChatResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

  @ExceptionHandler(MethodArgumentNotValidException.class)
  @ResponseStatus(HttpStatus.OK)
  public ChatResponse handleValidation(MethodArgumentNotValidException ex) {
    return new ChatResponse("系统繁忙，请稍后再试");
  }

  @ExceptionHandler(Exception.class)
  @ResponseStatus(HttpStatus.OK)
  public ChatResponse handleAny(Exception ex) {
    log.error("Unhandled error", ex);
    return new ChatResponse("系统繁忙，请稍后再试");
  }
}

