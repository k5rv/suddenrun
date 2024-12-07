package com.ksaraev.spotify.exception;

public class SpotifyServiceException extends RuntimeException {

  public SpotifyServiceException(String message, Throwable cause) {
    super(message, cause);
  }

  public SpotifyServiceException(String message) {
    super(message);
  }
}
