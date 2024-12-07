package com.ksaraev.spotify.exception;

public class GetSpotifyUserPlaylistsException extends SpotifyServiceException {

  public GetSpotifyUserPlaylistsException(String spotifyUserId, Throwable cause) {
    super(
        "Error while getting Spotify user playlists for user with id ["
            + spotifyUserId
            + "]: "
            + cause.getMessage(),
        cause);
  }
}
