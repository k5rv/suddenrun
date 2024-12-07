package com.ksaraev.spotify.client.feign.exception;

public class SpotifyWebApiException extends RuntimeException {

  public SpotifyWebApiException(String message) {
    super(message);
  }

  public SpotifyWebApiException() {
    super();
  }
}
