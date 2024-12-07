package com.ksaraev.spotify.client.exception;

public class SpotifyClientException extends RuntimeException {
  public SpotifyClientException(String message) {
    super(message);
  }

  public SpotifyClientException(String message, Throwable cause) {
    super(message, cause);
  }
}
