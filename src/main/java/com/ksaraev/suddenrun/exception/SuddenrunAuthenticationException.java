package com.ksaraev.suddenrun.exception;

public class SuddenrunAuthenticationException extends RuntimeException {

  public SuddenrunAuthenticationException(Throwable cause) {
    super("Suddenrun authentication exception: " + cause.getMessage(), cause);
  }
}
