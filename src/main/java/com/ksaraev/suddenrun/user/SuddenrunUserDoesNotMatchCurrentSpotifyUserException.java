package com.ksaraev.suddenrun.user;


public class SuddenrunUserDoesNotMatchCurrentSpotifyUserException extends RuntimeException {

  public SuddenrunUserDoesNotMatchCurrentSpotifyUserException(String userId) {
    super("Suddenrun user with id [" + userId + "] does not match current Spotify user");
  }
}
