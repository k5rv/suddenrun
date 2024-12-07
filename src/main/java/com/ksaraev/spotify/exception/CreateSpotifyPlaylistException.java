package com.ksaraev.spotify.exception;

public class CreateSpotifyPlaylistException extends SpotifyServiceException {

  public CreateSpotifyPlaylistException(String userId, Throwable cause) {
    super(
        "Error while creating Spotify playlist for user with id ["
            + userId
            + "]: "
            + cause.getMessage(),
        cause);
  }
}
