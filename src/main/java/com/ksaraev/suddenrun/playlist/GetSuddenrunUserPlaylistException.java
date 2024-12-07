package com.ksaraev.suddenrun.playlist;

public class GetSuddenrunUserPlaylistException extends RuntimeException {

  public GetSuddenrunUserPlaylistException(String appUserId, Throwable cause) {
    super(
        "Error while getting Suddenrun playlist for user with id ["
            + appUserId
            + "]: "
            + cause.getMessage(),
        cause);
  }
}
