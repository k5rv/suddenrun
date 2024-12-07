package com.ksaraev.suddenrun.user;


public class RegisterSuddenrunUserException extends RuntimeException {

  public RegisterSuddenrunUserException(String userId, Throwable cause) {
    super("Error while registering Suddenrun user with id [" + userId + "]: " + cause.getMessage(), cause);
  }
}
