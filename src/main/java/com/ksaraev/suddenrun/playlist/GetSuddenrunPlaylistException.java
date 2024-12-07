package com.ksaraev.suddenrun.playlist;


public class GetSuddenrunPlaylistException extends RuntimeException {

  public GetSuddenrunPlaylistException(String playlistId, Throwable cause) {
    super(
        "Error while getting Suddenrun playlist with id [" + playlistId + "]: " + cause.getMessage(), cause);
  }
}
