package com.ksaraev.suddenrun.user;

public class SuddenrunUserIsNotRegisteredException extends RuntimeException {

  public SuddenrunUserIsNotRegisteredException(String userId) {
    super("Suddenrun user with id [" + userId + "] is not registered");
  }
}
