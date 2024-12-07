package com.ksaraev.suddenrun.user;

import java.util.Optional;

public interface AppUserService {

  Optional<AppUser> getUser(String userId);

  AppUser registerUser(String userId, String userName);
}
