package com.ksaraev.suddenrun.playlist;


public class SuddenrunPlaylistDoesNotExistException extends RuntimeException {

  public SuddenrunPlaylistDoesNotExistException(String playlistId) {
    super("Suddenrun playlist with id [" + playlistId + "] doesn't exist");
  }
}
