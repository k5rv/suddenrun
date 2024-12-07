package com.ksaraev.spotify.exception;

public class GetSpotifyPlaylistException extends SpotifyServiceException {

  public GetSpotifyPlaylistException(String spotifyPlaylistId, Throwable cause) {
    super(
        "Error while getting Spotify playlist with id ["
            + spotifyPlaylistId
            + "]: "
            + cause.getMessage(),
        cause);
  }
}
