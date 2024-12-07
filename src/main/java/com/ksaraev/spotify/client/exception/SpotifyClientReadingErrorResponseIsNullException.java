package com.ksaraev.spotify.client.exception;


public class SpotifyClientReadingErrorResponseIsNullException extends SpotifyClientException {

  public SpotifyClientReadingErrorResponseIsNullException() {
    super("Reading Spotify API error response is null");
  }
}
