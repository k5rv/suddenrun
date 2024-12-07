package com.ksaraev.spotify.client.exception;


public class SpotifyClientReadingErrorResponseException extends SpotifyClientException {

  public SpotifyClientReadingErrorResponseException(Throwable e) {
    super("Unable to read Spotify API error: " + e.getMessage(), e);
  }
}
