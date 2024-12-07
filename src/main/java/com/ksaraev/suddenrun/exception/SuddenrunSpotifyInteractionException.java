package com.ksaraev.suddenrun.exception;


public class SuddenrunSpotifyInteractionException extends RuntimeException {

  public SuddenrunSpotifyInteractionException(Throwable cause) {
    super("Error while interacting with spotify service: " + cause.getMessage(), cause);
  }
}
