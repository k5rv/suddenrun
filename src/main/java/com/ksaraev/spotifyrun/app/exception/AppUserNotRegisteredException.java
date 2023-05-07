package com.ksaraev.spotifyrun.app.exception;

import lombok.experimental.StandardException;

@StandardException
public class AppUserNotRegisteredException extends RuntimeException {

  public AppUserNotRegisteredException(String appUserId) {
    super("User with id" + " [" + appUserId + "] is not registered");
  }
}
