package com.ksaraev.spotify.exception;

public class SpotifyAccessTokenException extends RuntimeException {

  public SpotifyAccessTokenException(Throwable cause) {
    super("Bad or expired access token: " + cause.getMessage(), cause);
  }

  public SpotifyAccessTokenException(String message) {
    super("Bad or expired access token: " + message);
  }
}
