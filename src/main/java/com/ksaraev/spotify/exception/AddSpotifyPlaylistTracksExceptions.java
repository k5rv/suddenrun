package com.ksaraev.spotify.exception;

public class AddSpotifyPlaylistTracksExceptions extends SpotifyServiceException {

  public AddSpotifyPlaylistTracksExceptions(String playlistId, Throwable cause) {
    super(
        "Error while adding Spotify playlist tracks to playlist with id ["
            + playlistId
            + "]: "
            + cause.getMessage(),
        cause);
  }
}
