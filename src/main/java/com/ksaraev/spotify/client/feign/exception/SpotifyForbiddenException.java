package com.ksaraev.spotify.client.feign.exception;


public class SpotifyForbiddenException extends SpotifyWebApiException {
  public SpotifyForbiddenException(String message) {
    super(message);
  }
}
