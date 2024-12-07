package com.ksaraev.spotify.exception;

public class GetSpotifyUserProfileException extends SpotifyServiceException {

  public GetSpotifyUserProfileException(Throwable cause) {
    super("Error while getting current Spotify user profile: " + cause.getMessage(), cause);
  }
}
