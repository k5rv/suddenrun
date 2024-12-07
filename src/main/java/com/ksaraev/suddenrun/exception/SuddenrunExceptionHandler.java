package com.ksaraev.suddenrun.exception;

import static org.springframework.http.HttpStatus.*;

import com.ksaraev.suddenrun.playlist.SuddenrunPlaylistAlreadyExistsException;
import com.ksaraev.suddenrun.playlist.SuddenrunPlaylistDoesNotExistException;
import com.ksaraev.suddenrun.playlist.SuddenrunUserDoesNotHaveAnyPlaylistsException;
import com.ksaraev.suddenrun.user.SuddenrunUserDoesNotMatchCurrentSpotifyUserException;
import com.ksaraev.suddenrun.user.SuddenrunUserIsAlreadyRegisteredException;
import com.ksaraev.suddenrun.user.SuddenrunUserIsNotRegisteredException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@ControllerAdvice
public class SuddenrunExceptionHandler {

  @ExceptionHandler(value = {SuddenrunAuthenticationException.class})
  public ResponseEntity<SuddenrunError> handleSuddenrunAuthenticationException(
      SuddenrunAuthenticationException e) {
    log.error(e.getMessage());
    int unauthorized = UNAUTHORIZED.value();
    String message = "Suddenrun authentication error";
    return ResponseEntity.status(UNAUTHORIZED).body(new SuddenrunError(unauthorized, message));
  }

  @ExceptionHandler(value = {SuddenrunUserIsNotRegisteredException.class})
  public ResponseEntity<SuddenrunError> handleSuddenrunUserIsNotRegisteredException(
      SuddenrunUserIsNotRegisteredException e) {
    String message = e.getMessage();
    log.error(message);
    int notFound = NOT_FOUND.value();
    return ResponseEntity.status(NOT_FOUND).body(new SuddenrunError(notFound, message));
  }

  @ExceptionHandler(value = {SuddenrunUserIsAlreadyRegisteredException.class})
  public ResponseEntity<SuddenrunError> handleSuddenrunUserIsAlreadyRegisteredException(
      SuddenrunUserIsAlreadyRegisteredException e) {
    String message = e.getMessage();
    log.error(message);
    int conflict = CONFLICT.value();
    return ResponseEntity.status(CONFLICT).body(new SuddenrunError(conflict, message));
  }

  @ExceptionHandler(value = {SuddenrunUserDoesNotMatchCurrentSpotifyUserException.class})
  public ResponseEntity<SuddenrunError> handleSuddenrunUserDoesNotMatchCurrentSpotifyUserException(
      SuddenrunUserDoesNotMatchCurrentSpotifyUserException e) {
    String message = e.getMessage();
    log.error(message);
    int conflict = CONFLICT.value();
    return ResponseEntity.status(CONFLICT).body(new SuddenrunError(conflict, message));
  }

  @ExceptionHandler(value = {SuddenrunUserDoesNotHaveAnyPlaylistsException.class})
  public ResponseEntity<SuddenrunError> handleSuddenrunUserDoesNotHaveAnyPlaylistsException(
      SuddenrunUserDoesNotHaveAnyPlaylistsException e) {
    String message = e.getMessage();
    log.error(message);
    int notFound = NOT_FOUND.value();
    return ResponseEntity.status(NOT_FOUND).body(new SuddenrunError(notFound, message));
  }

  @ExceptionHandler(value = {SuddenrunPlaylistDoesNotExistException.class})
  public ResponseEntity<SuddenrunError> handleSuddenrunPlaylistDoesNotExistException(
      SuddenrunPlaylistDoesNotExistException e) {
    String message = e.getMessage();
    log.error(message);
    int notFound = NOT_FOUND.value();
    return ResponseEntity.status(NOT_FOUND).body(new SuddenrunError(notFound, message));
  }

  @ExceptionHandler(value = {SuddenrunPlaylistAlreadyExistsException.class})
  public ResponseEntity<SuddenrunError> handleAppPlaylistAlreadyExistException(
      SuddenrunPlaylistAlreadyExistsException e) {
    String message = e.getMessage();
    log.error(message);
    int conflict = CONFLICT.value();
    return ResponseEntity.status(CONFLICT).body(new SuddenrunError(conflict, message));
  }

  @ExceptionHandler(value = {RuntimeException.class})
  public ResponseEntity<SuddenrunError> handleSuddenrunRuntimeException(RuntimeException e) {
    log.error(e.getMessage(), e);
    String message = "Suddenrun internal error: please contact support";
    int internalServerError = INTERNAL_SERVER_ERROR.value();
    return ResponseEntity.status(INTERNAL_SERVER_ERROR)
        .body(new SuddenrunError(internalServerError, message));
  }
}
