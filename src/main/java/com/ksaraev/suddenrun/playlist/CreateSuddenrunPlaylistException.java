package com.ksaraev.suddenrun.playlist;


public class CreateSuddenrunPlaylistException extends RuntimeException {

  public CreateSuddenrunPlaylistException(String userId, Throwable cause) {
    super(
        "Error while creating Suddenrun playlist for user with id [" + userId + "]: " + cause.getMessage(),
        cause);
  }
}
