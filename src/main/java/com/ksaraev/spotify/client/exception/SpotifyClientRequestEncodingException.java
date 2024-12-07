package com.ksaraev.spotify.client.exception;

public class SpotifyClientRequestEncodingException extends SpotifyClientException {

  public SpotifyClientRequestEncodingException(Throwable e) {
    super("Unable to encode object into query map: " + e.getMessage(), e);
  }
}
