package com.ksaraev.suddenrun.user;

public class SuddenrunUserIsAlreadyRegisteredException extends RuntimeException {

  public SuddenrunUserIsAlreadyRegisteredException(String userId) {
    super("Suddenrun user with id [" + userId + "] is already registered");
  }
}
