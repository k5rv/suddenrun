package com.ksaraev.suddenrun.user;


public class GetSuddenrunUserException extends RuntimeException {

  public GetSuddenrunUserException(String userId, Throwable cause) {
    super("Error while getting Suddenrun user with id [" + userId + "]: " + cause.getMessage(), cause);
  }
}
