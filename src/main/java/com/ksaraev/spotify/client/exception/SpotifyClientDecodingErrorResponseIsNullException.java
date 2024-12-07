package com.ksaraev.spotify.client.exception;


public class SpotifyClientDecodingErrorResponseIsNullException extends SpotifyClientException {

  public SpotifyClientDecodingErrorResponseIsNullException() {
    super("Decoding Spotify API error response is null");
  }
}
