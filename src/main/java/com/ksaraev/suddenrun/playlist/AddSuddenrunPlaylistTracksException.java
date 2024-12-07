package com.ksaraev.suddenrun.playlist;


public class AddSuddenrunPlaylistTracksException extends RuntimeException {

  public AddSuddenrunPlaylistTracksException(String playlistId, Throwable cause) {
    super(
        "Error while adding tracks to playlist with id [" + playlistId + "]: " + cause.getMessage(),
        cause);
  }
}
