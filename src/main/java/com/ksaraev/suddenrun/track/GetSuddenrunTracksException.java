package com.ksaraev.suddenrun.track;


public class GetSuddenrunTracksException extends RuntimeException {

  public GetSuddenrunTracksException(Throwable cause) {
    super("Error while getting tracks: " + cause.getMessage(), cause);
  }
}
