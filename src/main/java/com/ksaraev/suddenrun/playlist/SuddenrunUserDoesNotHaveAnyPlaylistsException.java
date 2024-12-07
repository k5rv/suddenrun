package com.ksaraev.suddenrun.playlist;


public class SuddenrunUserDoesNotHaveAnyPlaylistsException extends RuntimeException {

  public SuddenrunUserDoesNotHaveAnyPlaylistsException(String userId) {
    super("Suddenrun user with id [" + userId + "] doesn't have any playlists");
  }
}
