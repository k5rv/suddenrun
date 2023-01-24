package com.ksaraev.spotifyrunning.exception;

import lombok.Data;
import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;

@Data
public final class ErrorResponse {
  private Integer status;
  private String message;
  private ZonedDateTime timeStamp;

  public ErrorResponse(HttpStatus httpStatus, ZonedDateTime timeStamp) {
    this.status = httpStatus.value();
    this.message = httpStatus.getReasonPhrase();
    this.timeStamp = timeStamp;
  }

  public ErrorResponse(HttpStatus httpStatus, String message, ZonedDateTime timeStamp) {
    this.status = httpStatus.value();
    this.message = message;
    this.timeStamp = timeStamp;
  }
}
