package com.ksaraev.suddenrun.playlist;

public class SuddenrunPlaylistAlreadyExistsException extends RuntimeException {

  public SuddenrunPlaylistAlreadyExistsException(String playlistId) {
    super("Suddenrun playlist with id [" + playlistId + "] already exists");
  }
}
