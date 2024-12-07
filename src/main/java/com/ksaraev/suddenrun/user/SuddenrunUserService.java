package com.ksaraev.suddenrun.user;

import java.util.Optional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class SuddenrunUserService implements AppUserService {

  private final SuddenrunUserRepository repository;

  @Override
  public Optional<AppUser> getUser(String userId) {
    try {
      log.info("Getting Suddenrun user with id [" + userId + "]");
      Optional<SuddenrunUser> optionalUser = repository.findById(userId);
      if (optionalUser.isEmpty()) {
        log.info("Suddenrun user with id [" + userId + "] not found. Returning empty result");
        return Optional.empty();
      }
      log.info("Found Suddenrun user with id [" + userId + "]. Returning user");
      return optionalUser.map(AppUser.class::cast);
    } catch (RuntimeException e) {
      throw new GetSuddenrunUserException(userId, e);
    }
  }

  @Override
  public AppUser registerUser(String userId, String userName) {
    Optional<AppUser> optionalUser = getUser(userId);
    if (optionalUser.isPresent()) throw new SuddenrunUserIsAlreadyRegisteredException(userId);
    try {
      log.info("Registering Suddenrun user with id [" + userId + "]");
      SuddenrunUser suddenrunUser = SuddenrunUser.builder().id(userId).name(userName).build();
      repository.save(suddenrunUser);
      log.info("Registered Suddenrun user with id [" + userId + "]");
      return suddenrunUser;
    } catch (RuntimeException e) {
      throw new RegisterSuddenrunUserException(userId, e);
    }
  }
}
