package com.ksaraev.spotify.exception;

public class RemoveSpotifyPlaylistTracksException extends SpotifyServiceException {

  public RemoveSpotifyPlaylistTracksException(String spotifyPlaylistId, Throwable cause) {
    super(
        "Error while removing Spotify playlist tracks from playlist with id ["
            + spotifyPlaylistId
            + "]: "
            + cause.getMessage(),
        cause);
  }
}
