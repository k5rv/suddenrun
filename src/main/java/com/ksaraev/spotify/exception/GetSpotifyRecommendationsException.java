package com.ksaraev.spotify.exception;

public class GetSpotifyRecommendationsException extends SpotifyServiceException {

  public GetSpotifyRecommendationsException(Throwable cause) {
    super("Error while getting Spotify user recommendations: " + cause.getMessage(), cause);
  }
}
